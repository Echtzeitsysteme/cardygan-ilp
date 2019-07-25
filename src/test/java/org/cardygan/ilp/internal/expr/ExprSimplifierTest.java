package org.cardygan.ilp.internal.expr;

import org.cardygan.ilp.api.model.IntVar;
import org.cardygan.ilp.api.model.Var;
import org.cardygan.ilp.api.model.bool.BoolExpr;
import org.cardygan.ilp.api.model.bool.RelOp;
import org.cardygan.ilp.internal.util.Pair;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.cardygan.ilp.api.util.ExprDsl.*;
import static org.cardygan.ilp.internal.expr.ExprSimplifier.simplify;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.junit.Assert.*;

public class ExprSimplifierTest {

    @Test
    public void simplifyRelOp() {
        ExprSimplifier.SimplifiedRelOp res = simplify(geq(v("a"), v("b")));

        List<Pair<Var, Double>> coeffs = getCoeffs(res);

        assertThat(coeffs, hasItem(new Pair<>(v("a"), 1d)));
        assertThat(coeffs, hasItem(new Pair<>(v("b"), -1d)));
        assertEquals(2, coeffs.size());
    }

    @Test
    public void simplifyRelOp2() {
        ExprSimplifier.SimplifiedRelOp res = simplify(leq(v("a"), v("b")));

        List<Pair<Var, Double>> coeffs = getCoeffs(res);

        assertThat(coeffs, hasItem(new Pair<>(v("a"), 1d)));
        assertThat(coeffs, hasItem(new Pair<>(v("b"), -1d)));
        assertEquals(2, coeffs.size());
    }

    @Test
    public void simplifyRelOp3() {
        ExprSimplifier.SimplifiedRelOp res = simplify(eq(v("a"), v("b")));

        List<Pair<Var, Double>> coeffs = getCoeffs(res);

        assertThat(coeffs, hasItem(new Pair<>(v("a"), 1d)));
        assertThat(coeffs, hasItem(new Pair<>(v("b"), -1d)));
        assertEquals(2, coeffs.size());
    }

    @Test
    public void simplifyRelOp4() {
        ExprSimplifier.SimplifiedRelOp res = simplify(eq(v("a"),
                sum(mult(p(-3), v("a")), v("b"))));

        List<Pair<Var, Double>> coeffs = getCoeffs(res);

        assertThat(coeffs, hasItem(new Pair<>(v("a"), 4d)));
        assertThat(coeffs, hasItem(new Pair<>(v("b"), -1d)));
        assertEquals(2, coeffs.size());
    }

    @Test
    public void simplifyRelOp5() {
        ExprSimplifier.SimplifiedRelOp res = simplify(eq(sum(v("a"), p(-3)),
                sum(p(39), mult(p(-3), v("a")), v("b"))));

        List<Pair<Var, Double>> coeffs = getCoeffs(res);

        assertThat(coeffs, hasItem(new Pair<>(v("a"), 4d)));
        assertThat(coeffs, hasItem(new Pair<>(v("b"), -1d)));
        assertEquals(42, res.getRhs(), 0);
        assertEquals(2, coeffs.size());
    }


    private static List<Pair<Var, Double>> getCoeffs(ExprSimplifier.SimplifiedRelOp res) {
        return zip(Arrays.asList(res.getVars()),
                Arrays.stream(res.getParams()).boxed().collect(Collectors.toList()));
    }

    private static <A, B> List<Pair<A, B>> zip(List<A> as, List<B> bs) {
        return IntStream.range(0, Math.min(as.size(), bs.size()))
                .mapToObj(i -> new Pair<>(as.get(i), bs.get(i)))
                .collect(Collectors.toList());
    }

    public static <A, B> List<Pair<A, B>> zip(A[] as, B[] bs) {
        return zip(Arrays.asList(as), Arrays.asList(bs));
    }


    private static double[] dblArrayOf(double... elems) {
        return elems;
    }

    private static <T> T[] arrayOf(T... elems) {
        return elems;
    }

    @Test
    public void simplifyArithExpr() {
        ExprSimplifier.SimplifiedArithExpr res = simplify(sum(v("a"), mult(p(2), v("a"))));
        Map<Var,Double> coeffs = res.getCoeffs();
        assertEquals(1, coeffs.size());
        assertEquals(3, coeffs.get(v("a")), 0);
        assertEquals(0, res.getConstant(), 0);
    }

    @Test
    public void simplifyArithExpr2() {
        ExprSimplifier.SimplifiedArithExpr res = simplify(sum(v("a"), mult(p(-2), v("a"))));
        Map<Var,Double> coeffs = res.getCoeffs();
        assertEquals(1, coeffs.size());
        assertEquals(-1, coeffs.get(v("a")), 0);
        assertEquals(0, res.getConstant(), 0);
    }

    @Test
    public void simplifyArithExpr3() {
        ExprSimplifier.SimplifiedArithExpr res = simplify(sum(v("a"), sum(mult(p(-2), v("a")),
                sum(p(2), mult(p(2), v("b"))), v("a"))));
        Map<Var,Double> coeffs = res.getCoeffs();
        assertEquals(1, coeffs.size());
        assertEquals(2, coeffs.get(v("b")), 0);
        assertEquals(2, res.getConstant(), 0);
    }

    @Test
    public void simplifyArithExpr4() {
        ExprSimplifier.SimplifiedArithExpr res = simplify(sum(v("a"), sum(neg(mult(p(-2), v("a"))),
                sum(p(2), mult(p(2), v("b"))), v("a"))));
        Map<Var,Double> coeffs = res.getCoeffs();
        assertEquals(2, coeffs.size());
        assertEquals(4, coeffs.get(v("a")), 0);
        assertEquals(2, coeffs.get(v("b")), 0);
        assertEquals(2, res.getConstant(), 0);
    }

    @Test
    public void simplifyArithExpr5() {
        ExprSimplifier.SimplifiedArithExpr res = simplify(neg(sum(v("a"), sum(neg(mult(p(-2), v("a")))),
                sum(p(2), mult(p(2), v("b"))), v("a"))));
        Map<Var,Double> coeffs = res.getCoeffs();
        assertEquals(2, coeffs.size());
        assertEquals(-4, coeffs.get(v("a")), 0);
        assertEquals(-2, coeffs.get(v("b")), 0);
        assertEquals(-2, res.getConstant(), 0);
    }

    @Test
    public void simplifyArithExpr6() {
        ExprSimplifier.SimplifiedArithExpr res = simplify(sum(v("a"), mult(p(2), p(2.5))));
        Map<Var,Double> coeffs = res.getCoeffs();
        assertEquals(1, coeffs.size());
        assertEquals(1, coeffs.get(v("a")), 0);
        assertEquals(5, res.getConstant(), 0);
    }

    @Test
    public void simplifyArithExpr7() {
        ExprSimplifier.SimplifiedArithExpr res = simplify(sum(v("a"), mult(v("a"), p(2.5))));
        Map<Var,Double> coeffs = res.getCoeffs();
        assertEquals(1, coeffs.size());
        assertEquals(3.5, coeffs.get(v("a")), 0);
        assertEquals(0, res.getConstant(), 0);
    }

    @Test(expected = IllegalStateException.class)
    public void simplifyArithExpr8() {
        simplify(sum(v("a"), mult(v("a"), v("a"))));
    }

    @Test
    public void simplifyArithExpr9() {
        ExprSimplifier.SimplifiedArithExpr res = simplify(sum(v("a"), mult(p(-1), v("a"))));
        Map<Var,Double> coeffs = res.getCoeffs();
        assertEquals(0, coeffs.size());
        assertEquals(0, res.getConstant(), 0);
    }

    @Test
    public void toNAryAnd() {
        List<BoolExpr> and = ExprSimplifier.toNAryAnd(and(
                and(eq(v("a"), v("b")), eq(v("a"), v("b"))
                        , eq(v("a"), v("b"))), eq(v("a"), v("b"))));
        assertTrue(and.stream().allMatch(it -> it.equals(eq(v("a"), v("b")))));
        assertEquals(4, and.size());
    }

    @Test
    public void collectConjunctiveRelops() {
        List<RelOp> sut = ExprSimplifier.collectConjunctiveRelops(and(
                and(geq(v("a"), p(2)), leq(v("b"), p(2))),
                eq(v("a"), p(2))));
        assertEquals(3, sut.size());

        List<RelOp> sut2 = ExprSimplifier.collectConjunctiveRelops(and(
                and(geq(v("a"), p(2)), leq(v("b"), p(2))),
                eq(v("a"), p(2)),
                or(eq(v("a"), p(3)), leq(v("c"), p(3)))));
        assertEquals(0, sut2.size());
    }

    private static IntVar v(String name) {
        return new IntVar(name);
    }


}