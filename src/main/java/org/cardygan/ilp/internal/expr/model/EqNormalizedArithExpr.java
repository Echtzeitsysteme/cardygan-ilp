package org.cardygan.ilp.internal.expr.model;

import org.cardygan.ilp.api.model.Param;
import org.cardygan.ilp.api.model.bool.RelOp;
import org.cardygan.ilp.internal.expr.Coefficient;

import java.util.List;
import java.util.Optional;

/**
 * Created by markus on 24.10.16.
 */
public class EqNormalizedArithExpr extends NormalizedArithExpr {


    EqNormalizedArithExpr(String name, RelOp relOp, Param rhs, List<Coefficient> coefficients) {
        super(name, relOp, rhs, coefficients);
    }

    EqNormalizedArithExpr(RelOp relOp, Param rhs, List<Coefficient> coefficients) {
        super(relOp, rhs, coefficients);
    }
}
