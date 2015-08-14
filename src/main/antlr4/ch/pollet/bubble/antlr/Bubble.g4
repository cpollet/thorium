/*
 * Copyright 2015 Christophe Pollet
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
    : statement* EOF
    ;

// STATEMENTS / BLOCKS

// used mainly for testing purposes at the moment
statements
    : statement+
    ;

statement
    : expression ';'
    ;

// EXPRESSIONS

expression
    : expression '*' expression                         # multiplicationExpression
    | expression '+' expression                         # additionExpression
    | literal                                           # literalExpression
    | '(' expression ')'                                # parenthesisExpression
    // | expression ':' expression '?' expression       # inlineConditionExpression
    | <assoc=right> expression '=' expression           # assignmentExpression
    ;

literal
    : IntegerLiteral                                    # integerLiteral
    | FloatLiteral                                      # floatLiteral
    | Identifier                                        # identifierLiteral
    ;

FloatLiteral
    : IntegerLiteral '.' [0]* IntegerLiteral
    ;

IntegerLiteral
    : '0'
    | [1-9][0-9]*
    ;

Identifier
    : ObjectOrClassName
    | VariableName
    | ConstantName
    ;

ObjectOrClassName
    : [A-Z] IdentifierChars
    ;

VariableName
    : [a-z_] IdentifierChars*
    ;

fragment
SymbolMethodName
    : '+=' | '++' | '+'
    | '-=' | '--' | '-'
    | '*=' | '*'
    | '/=' | '/'
    | '%=' | '%'
    | '^=' | '^'
    | '~=' | '~'
    | '===' | '=='
    | '!==' | '!=' | '!'
    | '&&=' | '&&' | '&=' | '&'
    | '||=' | '||' | '|=' | '|'
    | '<<=' | '<<' | '<=' | '<'
    | '>>=' | '>>' | '>=' | '>'
    | '..'
    | '->'
    ;

MethodName
    : [a-z_] IdentifierChars*
    | SymbolMethodName
    ;

fragment
IdentifierChars
    : [a-zA-Z0-9_$]+
    ;

ConstantName
    : [A-Z0-9_$]+
    ;

//
// Whitespace and comments
//
WS
    : [ \t\r\n\u000C]+ -> skip
    ;

COMMENT
    : '/*' .*? '*/' -> skip
    ;

LINE_COMMENT
    : '//' ~[\r\n]* -> skip
    ;