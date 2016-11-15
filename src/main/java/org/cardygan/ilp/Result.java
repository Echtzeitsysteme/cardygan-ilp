package org.cardygan.ilp;

import java.util.Map;
import java.util.Optional;

public class Result {

	private final Statistics statistics;
	private final Map<IlpVar, Double> solutions;
	private final Optional<Double> objVal;

	public Result(Statistics statistics, Map<IlpVar, Double> solutions, Optional<Double> objVal) {
		this.statistics = statistics;
		this.solutions = solutions;
		this.objVal = objVal;
	}

	public Statistics getStatistics() {
		return statistics;
	}

	public Map<IlpVar, Double> getSolutions() {
		return solutions;
	}

	public Optional<Double> getObjVal() {
		return objVal;
	}

	public static class Statistics {

		private final boolean feasible;
		private final boolean unbounded;
		private final long duration;

		public Statistics(boolean feasible, boolean unbounded, long duration) {
			this.feasible = feasible;
			this.unbounded = unbounded;
			this.duration = duration;
		}

		public boolean isFeasible() {
			return feasible;
		}

		public boolean isUnbounded() {
			return unbounded;
		}

		public long getDuration() {
			return duration;
		}

	}

}
