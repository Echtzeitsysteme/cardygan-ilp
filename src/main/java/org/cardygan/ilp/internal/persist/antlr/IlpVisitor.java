// Generated from Ilp.g4 by ANTLR 4.7.2
package org.cardygan.ilp.internal.persist.antlr;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link IlpParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface IlpVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link IlpParser#model}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitModel(IlpParser.ModelContext ctx);
	/**
	 * Visit a parse tree produced by {@link IlpParser#vars}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVars(IlpParser.VarsContext ctx);
	/**
	 * Visit a parse tree produced by the {@code intVar}
	 * labeled alternative in {@link IlpParser#varDecl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIntVar(IlpParser.IntVarContext ctx);
	/**
	 * Visit a parse tree produced by the {@code realVar}
	 * labeled alternative in {@link IlpParser#varDecl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRealVar(IlpParser.RealVarContext ctx);
	/**
	 * Visit a parse tree produced by the {@code binVar}
	 * labeled alternative in {@link IlpParser#varDecl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBinVar(IlpParser.BinVarContext ctx);
	/**
	 * Visit a parse tree produced by {@link IlpParser#intervalInt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIntervalInt(IlpParser.IntervalIntContext ctx);
	/**
	 * Visit a parse tree produced by {@link IlpParser#intervalReal}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIntervalReal(IlpParser.IntervalRealContext ctx);
	/**
	 * Visit a parse tree produced by {@link IlpParser#lbInt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLbInt(IlpParser.LbIntContext ctx);
	/**
	 * Visit a parse tree produced by {@link IlpParser#ubInt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUbInt(IlpParser.UbIntContext ctx);
	/**
	 * Visit a parse tree produced by {@link IlpParser#integer}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInteger(IlpParser.IntegerContext ctx);
	/**
	 * Visit a parse tree produced by {@link IlpParser#inf}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInf(IlpParser.InfContext ctx);
	/**
	 * Visit a parse tree produced by {@link IlpParser#lbReal}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLbReal(IlpParser.LbRealContext ctx);
	/**
	 * Visit a parse tree produced by {@link IlpParser#ubReal}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUbReal(IlpParser.UbRealContext ctx);
	/**
	 * Visit a parse tree produced by {@link IlpParser#varName}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVarName(IlpParser.VarNameContext ctx);
	/**
	 * Visit a parse tree produced by the {@code minObj}
	 * labeled alternative in {@link IlpParser#obj}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMinObj(IlpParser.MinObjContext ctx);
	/**
	 * Visit a parse tree produced by the {@code maxObj}
	 * labeled alternative in {@link IlpParser#obj}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMaxObj(IlpParser.MaxObjContext ctx);
	/**
	 * Visit a parse tree produced by {@link IlpParser#genCstrs}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGenCstrs(IlpParser.GenCstrsContext ctx);
	/**
	 * Visit a parse tree produced by {@link IlpParser#cstrs}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCstrs(IlpParser.CstrsContext ctx);
	/**
	 * Visit a parse tree produced by {@link IlpParser#cstrName}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCstrName(IlpParser.CstrNameContext ctx);
	/**
	 * Visit a parse tree produced by {@link IlpParser#sos1Cstrs}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSos1Cstrs(IlpParser.Sos1CstrsContext ctx);
	/**
	 * Visit a parse tree produced by {@link IlpParser#sos1}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSos1(IlpParser.Sos1Context ctx);
	/**
	 * Visit a parse tree produced by {@link IlpParser#sos1Element}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSos1Element(IlpParser.Sos1ElementContext ctx);
	/**
	 * Visit a parse tree produced by {@link IlpParser#var}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVar(IlpParser.VarContext ctx);
	/**
	 * Visit a parse tree produced by {@link IlpParser#weight}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWeight(IlpParser.WeightContext ctx);
	/**
	 * Visit a parse tree produced by the {@code Not}
	 * labeled alternative in {@link IlpParser#boolExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNot(IlpParser.NotContext ctx);
	/**
	 * Visit a parse tree produced by the {@code Impl}
	 * labeled alternative in {@link IlpParser#boolExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitImpl(IlpParser.ImplContext ctx);
	/**
	 * Visit a parse tree produced by the {@code Or}
	 * labeled alternative in {@link IlpParser#boolExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOr(IlpParser.OrContext ctx);
	/**
	 * Visit a parse tree produced by the {@code boolBrackets}
	 * labeled alternative in {@link IlpParser#boolExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBoolBrackets(IlpParser.BoolBracketsContext ctx);
	/**
	 * Visit a parse tree produced by the {@code boolLit}
	 * labeled alternative in {@link IlpParser#boolExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBoolLit(IlpParser.BoolLitContext ctx);
	/**
	 * Visit a parse tree produced by the {@code And}
	 * labeled alternative in {@link IlpParser#boolExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAnd(IlpParser.AndContext ctx);
	/**
	 * Visit a parse tree produced by the {@code Rel}
	 * labeled alternative in {@link IlpParser#boolExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRel(IlpParser.RelContext ctx);
	/**
	 * Visit a parse tree produced by the {@code XOr}
	 * labeled alternative in {@link IlpParser#boolExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitXOr(IlpParser.XOrContext ctx);
	/**
	 * Visit a parse tree produced by the {@code BiImpl}
	 * labeled alternative in {@link IlpParser#boolExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBiImpl(IlpParser.BiImplContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ge}
	 * labeled alternative in {@link IlpParser#relOp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGe(IlpParser.GeContext ctx);
	/**
	 * Visit a parse tree produced by the {@code le}
	 * labeled alternative in {@link IlpParser#relOp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLe(IlpParser.LeContext ctx);
	/**
	 * Visit a parse tree produced by the {@code eq}
	 * labeled alternative in {@link IlpParser#relOp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEq(IlpParser.EqContext ctx);
	/**
	 * Visit a parse tree produced by the {@code realLit}
	 * labeled alternative in {@link IlpParser#arithExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRealLit(IlpParser.RealLitContext ctx);
	/**
	 * Visit a parse tree produced by the {@code neg}
	 * labeled alternative in {@link IlpParser#arithExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNeg(IlpParser.NegContext ctx);
	/**
	 * Visit a parse tree produced by the {@code arithVar}
	 * labeled alternative in {@link IlpParser#arithExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArithVar(IlpParser.ArithVarContext ctx);
	/**
	 * Visit a parse tree produced by the {@code mult}
	 * labeled alternative in {@link IlpParser#arithExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMult(IlpParser.MultContext ctx);
	/**
	 * Visit a parse tree produced by the {@code arithBrackets}
	 * labeled alternative in {@link IlpParser#arithExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArithBrackets(IlpParser.ArithBracketsContext ctx);
	/**
	 * Visit a parse tree produced by the {@code intLit}
	 * labeled alternative in {@link IlpParser#arithExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIntLit(IlpParser.IntLitContext ctx);
	/**
	 * Visit a parse tree produced by the {@code sum}
	 * labeled alternative in {@link IlpParser#arithExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSum(IlpParser.SumContext ctx);
	/**
	 * Visit a parse tree produced by {@link IlpParser#realLiteral}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRealLiteral(IlpParser.RealLiteralContext ctx);
	/**
	 * Visit a parse tree produced by {@link IlpParser#intLiteral}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIntLiteral(IlpParser.IntLiteralContext ctx);
}