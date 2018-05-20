package org.cardygan.ilp.api.util;

import org.cardygan.ilp.api.model.*;
import org.cardygan.ilp.api.model.bool.*;

import java.util.List;

public final class ExprDsl {

    public static And and(BoolExpr firstElem, BoolExpr... otherElems) {
        return new And(firstElem, otherElems);
    }

    public static And and(List<BoolExpr> elements) {
        return new And(elements);
    }

    public static Sum sum(ArithExpr... summands) {
        return new Sum(summands);
    }

    public static Sum sum(List<ArithExpr> summands) {
        return sum(summands.toArray(new ArithExpr[summands.size()]));
    }

    public static Param param(double val) {
        return new DoubleParam(val);
    }

    public static Param param(int val) {
        return new DoubleParam(val);
    }

    public static Mult mult(ArithUnaryExpr left, ArithUnaryExpr right) {
        return new Mult(left, right);
    }

    public static Neg neg(ArithExpr arithExpr) {
        return new Neg(arithExpr);
    }

    public static ArithExpr mult(int param, ArithUnaryExpr right) {
        return new Mult(param(param), right);
    }

    public static Eq eq(ArithExpr lhs, ArithExpr rhs) {
        return new Eq(lhs, rhs);
    }

    public static Leq leq(ArithExpr lhs, ArithExpr rhs) {
        return new Leq(lhs, rhs);
    }

    public static Geq geq(ArithExpr lhs, ArithExpr rhs) {
        return new Geq(lhs, rhs);
    }

    public static Or or(List<BoolExpr> e) {
        return new Or(e);
    }

    public static Or or(BoolExpr e1, BoolExpr... es) {
        return new Or(e1, es);
    }

    public static Xor xor(BoolExpr lhs, BoolExpr rhs) {
        return new Xor(lhs, rhs);
    }

    public static Not not(BoolExpr e) {
        return new Not(e);
    }

    public static BiImpl bi_impl(BoolExpr lhs, BoolExpr rhs) {
        return new BiImpl(lhs, rhs);
    }

    public static Impl impl(BoolExpr lhs, BoolExpr rhs) {
        return new Impl(lhs, rhs);
    }

    public static DoubleParam p(double val) {
        return new DoubleParam(val);
    }

}
