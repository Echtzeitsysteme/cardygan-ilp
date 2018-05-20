package org.cardygan.ilp.api.model.bool;

import org.cardygan.ilp.api.model.ArithExpr;
import org.cardygan.ilp.internal.expr.BoolExprVisitor;

/**
 * Created by markus on 18.02.17.
 */
public abstract class RelOp implements BoolLiteral {

    private final ArithExpr lhs;
    private final ArithExpr rhs;
    
    protected RelOp(ArithExpr lhs, ArithExpr rhs) {
        this.lhs = lhs;
        this.rhs = rhs;
    }

    public ArithExpr getLhs() {
        return lhs;
    }

    public ArithExpr getRhs() {
        return rhs;
    }

    @Override
    public <T> T accept(BoolExprVisitor<T> visitor) {
        return visitor.visit(this);
    }

}
