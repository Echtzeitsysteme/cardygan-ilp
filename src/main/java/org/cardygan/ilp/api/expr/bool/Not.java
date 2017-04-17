package org.cardygan.ilp.api.expr.bool;

import org.cardygan.ilp.internal.expr.BoolExprVisitor;

public class Not implements BoolExpr {

    private final BoolExpr val;

    public Not(BoolExpr val) {
        this.val = val;
    }

    public BoolExpr getVal() {
        return val;
    }



    @Override
    public <T> T accept(BoolExprVisitor<T> visitor) {
        return visitor.visit(this);
    }

}
