package org.cardygan.ilp.api.model;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Sos1Constraint {


    private final Map<Var, Double> elements;

    public Sos1Constraint(Map<Var, Double> elements) {
        this.elements = elements;
    }

    /**
     * Create SOS1 constraint with given variables and assign weights corresponding to ordering of variables.
     * The first element of the list has the lowest weight.
     *
     * @param vars
     */
    public Sos1Constraint(List<Var> vars) {
        elements = new HashMap<>(vars.size());

        for (int i = 0; i < vars.size(); i++) {
            elements.put(vars.get(i), (double) (i + 1));
        }
    }

    public Map<Var, Double> getElements() {
        return elements;
    }
}
