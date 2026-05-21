parser grammar ChimeraParser;

@header {
package net.aros.chimera;
}

options { tokenVocab=ChimeraLexer; }

program
    : stmt* EOF
    ;

stmt
    : ifStmt
    | fnStmt
    | doWhileStmt
    | whileStmt
    | forStmt
    | returnStmt
    | exprStmt
    ;

exprStmt
    : annotations expr Semicolon
    ;

fnStmt
    : annotations modifier* Fn Identifier LParen parameters RParen (Colon type)? (block | (Assign expr))
    ;

returnStmt
    : annotations Return expr? Semicolon
    ;

ifStmt
    : parenIfStmt
    | parenlessIfStmt
    ;

parenIfStmt
    : annotations If LParen expr RParen blockOrStmt (Else blockOrStmt)?
    ;

parenlessIfStmt
    : annotations If expr blockOrStmt (Else blockOrStmt)?
    ;

blockOrStmt
    : block
    | stmt
    ;

forStmt
    : parenForStmt
    | parenlessForStmt
    ;

parenForStmt
    : annotations For LParen (Identifier (Comma Identifier)*) In expr RParen blockOrStmt
    ;

parenlessForStmt
    : annotations For (Identifier (Comma Identifier)*) In expr blockOrStmt
    ;

doWhileStmt
    : parenDoWhileStmt
    | parenlessDoWhileStmt
    ;

parenDoWhileStmt
    : annotations Do block While LParen expr RParen Semicolon
    ;

parenlessDoWhileStmt
    : annotations Do block While expr Semicolon
    ;

whileStmt
    : parenWhileStmt
    | parenlessWhileStmt
    ;

parenWhileStmt
    : annotations While LParen expr RParen block
    ;

parenlessWhileStmt
    : annotations While expr block
    ;

block
    : LBrace stmt* RBrace
    ;

expr
    : lambda
    | assignment
    ;

modifier
    : At
    | Const
    ;

assignment
    : modifier* primary postfix* (Colon type)? assignmentOperator assignment
    | nullCoalesce
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

lambda
    : Fn LParen parameters RParen (Colon type)? (block | (Assign expr))
    ;

nullCoalesce
    : ternary (DoubleQuestionMark ternary)*
    ;

ternary
    : logicalOr (QuestionMark expr Colon ternary)?
    ;

logicalOr
    : logicalXor (LogicOr logicalXor)*
    ;

logicalXor
    : logicalAnd (LogicXor logicalAnd)*
    ;

logicalAnd
    : bitwiseOr (LogicAnd bitwiseOr)*
    ;

bitwiseOr
    : bitwiseXor (BitOr bitwiseXor)*
    ;

bitwiseXor
    : bitwiseAnd (BitXor bitwiseAnd)*
    ;

bitwiseAnd
    : equality (BitAnd equality)*
    ;

equality
    : comparison ((Equals | NotEquals) comparison)*
    ;

comparison
    : shift ((Less | LessEqual | Greater | GreaterEqual) shift)*
    ;

shift
    : term ((ShiftLeft | ShiftRight | ShiftRightUnsigned) term)*
    ;

term
    : factor ((Plus | Minus) factor)*
    ;

factor
    : unary ((Multiply | Divide | Modulo) unary)*
    ;

unary
    : (ExclamationMark | Plus | Minus | BitNot) unary
    | shortTry
    | call
    ;

shortTry
    : Try unary
    ;

call
    : primary postfix*
    ;

postfix
    : argumentsPostfix
    | memberAccessPostfix
    | strictUnwrapPostfix
    | unwrapPostfix
    ;

argumentsPostfix
    : LParen arguments RParen
    ;

memberAccessPostfix
    : QuestionMark? Dot Identifier
    ;

unwrapPostfix
    : ExclamationMark
    ;

strictUnwrapPostfix
    : DoubleExclamationMark
    ;

arguments
    : (argument (Comma argument)*)?
    ;

argument
    : namedArgument
    | positionalArgument
    ;

namedArgument
    : Identifier Assign expr
    ;

positionalArgument
    : expr
    ;

parameters
    : (parameter (Comma parameter)*)?
    ;

parameter
    : annotations Identifier (Colon type)? (Assign expr)?
    ;

primary
    : IntLiteral
    | FloatLiteral
    | StringLiteral
    | listLiteral
    | mapLiteral
    | True
    | False
    | Null
    | Identifier
    | LParen expr RParen
    ;

listLiteral
    : LBracket (expr (Comma expr)*)? RBracket
    ;

mapLiteral
    : LBrace ((expr Colon expr) (Comma (expr Colon expr))*)? RBrace
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
    : Identifier
    | tupleType
    | functionType
    | listType
    | mapType
    | LParen type RParen
    ;

functionType
    : LParen (type (Comma type)*)? RParen RArrow type
    ;

tupleType
    : Tuple Less type (Comma type)* Greater // at least one
    ;

listType
    : List Less type Greater
    ;

mapType
    : Map Less type Comma type Greater
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