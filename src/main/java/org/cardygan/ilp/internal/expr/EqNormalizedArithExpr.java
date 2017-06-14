package org.cardygan.ilp.internal.expr;

import org.cardygan.ilp.api.expr.Param;
import org.cardygan.ilp.api.expr.bool.RelOp;
import org.cardygan.ilp.internal.Coefficient;

import java.util.List;

/**
 * Created by markus on 24.10.16.
 */
public class EqNormalizedArithExpr extends NormalizedArithExpr {


    public EqNormalizedArithExpr(String name, RelOp relOp, Param rhs, List<Coefficient> coefficients) {
        super(name, relOp,rhs,coefficients);
    }
}
