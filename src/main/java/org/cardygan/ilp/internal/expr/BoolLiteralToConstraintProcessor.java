package org.cardygan.ilp.internal.expr;


import org.cardygan.ilp.api.Model;
import org.cardygan.ilp.api.expr.bool.Geq;
import org.cardygan.ilp.api.expr.bool.Leq;
import org.cardygan.ilp.api.expr.bool.RelOp;
import org.cardygan.ilp.api.expr.Var;
import org.cardygan.ilp.api.BinaryVar;
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

            addGeqCstr(var, expr, exprSimplifier, false);

        } else if (expr instanceof Geq && rhs < 0) {

            addLeqCstr(var, expr, exprSimplifier, true);

        } else if (expr instanceof Leq && rhs >= 0) {

            addLeqCstr(var, expr, exprSimplifier, false);

        } else if (expr instanceof Leq && rhs < 0) {

            addGeqCstr(var, expr, exprSimplifier, true);

        } else {
            throw new IllegalStateException("Unknown BoolRel subtype.");
        }

        processed.add(expr);
    }

    private void addGeqCstr(BinaryVar var, RelOp expr, ArithExprSimplifier exprSimplifier, boolean negateBothSides) {
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
            summands.add(Util.coef(-ilpModel.getM(expr), r_k));
            summands.addAll(lhs);

//            ilpModel.add(cstr("geq_" + r_k.getName()).sum(summands).geq(-ilpModel.getM(expr)));
            ilpModel.newConstraint("geq_" + r_k.getName()).setExpr(Util.geq(summands, -ilpModel.getM(expr)));

            // f_1 - f_i + ... + f_n <= M * r_k - r_l
            // f_1 - f_i + ... + f_n - M r_k + r_l <= 0
            summands = new ArrayList<>();
            summands.addAll(lhs);
            summands.add(Util.coef(-ilpModel.getM(expr), r_k));
            summands.add(Util.coef(1, r_l));

//            ilpModel.add(cstr("geq_" + r_k.getName()).sum(summands).leq(0));
            ilpModel.newConstraint("geq_" + r_k.getName()).setExpr(Util.geq(summands, 0));

        } else {
            if (rhs == 1) {
                // 1 - r_l + M r_k >= f_i + ... + f_n
                List<Coefficient> summands = new ArrayList<>();
                summands.add(Util.coef(-1, r_l));
                summands.add(Util.coef(ilpModel.getM(expr), r_k));
                summands.addAll(Util.neg(lhs));

//                ilpModel.add(cstr("geq_" + r_k.getName()).sum(summands).geq(-1));
                ilpModel.newConstraint("geq_" + r_k.getName()).setExpr(Util.geq(summands, -1));
            } else {
                // (rhs-1) * r_l + M * r_k >= f_i + ... + f_n
                List<Coefficient> summands = new ArrayList<>();
                summands.add(Util.coef(rhs - 1, r_l));
                summands.add(Util.coef(ilpModel.getM(expr), r_k));
                summands.addAll(Util.neg(lhs));

//                ilpModel.add(cstr("geq_" + r_k.getName()).sum(summands).geq(0));
                ilpModel.newConstraint("geq_" + r_k.getName()).setExpr(Util.geq(summands, 0));
            }


            // a >= b
            lhs.add(Util.coef(-rhs, var));

//            ilpModel.add(cstr("geq_" + r_k.getName()).sum(lhs).geq(0));
            ilpModel.newConstraint("geq_" + r_k.getName()).setExpr(Util.geq(lhs, 0));
        }

        // r_l + r_k = 1
//        ilpModel.add(cstr("geq_" + r_k.getName()).sum(coef(1, r_l), coef(1, r_k)).eq(1));
        ilpModel.newConstraint("eq_" + r_k.getName()).setExpr(Util.eq(Arrays.asList(Util.coef(1, r_l), Util.coef(1, r_k)), 1));
    }

    private void addLeqCstr(BinaryVar var, RelOp expr, ArithExprSimplifier exprSimplifier, boolean negateBothSides) {

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
            summands.add(Util.coef(ilpModel.getM(expr), r_k));

//            ilpModel.add(cstr("leq_" + r_k.getName()).sum(summands).leq(ilpModel.getM(expr)));
            ilpModel.newConstraint("leq_" + r_k.getName()).setExpr(Util.leq(summands, ilpModel.getM(expr)));

            // f_i - ... + f_n >= - M * r_k + r_l
            // f_i - ... + f_n + M * r_k - r_l >= 0
            summands = new ArrayList<>();
            summands.addAll(lhs);
            summands.add(Util.coef(ilpModel.getM(expr), r_k));
            summands.add(Util.coef(-1, r_l));

//            ilpModel.add(cstr("leq_" + r_k.getName()).sum(summands).geq(0));
            ilpModel.newConstraint("leq_" + r_k.getName()).setExpr(Util.geq(summands, 0));

        } else {
            // f_i + ... + f_n >= (rhs + 1) * r_l
            List<Coefficient> summands = new ArrayList<>();
            summands.add(Util.coef(-(rhs + 1), r_l));
            summands.addAll(lhs);

//            ilpModel.add(cstr("leq_" + r_k.getName()).sum(summands).geq(0));
            ilpModel.newConstraint("leq_" + r_k.getName()).setExpr(Util.geq(summands, 0));

            // a <= b
            lhs.add(Util.coef(ilpModel.getM(expr), var));

            rhs = rhs + ilpModel.getM(expr);
//            ilpModel.add(cstr("leq_" + r_k.getName()).sum(lhs).leq(rhs));
            ilpModel.newConstraint("leq_" + r_k.getName()).setExpr(Util.leq(lhs, rhs));
        }

        // r_l + r_k = 1
//        ilpModel.add(cstr("leq_" + r_k.getName()).sum(coef(1, r_l), coef(1, r_k)).eq(1));
        ilpModel.newConstraint("leq_" + r_k.getName()).setExpr(Util.eq(Arrays.asList(Util.coef(1, r_l), Util.coef(1, r_k)), 1));
    }


    private List<Coefficient> createCoefficients(Collection<Pair<Double, Var>> summands) {
        return summands.stream().map(s ->
                new Coefficient(p(s.getFirst()), s.getSecond())
        ).collect(Collectors.toList());
    }
}
