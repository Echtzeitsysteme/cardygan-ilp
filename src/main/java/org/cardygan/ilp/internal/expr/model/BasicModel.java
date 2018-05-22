package org.cardygan.ilp.internal.expr.model;

import org.cardygan.ilp.api.model.Model;
import org.cardygan.ilp.api.model.Var;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class BasicModel {

    private final List<NormalizedArithExpr> constraints;
    private final BasicObjective objective;
    private final Integer m;
    private final Map<String, Var> vars;
    private final Map<Var, Model.Bounds> bounds;
    private final List<Set<Var>> sos1;

    public BasicModel(List<NormalizedArithExpr> constraints,
                      BasicObjective objective,
                      int m,
                      Map<String, Var> vars,
                      Map<Var, Model.Bounds> bounds,
                      List<Set<Var>> sos1) {
        this.constraints = constraints;
        this.objective = objective;
        this.m = m;
        this.vars = vars;
        this.bounds = bounds;
        this.sos1 = sos1;
    }

    public BasicModel(List<NormalizedArithExpr> constraints,
                      BasicObjective objective,
                      Map<String, Var> vars,
                      Map<Var, Model.Bounds> bounds,
                      List<Set<Var>> sos1) {
        this.constraints = constraints;
        this.objective = objective;
        this.m = null;
        this.vars = vars;
        this.bounds = bounds;
        this.sos1 = sos1;
    }

    public List<NormalizedArithExpr> getConstraints() {
        return constraints;
    }

    public Optional<BasicObjective> getObjective() {
        return Optional.ofNullable(objective);
    }

    public Optional<Integer> getM() {
        return Optional.ofNullable(m);
    }

    public Map<String, Var> getVars() {
        return vars;
    }


    public Model.Bounds getBounds(Var var) {
        return bounds.get(var);
    }

    public Map<Var, Model.Bounds> getBounds() {
        return bounds;
    }

    public List<Set<Var>> getSos1() {
        return sos1;
    }
}
