package org.cardygan.ilp.internal.expr;


import org.cardygan.ilp.api.model.*;
import org.cardygan.ilp.internal.util.Pair;
import org.cardygan.ilp.internal.util.Util;

import java.util.*;

public class ArithExprSimplifier implements ArithExprVisitor<Void> {

    private final boolean inNeg;
    private final Optional<Pair<Double, Var>> mult;
    private final SimplifierContext ctx;
    private final ArithExpr arithExpr;

    public ArithExprSimplifier(ArithExpr arithExpr) {
        this(arithExpr, false, Optional.empty(), new SimplifierContext());

        simplify();
    }

    private ArithExprSimplifier(ArithExpr arithExpr, boolean inNeg, Optional<Pair<Double, Var>> mult, SimplifierContext ctx) {
        this.inNeg = inNeg;
        this.ctx = ctx;
        this.mult = mult;
        this.arithExpr = arithExpr;
    }

    private void simplify() {
        arithExpr.accept(this);
    }

    public double getConstant() {
        return ctx.constants.stream().mapToDouble(e -> e).sum();
    }

    public Collection<Pair<Double, Var>> getSummands() {
        return ctx.summands.values();
    }

    @Override
    public Void visit(Sum expr) {
        expr.getSummands().forEach(s -> s.accept(this));

        return null;
    }

    @Override
    public Void visit(Mult expr) {
        if (mult.isPresent()) {
            throw new IllegalStateException("Nested multiplication are not supported.");
        }

        Pair<Double, Var> pair = new Pair<>();
        expr.getLeft().accept(new ArithExprSimplifier(expr, inNeg, Optional.of(pair), ctx));
        expr.getRight().accept(new ArithExprSimplifier(expr, inNeg, Optional.of(pair), ctx));

        return null;
    }


    @Override
    public Void visit(Param param) {
        double val = param.getVal();
        if (mult.isPresent()) {
            if (!Util.isNull(mult.get().getFirst()) && !Util.isNull(mult.get().getSecond())) {
                throw new IllegalStateException("Only two unary expressions can be multiplied.");
            }

            if (Util.isNull(mult.get().getFirst())) {
                if (val >= 0 && inNeg || val < 0 && inNeg) {
                    mult.get().setFirst(new Double(-val));
                } else {
                    mult.get().setFirst(val);
                }
            } else {
                ctx.constants.add(mult.get().getFirst() * val);
            }

            if (!Util.isNull(mult.get().getFirst()) && !Util.isNull(mult.get().getSecond())) {
                ctx.addSummand(mult.get());
            }
        } else {
            if (val >= 0 && inNeg || val < 0 && inNeg) {
                ctx.constants.add(-val);
            } else {
                ctx.constants.add(val);
            }
        }
        return null;
    }

    @Override
    public Void visit(Neg expr) {
        expr.getNeg().accept(new ArithExprSimplifier(expr, !inNeg, mult, ctx));
        return null;
    }


    @Override
    public Void visit(Var var) {
        if (mult.isPresent()) {
            if (!Util.isNull(mult.get().getSecond())) {
                throw new IllegalStateException("Multiplication of vars not supported.");
            }
            mult.get().setSecond(var);

            if (!Util.isNull(mult.get().getFirst()) && !Util.isNull(mult.get().getSecond())) {
                ctx.addSummand(mult.get());
            }
        } else {
            if (inNeg) {
                ctx.addSummand(new Pair<Double, Var>(-1d, var));
            } else {
                ctx.addSummand(new Pair<Double, Var>(1d, var));
            }
        }
        return null;
    }

    private static class SimplifierContext {
        private final List<Double> constants = new ArrayList<>();

        private Map<Var, Pair<Double, Var>> summands = new HashMap<>();

        public void addSummand(Pair<Double, Var> element) {
            double newVal = element.getFirst();
            if (summands.containsKey(element.getSecond())) {
                newVal += summands.get(element.getSecond()).getFirst();
                element.setFirst(newVal);

                if (newVal == 0) {
                    summands.remove(element.getSecond());
                } else {
                    summands.put(element.getSecond(), element);
                }
            } else {
                if (newVal != 0) {
                    summands.put(element.getSecond(), element);
                }
            }

        }

    }

}