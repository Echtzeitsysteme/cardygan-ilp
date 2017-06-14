package org.cardygan.ilp.internal.expr;


import org.cardygan.ilp.api.BinaryVar;
import org.cardygan.ilp.api.IntVar;
import org.cardygan.ilp.api.Model;
import org.cardygan.ilp.api.expr.Var;
import org.cardygan.ilp.api.expr.bool.Geq;
import org.cardygan.ilp.api.expr.bool.Leq;
import org.cardygan.ilp.api.expr.bool.RelOp;
import org.cardygan.ilp.internal.Coefficient;
import org.cardygan.ilp.internal.Pair;
import org.cardygan.ilp.internal.util.Util;

import java.util.*;
import java.util.stream.Collectors;

import static org.cardygan.ilp.api.util.ExprDsl.*;

public class BoolLiteralToConstraintProcessor {

    private final Model ilpModel;
    private final Set<RelOp> processed;

    public BoolLiteralToConstraintProcessor(Model ilpModel) {
        this.ilpModel = ilpModel;
        this.processed = new HashSet<>();
    }


    public void process(BinaryVar var, RelOp expr) {
        if (processed.contains(expr)) {
            return;
        }

        ArithExprSimplifier exprSimplifier = new ArithExprSimplifier(sum(expr.getLhs(), neg(expr.getRhs())));
        double rhs = -exprSimplifier.getConstant();

        if (expr instanceof Geq && rhs >= 0) {
            if (ilpModel.getM(expr).isPresent()) {
                addGeqCstrWithM(var, expr, exprSimplifier, false);
            } else {
                addGeqCstrWithoutM(var, expr, exprSimplifier, false);
            }

        } else if (expr instanceof Geq && rhs < 0) {
            if (ilpModel.getM(expr).isPresent()) {
                addLeqCstrWithM(var, expr, exprSimplifier, true);
            } else {
                addLeqCstrWithoutM(var, expr, exprSimplifier, true);
            }

        } else if (expr instanceof Leq && rhs >= 0) {
            if (ilpModel.getM(expr).isPresent()) {
                addLeqCstrWithM(var, expr, exprSimplifier, false);
            } else {
                addLeqCstrWithoutM(var, expr, exprSimplifier, false);
            }

        } else if (expr instanceof Leq && rhs < 0) {
            if (ilpModel.getM(expr).isPresent()) {
                addGeqCstrWithM(var, expr, exprSimplifier, true);
            } else {
                addGeqCstrWithoutM(var, expr, exprSimplifier, true);
            }

        } else {
            throw new IllegalStateException("Unknown BoolRel subtype.");
        }

        processed.add(expr);

    }

    private void addLeqCstrWithoutM(BinaryVar var, RelOp expr, ArithExprSimplifier exprSimplifier, boolean negateBothSides) {
        // lhs_coefs - rhs_coefs
        List<Coefficient> lhs = createCoefficients(exprSimplifier.getSummands());
        lhs = negateBothSides ? Util.neg(lhs) : lhs;


        // move constants to rhs or add: -(lhs_const - rhs_const)
        double rhs = negateBothSides ? exprSimplifier.getConstant() : -exprSimplifier.getConstant();

        Util.assertTrue(rhs >= 0);

        // bind f_i to helping variable r_k using additional helping variable r_l
        BinaryVar r_k = var;
        BinaryVar r_l = ilpModel.newBinaryVar();

        // r_l + r_k = 1
        ilpModel.newConstraint("leq_" + r_k.getName()).setExpr(Util.eq(Arrays.asList(Util.coef(1, r_l), Util.coef(1, r_k)), 1));

        if (rhs == 0) {
            // f_i - ... + f_n <= s1
            // f_i - ... + f_n - s1 <= 0
            IntVar s1 = ilpModel.newIntVar();
            List<Coefficient> summands = new ArrayList<>();
            summands.addAll(lhs);
            summands.add(Util.coef(-1, s1));

            Set<Var> sos1 = new HashSet<>();
            sos1.add(r_k);
            sos1.add(s1);
            ilpModel.addSos1(sos1);

            ilpModel.newConstraint("leq_" + r_k.getName()).setExpr(Util.leq(summands, 0));

            // f_i - ... + f_n >= -s2 + r_l
            // f_i - ... + f_n + s2 - r_l >= 0
            IntVar s2 = ilpModel.newIntVar();
            summands = new ArrayList<>();
            summands.addAll(lhs);
            summands.add(Util.coef(1, s2));
            summands.add(Util.coef(-1, r_l));

            Set<Var> sos2 = new HashSet<>();
            sos2.add(r_l);
            sos2.add(s2);
            ilpModel.addSos1(sos2);

            ilpModel.newConstraint("leq_" + r_k.getName()).setExpr(Util.geq(summands, 0));

        } else {
            // f_i + ... + f_n >= (rhs + 1) * r_l
            List<Coefficient> summands = new ArrayList<>();
            summands.add(Util.coef(-(rhs + 1), r_l));
            summands.addAll(lhs);

            ilpModel.newConstraint("leq_" + r_k.getName()).setExpr(Util.geq(summands, 0));

            // f_i + ... + f_n <= rhs + s
            // f_i + ... + f_n - s <= rhs
            IntVar s = ilpModel.newIntVar();
            lhs.add(Util.coef(-1, s));

            Set<Var> sos = new HashSet<>();
            sos.add(r_k);
            sos.add(s);
            ilpModel.addSos1(sos);

            ilpModel.newConstraint("leq_" + r_k.getName()).setExpr(Util.leq(lhs, rhs));
        }


    }

    private void addGeqCstrWithoutM(BinaryVar var, RelOp expr, ArithExprSimplifier exprSimplifier, boolean negateBothSides) {
        // lhs_coefs - rhs_coefs
        List<Coefficient> lhs = createCoefficients(exprSimplifier.getSummands());
        lhs = negateBothSides ? Util.neg(lhs) : lhs;

        // move constants to rhs or add: -(lhs_const - rhs_const)
        double rhs = negateBothSides ? exprSimplifier.getConstant() : -exprSimplifier.getConstant();


        Util.assertTrue(rhs >= 0);

        // bind f_i to helping variable r_k using additional helping variable r_l
        BinaryVar r_k = var;
        BinaryVar r_l = ilpModel.newBinaryVar();

        // r_l + r_k = 1
        ilpModel.newConstraint("eq_" + r_k.getName()).setExpr(Util.eq(Arrays.asList(Util.coef(1, r_l), Util.coef(1, r_k)), 1));

        if (rhs == 0) {
            // s_1 + f_1 - f_i + ... + f_n >= 0
            IntVar s_1 = ilpModel.newIntVar();
            List<Coefficient> summands = new ArrayList<>();
            summands.add(Util.coef(1, s_1));
            summands.addAll(lhs);

            ilpModel.newConstraint("geq_" + r_k.getName()).setExpr(Util.geq(summands, 0));

            // sos1 {s_1, r_k}
            Set<Var> sos = new HashSet<>();
            sos.add(s_1);
            sos.add(r_k);
            ilpModel.addSos1(sos);



            // f_1 - f_i + ... + f_n <= s_2 - r_l
            // f_1 - f_i + ... + f_n - s_2 + r_l <= 0
            IntVar s_2 = ilpModel.newIntVar();
            summands = new ArrayList<>();
            summands.addAll(lhs);
            summands.add(Util.coef(-1, s_2));
            summands.add(Util.coef(1, r_l));

            // sos1 {s_2, r_l}
            sos = new HashSet<>();
            sos.add(s_2);
            sos.add(r_l);
            ilpModel.addSos1(sos);


            ilpModel.newConstraint("geq_" + r_k.getName()).setExpr(Util.leq(summands, 0));

        } else {
            if (rhs == 1) {
                // 1 - r_l + s >= f_i + ... + f_n
                IntVar s = ilpModel.newIntVar();

                List<Coefficient> summands = new ArrayList<>();
                summands.add(Util.coef(-1, r_l));
                summands.add(Util.coef(1, s));
                summands.addAll(Util.neg(lhs));

                // sos1 {s, r_l}
                Set<Var> sos = new HashSet<>();
                sos.add(r_l);
                sos.add(s);
                ilpModel.addSos1(sos);

                ilpModel.newConstraint("geq_" + r_k.getName()).setExpr(Util.geq(summands, -1));
            } else {
                // (rhs-1) * r_l + s >= f_i + ... + f_n
                IntVar s = ilpModel.newIntVar();

                List<Coefficient> summands = new ArrayList<>();
                summands.add(Util.coef(rhs - 1, r_l));
                summands.add(Util.coef(1, s));
                summands.addAll(Util.neg(lhs));

                ilpModel.newConstraint("geq_" + r_k.getName()).setExpr(Util.geq(summands, 0));

                // sos1 {s, r_l}
                Set<Var> sos = new HashSet<>();
                sos.add(r_l);
                sos.add(s);
                ilpModel.addSos1(sos);

            }


            // f_i + ... + f_n >= rhs * r_k
            lhs.add(Util.coef(-rhs, r_k));

            ilpModel.newConstraint("geq_" + r_k.getName()).setExpr(Util.geq(lhs, 0));
        }

    }

    private void addGeqCstrWithM(BinaryVar var, RelOp expr, ArithExprSimplifier exprSimplifier, boolean negateBothSides) {
        // lhs_coefs - rhs_coefs
        List<Coefficient> lhs = createCoefficients(exprSimplifier.getSummands());
        lhs = negateBothSides ? Util.neg(lhs) : lhs;

        // move constants to rhs or add: -(lhs_const - rhs_const)
        double rhs = negateBothSides ? exprSimplifier.getConstant() : -exprSimplifier.getConstant();


        Util.assertTrue(rhs >= 0);

        // bind f_i to helping variable r_k using additional helping variable r_l
        BinaryVar r_k = var;
        BinaryVar r_l = ilpModel.newBinaryVar();

        if (rhs == 0) {
            // (1-r_k) * M + f_1 - f_i + ... + f_n >= 0
            // - r_k * M + f_1 - f_i + ... + f_n >= -M
            List<Coefficient> summands = new ArrayList<>();
            summands.add(Util.coef(-ilpModel.getM(expr).get(), r_k));
            summands.addAll(lhs);

            ilpModel.newConstraint("geq_" + r_k.getName()).setExpr(Util.geq(summands, -ilpModel.getM(expr).get()));

            // f_1 - f_i + ... + f_n <= M * r_k - r_l
            // f_1 - f_i + ... + f_n - M r_k + r_l <= 0
            summands = new ArrayList<>();
            summands.addAll(lhs);
            summands.add(Util.coef(-ilpModel.getM(expr).get(), r_k));
            summands.add(Util.coef(1, r_l));

            ilpModel.newConstraint("geq_" + r_k.getName()).setExpr(Util.leq(summands, 0));

        } else {
            if (rhs == 1) {
                // 1 - r_l + M r_k >= f_i + ... + f_n
                List<Coefficient> summands = new ArrayList<>();
                summands.add(Util.coef(-1, r_l));
                summands.add(Util.coef(ilpModel.getM(expr).get(), r_k));
                summands.addAll(Util.neg(lhs));

                ilpModel.newConstraint("geq_" + r_k.getName()).setExpr(Util.geq(summands, -1));
            } else {
                // (rhs-1) * r_l + M * r_k >= f_i + ... + f_n
                List<Coefficient> summands = new ArrayList<>();
                summands.add(Util.coef(rhs - 1, r_l));
                summands.add(Util.coef(ilpModel.getM(expr).get(), r_k));
                summands.addAll(Util.neg(lhs));

                ilpModel.newConstraint("geq_" + r_k.getName()).setExpr(Util.geq(summands, 0));
            }


            // a >= b
            lhs.add(Util.coef(-rhs, r_k));

            ilpModel.newConstraint("geq_" + r_k.getName()).setExpr(Util.geq(lhs, 0));
        }

        // r_l + r_k = 1
        ilpModel.newConstraint("eq_" + r_k.getName()).setExpr(Util.eq(Arrays.asList(Util.coef(1, r_l), Util.coef(1, r_k)), 1));
    }

    private void addLeqCstrWithM(BinaryVar var, RelOp expr, ArithExprSimplifier exprSimplifier, boolean negateBothSides) {

        // lhs_coefs - rhs_coefs
        List<Coefficient> lhs = createCoefficients(exprSimplifier.getSummands());
        lhs = negateBothSides ? Util.neg(lhs) : lhs;


        // move constants to rhs or add: -(lhs_const - rhs_const)
        double rhs = negateBothSides ? exprSimplifier.getConstant() : -exprSimplifier.getConstant();

        Util.assertTrue(rhs >= 0);

        // bind f_i to helping variable r_k using additional helping variable r_l
        BinaryVar r_k = var;
        BinaryVar r_l = ilpModel.newBinaryVar();

        if (rhs == 0) {
            // f_i - ... + f_n <= 0 + M * (1-r_k)
            // f_i - ... + f_n + M * r_k <= M
            List<Coefficient> summands = new ArrayList<>();
            summands.addAll(lhs);
            summands.add(Util.coef(ilpModel.getM(expr).get(), r_k));

            ilpModel.newConstraint("leq_" + r_k.getName()).setExpr(Util.leq(summands, ilpModel.getM(expr).get()));

            // f_i - ... + f_n >= - M * r_k + r_l
            // f_i - ... + f_n + M * r_k - r_l >= 0
            summands = new ArrayList<>();
            summands.addAll(lhs);
            summands.add(Util.coef(ilpModel.getM(expr).get(), r_k));
            summands.add(Util.coef(-1, r_l));

            ilpModel.newConstraint("leq_" + r_k.getName()).setExpr(Util.geq(summands, 0));

        } else {
            // f_i + ... + f_n >= (rhs + 1) * r_l
            List<Coefficient> summands = new ArrayList<>();
            summands.add(Util.coef(-(rhs + 1), r_l));
            summands.addAll(lhs);

            ilpModel.newConstraint("leq_" + r_k.getName()).setExpr(Util.geq(summands, 0));

            // a <= b
            // f_i + ... + f_n <= rhs + M (1-r_k)
            // f_i + ... + f_n <= - M r_k + rhs + M
            // f_i + ... + f_n + M r_k <=  rhs + M
            lhs.add(Util.coef(ilpModel.getM(expr).get(), r_k));

            rhs = rhs + ilpModel.getM(expr).get();
            ilpModel.newConstraint("leq_" + r_k.getName()).setExpr(Util.leq(lhs, rhs));
        }

        // r_l + r_k = 1
        ilpModel.newConstraint("leq_" + r_k.getName()).setExpr(Util.eq(Arrays.asList(Util.coef(1, r_l), Util.coef(1, r_k)), 1));
    }


    private List<Coefficient> createCoefficients(Collection<Pair<Double, Var>> summands) {
        return summands.stream().map(s ->
                new Coefficient(p(s.getFirst()), s.getSecond())
        ).collect(Collectors.toList());
    }
}
