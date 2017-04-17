package org.cardygan.ilp.internal.expr;

import org.cardygan.ilp.api.BinaryVar;
import org.cardygan.ilp.api.expr.bool.*;

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
