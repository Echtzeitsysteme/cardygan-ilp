package org.cardygan.ilp.internal.expr;

import org.cardygan.ilp.api.model.Var;
import org.cardygan.ilp.api.model.Param;

import java.util.Objects;

/**
 * Created by markus on 24.10.16.
 */
public class Coefficient {

    private final Param param;
    private final Var var;

    public Coefficient(Param param, Var var) {
        this.param = param;
        this.var = var;
    }

    public Param getParam() {
        return param;
    }

    public Var getVar() {
        return var;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Coefficient that = (Coefficient) o;
        return Objects.equals(param, that.getParam()) &&
                Objects.equals(var, that.getVar());
    }

    @Override
    public int hashCode() {
        return Objects.hash(param, var);
    }

    @Override
    public String toString() {
        return "("+ param.getVal()+","+ var.getName()+")";
    }
}
