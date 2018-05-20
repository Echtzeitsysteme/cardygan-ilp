package org.cardygan.ilp.internal.expr;


import org.cardygan.ilp.api.model.BinaryVar;
import org.cardygan.ilp.api.model.Model;
import org.cardygan.ilp.api.model.Var;
import org.cardygan.ilp.api.model.bool.Geq;
import org.cardygan.ilp.api.model.bool.Leq;
import org.cardygan.ilp.api.model.bool.RelOp;
import org.cardygan.ilp.internal.util.Pair;
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
                addGeqCstrWithM(var, ilpModel.getM(expr).get(), exprSimplifier, false);
            } else {
                addGeqCstrWithoutM(var, exprSimplifier, false);
            }

        } else if (expr instanceof Geq) {
            if (ilpModel.getM(expr).isPresent()) {
                addLeqCstrWithM(var, ilpModel.getM(expr).get(), exprSimplifier, true);
            } else {
                addLeqCstrWithoutM(var, exprSimplifier, true);
            }

        } else if (expr instanceof Leq && rhs >= 0) {
            if (ilpModel.getM(expr).isPresent()) {
                addLeqCstrWithM(var, ilpModel.getM(expr).get(), exprSimplifier, false);
            } else {
                addLeqCstrWithoutM(var, exprSimplifier, false);
            }

        } else if (expr instanceof Leq) {
            if (ilpModel.getM(expr).isPresent()) {
                addGeqCstrWithM(var, ilpModel.getM(expr).get(), exprSimplifier, true);
            } else {
                addGeqCstrWithoutM(var, exprSimplifier, true);
            }

        } else {
            throw new IllegalStateException("Unknown BoolRel subtype.");
        }

        processed.add(expr);

    }

    private void addLeqCstrWithoutM(BinaryVar var, ArithExprSimplifier exprSimplifier, boolean negateBothSides) {
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
        ilpModel.newConstraint("eq_" + r_k.getName(), Util.eq(Arrays.asList(Util.coef(1, r_l), Util.coef(1, r_k)), 1));

        Var s = ilpModel.newDoubleVar();
        List<Coefficient> summands = new ArrayList<>();
        summands.add(Util.coef(-1, s));
        summands.addAll(lhs);

        Set<Var> sos = new HashSet<>();
        sos.add(s);
        sos.add(r_k);
        addSos1(ilpModel, sos);

        ilpModel.newConstraint("leq1_" + r_k.getName(), Util.leq(summands, rhs));

        Var s_2 = ilpModel.newDoubleVar();
        summands = new ArrayList<>();
        summands.add(Util.coef(1, s_2));
        summands.addAll(lhs);

        Set<Var> sos2 = new HashSet<>();
        sos2.add(s_2);
        sos2.add(r_l);
        addSos1(ilpModel, sos2);

        ilpModel.newConstraint("leq2_" + r_k.getName(), Util.geq(summands, rhs < 1 ? rhs + Model.EPSILON : rhs + 1));
    }

    private void addGeqCstrWithoutM(BinaryVar var, ArithExprSimplifier exprSimplifier, boolean negateBothSides) {
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
        ilpModel.newConstraint("eq_" + r_k.getName(), Util.eq(Arrays.asList(Util.coef(1, r_l), Util.coef(1, r_k)), 1));

        Var s = ilpModel.newDoubleVar();
        List<Coefficient> summands = new ArrayList<>();
        summands.add(Util.coef(1, s));
        summands.addAll(lhs);

        Set<Var> sos = new HashSet<>();
        sos.add(s);
        sos.add(r_k);
        addSos1(ilpModel, sos);

        ilpModel.newConstraint("geq1_" + r_k.getName(), Util.geq(summands, rhs));

        Var s_2 = ilpModel.newDoubleVar();
        summands = new ArrayList<>();
        summands.add(Util.coef(-1, s_2));
        summands.addAll(lhs);

        Set<Var> sos2 = new HashSet<>();
        sos2.add(s_2);
        sos2.add(r_l);
        addSos1(ilpModel, sos2);


        ilpModel.newConstraint("geq2_" + r_k.getName(), Util.leq(summands, rhs - Model.EPSILON));

    }

    private List<Coefficient> createSosCoeff(Set<Var> sos) {
        List<Coefficient> coefficients = new ArrayList<>();
        for (Var var : sos) {
            coefficients.add(Util.coef(1, var));
        }
        return coefficients;
    }

    private void addGeqCstrWithM(BinaryVar var, int M, ArithExprSimplifier exprSimplifier, boolean negateBothSides) {

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
            summands.add(Util.coef(-M, r_k));
            summands.addAll(lhs);

            ilpModel.newConstraint("geq_" + r_k.getName(), Util.geq(summands, -M));

            // f_1 - f_i + ... + f_n <= M * r_k - r_l
            // f_1 - f_i + ... + f_n - M r_k + r_l <= 0
            summands = new ArrayList<>(lhs);
            summands.add(Util.coef(-M, r_k));
            summands.add(Util.coef(1, r_l));

            ilpModel.newConstraint("geq_" + r_k.getName(), Util.leq(summands, 0));

        } else {
            if (rhs == 1) {
                // 1 - r_l + M r_k >= f_i + ... + f_n
                List<Coefficient> summands = new ArrayList<>();
                summands.add(Util.coef(-1, r_l));
                summands.add(Util.coef(M, r_k));
                summands.addAll(Util.neg(lhs));

                ilpModel.newConstraint("geq_" + r_k.getName(), Util.geq(summands, -1));
            } else {
                // (rhs-1) * r_l + M * r_k >= f_i + ... + f_n
                List<Coefficient> summands = new ArrayList<>();
                summands.add(Util.coef(rhs - 1, r_l));
                summands.add(Util.coef(M, r_k));
                summands.addAll(Util.neg(lhs));

                ilpModel.newConstraint("geq_" + r_k.getName(), Util.geq(summands, 0));
            }


            // a >= b
            lhs.add(Util.coef(-rhs, r_k));

            ilpModel.newConstraint("geq_" + r_k.getName(), Util.geq(lhs, 0));
        }

        // r_l + r_k = 1
        ilpModel.newConstraint("eq_" + r_k.getName(), Util.eq(Arrays.asList(Util.coef(1, r_l), Util.coef(1, r_k)), 1));
    }

    private void addLeqCstrWithM(BinaryVar var, int M, ArithExprSimplifier exprSimplifier, boolean negateBothSides) {
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
            List<Coefficient> summands = new ArrayList<>(lhs);
            summands.add(Util.coef(M, r_k));

            ilpModel.newConstraint("leq_" + r_k.getName(), Util.leq(summands, M));

            // f_i - ... + f_n >= - M * r_k + r_l
            // f_i - ... + f_n + M * r_k - r_l >= 0
            summands = new ArrayList<>(lhs);
            summands.add(Util.coef(M, r_k));
            summands.add(Util.coef(-1, r_l));

            ilpModel.newConstraint("leq_" + r_k.getName(), Util.geq(summands, 0));

        } else {
            // f_i + ... + f_n >= (rhs + 1) * r_l
            List<Coefficient> summands = new ArrayList<>();
            summands.add(Util.coef(-(rhs + 1), r_l));
            summands.addAll(lhs);

            ilpModel.newConstraint("leq_" + r_k.getName(), Util.geq(summands, 0));

            // a <= b
            // f_i + ... + f_n <= rhs + M (1-r_k)
            // f_i + ... + f_n <= - M r_k + rhs + M
            // f_i + ... + f_n + M r_k <=  rhs + M
            lhs.add(Util.coef(M, r_k));

            rhs = rhs + M;
            ilpModel.newConstraint("leq_" + r_k.getName(), Util.leq(lhs, rhs));
        }

        // r_l + r_k = 1
        ilpModel.newConstraint("leq_" + r_k.getName(), Util.eq(Arrays.asList(Util.coef(1, r_l), Util.coef(1, r_k)), 1));
    }

    private void addSos1(Model ilp, Set<Var> vars) {

        ilp.newConstraint("sos", Util.geq(createSosCoeff(vars), Model.EPSILON));
        ilp.newSos1(vars);
    }


    private List<Coefficient> createCoefficients(Collection<Pair<Double, Var>> summands) {
        return summands.stream().map(s ->
                new Coefficient(p(s.getFirst()), s.getSecond())
        ).collect(Collectors.toList());
    }
}
