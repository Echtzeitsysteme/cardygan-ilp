package org.cardygan.ilp.internal.expr.model;

import org.cardygan.ilp.api.model.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class BasicModel {

    private final List<NormalizedArithExpr> constraints;
    private final BasicObjective objective;
    private final Integer m;
    private final Map<String, Var> vars;
    private final Map<DoubleVar, DblBounds> dblBounds;
    private final Map<IntVar, IntBounds> intBounds;
    private final List<Sos1Constraint> sos1;

    public BasicModel(List<NormalizedArithExpr> constraints,
                      BasicObjective objective,
                      int m,
                      Map<String, Var> vars,
                      Map<DoubleVar, DblBounds> dblBounds,
                      Map<IntVar, IntBounds> intBounds,
                      List<Sos1Constraint> sos1) {
        this.constraints = constraints;
        this.objective = objective;
        this.m = m;
        this.vars = vars;
        this.dblBounds = dblBounds;
        this.intBounds = intBounds;
        this.sos1 = sos1;
    }

    public BasicModel(List<NormalizedArithExpr> constraints,
                      BasicObjective objective,
                      Map<String, Var> vars,
                      Map<DoubleVar, DblBounds> dblBounds,
                      Map<IntVar, IntBounds> intBounds,
                      List<Sos1Constraint> sos1) {
        this.constraints = constraints;
        this.objective = objective;
        this.m = null;
        this.vars = vars;
        this.dblBounds = dblBounds;
        this.intBounds = intBounds;
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


    public DblBounds getBounds(DoubleVar var) {
        return dblBounds.get(var);
    }

    public IntBounds getBounds(IntVar var) {
        return intBounds.get(var);
    }

    public Map<DoubleVar, DblBounds> getDblBounds() {
        return dblBounds;
    }

    public Map<IntVar, IntBounds> getIntBounds() {
        return intBounds;
    }

    public List<Sos1Constraint> getSos1() {
        return sos1;
    }
}
