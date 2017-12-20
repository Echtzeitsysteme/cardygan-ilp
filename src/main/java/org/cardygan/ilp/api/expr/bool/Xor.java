package org.cardygan.ilp.api.expr.bool;

import org.cardygan.ilp.internal.expr.BoolExprVisitor;

public class Xor extends BinaryBoolExpr {

    public Xor(BoolExpr lhs, BoolExpr rhs) {
        super(lhs, rhs);
    }


    @Override
    public <T> T accept(BoolExprVisitor<T> visitor) {
        return visitor.visit(this);
    }



}
