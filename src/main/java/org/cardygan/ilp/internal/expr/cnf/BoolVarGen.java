package org.cardygan.ilp.internal.expr.cnf;


import org.cardygan.ilp.api.BinaryVar;
import org.cardygan.ilp.api.ModelContext;

public class BoolVarGen {

    private final ModelContext model;
    private int counter = 0;

    public BoolVarGen(ModelContext model) {
        this.model = model;
    }

    public BinaryVar newVar() {
        counter++;
        return model.newBinaryVarWithPrefix(ModelContext.HELPING_VAR_PREFIX);
    }

    public boolean isFirst() {
        return counter == 0;
    }

}
