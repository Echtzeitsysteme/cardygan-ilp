package org.cardygan.ilp;

public final class IlpUtil {

    public static <T> T checkBinaryMultArg(T arg) {
        if (arg instanceof IlpIntVar || arg instanceof IlpIntVar) {
            throw new IllegalArgumentException("Only multiplication of binary variables supported.");
        }
        return arg;
    }

    public static boolean isIntVar(IlpVar var) {
        return var instanceof IlpIntVar;
    }

    public static boolean isBinaryVar(IlpVar var) {
        return var instanceof IlpBinaryVar;
    }



}
