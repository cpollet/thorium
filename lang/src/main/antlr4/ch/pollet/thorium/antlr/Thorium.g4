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
    : statements EOF
    ;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// STATEMENTS

statements
    : statement+
    ;

statement
    : block
    | expressionStatement
    | variableOrConstantDeclarationStatement
    | ';'
    ;

expressionStatement
    : expression ';'                                                                    # unconditionalStatement
    | expression IF expression ';'                                                      # conditionalIfStatement
    | expression UNLESS expression ';'                                                  # conditionalUnlessStatement
    | expression WHILE expression ';'                                                   # repeatedWhileStatement
    | expression UNTIL expression ';'                                                   # repeatedUntilStatement
    ;

block
    : statementsBlock
    | ifStatement
    | whileLoopStatement
    | forLoopStatement
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

whileLoopStatement
    : WHILE '(' expression ')' '{' statements '}'
    ;
forLoopStatement
    : FOR '(' init=forLoopStatementInit? ';' condition=expression? ';' increment=expression? ')' '{' statements '}'
    ;
forLoopStatementInit
    : expression                                                                            # forLoopStatementInitExpression
    | DEF type? LCFirstIdentifier ('=' expression)?                                         # forLoopStatementInitVariableDeclaration
    ;

variableOrConstantDeclarationStatement
    : DEF type? LCFirstIdentifier ('=' expression)? ';'                                     # variableDeclarationStatement
    | DEF type? UCIdentifier '=' expression ';'                                             # constantDeclarationStatement
    ;

type
    : UCFirstIdentifier '?'?
    ;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// EXPRESSIONS

// memberDeclaration
//     : DEF type? name accessors? ('=' expression)? (':' DELEGATE delegate)?
//     ;
//
// accessors
//     : '{' visibility GET (statementsBlock | ';') visibility SET statementsBlock? '}'    # fullAccessorDefinition
//     | '{' visibility '}'                                                                # shortAccessorDefinition
//     ;
//
// visibility
//     : PRIVATE
//     | PROTECTED
//     | PACKAGE
//     | PUBLIC
//     ;
//
// delegate
//     : ALL                                                                               # delegateAll
//     | methodName (',' methodName)*                                                      # delegateList
//     | ALL BUT methodName (',' methodName)*                                              # delegateAllButList
//     ;
//
// methodName
//     : LCFirstIdentifier
//     | LCFirstIdentifierWithMarkSuffix
//     | SymbolMethodName
//     ;

expression
    : op='!' expression                                                                 # notExpression
    | expression op='*' expression                                                      # multiplicationExpression
    | expression op='+' expression                                                      # additionExpression
    | expression op=( '<' | '<=' | '>' | '>=' ) expression                              # orderComparisonExpression
    | literal                                                                           # literalExpression
    | '(' expression ')'                                                                # parenthesisExpression
    // | expression ':' expression '?' expression                                       # inlineConditionExpression
    | <assoc=right> identifier '=' expression                                           # assignmentExpression
    | '(' block ')'                                                                     # blockExpression
    ;

literal
    : IntegerLiteral                                                                    # integerLiteral
    | FloatLiteral                                                                      # floatLiteral
    | BooleanLiteral                                                                    # booleanLiteral
    | identifier                                                                        # identifierLiteral
    ;

identifier
    : UCFirstIdentifier                                                                 # objectOrClassName
    | ( LCFirstIdentifier | LCFirstIdentifierWithMarkSuffix )                           # variableName
    | UCIdentifier                                                                      # constantName
    ;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// TOKENS

// CLASS       : 'class';
// INTERFACE   : 'interface';
// IMPLEMENTS  : 'implements';
// RETURN      : 'return';
ALL         : 'all';
BUT         : 'but';
DELEGATE    : 'delegate';
GET         : 'get';
SET         : 'set';
PRIVATE     : 'private';
PROTECTED   : 'protected';
PACKAGE     : 'package';
PUBLIC      : 'public';
DEF         : 'def';
UNLESS      : 'unless';
IF          : 'if';
ELSE        : 'else';
WHILE       : 'while';
UNTIL       : 'until';
FOR         : 'for';

IntegerLiteral
    : '0'
    | [+-]?[1-9][0-9]*
    ;

FloatLiteral
    : IntegerLiteral '.' [0]* IntegerLiteral
    ;

BooleanLiteral
    : TRUE
    | FALSE
    ;

TRUE        : 'true';
FALSE       : 'false';

fragment
IdentifierChars
    : [a-zA-Z0-9_]+
    ;

UCFirstIdentifier
    : [A-Z] IdentifierChars
    ;

LCFirstIdentifierWithMarkSuffix
     : LCFirstIdentifier '?'
     | LCFirstIdentifier '!'
     ;

LCFirstIdentifier
    : [a-z_] IdentifierChars*
    ;

UCIdentifier
    : [A-Z0-9_]+
    ;

SymbolMethodName
    : '+=' | '++' | '+'             // + implemented on Integer, Float and Boolean
    | '-=' | '--' | '-'
    | '*=' | '*'                    // * implemented on Integer, Float and Boolean
    | '/=' | '/'
    | '%=' | '%'
    | '^=' | '^'
    | '~=' | '~'
    | '===' | '=='
    | '!==' | '!=' | '!'            // ! implemented on Boolean
    | '&&=' | '&&' | '&=' | '&'
    | '||=' | '||' | '|=' | '|'
    | '<<=' | '<<' | '<=' | '<'     // <=, < implemented on Integer and Float
    | '>>=' | '>>' | '>=' | '>'     // >=, > implemented on Integer and Float
    | '<=>'                         // compare to with Ineteger, Float
    | '..'                          // builds a sequence when applied on Integer
    | '->'                          // accesses a property without using getter/setter
    | '=>'
    | '??='                         // a ??= 1 is the same as a = a ?? 1;
    | '??'                          // return left unless it's null, then return right
    ;

MethodCall
    : '.'
    | '?.'
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