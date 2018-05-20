package org.cardygan.ilp.api.model;

import org.cardygan.ilp.api.model.bool.BoolExpr;
import org.cardygan.ilp.internal.expr.ExprPrettyPrinter;

import java.util.Optional;

/**
 * Created by markus on 18.02.17.
 */
public class Constraint {

    private final String name;
    private final BoolExpr expr;

    public Constraint(BoolExpr expr) {
        this.name = null;
        this.expr = expr;
    }

    public Constraint(String name, BoolExpr expr) {
        this.name = name;
        this.expr = expr;
    }

    public BoolExpr getExpr() {
        return expr;
    }

    public Optional<String> getName() {
        return Optional.ofNullable(name);
    }

    @Override
    public String toString() {
        return name + ": " + expr.accept(new ExprPrettyPrinter());
    }
}
