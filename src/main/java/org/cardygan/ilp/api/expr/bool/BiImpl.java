package org.cardygan.ilp.api.expr.bool;

import org.cardygan.ilp.internal.expr.BoolExprVisitor;

public class BiImpl extends BinaryBoolExpr {

    public BiImpl(BoolExpr lhs, BoolExpr rhs) {
        super(lhs, rhs);
    }


    @Override
    public <T> T accept(BoolExprVisitor<T> visitor) {
        return visitor.visit(this);
    }

}
