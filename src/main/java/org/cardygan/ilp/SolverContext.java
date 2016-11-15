package org.cardygan.ilp;

public class SolverContext {

	public void solve(IlpModel ilpModel) {
		// TODO Auto-generated method stub

	}

	public Solver createSolver(IlpModel ilpModel) {
		return new CplexSolver(ilpModel);
	}

}
