package org.cardygan.ilp.api;

import org.cardygan.ilp.api.expr.Var;
import org.cardygan.ilp.internal.expr.BoolExprVisitor;
import org.cardygan.ilp.api.expr.bool.BoolLiteral;

import java.util.Objects;

public class BinaryVar extends Var implements BoolLiteral {

	BinaryVar(String name) {
		super(name);
	}

	@Override
	public int hashCode() {
		return Objects.hash(getName());
	}

	@Override
	public boolean equals(Object other) {
		if (other == null) {
			return false;
		}
		if (other == this) {
			return true;
		}
		if (!(other instanceof BinaryVar)) {
			return false;
		}
		BinaryVar otherMyClass = (BinaryVar) other;
		if (getName().equals(otherMyClass.getName())) {
			return true;
		}
		return false;
	}

	@Override
	public <T> T accept(BoolExprVisitor<T> visitor) {
		return visitor.visit(this);
	}
}
