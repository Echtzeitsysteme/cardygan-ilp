package org.cardygan.ilp.internal.expr;

import org.cardygan.ilp.api.expr.Var;
import org.cardygan.ilp.api.expr.bool.RelOp;
import org.cardygan.ilp.api.expr.*;
import org.cardygan.ilp.internal.util.RandomString;
import org.cardygan.ilp.internal.util.Util;
import org.cardygan.ilp.internal.Coefficient;
import org.cardygan.ilp.internal.Pair;

import java.util.*;
import java.util.stream.Collectors;

import static org.cardygan.ilp.internal.util.Util.neg;
import static org.cardygan.ilp.api.util.ExprDsl.p;

public abstract class NormalizedArithExpr {

    protected final Param rhs;
    protected final List<Coefficient> coefficients;
    protected final String name;

    protected NormalizedArithExpr(RelOp relOp) {
        this(new RandomString(6).nextString(), relOp);
    }

    protected NormalizedArithExpr(String name, RelOp relOp) {
        this.name = name;

        ArithExprSimplifier exprSimplifier = new ArithExprSimplifier(new Sum(relOp.getLhs(), new Neg(relOp.getRhs())));
        double rhs = -exprSimplifier.getConstant();

        boolean negateBothSides = rhs < 0;

        // lhs_coefs - rhs_coefs
        List<Coefficient> lhs = createCoefficients(exprSimplifier.getSummands());
        lhs = negateBothSides ? Util.neg(lhs) : lhs;

        // move constants to rhs or add: -(lhs_const - rhs_const)
        rhs = negateBothSides ? exprSimplifier.getConstant() : -exprSimplifier.getConstant();

        Util.assertTrue(rhs >= 0);

        coefficients = consolidate(lhs);
        this.rhs = new DoubleParam(rhs);
    }


    private List<Coefficient> createCoefficients(Collection<Pair<Double, Var>> summands) {
        return summands.stream().map(s ->
                new Coefficient(p(s.getFirst()), s.getSecond())
        ).collect(Collectors.toList());
    }

    private List<Coefficient> consolidate(List<Coefficient> coefficients) {
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

    public String getName() {
        return name;
    }

    public Param getRhs() {
        return rhs;
    }

    public List<Coefficient> getCoefficients() {
        return coefficients;
    }

}
