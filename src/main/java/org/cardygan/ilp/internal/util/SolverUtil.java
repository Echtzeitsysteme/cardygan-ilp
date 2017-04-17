package org.cardygan.ilp.internal.util;


public class SolverUtil {

   private static double EPSILON = .00001;

    public static void assertIsInteger(double value) {
        if (!(Math.abs(Math.round(value) - value) < EPSILON)){
            throw new IllegalStateException("Could not round to integer.");
        }
    }
}
