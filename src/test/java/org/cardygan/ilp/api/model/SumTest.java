package org.cardygan.ilp.api.model;

import org.junit.Test;

import static org.junit.Assert.*;
import static org.cardygan.ilp.api.util.ExprDsl.*;

public class SumTest {

    @Test
    public void testComplexConstrutor() {
        ArithExpr sut = sum(p(1), p(2), p(3), p(4));

        assertEquals(p(4), ((Sum)sut).getRhs());

        assertEquals(new Sum(new Sum(p(1), p(2)), p(3)), ((Sum)sut).getLhs());

//        assertEquals(sum(p(4)), sut.getLhs());
    }

}