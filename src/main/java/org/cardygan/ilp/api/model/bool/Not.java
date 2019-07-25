package org.cardygan.ilp.api.model.bool;

import org.cardygan.ilp.internal.expr.BoolExprVisitor;

import java.util.Objects;

import static org.cardygan.ilp.internal.util.Util.assertNotNull;

public class Not implements BoolExpr {

    private final BoolExpr val;

    public Not(BoolExpr val) {
        assertNotNull(val);

        this.val = val;
    }

    public BoolExpr getVal() {
        return val;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Not not = (Not) o;
        return val.equals(not.val);
    }

    @Override
    public int hashCode() {
        return Objects.hash(val);
    }

    @Override
    public <T> T accept(BoolExprVisitor<T> visitor) {
        return visitor.visit(this);
    }


}
