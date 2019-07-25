package org.cardygan.ilp.api.model;

import org.cardygan.ilp.api.model.bool.BoolLiteral;
import org.cardygan.ilp.internal.expr.BoolExprVisitor;

public class BinaryVar extends Var implements BoolLiteral {

    public BinaryVar(String name) {
        super(name);
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public <T> T accept(BoolExprVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
