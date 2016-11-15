package org.cardygan.ilp;

public interface Solver {

	 Result solveProblem();

	 double getVal(IlpVar targetIlpVar);

}
