package org.cardygan.ilp.api.model.bool;


import org.cardygan.ilp.api.model.ArithExpr;

/**
 * Created by markus on 18.02.17.
 */
public class Eq extends RelOp {

    public Eq(ArithExpr lhs, ArithExpr rhs) {
        super(lhs, rhs);
    }
}
