package org.cardygan.ilp;

import java.util.Objects;

public class IlpDoubleParam extends IlpParam {

	private final double val;

	public IlpDoubleParam(double val) {
		this.val = val;
	}

	public double getVal() {
		return val;
	}


	@Override
	public int hashCode() {
		return Objects.hash(val);
	}

	@Override
	public boolean equals(Object other) {
		if (other == null) {
			return false;
		}
		if (other == this) {
			return true;
		}
		if (!(other instanceof IlpDoubleParam)) {
			return false;
		}
		IlpDoubleParam otherMyClass = (IlpDoubleParam) other;
		if (getVal() == otherMyClass.getVal()) {
			return true;
		}
		return false;
	}

}
