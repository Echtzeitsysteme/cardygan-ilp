package org.cardygan.ilp;

import java.util.List;

public class Objective {

	private final List<Coefficient> coefficients;

	private final boolean max;

	private final double constant;



	public Objective(List<Coefficient> coefficients, boolean max) {
		this(0,coefficients,max);
	}

	public Objective(double constant, List<Coefficient> coefficients, boolean max){
		this.coefficients = coefficients;
		this.max = max;
		this.constant = constant;
	}

	public double getConstant() {
		return constant;
	}

	public List<Coefficient> getCoefficients() {
		return coefficients;
	}

	public boolean isMax() {
		return max;
	}

}
