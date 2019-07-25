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

public class BigMBasedCstrGenerator extends AbstractCstrGenerator {

//    public static class Factory implements MILPConstGeneratorFactory {
//        private final int M;
//
//        public Factory(int M) {
//            this.M = M;
//        }
//
//        @Override
//        public MILPConstrGenerator newInstance() {
//            return new BigMBasedCstrGenerator(M);
//        }
//    }

    private final int M;

    public BigMBasedCstrGenerator(int M) {
        this.M = M;
    }

    @Override
    protected List<LinearConstr> addLeqCstr(BinaryVar r_k, ExprSimplifier.SimplifiedArithExpr expr,
                                            boolean negateBothSides, Model ilpModel) {

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

        if (rhs == 0) {
            // f_i - ... + f_n <= 0 + M * (1-r_k)
            // f_i - ... + f_n + M * r_k <= M
            final LinkedList<ArithExpr> summands = new LinkedList<>();
            lhs.forEach((key, value) -> summands.add(mult(p(value), key)));
            summands.add(mult(p(M), r_k));

            ret.add(normalize(leq(sum(summands), p(M))));

//            ilpModel.newConstraint("leq_" + r_k.getName(), Util.leq(summands, M));

            // f_i - ... + f_n >= - M * r_k + r_l
            // f_i - ... + f_n + M * r_k - r_l >= 0
//            summands = new ArrayList<>(lhs);
//            summands.add(Util.coef(M, r_k));
//            summands.add(Util.coef(-1, r_l));
            summands.removeLast();
            summands.add(mult(p(M), r_k));
            summands.add(mult(p(-1), r_l));
            ret.add(normalize(geq(sum(summands), p(0))));

//            ilpModel.newConstraint("leq_" + r_k.getName(), Util.geq(summands, 0));

        } else {
            // f_i + ... + f_n >= (rhs + 1) * r_l
            final LinkedList<ArithExpr> summands = new LinkedList<>();
            lhs.forEach((key, value) -> summands.add(mult(p(value), key)));
            summands.add(mult(p(M), r_k));
            summands.add(mult(p(-(rhs + 1)), r_l));
//            List<Coefficient> summands = new ArrayList<>(lhs.size() + 1);
//            summands.add(Util.coef(-(rhs + 1), r_l));
//            summands.addAll(lhs);
            ret.add(normalize(geq(sum(summands), p(0))));

            // a <= b
            // f_i + ... + f_n <= rhs + M (1-r_k)
            // f_i + ... + f_n <= - M r_k + rhs + M
            // f_i + ... + f_n + M r_k <=  rhs + M
            final LinkedList<ArithExpr> lhsCopy = new LinkedList<>();
            lhs.forEach((key, value) -> lhsCopy.add(mult(p(value), key)));
            lhsCopy.add(mult(M, r_k));

            final double rhsNew = rhs + M;
            ret.add(normalize(leq(sum(lhsCopy), p(rhsNew))));

//            ilpModel.newConstraint("leq_" + r_k.getName(), Util.leq(lhs, rhs));
        }

        // r_l + r_k = 1
//        ilpModel.newConstraint("leq_" + r_k.getName(), Util.eq(Arrays.asList(Util.coef(1, r_l), Util.coef(1, r_k)), 1));
        ret.add(normalize(eq(sum(r_k, r_l), p(1))));

        return ret;
    }

    @Override
    protected List<LinearConstr> addGeqCstr(BinaryVar r_k, ExprSimplifier.SimplifiedArithExpr expr,
                                            boolean negateBothSides, Model ilpModel) {
        final List<LinearConstr> ret = new ArrayList<>(7);

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
        final BinaryVar r_l = ilpModel.newBinaryVar();


        if (rhs == 0) {
            // (1-r_k) * M + f_1 - f_i + ... + f_n >= 0
            // - r_k * M + f_1 - f_i + ... + f_n >= -M
            final LinkedList<ArithExpr> summands = new LinkedList<>();
            lhs.forEach((key, value) -> summands.add(mult(p(value), key)));
            summands.add(mult(p(-M), r_k));

//            List<Coefficient> summands = new ArrayList<>(lhs.size() + 1);
//            summands.add(Util.coef(-M, r_k));
//            summands.addAll(lhs);

            ret.add(normalize(geq(sum(summands), p(-M))));

//            ilpModel.newConstraint("geq_" + r_k.getName(), Util.geq(summands, -M));

            // f_1 - f_i + ... + f_n <= M * r_k - r_l
            // f_1 - f_i + ... + f_n - M r_k + r_l <= 0
            summands.removeLast();
            summands.add(mult(p(-M), r_k));
            summands.add(mult(p(1), r_l));
            ret.add(normalize(leq(sum(summands), p(0))));

//            summands.clear();
//            summands.add(Util.coef(-M, r_k));
//            summands.add(Util.coef(1, r_l));
//
//            ilpModel.newConstraint("geq_" + r_k.getName(), Util.leq(summands, 0));

        } else {
            if (rhs == 1) {
                // 1 - r_l + M r_k >= f_i + ... + f_n
                final LinkedList<ArithExpr> summands = new LinkedList<>();
                lhs.forEach((key, value) -> summands.add(mult(p(-value), key)));
                summands.add(mult(p(M), r_k));
                summands.add(mult(p(-1), r_l));

//                summands.add(mult(p(-(rhs + 1)), r_l));

//                List<Coefficient> summands = new ArrayList<>(lhs.size() + 2);
//                summands.add(Util.coef(-1, r_l));
//                summands.add(Util.coef(M, r_k));
//                summands.addAll(Util.neg(lhs));
                ret.add(normalize(geq(sum(summands), p(-1))));
//                ilpModel.newConstraint("geq_" + r_k.getName(), Util.geq(summands, -1));
            } else {
                // (rhs-1) * r_l + M * r_k >= f_i + ... + f_n
                final LinkedList<ArithExpr> summands = new LinkedList<>();
                lhs.forEach((key, value) -> summands.add(mult(p(-value), key)));
                summands.add(mult(p(rhs - 1), r_l));
                summands.add(mult(p(M), r_k));
//                List<Coefficient> summands = new ArrayList<>(lhs.size() + 2);
//                summands.add(Util.coef(rhs - 1, r_l));
//                summands.add(Util.coef(M, r_k));
//                summands.addAll(Util.neg(lhs));
                ret.add(normalize(geq(sum(summands), p(0))));
//                ilpModel.newConstraint("geq_" + r_k.getName(), Util.geq(summands, 0));
            }


            // a >= b
            final LinkedList<ArithExpr> summands = new LinkedList<>();
            lhs.forEach((key, value) -> summands.add(mult(p(value), key)));
            summands.add(mult(p(-rhs), r_k));

//            lhs.add(Util.coef(-rhs, r_k));
            ret.add(normalize(geq(sum(summands), p(0))));
//            ilpModel.newConstraint("geq_" + r_k.getName(), Util.geq(lhs, 0));
        }

        // r_l + r_k = 1
        ret.add(normalize(eq(sum(r_k, r_l), p(1))));
//        ilpModel.newConstraint("eq_" + r_k.getName(), Util.eq(Arrays.asList(Util.coef(1, r_l), Util.coef(1, r_k)), 1));

        return ret;
    }

    private LinearConstr normalize(RelOp expr) {
        Optional<LinearConstr> ret = ExprSimplifier.normalizeCstr(expr);
        return ret.orElseThrow(IllegalStateException::new);
    }


}
