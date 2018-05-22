package org.cardygan.ilp.api;

import org.cardygan.ilp.api.model.Model;
import org.cardygan.ilp.api.model.Var;

import java.util.Map;
import java.util.Optional;

public class Result {

    private final Statistics statistics;
    private final Map<Var, Double> solutions;
    private final Double objVal;
    private final Model model;

    public Result(Model model, Statistics statistics, Map<Var, Double> solutions, Double objVal) {
        this.model = model;
        this.statistics = statistics;
        this.solutions = solutions;
        this.objVal = objVal;
    }

    public Statistics getStatistics() {
        return statistics;
    }

    public Map<Var, Double> getSolutions() {
        return solutions;
    }

    public Optional<Double> getObjVal() {
        return Optional.ofNullable(objVal);
    }

    @Override
    public String toString() {
        return "feasible: " + statistics.feasible + ", unbounded: " + statistics.unbounded + (objVal != null ? ", objVal: " + objVal : "") + ", duration " + statistics.getDuration() + "ms";
    }

    public Model getModel() {
        return model;
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
