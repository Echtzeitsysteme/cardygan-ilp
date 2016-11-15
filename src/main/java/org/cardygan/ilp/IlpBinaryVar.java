package org.cardygan.ilp;

import java.util.Objects;

public class IlpBinaryVar extends IlpVar {

	public IlpBinaryVar(String name) {
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
		if (!(other instanceof IlpBinaryVar)) {
			return false;
		}
		IlpBinaryVar otherMyClass = (IlpBinaryVar) other;
		if (getName().equals(otherMyClass.getName())) {
			return true;
		}
		return false;
	}

}
