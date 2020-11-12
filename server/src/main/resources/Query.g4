grammar Query;

AND     : ',' | 'and' ;
OR      : 'or' ;
INT     : '-'? [0-9]+ ;
DOUBLE  : '-'? [0-9]+'.'[0-9]+ ;
WITHIN  : 'within' ;
FROM    : 'from' ;
ID      : [a-zA-Z_][a-zA-Z_0-9]* ;
STRING  :  '"' (~["])* '"' | '\'' (~['])* '\''
        ;
EQ      : '=' '=' ? ;
LE      : '<=' ;
GE      : '>=' ;
NE      : '!=' ;
LT      : '<' ;
GT      : '>' ;
SEP     : '.' ;
WS      : [ \t\r\n]+ -> skip ;

query : expression ;

expression
    : expression AND expression # AndExpression
    | expression OR expression  # OrExpression
    | predicate                 # PredicateExpression
    | '(' expression ')'        # BracketExpression
    ;

reference : element (SEP element)* ;

element : ID ;

predicate
    : reference WITHIN amount FROM location # LocationPredicate
    | reference operator term               # OperatorPredicate
    ;

location : '(' DOUBLE ',' DOUBLE ')' ;

term
    : reference
    | value
    | amount
    ;

operator
    : LE
    | GE
    | NE
    | LT
    | GT
    | EQ
    ;

amount : value unit ;

value
   : INT          # IntegerValue
   | DOUBLE       # DoubleValue
   | STRING       # StringValue
   | ID           # StringValue
   ;

unit :
   | '%'
   | ID
   ;