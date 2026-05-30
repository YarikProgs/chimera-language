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
    : annotations If expr body (Else body)?                                                    # IfStmt
    | annotations modifier* Fn Identifier LParen parameters RParen (RArrow type)? functionBody # FnStmt
    | annotations Do body While expr Semicolon                                                 # DoWhileStmt
    | annotations While expr body                                                              # WhileStmt
    | annotations For forHeader body                                                           # ForStmt
    | annotations Return expr? Semicolon                                                       # ReturnStmt
    | annotations expr Semicolon                                                               # ExprStmt
    | blockStmt                                                                                # BlockStmtStmt
    ;

blockStmt
    : LBrace stmt* RBrace
    ;

body
    : Colon stmt # SingleStmtBody
    | blockStmt  # BlockStmtBody
    ;

functionBody
    : Colon expr # ExprFunctionBody
    | blockStmt  # BlockStmtFunctionBody
    | Colon stmt # IllegalStmtBody
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
    | left=expr op=(Equals | NotEquals | AddressEquals | AddressNotEquals) right=expr           # EqualityExpr
    | left=expr BitAnd right=expr                                                               # BitwiseAndExpr
    | left=expr BitXor right=expr                                                               # BitwiseXorExpr
    | left=expr BitOr right=expr                                                                # BitwiseOrExpr
    | left=expr LogicAnd right=expr                                                             # LogicalAndExpr
    | left=expr LogicXor right=expr                                                             # LogicalXorExpr
    | left=expr LogicOr right=expr                                                              # LogicalOrExpr
    // Ternary
    | cond=expr QuestionMark thenExpr=expr Colon elseExpr=expr                                  # TernaryExpr
    | cond=expr QuestionMark Colon elseExpr=expr                                                # TernaryExprMissingThen
    | cond=expr QuestionMark thenExpr=expr                                                      # TernaryExprMissingElse
    | cond=expr QuestionMark                                                                    # TernaryExprMissingThenAndElse
    // Null coalesce
    | left=expr DoubleQuestionMark right=expr                                                   # NullCoalesceExpr
    // Lambda
    | Fn LParen parameters RParen (RArrow type)? functionBody                                   # LambdaExpr
    | Fn LParen parameters RParen                                                               # LambdaExprMissingBody
    | Fn                          (RArrow type)? functionBody                                   # LambdaExprMissingParameters
    | Fn LParen parameters RParen  RArrow        functionBody                                   # LambdaExprMissingReturnType
    // Assignment
    | <assoc=right> lvalue=expr (Colon type)? assignmentOperator rvalue=expr                    # AssignmentExpr
    | <assoc=right> lvalue=expr  Colon        assignmentOperator rvalue=expr                    # AssignmentExprMissingType
    | <assoc=right> lvalue=expr (Colon type)? assignmentOperator                                # AssignmentExprMissingRhs
    // Binary error nodes
    | left=expr op=(Multiply | Divide | Modulo | Plus | Minus | ShiftLeft | ShiftRight |
                    ShiftRightUnsigned | Less | LessEqual | Greater | GreaterEqual | Equals |
                    NotEquals | BitAnd | BitXor | BitOr | LogicAnd | LogicXor | LogicOr |
                    DoubleQuestionMark | AddressEquals | AddressNotEquals)                      # BinaryExprMissingRhs
    | op=(Multiply | Divide | Modulo | ShiftLeft | ShiftRight | ShiftRightUnsigned | Less |
          LessEqual | Greater | GreaterEqual | Equals | NotEquals | BitAnd | BitXor | BitOr |
          LogicAnd | LogicXor | LogicOr | DoubleQuestionMark |
          AddressEquals | AddressNotEquals) right=expr                                          # BinaryExprMissingLhs

    // Atoms
    | LBracket (expr (Comma expr)*)? RBracket                                                   # ListPrimary
    | LBracket (expr (Comma expr)*)?                                                            # ListPrimaryUnclosed
    | LParen (expr (Comma expr)*) RParen                                                        # TuplePrimary
    | LParen (expr (Comma expr)*)                                                               # TuplePrimaryUnclosed
    | LBrace (mapPair (Comma mapPair)*)? RBrace                                                 # MapPrimary
    | LBrace (mapPair (Comma mapPair)*)?                                                        # MapPrimaryUnclosed
    | IntLiteral                                                                                # IntLiteralPrimary
    | FloatLiteral                                                                              # FloatLiteralPrimary
    | True                                                                                      # TruePrimary
    | False                                                                                     # FalsePrimary
    | Null                                                                                      # NullPrimary
    | StringLiteral                                                                             # StringLiteralPrimary
    | MultilineStringLiteral                                                                    # MultilineStringLiteral
    | Identifier                                                                                # IdentifierPrimary
    | Ellipsis                                                                                  # EllipsisPrimary
    | LParen expr RParen                                                                        # ExprParenPrimary
    | LParen expr                                                                               # ExprParenPrimaryUnclosed
    ;

annotations
    : annotation*
    ;

annotation
    : LBracket Identifier (LParen arguments RParen)? RBracket                                    # ValidAnnotation
    | LBracket Identifier (LParen arguments RParen)?                                             # UnclosedAnnotation
    ;

modifier
    : At
    | Const
    ;

assignmentOperator
    : op=Assign
    | op=PlusAssign
    | op=MinusAssign
    | op=MultiplyAssign
    | op=DivideAssign
    | op=ModuloAssign
    | op=BitAndAssign
    | op=BitOrAssign
    | op=BitXorAssign
    | op=ShiftLeftAssign
    | op=ShiftRightAssign
    | op=ShiftRightUnsignedAssign
    | op=LogicAndAssign
    | op=LogicOrAssign
    | op=LogicXorAssign
    ;

arguments
    : (argument (Comma argument)*)?
    ;

argument
    : Identifier Assign expr # NamedArgument
    | Identifier Assign      # NamedArgumentWithoutValue
    | Assign expr            # NamedArgumentWithoutName
    | expr                   # PositionalArgument
    ;

parameters
    : (parameter (Comma parameter)*)?
    ;

parameter
    : annotations Identifier (Colon type)? (Assign expr)?  # ValidParameter
    | annotations Identifier (Colon type)? Assign          # DefaultedParameterWithoutValue
    ;

mapPair
    : key=expr Colon value=expr # ValidMapPair
    | key=expr Colon            # MapPairWithoutValue
    | Colon value=expr          # MapPairWithoutKey
    | Colon                     # MapPairWithoutAll
    ;


// Types

type
    // Nullable
    : type QuestionMark                                               # NullableType
    // Binary
    | left=type BitAnd right=type                                     # IntersectionType
    | left=type BitAnd                                                # IntersectionTypeMissingRhs
    |           BitAnd right=type                                     # IntersectionTypeMissingLhs
    | left=type BitOr right=type                                      # UnionType
    | left=type BitOr                                                 # UnionTypeMissingRhs
    |           BitOr right=type                                      # UnionTypeMissingLhs
    // Atoms
    | Tuple Less type (Comma type)* Greater                           # TupleType
    | Tuple Less type (Comma type)*                                   # TupleTypeMissingClosure
    | LParen (args+=type (Comma args+=type)*)? RParen RArrow ret=type # FunctionType
    | LParen (args+=type (Comma args+=type)*)? RParen RArrow          # FunctionTypeMissingReturn
    | List Less type Greater                                          # ListType
    | List Less type                                                  # ListTypeMissingClosure
    | Map Less key=type Comma value=type Greater                      # MapType
    | Map Less key=type Comma value=type                              # MapTypeMissingClosure
    | Map Less Comma value=type                                       # MapTypeMissingKey
    | Map Less key=type Comma?                                        # MapTypeMissingValue
    | LParen type RParen                                              # ParenType
    | LParen type                                                     # ParenTypeMissingClosure
    | Identifier                                                      # IdentifierType
    ;