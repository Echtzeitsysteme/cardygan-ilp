package org.cardygan.ilp.internal.expr.cnf;


import org.cardygan.ilp.api.model.BinaryVar;
import org.cardygan.ilp.api.model.Model;

public class BoolVarGen {

    private final Model model;
    private int counter = 0;

    public BoolVarGen(Model model) {
        this.model = model;
    }

    public BinaryVar newVar() {
        counter++;
        return model.newBinaryVar();
    }

    public boolean isFirst() {
        return counter == 0;
    }

}
