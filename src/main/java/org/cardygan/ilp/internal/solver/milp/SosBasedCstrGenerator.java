package org.cardygan.ilp.internal.solver.milp;

import org.apache.commons.lang3.ArrayUtils;
import org.cardygan.ilp.api.model.BinaryVar;
import org.cardygan.ilp.api.model.Model;
import org.cardygan.ilp.api.model.Var;
import org.cardygan.ilp.internal.expr.ExprSimplifier;
import org.cardygan.ilp.internal.util.Util;

import java.util.ArrayList;
import java.util.List;

public class SosBasedCstrGenerator extends AbstractCstrGenerator {

    protected List<LinearConstr> addGeqCstr(BinaryVar r_k, ExprSimplifier.SimplifiedArithExpr expr,
                                            boolean negateBothSides, final Model ilpModel) {
        final List<LinearConstr> ret = new ArrayList<>(7);


        // move constants to rhs or add: -(lhs_const - rhs_const)
        final double rhs = negateBothSides ? expr.getConstant() : -expr.getConstant();

        Util.assertTrue(rhs >= 0);

        // bind f_i to helping variable r_k using additional helping variable r_l
        BinaryVar r_l = ilpModel.newBinaryVar();

        // r_l + r_k = 1
        ret.add(new LinearConstr(new Var[]{r_l, r_k}, new double[]{1, 1}, 1, LinearConstr.Type.EQ));


        Var s = ilpModel.newDoubleVar();


        Var[] vars = expr.getVars();
        double[] params = (negateBothSides) ? expr.getParams(e -> -e) : expr.getParams();

        ret.add(new LinearConstr(ArrayUtils.addAll(vars, s), ArrayUtils.addAll(params, 1), rhs, LinearConstr.Type.GEQ));


        ret.addAll(addSos1(new Var[]{s, r_k}, new double[]{1, 2}));


        Var s_2 = ilpModel.newDoubleVar();

        ret.add(new LinearConstr(ArrayUtils.addAll(vars, s_2), ArrayUtils.addAll(params, -1), rhs - EPSILON, LinearConstr.Type.LEQ));

        ret.addAll(addSos1(new Var[]{s_2, r_l}, new double[]{1, 2}));

        return ret;
    }

    protected List<LinearConstr> addLeqCstr(BinaryVar r_k, ExprSimplifier.SimplifiedArithExpr expr,
                                            boolean negateBothSides, final Model ilpModel) {
        final List<LinearConstr> ret = new ArrayList<>(7);

        // move constants to rhs or add: -(lhs_const - rhs_const)
        final double rhs = negateBothSides ? expr.getConstant() : -expr.getConstant();


        Util.assertTrue(rhs >= 0);

        // bind f_i to helping variable r_k using additional helping variable r_l
        BinaryVar r_l = ilpModel.newBinaryVar();


        // r_l + r_k = 1
        ret.add(new LinearConstr(new Var[]{r_l, r_k}, new double[]{1, 1}, 1, LinearConstr.Type.EQ));

        Var s = ilpModel.newDoubleVar();


        Var[] vars = expr.getVars();
        double[] params = (negateBothSides) ? expr.getParams(e -> -e) : expr.getParams();


        ret.add(new LinearConstr(ArrayUtils.addAll(vars, s), ArrayUtils.addAll(params, -1), rhs, LinearConstr.Type.LEQ));

        ret.addAll(addSos1(new Var[]{s, r_k}, new double[]{1, 2}));


        Var s_2 = ilpModel.newDoubleVar();

        ret.add(new LinearConstr(ArrayUtils.addAll(vars, s_2), ArrayUtils.addAll(params, 1), rhs < 1 ? rhs + EPSILON : rhs + 1, LinearConstr.Type.GEQ));

        ret.addAll(addSos1(new Var[]{s_2, r_l}, new double[]{1, 2}));


        return ret;
    }

    private List<LinearConstr> addSos1(Var[] vars, double[] params) {
        final List<LinearConstr> ret = new ArrayList<>(2);

        LinearConstr cstr = new LinearConstr(vars, params, EPSILON, LinearConstr.Type.GEQ);

        ret.add(cstr);

        LinearConstr sos = new LinearConstr(vars, params, 0, LinearConstr.Type.SOS);
        ret.add(sos);

        return ret;
    }

}
