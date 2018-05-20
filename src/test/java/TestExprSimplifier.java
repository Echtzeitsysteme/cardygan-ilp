import org.cardygan.ilp.api.model.BinaryVar;
import org.cardygan.ilp.api.model.IntVar;
import org.cardygan.ilp.api.model.Model;
import org.cardygan.ilp.api.model.ArithExpr;
import org.cardygan.ilp.internal.util.Pair;
import org.cardygan.ilp.internal.expr.ArithExprSimplifier;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.cardygan.ilp.api.util.ExprDsl.*;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;


@SuppressWarnings("unchecked")
public class TestExprSimplifier {

    private static Model model;
    private static Map<String, IntVar> intVarCache;
    private static Map<String, BinaryVar> binVarCache;

    private static IntVar intVar(String name) {
        if (!intVarCache.containsKey(name)) {
            intVarCache.put(name, model.newIntVar(name));
        }
        return intVarCache.get(name);
    }

    private static BinaryVar binVar(String name) {
        if (!binVarCache.containsKey(name)) {
            binVarCache.put(name, model.newBinaryVar(name));
        }
        return binVarCache.get(name);
    }

    @Before
    public void setup() {
        intVarCache = new HashMap<>();
        binVarCache = new HashMap<>();
        model = new Model();
    }

    /**
     * a + 2 + 2b + 3c
     */
    @Test
    public void test_addition() {
        ArithExprSimplifier sut = createSut(sum(intVar("a"), param(2), mult(2, intVar("b")), mult(intVar("c"), param(3))));

        assertThat(sut.getSummands(),
                containsInAnyOrder(new Pair<>(1d, intVar("a")),
                        new Pair<>(2d, intVar("b")), new Pair<>(3d, intVar("c"))));
    }

    /**
     * -(-(-1*b+c)) = -1*b + 1*c
     */
    @Test
    public void test_neg1() {
        ArithExprSimplifier sut = createSut(sum(neg(neg(sum(mult(param(-1), intVar("b")), intVar("c"))))));
        assertThat(sut.getSummands(), containsInAnyOrder(new Pair<>(-1d, intVar("b")), new Pair<>(1d, intVar("c"))));
    }

    /**
     * -(3 + 3*-4 - (8 + -(a + -(2 b - 2)))) <br/>
     * = -3 + 12 +8 - a + 2b - 2 = 15 - a + 2b
     */
    @Test
    public void test_neg2() {
        ArithExprSimplifier sut = createSut(neg(sum(param(3), mult(param(3), param(-4)),
                neg(sum(param(8), neg(sum(intVar("a"), neg(sum(mult(2, intVar("b")), neg(param(2)))))))))));
        assertThat(sut.getSummands(), containsInAnyOrder(new Pair<>(-1d, intVar("a")), new Pair<>(2d, intVar("b"))));
        assertThat(sut.getConstant(), is(15d));
    }

    /**
     * -(-(-1*b-c)) = -1*b - 1*c
     */
    @Test
    public void test_neg3() {
        ArithExprSimplifier sut = createSut(sum(neg(neg(sum(mult(param(-1), intVar("b")), neg(intVar("c")))))));
        assertThat(sut.getSummands(), containsInAnyOrder(new Pair<>(-1d, intVar("b")), new Pair<>(-1d, intVar("c"))));
    }

    /**
     * 3b + 2b - 2b + 2a - a + d - d = 3b + a
     */
    @Test
    public void test_combination() {
        ArithExprSimplifier sut = createSut(sum(mult(3, intVar("b")), mult(2, intVar("b")), mult(-2, intVar("b")),
                mult(2, intVar("a")), neg(intVar("a")), intVar("d"), neg(intVar("d"))));

        assertThat(sut.getSummands(), containsInAnyOrder(new Pair<>(3d, intVar("b")), new Pair<>(1d, intVar("a"))));
    }

    /**
     * v1 + v2
     */
    @Test
    public void test_simpleSum() {
        ArithExprSimplifier sut = createSut(sum(binVar("v1"), binVar("v2")));

        assertThat(sut.getSummands(), containsInAnyOrder(new Pair<>(1d, binVar("v1")), new Pair<>(1d, binVar("v2"))));
    }

    private ArithExprSimplifier createSut(ArithExpr arithExpr) {
        return new ArithExprSimplifier(arithExpr);
    }

}
