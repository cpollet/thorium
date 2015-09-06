Scenario: types attached to all symbols
Given a compilation unit <unit>
When types are attached to nodes
Then the symbol <symbol> is of type <type>

Examples:
| unit                          | symbol    | type      |
| A = 1;                        | A         | Integer   |
| a = 1;                        | a         | Integer   |
| a = b; b = 1;                 | a         | Integer   |
| a = b; b = c; c = 1;          | a         | Integer   |
| a = (b); b = 1;               | a         | Integer   |
| b; a = ({ b; }); b = 1;       | a         | Integer   |
| b; a = b; b = 1 if true;      | a         | Integer   |
| b; a = b; b = 1 unless true;  | a         | Integer   |
| b = a + 1; a = 1;             | b         | Integer   |
| b = a * 1; a = 1.0;           | b         | Float     |
|-- a = ({ b; }); b = 1; | c         | Void   |


Scenario: types are attached to expression nodes
Given an expression <expression>
When types are attached to nodes
Then root node is of type <type>

Examples:
| expression                                            | type          |
| true                                                  | Boolean       |
| 1                                                     | Integer       |
| 1.0                                                   | Float         |
| 1 * 1                                                 | Integer       |
| 1 + 1                                                 | Integer       |
| 1 + 1.0                                               | Float         |
| (1)                                                   | Integer       |
| ({ 1; })                                              | Integer       |
| ({ 1; true; })                                        | Boolean       |
| ({ 1; { true; } })                                    | Boolean       |
| ({ 1 if true; })                                      | Integer       |
| ({ 1 unless true; })                                  | Integer       |
| (if (true) { 1; })                                    | Integer       |
| (if (true) { 1; } else { 1; })                        | Integer       |
| (if (true) { 1; } else if (false) { 1; } else { 1; }) | Integer       |

Scenario: expressions must have exactly one type
Given an expression <expression>
And exception expected
When types are attached to nodes
Then the exception <exception> is thrown

Examples:
| expression                                                    | exception                                                     |
|-- a                                                             | ch.pollet.thorium.analysis.exceptions.InvalidTypeException    |
|-- A                                                             | ch.pollet.thorium.analysis.exceptions.InvalidTypeException    |
| (if (true) { 1; } else { 1.0; })                              | ch.pollet.thorium.analysis.exceptions.InvalidTypeException    |
| (if (true) { 1; } else if (false) { 1.0; } else { true; })    | ch.pollet.thorium.analysis.exceptions.InvalidTypeException    |
| (if (true) { 1; } else if (false) { 1; } else { true; })      | ch.pollet.thorium.analysis.exceptions.InvalidTypeException    |
| ({a = 1; a = 1.0;})                                           | ch.pollet.thorium.analysis.exceptions.InvalidTypeException    |
| 1 + true                                                      | ch.pollet.thorium.analysis.exceptions.MethodNotFoundException |

Scenario: failing statements
Given a list of statements <statements>
And exception expected
When types are attached to nodes
Then the exception <exception> is thrown with message matching <message>
Examples:
| statements                    | exception                                                             | message                                                                                   |
| a = 1; b = 1.0; a = b;        | ch.pollet.thorium.analysis.exceptions.InvalidTypeException            | Incompatible types found on line [0-9]+:[0-9]+ \(a\): Float is no assignable to Integer   |
| A = 1; A = 2;                 | ch.pollet.thorium.analysis.exceptions.InvalidAssignmentException      | Invalid assignment found on line [0-9]+:[0-9]+ \(A\): unable to change a constant value.  |
|-- a = 1; { b = a + 1; } ; c = b;  | ch.pollet.thorium.semantic.exception.InvalidAssignmentSourceException | Cannot assign from Symbol(b: Void)    |
|-- b; a = b;                     | ch.pollet.thorium.analysis.exceptions.InvalidTypeException            | Type expected, but got Void. |
|-- a; b = a;                     | ch.pollet.thorium.semantic.exception.InvalidAssignmentSourceException | Cannot assign from Symbol(a: Void)                |
