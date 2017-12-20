package org.cardygan.ilp.api;

import org.cardygan.ilp.api.expr.bool.BoolExpr;
import org.cardygan.ilp.api.util.ExprPrettyPrinter;

/**
 * Created by markus on 18.02.17.
 */
public class Constraint {

    private final String name;
    private BoolExpr expr;


    public Constraint(String name) {
        this.name = name;
    }

    public BoolExpr getExpr() {
        return expr;
    }

    public void setExpr(BoolExpr expr) {
        this.expr = expr;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name + ": " + expr.accept(new ExprPrettyPrinter());
    }
}
