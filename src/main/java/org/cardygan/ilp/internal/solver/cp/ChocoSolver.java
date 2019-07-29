package org.cardygan.ilp.internal.solver.cp;

import org.cardygan.ilp.api.Result;
import org.cardygan.ilp.api.model.ArithExpr;
import org.cardygan.ilp.api.model.BinaryVar;
import org.cardygan.ilp.api.model.Var;
import org.cardygan.ilp.api.model.bool.*;
import org.cardygan.ilp.internal.expr.ExprSimplifier;
import org.cardygan.ilp.internal.solver.milp.MILPConstrGenerator;
import org.cardygan.ilp.internal.solver.milp.GurobiSolver.GurobiSolverBuilder;
import org.cardygan.ilp.internal.expr.BoolExprVisitor;
import org.cardygan.ilp.internal.util.ModelException;
import org.cardygan.ilp.internal.util.Util;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solution;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.constraints.Constraint;
import org.chocosolver.solver.expression.discrete.arithmetic.ArExpression;
import org.chocosolver.solver.expression.discrete.relational.ReExpression;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.solver.variables.Variable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ChocoSolver implements org.cardygan.ilp.internal.solver.Solver {

    private final Model chocoModel;
    private final Solver solver;
    private final int objLb;
    private final int objUb;
    private IntVar objVar;
    private boolean isMax = false;
    private Solution solution;

    public ChocoSolver(int objLb, int objUb) {
        this.objLb = objLb;
        this.objUb = objUb;
        this.chocoModel = new Model();
        this.solver = chocoModel.getSolver();
    }

    @Override
    public org.cardygan.ilp.api.model.Constraint[] newConstraint(org.cardygan.ilp.api.model.Model model, String name, BoolExpr expr) {
        if (!hasConstraint(name)) {
            expr.accept(new BoolExprConverter(chocoModel)).post();
            return new org.cardygan.ilp.api.model.Constraint[]{new org.cardygan.ilp.api.model.Constraint(name)};
        }
        throw new ModelException("Model already contains constraint with name " + name);
    }

    @Override
    public void newObjective(boolean maximize, ArithExpr expr) {
        objVar = chocoModel.intVar(objLb, objUb);
        convertArithExpr(expr, chocoModel).eq(objVar).post();
        isMax = maximize;
    }

    @Override
    public void removeConstr(org.cardygan.ilp.api.model.Constraint constraint) {
        if (!hasConstraint(constraint.getName()))
            throw new ModelException("Model does not contain constraint with name " + constraint.getName());
        retrieveCstrByName(constraint.getName(), chocoModel).ifPresent(chocoModel::unpost);
    }

    @Override
    public Result optimize() {
        final long start;
        final long end;

        if (objVar != null) {
            // solve with objective
            start = System.currentTimeMillis();
            solution = solver.findOptimalSolution(objVar, isMax);
            end = System.currentTimeMillis();
        } else {
            // solve without objective
            start = System.currentTimeMillis();
            solution = solver.findSolution();
            end = System.currentTimeMillis();
        }

        Result.SolverStatus status;
        if (solution != null)
            status = Result.SolverStatus.INFEASIBLE;
        else
            status = Result.SolverStatus.OPTIMAL;

        return new Result(status, end - start);
    }

    @Override
    public void dispose() {
        //TODO
    }

    @Override
    public boolean isDisposed() {
        //TODO
        return false;
    }

    @Override
    public double getVal(Var var) {
        if (solution != null) {
            Variable chocoVar = retrieveVarByName(var.getName(), chocoModel)
                    .orElseThrow(() -> new ModelException("Cannot find variable " + var.getName() + " in model."));

            return solution.getIntVal(chocoVar.asIntVar());
        }
        throw new ModelException("Check if optimize has been called before or if solution is optimal");
    }


    @Override
    public double getObjVal() {
        if (solution != null)
            return (double) solution.getIntVal(objVar);

        throw new ModelException("Check if optimize has been called before or if solution is optimal");
    }

    @Override
    public Solver getUnderlyingModel() {
        return solver;
    }

    @Override
    public void addVar(String name, double lb, double ub, VarType type) {
        if (lb >= 0 && ub >= 0 && lb > ub)
            throw new ModelException("Specified lower bound needs to be larger than upper bound.");


        if (hasVar(name))
            throw new ModelException("Variable with name " + name + " does already exist in model.");

        switch (type) {
            case INT:
                chocoModel.intVar(name, (int) lb, (int) ub);
                break;
            case BIN:
                chocoModel.boolVar(name);
                break;
            case DBL:
                //TODO generalize precision
                chocoModel.realVar(name, lb, ub, 0.01);
                break;
        }
    }

    @Override
    public int getNumVars() {
        return chocoModel.getNbVars();
    }

    @Override
    public int getNumConstrs() {
        return chocoModel.getNbCstrs();
    }

    @Override
    public boolean hasVar(String varName) {
        return retrieveVarByName(varName, chocoModel).isPresent();
    }

    @Override
    public boolean hasConstraint(String cstrName) {
        return retrieveCstrByName(cstrName, chocoModel).isPresent();
    }

    private static Optional<Variable> retrieveVarByName(String name, Model model) {
        return Arrays.stream(model.getVars())
                .filter(it -> it.getName().equals(name))
                .findAny();

    }

    private static Optional<Constraint> retrieveCstrByName(String name, Model model) {
        return Arrays.stream(model.getCstrs())
                .filter(it -> it.getName().equals(name))
                .findAny();
    }


    private class BoolExprConverter implements BoolExprVisitor<ReExpression> {

        private final Model model;

        BoolExprConverter(Model model) {
            this.model = model;
        }

        @Override
        public ReExpression visit(And expr) {
            ReExpression lhs = expr.getLhs().accept(this);
            ReExpression rhs = expr.getRhs().accept(this);

            return lhs.and(rhs);
        }

        @Override
        public ReExpression visit(Or expr) {
            ReExpression lhs = expr.getLhs().accept(this);
            ReExpression rhs = expr.getRhs().accept(this);

            return lhs.or(rhs);
        }

        @Override
        public ReExpression visit(Xor expr) {
            ReExpression lhs = expr.getLhs().accept(this);
            ReExpression rhs = expr.getRhs().accept(this);

            return lhs.and(rhs.not()).or(lhs.not().and(rhs));

        }

        @Override
        public ReExpression visit(Not expr) {
            return expr.getVal().accept(this).not();
        }

        @Override
        public ReExpression visit(Impl expr) {
            ReExpression lhs = expr.getLhs().accept(this);
            ReExpression rhs = expr.getRhs().accept(this);

            return lhs.not().or(rhs);
        }

        @Override
        public ReExpression visit(BiImpl expr) {
            ReExpression lhs = expr.getLhs().accept(this);
            ReExpression rhs = expr.getRhs().accept(this);

            return lhs.and(rhs).or(lhs.not().and(rhs.not()));
        }

        @Override
        public ReExpression visit(BinaryVar expr) {
            return retrieveVarByName(expr.getName(), model)
                    .orElseThrow(() -> new ModelException("Could not retrieve Variable with name " + expr.getName()))
                    .asBoolVar();
        }

        @Override
        public ReExpression visit(RelOp expr) {
            ArExpression lhs = convertArithExpr(expr.getLhs(), model);
            ArExpression rhs = convertArithExpr(expr.getRhs(), model);

            ReExpression ret;
            if (expr instanceof Eq)
                ret = lhs.eq(rhs);
            else if (expr instanceof Leq)
                ret = lhs.le(rhs);
            else if (expr instanceof Geq)
                ret = lhs.ge(rhs);
            else throw new IllegalStateException("Unknown relation type.");

            return ret;
        }
    }

    private ArExpression convertArithExpr(ArithExpr arithExpr, Model model) {
        ExprSimplifier.SimplifiedArithExpr simpleExpr = ExprSimplifier.simplify(arithExpr);

        List<ArExpression> sumList = simpleExpr.getCoeffs().entrySet().stream().map(
                e -> {
                    IntVar var = retrieveVarByName(e.getKey().getName(), model).orElseThrow(
                            () -> new ModelException("Could not retrieve variable.")
                    ).asIntVar();
                    int param = tryCastToInt(e.getValue());
                    if (param == 1)
                        return var.asIntVar();
                    else
                        return var.mul(param);
                }
        ).collect(Collectors.toList());

        Optional<ArExpression> sum = Optional.empty();
        if (sumList.size() > 1) {
            sum = Optional.of(sumList.get(0).add(sumList.toArray(new ArExpression[0])));
        } else if (sumList.size() == 1) {
            sum = Optional.of(sumList.get(0));
        }

        int constant = tryCastToInt(simpleExpr.getConstant());

        if (sum.isPresent())
            if (constant == 0)
                return sum.get();
            else
                return sum.get().add(constant);
        else return model.intVar(constant);
    }

    private int tryCastToInt(double val) {
        if (!Util.isInteger(val))
            throw new IllegalStateException("Double parameters not supported.");
        return new Double(val).intValue();
    }
    
    /**
     * This class represents an implementation of the SolverBuilder for the Choco Solver.
     * 
     * @author Maximilian Kratz <maximilian.kratz@stud.tu-darmstadt.de>
     *
     */
    public static class ChocoSolverBuilder implements SolverBuilder {
    	private final int objLb;
    	private final int objUb;
    	
    	/**
    	 * Constructor for initializing all parameters.
    	 * @param objLb Lower bound.
    	 * @param objUb Upper bound.
    	 */
		public ChocoSolverBuilder(int objLb, int objUb) {
			this.objLb = objLb;
			this.objUb = objUb;
		}

		@Override
		public org.cardygan.ilp.internal.solver.Solver build() {
			return new ChocoSolver(objLb, objUb);
		}
	}
}
