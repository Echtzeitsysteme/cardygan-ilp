package org.cardygan.ilp.api.model.bool;

public abstract class BinaryBoolExpr implements BoolExpr {

	private final BoolExpr lhs;
	private final BoolExpr rhs;

	public BinaryBoolExpr(BoolExpr lhs, BoolExpr rhs) {
		this.lhs = lhs;
		this.rhs = rhs;
	}

	public BoolExpr getLhs() {
		return lhs;
	}

	public BoolExpr getRhs() {
		return rhs;
	}

}
