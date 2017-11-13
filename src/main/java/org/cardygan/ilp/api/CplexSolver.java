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
    private final boolean logging;
    private Map<Var, IloIntVar> vars;
    private Map<Var, Double> solutions;

    public CplexSolver() {
        String cplexLibraryPath = System.getenv(ENV_VAR_CPLEX_LIB_PATH);
        if (cplexLibraryPath == null) {
            throw new IllegalStateException("Could not read Cplex library path. Environment variable " + ENV_VAR_CPLEX_LIB_PATH + " not set.");
        }
        loadLibraryFromPath(cplexLibraryPath);

        modelOutputFilePath = Optional.empty();
        logging = false;

    }

    public CplexSolver(String cplexLibraryPath) {
        this(cplexLibraryPath, false, Optional.empty());
    }

    public CplexSolver(String cplexLibraryPath, boolean logging) {
        this(cplexLibraryPath, logging, Optional.empty());
    }

    public CplexSolver(String cplexLibraryPath, boolean logging, Optional<String> modelOutputFilePath) {
        this.logging = logging;
        this.modelOutputFilePath = modelOutputFilePath;

        loadLibraryFromPath(cplexLibraryPath);

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
                    vars.put(var, cplex.intVar(0, Integer.MAX_VALUE, var.getName()));
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
            }

            final Optional<Double> objVal;
            if (succ) {
                objVal = Optional.of(cplex.getObjValue());
            } else {
                objVal = Optional.empty();
            }

            final Result res = new Result(new Result.Statistics(model.getModel(), succ, cplex.getStatus() == IloCplex.Status.Unbounded, end - start),
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

            exprs[i] = model.prod(paramVal, vars.get(coef.getVar()));
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
            IloNumVar[] sosVars = sos.stream().map(v -> vars.get(v)).toArray(IloNumVar[]::new);
            model.addSOS1(sosVars, weights);
        }
    }

    private void constructConstraints(IloCplex model, List<NormalizedArithExpr> ilpNormalizedArithExprs) throws IloException {

        for (final NormalizedArithExpr ilpNormalizedArithExpr : ilpNormalizedArithExprs) {
            IloNumExpr[] exprs = new IloNumExpr[ilpNormalizedArithExpr.getCoefficients().size()];
            int i = 0;
            for (Coefficient coef : ilpNormalizedArithExpr.getCoefficients()) {
                exprs[i] = model.prod(coef.getParam().getVal(), vars.get(coef.getVar()));
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


}
