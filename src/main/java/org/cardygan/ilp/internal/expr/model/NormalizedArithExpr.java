package org.cardygan.ilp.internal.expr.model;

import org.cardygan.ilp.api.model.Param;
import org.cardygan.ilp.api.model.bool.RelOp;
import org.cardygan.ilp.internal.expr.Coefficient;

import java.util.List;
import java.util.Optional;

public abstract class NormalizedArithExpr {

    private final Param rhs;
    private final List<Coefficient> coefficients;
    private final String name;

    protected NormalizedArithExpr(String name, RelOp relOp, Param rhs, List<Coefficient> coefficients) {
        this.rhs = rhs;
        this.coefficients = coefficients;
        this.name = name;
    }

    protected NormalizedArithExpr(RelOp relOp, Param rhs, List<Coefficient> coefficients) {
        this(null, relOp, rhs, coefficients);
    }

    public Optional<String> getName() {
        return Optional.ofNullable(name);
    }

    public Param getRhs() {
        return rhs;
    }

    public List<Coefficient> getCoefficients() {
        return coefficients;
    }

}
