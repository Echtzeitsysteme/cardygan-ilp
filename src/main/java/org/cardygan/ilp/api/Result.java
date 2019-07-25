package org.cardygan.ilp.api;

import org.cardygan.ilp.api.model.Var;

public class Result {

    private final SolverStatus status;
    private final long duration;


    public Result(SolverStatus status, long duration) {
        this.status = status;
        this.duration = duration;
    }

    public SolverStatus getStatus() {
        return status;
    }

    public long getDuration() {
        return duration;
    }


    @Override
    public String toString() {
        return "status: " + status + ", duration " + getDuration() + "ms";
    }


    public enum SolverStatus {
        UNBOUNDED, INF_OR_UNBD, INFEASIBLE, OPTIMAL, TIME_OUT
    }

}
