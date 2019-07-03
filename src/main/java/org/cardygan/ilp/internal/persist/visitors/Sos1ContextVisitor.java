package org.cardygan.ilp.internal.persist.visitors;

import org.cardygan.ilp.api.model.Model;
import org.cardygan.ilp.api.model.Var;
import org.cardygan.ilp.internal.persist.antlr.IlpBaseVisitor;
import org.cardygan.ilp.internal.persist.antlr.IlpParser;

import java.util.HashMap;
import java.util.Map;

public class Sos1ContextVisitor extends IlpBaseVisitor<Map<Var, Double>> {

    private final Model model;

    public Sos1ContextVisitor(Model model) {
        this.model = model;
    }

    @Override
    public Map<Var, Double> visitSos1(IlpParser.Sos1Context ctx) {

        final Map<Var, Double> elems = new HashMap<>();

        for (IlpParser.Sos1ElementContext elem : ctx.sos1Element()) {
            final double weight = Double.parseDouble(elem.weight().NUMBER().getText());

            final String varName = elem.var().getText();
            Var var = model.getVars().get(varName);

            if (var == null)
                throw new IllegalStateException("Could not find variable with name " + varName +
                        ". Check if variable is properly declared.");

            elems.put(var, weight);
        }

        return elems;
    }
}
