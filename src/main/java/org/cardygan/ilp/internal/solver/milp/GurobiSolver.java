package org.cardygan.ilp.internal.solver.milp;

import gurobi.*;
import org.cardygan.ilp.api.Result;
import org.cardygan.ilp.api.model.Constraint;
import org.cardygan.ilp.api.model.Var;
import org.cardygan.ilp.internal.util.LibraryUtil;
import org.cardygan.ilp.internal.util.ModelException;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static org.cardygan.ilp.internal.util.Util.assertNotNull;

public class GurobiSolver extends MILPSolver {

    private GRBModel model;
    private GRBEnv env;
    private boolean disposed = false;
    private Map<String, GRBVar> vars = new HashMap<>();
    private boolean with_cache = true;

    private Set<String> cstrs = new HashSet<>();

    /**
     * Set for tracking added SOS constraints.
     */
    private Map<String, GRBSOS> addedSos = new HashMap<>();

    public GurobiSolver() {
        init(true, false, -1, TimeUnit.SECONDS, -1, null);
    }

    private GurobiSolver(MILPConstrGenerator gen, boolean with_cache, boolean logging, boolean presolve,
                         long timeout, TimeUnit timeoutUnit, int seed, String libPath) {
        super(gen);

        this.with_cache = with_cache;

        init(presolve, logging, timeout, timeoutUnit, seed, libPath);
    }

    private void init(boolean presolve, boolean logging, long timeout, TimeUnit timeoutUnit, int seed, String libPath) {


        if (libPath != null)
            LibraryUtil.loadLibraryFromPath(libPath);

        try {
            env = new GRBEnv();
            model = new GRBModel(env);

            setLogging(logging);

            if (timeout >= 0)
                env.set(GRB.DoubleParam.TimeLimit, timeoutUnit.toSeconds(timeout));

            if (seed != -1)
                env.set(GRB.IntParam.Seed, seed);

            setPresolve(presolve);


        } catch (GRBException e) {
            e.printStackTrace();

            throw new IllegalStateException("Could not initialize gurobi backend.");
        }
    }

    public void setPresolve(boolean presolve) {
        checkIsDisposed();
        try {
            if (presolve)
                model.set(GRB.IntParam.Presolve, 1);
            else
                model.set(GRB.IntParam.Presolve, 0);
        } catch (GRBException e) {
            e.printStackTrace();
        }
    }


    private void setLogging(boolean isLogging) throws GRBException {
        if (!isLogging) {
            model.set(GRB.IntParam.LogToConsole, 0);
            model.set(GRB.StringParam.LogFile, "");
        } else {
            model.set(GRB.IntParam.LogToConsole, 1);
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

                try {
                    GRBSOS sosCstr = model.addSOS(grbVars, params, GRB.SOS_TYPE1);

                    // add sos constraint so that we can remove it later by id
                    addedSos.put(name, sosCstr);
                } catch (NullPointerException e) {
                    throw new ModelException("Could not add sos constraint. Make sure that all variables exist in model.");
                }

                if (!with_cache)
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

                lhs.addTerms(params, Arrays.stream(vars).map(e -> retrieveVar(e.getName())).toArray(GRBVar[]::new));

                try {
                    model.addConstr(lhs, sense, rhs, name);
                } catch (NullPointerException e) {
                    throw new ModelException("Null Pointer in Gurobi: Make sure that variables in constraint exist in model.");
                }
                cstrs.add(name);

                if (!with_cache)
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
        } catch (NullPointerException e) {
            throw new ModelException("Could not add objective to Gurobi model. Make sure that all contained variables exist in model.");
        }

    }

    @Override
    protected void removeObj() {
        try {
            model.setObjective(new GRBLinExpr(), GRB.MAXIMIZE);
        } catch (GRBException e) {
            e.printStackTrace();
            throw new IllegalStateException("Could not remove objective.");
        }
    }

    private GRBVar retrieveVar(final String varName) {
        checkIsDisposed();

        if (with_cache) {
            return vars.get(varName);
        } else {

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
    }

    @Override
    public void removeConstr(Constraint constraint) {
        checkIsDisposed();

        if (!hasConstraint(constraint.getName()))
            throw new ModelException("Model does not contain constraint with name " + constraint.getName());


        if (addedSos.containsKey(constraint.getName())) {
            try {
                model.update();
                model.remove(addedSos.get(constraint.getName()));
                addedSos.remove(constraint.getName());
            } catch (GRBException e) {
                e.printStackTrace();
                throw new IllegalStateException("Undesired state");
            }
        } else {

            try {
                model.update();
                GRBConstr cstr = model.getConstrByName(constraint.getName());
                model.remove(cstr);
                model.update();
            } catch (GRBException e) {
                e.printStackTrace();
                throw new ModelException("Could not find constraint with name " + constraint.getName());
            }
        }

        cstrs.remove(constraint.getName());
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
            cstrs.clear();
            vars.clear();
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
            final GRBVar var;
            switch (type) {
                case INT:
                    var = model.addVar(lbNew, ubNew, 0.0, GRB.INTEGER, name);
                    break;
                case BIN:
                    var = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, name);
                    break;
                case DBL:
                    var = model.addVar(lbNew, ubNew, 0.0, GRB.CONTINUOUS, name);
                    break;
                default:
                    throw new IllegalStateException("Unknown variable type.");
            }
            vars.put(name, var);

            if (!with_cache)
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
        checkIsDisposed();

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

        if (with_cache) {
            return vars.containsKey(varName);
        } else {
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
    }

    @Override
    public boolean hasConstraint(String cstrName) {
        checkIsDisposed();

        // check first if constraint was added
        if (addedSos.containsKey(cstrName))
            return true;

        if (with_cache)
            return cstrs.contains(cstrName);
        else
            return Arrays.stream(model.getConstrs()).anyMatch(it -> {
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
        private long timeout = -1;
        private TimeUnit timeoutUnit = TimeUnit.SECONDS;
        private String libPath = null;
        private int seed = -1;
        private boolean withCache = true;

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

        public GurobiSolverBuilder withCache(boolean withCache) {
            this.withCache = withCache;
            return this;
        }

        @Override
        public GurobiSolver build() {
            if (gen == null)
                gen = new SosBasedCstrGenerator();

            return new GurobiSolver(gen, withCache, logging, presolve, timeout, timeoutUnit, seed, libPath);
        }
    }

}
