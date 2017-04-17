package org.cardygan.ilp.api.expr;

import org.cardygan.ilp.internal.expr.ArithExprVisitor;

public class Neg extends ArithExpr {

    private final ArithExpr neg;

    public Neg(ArithExpr neg) {
        super();
        this.neg = neg;
    }

    public ArithExpr getNeg() {
        return neg;
    }



    @Override
    public <T> T accept(ArithExprVisitor<T> visitor) {
        return visitor.visit(this);
    }

}
