/** A bubble grammar for ANTLR v4
 * 
 *  You can test with
 *
 *  $ antlr4 Bubble.g4
 *  $ javac *.java
 *  $ grun Bubble compilationUnit *.bbl
 */
grammar Bubble;

// starting point for parsing a bubble file
compilationUnit
    :   statement
    ;

literal
    :   IntegerLiteral
    ;

// STATEMENTS / BLOCKS

statement
    :   statementExpression ';'
    ;

// EXPRESSIONS

statementExpression
    :   expression
    ;

expression
    :   primary
    |   expression '+' expression
    ;

primary
    :   '(' expression ')'
    |   literal
    ;

// Integer Literals

IntegerLiteral
    :   DecimalIntegerLiteral
    ;

fragment
DecimalIntegerLiteral
    :   DecimalNumeral
    ;

fragment
DecimalNumeral
    :   '0'
    |   NonZeroDigit Digits?
    ;

fragment
Digits
    :   Digit Digit*
    ;

fragment
Digit
    :   '0'
    |   NonZeroDigit
    ;

fragment
NonZeroDigit
    :   [1-9]
    ;

//
// Whitespace and comments
//
WS  :  [ \t\r\n\u000C]+ -> skip
    ;

COMMENT
    :   '/*' .*? '*/' -> skip
    ;

LINE_COMMENT
    :   '//' ~[\r\n]* -> skip
    ;