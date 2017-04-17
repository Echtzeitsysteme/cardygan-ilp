package org.cardygan.ilp.api.expr;

import org.cardygan.ilp.internal.expr.ArithExprVisitor;

public abstract class ArithExpr {

	public abstract <T> T accept(ArithExprVisitor<T> visitor);

}
