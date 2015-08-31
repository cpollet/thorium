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
| a                                                     | Void          |
| A                                                     | Void          |
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

Scenario: expressions can only have one type
Given an expression <expression>
And exception expected
When types are attached to nodes
Then the exception <exception> is thrown

Examples:
| expression                                                    | exception                                                     |
| (if (true) { 1; } else { 1.0; })                              | ch.pollet.thorium.analysis.exceptions.InvalidTypeException    |
| (if (true) { 1; } else if (false) { 1.0; } else { true; })    | ch.pollet.thorium.analysis.exceptions.InvalidTypeException    |
| (if (true) { 1; } else if (false) { 1; } else { true; })      | ch.pollet.thorium.analysis.exceptions.InvalidTypeException    |
| ({a = 1; a = 1.0;})                                           | ch.pollet.thorium.analysis.exceptions.InvalidTypeException    |

