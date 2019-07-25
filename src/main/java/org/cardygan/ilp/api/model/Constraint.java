package org.cardygan.ilp.api.model;

import org.cardygan.ilp.internal.util.Util;

import java.util.Objects;

/**
 * Created by markus on 18.02.17.
 */
public class Constraint {

    private final String name;

    public Constraint(String name) {
        Util.assertNotNull(name);
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Constraint that = (Constraint) o;
        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return name;
    }
}
