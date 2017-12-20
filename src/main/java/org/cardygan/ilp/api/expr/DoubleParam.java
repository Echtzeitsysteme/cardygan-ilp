package org.cardygan.ilp.api.expr;

import org.cardygan.ilp.internal.expr.ArithExprVisitor;

import java.util.Objects;

public class DoubleParam extends Param {

    private final double val;

    public DoubleParam(double val) {
        this.val = val;
    }

    public double getVal() {
        return val;
    }


    @Override
    public int hashCode() {
        return Objects.hash(val);
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (other == this) {
            return true;
        }
        if (!(other instanceof DoubleParam)) {
            return false;
        }
        DoubleParam otherMyClass = (DoubleParam) other;
        if (getVal() == otherMyClass.getVal()) {
            return true;
        }
        return false;
    }



    @Override
    public <T> T accept(ArithExprVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
