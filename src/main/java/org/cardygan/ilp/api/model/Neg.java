package org.cardygan.ilp.api.model;

import org.cardygan.ilp.internal.expr.ArithExprVisitor;

import java.util.Objects;

import static org.cardygan.ilp.internal.util.Util.assertNotNull;

public class Neg extends ArithExpr {

    private final ArithExpr neg;

    public Neg(ArithExpr neg) {
        assertNotNull(neg);
        this.neg = neg;
    }

    public ArithExpr getNeg() {
        return neg;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Neg neg1 = (Neg) o;
        return neg.equals(neg1.neg);
    }

    @Override
    public int hashCode() {
        return Objects.hash(neg);
    }

    @Override
    public <T> T accept(ArithExprVisitor<T> visitor) {
        return visitor.visit(this);
    }


}
