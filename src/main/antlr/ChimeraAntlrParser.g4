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
    : annotations If expr stmt (Else stmt)?                                                                            # IfStmt
    | annotations modifier* Fn Identifier LParen parameters RParen (Colon type)? (blockStmt | (Assign expr Semicolon)) # FnStmt
    | annotations Do blockStmt While expr Semicolon                                                                    # DoWhileStmt
    | annotations While expr blockStmt                                                                                 # WhileStmt
    | annotations For forHeader stmt                                                                                   # ForStmt
    | annotations Return expr? Semicolon                                                                               # ReturnStmt
    | annotations expr Semicolon                                                                                       # ExprStmt
    | blockStmt                                                                                                        # BlockStmtStmt
    | ERROR_STMT                                                                                                       # ErrorStmt
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
    | cond=expr QuestionMark Colon elseExpr=expr                                                # TernaryExprMissingThen
    | cond=expr QuestionMark thenExpr=expr                                                      # TernaryExprMissingElse
    | cond=expr QuestionMark                                                                    # TernaryExprMissingThenAndElse
    | left=expr DoubleQuestionMark right=expr                                                   # NullCoalesceExpr
    // Lambda
    | Fn LParen parameters RParen (Colon type)? (blockStmt | (Assign expr))                     # LambdaExpr
    | Fn LParen parameters RParen                                                               # LambdaExprMissingBody
    | Fn (Colon type)? (blockStmt | (Assign expr))                                              # LambdaExprMissingParameters
    | Fn LParen parameters RParen Colon (blockStmt | (Assign expr))                             # LambdaExprMissingReturnType
    // Assignment
    | <assoc=right> lvalue=expr (Colon type)? assignmentOperator rvalue=expr                    # AssignmentExpr
    | <assoc=right> lvalue=expr Colon assignmentOperator rvalue=expr                            # AssignmentExprMissingType
    | <assoc=right> lvalue=expr (Colon type)? assignmentOperator                                # AssignmentExprMissingRhs
    | <assoc=right>             (Colon type)? assignmentOperator rvalue=expr                    # AssignmentExprMissingLhs
    // Binary error nodes
    | left=expr op=(Multiply | Divide | Modulo | Plus | Minus | ShiftLeft | ShiftRight |
                    ShiftRightUnsigned | Less | LessEqual | Greater | GreaterEqual | Equals |
                    NotEquals | BitAnd | BitXor | BitOr | LogicAnd | LogicXor | LogicOr |
                    DoubleQuestionMark)                                                         # BinaryExprMissingRhs
    | op=(Multiply | Divide | Modulo | ShiftLeft | ShiftRight | ShiftRightUnsigned | Less |
          LessEqual | Greater | GreaterEqual | Equals | NotEquals | BitAnd | BitXor | BitOr |
          LogicAnd | LogicXor | LogicOr | DoubleQuestionMark) right=expr                        # BinaryExprMissingLhs

    // Atoms
    | LBracket (expr (Comma expr)*)? RBracket                                                   # ListPrimary
    | LBracket (expr (Comma expr)*)?                                                            # ListPrimaryUnclosed
    | LBrace (mapPair (Comma mapPair)*)? RBrace                                                 # MapPrimary
    | LBrace (mapPair (Comma mapPair)*)?                                                        # MapPrimaryUnclosed
    | IntLiteral                                                                                # IntLiteralPrimary
    | FloatLiteral                                                                              # FloatLiteralPrimary
    | True                                                                                      # TruePrimary
    | False                                                                                     # FalsePrimary
    | Null                                                                                      # NullPrimary
    | StringLiteral                                                                             # StringLiteralPrimary
    | Identifier                                                                                # IdentifierPrimary
    | LParen expr RParen                                                                        # ExprParenPrimary
    | LParen expr                                                                               # ExprParenPrimaryUnclosed
    // Error
    | ERROR_EXPR                                                                                # ErrorExpr
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