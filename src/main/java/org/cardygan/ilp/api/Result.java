package org.cardygan.ilp.api;

import org.cardygan.ilp.api.model.Model;
import org.cardygan.ilp.api.model.Var;
import org.cardygan.ilp.api.solver.Solver;

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
        return "status: " + statistics.status + (objVal != null ? ", objVal: " + objVal : "") + ", duration " + statistics.getDuration() + "ms";
    }

    public Model getModel() {
        return model;
    }

    public static class Statistics {

        private final SolverStatus status;
        private final long duration;
        private final int colsRemovedByPresolve;
        private final int rowsRemovedByPresolve;


        public Statistics(SolverStatus status, long duration,
                          int colsRemovedByPresolve, int rowsRemovedByPresolve) {
            this.status = status;
            this.duration = duration;
            this.colsRemovedByPresolve = colsRemovedByPresolve;
            this.rowsRemovedByPresolve = rowsRemovedByPresolve;
        }

        public SolverStatus getStatus() {
            return status;
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

    public enum SolverStatus {
        UNBOUNDED, INF_OR_UNBD, INFEASIBLE, OPTIMAL, TIME_OUT
    }

}
