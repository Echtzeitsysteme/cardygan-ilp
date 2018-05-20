package org.cardygan.ilp.internal.expr.model;

import org.cardygan.ilp.internal.expr.Coefficient;

import java.util.List;

public class BasicObjective {

    private final boolean max;
    private final List<Coefficient> coefficients;
    private final Double constant;


    public BasicObjective(boolean max, Double constant, List<Coefficient> coefficients) {
        this.max = max;
        this.constant = constant;
        this.coefficients = coefficients;
    }

    public boolean isMax() {
        return max;
    }

    public List<Coefficient> getCoefficients() {
        return coefficients;
    }

    public Double getConstant() {
        return constant;
    }

}
