package org.cardygan.ilp.internal.persist;

import org.cardygan.ilp.api.model.*;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static junit.framework.TestCase.assertEquals;
import static org.cardygan.ilp.api.util.ExprDsl.*;

public class ModelWriterTest {

    @Test
    public void writeToString() {

        Model model = new Model();

        BinaryVar v1 = model.newBinaryVar("v1");
        IntVar v2 = model.newIntVar("v2");
        IntVar v3 = model.newIntVar("v3", -2);
        IntVar v4 = model.newIntVar("v4", 2, -1);
        IntVar v5 = model.newIntVar("v5", 2, 10);
        DoubleVar v6 = model.newDoubleVar("v6");
        DoubleVar v7 = model.newDoubleVar("v7", -2);
        DoubleVar v8 = model.newDoubleVar("v8", 2, -1);
        DoubleVar v9 = model.newDoubleVar("v9", 2, 10);

        model.newObjective(true, sum(v1, v2, v3, v4, v5, v6, v7, mult(p(2), v8), mult(p(2), v9)));

        model.newConstraint(and(or(v1, leq(v6, v7)), impl(eq(v8, v9), geq(v5, v6)), bi_impl(leq(v8, v9), eq(v6, v7)),
                and(xor(v1, eq(v3, v5)), leq(v2, v3))));
        model.newConstraint(or(eq(v3, v4), leq(v5, v6)));

        model.newConstraint(leq(mult(param(3), v3), v9));
        model.newConstraint(geq(mult(param(-10.3034), v3), sum(v4, neg(sum(v5, v6)))));

        Map<Var, Double> sos = new HashMap<>();
        sos.put(v2, 1.3);
        sos.put(v3, 1.4);
        sos.put(v4, 1.9);

        model.newSos1(sos);

        final String ret = ModelWriter.writeToString(model);

        System.out.println(ret);

        final Model sut = ModelReader.readFromString(ret);

        assertEquals(model,sut);
    }
}