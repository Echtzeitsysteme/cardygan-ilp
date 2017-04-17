package org.cardygan.ilp.internal.util;

import org.cardygan.ilp.api.BinaryVar;
import org.cardygan.ilp.api.IntVar;
import org.cardygan.ilp.api.expr.*;
import org.cardygan.ilp.api.expr.bool.Eq;
import org.cardygan.ilp.api.expr.bool.Geq;
import org.cardygan.ilp.api.expr.bool.Leq;
import org.cardygan.ilp.api.util.ExprDsl;
import org.cardygan.ilp.internal.Coefficient;

import java.util.List;
import java.util.stream.Collectors;

public final class Util {

    public static <T> T checkBinaryMultArg(T arg) {
        if (arg instanceof IntVar || arg instanceof IntVar) {
            throw new IllegalArgumentException("Only multiplication of binary variables supported.");
        }
        return arg;
    }


    public static void assertNotNull(Object... objs) {
        for (Object o : objs) {
            if (o == null) {
                throw new NullPointerException("Assertion failed, value is not allowed to be null.");
            }
        }
    }

    public static boolean isNull(Object obj) {
        return (obj == null);
    }

    public static boolean assertTrue(boolean expected) {
        if (expected) {
            return expected;
        }
        throw new IllegalStateException("Assertion failed.");
    }

    public static boolean isIntVar(Var var) {
        return var instanceof IntVar;
    }

    public static boolean isBinaryVar(Var var) {
        return var instanceof BinaryVar;
    }


    public static Geq geq(List<Coefficient> summands, double rhs) {
        List<ArithExpr> tmpList = summands.stream().map(c -> new Mult(c.getParam(), c.getVar())).collect(Collectors.toList());

        return ExprDsl.geq(ExprDsl.sum(tmpList.toArray(new ArithExpr[tmpList.size()])), ExprDsl.p(rhs));
    }

    public static Eq eq(List<Coefficient> summands, double rhs) {
        List<ArithExpr> tmpList = summands.stream().map(c -> new Mult(c.getParam(), c.getVar())).collect(Collectors.toList());

        return ExprDsl.eq(ExprDsl.sum(tmpList.toArray(new ArithExpr[tmpList.size()])), ExprDsl.p(rhs));
    }

    public static Leq leq(List<Coefficient> summands, double rhs) {
        List<ArithExpr> tmpList = summands.stream().map(c -> new Mult(c.getParam(), c.getVar())).collect(Collectors.toList());

        return ExprDsl.leq(ExprDsl.sum(tmpList.toArray(new ArithExpr[tmpList.size()])), ExprDsl.p(rhs));
    }

    public static List<Coefficient> neg(List<Coefficient> coefficients) {
        return coefficients.stream().map(c -> neg(c)).collect(Collectors.toList());
    }

    public static Coefficient neg(Coefficient coefficient) {
        return new Coefficient(ExprDsl.p(-coefficient.getParam().getVal()), coefficient.getVar());
    }

    public static Coefficient coef(int param, Var var) {
        return new Coefficient(ExprDsl.p(param), var);
    }

    public static Coefficient coef(Param param, Var var) {
        return new Coefficient(param, var);
    }

    public static Coefficient coef(double param, Var var) {
        return new Coefficient(ExprDsl.p(param), var);
    }
}
