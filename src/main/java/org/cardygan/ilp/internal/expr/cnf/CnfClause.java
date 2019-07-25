package org.cardygan.ilp.internal.expr.cnf;

import org.cardygan.ilp.api.model.BinaryVar;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class CnfClause {
    private final Map<BinaryVar, Boolean> vars;

    public CnfClause() {
        vars = new HashMap<>();
    }

    public Map<BinaryVar, Boolean> getVars() {
        return vars;
    }

    /**
     * Adds a variable to the clause.
     *
     * @param x   variable of clause
     * @param neg true if variable is negated, otherwise false
     */
    public void add(BinaryVar x, boolean neg) {
        vars.put(x, neg);
    }

    /**
     * Adds a (non negated) variable to the clause
     *
     * @param x variable of clause
     */
    public void add(BinaryVar x) {
        vars.put(x, false);
    }

    /**
     * Adds a list of (non negated) variables to the clause.
     *
     * @param x_c list of positive variables.
     */
    public void addAll(List<BinaryVar> x_c) {
        x_c.forEach(x -> vars.put(x, false));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CnfClause cnfClause = (CnfClause) o;
        return vars.equals(cnfClause.vars);
    }

    @Override
    public int hashCode() {
        return Objects.hash(vars);
    }

    @Override
    public String toString() {
        return "{ "
                + vars.entrySet().stream().map(e -> e.getValue() ? "!" + e.getKey().toString() : e.getKey().toString())
                .collect(Collectors.joining(", "))
                + " }";
    }
}