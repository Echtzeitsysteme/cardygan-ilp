package org.cardygan.ilp.internal.expr.cnf;

import org.cardygan.ilp.api.model.BinaryVar;
import org.cardygan.ilp.api.model.Model;
import org.cardygan.ilp.api.model.bool.BoolExpr;
import org.junit.Before;
import org.junit.Test;
import org.mockito.stubbing.Answer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static org.cardygan.ilp.api.util.ExprDsl.not;
import static org.cardygan.ilp.api.util.ExprDsl.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TseytinTransformerTest {


    private static Model model;
    private static Map<String, BinaryVar> binVarCache;

    private static BinaryVar v(String name) {
        if (!binVarCache.containsKey(name)) {
            binVarCache.put(name, model.newBinaryVar(name));
        }
        return binVarCache.get(name);
    }

    @Before
    public void setup() {
        binVarCache = new HashMap<>();
        model = mock(Model.class);

        when(model.newBinaryVar()).then((Answer<BinaryVar>) invocation -> mockBinVar());

        when(model.newBinaryVar(anyString())).thenAnswer((Answer<BinaryVar>) invocation -> {
            Object[] args = invocation.getArguments();
            String name = (String) args[0];

            return new BinaryVar(name);
        });
    }

    private AtomicInteger binVarCounter = new AtomicInteger();
    private final static String VAR_PREFIX = "v";

    private BinaryVar mockBinVar() {
        return new BinaryVar(VAR_PREFIX + binVarCounter.getAndIncrement());
    }

    @Test
    public void testSimpleAnd() {
        BoolExpr expr = and(v("a"), v("b"));
        TseytinTransformer sut = new TseytinTransformer();
        sut.computeCnf(expr, model);

        assertEquals(2, sut.getClauses().size());
        assertThat(sut.getVars().values(), hasItems(v("a"), v("b")));
    }

    @Test
    public void testSimpleAnd2() {
        BoolExpr expr = and(v("a"), v("b"), v("c"), v("d"), v("e"), v("f"));
        TseytinTransformer sut = new TseytinTransformer();
        sut.computeCnf(expr, model);

        assertEquals(6, sut.getClauses().size());
        assertThat(sut.getVars().values(), hasItems(v("a"), v("b"), v("c"), v("d"), v("e"), v("f")));
    }

    @Test
    public void testAndOr() {
        testExpr(or(and(v("a"), v("b")), and(v("c"), v("d"))));
    }

    @Test
    public void testAndOr2() {
        testExpr(or(and(v("a"), v("b")), and(v("c"), v("d"), v("e"), v("f")), or(v("g"), and(v("h"), v("i")), v("j"))));
    }

    @Test
    public void testAndOrNeg() {
        testExpr(not(or(and(v("a"), v("b")), not(and(v("c"), v("d"), not(v("e")), v("f"))),
                or(v("g"), and(v("h"), v("i")), v("j")))));
    }

    @Test
    public void testXorSimple() {
        setup();
        testExpr(xor(v("a"), v("b")));

        setup();
        testExpr(xor(v("a"), not(v("b"))));

        setup();
        testExpr(xor(not(v("a")), not(v("b"))));

        setup();
        testExpr(not(xor(not(v("a")), not(v("b")))));
    }

    @Test
    public void testAndOrXor() {
        testExpr(or(and(v("a"), v("b")), and(v("c"), xor(v("d"), v("e")), v("f")),
                or(v("g"), and(v("h"), xor(v("i"), v("j"))))));
    }

    @Test
    public void testAndXor() {
        testExpr(and(xor(v("a"), v("b")), xor(v("c"), xor(v("d"), v("e"))), v("f"),
                xor(v("g"), and(v("h"), xor(v("i"), v("j"))))));
    }

    @Test
    public void testSimpleImplication() {
        testExpr(impl(v("a"), v("b")));
    }

    @Test
    public void testAndXorImplication() {
        testExpr(impl(and(xor(v("a"), v("b")), xor(v("c"), xor(v("d"), v("e"))), v("f"),
                xor(v("g"), and(v("h"), xor(v("i"), v("j"))))), xor(v("k"), v("l"))));
    }

    @Test
    public void testSimpleBiImplication() {
        testExpr(bi_impl(v("a"), v("b")));
    }

    @Test
    public void testAndXorBiImplication() {
        testExpr(bi_impl(and(xor(v("a"), v("b")), xor(v("c"), xor(v("d"), v("e"))), v("f"),
                xor(v("g"), and(v("h"), xor(v("i"), v("j"))))), xor(v("k"), v("l"))));
    }

    private void testExpr(BoolExpr expr) {
        TseytinTransformer sut = new TseytinTransformer();
        sut.computeCnf(expr, model);

        List<BinaryVar> nonTmpVars = sut.getVars().values().stream()
                .filter(e -> !e.getName().startsWith(VAR_PREFIX)).collect(Collectors.toList());

        List<BinaryVar> tmpVars = sut.getVars().values().stream().filter(e -> e.getName().startsWith(VAR_PREFIX))
                .collect(Collectors.toList());

        int n = nonTmpVars.size();

        // System.out.println(visitor.getClauses());

        for (int i = 0; i < Math.pow(2, n); i++) {

            Map<BinaryVar, Boolean> vals = getPermutation(nonTmpVars, i);

            boolean expected = expr.accept(new EvalVisitor(vals));
            // System.out.println("--------------");
            // System.out.println("CHECK COMBINATION " + expected + ": " +
            // vals);

            int countMatches = 0;
            for (int j = 0; j < Math.pow(2, tmpVars.size()); j++) {
                Map<BinaryVar, Boolean> tmpVals = getPermutation(tmpVars, j);
                Map<BinaryVar, Boolean> mergedVals = new HashMap<>();
                mergedVals.putAll(tmpVals);
                mergedVals.putAll(vals);
                // System.out.println("---------- NEW CHECK TO MATCH -------");
                // System.out.println("Check: " + tmpVals);

                if (evalClauses(sut.getClauses(), mergedVals)) {
                    // System.out.println("Matched: " + mergedVals);
                    countMatches++;
                }
            }

            if (!expected) {
                // there exists no combination of tmpVars such that expression
                // is true
                assertThat("Transformation not equivalent", countMatches, equalTo(0));
            } else {
                // there exists at least one combination of tmpVars such that
                // expression is true
                assertThat("Transformation not equivalent", countMatches, greaterThanOrEqualTo(1));
            }
        }

    }

    private Map<BinaryVar, Boolean> getPermutation(List<BinaryVar> vars, int permCount) {
        int n = vars.size();
        StringBuilder bin = new StringBuilder(Integer.toBinaryString(permCount));
        while (bin.length() < n) {
            bin.insert(0, "0");
        }
        char[] chars = bin.toString().toCharArray();
        boolean[] boolArray = new boolean[n];
        for (int j = 0; j < chars.length; j++) {
            boolArray[j] = chars[j] == '0';
        }
        Map<BinaryVar, Boolean> vals = new HashMap<>(n);
        int j = 0;
        for (BinaryVar var : vars) {
            vals.put(var, boolArray[j]);
            j++;
        }
        return vals;
    }

    private boolean evalClauses(List<CnfClause> clauses, Map<BinaryVar, Boolean> vals) {
        return clauses.stream().allMatch(c -> evalClause(c, vals));
    }

    private boolean evalClause(CnfClause clause, Map<BinaryVar, Boolean> vals) {
        // System.out.println("Match clause?: " + clause + " ---> " +
        // clause.getIlpVars().keySet().stream().anyMatch(
        // var -> vals.get(var) && !clause.getIlpVars().get(var) || !vals.get(var)
        // && clause.getIlpVars().get(var))
        // + " WITH VALS " + vals);
        return clause.getVars().keySet().stream().anyMatch(
                var -> vals.get(var) && !clause.getVars().get(var) || !vals.get(var) && clause.getVars().get(var));
    }

}
