package org.cardygan.ilp.internal.persist.visitors;

import org.cardygan.ilp.api.model.ArithExpr;
import org.cardygan.ilp.api.model.Model;
import org.cardygan.ilp.internal.persist.antlr.IlpBaseVisitor;
import org.cardygan.ilp.internal.persist.antlr.IlpParser;

public class TopLevelVisitor extends IlpBaseVisitor<Void> {


    private final Model model;

    public TopLevelVisitor(Model model) {
        this.model = model;
    }

    @Override
    public Void visitVars(IlpParser.VarsContext ctx) {
        VarVisitor visitor = new VarVisitor(model);
        ctx.accept(visitor);

        return null;
    }

    @Override
    public Void visitGenCstrs(IlpParser.GenCstrsContext ctx) {
        BoolExprCtxVisitor visitor = new BoolExprCtxVisitor(model);

        ctx.boolExpr().stream().map(
                boolExprContext -> boolExprContext.accept(visitor)
        ).forEach(model::newConstraint);

        return null;
    }

    @Override
    public Void visitCstrs(IlpParser.CstrsContext ctx) {
        BoolExprCtxVisitor visitor = new BoolExprCtxVisitor(model);

        ctx.relOp().stream().map(
                boolExprContext -> boolExprContext.accept(visitor)
        ).forEach(model::newConstraint);

        return null;
    }

    @Override
    public Void visitMaxObj(IlpParser.MaxObjContext ctx) {
        ArithExprCtxVisitor visitor = new ArithExprCtxVisitor(model);

        model.newObjective(true, ctx.arithExpr().accept(visitor));

        return null;
    }

    @Override
    public Void visitSos1Cstrs(IlpParser.Sos1CstrsContext ctx) {
        Sos1ContextVisitor visitor = new Sos1ContextVisitor(model);

        ctx.sos1().stream().forEach(sos1 -> model.newSos1(sos1.accept(visitor)));

        return null;
    }

    @Override
    public Void visitMinObj(IlpParser.MinObjContext ctx) {
        ArithExprCtxVisitor visitor = new ArithExprCtxVisitor(model);

        model.newObjective(false, ctx.arithExpr().accept(visitor));

        return null;
    }

}
