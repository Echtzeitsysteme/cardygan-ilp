package org.cardygan.ilp.api.model;

import java.util.Objects;

public class DoubleVar extends Var {

    DoubleVar(String name) {
        super(name);
    }


    @Override
    public int hashCode() {
        return Objects.hash(getName());
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (other == this) {
            return true;
        }
        if (!(other instanceof DoubleVar)) {
            return false;
        }
        DoubleVar otherMyClass = (DoubleVar) other;
        if (getName().equals(otherMyClass.getName())) {
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return getName();
    }

}
