package org.cardygan.ilp.api.model;

import org.cardygan.ilp.internal.expr.ArithExprVisitor;
import org.cardygan.ilp.internal.util.Util;

import java.util.Objects;

/**
 * Created by markus on 27.10.16.
 */
public abstract class Var extends ArithUnaryExpr {

    private final String name;

    Var(String name) {
        Util.assertNotNull(name);
        this.name = name;
    }

    @Override
    public <T> T accept(ArithExprVisitor<T> visitor) {
        return visitor.visit(this);
    }

    public String getName() {
        return name;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Var var = (Var) o;
        return name.equals(var.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
