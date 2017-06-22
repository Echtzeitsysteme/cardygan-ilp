package org.cardygan.ilp.api;


import org.cardygan.ilp.api.expr.ArithExpr;
import org.cardygan.ilp.internal.Coefficient;
import org.cardygan.ilp.internal.expr.ArithExprSimplifier;
import org.cardygan.ilp.internal.util.Util;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.cardygan.ilp.api.util.ExprDsl.p;

public class Objective {

    private final boolean max;
    private Optional<List<Coefficient>> coefficients;
    private Optional<Double> constant;
    private ArithExpr expr;


    public Objective(boolean max) {
        this.max = max;
    }


    public double getConstant() {
        if (!constant.isPresent()) {
            throw new IllegalStateException("Objective term is not set.");
        }
        return constant.get();
    }

    public ArithExpr getExpr() {
        return expr;
    }

    public void setExpr(ArithExpr expr) {
        this.expr = expr;
        ArithExprSimplifier simplifier = new ArithExprSimplifier(expr);
        coefficients = Optional.of(simplifier.getSummands().stream().map(p -> Util.coef(p(p.getFirst()), p.getSecond())).collect(Collectors.toList()));
        constant = Optional.of(simplifier.getConstant());
    }

    public List<Coefficient> getCoefficients() {
        if (!coefficients.isPresent()) {
            throw new IllegalStateException("Objective term is not set.");
        }

        return coefficients.get();
    }

    public boolean isMax() {
        return max;
    }
}
