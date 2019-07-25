package org.cardygan.ilp.internal.solver.milp;

import org.cardygan.ilp.api.model.ArithExpr;
import org.cardygan.ilp.api.model.BinaryVar;
import org.cardygan.ilp.api.model.Model;
import org.cardygan.ilp.api.model.Var;
import org.cardygan.ilp.api.model.bool.RelOp;
import org.cardygan.ilp.internal.expr.ExprSimplifier;
import org.cardygan.ilp.internal.util.Util;

import java.util.*;

import static org.cardygan.ilp.api.util.ExprDsl.*;

public class SosBasedCstrGenerator extends AbstractCstrGenerator {

    protected List<LinearConstr> addGeqCstr(BinaryVar r_k, ExprSimplifier.SimplifiedArithExpr expr,
                                            boolean negateBothSides, final Model ilpModel) {
        final List<LinearConstr> ret = new ArrayList<>(7);

        // lhs_coefs - rhs_coefs
        final Map<Var, Double> lhs;
        if (negateBothSides) {
            lhs = new HashMap<>(expr.getCoeffs().size());
            expr.getCoeffs().forEach((key, value) -> lhs.put(key, -value));
        } else
            lhs = new HashMap<>(expr.getCoeffs());

        // move constants to rhs or add: -(lhs_const - rhs_const)
        final double rhs = negateBothSides ? expr.getConstant() : -expr.getConstant();

        Util.assertTrue(rhs >= 0);

        // bind f_i to helping variable r_k using additional helping variable r_l
        BinaryVar r_l = ilpModel.newBinaryVar();

        // r_l + r_k = 1
        ret.add(normalize(eq(sum(r_l, r_k), p(1))));

        Var s = ilpModel.newDoubleVar();

        LinkedList<ArithExpr> summands = new LinkedList<>();
        lhs.forEach((key, value) -> summands.add(mult(p(value), key)));
        summands.add(mult(p(1), s));

        Set<Var> sos = new HashSet<>(2);
        sos.add(s);
        sos.add(r_k);
        ret.addAll(addSos1(sos));

        ret.add(normalize(geq(sum(summands), p(rhs))));

        Var s_2 = ilpModel.newDoubleVar();
        summands.removeLast();
        summands.add(mult(p(-1), s_2));

        Set<Var> sos2 = new HashSet<>(2);
        sos2.add(s_2);
        sos2.add(r_l);
        ret.addAll(addSos1(sos2));

        ret.add(normalize(leq(sum(summands), p(rhs - EPSILON))));

        return ret;
    }

    protected List<LinearConstr> addLeqCstr(BinaryVar r_k, ExprSimplifier.SimplifiedArithExpr expr,
                                            boolean negateBothSides, final Model ilpModel) {
        final List<LinearConstr> ret = new ArrayList<>(7);

        // lhs_coefs - rhs_coefs
        final Map<Var, Double> lhs;
        if (negateBothSides) {
            lhs = new HashMap<>(expr.getCoeffs().size());
            expr.getCoeffs().forEach((var, value) -> lhs.put(var, -value));
        } else
            lhs = new HashMap<>(expr.getCoeffs());

        // move constants to rhs or add: -(lhs_const - rhs_const)
        final double rhs = negateBothSides ? expr.getConstant() : -expr.getConstant();


        Util.assertTrue(rhs >= 0);

        // bind f_i to helping variable r_k using additional helping variable r_l
        BinaryVar r_l = ilpModel.newBinaryVar();


        // r_l + r_k = 1
        ret.add(normalize(eq(sum(r_l, r_k), p(1))));


        Var s = ilpModel.newDoubleVar();

        LinkedList<ArithExpr> summands = new LinkedList<>();
        lhs.forEach((key, value) -> summands.add(mult(p(value), key)));
        summands.add(mult(p(-1), s));


        Set<Var> sos = new HashSet<>();
        sos.add(s);
        sos.add(r_k);
        ret.addAll(addSos1(sos));

        ret.add(normalize(leq(sum(summands), p(rhs))));

        Var s_2 = ilpModel.newDoubleVar();
        summands.removeLast();
//        summands.removeFirst( );
        summands.add(mult(1, s_2));

        Set<Var> sos2 = new HashSet<>(2);
        sos2.add(s_2);
        sos2.add(r_l);
        ret.addAll(addSos1(sos2));

        ret.add(normalize(geq(sum(summands), rhs < 1 ? p(rhs + EPSILON) : p(rhs + 1))));

        return ret;
    }

    private LinearConstr normalize(RelOp expr) {
        Optional<LinearConstr> ret = ExprSimplifier.normalizeCstr(expr);
        return ret.orElseThrow(IllegalStateException::new);
    }

    private List<LinearConstr> addSos1(Set<Var> vars) {
        final List<LinearConstr> ret = new ArrayList<>(2);

        ret.add(normalize(geq(sum(new ArrayList<>(vars)), p(EPSILON))));

        final Map<Var, Double> sos = new HashMap<>(vars.size());
        int weight = 1;
        for (Var v : vars) {
            sos.put(v, (double) weight);
            weight++;
        }
        ret.add(ExprSimplifier.normalizeSos(sos));

        return ret;
    }

}
