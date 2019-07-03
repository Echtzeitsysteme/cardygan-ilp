package org.cardygan.ilp.internal.persist.visitors;

import org.cardygan.ilp.api.model.*;
import org.cardygan.ilp.internal.persist.antlr.IlpBaseVisitor;
import org.cardygan.ilp.internal.persist.antlr.IlpParser;

public class ArithExprCtxVisitor extends IlpBaseVisitor<ArithExpr> {

    private final Model model;

    public ArithExprCtxVisitor(Model model) {
        this.model = model;
    }

    @Override
    public ArithExpr visitArithBrackets(IlpParser.ArithBracketsContext ctx) {
        return ctx.arithExpr().accept(this);
    }

    @Override
    public ArithExpr visitSum(IlpParser.SumContext ctx) {
        return new Sum(ctx.arithExpr(0).accept(this), ctx.arithExpr(1).accept(this));
    }

    @Override
    public ArithExpr visitMult(IlpParser.MultContext ctx) {
        ArithUnaryExpr lhs = (ArithUnaryExpr) ctx.arithExpr(0).accept(this);
        ArithUnaryExpr rhs = (ArithUnaryExpr) ctx.arithExpr(1).accept(this);

        return new Mult(lhs, rhs);
    }

    @Override
    public ArithExpr visitNeg(IlpParser.NegContext ctx) {
        return new Neg(ctx.arithExpr().accept(this));
    }

    @Override
    public ArithExpr visitVar(IlpParser.VarContext ctx) {
        final String varName = ctx.varName().ID().getText();
        Var var = model.getVars().get(varName);

        return var;
    }

    @Override
    public ArithExpr visitIntLit(IlpParser.IntLitContext ctx) {
        return new DoubleParam(Integer.parseInt(ctx.intLiteral().INTEGER().getText()));
    }

    @Override
    public ArithExpr visitRealLit(IlpParser.RealLitContext ctx) {
        return new DoubleParam(Double.parseDouble(ctx.realLiteral().NUMBER().getText()));
    }

}
