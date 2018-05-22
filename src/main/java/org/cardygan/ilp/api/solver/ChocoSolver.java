package org.cardygan.ilp.api.solver;

import org.cardygan.ilp.api.Result;
import org.cardygan.ilp.api.model.ArithExpr;
import org.cardygan.ilp.api.model.BinaryVar;
import org.cardygan.ilp.api.model.Model;
import org.cardygan.ilp.api.model.Var;
import org.cardygan.ilp.api.model.bool.*;
import org.cardygan.ilp.internal.expr.ArithExprSimplifier;
import org.cardygan.ilp.internal.expr.BoolExprVisitor;
import org.cardygan.ilp.internal.util.IlpUtil;
import org.cardygan.ilp.internal.util.Util;
import org.chocosolver.solver.Solution;
import org.chocosolver.solver.expression.discrete.arithmetic.ArExpression;
import org.chocosolver.solver.expression.discrete.relational.ReExpression;
import org.chocosolver.solver.variables.BoolVar;
import org.chocosolver.solver.variables.IntVar;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ChocoSolver implements Solver {

    private final Map<Var, IntVar> vars = new HashMap<>();
    private final Integer objLb;
    private final Integer objUb;
    private final Integer intVarLb;
    private final Integer intVarUb;
    private final Long timeout;

    public ChocoSolver(ChocoSolverBuilder builder) {

        this.objLb = builder.objLb;
        this.objUb = builder.objUb;
        this.intVarLb = builder.intVarLb;
        this.intVarUb = builder.intVarUb;
        this.timeout = builder.timeout;
    }

    @Override
    public Result solve(Model model) {
        vars.clear();

        org.chocosolver.solver.Model chocoModel = new org.chocosolver.solver.Model();

        // Create vars
        for (Var var : model.getVars().values()) {

            if (IlpUtil.isBinaryVar(var)) {
                IntVar chocoVar = chocoModel.boolVar(var.getName());
                vars.put(var, chocoVar);
            } else if (IlpUtil.isIntVar(var)) {
                final int lb;
                final int ub;

                if (model.getBounds(var) != null) {
                    lb = new Double(model.getBounds(var).getLb()).intValue();
                    ub = new Double(model.getBounds(var).getUb()).intValue();
                } else if (intVarLb != null && intVarUb != null) {
                    lb = intVarLb;
                    ub = intVarUb;
                } else throw new IllegalStateException("No bounds for variable " + var.getName()
                        + " found. Chocosolver does not support unbounded variables. " +
                        "Set specific bounds for variable " + var.getName() +
                        " or set default integer bounds in ChocoBuilder.");

                IntVar chocoVar = chocoModel.intVar(var.getName(), lb, ub);
                vars.put(var, chocoVar);
            } else if (IlpUtil.isDoubleVar(var)) {
                throw new IllegalStateException("Cannot add variable " + var.getName()
                        + ". Chocosolver does not supported double variables.");
            } else {
                throw new IllegalStateException("Model does contain not supported variable types.");
            }
        }

        // Create constraints
        for (org.cardygan.ilp.api.model.Constraint cstr : model.getConstraints()) {
            cstr.getExpr().accept(new BoolExprConverter(chocoModel)).post();
        }


        // Solve
        final long start;
        final long end;
        final Double objVal;
        final Solution solution;
        final org.chocosolver.solver.Solver chocoSolver = chocoModel.getSolver();

        if (timeout != null)
            chocoSolver.limitTime(timeout * 1000);

        // Solve with objective
        if (model.getObjective().isPresent()) {

            if (objLb == null || objUb == null) {
                throw new IllegalStateException("No bounds for objective function set." +
                        " Set bounds via ChocoBuilder.");
            }

            IntVar objVar = chocoModel.intVar(objLb, objUb);
            convertArithExpr(model.getObjective().get().getExpr(), chocoModel).eq(objVar).post();

            start = System.currentTimeMillis();
            solution = chocoSolver.findOptimalSolution(objVar, model.getObjective().get().isMax());
            end = System.currentTimeMillis();

            if (solution != null)
                objVal = (double) solution.getIntVal(objVar);
            else
                objVal = null;
        } else {
            //Solve without objective
            start = System.currentTimeMillis();
            solution = chocoSolver.findSolution();
            end = System.currentTimeMillis();

            objVal = null;
        }

        // Create result object
        final Map<Var, Double> solutions = new HashMap<>();

        if (solution != null) {
            for (Map.Entry<Var, IntVar> entry : vars.entrySet()) {
                int val = solution.getIntVal(entry.getValue());
                solutions.put(entry.getKey(), (double) val);
            }
        }

        Result.Statistics stats = new Result.Statistics(solution != null, false, end - start);
        return new Result(model, stats, solutions, objVal);
    }

    private class BoolExprConverter implements BoolExprVisitor<ReExpression> {

        private final org.chocosolver.solver.Model model;

        BoolExprConverter(org.chocosolver.solver.Model model) {
            this.model = model;
        }

        @Override
        public ReExpression visit(And expr) {
            return expr.getElements().stream()
                    .map(e -> e.accept(this))
                    .reduce(ReExpression::and)
                    .orElseThrow(() -> new IllegalStateException("Could not reduce expression."));
        }

        @Override
        public ReExpression visit(Or expr) {
            return expr.getElements().stream()
                    .map(e -> e.accept(this))
                    .reduce(ReExpression::or)
                    .orElseThrow(() -> new IllegalStateException("Could not reduce expression."));
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
            if (!(vars.get(expr) instanceof BoolVar))
                throw new IllegalStateException("Only boolean variables are allowed in propositional expressions.");
            return (BoolVar) vars.get(expr);
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

    private ArExpression convertArithExpr(ArithExpr arithExpr, org.chocosolver.solver.Model model) {
        ArithExprSimplifier simpleExpr = new ArithExprSimplifier(arithExpr);
        Optional<ArExpression> sum = simpleExpr.getSummands().stream().map(
                e -> {
                    int param = tryCastToInt(e.getFirst());
                    if (param == 1)
                        return vars.get(e.getSecond());
                    else
                        return vars.get(e.getSecond()).mul(param);
                }
        ).reduce(ArExpression::add);

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

    public static class ChocoSolverBuilder {

        private Long timeout = null;
        private Integer intVarLb = null;
        private Integer intVarUb = null;
        private Integer objLb = null;
        private Integer objUb = null;


        /**
         * Default bounds for integer variables if not otherwise defined.
         *
         * @param lb default lower bound for integer variable
         * @param ub default upper bound for integer variable
         * @return the builder
         */
        public ChocoSolverBuilder withDefaultIntVarBounds(int lb, int ub) {
            checkBounds(lb, ub);
            intVarLb = lb;
            intVarUb = ub;
            return this;
        }

        /**
         * Bounds for objective function.
         *
         * @param lb default lower bound for objective function
         * @param ub default upper bound for objective function
         * @return the builder
         */
        public ChocoSolverBuilder withObjectiveBounds(int lb, int ub) {
            checkBounds(lb, ub);
            objLb = lb;
            objUb = ub;
            return this;
        }

        private void checkBounds(int lb, int ub) {
            if (lb > ub) {
                throw new IllegalArgumentException("Illegal bounds.");
            }
        }

        /**
         * Sets timeout of solver in seconds.
         *
         * @param timeout solver timeout in seconds.
         * @return the builder
         */
        public ChocoSolverBuilder withTimeOut(long timeout) {
            this.timeout = timeout;
            return this;
        }


        /**
         * Build a solver object.
         *
         * @return the solver
         */
        public ChocoSolver build() {
            return new ChocoSolver(this);
        }
    }

}
