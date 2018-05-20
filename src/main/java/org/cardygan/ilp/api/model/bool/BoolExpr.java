package org.cardygan.ilp.api.model.bool;

import org.cardygan.ilp.internal.expr.BoolExprVisitor;

/**
 * Created by markus on 16.02.17.
 */
public interface BoolExpr {

    <T> T accept(BoolExprVisitor<T> visitor);
}
