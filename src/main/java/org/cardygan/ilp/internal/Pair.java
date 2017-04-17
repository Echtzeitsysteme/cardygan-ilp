package org.cardygan.ilp.internal;

import java.util.Objects;

public class Pair<U, V> {

	private U first;
	private V second;

	public Pair() {

	}

	public void setFirst(U first) {
		this.first = first;
	}

	public void setSecond(V second) {
		this.second = second;
	}

	public Pair(U first, V second) {
		this.first = first;
		this.second = second;
	}

	public U getFirst() {
		return first;
	}

	public V getSecond() {
		return second;
	}

	@Override
	public String toString() {
		return "<" + first + "," + second + ">";
	}

	@Override
	public int hashCode() {
		return Objects.hash(first, second);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public boolean equals(Object other) {
		if (other == null) {
			return false;
		}
		if (other == this) {
			return true;
		}
		if (!(other instanceof Pair)) {
			return false;
		}
		Pair otherMyClass = (Pair) other;
		if (first.equals(otherMyClass.getFirst()) && second.equals(otherMyClass.getSecond())) {
			return true;
		}
		return false;
	}

}
