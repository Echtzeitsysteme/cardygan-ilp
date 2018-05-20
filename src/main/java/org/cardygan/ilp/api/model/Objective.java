package org.cardygan.ilp.api.model;


import org.cardygan.ilp.internal.expr.ExprPrettyPrinter;

public class Objective {

    private final boolean max;
    private final ArithExpr expr;

    public Objective(boolean max, ArithExpr expr) {
        this.max = max;
        this.expr = expr;
    }

    public ArithExpr getExpr() {
        return expr;
    }

    @Override
    public String toString() {
        return (max ? "max " : "min ") + expr.accept(new ExprPrettyPrinter());
    }

    public boolean isMax() {
        return max;
    }
}
