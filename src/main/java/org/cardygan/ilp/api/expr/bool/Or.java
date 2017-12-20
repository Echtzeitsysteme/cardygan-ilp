package org.cardygan.ilp.api.expr.bool;


import org.cardygan.ilp.internal.expr.BoolExprVisitor;
import org.cardygan.ilp.internal.util.Util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Or implements BoolExpr {

    private final List<BoolExpr> elements;

    public Or(List<BoolExpr> elements) {
        this.elements = elements;
    }

    public Or(BoolExpr firstElem, BoolExpr... otherElems) {
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
