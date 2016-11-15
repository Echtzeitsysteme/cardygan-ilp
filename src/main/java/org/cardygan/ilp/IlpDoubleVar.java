package org.cardygan.ilp;

import java.util.Objects;

public class IlpDoubleVar extends IlpVar {

	public IlpDoubleVar(String name) {
		super(name);
	}

//	@Override
//	public <T> T accept(ArithExprVisitor<T> visitor) {
//		return visitor.visitDoubleVar(this);
//	}

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
		if (!(other instanceof IlpDoubleVar)) {
			return false;
		}
		IlpDoubleVar otherMyClass = (IlpDoubleVar) other;
		if (getName().equals(otherMyClass.getName())) {
			return true;
		}
		return false;
	}

}
