package org.cardygan.ilp.internal.util;

import com.google.gson.*;
import com.google.gson.typeadapters.RuntimeTypeAdapterFactory;
import org.cardygan.ilp.api.model.BinaryVar;
import org.cardygan.ilp.api.model.Model;
import org.cardygan.ilp.api.model.bool.And;
import org.cardygan.ilp.api.model.bool.BoolExpr;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class IlpSerializer {

//    public static String persistToJson(Model model) {
//        GsonBuilder gsonBuilder = new GsonBuilder();
//
//        JsonSerializer<BoolExpr> serializer = (src, typeOfSrc, context) -> {
//            JsonElement e = context.serialize(src);
//            if (e.isJsonObject()) {
//                e.getAsJsonObject().addProperty("type", src.getClass().getSimpleName());
//            }
//            return e;
//        };
//
//        gsonBuilder.registerTypeAdapter(BoolExpr.class, serializer);
//        gsonBuilder.setPrettyPrinting();
//        return gsonBuilder.create().toJson(model);
//    }
//
//    public static Model readFromJson(String jsonString) {
//        Gson gson = new Gson();
//
//        GsonBuilder gsonBuilder = new GsonBuilder();
//
//        RuntimeTypeAdapterFactory<BoolExprAdapter> boolAdapterFactory = RuntimeTypeAdapterFactory.of(BoolExprAdapter.class, "type")
//                .registerSubtype(AndAdapter.class, "And")
//                .registerSubtype(OrAdapter.class, "Or")
//                .registerSubtype(EqAdapter.class, "Eq")
//                .registerSubtype(LeqAdapter.class, "Leq")
//                .registerSubtype(GeqAdapter.class, "Geq")
//                .registerSubtype(ImplAdapter.class, "Impl")
//                .registerSubtype(BiImplAdapter.class, "BiImpl")
//                .registerSubtype(XorAdapter.class, "Xor")
//                .registerSubtype(BinaryVarAdapter.class, "BinaryVar");
//
//        gsonBuilder.registerTypeAdapterFactory(boolAdapterFactory);
//
//
//
//
//        JsonDeserializer<BoolExpr> deserializer = (src, typeOfSrc, context) -> {
//            JsonObject obj = src.getAsJsonObject();
//            String type = obj.get("type").getAsString();
//
//            if (type.equals(And.class.getSimpleName())) {
//                JsonArray arr = obj.get("elements").getAsJsonArray();
//                List<BoolExpr> elems = new ArrayList<>();
//                arr.forEach(e -> elems.add(context.deserialize(e, BoolExpr.class)));
//                return new And(elems);
//            }
//            if (type.equals(BinaryVar.class.getSimpleName())) {
//
//                return;
//            }
//
//            System.out.println("Target Type: " + type);
//
//            throw new IllegalStateException("Illegal State.");
//        };
//
//        gsonBuilder.registerTypeAdapter(BoolExpr.class, deserializer);
//
//        Gson customGson = gsonBuilder.setPrettyPrinting().create();
//
//        return customGson.fromJson(jsonString, Model.class);
//    }
//
//    private class ModelAdapter {
//        List<ConstraintAdapter> constraints;
//        ObjectiveAdapter objective;
//        Integer m;
//        Map<String, VarAdapter> vars;
//        Map<VarAdapter, BoundsAdapter> bounds;
//        List<Set<VarAdapter>> sos1;
//    }
//
//    private class ConstraintAdapter {
//        String name;
//        BoolExprAdapter expr;
//    }
//
//    private class ObjectiveAdapter {
//        boolean max;
//        BoolExprAdapter expr;
//    }
//
//    private class BoundsAdapter {
//        double lb;
//        double ub;
//    }
//
//    private abstract class BoolExprAdapter {
//    }
//
//    private class AndAdapter extends BoolExprAdapter {
//        List<BoolExprAdapter> elements;
//    }
//
//    private class BiImplAdapter extends BoolExprAdapter {
//        BoolExprAdapter lhs;
//        BoolExprAdapter rhs;
//    }
//
//    private class EqAdapter extends BoolExprAdapter {
//        BoolExprAdapter lhs;
//        BoolExprAdapter rhs;
//    }
//
//    private class GeqAdapter extends BoolExprAdapter {
//        BoolExprAdapter lhs;
//        BoolExprAdapter rhs;
//    }
//
//    private class ImplAdapter extends BoolExprAdapter {
//        BoolExprAdapter lhs;
//        BoolExprAdapter rhs;
//    }
//
//    private class LeqAdapter extends BoolExprAdapter {
//        BoolExprAdapter lhs;
//        BoolExprAdapter rhs;
//    }
//
//    private class NotAdapter extends BoolExprAdapter {
//        BoolExprAdapter val;
//    }
//
//    private class OrAdapter extends BoolExprAdapter {
//        BoolExprAdapter lhs;
//        BoolExprAdapter rhs;
//    }
//
//    private class XorAdapter extends BoolExprAdapter {
//        BoolExprAdapter lhs;
//        BoolExprAdapter rhs;
//    }
//
//    private class BinaryVarAdapter extends BoolExprAdapter {
//        String name;
//    }
//
//    private abstract class ArithExprAdapter {
//    }
//
//    private abstract class ArithUnaryExprAdapter {
//    }
//
//    private abstract class VarAdapter extends ArithUnaryExprAdapter {
//    }
//
//    private abstract class ParamAdapter extends ArithUnaryExprAdapter {
//    }
//
//    private class DoubleParamAdapter extends ParamAdapter {
//        double val;
//    }
//
//    private class DoubleVarAdapter extends VarAdapter {
//        String name;
//    }
//
//    private class IntVarAdapter extends VarAdapter {
//        String name;
//    }
//
//    private class MultAdapter extends ArithExprAdapter {
//        ArithUnaryExprAdapter lhs;
//        ArithUnaryExprAdapter rhs;
//    }
//
//
//    private class SumAdapter extends ArithExprAdapter {
//        List<ArithExprAdapter> summands;
//    }
//
//    private class NegAdapter extends ArithExprAdapter {
//        ArithExprAdapter neg;
//    }


}
