package org.cardygan.ilp.internal.expr;

import org.cardygan.ilp.api.expr.*;
import org.cardygan.ilp.api.expr.bool.Eq;
import org.cardygan.ilp.api.expr.bool.Geq;
import org.cardygan.ilp.api.expr.bool.Leq;
import org.cardygan.ilp.api.expr.bool.RelOp;
import org.cardygan.ilp.internal.Coefficient;
import org.cardygan.ilp.internal.Pair;
import org.cardygan.ilp.internal.util.Util;

import java.util.*;
import java.util.stream.Collectors;

import static org.cardygan.ilp.api.util.ExprDsl.p;

/**
 * Created by markus on 26.04.17.
 */
public class NormalizedArithExprCreator {

    public static NormalizedArithExpr createArithExpr(String name, RelOp expr) {
        return create(name, expr);
    }

    private static NormalizedArithExpr create(String name, RelOp relOp) {
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
            return new LeqNormalizedArithExpr(name, relOp, rhsParam, coefficients);
        } else if (relOp instanceof Leq && negateBothSides || relOp instanceof Geq && !negateBothSides) {
            return new GeqNormalizedArithExpr(name, relOp, rhsParam, coefficients);
        } else if (relOp instanceof Eq) {
            return new EqNormalizedArithExpr(name, relOp, rhsParam, coefficients);
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
        Map<Var, Coefficient> ret = new HashMap<>();
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
