package org.cardygan.ilp;


public abstract class IlpVar  {

	private final String name;

	public IlpVar(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return name;
	}

}
