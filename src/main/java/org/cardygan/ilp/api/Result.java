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
        private final int colsRemovedByPresolve;
        private final int rowsRemovedByPresolve;


        public Statistics(boolean feasible, boolean unbounded, long duration,
                          int colsRemovedByPresolve, int rowsRemovedByPresolve) {
            this.feasible = feasible;
            this.unbounded = unbounded;
            this.duration = duration;
            this.colsRemovedByPresolve = colsRemovedByPresolve;
            this.rowsRemovedByPresolve = rowsRemovedByPresolve;
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

        /**
         * Get number of rows removed by pre-solving or -1 if now statistics were collected.
         *
         * @return the number of rows removed by pre-solving
         */
        public int getNumColsRemovedAfterPresolve() {
            return colsRemovedByPresolve;
        }

        /**
         * Get number of columns removed by pre-solving or -1 if now statistics were collected.
         *
         * @return the number of columns removed by pre-solving
         */
        public int getNumRowsRemovedAfterPresolve() {
            return rowsRemovedByPresolve;
        }
    }

}
