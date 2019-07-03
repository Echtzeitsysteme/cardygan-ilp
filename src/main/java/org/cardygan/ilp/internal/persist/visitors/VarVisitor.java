package org.cardygan.ilp.internal.persist.visitors;

import org.cardygan.ilp.api.model.Model;
import org.cardygan.ilp.internal.persist.antlr.IlpBaseVisitor;
import org.cardygan.ilp.internal.persist.antlr.IlpParser;


public class VarVisitor extends IlpBaseVisitor<Void> {


    private final Model model;

    public VarVisitor(Model model) {
        this.model = model;
    }

    @Override
    public Void visitIntVar(IlpParser.IntVarContext ctx) {
        final String name = ctx.varName().ID().getText();

        if (ctx.intervalInt() != null) {
            IntIvBuilder builder = new IntIvBuilder(ctx);
            builder.build();

            if (builder.ub < 0)
                model.newIntVar(name, builder.lb);
            else
                model.newIntVar(name, builder.lb, builder.ub);
        } else
            model.newIntVar(name);


        return null;
    }

    @Override
    public Void visitRealVar(IlpParser.RealVarContext ctx) {
        final String name = ctx.varName().ID().getText();

        if (ctx.intervalReal() != null) {
            RealIvBuilder builder = new RealIvBuilder(ctx);
            builder.build();

            if (builder.ub < 0)
                model.newDoubleVar(name, builder.lb);
            else
                model.newDoubleVar(name, builder.lb, builder.ub);
        } else
            model.newIntVar(name);

        return null;
    }

    @Override
    public Void visitBinVar(IlpParser.BinVarContext ctx) {
        final String name = ctx.varName().ID().getText();
        model.newBinaryVar(name);
        return null;
    }

    private class IntIvBuilder extends IlpBaseVisitor<Void> {

        int lb;
        int ub;

        private final IlpParser.IntVarContext intVarctx;

        private IntIvBuilder(IlpParser.IntVarContext intVarctx) {
            this.intVarctx = intVarctx;
        }


        void build() {
            intVarctx.accept(this);
        }


        @Override
        public Void visitLbInt(IlpParser.LbIntContext ctx) {
            lb = Integer.parseInt(ctx.integer().getText());

            return null;
        }

        @Override
        public Void visitUbInt(IlpParser.UbIntContext ctx) {
            if (ctx.inf() != null)
                ub = -1;
            else
                ub = Integer.parseInt(ctx.integer().getText());

            return null;
        }


    }

    private class RealIvBuilder extends IlpBaseVisitor<Void> {

        double lb;
        double ub;

        private final IlpParser.RealVarContext realVarctx;


        private RealIvBuilder(IlpParser.RealVarContext realVarctx) {
            this.realVarctx = realVarctx;
        }

        void build() {
            realVarctx.accept(this);
        }

        @Override
        public Void visitLbReal(IlpParser.LbRealContext ctx) {
            lb = Double.parseDouble(ctx.NUMBER().getText());

            return null;
        }

        @Override
        public Void visitUbReal(IlpParser.UbRealContext ctx) {
            if (ctx.INF() != null)
                ub = -1;
            else
                ub = Double.parseDouble(ctx.NUMBER().getText());

            return null;
        }
    }

}