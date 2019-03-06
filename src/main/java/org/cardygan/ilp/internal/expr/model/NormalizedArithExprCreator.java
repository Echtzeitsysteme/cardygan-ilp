package org.cardygan.ilp.internal.expr.model;

import org.cardygan.ilp.api.model.*;
import org.cardygan.ilp.api.model.bool.Eq;
import org.cardygan.ilp.api.model.bool.Geq;
import org.cardygan.ilp.api.model.bool.Leq;
import org.cardygan.ilp.api.model.bool.RelOp;
import org.cardygan.ilp.internal.expr.ArithExprSimplifier;
import org.cardygan.ilp.internal.expr.Coefficient;
import org.cardygan.ilp.internal.util.Pair;
import org.cardygan.ilp.internal.util.Util;

import java.util.*;
import java.util.stream.Collectors;

import static org.cardygan.ilp.api.util.ExprDsl.p;

/**
 * Created by markus on 26.04.17.
 */
class NormalizedArithExprCreator {

    static NormalizedArithExpr createArithExpr(RelOp expr, Constraint c) {
        return create(expr, c);
    }

    private static NormalizedArithExpr create(RelOp relOp, Constraint c) {
        ArithExprSimplifier exprSimplifier = new ArithExprSimplifier(new Sum(relOp.getLhs(), new Neg(relOp.getRhs())));
        double rhs = -exprSimplifier.getConstant();

        boolean negateBothSides = rhs < 0;

        // lhs_coefs - rhs_coefs
        List<Coefficient> lhs = createCoefficients(exprSimplifier.getSummands());
        lhs = negateBothSides ? Util.neg(lhs) : lhs;

        // move constants to rhs or add: -(lhs_const - rhs_const)
        rhs = negateBothSides ? exprSimplifier.getConstant() : -exprSimplifier.getConstant();

        Util.assertTrue(rhs >= 0);

        List<Coefficient> coefficients = consolidate(lhs);
        Param rhsParam = new DoubleParam(rhs);

        if (relOp instanceof Leq && !negateBothSides || relOp instanceof Geq && negateBothSides) {
            if (c.getName().isPresent())
                return new LeqNormalizedArithExpr(c.getName().get(), relOp, rhsParam, coefficients);
            else
                return new LeqNormalizedArithExpr(relOp, rhsParam, coefficients);
        } else if (relOp instanceof Leq || relOp instanceof Geq) {
            if (c.getName().isPresent())
                return new GeqNormalizedArithExpr(c.getName().get(), relOp, rhsParam, coefficients);
            else
                return new GeqNormalizedArithExpr(relOp, rhsParam, coefficients);
        } else if (relOp instanceof Eq) {
            if (c.getName().isPresent())
                return new EqNormalizedArithExpr(c.getName().get(), relOp, rhsParam, coefficients);
            else
                return new EqNormalizedArithExpr(relOp, rhsParam, coefficients);
        } else {
            throw new IllegalStateException("Unknown RelOp type.");
        }
    }

    private static List<Coefficient> createCoefficients(Collection<Pair<Double, Var>> summands) {
        return summands.stream().map(s ->
                new Coefficient(p(s.getFirst()), s.getSecond())
        ).collect(Collectors.toList());
    }

    private static List<Coefficient> consolidate(List<Coefficient> coefficients) {
        Map<Var, Coefficient> ret = new HashMap<>(coefficients.size());
        for (Coefficient coef : coefficients) {
            if (ret.containsKey(coef.getVar())) {
                double curVal = ret.get(coef.getVar()).getParam().getVal();
                double newVal = curVal + coef.getParam().getVal();
                if (newVal == 0) {
                    ret.remove(coef.getVar());
                } else {
                    ret.put(coef.getVar(), new Coefficient(p(newVal), coef.getVar()));
                }
            } else {
                ret.put(coef.getVar(), coef);
            }
        }

        return new ArrayList<>(ret.values());
    }

}
