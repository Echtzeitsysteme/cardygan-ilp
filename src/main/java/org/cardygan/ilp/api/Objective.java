package org.cardygan.ilp.api;


import org.cardygan.ilp.api.expr.ArithExpr;
import org.cardygan.ilp.internal.expr.ArithExprSimplifier;
import org.cardygan.ilp.internal.Coefficient;
import org.cardygan.ilp.internal.util.Util;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.cardygan.ilp.internal.util.Util.coef;
import static org.cardygan.ilp.api.util.ExprDsl.p;

public class Objective {

    private final boolean max;
    private Optional<List<Coefficient>> coefficients;
    private Optional<Double> constant;


    public Objective(boolean max) {
        this.max = max;
    }


    public double getConstant() {
        if (!constant.isPresent()) {
            throw new IllegalStateException("Objective term is not set.");
        }
        return constant.get();
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

    public void setTerm(ArithExpr term) {
        ArithExprSimplifier simplifier = new ArithExprSimplifier(term);
        coefficients = Optional.of(simplifier.getSummands().stream().map(p -> Util.coef(p(p.getFirst()), p.getSecond())).collect(Collectors.toList()));
        constant = Optional.of(simplifier.getConstant());
    }
}
