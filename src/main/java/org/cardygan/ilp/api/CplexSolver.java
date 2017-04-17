package org.cardygan.ilp.api;

import ilog.concert.IloException;
import ilog.concert.IloIntVar;
import ilog.concert.IloNumExpr;
import ilog.cplex.IloCplex;
import org.cardygan.ilp.api.expr.Var;
import org.cardygan.ilp.internal.Coefficient;
import org.cardygan.ilp.internal.expr.EqNormalizedArithExpr;
import org.cardygan.ilp.internal.expr.GeqNormalizedArithExpr;
import org.cardygan.ilp.internal.expr.LeqNormalizedArithExpr;
import org.cardygan.ilp.internal.expr.NormalizedArithExpr;
import org.cardygan.ilp.internal.util.IlpUtil;
import org.cardygan.ilp.internal.util.SolverUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

public class CplexSolver implements Solver {

    private Model model;
    private Map<Var, IloIntVar> vars;
    private Map<Var, Double> solutions;


    @Override
    public Result solveProblem(Model model) {
        vars = new HashMap<>();
        this.model = model;

        IloCplex cplex;
        try {

            cplex = new IloCplex();

            cplex.setOut(null);

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
            constructConstraints(cplex, model.getConstraints());


            if (model.getObjective().isMax()) {
                cplex.addMaximize(createObjectiveTerms(cplex, model.getObjective()));
            } else {
                cplex.addMinimize(createObjectiveTerms(cplex, model.getObjective()));
            }

            final long start = System.currentTimeMillis();
            boolean succ = cplex.solve();
            final long end = System.currentTimeMillis();


            cplex.exportModel("/Users/markus/Desktop/testOutput/model" + ".lp");
            solutions = new HashMap<>();
            if (succ) {
                for (Entry<Var, IloIntVar> entry : vars.entrySet()) {
                    SolverUtil.assertIsInteger(cplex.getValue(entry.getValue()));
                    solutions.put(entry.getKey(), (double) Math.round(cplex.getValue(entry.getValue())));
                }
            }

            final Optional<Double> objVal;
            if (succ) {
                SolverUtil.assertIsInteger(cplex.getObjValue());
                objVal = Optional.of((double) Math.round(cplex.getObjValue()));
            } else {
                objVal = Optional.empty();
            }
            final Result res = new Result(new Result.Statistics(succ, cplex.getStatus() == IloCplex.Status.Unbounded, end - start),
                    solutions, objVal);

            cplex.end();
            return res;

        } catch (IloException e) {
            e.printStackTrace();
        }
        throw new RuntimeException("Error in solver Run ");
    }

    @Override
    public double getVal(Var var) {
        return solutions.get(var);
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
                throw new IllegalStateException("Unknown expr type.");
            }
        }

    }


}
