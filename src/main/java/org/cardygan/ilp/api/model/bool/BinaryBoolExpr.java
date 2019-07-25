package org.cardygan.ilp.api.model.bool;

import java.util.Objects;

import static org.cardygan.ilp.internal.util.Util.assertNotNull;

public abstract class BinaryBoolExpr implements BoolExpr {

	private final BoolExpr lhs;
	private final BoolExpr rhs;

	public BinaryBoolExpr(BoolExpr lhs, BoolExpr rhs) {
		assertNotNull(lhs,rhs);

		this.lhs = lhs;
		this.rhs = rhs;
	}

	public BoolExpr getLhs() {
		return lhs;
	}

	public BoolExpr getRhs() {
		return rhs;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		BinaryBoolExpr that = (BinaryBoolExpr) o;
		return lhs.equals(that.lhs) &&
				rhs.equals(that.rhs);
	}

	@Override
	public int hashCode() {
		return Objects.hash(lhs, rhs);
	}
}
