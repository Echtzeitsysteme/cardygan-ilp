package org.cardygan.ilp.api.solver;

import gurobi.*;
import org.cardygan.ilp.api.Result;
import org.cardygan.ilp.api.model.Model;
import org.cardygan.ilp.api.model.Var;
import org.cardygan.ilp.internal.expr.Coefficient;
import org.cardygan.ilp.internal.expr.model.*;
import org.cardygan.ilp.internal.util.IlpUtil;
import org.cardygan.ilp.internal.util.LibraryUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class GurobiSolver implements Solver {

    private final String libraryPath;
    private final String modelOutputFilePath;
    private final Integer seed;
    private final boolean logging;
    private Map<Var, GRBVar> vars;
    private final Long timeout;
    private final boolean withPresolve;
    private final Double mipGap;
    private final Integer feasibleSolLimit;

    public GurobiSolver() {
        this(new GurobiSolverBuilder());
    }

    private GurobiSolver(GurobiSolverBuilder builder) {
        this.seed = builder.seed;
        this.libraryPath = builder.libraryPath;
        this.withPresolve = builder.withPresolve;
        this.mipGap = builder.mipGap;

        if (libraryPath != null)
            LibraryUtil.loadLibraryFromPath(libraryPath);

        this.logging = builder.logging;
        this.timeout = builder.timeout;
        this.modelOutputFilePath = builder.modelOutputFilePath;
        this.feasibleSolLimit = builder.feasibleSolLimit;
    }

    public static GurobiSolverBuilder create() {
        return new GurobiSolverBuilder();
    }

    @Override
    public Result solve(Model model) {
        BasicModel basicModel = IlpToBasicConverter.convert(model);

        vars = new HashMap<>();

        final GRBEnv env;
        try {

            env = new GRBEnv();

            if (!logging) {
                env.set(GRB.IntParam.LogToConsole, 0);
                env.set(GRB.StringParam.LogFile, "");
            } else {
                env.set(GRB.IntParam.LogToConsole, 1);
            }

            if (seed != null)
                env.set(GRB.IntParam.Seed, seed);

            if (timeout != null) {
                env.set(GRB.DoubleParam.TimeLimit, timeout.doubleValue());
            }

            final GRBModel grbModel = new GRBModel(env);

            if (mipGap != null)
                grbModel.set(GRB.DoubleParam.MIPGap, mipGap);

            // Create vars
            for (Var var : basicModel.getVars().values()) {

                if (IlpUtil.isBinaryVar(var)) {
                    GRBVar grbVar = grbModel.addVar(0.0, 1.0, 0.0, GRB.BINARY, var.getName());
                    vars.put(var, grbVar);
                } else if (IlpUtil.isIntVar(var)) {
                    double lb = -GRB.INFINITY;
                    double ub = GRB.INFINITY;

                    if (basicModel.getBounds(var) != null) {
                        Model.Bounds bounds = basicModel.getBounds(var);
                        lb = new Double(bounds.getLb()).intValue();

                        int newUb = new Double(bounds.getUb()).intValue();
                        if (newUb >= 0)
                            ub = newUb;
                    }

                    if (lb > ub)
                        throw new IllegalArgumentException("Lower bound of variable " + var + "needs to be less than upper bound.");

                    GRBVar grbVar = grbModel.addVar(lb, ub, 0.0, GRB.INTEGER, var.getName());
                    vars.put(var, grbVar);
                } else if (IlpUtil.isDoubleVar(var)) {
                    double lb = -GRB.INFINITY;
                    double ub = GRB.INFINITY;

                    if (basicModel.getBounds(var) != null) {
                        Model.Bounds bounds = basicModel.getBounds(var);
                        lb = new Double(bounds.getLb()).intValue();

                        double newUb = bounds.getUb();
                        if (newUb >= 0)
                            ub = newUb;
                    }

                    if (lb > ub)
                        throw new IllegalArgumentException("Lower bound of variable " + var + "needs to be less than upper bound.");

                    GRBVar grbVar = grbModel.addVar(lb, ub, 0.0, GRB.CONTINUOUS, var.getName());
                    vars.put(var, grbVar);

                } else {
                    throw new IllegalStateException("Not supported variable type.");
                }
            }

            // Create constraints
            constructConstraints(grbModel, basicModel.getConstraints());

            // create sos1 constraints
            constructSos1Constraints(grbModel, basicModel.getSos1());

            final GRBLinExpr obj;
            if (basicModel.getObjective().isPresent()) {
                obj = createObjective(grbModel, basicModel.getObjective().get());
                if (basicModel.getObjective().get().isMax()) {
                    grbModel.setObjective(obj, GRB.MAXIMIZE);
                } else {
                    grbModel.setObjective(obj, GRB.MINIMIZE);
                }
            } else {
                obj = null;
            }
            grbModel.update();

            class PresolveCallback extends GRBCallback {
                private int rowsRemovedByPresolve = 0;
                private int colsRemovedByPresolve = 0;

                @Override
                protected void callback() {
                    try {
                        if (where == GRB.CB_PRESOLVE) {

                            colsRemovedByPresolve = getIntInfo(GRB.CB_PRE_COLDEL);
                            rowsRemovedByPresolve = getIntInfo(GRB.CB_PRE_ROWDEL);
                        }
                    } catch (GRBException e) {
                        e.printStackTrace();
                    }
                }
            }
            PresolveCallback presolveCallback = new PresolveCallback();
            grbModel.setCallback(presolveCallback);

            if (feasibleSolLimit != null)
                grbModel.set(GRB.IntParam.SolutionLimit, feasibleSolLimit);

//            grbModel.tune();


            final long start = System.currentTimeMillis();
            grbModel.optimize();
            final long end = System.currentTimeMillis();


            final boolean succ = grbModel.get(GRB.IntAttr.Status) == GRB.OPTIMAL;

            Result.SolverStatus status;
            if (grbModel.get(GRB.IntAttr.Status) == GRB.UNBOUNDED) {
                status = Result.SolverStatus.UNBOUNDED;
            } else if (grbModel.get(GRB.IntAttr.Status) == GRB.INF_OR_UNBD) {
                status = Result.SolverStatus.INF_OR_UNBD;
            } else if (grbModel.get(GRB.IntAttr.Status) == GRB.INFEASIBLE) {
                status = Result.SolverStatus.INFEASIBLE;
            } else if (grbModel.get(GRB.IntAttr.Status) == GRB.OPTIMAL) {
                status = Result.SolverStatus.OPTIMAL;
            } else if (grbModel.get(GRB.IntAttr.Status) == GRB.TIME_LIMIT) {
                System.err.println("Warning: time limit reached! " + grbModel.get(GRB.IntAttr.SolCount)
                        + " solutions were found so far.");
                status = Result.SolverStatus.TIME_OUT;
            } else throw new RuntimeException("Unknown solver status.");

            if (modelOutputFilePath != null) {
                grbModel.write(modelOutputFilePath);
            }

            Map<Var, Double> solutions = new HashMap<>();
            if (succ) {
                for (Entry<Var, GRBVar> entry : vars.entrySet()) {
                    try {
                        solutions.put(entry.getKey(), entry.getValue().get(GRB.DoubleAttr.X));
                    } catch (GRBException e) {
                        if (logging)
                            System.err.println("Warning: Variable " + entry.getKey().getName()
                                    + " is not in the active model! Could not retrieve objective value.");
                        solutions.put(entry.getKey(), 0d);
                    }
                }
            }


            Double objVal;
            if (succ && obj != null) {
                try {
                    objVal = obj.getValue();
                } catch (GRBException e) {
                    objVal = 0d;
                }
            } else {
                objVal = null;
            }


            final Result.Statistics stats = new Result.Statistics(status, end - start, presolveCallback.colsRemovedByPresolve, presolveCallback.rowsRemovedByPresolve);
            final Result res = new Result(model, stats, solutions, objVal);

            grbModel.dispose();
            env.dispose();


            return res;

        } catch (GRBException e) {
            e.printStackTrace();
        }
        throw new RuntimeException("Error in solver Run ");
    }

    private GRBLinExpr createObjective(GRBModel model, BasicObjective objective) {
        final GRBLinExpr expr = createExpr(objective.getCoefficients());
        expr.addConstant(objective.getConstant());

        return expr;
    }

    private void constructSos1Constraints(GRBModel model, List<Set<Var>> sets) throws GRBException {
        for (Set<Var> sos : sets) {
            double[] weights = new double[sos.size()];
            for (int i = 0; i < sos.size(); i++) {
                weights[i] = i + 1;
            }
            GRBVar[] sosVars = sos.stream().map(v -> {
                if (vars.containsKey(v)) {
                    return vars.get(v);
                } else {
                    throw new IllegalStateException("Could not find variable corresponding to " + v.getName());
                }
            }).toArray(GRBVar[]::new);
            model.addSOS(sosVars, weights, 1);
        }
    }

    private void constructConstraints(GRBModel model, List<NormalizedArithExpr> ilpNormalizedArithExprs) throws GRBException {
        int count = 0;
        for (final NormalizedArithExpr expr : ilpNormalizedArithExprs) {
            final GRBLinExpr lhs = createExpr(expr.getCoefficients());

            double rhs = expr.getRhs().getVal();
            String name = expr.getName().orElse("cstr_" + count);
            if (expr instanceof LeqNormalizedArithExpr) {
                model.addConstr(lhs, GRB.LESS_EQUAL, rhs, name);
            } else if (expr instanceof GeqNormalizedArithExpr) {
                model.addConstr(lhs, GRB.GREATER_EQUAL, rhs, name);
            } else if (expr instanceof EqNormalizedArithExpr) {
                model.addConstr(lhs, GRB.EQUAL, rhs, name);
            } else {
                throw new RuntimeException("Constraint type was not recognized.");
            }
        }

    }

    private GRBLinExpr createExpr(List<Coefficient> coefficients) {
        final GRBLinExpr lhs = new GRBLinExpr();

        coefficients.forEach(
                coef -> {
                    double param = coef.getParam().getVal();
                    GRBVar var = vars.get(coef.getVar());
                    lhs.addTerm(param, var);
                }
        );
        return lhs;
    }

    public static class GurobiSolverBuilder {

        private String libraryPath = null;
        private Integer seed = null;
        private Long timeout = null;
        private boolean logging = false;
        private String modelOutputFilePath = null;
        private boolean withPresolve = true;
        private Double mipGap = null;
        private Integer feasibleSolLimit = null;

        private GurobiSolverBuilder() {
        }

        public GurobiSolverBuilder withSeed(int seed) {
            this.seed = seed;
            return this;
        }

        /**
         * Sets timeout of solver in seconds.
         *
         * @param timeout
         * @return
         */
        public GurobiSolverBuilder withTimeOut(long timeout) {
            this.timeout = timeout;
            return this;
        }

        /**
         * Sets limit for feasible solutions. Solver will stop if it has found the given number of feasible solutions
         * even if solution is not optimal.
         *
         * @param feasibleSolLimit
         * @return
         */
        public GurobiSolverBuilder withFeasibleSolLimit(int feasibleSolLimit) {
            this.feasibleSolLimit = feasibleSolLimit;
            return this;
        }

        public GurobiSolverBuilder withModelOutput(String absoluteFilePath) {
            this.modelOutputFilePath = absoluteFilePath;
            return this;
        }

        public GurobiSolverBuilder withMipGap(double mipGap) {
            this.mipGap = mipGap;
            return this;
        }

        public GurobiSolverBuilder withLibPath(String libraryPath) {
            this.libraryPath = libraryPath;
            return this;
        }

        public GurobiSolverBuilder withPresolve(boolean withPresolve) {
            this.withPresolve = withPresolve;
            return this;
        }

        public GurobiSolverBuilder withLogging(boolean logging) {
            this.logging = logging;
            return this;
        }

        public GurobiSolver build() {
            return new GurobiSolver(this);
        }
    }


}
