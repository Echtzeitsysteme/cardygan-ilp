//package org.cardygan.ilp.internal;
//
//import ilog.concert.*;
//import ilog.cplex.IloCplex;
//import org.cardygan.ilp.api.Result;
//import org.cardygan.ilp.api.model.*;
//import org.cardygan.ilp.internal.expr.Coefficient;
//import org.cardygan.ilp.internal.expr.model.*;
//import org.cardygan.ilp.internal.util.IlpUtil;
//import org.cardygan.ilp.internal.util.LibraryUtil;
//import org.cardygan.ilp.internal.util.SolverUtil;
//
//import java.util.HashMap;
//import java.util.Iterator;
//import java.util.List;
//import java.util.Map;
//import java.util.Map.Entry;
//import java.util.concurrent.TimeUnit;
//
//public class CplexSolver implements SolverOld {
//
//    private final static String ENV_VAR_CPLEX_LIB_PATH = "CPLEX_LIB_PATH";
//    private final String modelOutputFilePath;
//    private final Integer seed;
//    private final boolean logging;
//    private final boolean preSolve;
//    private final Integer threadCount;
//    private final int parallelMode;
//    private Map<Var, IloIntVar> vars;
//    private Map<Var, IloNumVar> numVars;
//    private final Long timeout;
//    private final Long feasibleSolLimit;
//
//    public CplexSolver() {
//        this(new CplexSolverBuilder());
//    }
//
//    private CplexSolver(CplexSolverBuilder builder) {
//        String libraryPath = builder.cplexLibraryPath;
//        if (libraryPath == null) {
//            throw new IllegalStateException("Could not read Cplex library path. Environment variable " + ENV_VAR_CPLEX_LIB_PATH + " not set.");
//        }
//        LibraryUtil.loadLibraryFromPath(libraryPath);
//
//        this.seed = builder.seed;
//        this.modelOutputFilePath = builder.modelOutputFilePath;
//        this.logging = builder.logging;
//        this.preSolve = builder.presolve;
//        this.threadCount = builder.threadCount;
//        this.parallelMode = builder.parallelMode;
//        this.timeout = builder.timeout;
//        this.feasibleSolLimit = builder.feasibleSolLimit;
//    }
//
//    public static CplexSolverBuilder create() {
//        return new CplexSolverBuilder();
//    }
//
//
//    @Override
//    public Result solve(Model model) {
//        BasicModel basicModel = IlpToBasicConverter.convert(model);
//
//        vars = new HashMap<>();
//        numVars = new HashMap<>();
//
//        IloCplex cplex;
//        try {
//
//            cplex = new IloCplex();
//
//
//            if (logging) {
//                cplex.setOut(System.out);
//            } else {
//                cplex.setOut(null);
//            }
//
//            if (timeout != null) {
//                cplex.setParam(IloCplex.Param.TimeLimit, timeout);
//            }
//
//            // Create vars
//            for (Var var : basicModel.getVars().values()) {
//
//                if (IlpUtil.isBinaryVar(var)) {
//                    vars.put(var, cplex.boolVar(var.getName()));
//                } else if (IlpUtil.isIntVar(var)) {
//                    int lb = -Integer.MAX_VALUE;
//                    int ub = Integer.MAX_VALUE;
//
//                    if (basicModel.getBounds((IntVar) var) != null) {
//                        IntBounds bounds = basicModel.getBounds((IntVar) var);
//                        lb = bounds.getLb();
//
//                        int newUb = bounds.getUb();
//                        if (newUb >= 0)
//                            ub = newUb;
//                    }
//
//                    if (lb > ub)
//                        throw new IllegalArgumentException("Lower bound of variable " + var + "needs to be less than upper bound.");
//
//                    vars.put(var, cplex.intVar(lb, ub, var.getName()));
//                } else if (IlpUtil.isDoubleVar(var)) {
//                    double lb = -Double.MAX_VALUE;
//                    double ub = Double.MAX_VALUE;
//
//                    if (basicModel.getBounds((DoubleVar) var) != null) {
//                        DblBounds dblBounds = basicModel.getBounds((DoubleVar) var);
//                        lb = dblBounds.getLb();
//                        double newUb = dblBounds.getUb();
//                        if (newUb >= 0)
//                            ub = newUb;
//                    }
//
//                    if (lb > ub)
//                        throw new IllegalArgumentException("Lower bound of variable " + var + "needs to be less than upper bound.");
//
//                    numVars.put(var, cplex.numVar(lb, ub, var.getName()));
//
//                } else {
//                    throw new IllegalStateException("Not supported variable type.");
//                }
//            }
//
//            // Create constraints
//            constructConstraints(cplex, basicModel.getConstraints());
//
//            // create sos1 constraints
//            constructSos1Constraints(cplex, basicModel.getSos1());
//
//            // Create objective
//            if (basicModel.getObjective().isPresent()) {
//                List<Coefficient> coefficients = basicModel.getObjective().get().getCoefficients();
//                double constant = basicModel.getObjective().get().getConstant();
//
//                if (basicModel.getObjective().get().isMax()) {
//                    cplex.addMaximize(createObjectiveTerms(cplex, coefficients, constant));
//                } else {
//                    cplex.addMinimize(createObjectiveTerms(cplex, coefficients, constant));
//                }
//            }
//
//            // deactivate pre-solve to prevent "UnounbdedOrInfeasible" status
//            cplex.setParam(IloCplex.BooleanParam.PreInd, preSolve);
//
//            cplex.setParam(IloCplex.IntParam.ParallelMode, parallelMode);
//
//            if (threadCount != null)
//                cplex.setParam(IloCplex.Param.Threads, threadCount);
//
//            if (feasibleSolLimit != null)
//                cplex.setParam(IloCplex.LongParam.IntSolLim, feasibleSolLimit);
//
//
//            if (seed != null) {
//                cplex.setParam(IloCplex.Param.RandomSeed, seed);
//            }
//
//            class PresolveCallback extends IloCplex.PresolveCallback {
//
//                private int rowsRemovedByPresolve = 0;
//                private int colsRemovedByPresolve = 0;
//
//                @Override
//                protected void main() throws IloException {
//                    colsRemovedByPresolve = Math.max(getNremovedCols(), colsRemovedByPresolve);
//                    rowsRemovedByPresolve = Math.max(getNremovedRows(), rowsRemovedByPresolve);
//                }
//            }
//
//            PresolveCallback callback = null;
//            int rowsRemovedByPresolve = 0;
//            int colsRemovedByPresolve = 0;
//
//            if (preSolve) {
//
//
//                callback = new PresolveCallback();
//                cplex.use(callback);
//            }
//            final long start = System.nanoTime();
//            boolean succ = cplex.solve();
//            final long end = System.nanoTime();
//
//            if (modelOutputFilePath != null) {
//                cplex.exportModel(modelOutputFilePath);
//            }
//
//            Map<Var, Double> solutions = new HashMap<>();
//            if (succ) {
//                for (Entry<Var, IloIntVar> entry : vars.entrySet()) {
//                    try {
//                        SolverUtil.assertIsInteger(cplex.getValue(entry.getValue()));
//                        solutions.put(entry.getKey(), (double) Math.round(cplex.getValue(entry.getValue())));
//                    } catch (IloCplex.UnknownObjectException e) {
//                        if (logging)
//                            System.err.println("Warning: Variable " + entry.getKey().getName()
//                                    + " is not in the active model! Could not retrieve objective value.");
//                        solutions.put(entry.getKey(), 0d);
//                    }
//                }
//
//                for (Entry<Var, IloNumVar> entry : numVars.entrySet()) {
//                    try {
//                        solutions.put(entry.getKey(), cplex.getValue(entry.getValue()));
//                    } catch (IloCplex.UnknownObjectException e) {
//                        if (logging)
//                            System.err.println("Warning: Variable " + entry.getKey().getName()
//                                    + " is not in the active model! Could not retrieve objective value.");
//                        solutions.put(entry.getKey(), 0d);
//                    }
//                }
//            }
//
//
//            final Double objVal;
//            if (succ) {
//                objVal = cplex.getObjValue();
//            } else {
//                objVal = null;
//            }
//
//            if (callback != null) {
//                rowsRemovedByPresolve = callback.rowsRemovedByPresolve;
//                colsRemovedByPresolve = callback.colsRemovedByPresolve;
//            }
//
//            Result.SolverStatus status;
//            if (cplex.getStatus() == IloCplex.Status.Unbounded) {
//                status = Result.SolverStatus.UNBOUNDED;
//            } else if (cplex.getStatus() == IloCplex.Status.InfeasibleOrUnbounded) {
//                status = Result.SolverStatus.INF_OR_UNBD;
//            } else if (cplex.getStatus() == IloCplex.Status.Infeasible) {
//                status = Result.SolverStatus.INFEASIBLE;
//            } else if (cplex.getStatus() == IloCplex.Status.Optimal) {
//                status = Result.SolverStatus.OPTIMAL;
//            } else if (cplex.getStatus() == IloCplex.Status.Unknown) {
//                status = Result.SolverStatus.TIME_OUT;
//            } else {
//                throw new RuntimeException("Unknown internal status.");
//            }
//
//            final long duration = TimeUnit.MILLISECONDS.convert(end - start, TimeUnit.NANOSECONDS);
//            final Result res = new Result(model, new Result.Statistics(status, duration, colsRemovedByPresolve, rowsRemovedByPresolve),
//                    solutions, objVal);
//
//            cplex.clearCallbacks();
//            cplex.clearModel();
//            cplex.end();
//            return res;
//
//        } catch (IloException e) {
//            e.printStackTrace();
//        }
//        throw new RuntimeException("Error in internal Run ");
//    }
//
//    private IloNumExpr createObjectiveTerms(IloCplex model, List<Coefficient> coefficients, double constant) throws IloException {
//
//        IloNumExpr[] exprs = new IloNumExpr[coefficients.size() + 1];
//
//        exprs[0] = model.constant(constant);
//
//        int i = 1;
//        for (Coefficient coef : coefficients) {
//            double paramVal = coef.getParam().getVal();
//
//            if (vars.containsKey(coef.getVar())) {
//                exprs[i] = model.prod(paramVal, vars.get(coef.getVar()));
//            } else if (numVars.containsKey(coef.getVar())) {
//                exprs[i] = model.prod(paramVal, numVars.get(coef.getVar()));
//            } else {
//                throw new IllegalStateException("Could not find variable corresponding to " + coef.getVar().getName());
//            }
//            i++;
//        }
//        return model.sum(exprs);
//    }
//
//    private void constructSos1Constraints(IloCplex model, List<Sos1Constraint> cstrs) throws IloException {
//
//        for (Sos1Constraint sos : cstrs) {
//            double[] weights = new double[sos.getElements().values().size()];
//            IloNumVar[] sosVars = new IloNumVar[sos.getElements().keySet().size()];
//
//            int i = 0;
//            for (Entry<Var, Double> entry : sos.getElements().entrySet()) {
//                weights[i] = entry.getValue();
//                if (vars.containsKey(entry.getKey())) {
//                    sosVars[i] = vars.get(entry.getKey());
//                } else {
//                    throw new IllegalStateException("Could not find variable corresponding to " + entry.getKey().getName());
//                }
//
//                i++;
//            }
//
//            model.addSOS1(sosVars, weights);
//        }
//    }
//
//
//    private void constructConstraints(IloCplex model, List<NormalizedArithExpr> ilpNormalizedArithExprs) throws IloException {
//
//        for (final NormalizedArithExpr ilpNormalizedArithExpr : ilpNormalizedArithExprs) {
//            IloNumExpr[] exprs = new IloNumExpr[ilpNormalizedArithExpr.getCoefficients().size()];
//            int i = 0;
//            for (Coefficient coef : ilpNormalizedArithExpr.getCoefficients()) {
//
//                if (vars.containsKey(coef.getVar())) {
//                    exprs[i] = model.prod(coef.getParam().getVal(), vars.get(coef.getVar()));
//                } else if (numVars.containsKey(coef.getVar())) {
//                    exprs[i] = model.prod(coef.getParam().getVal(), numVars.get(coef.getVar()));
//                } else {
//                    throw new IllegalStateException("Could not find variable corresponding to " + coef.getVar().getName());
//                }
//
//                i++;
//            }
////            Iterator iter = model.rangeIterator();
////            IloRange cstr = (IloRange) iter.next();
////            cstr.getName();
//
//            double right = ilpNormalizedArithExpr.getRhs().getVal();
//
//            if (ilpNormalizedArithExpr instanceof LeqNormalizedArithExpr) {
//                if (ilpNormalizedArithExpr.getName().isPresent())
//                    model.addLe(model.sum(exprs), right, ilpNormalizedArithExpr.getName().get());
//                else
//                    model.addLe(model.sum(exprs), right);
//            } else if (ilpNormalizedArithExpr instanceof GeqNormalizedArithExpr) {
//                if (ilpNormalizedArithExpr.getName().isPresent())
//                    model.addGe(model.sum(exprs), right, ilpNormalizedArithExpr.getName().get());
//                else
//                    model.addGe(model.sum(exprs), right);
//            } else if (ilpNormalizedArithExpr instanceof EqNormalizedArithExpr) {
//                if (ilpNormalizedArithExpr.getName().isPresent())
//                    model.addEq(model.sum(exprs), right, ilpNormalizedArithExpr.getName().get());
//                else
//                    model.addEq(model.sum(exprs), right);
//            } else {
//                throw new IllegalStateException("Unknown expression type.");
//            }
//        }
//
//    }
//
//    public static class CplexSolverBuilder {
//        public final static int CPX_PARALLEL_OPPORTUNISTIC = -1;
//        public final static int CPX_PARALLEL_AUTO = 0;
//        public final static int CPX_PARALLEL_DETERMINISTIC = 1;
//
//        private String cplexLibraryPath;
//        private String modelOutputFilePath = null;
//        private Integer seed = null;
//        private int parallelMode = CPX_PARALLEL_AUTO;
//        private Integer threadCount;
//        private boolean presolve = true;
//        private Long timeout = null;
//        private boolean logging = false;
//        private Long feasibleSolLimit = null;
//
//        private CplexSolverBuilder() {
//            this.cplexLibraryPath = System.getenv(ENV_VAR_CPLEX_LIB_PATH);
//        }
//
//        public CplexSolverBuilder withSeed(int seed) {
//            this.seed = seed;
//            return this;
//        }
//
//        /**
//         * Sets timeout of internal in seconds.
//         *
//         * @param timeout
//         * @return
//         */
//        public CplexSolverBuilder withTimeOut(long timeout) {
//            this.timeout = timeout;
//            return this;
//        }
//
//        public CplexSolverBuilder withPresolve() {
//            this.presolve = true;
//            return this;
//        }
//
//        /**
//         * Sets limit for feasible solutions. SolverOld will stop if it has found the given number of feasible solutions
//         * even if solution is not optimal.
//         *
//         * @param feasibleSolLimit
//         * @return
//         */
//        public CplexSolverBuilder withFeasibleSolLimit(long feasibleSolLimit) {
//            this.feasibleSolLimit = feasibleSolLimit;
//            return this;
//        }
//
//        public CplexSolverBuilder withPresolve(boolean presolve) {
//            this.presolve = presolve;
//            return this;
//        }
//
//        public CplexSolverBuilder withThreadCount(int noThreads) {
//            this.threadCount = noThreads;
//            return this;
//        }
//
//        public CplexSolverBuilder withParallelMode(int parallelMode) {
//            this.parallelMode = parallelMode;
//            return this;
//        }
//
//        public CplexSolverBuilder withModelOutput(String absoluteFilePath) {
//            this.modelOutputFilePath = absoluteFilePath;
//            return this;
//        }
//
//        public CplexSolverBuilder withLibPath(String libraryPath) {
//            this.cplexLibraryPath = libraryPath;
//            return this;
//        }
//
//        public CplexSolverBuilder withLogging(boolean logging) {
//            this.logging = logging;
//            return this;
//        }
//
//        public CplexSolver build() {
//            return new CplexSolver(this);
//        }
//    }
//
//
//}
