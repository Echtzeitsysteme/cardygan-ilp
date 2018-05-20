package org.cardygan.ilp.api.model;

//import static org.cardygan.cfm.util.Util.checkBinaryMultArg;

import org.cardygan.ilp.internal.expr.ArithExprVisitor;

public class Mult extends ArithExpr {

    private final ArithUnaryExpr left;

    private final ArithUnaryExpr right;

    public Mult(ArithUnaryExpr left, ArithUnaryExpr right) {
        this.left = left;
        this.right = right;
    }

    public ArithUnaryExpr getLeft() {
        return left;
    }

    public ArithUnaryExpr getRight() {
        return right;
    }


    @Override
    public <T> T accept(ArithExprVisitor<T> visitor) {
        return visitor.visit(this);
    }

}
