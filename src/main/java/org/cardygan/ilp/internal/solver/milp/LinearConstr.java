package org.cardygan.ilp.internal.solver.milp;

import org.cardygan.ilp.api.model.Var;
import org.cardygan.ilp.internal.util.Util;

public class LinearConstr {

    private final Var[] vars;
    private final double[] params;

    private final double rhs;
    private final Type type;

    public LinearConstr(Var[] vars, double[] params, double rhs, Type type) {
        Util.assertNotNull(vars, params, type);

        Util.assertTrue(vars.length == params.length);
        Util.assertTrue(vars.length > 0);

        this.vars = vars;
        this.params = params;
        this.rhs = rhs;
        this.type = type;
    }

    public Var[] getVars() {
        return vars;
    }

    public double[] getParams() {
        return params;
    }

    public double getRhs() {
        return rhs;
    }

    public Type getType() {
        return type;
    }


    public enum Type {
        LEQ(0), GEQ(1), EQ(2), SOS(3);

        private final int val;

        Type(int val) {
            this.val = val;
        }

        public int getVal() {
            return val;
        }
    }
}
