package org.cardygan.ilp.internal.expr.cnf;

import org.cardygan.ilp.api.model.BinaryVar;
import org.cardygan.ilp.api.model.bool.*;
import org.cardygan.ilp.internal.expr.BoolExprVisitor;

import java.util.Map;

public class EvalVisitor implements BoolExprVisitor<Boolean> {

    private final Map<BinaryVar, Boolean> vals;

    public EvalVisitor(Map<BinaryVar, Boolean> vals) {
        this.vals = vals;
    }

    @Override
    public Boolean visit(And expr) {
        boolean a = expr.getLhs().accept(this);
        boolean b = expr.getRhs().accept(this);
        return a && b;
    }

    @Override
    public Boolean visit(Or expr) {
        boolean a = expr.getLhs().accept(this);
        boolean b = expr.getRhs().accept(this);
        return a || b;
    }

    @Override
    public Boolean visit(Xor expr) {
        boolean a = expr.getLhs().accept(this);
        boolean b = expr.getRhs().accept(this);
        return a && !b || !a && b;
    }

    @Override
    public Boolean visit(Not expr) {
        return !expr.getVal().accept(this);
    }

    @Override
    public Boolean visit(Impl expr) {
        boolean a = expr.getLhs().accept(this);
        boolean b = expr.getRhs().accept(this);
        return !a || b;
    }

    @Override
    public Boolean visit(BiImpl expr) {
        boolean a = expr.getLhs().accept(this);
        boolean b = expr.getRhs().accept(this);
        return a == b;
    }

    @Override
    public Boolean visit(BinaryVar var) {
        if (!vals.containsKey(var)) {
            throw new IllegalStateException(var + " is not contained in values.");
        }
        return vals.get(var);
    }

    @Override
    public Boolean visit(RelOp expr) {
        return null;
    }

}
