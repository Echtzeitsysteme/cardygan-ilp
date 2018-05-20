package org.cardygan.ilp.api.model;

import org.cardygan.ilp.internal.expr.ArithExprVisitor;

import java.util.Arrays;
import java.util.List;

public class Sum extends ArithExpr {

    private final List<ArithExpr> summands;

    public Sum(ArithExpr... summands) {
        this.summands = Arrays.asList(summands);
    }

    public Sum(List<ArithExpr> summands) {
        this.summands = summands;
    }

    public List<ArithExpr> getSummands() {
        return summands;
    }

    @Override
    public <T> T accept(ArithExprVisitor<T> visitor) {
        return visitor.visit(this);
    }




}