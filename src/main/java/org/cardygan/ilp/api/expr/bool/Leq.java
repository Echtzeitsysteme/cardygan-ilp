package org.cardygan.ilp.api.expr.bool;


import org.cardygan.ilp.api.expr.ArithExpr;

/**
 * Created by markus on 18.02.17.
 */
public class Leq extends RelOp {

    public Leq(ArithExpr lhs, ArithExpr rhs) {
        super(lhs, rhs);
    }


}
