package org.cardygan.ilp.internal.util;

import com.google.gson.*;
import org.cardygan.ilp.api.model.*;
import org.cardygan.ilp.api.model.bool.And;
import org.cardygan.ilp.api.model.bool.BiImpl;
import org.cardygan.ilp.api.model.bool.BoolExpr;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

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
