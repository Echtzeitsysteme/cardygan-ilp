package org.cardygan.ilp.api.model;

public class DoubleVar extends Var {

    DoubleVar(String name) {
        super(name);
    }


    @Override
    public String toString() {
        return getName();
    }

}
