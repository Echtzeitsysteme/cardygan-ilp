package org.cardygan.ilp.api;

import org.cardygan.ilp.api.expr.Var;

import java.util.Objects;

public class IntVar extends Var {

	IntVar(String name) {
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
		if (!(other instanceof IntVar)) {
			return false;
		}
		IntVar otherMyClass = (IntVar) other;
		if (getName().equals(otherMyClass.getName())) {
			return true;
		}
		return false;
	}


	@Override
	public String toString() {
		return getName();
	}
}
