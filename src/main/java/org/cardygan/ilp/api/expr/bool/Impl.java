package org.cardygan.ilp.api.expr.bool;

import org.cardygan.ilp.internal.expr.BoolExprVisitor;

public class Impl extends BinaryBoolExpr {

	public Impl(BoolExpr lhs, BoolExpr rhs) {
		super(lhs, rhs);
	}



	@Override
	public <T> T accept(BoolExprVisitor<T> visitor) {
		return visitor.visit(this);
	}

}
