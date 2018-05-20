package org.cardygan.ilp.api.solver;

import ilog.concert.IloException;
import ilog.concert.IloIntVar;
import ilog.concert.IloNumExpr;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;
import org.cardygan.ilp.api.model.Model;
import org.cardygan.ilp.api.Result;
import org.cardygan.ilp.api.model.Var;
import org.cardygan.ilp.internal.expr.Coefficient;
import org.cardygan.ilp.internal.expr.*;
import org.cardygan.ilp.internal.expr.model.*;
import org.cardygan.ilp.internal.util.IlpUtil;
import org.cardygan.ilp.internal.util.SolverUtil;
import org.cardygan.ilp.internal.util.Util;

import java.lang.reflect.Field;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import static org.cardygan.ilp.api.util.ExprDsl.p;

public class CplexSolver implements Solver {

    private final static String ENV_VAR_CPLEX_LIB_PATH = "CPLEX_LIB_PATH";
    private final Optional<String> modelOutputFilePath;
    private final Optional<Integer> seed;
    private final boolean logging;
    private final boolean preSolve;
    private final int threadCount;
    private final int parallelMode;
    private Map<Var, IloIntVar> vars;
    private Map<Var, IloNumVar> numVars;
    private Map<Var, Double> solutions;
    private final Optional<Long> timeout;

    public CplexSolver() {
        this(new CplexSolverBuilder());
    }

    private CplexSolver(CplexSolverBuilder builder) {
        String libraryPath = builder.cplexLibraryPath;
        if (libraryPath == null) {
            throw new IllegalStateException("Could not read Cplex library path. Environment variable " + ENV_VAR_CPLEX_LIB_PATH + " not set.");
        }
        loadLibraryFromPath(libraryPath);

        this.seed = builder.seed;
        this.modelOutputFilePath = builder.modelOutputFilePath;
        this.logging = builder.logging;
        this.preSolve = builder.presolve;
        this.threadCount = builder.threadCount;
        this.parallelMode = builder.parallelMode;
        this.timeout = builder.timeout;
    }

    public static CplexSolverBuilder create() {
        return new CplexSolverBuilder();
    }

    private void loadLibraryFromPath(String cplexLibraryPath) {
        try {
            System.setProperty("java.library.path", cplexLibraryPath);
            Field fieldSysPath = ClassLoader.class.getDeclaredField("sys_paths");
            fieldSysPath.setAccessible(true);
            fieldSysPath.set(null, null);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            System.err.println("Could not load cplex library from path " + cplexLibraryPath);
            e.printStackTrace();
        }
    }

    @Override
    public Result solve(Model model) {
        BasicModel basicModel = IlpToBasicConverter.convert(model);

        vars = new HashMap<>();
        numVars = new HashMap<>();

        IloCplex cplex;
        try {

            cplex = new IloCplex();

            if (logging) {
                cplex.setOut(System.out);
            } else {
                cplex.setOut(null);
            }

            if (timeout.isPresent()) {
                cplex.setParam(IloCplex.Param.TimeLimit, timeout.get());
            }

            // Create vars
            for (Var var : basicModel.getVars().values()) {

                if (IlpUtil.isBinaryVar(var)) {
                    vars.put(var, cplex.boolVar(var.getName()));
                } else if (IlpUtil.isIntVar(var)) {
                    int lb = 0;
                    int ub = Integer.MAX_VALUE;

                    if (basicModel.getBounds(var) != null) {
                        Model.Bounds bounds = basicModel.getBounds(var);
                        lb = new Double(bounds.getLb()).intValue();
                        ub = new Double(bounds.getUb()).intValue();
                    }

                    vars.put(var, cplex.intVar(lb, ub, var.getName()));
                } else if (IlpUtil.isDoubleVar(var)) {
                    double lb = -Double.MAX_VALUE;
                    double ub = Double.MAX_VALUE;

                    if (basicModel.getBounds(var) != null) {
                        Model.Bounds bounds = basicModel.getBounds(var);
                        lb = bounds.getLb();
                        ub = bounds.getUb();
                    }

                    numVars.put(var, cplex.numVar(lb, ub, var.getName()));

                } else {
                    throw new IllegalStateException("Not supported variable type.");
                }
            }

            // Create constraints
            constructConstraints(cplex, basicModel.getConstraints());

            // create sos1 constraints
            constructSos1Constraints(cplex, basicModel.getSos1());


//            List<Coefficient> coefficients = simplifier.getSummands().stream().map(p -> Util.coef(p(p.getFirst()), p.getSecond())).collect(Collectors.toList());
            List<Coefficient> coefficients = basicModel.getObjective().getCoefficients();
            double constant = basicModel.getObjective().getConstant();

            if (model.getObjective().isMax()) {
                cplex.addMaximize(createObjectiveTerms(cplex, coefficients, constant));
            } else {
                cplex.addMinimize(createObjectiveTerms(cplex, coefficients, constant));
            }

            // deactivate presolve to prevent "UnounbdedOrInfeasible" status
            cplex.setParam(IloCplex.BooleanParam.PreInd, preSolve);

            cplex.setParam(IloCplex.IntParam.ParallelMode, parallelMode);
            cplex.setParam(IloCplex.Param.Threads, threadCount);

            final long start = System.currentTimeMillis();
            boolean succ = cplex.solve();
            final long end = System.currentTimeMillis();

            if (modelOutputFilePath.isPresent()) {
                cplex.exportModel(modelOutputFilePath.get());
            }

            solutions = new HashMap<>();
            if (succ) {
                for (Entry<Var, IloIntVar> entry : vars.entrySet()) {
                    try {
                        SolverUtil.assertIsInteger(cplex.getValue(entry.getValue()));
                        solutions.put(entry.getKey(), (double) Math.round(cplex.getValue(entry.getValue())));
                    } catch (IloCplex.UnknownObjectException e) {
                        if (logging)
                            System.err.println("Warning: Variable " + entry.getKey().getName()
                                    + " is not in the active model! Could not retrieve objective value.");
                        solutions.put(entry.getKey(), 0d);
                    }
                }

                for (Entry<Var, IloNumVar> entry : numVars.entrySet()) {
                    try {
                        solutions.put(entry.getKey(), cplex.getValue(entry.getValue()));
                    } catch (IloCplex.UnknownObjectException e) {
                        if (logging)
                            System.err.println("Warning: Variable " + entry.getKey().getName()
                                    + " is not in the active model! Could not retrieve objective value.");
                        solutions.put(entry.getKey(), 0d);
                    }
                }
            }

            if (seed.isPresent()) {
                cplex.setParam(IloCplex.Param.RandomSeed, seed.get());
            }

            final Optional<Double> objVal;
            if (succ) {
                objVal = Optional.of(cplex.getObjValue());
            } else {
                objVal = Optional.empty();
            }

            final Result res = new Result(model, new Result.Statistics(succ, cplex.getStatus() == IloCplex.Status.Unbounded, end - start),
                    solutions, objVal);

            cplex.end();
            return res;

        } catch (IloException e) {
            e.printStackTrace();
        }
        throw new RuntimeException("Error in solver Run ");
    }

    private IloNumExpr createObjectiveTerms(IloCplex model, List<Coefficient> coefficients, double constant) throws IloException {

        IloNumExpr[] exprs = new IloNumExpr[coefficients.size() + 1];

        exprs[0] = model.constant(constant);

        int i = 1;
        for (Coefficient coef : coefficients) {
            double paramVal = coef.getParam().getVal();

            if (vars.containsKey(coef.getVar())) {
                exprs[i] = model.prod(paramVal, vars.get(coef.getVar()));
            } else if (numVars.containsKey(coef.getVar())) {
                exprs[i] = model.prod(paramVal, numVars.get(coef.getVar()));
            } else {
                throw new IllegalStateException("Could not find variable corresponding to " + coef.getVar().getName());
            }
            i++;
        }
        return model.sum(exprs);
    }

    private void constructSos1Constraints(IloCplex model, List<Set<Var>> sets) throws IloException {
        double d = 1;
        for (Set<Var> sos : sets) {
            double[] weights = new double[sos.size()];
            for (int i = 0; i < sos.size(); i++) {
                weights[i] = i + 1;
            }
            IloNumVar[] sosVars = sos.stream().map(v -> {
                if (vars.containsKey(v)) {
                    return vars.get(v);
                } else if (numVars.containsKey(v)) {
                    return numVars.get(v);
                } else {
                    throw new IllegalStateException("Could not find variable corresponding to " + v.getName());
                }
            }).toArray(IloNumVar[]::new);
            model.addSOS1(sosVars, weights);
        }
    }

    private void constructConstraints(IloCplex model, List<NormalizedArithExpr> ilpNormalizedArithExprs) throws IloException {

        for (final NormalizedArithExpr ilpNormalizedArithExpr : ilpNormalizedArithExprs) {
            IloNumExpr[] exprs = new IloNumExpr[ilpNormalizedArithExpr.getCoefficients().size()];
            int i = 0;
            for (Coefficient coef : ilpNormalizedArithExpr.getCoefficients()) {

                if (vars.containsKey(coef.getVar())) {
                    exprs[i] = model.prod(coef.getParam().getVal(), vars.get(coef.getVar()));
                } else if (numVars.containsKey(coef.getVar())) {
                    exprs[i] = model.prod(coef.getParam().getVal(), numVars.get(coef.getVar()));
                } else {
                    throw new IllegalStateException("Could not find variable corresponding to " + coef.getVar().getName());
                }

                i++;
            }

            double right = ilpNormalizedArithExpr.getRhs().getVal();

            if (ilpNormalizedArithExpr instanceof LeqNormalizedArithExpr) {
                if (ilpNormalizedArithExpr.getName().isPresent())
                    model.addLe(model.sum(exprs), right, ilpNormalizedArithExpr.getName().get());
                else
                    model.addLe(model.sum(exprs), right);
            } else if (ilpNormalizedArithExpr instanceof GeqNormalizedArithExpr) {
                if (ilpNormalizedArithExpr.getName().isPresent())
                    model.addGe(model.sum(exprs), right, ilpNormalizedArithExpr.getName().get());
                else
                    model.addGe(model.sum(exprs), right);
            } else if (ilpNormalizedArithExpr instanceof EqNormalizedArithExpr) {
                if (ilpNormalizedArithExpr.getName().isPresent())
                    model.addEq(model.sum(exprs), right, ilpNormalizedArithExpr.getName().get());
                else
                    model.addEq(model.sum(exprs), right);
            } else {
                throw new IllegalStateException("Unknown expression type.");
            }
        }

    }

    public static class CplexSolverBuilder {
        public final static int CPX_PARALLEL_OPPORTUNISTIC = -1;
        public final static int CPX_PARALLEL_AUTO = 0;
        public final static int CPX_PARALLEL_DETERMINISTIC = 1;

        String cplexLibraryPath;
        Optional<String> modelOutputFilePath = Optional.empty();
        Optional<Integer> seed = Optional.empty();
        int parallelMode = CPX_PARALLEL_AUTO;
        int threadCount = 0;
        boolean presolve = false;
        Optional<Long> timeout = Optional.empty();

        boolean logging = false;

        private CplexSolverBuilder() {
            this.cplexLibraryPath = System.getenv(ENV_VAR_CPLEX_LIB_PATH);
        }

        public CplexSolverBuilder withSeed(int seed) {
            this.seed = Optional.of(seed);
            return this;
        }

        /**
         * Sets timeout of solver in seconds.
         *
         * @param timeout
         * @return
         */
        public CplexSolverBuilder withTimeOut(long timeout) {
            this.timeout = Optional.of(timeout);
            return this;
        }

        public CplexSolverBuilder withPresolve() {
            this.presolve = true;
            return this;
        }

        public CplexSolverBuilder withThreadCount(int noThreads) {
            this.threadCount = noThreads;
            return this;
        }

        public CplexSolverBuilder withParallelMode(int parallelMode) {
            this.parallelMode = parallelMode;
            return this;
        }

        public CplexSolverBuilder withModelOutput(String absoluteFilePath) {
            this.modelOutputFilePath = Optional.of(absoluteFilePath);
            return this;
        }

        public CplexSolverBuilder withLibPath(String libraryPath) {
            this.cplexLibraryPath = libraryPath;
            return this;
        }

        public CplexSolverBuilder withLogging(boolean logging) {
            this.logging = logging;
            return this;
        }

        public CplexSolver build() {
            return new CplexSolver(this);
        }
    }


}