package org.cardygan.ilp.api.util;

import org.cardygan.ilp.api.model.*;
import org.cardygan.ilp.api.model.bool.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class ExprDsl {

//    /**
//     * @param firstElem
//     * @param otherElems
//     * @return
//     * @deprecated <p> Use {@link ExprDsl#and(BoolExpr...)}  instead.
//     */
//    @Deprecated
//    public static And and(BoolExpr firstElem, BoolExpr... otherElems) {
//        List<BoolExpr> ret = new ArrayList<>(otherElems.length + 1);
//        ret.add(firstElem);
//        ret.addAll(Arrays.asList(otherElems));
//        return new And(ret);
//    }

    public static And and(BoolExpr... elems) {
        return new And(elems);
    }

    public static And and(List<BoolExpr> elements) {
        return new And(elements);
    }

    public static ArithExpr sum(ArithExpr... summands) {
        return sum(Arrays.asList(summands));
    }

    public static ArithExpr sum(List<ArithExpr> summands) {
        if (summands.size() == 0)
            throw new IllegalArgumentException("At least one summand needs to be passed as parameter");
        if (summands.size() == 1)
            return summands.get(0);
        return new Sum(summands);
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

//    /**
//     * @param firstElem
//     * @param otherElems
//     * @return
//     * @deprecated <p> Use {@link ExprDsl#or(BoolExpr...)}  instead.
//     */
//    @Deprecated
//    public static Or or(BoolExpr firstElem, BoolExpr... otherElems) {
//        List<BoolExpr> ret = new ArrayList<>(otherElems.length + 1);
//        ret.add(firstElem);
//        ret.addAll(Arrays.asList(otherElems));
//        return new Or(ret);
//    }

    public static Or or(BoolExpr... elems) {
        return new Or(elems);
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
