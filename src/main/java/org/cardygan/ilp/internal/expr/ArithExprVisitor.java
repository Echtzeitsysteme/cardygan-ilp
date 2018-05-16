package org.cardygan.ilp.internal.expr;

import org.cardygan.ilp.api.expr.*;

public interface ArithExprVisitor<T> {

    T visit(Sum expr);

    T visit(Mult expr);

    T visit(Param expr);

    T visit(Neg expr);

    T visit(Var var);

}
