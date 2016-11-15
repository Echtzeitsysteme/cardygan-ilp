package org.cardygan.ilp;

import ilog.concert.IloException;
import ilog.concert.IloIntVar;
import ilog.concert.IloNumExpr;
import ilog.cplex.IloCplex;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

public class CplexSolver implements Solver {

    private final IlpModel ilpModel;
    private Map<IlpVar, IloIntVar> vars;
    private Map<IlpVar, Double> solutions;

    public CplexSolver(IlpModel ilpModel) {
        this.ilpModel = ilpModel;

    }

    @Override
    public Result solveProblem() {
        vars = new HashMap<>();


        IloCplex cplex;
        try {

            cplex = new IloCplex();

            cplex.setOut(null);

            for (IlpVar ilpVar : ilpModel.getIlpVars()) {
                if (IlpUtil.isBinaryVar(ilpVar)) {
                    vars.put(ilpVar, cplex.boolVar(ilpVar.getName()));
                } else if (IlpUtil.isIntVar(ilpVar)) {
                    vars.put(ilpVar, cplex.intVar(0, Integer.MAX_VALUE, ilpVar.getName()));
                } else {
                    throw new IllegalStateException("Not supported variable type.");
                }
            }

            // Create constraints
            constructConstraints(cplex, ilpModel.getIlpConstraints());


            if (ilpModel.getObjective().isMax()) {
                cplex.addMaximize(createObjectiveTerms(cplex, ilpModel.getObjective()));
            } else {
                cplex.addMinimize(createObjectiveTerms(cplex, ilpModel.getObjective()));
            }

            final long start = System.currentTimeMillis();
            boolean succ = cplex.solve();
            final long end = System.currentTimeMillis();


//            cplex.exportModel("/Users/markus/Desktop/testOutput/model" + ".lp");
            solutions = new HashMap<>();
            if (succ) {
                for (Entry<IlpVar, IloIntVar> entry : vars.entrySet()) {
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
    public double getVal(IlpVar var) {
        return solutions.get(var);
    }

    private IloNumExpr createObjectiveTerms(IloCplex model, Objective obj) throws IloException {

        IloNumExpr[] exprs = new IloNumExpr[obj.getCoefficients().size()];
        int i = 0;
        for (Coefficient coef : obj.getCoefficients()) {
            double paramVal = coef.getIlpParam().getVal();

            exprs[i] = model.prod(paramVal, vars.get(coef.getIlpVar()));
            i++;
        }
        return model.sum(exprs);
    }

    private void constructConstraints(IloCplex model, List<IlpConstraint> ilpConstraints) throws IloException {

        for (final IlpConstraint ilpConstraint : ilpConstraints) {
            IloNumExpr[] exprs = new IloNumExpr[ilpConstraint.getCoefficientsConsolidated().size()];
            int i = 0;
            for (Coefficient coef : ilpConstraint.getCoefficientsConsolidated()) {
                exprs[i] = model.prod(coef.getIlpParam().getVal(), vars.get(coef.getIlpVar()));
                i++;
            }

            double right = ilpConstraint.getRhs().getVal();

            if (ilpConstraint instanceof IlpLeqConstraint) {
                model.addLe(model.sum(exprs), right, ilpConstraint.getName());
            } else if (ilpConstraint instanceof IlpGeqConstraint) {
                model.addGe(model.sum(exprs), right, ilpConstraint.getName());
            } else if (ilpConstraint instanceof IlpEqConstraint) {
                model.addEq(model.sum(exprs), right, ilpConstraint.getName());
            } else {
                throw new IllegalStateException("Unkown constraint type.");
            }
        }

    }


}
