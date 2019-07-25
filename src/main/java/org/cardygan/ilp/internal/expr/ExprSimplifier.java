package org.cardygan.ilp.internal.expr;


import org.cardygan.ilp.api.model.*;
import org.cardygan.ilp.api.model.bool.*;
import org.cardygan.ilp.internal.solver.milp.LinearConstr;
import org.cardygan.ilp.internal.util.Util;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static org.cardygan.ilp.internal.util.Util.listOf;

public class ExprSimplifier {

    public static LinearConstr normalizeSos(final Map<Var, Double> elements) {
        Util.assertNotNull(elements);

        final Var[] vars = elements.keySet().toArray(new Var[0]);
        final double[] params = elements.values().stream().mapToDouble(it -> it).toArray();

        Util.assertTrue(vars.length > 0);

        return new LinearConstr(vars, params, 0, LinearConstr.Type.SOS);
    }

    public static Optional<LinearConstr> normalizeCstr(final RelOp expr) {
        Util.assertNotNull(expr);

        final ExprSimplifier.SimplifiedRelOp simplExpr = ExprSimplifier.simplify(expr);

        final Var[] vars = simplExpr.getVars();

        final double[] params = simplExpr.getParams();

        final double rhs = simplExpr.getRhs();

        // if variable array is empty check if condition always holds
        //TODO do something meaningful if condition never holds
        if (vars.length == 0) {
            switch (simplExpr.type) {
                case LEQ:
                    if (0 <= rhs)
                        return Optional.empty();
                    break;
                case GEQ:
                    if (0 >= rhs)
                        return Optional.empty();
                    break;
                case EQ:
                    if (0 == rhs)
                        return Optional.empty();
                    break;
                case SOS:
                    throw new IllegalStateException("Trying to normalize SOS constraint without variable. This should not happen.");
            }
        }

        return Optional.of(new LinearConstr(vars, params, rhs, simplExpr.type));
    }

    public static List<BoolExpr> toNAryAnd(And expr) {
        return expr.accept(new ToNaryVisitor(And.class));
    }

    public static List<BoolExpr> toNAryOr(Or expr) {
        return expr.accept(new ToNaryVisitor(Or.class));
    }

    private static class ToNaryVisitor implements BoolExprVisitor<List<BoolExpr>> {

        private final Class<? extends BoolExpr> type;
        private final AtomicBoolean isType = new AtomicBoolean(false);

        ToNaryVisitor(Class<? extends BoolExpr> type) {
            this.type = type;
        }

        @Override
        public List<BoolExpr> visit(And expr) {
            if (isType(expr)) {
                return listOf(
                        expr.getLhs().accept(this),
                        expr.getRhs().accept(this));
            }
            return listOf(expr);
        }


        private boolean isType(BoolExpr expr) {
            return type.isInstance(expr);
        }

        @Override
        public List<BoolExpr> visit(Or expr) {
            if (isType(expr)) {
                return listOf(
                        expr.getLhs().accept(this),
                        expr.getRhs().accept(this));
            }
            return listOf(expr);
        }

        @Override
        public List<BoolExpr> visit(Xor expr) {
            return listOf(expr);
        }

        @Override
        public List<BoolExpr> visit(Not expr) {
            return listOf(expr);
        }

        @Override
        public List<BoolExpr> visit(Impl expr) {
            return listOf(expr);
        }

        @Override
        public List<BoolExpr> visit(BiImpl expr) {
            return listOf(expr);
        }

        @Override
        public List<BoolExpr> visit(BinaryVar expr) {
            return listOf(expr);
        }

        @Override
        public List<BoolExpr> visit(RelOp expr) {
            return listOf(expr);
        }
    }


    public static List<RelOp> collectConjunctiveRelops(And expr) {
        List<BoolExpr> ret = toNAryAnd(expr);
        if (ret.stream().anyMatch(it -> !(it instanceof RelOp))) {
            return Collections.emptyList();
        } else {
            return ret.stream()
                    .map(it -> (RelOp) it).collect(Collectors.toList());
        }

    }

    public static SimplifiedRelOp simplify(final RelOp expr) {
        final ExprSimplifier.SimplifiedArithExpr lhs = ExprSimplifier.simplify(expr.getLhs());
        final ExprSimplifier.SimplifiedArithExpr rhs = ExprSimplifier.simplify(expr.getRhs());
        final Map<Var, Double> mutableLhs = new HashMap<>(lhs.getCoeffs().size() + rhs.getCoeffs().size() + 2);

        mutableLhs.putAll(lhs.coeffs);

        rhs.getCoeffs().forEach(
                (key, value) -> {
                    if (mutableLhs.containsKey(key)) {
                        final double oldVal = mutableLhs.get(key);
                        mutableLhs.put(key, oldVal - value);
                    } else {
                        mutableLhs.put(key, -value);
                    }
                }
        );
        final double newRhs = rhs.getConstant() - lhs.getConstant();


        final double[] params = new double[mutableLhs.size()];
        final Var[] vars = new Var[mutableLhs.size()];

        AtomicInteger i = new AtomicInteger(0);
        mutableLhs.forEach(
                (var, param) -> {
                    params[i.get()] = param;
                    vars[i.get()] = var;
                    i.incrementAndGet();
                }
        );

        final LinearConstr.Type cstrType;
        if (expr instanceof Leq)
            cstrType = LinearConstr.Type.LEQ;
        else if (expr instanceof Geq)
            cstrType = LinearConstr.Type.GEQ;
        else if (expr instanceof Eq)
            cstrType = LinearConstr.Type.EQ;
        else throw new IllegalStateException("Unknown constraint type.");

        return new SimplifiedRelOp(params, vars, newRhs, cstrType);
    }

    public static class SimplifiedRelOp {

        private final double[] params;
        private final Var[] vars;
        private final double rhs;
        private final LinearConstr.Type type;

        SimplifiedRelOp(double[] params, Var[] vars, double rhs, LinearConstr.Type type) {
            Util.assertNotNull(params, vars, type);
            Util.assertTrue(type != LinearConstr.Type.SOS);

            this.params = params;
            this.vars = vars;
            this.rhs = rhs;
            this.type = type;
        }

        public double[] getParams() {
            return params;
        }

        public Var[] getVars() {
            return vars;
        }

        public double getRhs() {
            return rhs;
        }

        public LinearConstr.Type getType() {
            return type;
        }
    }

    public static SimplifiedArithExpr simplify(ArithExpr expr) {
        final Map<Var, Double> coeffs = new HashMap<>();
        final AtomicReference<Double> constant = new AtomicReference<>(0d);
        final AtomicBoolean inNeg = new AtomicBoolean();

        expr.accept(new ArithExprVisitor<Void>() {
            @Override
            public Void visit(Sum expr) {
                expr.getLhs().accept(this);
                expr.getRhs().accept(this);

                return null;
            }

            @Override
            public Void visit(Neg expr) {
                inNeg.set(!inNeg.get());
                expr.getNeg().accept(this);
                inNeg.set(!inNeg.get());
                return null;
            }

            @Override
            public Void visit(Mult expr) {
                final ArithUnaryExpr lhs = expr.getLhs();
                final ArithUnaryExpr rhs = expr.getRhs();

                if (lhs instanceof Param && rhs instanceof Param) {
                    constant.updateAndGet(oldVal -> {
                                final double newVal = ((Param) lhs).getVal() * ((Param) rhs).getVal();
                                return oldVal + ((inNeg.get()) ? -newVal : newVal);
                            }
                    );
                } else if (lhs instanceof Param && rhs instanceof Var) {
                    final double newVal = (inNeg.get()) ? -((Param) lhs).getVal() : ((Param) lhs).getVal();
                    updateCoeff((Var) rhs, newVal);
                } else if (lhs instanceof Var && rhs instanceof Param) {
                    final double newVal = (inNeg.get()) ? -((Param) rhs).getVal() : ((Param) rhs).getVal();
                    updateCoeff((Var) lhs, newVal);
                } else
                    throw new IllegalStateException("Cannot multiply " + lhs.getClass().getSimpleName()
                            + " with " + rhs.getClass().getSimpleName());

                return null;
            }

            private void updateCoeff(Var var, double newVal) {
                if (!coeffs.containsKey(var) && newVal != 0) {
                    coeffs.put(var, newVal);
                } else {
                    double oldVal = coeffs.get(var);

                    if (oldVal + newVal != 0)
                        coeffs.put(var, oldVal + newVal);
                    else
                        // remove entry if addition of new value equals to 0
                        coeffs.remove(var);
                }
            }

            @Override
            public Void visit(Param expr) {
                constant.updateAndGet(oldVal -> {
                            final double newVal = expr.getVal();
                            return oldVal + ((inNeg.get()) ? -newVal : newVal);
                        }
                );

                return null;
            }


            @Override
            public Void visit(Var var) {
                final double newVal = (inNeg.get()) ? -1 : 1;
                updateCoeff(var, newVal);

                return null;
            }
        });

        return new SimplifiedArithExpr(coeffs, constant.get());
    }

    public static class SimplifiedArithExpr {

        private final Map<Var, Double> coeffs;
        private final double constant;

        SimplifiedArithExpr(Map<Var, Double> coeffs, double constant) {
            this.coeffs = coeffs;
            this.constant = constant;
        }

        public Map<Var, Double> getCoeffs() {
            return Collections.unmodifiableMap(coeffs);
        }

        public double getConstant() {
            return constant;
        }
    }

}
