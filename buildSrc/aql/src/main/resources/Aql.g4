grammar Aql;

// Main entry point for parsing
query: expression EOF;

// Expression rule
expression
    : expression AND expression # AndExpression
    | expression OR expression  # OrExpression
    | predicate                 # PredicateExpression
    | '(' expression ')'        # BracketExpression
    ;

// Predicates
predicate
    : path (NOT | IN) (set | range)         # InPredicate
    | path operator termOp                  # OperatorPredicate
    // WARNING: next two rules have ambiguity
    // simple Identifier will be treated as path, not as path segment
    | NOT? path                             # ReferencePredicate
    // Identifier with '{expression}' block will be resolved as segment
    | (pathSegment SEPARATOR)* pathSegment  # PathSegmentPredicate
    | path? tag                             # TagPredicate
    | path? notTag                          # NotTagPredicate
    | path unaryOperator                    # UnaryOperatorPredicate
    | LIKE termOp                           # EntityRootSearch
    | filterTag                             # FilterTagReference
    | NOT? path idsSet                      # IdSetPredicate
    ;

// Path in Cayenne
path : (pathSegment SEPARATOR)* Identifier ;

pathSegment : Identifier ('{' expression '}')?;

termOp
    : term                   # SingleTerm
    | term mathOperator term # MathOp
    ;

term
    : value          # ValueTerm
    | amount         # AmountTerm
    | '(' termOp ')' # ParTerm
    ;

dateTermOp
    : dateTerm # SingleDateTerm
    | dateTerm mathOperator dateTerm # DateMathOp
    ;

dateTerm
    : unaryOperator     # UnaryTerm
    | amount            # DateAmountTerm
    | dateTimeLiteral   # DateTimeLiteralTerm
    ;

operator
    : LE
    | GE
    | NE
    | LT
    | GT
    | EQ
    | LIKE
    | CONTAINS
    | STARTS_WITH
    | ENDS_WITH
    | NOT_LIKE
    | NOT_CONTAINS
    | NOT_ENDS_WITH
    | NOT_STARTS_WITH
    | AFTER
    | BEFORE
    ;

unaryOperator
    : 'today'
    | 'yesterday'
    | 'tomorrow'

    | 'last week'
    | 'this week'
    | 'next week'

    | 'last month'
    | 'this month'
    | 'next month'

    | 'last year'
    | 'this year'
    | 'next year'
    | 'now'

    | 'me'
    ;

mathOperator
    : MINUS
    | PLUS
    | DIV
    | MUL
    | MOD
    ;

range : (dateTermOp | '*') '..' (dateTermOp | '*');

set
    : '(' value (',' value)* ')'
    | value ',' value (',' value)*
    ;

idsSet : '[' value (',' value)* ']';

amount : (IntegerLiteral unit)+;

tag : '#' (Identifier | SingleQuotedStringLiteral | DoubleQuotedStringLiteral);

notTag : 'not #' (Identifier | SingleQuotedStringLiteral | DoubleQuotedStringLiteral);

filterTag : '@' (Identifier | SingleQuotedStringLiteral | DoubleQuotedStringLiteral);

value
   : dateTimeLiteral           # DateTime
   | IntegerLiteral            # Int
   | FloatingPointLiteral      # Float
   | SingleQuotedStringLiteral # String
   | DoubleQuotedStringLiteral # String
   | RichTextLiteral           # RichText
   | BooleanLiteral            # Boolean
   | NullLiteral               # Null
   | EmptyLiteral              # Empty
   | Identifier                # Id
   | unaryOperator             # Id
   ;

unit
   : '%'        #UnitPercent
   | Identifier #UnitCustom
   ;

// general ops
AND     : '&&' | 'and' | 'AND';
OR      : 'or' | '||' | 'OR';
EQ      : ('=' '=' ?) | 'is' | 'IS';

// numeric and date ops
LE      : '<=';
GE      : '>=';
NE      : ('!=' '=' ?) | 'not is';
LT      : '<';
GT      : '>';
MINUS   : '-';
PLUS    : '+';
MUL     : '*';
DIV     : '/';
MOD     : '%';
IN      : 'in' ;

// string ops
LIKE        : '~' | 'like' ;
CONTAINS    : 'contains';
STARTS_WITH : 'starts with';
ENDS_WITH   : 'ends with';
NOT_LIKE    : '!~' | 'not like';
NOT_CONTAINS    : 'not contains';
NOT_STARTS_WITH : 'not starts with';
NOT_ENDS_WITH   : 'not ends with';

// date
AFTER       : 'after';
BEFORE      : 'before';

// not
NOT     : 'not' | '!';

// Path separator
SEPARATOR   : '.' ;

// Boolean Literals
BooleanLiteral: 'true' | 'false';

// The Null Literal
NullLiteral: 'null';

//Empty Literal
EmptyLiteral: 'empty';

Identifier: Letter LetterOrDigit*;
fragment Letter: [a-zA-Z_];
fragment LetterOrDigit: [a-zA-Z0-9_];

// Date literals based on https://en.wikipedia.org/wiki/Date_and_time_notation_in_Australia
// Date format:
//  - (d)d/mm/yyyy (e.g., 30/09/2001)
//  - ISO 8601 date format of YYYY-MM-DD
// Time format:
//  - hh:mm (am | pm)
//  - HH:mm
dateTimeLiteral: dateLiteral timeLiteral?;
dateLiteral: MainDateFormat | IsoDateFormat;
timeLiteral: Time12 | Time24;

MainDateFormat: Digit Digit? '/' Digit Digit? '/' Digit Digit Digit Digit ;
IsoDateFormat: Digit Digit Digit Digit '-' Digit Digit '-' Digit Digit;
Time12: Hours12 ':' Minutes AmPm;
Time24: Hours24 ':' Minutes;

fragment Hours12: ('0'? Digit) | ('1' [0-2]);
fragment AmPm: ' '? ('am' | 'pm' | 'a.m.' | 'p.m.' | 'AM' | 'PM' | 'A.M.' | 'P.M.');
fragment Hours24: ('0'? Digit) | ('1' Digit) | ('2' [0-3]);
fragment Minutes: [0-5] Digit;

// Integer Literals, following Java spec, i.e. forms like 10, 0xff, 0777, 0b1001 are allowed
// TODO: revisit and simplify literals, most probably only decimal form is required,
// TODO: same for FloatingPointLiteral below
IntegerLiteral
   :   DecimalIntegerLiteral
   |   HexIntegerLiteral
   |   OctalIntegerLiteral
   |   BinaryIntegerLiteral
   ;

fragment DecimalIntegerLiteral: Sign? DecimalNumeral IntegerTypeSuffix?;
fragment HexIntegerLiteral: Sign? HexNumeral IntegerTypeSuffix?;
fragment OctalIntegerLiteral: Sign? OctalNumeral IntegerTypeSuffix?;
fragment BinaryIntegerLiteral: Sign? BinaryNumeral IntegerTypeSuffix?;
fragment IntegerTypeSuffix: [lL];

fragment DecimalNumeral: '0' | NonZeroDigit (Digits? | Underscores Digits);
fragment Digits: Digit (DigitOrUnderscore* Digit)?;
fragment Digit: '0' | NonZeroDigit;
fragment NonZeroDigit: [1-9];
fragment DigitOrUnderscore: Digit | '_';
fragment Underscores: '_'+;

fragment HexNumeral: '0' [xX] HexDigits;
fragment HexDigits: HexDigit (HexDigitOrUnderscore* HexDigit)?;
fragment HexDigit: [0-9a-fA-F];
fragment HexDigitOrUnderscore: HexDigit | '_';

fragment OctalNumeral: '0' Underscores? OctalDigits;
fragment OctalDigits: OctalDigit (OctalDigitOrUnderscore* OctalDigit)?;
fragment OctalDigit: [0-7];
fragment OctalDigitOrUnderscore: OctalDigit | '_';

fragment BinaryNumeral: '0' [bB] BinaryDigits;
fragment BinaryDigits: BinaryDigit (BinaryDigitOrUnderscore* BinaryDigit)?;
fragment BinaryDigit: [01];
fragment BinaryDigitOrUnderscore: BinaryDigit | '_';

// Floating-Point Literals, following Java spec
FloatingPointLiteral: DecimalFloatingPointLiteral | HexadecimalFloatingPointLiteral;

fragment DecimalFloatingPointLiteral
    :   Sign? Digits '.' Digits? ExponentPart? FloatTypeSuffix?
    |   Sign? '.' Digits ExponentPart? FloatTypeSuffix?
    |   Sign? Digits ExponentPart FloatTypeSuffix?
    |   Sign? Digits FloatTypeSuffix
    ;

fragment ExponentPart: ExponentIndicator SignedInteger;
fragment ExponentIndicator: [eE];
fragment SignedInteger: Sign? Digits;
fragment Sign: [+-];
fragment FloatTypeSuffix: [fFdD];
fragment HexadecimalFloatingPointLiteral: HexSignificand BinaryExponent FloatTypeSuffix?;
fragment HexSignificand: HexNumeral '.'? | '0' [xX] HexDigits? '.' HexDigits;
fragment BinaryExponent: BinaryExponentIndicator SignedInteger;
fragment BinaryExponentIndicator: [pP];

// Character Literals
SingleQuotedStringLiteral: '\'' ~['\\]+ '\'';

// String Literals
DoubleQuotedStringLiteral: '"' StringCharacters? '"';
fragment StringCharacters: StringCharacter+;
fragment StringCharacter: ~["\\] | EscapeSequence;

// RichText Literals
RichTextLiteral: '"' StringCharacters? '"';

// Escape Sequences for Character and String Literals
fragment EscapeSequence
    :   '\\' [btnfr"'\\]
    |   OctalEscape
    |   UnicodeEscape
    ;
fragment OctalEscape
    :   '\\' OctalDigit
    |   '\\' OctalDigit OctalDigit
    |   '\\' ZeroToThree OctalDigit OctalDigit
    ;
fragment UnicodeEscape: '\\' 'u' HexDigit HexDigit HexDigit HexDigit;
fragment ZeroToThree: [0-3];

// Skip all whitespace signs
WS: [ \t\r\n]+ -> channel(HIDDEN) ;