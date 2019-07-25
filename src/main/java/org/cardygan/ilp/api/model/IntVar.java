package org.cardygan.ilp.api.model;

public class IntVar extends Var {

    public IntVar(String name) {
        super(name);
    }



    @Override
    public String toString() {
        return getName();
    }
}
