package org.cardygan.ilp.internal.expr;

import org.cardygan.ilp.api.model.BinaryVar;
import org.cardygan.ilp.api.model.bool.*;

public interface BoolExprVisitor<T> {

    T visit(And expr);

    T visit(Or expr);

    T visit(Xor expr);

    T visit(Not expr);

    T visit(Impl expr);

    T visit(BiImpl expr);

    T visit(BinaryVar expr);

    T visit(RelOp expr);

}
