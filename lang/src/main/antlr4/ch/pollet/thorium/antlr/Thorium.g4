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

/** A thorium grammar for ANTLR v4
 * 
 *  You can test with
 *
 *  $ antlr4 Thorium.g4
 *  $ javac *.java
 *  $ grun Thorium compilationUnit *.th
 */
grammar Thorium;

// starting point for parsing a thorium file
compilationUnit
    : statement* EOF
    ;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// STATEMENTS

statements
    : statement+
    ;

statement
    : block
    | expressionStatement
    | ';'
    ;

expressionStatement
    : expression ';'                                    # unconditionalStatement
    | variableDeclaration ';'                           # variableDeclarationStatement
    | expression IF expression ';'                      # conditionalIfStatement
    | expression UNLESS expression ';'                  # conditionalUnlessStatement
    // | expression WHILE expression ';'                   # repeatedStatement
    ;

block
    : statementsBlock
    | ifStatement
    ;

statementsBlock
    : '{' statements '}'
    ;

ifStatement
    : IF '(' expression ')' '{' statements '}' elseStatement?
    ;
elseStatement
    : ELSE '{' statements '}'
    | ELSE ifStatement
    ;

// loopStatement
//     : (WHILE | FOR) '(' expression  (';' expression)* ')' '{' statements '}'
//     ;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// EXPRESSIONS

variableDeclaration
    : DEF ( VariableName | ConstantName ) (':' ObjectOrClassName)? ('=' expression)?
    ;

expression
    : expression '*' expression                         # multiplicationExpression
    | expression '+' expression                         # additionExpression
    | literal                                           # literalExpression
    | '(' expression ')'                                # parenthesisExpression
    // | expression ':' expression '?' expression       # inlineConditionExpression
    | <assoc=right> identifier '=' expression           # assignmentExpression
    | '(' block ')'                                     # blockExpression
    ;

literal
    : IntegerLiteral                                    # integerLiteral
    | FloatLiteral                                      # floatLiteral
    | BooleanLiteral                                    # booleanLiteral
    | identifier                                        # identifierLiteral
    ;

identifier
    : ObjectOrClassName                                 # objectOrClassName
    | VariableName                                      # variableName
    | ConstantName                                      # constantName
    | MethodName                                        # methodName
    ;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// TOKENS

DEF     : 'def';
UNLESS  : 'unless';
IF      : 'if';
ELSE    : 'else';
WHILE   : 'while';
FOR     : 'for';

IntegerLiteral
    : '0'
    | [+-]?[1-9][0-9]*
    ;

FloatLiteral
    : IntegerLiteral '.' [0]* IntegerLiteral
    ;

BooleanLiteral
    : 'true'
    | 'false'
    ;

ObjectOrClassName
    : [A-Z] IdentifierChars
    ;

VariableName
    : [a-z_] IdentifierChars*
    ;

ConstantName
    : [A-Z0-9_$]+
    ;

OptionalMethodName
    : '?' SymbolMethodName
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
    | '<=>'
    | '..'                          // builds a sequence when applied on Integer
    | '->' | '=>'
    | '??'                          // return left unless it's null, then return right
    ;

MethodCall
    : '.'
    | '?.'
    ;

MethodName
    : [a-z_] IdentifierChars* [!?]?
    | SymbolMethodName
    ;

fragment
IdentifierChars
    : [a-zA-Z0-9_$]+
    ;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// WHITESPACES

WS
    : [ \t\r\n\u000C]+ -> skip
    ;

COMMENT
    : '/*' .*? '*/' -> skip
    ;

LINE_COMMENT
    : '//' ~[\r\n]* -> skip
    ;