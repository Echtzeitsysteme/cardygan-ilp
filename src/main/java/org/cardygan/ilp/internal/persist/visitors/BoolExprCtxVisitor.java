package org.cardygan.ilp.internal.persist.visitors;

import org.cardygan.ilp.api.model.BinaryVar;
import org.cardygan.ilp.api.model.Model;
import org.cardygan.ilp.api.model.Var;
import org.cardygan.ilp.api.model.bool.*;
import org.cardygan.ilp.internal.persist.antlr.IlpBaseVisitor;
import org.cardygan.ilp.internal.persist.antlr.IlpParser;

public class BoolExprCtxVisitor extends IlpBaseVisitor<BoolExpr> {

    private final Model model;

    public BoolExprCtxVisitor(Model model) {
        this.model = model;
    }

    @Override
    public BoolExpr visitAnd(IlpParser.AndContext ctx) {
        return new And(ctx.boolExpr(0).accept(this), ctx.boolExpr(1).accept(this));
    }

    @Override
    public BoolExpr visitOr(IlpParser.OrContext ctx) {
        return new Or(ctx.boolExpr(0).accept(this), ctx.boolExpr(1).accept(this));
    }

    @Override
    public BoolExpr visitNot(IlpParser.NotContext ctx) {
        return new Not(ctx.boolExpr().accept(this));
    }

    @Override
    public BoolExpr visitImpl(IlpParser.ImplContext ctx) {
        return new Impl(ctx.boolExpr(0).accept(this), ctx.boolExpr(1).accept(this));
    }

    @Override
    public BoolExpr visitBiImpl(IlpParser.BiImplContext ctx) {
        return new BiImpl(ctx.boolExpr(0).accept(this), ctx.boolExpr(1).accept(this));
    }

    @Override
    public BoolExpr visitXOr(IlpParser.XOrContext ctx) {
        return new Xor(ctx.boolExpr(0).accept(this), ctx.boolExpr(1).accept(this));
    }

    @Override
    public BoolExpr visitBoolLit(IlpParser.BoolLitContext ctx) {
        final String varName = ctx.var().varName().ID().getText();
        Var var = model.getVars().get(varName);

        if (!(var instanceof BinaryVar))
            throw new IllegalStateException("Boolean expressions can only contain binary variables.");

        return (BinaryVar) var;
    }

    @Override
    public BoolExpr visitBoolBrackets(IlpParser.BoolBracketsContext ctx) {
        return ctx.boolExpr().accept(this);
    }

    @Override
    public BoolExpr visitEq(IlpParser.EqContext ctx) {
        ArithExprCtxVisitor visitor = new ArithExprCtxVisitor(model);

        return new Eq(ctx.arithExpr(0).accept(visitor), ctx.arithExpr(1).accept(visitor));
    }

    @Override
    public BoolExpr visitGe(IlpParser.GeContext ctx) {
        ArithExprCtxVisitor visitor = new ArithExprCtxVisitor(model);

        return new Geq(ctx.arithExpr(0).accept(visitor), ctx.arithExpr(1).accept(visitor));
    }

    @Override
    public BoolExpr visitLe(IlpParser.LeContext ctx) {
        ArithExprCtxVisitor visitor = new ArithExprCtxVisitor(model);

        return new Leq(ctx.arithExpr(0).accept(visitor), ctx.arithExpr(1).accept(visitor));
    }
}
