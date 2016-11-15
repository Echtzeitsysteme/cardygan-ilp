package org.cardygan.ilp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class IlpModel {

	protected IlpModel() {

	}

	private final List<IlpVar> ilpVars = new ArrayList<>();

	private Objective objective;

	private final List<IlpConstraint> ilpConstraints = new ArrayList<>();

	public void addVar(IlpVar ilpVar) {
		ilpVars.add(ilpVar);
	}

	public void add(IlpConstraint cstr) {
		ilpConstraints.add(cstr);
	}

	public List<IlpVar> getIlpVars() {
		return Collections.unmodifiableList(ilpVars);
	}

	public Objective getObjective() {
		return objective;
	}

	public List<IlpConstraint> getIlpConstraints() {
		return Collections.unmodifiableList(ilpConstraints);
	}

	public void setObjective(Objective objective) {
		this.objective = objective;
	}


	@Override
	public String toString() {
		StringBuffer ret = new StringBuffer();
		ilpConstraints.forEach(c -> ret.append(c + "\n"));
		return ret.toString();
	}

}
