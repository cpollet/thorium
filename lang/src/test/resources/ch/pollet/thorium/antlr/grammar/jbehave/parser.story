Scenario: invalid inputs
Given exception expected
And a list of statements <statements>
Then the exception <exception> is thrown with message <message>

Examples:
| statements    | exception                                             | message                                                                                                                                                                       |
| 1             | org.antlr.v4.runtime.misc.ParseCancellationException  | line 1:1 missing ';' at '<EOF>'                                                                                                                                               |
| (1)           | org.antlr.v4.runtime.misc.ParseCancellationException  | line 1:3 missing ';' at '<EOF>'                                                                                                                                               |
| { 1 }         | org.antlr.v4.runtime.misc.ParseCancellationException  | line 1:4 mismatched input '}' expecting {';', '*', '+', '='}                                                                                                                  |
| a = { 1 };    | org.antlr.v4.runtime.misc.ParseCancellationException  | line 1:4 no viable alternative at input '{'                                                                                                                                   |
| { 1; } + 1    | org.antlr.v4.runtime.misc.ParseCancellationException  | line 1:7 extraneous input '+' expecting {<EOF>, ';', '(', '{', 'if', IntegerLiteral, FloatLiteral, BooleanLiteral, ObjectOrClassName, VariableName, ConstantName, MethodName} |

