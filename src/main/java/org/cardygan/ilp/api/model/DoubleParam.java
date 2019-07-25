package org.cardygan.ilp.api.model;

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
    public String toString() {
        return Double.toString(val);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DoubleParam that = (DoubleParam) o;
        return Double.compare(that.val, val) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(val);
    }

    @Override
    public <T> T accept(ArithExprVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
