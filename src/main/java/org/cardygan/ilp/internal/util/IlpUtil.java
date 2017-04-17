package org.cardygan.ilp.internal.util;

import org.cardygan.ilp.api.BinaryVar;
import org.cardygan.ilp.api.IntVar;
import org.cardygan.ilp.api.expr.Var;

public final class IlpUtil {


    public static boolean isIntVar(Var var) {
        return var instanceof IntVar;
    }

    public static boolean isBinaryVar(Var var) {
        return var instanceof BinaryVar;
    }


}
