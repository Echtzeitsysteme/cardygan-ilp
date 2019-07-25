package org.cardygan.ilp.api.model.bool;

import org.cardygan.ilp.api.model.ArithExpr;
import org.cardygan.ilp.internal.expr.BoolExprVisitor;

import java.util.Objects;

import static org.cardygan.ilp.internal.util.Util.assertNotNull;

/**
 * Created by markus on 18.02.17.
 */
public abstract class RelOp implements BoolLiteral {

    private final ArithExpr lhs;
    private final ArithExpr rhs;

    protected RelOp(ArithExpr lhs, ArithExpr rhs) {
        assertNotNull(lhs, rhs);

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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RelOp relOp = (RelOp) o;
        return lhs.equals(relOp.lhs) &&
                rhs.equals(relOp.rhs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lhs, rhs);
    }

    @Override
    public <T> T accept(BoolExprVisitor<T> visitor) {
        return visitor.visit(this);
    }

}
