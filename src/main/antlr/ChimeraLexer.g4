lexer grammar ChimeraLexer;

@header {
package net.aros.chimera;
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
    : '"' ( '\\' . | ~["\\\r\n] )* '"'
    | '\'' ( '\\' . | ~['\\\r\n] )* '\''
    ;
LineComment   : '//' ~[\r\n]*            -> skip ;
BlockComment  : '/*' .*? '*/'            -> skip ;
WS            : [ \t\r\n]+               -> skip ;