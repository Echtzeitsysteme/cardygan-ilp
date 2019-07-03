package org.cardygan.ilp.api.model;

public class DblBounds {
    private final double lb;
    private final double ub;

    public DblBounds(double lb, double ub) {
        this.lb = lb;
        this.ub = ub;
    }

    public double getLb() {
        return lb;
    }

    public double getUb() {
        return ub;
    }
}
