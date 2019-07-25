package org.cardygan.ilp.api.model;

import org.cardygan.ilp.internal.expr.ArithExprVisitor;

import java.util.Objects;

import static org.cardygan.ilp.internal.util.Util.assertNotNull;

public class Mult extends ArithExpr {

    private final ArithUnaryExpr lhs;

    private final ArithUnaryExpr rhs;

    public Mult(ArithUnaryExpr lhs, ArithUnaryExpr rhs) {
        assertNotNull(lhs, rhs);
        this.lhs = lhs;
        this.rhs = rhs;
    }

    public ArithUnaryExpr getLhs() {
        return lhs;
    }

    public ArithUnaryExpr getRhs() {
        return rhs;
    }

    @Override
    public <T> T accept(ArithExprVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Mult mult = (Mult) o;
        return lhs.equals(mult.lhs) &&
                rhs.equals(mult.rhs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lhs, rhs);
    }
}
