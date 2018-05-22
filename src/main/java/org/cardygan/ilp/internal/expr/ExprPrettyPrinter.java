package org.cardygan.ilp.internal.expr;

import org.cardygan.ilp.api.model.*;
import org.cardygan.ilp.api.model.bool.*;

import java.util.stream.Collectors;

public class ExprPrettyPrinter implements BoolExprVisitor<String>, ArithExprVisitor<String> {
    @Override
    public String visit(Sum expr) {
        return "(" + expr.getSummands().stream().map(e -> e.accept(this)).collect(Collectors.joining(" + ")) + ")";
    }

    @Override
    public String visit(Mult expr) {
        return expr.getLeft().accept(this) + " " + expr.getRight().accept(this);
    }

    @Override
    public String visit(Param expr) {
        return Double.toString(expr.getVal());
    }

    @Override
    public String visit(Neg expr) {
        return "-(" + expr.getNeg().accept(this) + ")";
    }

    @Override
    public String visit(Var var) {
        return var.getName();
    }

    @Override
    public String visit(And expr) {
        return expr.getElements().stream().map(e -> e.accept(this)).collect(Collectors.joining(" && "));
    }

    @Override
    public String visit(Or expr) {
        return "(" + expr.getElements().stream().map(e -> expr.accept(this)).collect(Collectors.joining(" || ")) + ")";
    }

    @Override
    public String visit(Xor expr) {
        return "(" + expr.getLhs().accept(this) + " XOR " + expr.getRhs().accept(this) + ")";
    }

    @Override
    public String visit(Not expr) {
        return "!(" + expr.getVal().accept(this) + ")";
    }

    @Override
    public String visit(Impl expr) {
        return expr.getLhs().accept(this) + " => " + expr.getRhs().accept(this);
    }

    @Override
    public String visit(BiImpl expr) {
        return expr.getLhs().accept(this) + " <=> " + expr.getRhs().accept(this);
    }

    @Override
    public String visit(BinaryVar expr) {
        return expr.getName();
    }

    @Override
    public String visit(RelOp expr) {
        String operator = " ?? ";
        if (expr instanceof Geq) {
            operator = ">=";
        }
        if (expr instanceof Eq) {
            operator = "=";
        }
        if (expr instanceof Leq) {
            operator = "<=";
        }

        return expr.getLhs().accept(this) + " " + operator + " " + expr.getRhs().accept(this);
    }
}
