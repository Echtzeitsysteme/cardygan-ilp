package org.cardygan.ilp.internal.expr;

import org.cardygan.ilp.api.expr.Param;
import org.cardygan.ilp.api.expr.bool.RelOp;
import org.cardygan.ilp.internal.Coefficient;

import java.util.List;

public abstract class NormalizedArithExpr {

    protected final Param rhs;
    protected final List<Coefficient> coefficients;
    protected final String name;

    protected NormalizedArithExpr(String name, RelOp relOp, Param rhs, List<Coefficient> coefficients) {
        this.name = name;
        this.rhs = rhs;
        this.coefficients = coefficients;
    }

    public String getName() {
        return name;
    }

    public Param getRhs() {
        return rhs;
    }

    public List<Coefficient> getCoefficients() {
        return coefficients;
    }

}
