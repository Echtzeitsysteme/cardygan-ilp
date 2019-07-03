grammar Ilp;

model
   : ('vars' LEFT_CURLY vars RIGHT_CURLY)
     ('obj' LEFT_CURLY obj RIGHT_CURLY)?
     ('genCstrs' LEFT_CURLY genCstrs RIGHT_CURLY)?
     ('cstrs' LEFT_CURLY cstrs RIGHT_CURLY)?
     ('sos1' LEFT_CURLY sos1Cstrs RIGHT_CURLY)?
   ;

vars
	: varDecl*
	;

varDecl
	: varName ':Int' intervalInt? #intVar
	| varName ':Real' intervalReal? #realVar
	| varName ':Bin' #binVar
	;

intervalInt
	: LEFT_BRACKET lbInt COMMA ubInt RIGHT_BRACKET
	;

intervalReal
	: LEFT_BRACKET lbReal COMMA ubReal RIGHT_BRACKET
	;

lbInt
	: integer
	;

ubInt
	: inf
	| integer
	;

integer : INTEGER;

inf : INF ;

lbReal
	: NUMBER
	;

ubReal
	: NUMBER
	| INF
	;

varName
    : ID
    ;

obj
	: 'min' arithExpr #minObj
	| 'max' arithExpr #maxObj
	;

genCstrs
	: ((cstrName':')?boolExpr';')*
	;

cstrs
	: ((cstrName':')?relOp';')*
	;

cstrName
	: ID
	;

sos1Cstrs
	: sos1*
	;

sos1
	: sos1Element (',' sos1Element)* ';'
	;

sos1Element
	: weight var
	;

var
	: varName
	;

weight
	: NUMBER
	;

 boolExpr
    : '(' boolExpr ')' #boolBrackets
    | boolExpr '&&' boolExpr #And
    | boolExpr '||' boolExpr #Or
    | boolExpr ('xor'|'XOR'|'Xor') boolExpr #XOr
 	| boolExpr '<=>' boolExpr #BiImpl
 	| boolExpr '=>' boolExpr #Impl
    |'!' boolExpr #Not
    | var #boolLit
 	| relOp #Rel
	;

relOp
	: arithExpr '>=' arithExpr #ge
	| arithExpr '<=' arithExpr #le
	| arithExpr '=' arithExpr #eq
	;

arithExpr
    : arithExpr arithExpr #mult
    | arithExpr '+' arithExpr #sum
    | '(' arithExpr ')' #arithBrackets
    | var #arithVar
    | '-' arithExpr #neg
    | realLiteral #realLit
    | intLiteral #intLit
    ;

realLiteral
	: NUMBER
	;

intLiteral
    : INTEGER
    ;

COMMA
	: ','
	;

LEFT_CURLY
	: '{'
	;

RIGHT_CURLY
	: '}'
	;

LEFT_BRACKET
	: '['
	;

RIGHT_BRACKET
	: ']'
	;

INF 
	: '*'
	;


ID : ID_LETTER (ID_LETTER | DIGIT)* ; // From C language

STRING
   : '"' (ESC | ~ ["\\])* '"'
   ;

//POS_NUMBER
//   : INT '.' DIGIT+
//   ;

NUMBER
   : '-'? INT '.' DIGIT+
   ;

//INTEGER
//    : '0' | [1-9] [0-9]*
//    ;

INTEGER
	: '-'? INT
	;

WS
   : [ \t\n\r] + -> skip
   ;

COMMENT
    : '/*' .*? '*/' -> skip
;

LINE_COMMENT
    : '//' ~[\r\n]* -> skip
;

fragment ID_LETTER : 'a'..'z'|'A'..'Z'|'_' ;

fragment DIGIT : '0'..'9' ;

fragment INT
   : '0' | [1-9] [0-9]*
   ;
//// no leading zeros
//fragment EXP
//   : [Ee] [+\-]? INT
//   ;
//// \- since - means "range" inside [...]

fragment ESC
   : '\\' (["\\/bfnrt] | UNICODE)
   ;
fragment UNICODE
   : 'u' HEX HEX HEX HEX
   ;
fragment HEX
   : [0-9a-fA-F]
   ;