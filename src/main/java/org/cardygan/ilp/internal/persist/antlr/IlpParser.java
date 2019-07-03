// Generated from Ilp.g4 by ANTLR 4.7.2
package org.cardygan.ilp.internal.persist.antlr;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class IlpParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.7.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, T__7=8, T__8=9, 
		T__9=10, T__10=11, T__11=12, T__12=13, T__13=14, T__14=15, T__15=16, T__16=17, 
		T__17=18, T__18=19, T__19=20, T__20=21, T__21=22, T__22=23, T__23=24, 
		T__24=25, T__25=26, T__26=27, COMMA=28, LEFT_CURLY=29, RIGHT_CURLY=30, 
		LEFT_BRACKET=31, RIGHT_BRACKET=32, INF=33, ID=34, STRING=35, NUMBER=36, 
		INTEGER=37, WS=38, COMMENT=39, LINE_COMMENT=40;
	public static final int
		RULE_model = 0, RULE_vars = 1, RULE_varDecl = 2, RULE_intervalInt = 3, 
		RULE_intervalReal = 4, RULE_lbInt = 5, RULE_ubInt = 6, RULE_integer = 7, 
		RULE_inf = 8, RULE_lbReal = 9, RULE_ubReal = 10, RULE_varName = 11, RULE_obj = 12, 
		RULE_genCstrs = 13, RULE_cstrs = 14, RULE_cstrName = 15, RULE_sos1Cstrs = 16, 
		RULE_sos1 = 17, RULE_sos1Element = 18, RULE_var = 19, RULE_weight = 20, 
		RULE_boolExpr = 21, RULE_relOp = 22, RULE_arithExpr = 23, RULE_realLiteral = 24, 
		RULE_intLiteral = 25;
	private static String[] makeRuleNames() {
		return new String[] {
			"model", "vars", "varDecl", "intervalInt", "intervalReal", "lbInt", "ubInt", 
			"integer", "inf", "lbReal", "ubReal", "varName", "obj", "genCstrs", "cstrs", 
			"cstrName", "sos1Cstrs", "sos1", "sos1Element", "var", "weight", "boolExpr", 
			"relOp", "arithExpr", "realLiteral", "intLiteral"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'vars'", "'obj'", "'genCstrs'", "'cstrs'", "'sos1'", "':Int'", 
			"':Real'", "':Bin'", "'min'", "'max'", "':'", "';'", "'('", "')'", "'&&'", 
			"'||'", "'xor'", "'XOR'", "'Xor'", "'<=>'", "'=>'", "'!'", "'>='", "'<='", 
			"'='", "'+'", "'-'", "','", "'{'", "'}'", "'['", "']'", "'*'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, "COMMA", "LEFT_CURLY", "RIGHT_CURLY", "LEFT_BRACKET", 
			"RIGHT_BRACKET", "INF", "ID", "STRING", "NUMBER", "INTEGER", "WS", "COMMENT", 
			"LINE_COMMENT"
		};
	}
	private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}

	@Override
	public String getGrammarFileName() { return "Ilp.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public IlpParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	public static class ModelContext extends ParserRuleContext {
		public List<TerminalNode> LEFT_CURLY() { return getTokens(IlpParser.LEFT_CURLY); }
		public TerminalNode LEFT_CURLY(int i) {
			return getToken(IlpParser.LEFT_CURLY, i);
		}
		public VarsContext vars() {
			return getRuleContext(VarsContext.class,0);
		}
		public List<TerminalNode> RIGHT_CURLY() { return getTokens(IlpParser.RIGHT_CURLY); }
		public TerminalNode RIGHT_CURLY(int i) {
			return getToken(IlpParser.RIGHT_CURLY, i);
		}
		public ObjContext obj() {
			return getRuleContext(ObjContext.class,0);
		}
		public GenCstrsContext genCstrs() {
			return getRuleContext(GenCstrsContext.class,0);
		}
		public CstrsContext cstrs() {
			return getRuleContext(CstrsContext.class,0);
		}
		public Sos1CstrsContext sos1Cstrs() {
			return getRuleContext(Sos1CstrsContext.class,0);
		}
		public ModelContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_model; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof IlpListener ) ((IlpListener)listener).enterModel(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof IlpListener ) ((IlpListener)listener).exitModel(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof IlpVisitor ) return ((IlpVisitor<? extends T>)visitor).visitModel(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ModelContext model() throws RecognitionException {
		ModelContext _localctx = new ModelContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_model);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(52);
			match(T__0);
			setState(53);
			match(LEFT_CURLY);
			setState(54);
			vars();
			setState(55);
			match(RIGHT_CURLY);
			}
			setState(62);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__1) {
				{
				setState(57);
				match(T__1);
				setState(58);
				match(LEFT_CURLY);
				setState(59);
				obj();
				setState(60);
				match(RIGHT_CURLY);
				}
			}

			setState(69);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__2) {
				{
				setState(64);
				match(T__2);
				setState(65);
				match(LEFT_CURLY);
				setState(66);
				genCstrs();
				setState(67);
				match(RIGHT_CURLY);
				}
			}

			setState(76);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__3) {
				{
				setState(71);
				match(T__3);
				setState(72);
				match(LEFT_CURLY);
				setState(73);
				cstrs();
				setState(74);
				match(RIGHT_CURLY);
				}
			}

			setState(83);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__4) {
				{
				setState(78);
				match(T__4);
				setState(79);
				match(LEFT_CURLY);
				setState(80);
				sos1Cstrs();
				setState(81);
				match(RIGHT_CURLY);
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class VarsContext extends ParserRuleContext {
		public List<VarDeclContext> varDecl() {
			return getRuleContexts(VarDeclContext.class);
		}
		public VarDeclContext varDecl(int i) {
			return getRuleContext(VarDeclContext.class,i);
		}
		public VarsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_vars; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof IlpListener ) ((IlpListener)listener).enterVars(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof IlpListener ) ((IlpListener)listener).exitVars(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof IlpVisitor ) return ((IlpVisitor<? extends T>)visitor).visitVars(this);
			else return visitor.visitChildren(this);
		}
	}

	public final VarsContext vars() throws RecognitionException {
		VarsContext _localctx = new VarsContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_vars);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(88);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==ID) {
				{
				{
				setState(85);
				varDecl();
				}
				}
				setState(90);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class VarDeclContext extends ParserRuleContext {
		public VarDeclContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_varDecl; }
	 
		public VarDeclContext() { }
		public void copyFrom(VarDeclContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class RealVarContext extends VarDeclContext {
		public VarNameContext varName() {
			return getRuleContext(VarNameContext.class,0);
		}
		public IntervalRealContext intervalReal() {
			return getRuleContext(IntervalRealContext.class,0);
		}
		public RealVarContext(VarDeclContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof IlpListener ) ((IlpListener)listener).enterRealVar(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof IlpListener ) ((IlpListener)listener).exitRealVar(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof IlpVisitor ) return ((IlpVisitor<? extends T>)visitor).visitRealVar(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class BinVarContext extends VarDeclContext {
		public VarNameContext varName() {
			return getRuleContext(VarNameContext.class,0);
		}
		public BinVarContext(VarDeclContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof IlpListener ) ((IlpListener)listener).enterBinVar(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof IlpListener ) ((IlpListener)listener).exitBinVar(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof IlpVisitor ) return ((IlpVisitor<? extends T>)visitor).visitBinVar(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class IntVarContext extends VarDeclContext {
		public VarNameContext varName() {
			return getRuleContext(VarNameContext.class,0);
		}
		public IntervalIntContext intervalInt() {
			return getRuleContext(IntervalIntContext.class,0);
		}
		public IntVarContext(VarDeclContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof IlpListener ) ((IlpListener)listener).enterIntVar(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof IlpListener ) ((IlpListener)listener).exitIntVar(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof IlpVisitor ) return ((IlpVisitor<? extends T>)visitor).visitIntVar(this);
			else return visitor.visitChildren(this);
		}
	}

	public final VarDeclContext varDecl() throws RecognitionException {
		VarDeclContext _localctx = new VarDeclContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_varDecl);
		int _la;
		try {
			setState(104);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,7,_ctx) ) {
			case 1:
				_localctx = new IntVarContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(91);
				varName();
				setState(92);
				match(T__5);
				setState(94);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==LEFT_BRACKET) {
					{
					setState(93);
					intervalInt();
					}
				}

				}
				break;
			case 2:
				_localctx = new RealVarContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(96);
				varName();
				setState(97);
				match(T__6);
				setState(99);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==LEFT_BRACKET) {
					{
					setState(98);
					intervalReal();
					}
				}

				}
				break;
			case 3:
				_localctx = new BinVarContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(101);
				varName();
				setState(102);
				match(T__7);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class IntervalIntContext extends ParserRuleContext {
		public TerminalNode LEFT_BRACKET() { return getToken(IlpParser.LEFT_BRACKET, 0); }
		public LbIntContext lbInt() {
			return getRuleContext(LbIntContext.class,0);
		}
		public TerminalNode COMMA() { return getToken(IlpParser.COMMA, 0); }
		public UbIntContext ubInt() {
			return getRuleContext(UbIntContext.class,0);
		}
		public TerminalNode RIGHT_BRACKET() { return getToken(IlpParser.RIGHT_BRACKET, 0); }
		public IntervalIntContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_intervalInt; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof IlpListener ) ((IlpListener)listener).enterIntervalInt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof IlpListener ) ((IlpListener)listener).exitIntervalInt(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof IlpVisitor ) return ((IlpVisitor<? extends T>)visitor).visitIntervalInt(this);
			else return visitor.visitChildren(this);
		}
	}

	public final IntervalIntContext intervalInt() throws RecognitionException {
		IntervalIntContext _localctx = new IntervalIntContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_intervalInt);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(106);
			match(LEFT_BRACKET);
			setState(107);
			lbInt();
			setState(108);
			match(COMMA);
			setState(109);
			ubInt();
			setState(110);
			match(RIGHT_BRACKET);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class IntervalRealContext extends ParserRuleContext {
		public TerminalNode LEFT_BRACKET() { return getToken(IlpParser.LEFT_BRACKET, 0); }
		public LbRealContext lbReal() {
			return getRuleContext(LbRealContext.class,0);
		}
		public TerminalNode COMMA() { return getToken(IlpParser.COMMA, 0); }
		public UbRealContext ubReal() {
			return getRuleContext(UbRealContext.class,0);
		}
		public TerminalNode RIGHT_BRACKET() { return getToken(IlpParser.RIGHT_BRACKET, 0); }
		public IntervalRealContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_intervalReal; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof IlpListener ) ((IlpListener)listener).enterIntervalReal(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof IlpListener ) ((IlpListener)listener).exitIntervalReal(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof IlpVisitor ) return ((IlpVisitor<? extends T>)visitor).visitIntervalReal(this);
			else return visitor.visitChildren(this);
		}
	}

	public final IntervalRealContext intervalReal() throws RecognitionException {
		IntervalRealContext _localctx = new IntervalRealContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_intervalReal);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(112);
			match(LEFT_BRACKET);
			setState(113);
			lbReal();
			setState(114);
			match(COMMA);
			setState(115);
			ubReal();
			setState(116);
			match(RIGHT_BRACKET);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class LbIntContext extends ParserRuleContext {
		public IntegerContext integer() {
			return getRuleContext(IntegerContext.class,0);
		}
		public LbIntContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_lbInt; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof IlpListener ) ((IlpListener)listener).enterLbInt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof IlpListener ) ((IlpListener)listener).exitLbInt(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof IlpVisitor ) return ((IlpVisitor<? extends T>)visitor).visitLbInt(this);
			else return visitor.visitChildren(this);
		}
	}

	public final LbIntContext lbInt() throws RecognitionException {
		LbIntContext _localctx = new LbIntContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_lbInt);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(118);
			integer();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class UbIntContext extends ParserRuleContext {
		public InfContext inf() {
			return getRuleContext(InfContext.class,0);
		}
		public IntegerContext integer() {
			return getRuleContext(IntegerContext.class,0);
		}
		public UbIntContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_ubInt; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof IlpListener ) ((IlpListener)listener).enterUbInt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof IlpListener ) ((IlpListener)listener).exitUbInt(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof IlpVisitor ) return ((IlpVisitor<? extends T>)visitor).visitUbInt(this);
			else return visitor.visitChildren(this);
		}
	}

	public final UbIntContext ubInt() throws RecognitionException {
		UbIntContext _localctx = new UbIntContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_ubInt);
		try {
			setState(122);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case INF:
				enterOuterAlt(_localctx, 1);
				{
				setState(120);
				inf();
				}
				break;
			case INTEGER:
				enterOuterAlt(_localctx, 2);
				{
				setState(121);
				integer();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class IntegerContext extends ParserRuleContext {
		public TerminalNode INTEGER() { return getToken(IlpParser.INTEGER, 0); }
		public IntegerContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_integer; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof IlpListener ) ((IlpListener)listener).enterInteger(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof IlpListener ) ((IlpListener)listener).exitInteger(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof IlpVisitor ) return ((IlpVisitor<? extends T>)visitor).visitInteger(this);
			else return visitor.visitChildren(this);
		}
	}

	public final IntegerContext integer() throws RecognitionException {
		IntegerContext _localctx = new IntegerContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_integer);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(124);
			match(INTEGER);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class InfContext extends ParserRuleContext {
		public TerminalNode INF() { return getToken(IlpParser.INF, 0); }
		public InfContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_inf; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof IlpListener ) ((IlpListener)listener).enterInf(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof IlpListener ) ((IlpListener)listener).exitInf(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof IlpVisitor ) return ((IlpVisitor<? extends T>)visitor).visitInf(this);
			else return visitor.visitChildren(this);
		}
	}

	public final InfContext inf() throws RecognitionException {
		InfContext _localctx = new InfContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_inf);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(126);
			match(INF);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class LbRealContext extends ParserRuleContext {
		public TerminalNode NUMBER() { return getToken(IlpParser.NUMBER, 0); }
		public LbRealContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_lbReal; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof IlpListener ) ((IlpListener)listener).enterLbReal(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof IlpListener ) ((IlpListener)listener).exitLbReal(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof IlpVisitor ) return ((IlpVisitor<? extends T>)visitor).visitLbReal(this);
			else return visitor.visitChildren(this);
		}
	}

	public final LbRealContext lbReal() throws RecognitionException {
		LbRealContext _localctx = new LbRealContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_lbReal);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(128);
			match(NUMBER);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class UbRealContext extends ParserRuleContext {
		public TerminalNode NUMBER() { return getToken(IlpParser.NUMBER, 0); }
		public TerminalNode INF() { return getToken(IlpParser.INF, 0); }
		public UbRealContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_ubReal; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof IlpListener ) ((IlpListener)listener).enterUbReal(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof IlpListener ) ((IlpListener)listener).exitUbReal(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof IlpVisitor ) return ((IlpVisitor<? extends T>)visitor).visitUbReal(this);
			else return visitor.visitChildren(this);
		}
	}

	public final UbRealContext ubReal() throws RecognitionException {
		UbRealContext _localctx = new UbRealContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_ubReal);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(130);
			_la = _input.LA(1);
			if ( !(_la==INF || _la==NUMBER) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class VarNameContext extends ParserRuleContext {
		public TerminalNode ID() { return getToken(IlpParser.ID, 0); }
		public VarNameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_varName; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof IlpListener ) ((IlpListener)listener).enterVarName(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof IlpListener ) ((IlpListener)listener).exitVarName(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof IlpVisitor ) return ((IlpVisitor<? extends T>)visitor).visitVarName(this);
			else return visitor.visitChildren(this);
		}
	}

	public final VarNameContext varName() throws RecognitionException {
		VarNameContext _localctx = new VarNameContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_varName);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(132);
			match(ID);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ObjContext extends ParserRuleContext {
		public ObjContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_obj; }
	 
		public ObjContext() { }
		public void copyFrom(ObjContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class MaxObjContext extends ObjContext {
		public ArithExprContext arithExpr() {
			return getRuleContext(ArithExprContext.class,0);
		}
		public MaxObjContext(ObjContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof IlpListener ) ((IlpListener)listener).enterMaxObj(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof IlpListener ) ((IlpListener)listener).exitMaxObj(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof IlpVisitor ) return ((IlpVisitor<? extends T>)visitor).visitMaxObj(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class MinObjContext extends ObjContext {
		public ArithExprContext arithExpr() {
			return getRuleContext(ArithExprContext.class,0);
		}
		public MinObjContext(ObjContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof IlpListener ) ((IlpListener)listener).enterMinObj(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof IlpListener ) ((IlpListener)listener).exitMinObj(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof IlpVisitor ) return ((IlpVisitor<? extends T>)visitor).visitMinObj(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ObjContext obj() throws RecognitionException {
		ObjContext _localctx = new ObjContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_obj);
		try {
			setState(138);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__8:
				_localctx = new MinObjContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(134);
				match(T__8);
				setState(135);
				arithExpr(0);
				}
				break;
			case T__9:
				_localctx = new MaxObjContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(136);
				match(T__9);
				setState(137);
				arithExpr(0);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class GenCstrsContext extends ParserRuleContext {
		public List<BoolExprContext> boolExpr() {
			return getRuleContexts(BoolExprContext.class);
		}
		public BoolExprContext boolExpr(int i) {
			return getRuleContext(BoolExprContext.class,i);
		}
		public List<CstrNameContext> cstrName() {
			return getRuleContexts(CstrNameContext.class);
		}
		public CstrNameContext cstrName(int i) {
			return getRuleContext(CstrNameContext.class,i);
		}
		public GenCstrsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_genCstrs; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof IlpListener ) ((IlpListener)listener).enterGenCstrs(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof IlpListener ) ((IlpListener)listener).exitGenCstrs(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof IlpVisitor ) return ((IlpVisitor<? extends T>)visitor).visitGenCstrs(this);
			else return visitor.visitChildren(this);
		}
	}

	public final GenCstrsContext genCstrs() throws RecognitionException {
		GenCstrsContext _localctx = new GenCstrsContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_genCstrs);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(150);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__12) | (1L << T__21) | (1L << T__26) | (1L << ID) | (1L << NUMBER) | (1L << INTEGER))) != 0)) {
				{
				{
				setState(143);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,10,_ctx) ) {
				case 1:
					{
					setState(140);
					cstrName();
					setState(141);
					match(T__10);
					}
					break;
				}
				setState(145);
				boolExpr(0);
				setState(146);
				match(T__11);
				}
				}
				setState(152);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class CstrsContext extends ParserRuleContext {
		public List<RelOpContext> relOp() {
			return getRuleContexts(RelOpContext.class);
		}
		public RelOpContext relOp(int i) {
			return getRuleContext(RelOpContext.class,i);
		}
		public List<CstrNameContext> cstrName() {
			return getRuleContexts(CstrNameContext.class);
		}
		public CstrNameContext cstrName(int i) {
			return getRuleContext(CstrNameContext.class,i);
		}
		public CstrsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_cstrs; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof IlpListener ) ((IlpListener)listener).enterCstrs(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof IlpListener ) ((IlpListener)listener).exitCstrs(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof IlpVisitor ) return ((IlpVisitor<? extends T>)visitor).visitCstrs(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CstrsContext cstrs() throws RecognitionException {
		CstrsContext _localctx = new CstrsContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_cstrs);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(163);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__12) | (1L << T__26) | (1L << ID) | (1L << NUMBER) | (1L << INTEGER))) != 0)) {
				{
				{
				setState(156);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,12,_ctx) ) {
				case 1:
					{
					setState(153);
					cstrName();
					setState(154);
					match(T__10);
					}
					break;
				}
				setState(158);
				relOp();
				setState(159);
				match(T__11);
				}
				}
				setState(165);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class CstrNameContext extends ParserRuleContext {
		public TerminalNode ID() { return getToken(IlpParser.ID, 0); }
		public CstrNameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_cstrName; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof IlpListener ) ((IlpListener)listener).enterCstrName(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof IlpListener ) ((IlpListener)listener).exitCstrName(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof IlpVisitor ) return ((IlpVisitor<? extends T>)visitor).visitCstrName(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CstrNameContext cstrName() throws RecognitionException {
		CstrNameContext _localctx = new CstrNameContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_cstrName);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(166);
			match(ID);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Sos1CstrsContext extends ParserRuleContext {
		public List<Sos1Context> sos1() {
			return getRuleContexts(Sos1Context.class);
		}
		public Sos1Context sos1(int i) {
			return getRuleContext(Sos1Context.class,i);
		}
		public Sos1CstrsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_sos1Cstrs; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof IlpListener ) ((IlpListener)listener).enterSos1Cstrs(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof IlpListener ) ((IlpListener)listener).exitSos1Cstrs(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof IlpVisitor ) return ((IlpVisitor<? extends T>)visitor).visitSos1Cstrs(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Sos1CstrsContext sos1Cstrs() throws RecognitionException {
		Sos1CstrsContext _localctx = new Sos1CstrsContext(_ctx, getState());
		enterRule(_localctx, 32, RULE_sos1Cstrs);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(171);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==NUMBER) {
				{
				{
				setState(168);
				sos1();
				}
				}
				setState(173);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Sos1Context extends ParserRuleContext {
		public List<Sos1ElementContext> sos1Element() {
			return getRuleContexts(Sos1ElementContext.class);
		}
		public Sos1ElementContext sos1Element(int i) {
			return getRuleContext(Sos1ElementContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(IlpParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(IlpParser.COMMA, i);
		}
		public Sos1Context(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_sos1; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof IlpListener ) ((IlpListener)listener).enterSos1(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof IlpListener ) ((IlpListener)listener).exitSos1(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof IlpVisitor ) return ((IlpVisitor<? extends T>)visitor).visitSos1(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Sos1Context sos1() throws RecognitionException {
		Sos1Context _localctx = new Sos1Context(_ctx, getState());
		enterRule(_localctx, 34, RULE_sos1);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(174);
			sos1Element();
			setState(179);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(175);
				match(COMMA);
				setState(176);
				sos1Element();
				}
				}
				setState(181);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(182);
			match(T__11);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Sos1ElementContext extends ParserRuleContext {
		public WeightContext weight() {
			return getRuleContext(WeightContext.class,0);
		}
		public VarContext var() {
			return getRuleContext(VarContext.class,0);
		}
		public Sos1ElementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_sos1Element; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof IlpListener ) ((IlpListener)listener).enterSos1Element(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof IlpListener ) ((IlpListener)listener).exitSos1Element(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof IlpVisitor ) return ((IlpVisitor<? extends T>)visitor).visitSos1Element(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Sos1ElementContext sos1Element() throws RecognitionException {
		Sos1ElementContext _localctx = new Sos1ElementContext(_ctx, getState());
		enterRule(_localctx, 36, RULE_sos1Element);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(184);
			weight();
			setState(185);
			var();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class VarContext extends ParserRuleContext {
		public VarNameContext varName() {
			return getRuleContext(VarNameContext.class,0);
		}
		public VarContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_var; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof IlpListener ) ((IlpListener)listener).enterVar(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof IlpListener ) ((IlpListener)listener).exitVar(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof IlpVisitor ) return ((IlpVisitor<? extends T>)visitor).visitVar(this);
			else return visitor.visitChildren(this);
		}
	}

	public final VarContext var() throws RecognitionException {
		VarContext _localctx = new VarContext(_ctx, getState());
		enterRule(_localctx, 38, RULE_var);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(187);
			varName();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class WeightContext extends ParserRuleContext {
		public TerminalNode NUMBER() { return getToken(IlpParser.NUMBER, 0); }
		public WeightContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_weight; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof IlpListener ) ((IlpListener)listener).enterWeight(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof IlpListener ) ((IlpListener)listener).exitWeight(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof IlpVisitor ) return ((IlpVisitor<? extends T>)visitor).visitWeight(this);
			else return visitor.visitChildren(this);
		}
	}

	public final WeightContext weight() throws RecognitionException {
		WeightContext _localctx = new WeightContext(_ctx, getState());
		enterRule(_localctx, 40, RULE_weight);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(189);
			match(NUMBER);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class BoolExprContext extends ParserRuleContext {
		public BoolExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_boolExpr; }
	 
		public BoolExprContext() { }
		public void copyFrom(BoolExprContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class NotContext extends BoolExprContext {
		public BoolExprContext boolExpr() {
			return getRuleContext(BoolExprContext.class,0);
		}
		public NotContext(BoolExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof IlpListener ) ((IlpListener)listener).enterNot(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof IlpListener ) ((IlpListener)listener).exitNot(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof IlpVisitor ) return ((IlpVisitor<? extends T>)visitor).visitNot(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ImplContext extends BoolExprContext {
		public List<BoolExprContext> boolExpr() {
			return getRuleContexts(BoolExprContext.class);
		}
		public BoolExprContext boolExpr(int i) {
			return getRuleContext(BoolExprContext.class,i);
		}
		public ImplContext(BoolExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof IlpListener ) ((IlpListener)listener).enterImpl(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof IlpListener ) ((IlpListener)listener).exitImpl(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof IlpVisitor ) return ((IlpVisitor<? extends T>)visitor).visitImpl(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class OrContext extends BoolExprContext {
		public List<BoolExprContext> boolExpr() {
			return getRuleContexts(BoolExprContext.class);
		}
		public BoolExprContext boolExpr(int i) {
			return getRuleContext(BoolExprContext.class,i);
		}
		public OrContext(BoolExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof IlpListener ) ((IlpListener)listener).enterOr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof IlpListener ) ((IlpListener)listener).exitOr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof IlpVisitor ) return ((IlpVisitor<? extends T>)visitor).visitOr(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class BoolBracketsContext extends BoolExprContext {
		public BoolExprContext boolExpr() {
			return getRuleContext(BoolExprContext.class,0);
		}
		public BoolBracketsContext(BoolExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof IlpListener ) ((IlpListener)listener).enterBoolBrackets(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof IlpListener ) ((IlpListener)listener).exitBoolBrackets(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof IlpVisitor ) return ((IlpVisitor<? extends T>)visitor).visitBoolBrackets(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class BoolLitContext extends BoolExprContext {
		public VarContext var() {
			return getRuleContext(VarContext.class,0);
		}
		public BoolLitContext(BoolExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof IlpListener ) ((IlpListener)listener).enterBoolLit(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof IlpListener ) ((IlpListener)listener).exitBoolLit(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof IlpVisitor ) return ((IlpVisitor<? extends T>)visitor).visitBoolLit(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class AndContext extends BoolExprContext {
		public List<BoolExprContext> boolExpr() {
			return getRuleContexts(BoolExprContext.class);
		}
		public BoolExprContext boolExpr(int i) {
			return getRuleContext(BoolExprContext.class,i);
		}
		public AndContext(BoolExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof IlpListener ) ((IlpListener)listener).enterAnd(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof IlpListener ) ((IlpListener)listener).exitAnd(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof IlpVisitor ) return ((IlpVisitor<? extends T>)visitor).visitAnd(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class RelContext extends BoolExprContext {
		public RelOpContext relOp() {
			return getRuleContext(RelOpContext.class,0);
		}
		public RelContext(BoolExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof IlpListener ) ((IlpListener)listener).enterRel(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof IlpListener ) ((IlpListener)listener).exitRel(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof IlpVisitor ) return ((IlpVisitor<? extends T>)visitor).visitRel(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class XOrContext extends BoolExprContext {
		public List<BoolExprContext> boolExpr() {
			return getRuleContexts(BoolExprContext.class);
		}
		public BoolExprContext boolExpr(int i) {
			return getRuleContext(BoolExprContext.class,i);
		}
		public XOrContext(BoolExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof IlpListener ) ((IlpListener)listener).enterXOr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof IlpListener ) ((IlpListener)listener).exitXOr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof IlpVisitor ) return ((IlpVisitor<? extends T>)visitor).visitXOr(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class BiImplContext extends BoolExprContext {
		public List<BoolExprContext> boolExpr() {
			return getRuleContexts(BoolExprContext.class);
		}
		public BoolExprContext boolExpr(int i) {
			return getRuleContext(BoolExprContext.class,i);
		}
		public BiImplContext(BoolExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof IlpListener ) ((IlpListener)listener).enterBiImpl(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof IlpListener ) ((IlpListener)listener).exitBiImpl(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof IlpVisitor ) return ((IlpVisitor<? extends T>)visitor).visitBiImpl(this);
			else return visitor.visitChildren(this);
		}
	}

	public final BoolExprContext boolExpr() throws RecognitionException {
		return boolExpr(0);
	}

	private BoolExprContext boolExpr(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		BoolExprContext _localctx = new BoolExprContext(_ctx, _parentState);
		BoolExprContext _prevctx = _localctx;
		int _startState = 42;
		enterRecursionRule(_localctx, 42, RULE_boolExpr, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(200);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,16,_ctx) ) {
			case 1:
				{
				_localctx = new BoolBracketsContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(192);
				match(T__12);
				setState(193);
				boolExpr(0);
				setState(194);
				match(T__13);
				}
				break;
			case 2:
				{
				_localctx = new NotContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(196);
				match(T__21);
				setState(197);
				boolExpr(3);
				}
				break;
			case 3:
				{
				_localctx = new BoolLitContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(198);
				var();
				}
				break;
			case 4:
				{
				_localctx = new RelContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(199);
				relOp();
				}
				break;
			}
			_ctx.stop = _input.LT(-1);
			setState(219);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,18,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(217);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,17,_ctx) ) {
					case 1:
						{
						_localctx = new AndContext(new BoolExprContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_boolExpr);
						setState(202);
						if (!(precpred(_ctx, 8))) throw new FailedPredicateException(this, "precpred(_ctx, 8)");
						setState(203);
						match(T__14);
						setState(204);
						boolExpr(9);
						}
						break;
					case 2:
						{
						_localctx = new OrContext(new BoolExprContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_boolExpr);
						setState(205);
						if (!(precpred(_ctx, 7))) throw new FailedPredicateException(this, "precpred(_ctx, 7)");
						setState(206);
						match(T__15);
						setState(207);
						boolExpr(8);
						}
						break;
					case 3:
						{
						_localctx = new XOrContext(new BoolExprContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_boolExpr);
						setState(208);
						if (!(precpred(_ctx, 6))) throw new FailedPredicateException(this, "precpred(_ctx, 6)");
						setState(209);
						_la = _input.LA(1);
						if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__16) | (1L << T__17) | (1L << T__18))) != 0)) ) {
						_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(210);
						boolExpr(7);
						}
						break;
					case 4:
						{
						_localctx = new BiImplContext(new BoolExprContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_boolExpr);
						setState(211);
						if (!(precpred(_ctx, 5))) throw new FailedPredicateException(this, "precpred(_ctx, 5)");
						setState(212);
						match(T__19);
						setState(213);
						boolExpr(6);
						}
						break;
					case 5:
						{
						_localctx = new ImplContext(new BoolExprContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_boolExpr);
						setState(214);
						if (!(precpred(_ctx, 4))) throw new FailedPredicateException(this, "precpred(_ctx, 4)");
						setState(215);
						match(T__20);
						setState(216);
						boolExpr(5);
						}
						break;
					}
					} 
				}
				setState(221);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,18,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	public static class RelOpContext extends ParserRuleContext {
		public RelOpContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_relOp; }
	 
		public RelOpContext() { }
		public void copyFrom(RelOpContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class LeContext extends RelOpContext {
		public List<ArithExprContext> arithExpr() {
			return getRuleContexts(ArithExprContext.class);
		}
		public ArithExprContext arithExpr(int i) {
			return getRuleContext(ArithExprContext.class,i);
		}
		public LeContext(RelOpContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof IlpListener ) ((IlpListener)listener).enterLe(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof IlpListener ) ((IlpListener)listener).exitLe(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof IlpVisitor ) return ((IlpVisitor<? extends T>)visitor).visitLe(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class EqContext extends RelOpContext {
		public List<ArithExprContext> arithExpr() {
			return getRuleContexts(ArithExprContext.class);
		}
		public ArithExprContext arithExpr(int i) {
			return getRuleContext(ArithExprContext.class,i);
		}
		public EqContext(RelOpContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof IlpListener ) ((IlpListener)listener).enterEq(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof IlpListener ) ((IlpListener)listener).exitEq(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof IlpVisitor ) return ((IlpVisitor<? extends T>)visitor).visitEq(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class GeContext extends RelOpContext {
		public List<ArithExprContext> arithExpr() {
			return getRuleContexts(ArithExprContext.class);
		}
		public ArithExprContext arithExpr(int i) {
			return getRuleContext(ArithExprContext.class,i);
		}
		public GeContext(RelOpContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof IlpListener ) ((IlpListener)listener).enterGe(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof IlpListener ) ((IlpListener)listener).exitGe(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof IlpVisitor ) return ((IlpVisitor<? extends T>)visitor).visitGe(this);
			else return visitor.visitChildren(this);
		}
	}

	public final RelOpContext relOp() throws RecognitionException {
		RelOpContext _localctx = new RelOpContext(_ctx, getState());
		enterRule(_localctx, 44, RULE_relOp);
		try {
			setState(234);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,19,_ctx) ) {
			case 1:
				_localctx = new GeContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(222);
				arithExpr(0);
				setState(223);
				match(T__22);
				setState(224);
				arithExpr(0);
				}
				break;
			case 2:
				_localctx = new LeContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(226);
				arithExpr(0);
				setState(227);
				match(T__23);
				setState(228);
				arithExpr(0);
				}
				break;
			case 3:
				_localctx = new EqContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(230);
				arithExpr(0);
				setState(231);
				match(T__24);
				setState(232);
				arithExpr(0);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ArithExprContext extends ParserRuleContext {
		public ArithExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_arithExpr; }
	 
		public ArithExprContext() { }
		public void copyFrom(ArithExprContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class RealLitContext extends ArithExprContext {
		public RealLiteralContext realLiteral() {
			return getRuleContext(RealLiteralContext.class,0);
		}
		public RealLitContext(ArithExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof IlpListener ) ((IlpListener)listener).enterRealLit(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof IlpListener ) ((IlpListener)listener).exitRealLit(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof IlpVisitor ) return ((IlpVisitor<? extends T>)visitor).visitRealLit(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class NegContext extends ArithExprContext {
		public ArithExprContext arithExpr() {
			return getRuleContext(ArithExprContext.class,0);
		}
		public NegContext(ArithExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof IlpListener ) ((IlpListener)listener).enterNeg(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof IlpListener ) ((IlpListener)listener).exitNeg(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof IlpVisitor ) return ((IlpVisitor<? extends T>)visitor).visitNeg(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ArithVarContext extends ArithExprContext {
		public VarContext var() {
			return getRuleContext(VarContext.class,0);
		}
		public ArithVarContext(ArithExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof IlpListener ) ((IlpListener)listener).enterArithVar(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof IlpListener ) ((IlpListener)listener).exitArithVar(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof IlpVisitor ) return ((IlpVisitor<? extends T>)visitor).visitArithVar(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class MultContext extends ArithExprContext {
		public List<ArithExprContext> arithExpr() {
			return getRuleContexts(ArithExprContext.class);
		}
		public ArithExprContext arithExpr(int i) {
			return getRuleContext(ArithExprContext.class,i);
		}
		public MultContext(ArithExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof IlpListener ) ((IlpListener)listener).enterMult(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof IlpListener ) ((IlpListener)listener).exitMult(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof IlpVisitor ) return ((IlpVisitor<? extends T>)visitor).visitMult(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ArithBracketsContext extends ArithExprContext {
		public ArithExprContext arithExpr() {
			return getRuleContext(ArithExprContext.class,0);
		}
		public ArithBracketsContext(ArithExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof IlpListener ) ((IlpListener)listener).enterArithBrackets(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof IlpListener ) ((IlpListener)listener).exitArithBrackets(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof IlpVisitor ) return ((IlpVisitor<? extends T>)visitor).visitArithBrackets(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class IntLitContext extends ArithExprContext {
		public IntLiteralContext intLiteral() {
			return getRuleContext(IntLiteralContext.class,0);
		}
		public IntLitContext(ArithExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof IlpListener ) ((IlpListener)listener).enterIntLit(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof IlpListener ) ((IlpListener)listener).exitIntLit(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof IlpVisitor ) return ((IlpVisitor<? extends T>)visitor).visitIntLit(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class SumContext extends ArithExprContext {
		public List<ArithExprContext> arithExpr() {
			return getRuleContexts(ArithExprContext.class);
		}
		public ArithExprContext arithExpr(int i) {
			return getRuleContext(ArithExprContext.class,i);
		}
		public SumContext(ArithExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof IlpListener ) ((IlpListener)listener).enterSum(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof IlpListener ) ((IlpListener)listener).exitSum(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof IlpVisitor ) return ((IlpVisitor<? extends T>)visitor).visitSum(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ArithExprContext arithExpr() throws RecognitionException {
		return arithExpr(0);
	}

	private ArithExprContext arithExpr(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		ArithExprContext _localctx = new ArithExprContext(_ctx, _parentState);
		ArithExprContext _prevctx = _localctx;
		int _startState = 46;
		enterRecursionRule(_localctx, 46, RULE_arithExpr, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(246);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__12:
				{
				_localctx = new ArithBracketsContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(237);
				match(T__12);
				setState(238);
				arithExpr(0);
				setState(239);
				match(T__13);
				}
				break;
			case ID:
				{
				_localctx = new ArithVarContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(241);
				var();
				}
				break;
			case T__26:
				{
				_localctx = new NegContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(242);
				match(T__26);
				setState(243);
				arithExpr(3);
				}
				break;
			case NUMBER:
				{
				_localctx = new RealLitContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(244);
				realLiteral();
				}
				break;
			case INTEGER:
				{
				_localctx = new IntLitContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(245);
				intLiteral();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			_ctx.stop = _input.LT(-1);
			setState(255);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,22,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(253);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,21,_ctx) ) {
					case 1:
						{
						_localctx = new MultContext(new ArithExprContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_arithExpr);
						setState(248);
						if (!(precpred(_ctx, 7))) throw new FailedPredicateException(this, "precpred(_ctx, 7)");
						setState(249);
						arithExpr(8);
						}
						break;
					case 2:
						{
						_localctx = new SumContext(new ArithExprContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_arithExpr);
						setState(250);
						if (!(precpred(_ctx, 6))) throw new FailedPredicateException(this, "precpred(_ctx, 6)");
						setState(251);
						match(T__25);
						setState(252);
						arithExpr(7);
						}
						break;
					}
					} 
				}
				setState(257);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,22,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	public static class RealLiteralContext extends ParserRuleContext {
		public TerminalNode NUMBER() { return getToken(IlpParser.NUMBER, 0); }
		public RealLiteralContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_realLiteral; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof IlpListener ) ((IlpListener)listener).enterRealLiteral(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof IlpListener ) ((IlpListener)listener).exitRealLiteral(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof IlpVisitor ) return ((IlpVisitor<? extends T>)visitor).visitRealLiteral(this);
			else return visitor.visitChildren(this);
		}
	}

	public final RealLiteralContext realLiteral() throws RecognitionException {
		RealLiteralContext _localctx = new RealLiteralContext(_ctx, getState());
		enterRule(_localctx, 48, RULE_realLiteral);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(258);
			match(NUMBER);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class IntLiteralContext extends ParserRuleContext {
		public TerminalNode INTEGER() { return getToken(IlpParser.INTEGER, 0); }
		public IntLiteralContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_intLiteral; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof IlpListener ) ((IlpListener)listener).enterIntLiteral(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof IlpListener ) ((IlpListener)listener).exitIntLiteral(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof IlpVisitor ) return ((IlpVisitor<? extends T>)visitor).visitIntLiteral(this);
			else return visitor.visitChildren(this);
		}
	}

	public final IntLiteralContext intLiteral() throws RecognitionException {
		IntLiteralContext _localctx = new IntLiteralContext(_ctx, getState());
		enterRule(_localctx, 50, RULE_intLiteral);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(260);
			match(INTEGER);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public boolean sempred(RuleContext _localctx, int ruleIndex, int predIndex) {
		switch (ruleIndex) {
		case 21:
			return boolExpr_sempred((BoolExprContext)_localctx, predIndex);
		case 23:
			return arithExpr_sempred((ArithExprContext)_localctx, predIndex);
		}
		return true;
	}
	private boolean boolExpr_sempred(BoolExprContext _localctx, int predIndex) {
		switch (predIndex) {
		case 0:
			return precpred(_ctx, 8);
		case 1:
			return precpred(_ctx, 7);
		case 2:
			return precpred(_ctx, 6);
		case 3:
			return precpred(_ctx, 5);
		case 4:
			return precpred(_ctx, 4);
		}
		return true;
	}
	private boolean arithExpr_sempred(ArithExprContext _localctx, int predIndex) {
		switch (predIndex) {
		case 5:
			return precpred(_ctx, 7);
		case 6:
			return precpred(_ctx, 6);
		}
		return true;
	}

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3*\u0109\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\5\2A\n\2"+
		"\3\2\3\2\3\2\3\2\3\2\5\2H\n\2\3\2\3\2\3\2\3\2\3\2\5\2O\n\2\3\2\3\2\3\2"+
		"\3\2\3\2\5\2V\n\2\3\3\7\3Y\n\3\f\3\16\3\\\13\3\3\4\3\4\3\4\5\4a\n\4\3"+
		"\4\3\4\3\4\5\4f\n\4\3\4\3\4\3\4\5\4k\n\4\3\5\3\5\3\5\3\5\3\5\3\5\3\6\3"+
		"\6\3\6\3\6\3\6\3\6\3\7\3\7\3\b\3\b\5\b}\n\b\3\t\3\t\3\n\3\n\3\13\3\13"+
		"\3\f\3\f\3\r\3\r\3\16\3\16\3\16\3\16\5\16\u008d\n\16\3\17\3\17\3\17\5"+
		"\17\u0092\n\17\3\17\3\17\3\17\7\17\u0097\n\17\f\17\16\17\u009a\13\17\3"+
		"\20\3\20\3\20\5\20\u009f\n\20\3\20\3\20\3\20\7\20\u00a4\n\20\f\20\16\20"+
		"\u00a7\13\20\3\21\3\21\3\22\7\22\u00ac\n\22\f\22\16\22\u00af\13\22\3\23"+
		"\3\23\3\23\7\23\u00b4\n\23\f\23\16\23\u00b7\13\23\3\23\3\23\3\24\3\24"+
		"\3\24\3\25\3\25\3\26\3\26\3\27\3\27\3\27\3\27\3\27\3\27\3\27\3\27\3\27"+
		"\5\27\u00cb\n\27\3\27\3\27\3\27\3\27\3\27\3\27\3\27\3\27\3\27\3\27\3\27"+
		"\3\27\3\27\3\27\3\27\7\27\u00dc\n\27\f\27\16\27\u00df\13\27\3\30\3\30"+
		"\3\30\3\30\3\30\3\30\3\30\3\30\3\30\3\30\3\30\3\30\5\30\u00ed\n\30\3\31"+
		"\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\5\31\u00f9\n\31\3\31\3\31"+
		"\3\31\3\31\3\31\7\31\u0100\n\31\f\31\16\31\u0103\13\31\3\32\3\32\3\33"+
		"\3\33\3\33\2\4,\60\34\2\4\6\b\n\f\16\20\22\24\26\30\32\34\36 \"$&(*,."+
		"\60\62\64\2\4\4\2##&&\3\2\23\25\2\u010f\2\66\3\2\2\2\4Z\3\2\2\2\6j\3\2"+
		"\2\2\bl\3\2\2\2\nr\3\2\2\2\fx\3\2\2\2\16|\3\2\2\2\20~\3\2\2\2\22\u0080"+
		"\3\2\2\2\24\u0082\3\2\2\2\26\u0084\3\2\2\2\30\u0086\3\2\2\2\32\u008c\3"+
		"\2\2\2\34\u0098\3\2\2\2\36\u00a5\3\2\2\2 \u00a8\3\2\2\2\"\u00ad\3\2\2"+
		"\2$\u00b0\3\2\2\2&\u00ba\3\2\2\2(\u00bd\3\2\2\2*\u00bf\3\2\2\2,\u00ca"+
		"\3\2\2\2.\u00ec\3\2\2\2\60\u00f8\3\2\2\2\62\u0104\3\2\2\2\64\u0106\3\2"+
		"\2\2\66\67\7\3\2\2\678\7\37\2\289\5\4\3\29:\7 \2\2:@\3\2\2\2;<\7\4\2\2"+
		"<=\7\37\2\2=>\5\32\16\2>?\7 \2\2?A\3\2\2\2@;\3\2\2\2@A\3\2\2\2AG\3\2\2"+
		"\2BC\7\5\2\2CD\7\37\2\2DE\5\34\17\2EF\7 \2\2FH\3\2\2\2GB\3\2\2\2GH\3\2"+
		"\2\2HN\3\2\2\2IJ\7\6\2\2JK\7\37\2\2KL\5\36\20\2LM\7 \2\2MO\3\2\2\2NI\3"+
		"\2\2\2NO\3\2\2\2OU\3\2\2\2PQ\7\7\2\2QR\7\37\2\2RS\5\"\22\2ST\7 \2\2TV"+
		"\3\2\2\2UP\3\2\2\2UV\3\2\2\2V\3\3\2\2\2WY\5\6\4\2XW\3\2\2\2Y\\\3\2\2\2"+
		"ZX\3\2\2\2Z[\3\2\2\2[\5\3\2\2\2\\Z\3\2\2\2]^\5\30\r\2^`\7\b\2\2_a\5\b"+
		"\5\2`_\3\2\2\2`a\3\2\2\2ak\3\2\2\2bc\5\30\r\2ce\7\t\2\2df\5\n\6\2ed\3"+
		"\2\2\2ef\3\2\2\2fk\3\2\2\2gh\5\30\r\2hi\7\n\2\2ik\3\2\2\2j]\3\2\2\2jb"+
		"\3\2\2\2jg\3\2\2\2k\7\3\2\2\2lm\7!\2\2mn\5\f\7\2no\7\36\2\2op\5\16\b\2"+
		"pq\7\"\2\2q\t\3\2\2\2rs\7!\2\2st\5\24\13\2tu\7\36\2\2uv\5\26\f\2vw\7\""+
		"\2\2w\13\3\2\2\2xy\5\20\t\2y\r\3\2\2\2z}\5\22\n\2{}\5\20\t\2|z\3\2\2\2"+
		"|{\3\2\2\2}\17\3\2\2\2~\177\7\'\2\2\177\21\3\2\2\2\u0080\u0081\7#\2\2"+
		"\u0081\23\3\2\2\2\u0082\u0083\7&\2\2\u0083\25\3\2\2\2\u0084\u0085\t\2"+
		"\2\2\u0085\27\3\2\2\2\u0086\u0087\7$\2\2\u0087\31\3\2\2\2\u0088\u0089"+
		"\7\13\2\2\u0089\u008d\5\60\31\2\u008a\u008b\7\f\2\2\u008b\u008d\5\60\31"+
		"\2\u008c\u0088\3\2\2\2\u008c\u008a\3\2\2\2\u008d\33\3\2\2\2\u008e\u008f"+
		"\5 \21\2\u008f\u0090\7\r\2\2\u0090\u0092\3\2\2\2\u0091\u008e\3\2\2\2\u0091"+
		"\u0092\3\2\2\2\u0092\u0093\3\2\2\2\u0093\u0094\5,\27\2\u0094\u0095\7\16"+
		"\2\2\u0095\u0097\3\2\2\2\u0096\u0091\3\2\2\2\u0097\u009a\3\2\2\2\u0098"+
		"\u0096\3\2\2\2\u0098\u0099\3\2\2\2\u0099\35\3\2\2\2\u009a\u0098\3\2\2"+
		"\2\u009b\u009c\5 \21\2\u009c\u009d\7\r\2\2\u009d\u009f\3\2\2\2\u009e\u009b"+
		"\3\2\2\2\u009e\u009f\3\2\2\2\u009f\u00a0\3\2\2\2\u00a0\u00a1\5.\30\2\u00a1"+
		"\u00a2\7\16\2\2\u00a2\u00a4\3\2\2\2\u00a3\u009e\3\2\2\2\u00a4\u00a7\3"+
		"\2\2\2\u00a5\u00a3\3\2\2\2\u00a5\u00a6\3\2\2\2\u00a6\37\3\2\2\2\u00a7"+
		"\u00a5\3\2\2\2\u00a8\u00a9\7$\2\2\u00a9!\3\2\2\2\u00aa\u00ac\5$\23\2\u00ab"+
		"\u00aa\3\2\2\2\u00ac\u00af\3\2\2\2\u00ad\u00ab\3\2\2\2\u00ad\u00ae\3\2"+
		"\2\2\u00ae#\3\2\2\2\u00af\u00ad\3\2\2\2\u00b0\u00b5\5&\24\2\u00b1\u00b2"+
		"\7\36\2\2\u00b2\u00b4\5&\24\2\u00b3\u00b1\3\2\2\2\u00b4\u00b7\3\2\2\2"+
		"\u00b5\u00b3\3\2\2\2\u00b5\u00b6\3\2\2\2\u00b6\u00b8\3\2\2\2\u00b7\u00b5"+
		"\3\2\2\2\u00b8\u00b9\7\16\2\2\u00b9%\3\2\2\2\u00ba\u00bb\5*\26\2\u00bb"+
		"\u00bc\5(\25\2\u00bc\'\3\2\2\2\u00bd\u00be\5\30\r\2\u00be)\3\2\2\2\u00bf"+
		"\u00c0\7&\2\2\u00c0+\3\2\2\2\u00c1\u00c2\b\27\1\2\u00c2\u00c3\7\17\2\2"+
		"\u00c3\u00c4\5,\27\2\u00c4\u00c5\7\20\2\2\u00c5\u00cb\3\2\2\2\u00c6\u00c7"+
		"\7\30\2\2\u00c7\u00cb\5,\27\5\u00c8\u00cb\5(\25\2\u00c9\u00cb\5.\30\2"+
		"\u00ca\u00c1\3\2\2\2\u00ca\u00c6\3\2\2\2\u00ca\u00c8\3\2\2\2\u00ca\u00c9"+
		"\3\2\2\2\u00cb\u00dd\3\2\2\2\u00cc\u00cd\f\n\2\2\u00cd\u00ce\7\21\2\2"+
		"\u00ce\u00dc\5,\27\13\u00cf\u00d0\f\t\2\2\u00d0\u00d1\7\22\2\2\u00d1\u00dc"+
		"\5,\27\n\u00d2\u00d3\f\b\2\2\u00d3\u00d4\t\3\2\2\u00d4\u00dc\5,\27\t\u00d5"+
		"\u00d6\f\7\2\2\u00d6\u00d7\7\26\2\2\u00d7\u00dc\5,\27\b\u00d8\u00d9\f"+
		"\6\2\2\u00d9\u00da\7\27\2\2\u00da\u00dc\5,\27\7\u00db\u00cc\3\2\2\2\u00db"+
		"\u00cf\3\2\2\2\u00db\u00d2\3\2\2\2\u00db\u00d5\3\2\2\2\u00db\u00d8\3\2"+
		"\2\2\u00dc\u00df\3\2\2\2\u00dd\u00db\3\2\2\2\u00dd\u00de\3\2\2\2\u00de"+
		"-\3\2\2\2\u00df\u00dd\3\2\2\2\u00e0\u00e1\5\60\31\2\u00e1\u00e2\7\31\2"+
		"\2\u00e2\u00e3\5\60\31\2\u00e3\u00ed\3\2\2\2\u00e4\u00e5\5\60\31\2\u00e5"+
		"\u00e6\7\32\2\2\u00e6\u00e7\5\60\31\2\u00e7\u00ed\3\2\2\2\u00e8\u00e9"+
		"\5\60\31\2\u00e9\u00ea\7\33\2\2\u00ea\u00eb\5\60\31\2\u00eb\u00ed\3\2"+
		"\2\2\u00ec\u00e0\3\2\2\2\u00ec\u00e4\3\2\2\2\u00ec\u00e8\3\2\2\2\u00ed"+
		"/\3\2\2\2\u00ee\u00ef\b\31\1\2\u00ef\u00f0\7\17\2\2\u00f0\u00f1\5\60\31"+
		"\2\u00f1\u00f2\7\20\2\2\u00f2\u00f9\3\2\2\2\u00f3\u00f9\5(\25\2\u00f4"+
		"\u00f5\7\35\2\2\u00f5\u00f9\5\60\31\5\u00f6\u00f9\5\62\32\2\u00f7\u00f9"+
		"\5\64\33\2\u00f8\u00ee\3\2\2\2\u00f8\u00f3\3\2\2\2\u00f8\u00f4\3\2\2\2"+
		"\u00f8\u00f6\3\2\2\2\u00f8\u00f7\3\2\2\2\u00f9\u0101\3\2\2\2\u00fa\u00fb"+
		"\f\t\2\2\u00fb\u0100\5\60\31\n\u00fc\u00fd\f\b\2\2\u00fd\u00fe\7\34\2"+
		"\2\u00fe\u0100\5\60\31\t\u00ff\u00fa\3\2\2\2\u00ff\u00fc\3\2\2\2\u0100"+
		"\u0103\3\2\2\2\u0101\u00ff\3\2\2\2\u0101\u0102\3\2\2\2\u0102\61\3\2\2"+
		"\2\u0103\u0101\3\2\2\2\u0104\u0105\7&\2\2\u0105\63\3\2\2\2\u0106\u0107"+
		"\7\'\2\2\u0107\65\3\2\2\2\31@GNUZ`ej|\u008c\u0091\u0098\u009e\u00a5\u00ad"+
		"\u00b5\u00ca\u00db\u00dd\u00ec\u00f8\u00ff\u0101";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}