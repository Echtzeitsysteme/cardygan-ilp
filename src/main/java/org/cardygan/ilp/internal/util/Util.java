package org.cardygan.ilp.internal.util;

import org.cardygan.ilp.api.model.IntVar;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public final class Util {

    public static <T> T checkBinaryMultArg(T arg) {
        if (arg instanceof IntVar) {
            throw new IllegalArgumentException("Only multiplication of binary variables supported.");
        }
        return arg;
    }

    @SafeVarargs
    public static <T> List<T> listOf(List<T>... lists) {
        return Arrays.stream(lists).flatMap(Collection::stream).collect(Collectors.toList());
    }

    @SafeVarargs
    public static <T> List<T> listOf(T... elems) {
        return Arrays.stream(elems).collect(Collectors.toList());
    }

    public static boolean isInteger(double val) {
        return val == (int) val;
    }


    public static void assertNotNull(Object... objs) {
        for (Object o : objs) {
            if (o == null) {
                throw new NullPointerException("Assertion failed, value is not allowed to be null.");
            }
        }
    }

    public static boolean assertTrue(boolean expected) {
        if (expected) {
            return expected;
        }
        throw new IllegalStateException("Assertion failed.");
    }

}
