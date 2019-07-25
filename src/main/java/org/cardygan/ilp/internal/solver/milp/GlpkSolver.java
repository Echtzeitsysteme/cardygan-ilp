package org.cardygan.ilp.internal.solver.milp;

import org.cardygan.ilp.api.Result;
import org.cardygan.ilp.api.model.Constraint;
import org.cardygan.ilp.api.model.Var;

public class GlpkSolver extends MILPSolver {

    @Override
    public void addCstr(String name, LinearConstr cstr) {

    }

    @Override
    public void setObj(LinearObj obj) {

    }

    @Override
    public void removeConstr(Constraint constraint) {

    }

    @Override
    public Result optimize() {
        return null;
    }

    @Override
    public void dispose() {

    }

    @Override
    public boolean isDisposed() {
        return false;
    }

    @Override
    public double getVal(Var var) {
        return 0;
    }

    @Override
    public double getObjVal() {
        return 0;
    }

    @Override
    public Object getUnderlyingModel() {
        return null;
    }

    @Override
    public void addVar(String name, double lb, double ub, VarType type) {

    }

    @Override
    public int getNumVars() {
        return 0;
    }

    @Override
    public int getNumConstrs() {
        return 0;
    }

    @Override
    public boolean hasVar(String varName) {
        return false;
    }

    @Override
    public boolean hasConstraint(String cstrName) {
        return false;
    }
}
