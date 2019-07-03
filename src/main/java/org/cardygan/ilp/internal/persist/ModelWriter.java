package org.cardygan.ilp.internal.persist;

import org.cardygan.ilp.api.model.*;
import org.cardygan.ilp.api.model.bool.RelOp;
import org.cardygan.ilp.internal.expr.ExprPrettyPrinter;

import java.util.Map;


public final class ModelWriter {

    private final static String NL = "\n";


    public static String writeToString(Model model) {
        StringBuffer ret = new StringBuffer();

        appendVars(model, ret);

        appendObj(model, ret);

        appendGenCstrs(model, ret);

        appendCstrs(model, ret);

        appendSos1(model, ret);

        return ret.toString();
    }


    private static void appendVars(Model model, StringBuffer ret) {
        ret.append("vars {" + NL);
        for (Var var : model.getVars().values()) {
            final String varName = var.getName();

            ret.append(varName);

            if (var instanceof IntVar) {
                ret.append(":Int");

                final IntBounds intBounds = model.getBounds((IntVar) var);
                if (intBounds != null) {
                    final String lb = Integer.toString(intBounds.getLb());

                    final String ub;
                    if (intBounds.getUb() < 0)
                        ub = "*";
                    else
                        ub = Integer.toString(intBounds.getUb());

                    ret.append("[" + lb + "," + ub + "]" + NL);
                } else
                    ret.append(NL);

            } else if (var instanceof DoubleVar) {
                ret.append(":Real");

                final DblBounds dblBounds = model.getBounds((DoubleVar) var);
                if (dblBounds != null) {
                    final String lb = Double.toString(dblBounds.getLb());

                    final String ub;
                    if (dblBounds.getUb() < 0)
                        ub = "*";
                    else
                        ub = Double.toString(dblBounds.getUb());

                    ret.append("[" + lb + "," + ub + "]" + NL);
                } else
                    ret.append(NL);

            } else if (var instanceof BinaryVar) {
                ret.append(":Bin" + NL);

            } else {
                throw new IllegalStateException("Unknown variable type.");
            }
        }
        ret.append("}" + NL);
    }


    private static void appendGenCstrs(Model model, StringBuffer ret) {
        ret.append("genCstrs {" + NL);
        ExprPrettyPrinter prettyPrinter = new ExprPrettyPrinter();

        for (Constraint cstr : model.getConstraints()) {
            if (cstr.getName().isPresent())
                ret.append(cstr.getName().get() + ":");

            if (!(cstr.getExpr() instanceof RelOp)) {
                ret.append(cstr.getExpr().accept(prettyPrinter) + ';' + NL);
            }
        }
        ret.append("}" + NL);
    }

    private static void appendCstrs(Model model, StringBuffer ret) {
        ret.append("cstrs {" + NL);
        ExprPrettyPrinter prettyPrinter = new ExprPrettyPrinter();
        for (Constraint cstr : model.getConstraints()) {
            if (cstr.getExpr() instanceof RelOp) {
                if (cstr.getName().isPresent())
                    ret.append(cstr.getName().get() + ":");

                ret.append(cstr.getExpr().accept(prettyPrinter) + ';' + NL);
            }
        }
        ret.append("}" + NL);
    }

    private static void appendSos1(Model model, StringBuffer ret) {
        ret.append("sos1 {" + NL);
        for (Sos1Constraint cstr : model.getSos1()) {
            int counter = 0;
            for (Map.Entry<Var, Double> entry : cstr.getElements().entrySet()) {
                ret.append(entry.getValue() + " " + entry.getKey().getName());

                counter++;
                if (counter < cstr.getElements().size())
                    ret.append(", ");
            }
            ret.append(';' + NL);
        }
        ret.append("}" + NL);
    }

    private static void appendObj(Model model, StringBuffer ret) {

        if (model.getObjective().isPresent()) {
            ExprPrettyPrinter prettyPrinter = new ExprPrettyPrinter();
            final Objective obj = model.getObjective().get();
            ret.append("obj {" + NL);
            if (obj.isMax())
                ret.append("max ");
            else
                ret.append("min ");

            ret.append(obj.getExpr().accept(prettyPrinter) + NL);

            ret.append("}" + NL);
        }
    }
}
