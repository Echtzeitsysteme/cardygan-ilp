// Generated from Ilp.g4 by ANTLR 4.7.2
package org.cardygan.ilp.internal.persist.antlr;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link IlpParser}.
 */
public interface IlpListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link IlpParser#model}.
	 * @param ctx the parse tree
	 */
	void enterModel(IlpParser.ModelContext ctx);
	/**
	 * Exit a parse tree produced by {@link IlpParser#model}.
	 * @param ctx the parse tree
	 */
	void exitModel(IlpParser.ModelContext ctx);
	/**
	 * Enter a parse tree produced by {@link IlpParser#vars}.
	 * @param ctx the parse tree
	 */
	void enterVars(IlpParser.VarsContext ctx);
	/**
	 * Exit a parse tree produced by {@link IlpParser#vars}.
	 * @param ctx the parse tree
	 */
	void exitVars(IlpParser.VarsContext ctx);
	/**
	 * Enter a parse tree produced by the {@code intVar}
	 * labeled alternative in {@link IlpParser#varDecl}.
	 * @param ctx the parse tree
	 */
	void enterIntVar(IlpParser.IntVarContext ctx);
	/**
	 * Exit a parse tree produced by the {@code intVar}
	 * labeled alternative in {@link IlpParser#varDecl}.
	 * @param ctx the parse tree
	 */
	void exitIntVar(IlpParser.IntVarContext ctx);
	/**
	 * Enter a parse tree produced by the {@code realVar}
	 * labeled alternative in {@link IlpParser#varDecl}.
	 * @param ctx the parse tree
	 */
	void enterRealVar(IlpParser.RealVarContext ctx);
	/**
	 * Exit a parse tree produced by the {@code realVar}
	 * labeled alternative in {@link IlpParser#varDecl}.
	 * @param ctx the parse tree
	 */
	void exitRealVar(IlpParser.RealVarContext ctx);
	/**
	 * Enter a parse tree produced by the {@code binVar}
	 * labeled alternative in {@link IlpParser#varDecl}.
	 * @param ctx the parse tree
	 */
	void enterBinVar(IlpParser.BinVarContext ctx);
	/**
	 * Exit a parse tree produced by the {@code binVar}
	 * labeled alternative in {@link IlpParser#varDecl}.
	 * @param ctx the parse tree
	 */
	void exitBinVar(IlpParser.BinVarContext ctx);
	/**
	 * Enter a parse tree produced by {@link IlpParser#intervalInt}.
	 * @param ctx the parse tree
	 */
	void enterIntervalInt(IlpParser.IntervalIntContext ctx);
	/**
	 * Exit a parse tree produced by {@link IlpParser#intervalInt}.
	 * @param ctx the parse tree
	 */
	void exitIntervalInt(IlpParser.IntervalIntContext ctx);
	/**
	 * Enter a parse tree produced by {@link IlpParser#intervalReal}.
	 * @param ctx the parse tree
	 */
	void enterIntervalReal(IlpParser.IntervalRealContext ctx);
	/**
	 * Exit a parse tree produced by {@link IlpParser#intervalReal}.
	 * @param ctx the parse tree
	 */
	void exitIntervalReal(IlpParser.IntervalRealContext ctx);
	/**
	 * Enter a parse tree produced by {@link IlpParser#lbInt}.
	 * @param ctx the parse tree
	 */
	void enterLbInt(IlpParser.LbIntContext ctx);
	/**
	 * Exit a parse tree produced by {@link IlpParser#lbInt}.
	 * @param ctx the parse tree
	 */
	void exitLbInt(IlpParser.LbIntContext ctx);
	/**
	 * Enter a parse tree produced by {@link IlpParser#ubInt}.
	 * @param ctx the parse tree
	 */
	void enterUbInt(IlpParser.UbIntContext ctx);
	/**
	 * Exit a parse tree produced by {@link IlpParser#ubInt}.
	 * @param ctx the parse tree
	 */
	void exitUbInt(IlpParser.UbIntContext ctx);
	/**
	 * Enter a parse tree produced by {@link IlpParser#integer}.
	 * @param ctx the parse tree
	 */
	void enterInteger(IlpParser.IntegerContext ctx);
	/**
	 * Exit a parse tree produced by {@link IlpParser#integer}.
	 * @param ctx the parse tree
	 */
	void exitInteger(IlpParser.IntegerContext ctx);
	/**
	 * Enter a parse tree produced by {@link IlpParser#inf}.
	 * @param ctx the parse tree
	 */
	void enterInf(IlpParser.InfContext ctx);
	/**
	 * Exit a parse tree produced by {@link IlpParser#inf}.
	 * @param ctx the parse tree
	 */
	void exitInf(IlpParser.InfContext ctx);
	/**
	 * Enter a parse tree produced by {@link IlpParser#lbReal}.
	 * @param ctx the parse tree
	 */
	void enterLbReal(IlpParser.LbRealContext ctx);
	/**
	 * Exit a parse tree produced by {@link IlpParser#lbReal}.
	 * @param ctx the parse tree
	 */
	void exitLbReal(IlpParser.LbRealContext ctx);
	/**
	 * Enter a parse tree produced by {@link IlpParser#ubReal}.
	 * @param ctx the parse tree
	 */
	void enterUbReal(IlpParser.UbRealContext ctx);
	/**
	 * Exit a parse tree produced by {@link IlpParser#ubReal}.
	 * @param ctx the parse tree
	 */
	void exitUbReal(IlpParser.UbRealContext ctx);
	/**
	 * Enter a parse tree produced by {@link IlpParser#varName}.
	 * @param ctx the parse tree
	 */
	void enterVarName(IlpParser.VarNameContext ctx);
	/**
	 * Exit a parse tree produced by {@link IlpParser#varName}.
	 * @param ctx the parse tree
	 */
	void exitVarName(IlpParser.VarNameContext ctx);
	/**
	 * Enter a parse tree produced by the {@code minObj}
	 * labeled alternative in {@link IlpParser#obj}.
	 * @param ctx the parse tree
	 */
	void enterMinObj(IlpParser.MinObjContext ctx);
	/**
	 * Exit a parse tree produced by the {@code minObj}
	 * labeled alternative in {@link IlpParser#obj}.
	 * @param ctx the parse tree
	 */
	void exitMinObj(IlpParser.MinObjContext ctx);
	/**
	 * Enter a parse tree produced by the {@code maxObj}
	 * labeled alternative in {@link IlpParser#obj}.
	 * @param ctx the parse tree
	 */
	void enterMaxObj(IlpParser.MaxObjContext ctx);
	/**
	 * Exit a parse tree produced by the {@code maxObj}
	 * labeled alternative in {@link IlpParser#obj}.
	 * @param ctx the parse tree
	 */
	void exitMaxObj(IlpParser.MaxObjContext ctx);
	/**
	 * Enter a parse tree produced by {@link IlpParser#genCstrs}.
	 * @param ctx the parse tree
	 */
	void enterGenCstrs(IlpParser.GenCstrsContext ctx);
	/**
	 * Exit a parse tree produced by {@link IlpParser#genCstrs}.
	 * @param ctx the parse tree
	 */
	void exitGenCstrs(IlpParser.GenCstrsContext ctx);
	/**
	 * Enter a parse tree produced by {@link IlpParser#cstrs}.
	 * @param ctx the parse tree
	 */
	void enterCstrs(IlpParser.CstrsContext ctx);
	/**
	 * Exit a parse tree produced by {@link IlpParser#cstrs}.
	 * @param ctx the parse tree
	 */
	void exitCstrs(IlpParser.CstrsContext ctx);
	/**
	 * Enter a parse tree produced by {@link IlpParser#cstrName}.
	 * @param ctx the parse tree
	 */
	void enterCstrName(IlpParser.CstrNameContext ctx);
	/**
	 * Exit a parse tree produced by {@link IlpParser#cstrName}.
	 * @param ctx the parse tree
	 */
	void exitCstrName(IlpParser.CstrNameContext ctx);
	/**
	 * Enter a parse tree produced by {@link IlpParser#sos1Cstrs}.
	 * @param ctx the parse tree
	 */
	void enterSos1Cstrs(IlpParser.Sos1CstrsContext ctx);
	/**
	 * Exit a parse tree produced by {@link IlpParser#sos1Cstrs}.
	 * @param ctx the parse tree
	 */
	void exitSos1Cstrs(IlpParser.Sos1CstrsContext ctx);
	/**
	 * Enter a parse tree produced by {@link IlpParser#sos1}.
	 * @param ctx the parse tree
	 */
	void enterSos1(IlpParser.Sos1Context ctx);
	/**
	 * Exit a parse tree produced by {@link IlpParser#sos1}.
	 * @param ctx the parse tree
	 */
	void exitSos1(IlpParser.Sos1Context ctx);
	/**
	 * Enter a parse tree produced by {@link IlpParser#sos1Element}.
	 * @param ctx the parse tree
	 */
	void enterSos1Element(IlpParser.Sos1ElementContext ctx);
	/**
	 * Exit a parse tree produced by {@link IlpParser#sos1Element}.
	 * @param ctx the parse tree
	 */
	void exitSos1Element(IlpParser.Sos1ElementContext ctx);
	/**
	 * Enter a parse tree produced by {@link IlpParser#var}.
	 * @param ctx the parse tree
	 */
	void enterVar(IlpParser.VarContext ctx);
	/**
	 * Exit a parse tree produced by {@link IlpParser#var}.
	 * @param ctx the parse tree
	 */
	void exitVar(IlpParser.VarContext ctx);
	/**
	 * Enter a parse tree produced by {@link IlpParser#weight}.
	 * @param ctx the parse tree
	 */
	void enterWeight(IlpParser.WeightContext ctx);
	/**
	 * Exit a parse tree produced by {@link IlpParser#weight}.
	 * @param ctx the parse tree
	 */
	void exitWeight(IlpParser.WeightContext ctx);
	/**
	 * Enter a parse tree produced by the {@code Not}
	 * labeled alternative in {@link IlpParser#boolExpr}.
	 * @param ctx the parse tree
	 */
	void enterNot(IlpParser.NotContext ctx);
	/**
	 * Exit a parse tree produced by the {@code Not}
	 * labeled alternative in {@link IlpParser#boolExpr}.
	 * @param ctx the parse tree
	 */
	void exitNot(IlpParser.NotContext ctx);
	/**
	 * Enter a parse tree produced by the {@code Impl}
	 * labeled alternative in {@link IlpParser#boolExpr}.
	 * @param ctx the parse tree
	 */
	void enterImpl(IlpParser.ImplContext ctx);
	/**
	 * Exit a parse tree produced by the {@code Impl}
	 * labeled alternative in {@link IlpParser#boolExpr}.
	 * @param ctx the parse tree
	 */
	void exitImpl(IlpParser.ImplContext ctx);
	/**
	 * Enter a parse tree produced by the {@code Or}
	 * labeled alternative in {@link IlpParser#boolExpr}.
	 * @param ctx the parse tree
	 */
	void enterOr(IlpParser.OrContext ctx);
	/**
	 * Exit a parse tree produced by the {@code Or}
	 * labeled alternative in {@link IlpParser#boolExpr}.
	 * @param ctx the parse tree
	 */
	void exitOr(IlpParser.OrContext ctx);
	/**
	 * Enter a parse tree produced by the {@code boolBrackets}
	 * labeled alternative in {@link IlpParser#boolExpr}.
	 * @param ctx the parse tree
	 */
	void enterBoolBrackets(IlpParser.BoolBracketsContext ctx);
	/**
	 * Exit a parse tree produced by the {@code boolBrackets}
	 * labeled alternative in {@link IlpParser#boolExpr}.
	 * @param ctx the parse tree
	 */
	void exitBoolBrackets(IlpParser.BoolBracketsContext ctx);
	/**
	 * Enter a parse tree produced by the {@code boolLit}
	 * labeled alternative in {@link IlpParser#boolExpr}.
	 * @param ctx the parse tree
	 */
	void enterBoolLit(IlpParser.BoolLitContext ctx);
	/**
	 * Exit a parse tree produced by the {@code boolLit}
	 * labeled alternative in {@link IlpParser#boolExpr}.
	 * @param ctx the parse tree
	 */
	void exitBoolLit(IlpParser.BoolLitContext ctx);
	/**
	 * Enter a parse tree produced by the {@code And}
	 * labeled alternative in {@link IlpParser#boolExpr}.
	 * @param ctx the parse tree
	 */
	void enterAnd(IlpParser.AndContext ctx);
	/**
	 * Exit a parse tree produced by the {@code And}
	 * labeled alternative in {@link IlpParser#boolExpr}.
	 * @param ctx the parse tree
	 */
	void exitAnd(IlpParser.AndContext ctx);
	/**
	 * Enter a parse tree produced by the {@code Rel}
	 * labeled alternative in {@link IlpParser#boolExpr}.
	 * @param ctx the parse tree
	 */
	void enterRel(IlpParser.RelContext ctx);
	/**
	 * Exit a parse tree produced by the {@code Rel}
	 * labeled alternative in {@link IlpParser#boolExpr}.
	 * @param ctx the parse tree
	 */
	void exitRel(IlpParser.RelContext ctx);
	/**
	 * Enter a parse tree produced by the {@code XOr}
	 * labeled alternative in {@link IlpParser#boolExpr}.
	 * @param ctx the parse tree
	 */
	void enterXOr(IlpParser.XOrContext ctx);
	/**
	 * Exit a parse tree produced by the {@code XOr}
	 * labeled alternative in {@link IlpParser#boolExpr}.
	 * @param ctx the parse tree
	 */
	void exitXOr(IlpParser.XOrContext ctx);
	/**
	 * Enter a parse tree produced by the {@code BiImpl}
	 * labeled alternative in {@link IlpParser#boolExpr}.
	 * @param ctx the parse tree
	 */
	void enterBiImpl(IlpParser.BiImplContext ctx);
	/**
	 * Exit a parse tree produced by the {@code BiImpl}
	 * labeled alternative in {@link IlpParser#boolExpr}.
	 * @param ctx the parse tree
	 */
	void exitBiImpl(IlpParser.BiImplContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ge}
	 * labeled alternative in {@link IlpParser#relOp}.
	 * @param ctx the parse tree
	 */
	void enterGe(IlpParser.GeContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ge}
	 * labeled alternative in {@link IlpParser#relOp}.
	 * @param ctx the parse tree
	 */
	void exitGe(IlpParser.GeContext ctx);
	/**
	 * Enter a parse tree produced by the {@code le}
	 * labeled alternative in {@link IlpParser#relOp}.
	 * @param ctx the parse tree
	 */
	void enterLe(IlpParser.LeContext ctx);
	/**
	 * Exit a parse tree produced by the {@code le}
	 * labeled alternative in {@link IlpParser#relOp}.
	 * @param ctx the parse tree
	 */
	void exitLe(IlpParser.LeContext ctx);
	/**
	 * Enter a parse tree produced by the {@code eq}
	 * labeled alternative in {@link IlpParser#relOp}.
	 * @param ctx the parse tree
	 */
	void enterEq(IlpParser.EqContext ctx);
	/**
	 * Exit a parse tree produced by the {@code eq}
	 * labeled alternative in {@link IlpParser#relOp}.
	 * @param ctx the parse tree
	 */
	void exitEq(IlpParser.EqContext ctx);
	/**
	 * Enter a parse tree produced by the {@code realLit}
	 * labeled alternative in {@link IlpParser#arithExpr}.
	 * @param ctx the parse tree
	 */
	void enterRealLit(IlpParser.RealLitContext ctx);
	/**
	 * Exit a parse tree produced by the {@code realLit}
	 * labeled alternative in {@link IlpParser#arithExpr}.
	 * @param ctx the parse tree
	 */
	void exitRealLit(IlpParser.RealLitContext ctx);
	/**
	 * Enter a parse tree produced by the {@code neg}
	 * labeled alternative in {@link IlpParser#arithExpr}.
	 * @param ctx the parse tree
	 */
	void enterNeg(IlpParser.NegContext ctx);
	/**
	 * Exit a parse tree produced by the {@code neg}
	 * labeled alternative in {@link IlpParser#arithExpr}.
	 * @param ctx the parse tree
	 */
	void exitNeg(IlpParser.NegContext ctx);
	/**
	 * Enter a parse tree produced by the {@code arithVar}
	 * labeled alternative in {@link IlpParser#arithExpr}.
	 * @param ctx the parse tree
	 */
	void enterArithVar(IlpParser.ArithVarContext ctx);
	/**
	 * Exit a parse tree produced by the {@code arithVar}
	 * labeled alternative in {@link IlpParser#arithExpr}.
	 * @param ctx the parse tree
	 */
	void exitArithVar(IlpParser.ArithVarContext ctx);
	/**
	 * Enter a parse tree produced by the {@code mult}
	 * labeled alternative in {@link IlpParser#arithExpr}.
	 * @param ctx the parse tree
	 */
	void enterMult(IlpParser.MultContext ctx);
	/**
	 * Exit a parse tree produced by the {@code mult}
	 * labeled alternative in {@link IlpParser#arithExpr}.
	 * @param ctx the parse tree
	 */
	void exitMult(IlpParser.MultContext ctx);
	/**
	 * Enter a parse tree produced by the {@code arithBrackets}
	 * labeled alternative in {@link IlpParser#arithExpr}.
	 * @param ctx the parse tree
	 */
	void enterArithBrackets(IlpParser.ArithBracketsContext ctx);
	/**
	 * Exit a parse tree produced by the {@code arithBrackets}
	 * labeled alternative in {@link IlpParser#arithExpr}.
	 * @param ctx the parse tree
	 */
	void exitArithBrackets(IlpParser.ArithBracketsContext ctx);
	/**
	 * Enter a parse tree produced by the {@code intLit}
	 * labeled alternative in {@link IlpParser#arithExpr}.
	 * @param ctx the parse tree
	 */
	void enterIntLit(IlpParser.IntLitContext ctx);
	/**
	 * Exit a parse tree produced by the {@code intLit}
	 * labeled alternative in {@link IlpParser#arithExpr}.
	 * @param ctx the parse tree
	 */
	void exitIntLit(IlpParser.IntLitContext ctx);
	/**
	 * Enter a parse tree produced by the {@code sum}
	 * labeled alternative in {@link IlpParser#arithExpr}.
	 * @param ctx the parse tree
	 */
	void enterSum(IlpParser.SumContext ctx);
	/**
	 * Exit a parse tree produced by the {@code sum}
	 * labeled alternative in {@link IlpParser#arithExpr}.
	 * @param ctx the parse tree
	 */
	void exitSum(IlpParser.SumContext ctx);
	/**
	 * Enter a parse tree produced by {@link IlpParser#realLiteral}.
	 * @param ctx the parse tree
	 */
	void enterRealLiteral(IlpParser.RealLiteralContext ctx);
	/**
	 * Exit a parse tree produced by {@link IlpParser#realLiteral}.
	 * @param ctx the parse tree
	 */
	void exitRealLiteral(IlpParser.RealLiteralContext ctx);
	/**
	 * Enter a parse tree produced by {@link IlpParser#intLiteral}.
	 * @param ctx the parse tree
	 */
	void enterIntLiteral(IlpParser.IntLiteralContext ctx);
	/**
	 * Exit a parse tree produced by {@link IlpParser#intLiteral}.
	 * @param ctx the parse tree
	 */
	void exitIntLiteral(IlpParser.IntLiteralContext ctx);
}