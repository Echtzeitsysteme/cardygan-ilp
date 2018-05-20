package org.cardygan.ilp.api.model.bool;

import org.cardygan.ilp.internal.expr.BoolExprVisitor;
import org.cardygan.ilp.internal.util.Util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class And implements BoolExpr {

    private final List<BoolExpr> elements;

    public And(List<BoolExpr> elements) {
        Util.assertNotNull(elements);
        if (elements.isEmpty()){
            throw new IllegalStateException("Cannot crete AND expression from empty list.");
        }
        this.elements = elements;
    }

    public And(BoolExpr firstElem, BoolExpr... otherElems) {
        Util.assertNotNull(firstElem, otherElems);

        elements = new ArrayList<>();
        elements.add(firstElem);
        elements.addAll(Arrays.asList(otherElems));
    }



    /**
     * Return immutable list of elements
     *
     * @return immutable list of elements
     */
    public List<BoolExpr> getElements() {
        return Collections.unmodifiableList(elements);
    }

    @Override
    public <T> T accept(BoolExprVisitor<T> visitor) {
        return visitor.visit(this);
    }

}
