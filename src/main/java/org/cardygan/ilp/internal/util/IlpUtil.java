package org.cardygan.ilp.internal.util;

import org.cardygan.ilp.api.model.BinaryVar;
import org.cardygan.ilp.api.model.DoubleVar;
import org.cardygan.ilp.api.model.IntVar;
import org.cardygan.ilp.api.model.Var;

public final class IlpUtil {


    public static boolean isIntVar(Var var) {
        return var instanceof IntVar;
    }

    public static boolean isBinaryVar(Var var) {
        return var instanceof BinaryVar;
    }


    public static boolean isDoubleVar(Var var) {
        return var instanceof DoubleVar;
    }
}
