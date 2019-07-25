package org.cardygan.ilp.internal.solver.milp;

import org.cardygan.ilp.api.model.Var;
import org.cardygan.ilp.internal.util.Util;

public class LinearObj {

    private final boolean max;
    private final double[] params;
    private final Var[] vars;
    private final double constant;

    public LinearObj(boolean max, Var[] vars, double[] params, double constant) {
        Util.assertNotNull(params, vars);
        Util.assertTrue(params.length == vars.length);

        this.max = max;
        this.params = params;
        this.vars = vars;
        this.constant = constant;
    }

    public double getConstant() {
        return constant;
    }

    public boolean isMax() {
        return max;
    }

    public double[] getParams() {
        return params;
    }

    public Var[] getVars() {
        return vars;
    }
}
