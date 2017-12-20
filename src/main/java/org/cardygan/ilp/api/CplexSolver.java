package org.cardygan.ilp.api;

import ilog.concert.IloException;
import ilog.concert.IloIntVar;
import ilog.concert.IloNumExpr;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;
import org.cardygan.ilp.api.expr.Var;
import org.cardygan.ilp.internal.Coefficient;
import org.cardygan.ilp.internal.expr.EqNormalizedArithExpr;
import org.cardygan.ilp.internal.expr.GeqNormalizedArithExpr;
import org.cardygan.ilp.internal.expr.LeqNormalizedArithExpr;
import org.cardygan.ilp.internal.expr.NormalizedArithExpr;
import org.cardygan.ilp.internal.util.IlpUtil;
import org.cardygan.ilp.internal.util.SolverUtil;

import java.lang.reflect.Field;
import java.util.*;
import java.util.Map.Entry;

public class CplexSolver implements Solver {

    private final static String ENV_VAR_CPLEX_LIB_PATH = "CPLEX_LIB_PATH";
    private final Optional<String> modelOutputFilePath;
    private final Optional<Integer> seed;
    private final boolean logging;
    private Map<Var, IloIntVar> vars;
    private Map<Var, IloNumVar> numVars;
    private Map<Var, Double> solutions;

    public CplexSolver() {
        this(Optional.empty(), Optional.empty(), Optional.empty(), false);
    }

    private CplexSolver(Optional<String> cplexLibraryPath, Optional<String> modelOutputFilePath, Optional<Integer> seed, boolean logging) {
        String libraryPath = cplexLibraryPath.isPresent() ? cplexLibraryPath.get() : System.getenv(ENV_VAR_CPLEX_LIB_PATH);
        if (libraryPath == null) {
            throw new IllegalStateException("Could not read Cplex library path. Environment variable " + ENV_VAR_CPLEX_LIB_PATH + " not set.");
        }
        loadLibraryFromPath(libraryPath);

        this.seed = seed;
        this.modelOutputFilePath = modelOutputFilePath;
        this.logging = logging;
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
    public Result solve(ModelContext model) {
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

            // Create vars
            for (Var var : model.getVars()) {


                if (IlpUtil.isBinaryVar(var)) {
                    vars.put(var, cplex.boolVar(var.getName()));
                } else if (IlpUtil.isIntVar(var)) {
                    int lb = 0;
                    int ub = Integer.MAX_VALUE;

                    if (model.getModel().getBounds(var) != null) {
                        Model.Bounds bounds = model.getModel().getBounds(var);
                        lb = bounds.getLb();
                        ub = bounds.getUb();
                    }

                    vars.put(var, cplex.intVar(lb, ub, var.getName()));
                } else if (IlpUtil.isDoubleVar(var)) {
                    double lb = -Double.MAX_VALUE;
                    double ub = Double.MAX_VALUE;

                    if (model.getModel().getBounds(var) != null) {
                        Model.Bounds bounds = model.getModel().getBounds(var);
                        lb = bounds.getLb();
                        ub = bounds.getUb();
                    }

                    numVars.put(var, cplex.numVar(lb, ub, var.getName()));

                } else {
                    throw new IllegalStateException("Not supported variable type.");
                }
            }

            // Create constraints
            constructConstraints(cplex, model.getNormalizedConstraints());

            // create sos1 constraints
            constructSos1Constraints(cplex, model.getSos1());

            if (model.getObjective().isMax()) {
                cplex.addMaximize(createObjectiveTerms(cplex, model.getObjective()));
            } else {
                cplex.addMinimize(createObjectiveTerms(cplex, model.getObjective()));
            }

            // deactivate presolve to prevent "UnounbdedOrInfeasible" status
            cplex.setParam(IloCplex.BooleanParam.PreInd, false);

            final long start = System.currentTimeMillis();
            boolean succ = cplex.solve();
            final long end = System.currentTimeMillis();

            if (modelOutputFilePath.isPresent()) {
                cplex.exportModel(modelOutputFilePath.get());
            }

            solutions = new HashMap<>();
            if (succ) {
                for (Entry<Var, IloIntVar> entry : vars.entrySet()) {
                    SolverUtil.assertIsInteger(cplex.getValue(entry.getValue()));
                    solutions.put(entry.getKey(), (double) Math.round(cplex.getValue(entry.getValue())));
                }

                for (Entry<Var, IloNumVar> entry : numVars.entrySet()) {
                    solutions.put(entry.getKey(), cplex.getValue(entry.getValue()));
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

            final Result res = new Result(model.getModel(), new Result.Statistics(succ, cplex.getStatus() == IloCplex.Status.Unbounded, end - start),
                    solutions, objVal);

            cplex.end();
            return res;

        } catch (IloException e) {
            e.printStackTrace();
        }
        throw new RuntimeException("Error in solver Run ");
    }

    private IloNumExpr createObjectiveTerms(IloCplex model, Objective obj) throws IloException {

        IloNumExpr[] exprs = new IloNumExpr[obj.getCoefficients().size()];
        int i = 0;
        for (Coefficient coef : obj.getCoefficients()) {
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
                model.addLe(model.sum(exprs), right, ilpNormalizedArithExpr.getName());
            } else if (ilpNormalizedArithExpr instanceof GeqNormalizedArithExpr) {
                model.addGe(model.sum(exprs), right, ilpNormalizedArithExpr.getName());
            } else if (ilpNormalizedArithExpr instanceof EqNormalizedArithExpr) {
                model.addEq(model.sum(exprs), right, ilpNormalizedArithExpr.getName());
            } else {
                throw new IllegalStateException("Unknown expression type.");
            }
        }

    }

    public static class CplexSolverBuilder {
        Optional<String> cplexLibraryPath = Optional.empty();
        Optional<String> modelOutputFilePath = Optional.empty();
        Optional<Integer> seed = Optional.empty();
        ;
        boolean logging = false;

        private CplexSolverBuilder() {

        }

        public CplexSolverBuilder withSeed(int seed) {
            this.seed = Optional.of(seed);
            return this;
        }

        public CplexSolverBuilder withModelOutput(String absoluteFilePath) {
            this.modelOutputFilePath = Optional.of(absoluteFilePath);
            return this;
        }

        public CplexSolverBuilder withLibPath(String libraryPath) {
            this.cplexLibraryPath = Optional.of(libraryPath);
            return this;
        }

        public CplexSolverBuilder withLogging(boolean logging) {
            this.logging = logging;
            return this;
        }

        public CplexSolver build() {
            return new CplexSolver(cplexLibraryPath, modelOutputFilePath, seed, logging);
        }
    }


}
