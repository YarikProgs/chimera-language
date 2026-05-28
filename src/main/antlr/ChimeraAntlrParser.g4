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
    : annotations If expr stmt (Else stmt)?                                                                  # IfStmt
    | annotations modifier* Fn Identifier LParen parameters RParen (Colon type)? (blockStmt | (Assign expr)) # FnStmt
    | annotations Do blockStmt While expr Semicolon                                                          # DoWhileStmt
    | annotations While expr blockStmt                                                                       # WhileStmt
    | annotations For forHeader stmt                                                                         # ForStmt
    | annotations Return expr? Semicolon                                                                     # ReturnStmt
    | annotations expr Semicolon                                                                             # ExprStmt
    | blockStmt                                                                                              # BlockStmtStmt
    | ERROR_STMT                                                                                             # ErrorStmt
    ;

blockStmt
    : LBrace stmt* RBrace
    ;

forHeader
    : LParen Identifier (Comma Identifier)* In expr RParen
    | Identifier (Comma Identifier)* In expr
    ;

// Expressions

expr
    // Postfix
    : expr LParen arguments RParen                                                              # ArgumentsPostfix
    | expr QuestionMark? Dot Identifier                                                         # MemberAccessPostfix
    | expr DoubleExclamationMark                                                                # StrictUnwrapPostfix
    | expr ExclamationMark                                                                      # UnwrapPostfix
    // Modifiers
    | modifier+ expr                                                                            # ModifiedExpr
    // Short try
    | Try expr                                                                                  # ShortTryExpr
    // Unary
    | op=(ExclamationMark | Plus | Minus | BitNot) expr                                         # UnaryExpr
    // Binary
    | left=expr op=(Multiply | Divide | Modulo) right=expr                                      # FactorExpr
    | left=expr op=(Plus | Minus) right=expr                                                    # TermExpr
    | left=expr op=(ShiftLeft | ShiftRight | ShiftRightUnsigned) right=expr                     # ShiftExpr
    | left=expr op=(Less | LessEqual | Greater | GreaterEqual) right=expr                       # ComparisonExpr
    | left=expr op=(Equals | NotEquals) right=expr                                              # EqualityExpr
    | left=expr BitAnd right=expr                                                               # BitwiseAndExpr
    | left=expr BitXor right=expr                                                               # BitwiseXorExpr
    | left=expr BitOr right=expr                                                                # BitwiseOrExpr
    | left=expr LogicAnd right=expr                                                             # LogicalAndExpr
    | left=expr LogicXor right=expr                                                             # LogicalXorExpr
    | left=expr LogicOr right=expr                                                              # LogicalOrExpr
    | cond=expr QuestionMark thenExpr=expr Colon elseExpr=expr                                  # TernaryExpr
    | left=expr DoubleQuestionMark right=expr                                                   # NullCoalesceExpr
    // Lambda
    | Fn LParen parameters RParen (Colon type)? (blockStmt | (Assign expr))                     # LambdaExpr
    // Assignment
    | <assoc=right> lvalue=expr (Colon type)? assignmentOperator rvalue=expr                    # AssignmentExpr
    // Atoms
    | LBracket (expr (Comma expr)*)? RBracket                                                   # ListPrimary
    | LBrace (mapPair (Comma mapPair)*)? RBrace                                                 # MapPrimary
    | IntLiteral                                                                                # IntLiteralPrimary
    | FloatLiteral                                                                              # FloatLiteralPrimary
    | True                                                                                      # TruePrimary
    | False                                                                                     # FalsePrimary
    | Null                                                                                      # NullPrimary
    | StringLiteral                                                                             # StringLiteralPrimary
    | Identifier                                                                                # IdentifierPrimary
    | LParen expr RParen                                                                        # ExprParenPrimary
    // Error
    | ERROR_EXPR                                                                                # ErrorExpr
    ;

annotations
    : annotation*
    ;

annotation
    : LBracket Identifier (LParen arguments RParen)? RBracket
    ;

modifier
    : At
    | Const
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

mapPair
    : key=expr Colon value=expr
    ;


// Types

type
    // Nullable
    : type QuestionMark                                               # NullableType
    // Binary
    | left=type BitAnd right=type                                     # IntersectionType
    | left=type BitOr right=type                                      # UnionType
    // Atoms
    | Tuple Less type (Comma type)* Greater                           # TupleType
    | LParen (args+=type (Comma args+=type)*)? RParen RArrow ret=type # FunctionType
    | List Less type Greater                                          # ListType
    | Map Less key=type Comma value=type Greater                      # MapType
    | LParen type RParen                                              # ParenType
    | Identifier                                                      # IdentifierType
    ;