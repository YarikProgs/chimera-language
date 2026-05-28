lexer grammar ChimeraAntlrLexer;

@header {
package net.aros.chimera;
}

tokens {
    ERROR_UNTERMINATED_STRING,
    ERROR_INVALID_ESCAPE,
    ERROR_UNTERMINATED_COMMENT,
    ERROR_UNMATCHED_COMMENT_CLOSURE,
    ERROR_INVALID_NUMBER,
    ERROR_INVALID_CHARACTER,
    ERROR_EXPR,
    ERROR_STMT
}

// Keywords
If                       : 'if'         ;
Else                     : 'else'       ;
While                    : 'while'      ;
For                      : 'for'        ;
True                     : 'true'       ;
False                    : 'false'      ;
Null                     : 'null'       ;
In                       : 'in'         ;
Do                       : 'do'         ;
Fn                       : 'fn'         ;
Return                   : 'return'     ;
Const                    : 'const'      ;
Tuple                    : 'tuple'      ;
List                     : 'list'       ;
Map                      : 'map'        ;
Try                      : 'try'        ;

// Symbols
LParen                   : '('          ;
RParen                   : ')'          ;
LBrace                   : '{'          ;
RBrace                   : '}'          ;
LBracket                 : '['          ;
RBracket                 : ']'          ;
Colon                    : ':'          ;
Semicolon                : ';'          ;
Comma                    : ','          ;
Dot                      : '.'          ;
At                       : '@'          ;
RArrow                   : '->'         ;
DoubleExclamationMark    : '!!'         ;
ExclamationMark          : '!'          ;
DoubleQuestionMark       : '??'          ;
QuestionMark             : '?'          ;

// Operators
PlusAssign               : '+='         ;
Plus                     : '+'          ;
MinusAssign              : '-='         ;
Minus                    : '-'          ;
MultiplyAssign           : '*='         ;
Multiply                 : '*'          ;
DivideAssign             : '/='         ;
Divide                   : '/'          ;
ModuloAssign             : '%='         ;
Modulo                   : '%'          ;
BitAndAssign             : '&='         ;
BitAnd                   : '&'          ;
BitOrAssign              : '|='         ;
BitOr                    : '|'          ;
BitXorAssign             : '^='         ;
BitXor                   : '^'          ;
ShiftLeftAssign          : '<<='        ;
ShiftLeft                : '<<'         ;
ShiftRightAssign         : '>>='        ;
ShiftRight               : '>>'         ;
ShiftRightUnsignedAssign : '>>>='       ;
ShiftRightUnsigned       : '>>>'        ;
BitNot                   : '~'          ;
LogicAndAssign           : '&&='        ;
LogicAnd                 : '&&' | 'and' ;
LogicOrAssign            : '||='        ;
LogicXorAssign           : '^^='        ;
LogicXor                 : '^^' | 'xor' ;
LogicOr                  : '||' | 'or'  ;
Equals                   : '=='         ;
NotEquals                : '!='         ;
LessEqual                : '<='         ;
Less                     : '<'          ;
GreaterEqual             : '>='         ;
Greater                  : '>'          ;
Assign                   : '='          ;

Identifier    : [_\p{L}][_\p{L}\p{N}]*           ;
IntLiteral    : [0-9]+                           ;
FloatLiteral  : [0-9]* '.' [0-9]+                ;
StringLiteral
    : '"'  ( '\\' [ntr"\\] | ~["\\\r\n] )* '"'
    | '\'' ( '\\' [ntr'\\] | ~['\\\r\n] )* '\''
    ;
LineComment   : '//' ~[\r\n]*            -> skip ;
BlockComment  : '/*' .*? '*/'            -> skip ;
WS            : [ \t\r\n]+               -> skip ;

InvalidIntegerIdentifier        : [0-9]+ [_\p{L}] [_\p{L}\p{N}]*                                                      -> type(ERROR_INVALID_NUMBER)            ;
InvalidFloatIdentifier          : [0-9]* '.' [0-9]+ [_\p{L}] [_\p{L}\p{N}]*                                           -> type(ERROR_INVALID_NUMBER)            ;
InvalidEscapeDoubleQuotedString : '"'  ( '\\' [ntr"\\] | ~["\\\r\n] )* '\\' ~[ntr"\r\n] ( '\\' . | ~["\\\r\n] )* '"'  -> type(ERROR_INVALID_ESCAPE)            ;
InvalidEscapeSingleQuotedString : '\'' ( '\\' [ntr'\\] | ~['\\\r\n] )* '\\' ~[ntr'\r\n] ( '\\' . | ~['\\\r\n] )* '\'' -> type(ERROR_INVALID_ESCAPE)            ;
UnterminatedDoubleQuoteString   : '"' ( '\\' . | ~["\\\r\n] )* (('\r'? '\n') | EOF)                                   -> type(ERROR_UNTERMINATED_STRING)       ;
UnterminatedSingleQuoteString   : '\'' ( '\\' . | ~['\\\r\n] )* (('\r'? '\n') | EOF)                                  -> type(ERROR_UNTERMINATED_STRING)       ;
UnterminatedComment             : '/*'                                                                                -> type(ERROR_UNTERMINATED_COMMENT)      ;
UnmatchedCommentClosure         : '*/'                                                                                -> type(ERROR_UNMATCHED_COMMENT_CLOSURE) ;
InvalidCharacter                : .                                                                                   -> type(ERROR_INVALID_CHARACTER)         ;