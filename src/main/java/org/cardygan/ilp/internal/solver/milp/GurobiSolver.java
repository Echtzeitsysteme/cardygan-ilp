package org.cardygan.ilp.internal.solver.milp;

import gurobi.*;
import org.cardygan.ilp.api.Result;
import org.cardygan.ilp.api.model.Constraint;
import org.cardygan.ilp.api.model.Var;
import org.cardygan.ilp.internal.util.LibraryUtil;
import org.cardygan.ilp.internal.util.ModelException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.cardygan.ilp.internal.util.Util.assertNotNull;

public class GurobiSolver extends MILPSolver {

    private GRBModel model;
    private GRBEnv env;
    private boolean disposed = false;

    /**
     * Set for tracking added SOS constraints.
     */
    private Map<String, GRBSOS> addedSos = new HashMap<>();

    public GurobiSolver() {
        init(true, false, 0, TimeUnit.SECONDS, -1, null);
    }

    private GurobiSolver(MILPConstrGenerator gen, boolean logging, boolean presolve,
                         long timeout, TimeUnit timeoutUnit, int seed, String libPath) {
        super(gen);
        init(presolve, logging, timeout, timeoutUnit, seed, libPath);
    }

    private void init(boolean presolve, boolean logging, long timeout, TimeUnit timeoutUnit, int seed, String libPath) {
        if (libPath != null)
            LibraryUtil.loadLibraryFromPath(libPath);

        try {
            env = new GRBEnv();
            model = new GRBModel(env);
            setLogging(logging, env);

            if (timeout != 0)
                env.set(GRB.DoubleParam.TimeLimit, timeoutUnit.toSeconds(timeout));

            if (seed != -1)
                env.set(GRB.IntParam.Seed, seed);

            if (!presolve)
                model.set(GRB.IntParam.Presolve, 0);

        } catch (GRBException e) {
            e.printStackTrace();

            throw new IllegalStateException("Could not initialize gurobi backend.");
        }
    }

    private void setLogging(boolean isLogging, GRBEnv env) throws GRBException {
        if (!isLogging) {
            env.set(GRB.IntParam.LogToConsole, 0);
            env.set(GRB.StringParam.LogFile, "");
        } else {
            env.set(GRB.IntParam.LogToConsole, 1);
        }
    }

    @Override
    public boolean isDisposed() {
        return disposed;
    }

    private void checkIsDisposed() {
        if (disposed) {
            throw new ModelException("Could not process requested operation. The Model was already disposed.");
        }
    }

    @Override
    public void addCstr(String name, LinearConstr cstr) {
        checkIsDisposed();

        assertNotNull(name, cstr);

        if (hasConstraint(name))
            throw new ModelException("Constraint with name " + name + " does already exist in model. Choose another name.");

        final LinearConstr.Type type = cstr.getType();
        final double[] params = cstr.getParams();
        final Var[] vars = cstr.getVars();

        try {

            if (type == LinearConstr.Type.SOS) {
                GRBVar[] grbVars = Arrays.stream(vars)
                        .map(it -> retrieveVar(it.getName())).toArray(GRBVar[]::new);
                GRBSOS sosCstr = model.addSOS(grbVars, params, GRB.SOS_TYPE1);

                // add sos constraint so that we can remove it later by id
                addedSos.put(name, sosCstr);

                model.update();
            } else {
                final GRBLinExpr lhs = new GRBLinExpr();

                final char sense;
                switch (type) {
                    case LEQ:
                        sense = GRB.LESS_EQUAL;
                        break;
                    case GEQ:
                        sense = GRB.GREATER_EQUAL;
                        break;
                    case EQ:
                        sense = GRB.EQUAL;
                        break;
                    default:
                        throw new IllegalStateException("Unknown relation type.");
                }

                final double rhs = cstr.getRhs();

                for (int i = 0; i < params.length; i++) {
                    GRBVar var = retrieveVar(vars[i].getName());

                    lhs.addTerm(params[i], var);
                }

                model.addConstr(lhs, sense, rhs, name);
                model.update();
            }
        } catch (GRBException e) {
            e.printStackTrace();
            // TODO create specific exception type
            throw new IllegalStateException("Could not add constraint to Gurobi model.");
        }

    }

    @Override
    public void setObj(LinearObj obj) {
        checkIsDisposed();

        assertNotNull(obj);

        final GRBLinExpr expr = new GRBLinExpr();
        final double[] params = obj.getParams();
        final Var[] vars = obj.getVars();
        final double constant = obj.getConstant();

        for (int i = 0; i < params.length; i++) {
            GRBVar var = retrieveVar(vars[i].getName());

            expr.addTerm(params[i], var);
        }

        expr.addConstant(constant);

        try {
            model.setObjective(expr, obj.isMax() ? GRB.MAXIMIZE : GRB.MINIMIZE);
        } catch (GRBException e) {
            e.printStackTrace();
            // TODO create specific exception type
            throw new IllegalStateException("Could not add objective to Gurobi model.");
        }
    }

    private GRBVar retrieveVar(final String varName) {
        checkIsDisposed();

        try {
            final GRBVar var = model.getVarByName(varName);

            if (var == null)
                throw new ModelException("Could not find variable " + varName + " in model.");

            return var;
        } catch (GRBException e) {
            if (e.getErrorCode() == GRB.ERROR_DATA_NOT_AVAILABLE) {
                return null;
            }

            e.printStackTrace();
            // TODO create specific exception type
            throw new IllegalStateException("Could not retrieve variable with name " + varName);
        }
    }

    @Override
    public void removeConstr(Constraint constraint) {
        checkIsDisposed();

        if (!hasConstraint(constraint.getName()))
            throw new ModelException("Model does not contain constraint with name " + constraint.getName());

        if (addedSos.containsKey(constraint.getName())) {
            try {
                model.remove(addedSos.get(constraint.getName()));
                addedSos.remove(constraint.getName());
            } catch (GRBException e) {
                e.printStackTrace();
                throw new IllegalStateException("Undesired state");
            }
        } else {

            try {
                GRBConstr cstr = model.getConstrByName(constraint.getName());
                model.remove(cstr);
                model.update();
            } catch (GRBException e) {
                e.printStackTrace();
                throw new ModelException("Could not find constraint with name " + constraint.getName());
            }
        }
    }

    @Override
    public Result optimize() {
        checkIsDisposed();

        try {
            model.update();

            final long start = System.nanoTime();
            model.optimize();
            final long end = System.nanoTime();

            Result.SolverStatus status;
            if (model.get(GRB.IntAttr.Status) == GRB.UNBOUNDED) {
                status = Result.SolverStatus.UNBOUNDED;
            } else if (model.get(GRB.IntAttr.Status) == GRB.INF_OR_UNBD) {
                status = Result.SolverStatus.INF_OR_UNBD;
            } else if (model.get(GRB.IntAttr.Status) == GRB.INFEASIBLE) {
                status = Result.SolverStatus.INFEASIBLE;
            } else if (model.get(GRB.IntAttr.Status) == GRB.OPTIMAL) {
                status = Result.SolverStatus.OPTIMAL;
            } else if (model.get(GRB.IntAttr.Status) == GRB.TIME_LIMIT) {
                System.err.println("Warning: time limit reached! " + model.get(GRB.IntAttr.SolCount)
                        + " solutions were found so far.");
                status = Result.SolverStatus.TIME_OUT;
            } else throw new RuntimeException("Unknown internal status.");

            return new Result(status, end - start);
        } catch (GRBException e) {
            e.printStackTrace();
            throw new IllegalStateException("SolverOld error.");
        }
    }

    @Override
    public void dispose() {
        checkIsDisposed();

        try {
            addedSos.clear();
            env.dispose();
            model.dispose();
        } catch (GRBException e) {
            e.printStackTrace();
            // TODO create specific exception type
            throw new IllegalStateException("Could not dispose model or environment.");
        }
        disposed = true;
    }

    @Override
    public double getVal(Var var) {
        checkIsDisposed();

        final GRBVar grbVar = retrieveVar(var.getName());

        try {
            return grbVar.get(GRB.DoubleAttr.X);
        } catch (GRBException e) {
            e.printStackTrace();
            // TODO create specific exception type
            throw new IllegalStateException("Could not add retrieve value for variable.");
        }
    }

    @Override
    public double getObjVal() {
        checkIsDisposed();

        try {
            return model.get(GRB.DoubleAttr.ObjVal);
        } catch (GRBException e) {
            e.printStackTrace();
            // TODO create specific exception type
            throw new IllegalStateException("Could not add retrieve objective value.");
        }
    }

    @Override
    public GRBModel getUnderlyingModel() {
        checkIsDisposed();

        return model;
    }

    @Override
    public void addVar(final String name, final double lb, final double ub, final VarType type) {
        checkIsDisposed();

        assertNotNull(name, type);

        if (lb >= 0 && ub >= 0 && lb > ub)
            throw new ModelException("Specified lower bound needs to be larger than upper bound.");


        if (hasVar(name))
            throw new ModelException("Variable with name " + name + " does already exist in model.");

        final double lbNew = (lb < 0) ? -GRB.INFINITY : lb;
        final double ubNew = (ub < 0) ? GRB.INFINITY : ub;
        try {
            switch (type) {
                case INT:
                    model.addVar(lbNew, ubNew, 0.0, GRB.INTEGER, name);
                    break;
                case BIN:
                    model.addVar(0.0, 1.0, 0.0, GRB.BINARY, name);
                    break;
                case DBL:
                    model.addVar(lbNew, ubNew, 0.0, GRB.CONTINUOUS, name);
                    break;
            }
            model.update();
        } catch (GRBException e) {
            e.printStackTrace();
            // TODO create specific exception type
            throw new IllegalStateException("Could not add variable name to Gurobi model.");
        }

    }

    @Override
    public int getNumVars() {
        checkIsDisposed();

        try {
            return model.get(GRB.IntAttr.NumVars);
        } catch (GRBException e) {
            e.printStackTrace();
            throw new IllegalStateException("This should not occur.");
        }
    }

    @Override
    public int getNumConstrs() {
        try {
            return model.get(GRB.IntAttr.NumConstrs);
        } catch (GRBException e) {
            e.printStackTrace();
            throw new IllegalStateException("This should not occur.");
        }
    }


    @Override
    public boolean hasVar(String varName) {
        checkIsDisposed();

        try {
            return model.getVarByName(varName) != null;
        } catch (GRBException e) {
            if (e.getErrorCode() == GRB.ERROR_DATA_NOT_AVAILABLE)
                return false;

            e.printStackTrace();
            // TODO create specific exception type
            throw new IllegalStateException("Could not access Gurobi model.");
        }
    }

    @Override
    public boolean hasConstraint(String cstrName) {
        checkIsDisposed();

        // check first if constraint was added
        if (addedSos.containsKey(cstrName))
            return true;

        return Arrays.stream(model.getConstrs()).parallel().anyMatch(it -> {
            try {
                return it.get(GRB.StringAttr.ConstrName).equals(cstrName);
            } catch (GRBException e) {
                e.printStackTrace();
                throw new IllegalStateException("Undesired state.");
            }
        });
    }

    public static class GurobiSolverBuilder implements MILPSolverBuilder {

        private boolean logging;
        private boolean presolve = true;
        private MILPConstrGenerator gen;
        private long timeout = 0;
        private TimeUnit timeoutUnit = TimeUnit.SECONDS;
        private String libPath = null;
        private int seed = -1;

        public GurobiSolverBuilder withLogging(boolean logging) {
            this.logging = logging;
            return this;
        }

        public GurobiSolverBuilder withLibPath(String path) {
            this.libPath = path;
            return this;
        }

        public GurobiSolverBuilder withTimeout(long timeout, TimeUnit timeoutUnit) {
            this.timeout = timeout;
            this.timeoutUnit = timeoutUnit;
            return this;
        }

        public GurobiSolverBuilder withPresolve(boolean presolve) {
            this.presolve = presolve;
            return this;
        }

        public GurobiSolverBuilder withMILPConstrGenerator(MILPConstrGenerator gen) {
            this.gen = gen;
            return this;
        }

        public GurobiSolverBuilder withSeed(int seed) {
            this.seed = seed;
            return this;
        }

        @Override
        public GurobiSolver build() {
            return new GurobiSolver(gen, logging, presolve, timeout, timeoutUnit, seed, libPath);
        }
    }

}
