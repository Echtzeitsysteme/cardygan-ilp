package org.cardygan.ilp.api.model;

public class IntBounds {
    private final int lb;
    private final int ub;

    public IntBounds(int lb, int ub) {
        this.lb = lb;
        this.ub = ub;
    }

    public int getLb() {
        return lb;
    }

    public int getUb() {
        return ub;
    }
}
