parser grammar ChimeraAntlrParser;

@header {
package net.aros.chimera;
}

options { tokenVocab=ChimeraAntlrLexer; }

// Program

program
    : stmt* EOF
    ;

// Statements

stmt
    : annotations If condition stmt (Else stmt)?                                                             # IfStmt
    | annotations modifier* Fn Identifier LParen parameters RParen (Colon type)? (blockStmt | (Assign expr)) # FnStmt
    | annotations Do blockStmt While condition Semicolon                                                     # DoWhileStmt
    | annotations While condition blockStmt                                                                  # WhileStmt
    | annotations For forHeader stmt                                                                         # ForStmt
    | annotations Return expr? Semicolon                                                                     # ReturnStmt
    | annotations expr Semicolon                                                                             # ExprStmt
    | blockStmt                                                                                              # BlockStmtStmt
    | ERROR_STMT                                                                                             # ErrorStmt
    ;

condition
    : LParen expr RParen
    | expr
    ;

forHeader
    : LParen Identifier (Comma Identifier)* In expr RParen
    | Identifier (Comma Identifier)* In expr
    ;

// Expressions

expr
    : Fn LParen parameters RParen (Colon type)? (blockStmt | (Assign expr)) # LambdaExpr
    | assignment                                                            # AssignmentExprExpr
    ;

blockStmt
    : LBrace stmt* RBrace
    ;

modifier
    : At
    | Const
    ;

assignment
    : modifier* primary postfix* (Colon type)? assignmentOperator assignment # AssignmentExpr
    | nullCoalesce                                                           # NullCoalesceAsgnmtExpr
    ;

assignmentOperator
    : Assign
    | PlusAssign
    | MinusAssign
    | MultiplyAssign
    | DivideAssign
    | ModuloAssign
    | BitAndAssign
    | BitOrAssign
    | BitXorAssign
    | ShiftLeftAssign
    | ShiftRightAssign
    | ShiftRightUnsignedAssign
    | LogicAndAssign
    | LogicOrAssign
    | LogicXorAssign
    ;

nullCoalesce
    : ternary (DoubleQuestionMark ternary)* # NullCoalesceExpr
//    | ternary DoubleQuestionMark            # NullCoalesceMissingRhs
    ;

ternary
    : logicalOr                                 # LogicalOrTernExpr
    | logicalOr QuestionMark expr Colon ternary # TernaryExpr
    | logicalOr QuestionMark expr Colon?        # TernaryMissingElse
    | logicalOr QuestionMark Colon ternary      # TernaryMissingThen
    | logicalOr QuestionMark Colon?             # TernaryMissingThenAndElse
    ;

logicalOr
    : logicalXor (LogicOr logicalXor)* # LogicalOrExpr
//    | logicalXor op=LogicOr               # LogicalOrMissingRhs
    ;

logicalXor
    : logicalAnd (LogicXor logicalAnd)* # LogicalXorExpr
//    | logicalAnd op=LogicXor               # LogicalXorMissingRhs
    ;

logicalAnd
    : bitwiseOr (LogicAnd bitwiseOr)* # LogicalAndExpr
//    | bitwiseOr op=LogicAnd              # LogicalAndMissingRhs
    ;

bitwiseOr
    : bitwiseXor (BitOr bitwiseXor)* # BitwiseOrExpr
//    | bitwiseXor op=BitOr               # BitwiseOrMissingRhs
    ;

bitwiseXor
    : bitwiseAnd (BitXor bitwiseAnd)* # BitwiseXorExpr
//    | bitwiseAnd op=BitXor               # BitwiseXorMissingRhs
    ;

bitwiseAnd
    : equality (BitAnd equality)* # BitwiseAndExpr
//    | equality op=BitAnd             # BitwiseAndMissingRhs
    ;

equality
    : comparison (op+=(Equals | NotEquals) comparison)* # EqualityExpr
//    | comparison op=(Equals | NotEquals)                # EqualityMissingRhs
    ;

comparison
    : shift (op+=(Less | LessEqual | Greater | GreaterEqual) shift)* # ComparisonExpr
//    | shift op=(Less | LessEqual | Greater | GreaterEqual)           # ComparisonMissingRhs
    ;

shift
    : term (op+=(ShiftLeft | ShiftRight | ShiftRightUnsigned) term)* # ShiftExpr
//    | term op=(ShiftLeft | ShiftRight | ShiftRightUnsigned)          # ShiftMissingRhs
    ;

term
    : factor (op+=(Plus | Minus) factor)* # TermExpr
//    | factor op=(Plus | Minus)            # TermMissingRhs
    ;

factor
    : unary (op+=(Multiply | Divide | Modulo) unary)* # FactorExpr
//    | unary op=(Multiply | Divide | Modulo)           # FactorMissingRhs
    ;

unary
    : op=(ExclamationMark | Plus | Minus | BitNot) unary # UnaryUnaryExpr
    | Try unary                                          # ShortTryUnaryExpr
    | primary postfix*                                   # CallUnaryExpr
    ;

postfix
    : LParen arguments RParen      # ArgumentsPostfix
    | QuestionMark? Dot Identifier # MemberAccessPostfix
    | DoubleExclamationMark        # StrictUnwrapPostfix
    | ExclamationMark              # UnwrapPostfix
    ;

arguments
    : (argument (Comma argument)*)?
    ;

argument
    : Identifier Assign expr # NamedArgument
    | expr                   # PositionalArgument
    ;

parameters
    : (parameter (Comma parameter)*)?
    ;

parameter
    : annotations Identifier (Colon type)? (Assign expr)?
    ;

primary
    : IntLiteral                                                     # IntLiteralPrimary
    | FloatLiteral                                                   # FloatLiteralPrimary
    | StringLiteral                                                  # StringLiteralPrimary
    | LBracket (expr (Comma expr)*)? RBracket                        # ListPrimary
    | LBrace ((expr Colon expr) (Comma (expr Colon expr))*)? RBrace  # MapPrimary
    | True                                                           # TruePrimary
    | False                                                          # FalsePrimary
    | Null                                                           # NullPrimary
    | Identifier                                                     # IdentifierPrimary
    | LParen expr RParen                                             # ExprParenPrimary
    | ERROR_EXPR                                                     # ErrorExpr
    ;


// Types

type
    : unionType
    ;

unionType
    : intersectionType (BitOr intersectionType)*
    ;

intersectionType
    : postfixType (BitAnd postfixType)*
    ;

postfixType
    : primaryType QuestionMark?
    ;

primaryType
    : Identifier                                      # IdentifierType
    | Tuple Less type (Comma type)* Greater           # TupleType
    | LParen (type (Comma type)*)? RParen RArrow type # FunctionType
    | List Less type Greater                          # ListType
    | Map Less type Comma type Greater                # MapType
    | LParen type RParen                              # ParenType
    ;

annotations
    : annotation*
    ;

annotation
    : LBracket annotationExpr RBracket
    ;

annotationExpr
    : Identifier (LParen arguments RParen)?
    ;