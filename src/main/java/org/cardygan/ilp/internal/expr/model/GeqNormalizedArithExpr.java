package org.cardygan.ilp.internal.expr.model;

import org.cardygan.ilp.api.model.Param;
import org.cardygan.ilp.api.model.bool.RelOp;
import org.cardygan.ilp.internal.expr.Coefficient;

import java.util.List;
import java.util.Optional;

/**
 * Created by markus on 24.10.16.
 */
public class GeqNormalizedArithExpr extends NormalizedArithExpr {


    GeqNormalizedArithExpr(String name, RelOp relOp, Param rhs, List<Coefficient> coefficients) {
        super(name, relOp, rhs, coefficients);
    }

    GeqNormalizedArithExpr(RelOp relOp, Param rhs, List<Coefficient> coefficients) {
        super(relOp, rhs, coefficients);
    }
}
