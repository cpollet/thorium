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
| (1)                                                   | Integer       |
| 1 = 1                                                 | Integer       |
| ({ 1; })                                              | Integer       |
| ({ 1; true; })                                        | Boolean       |
| ({ 1; { true; } })                                    | Boolean       |
| ({ 1 if true; })                                      | Integer       |
| ({ 1 unless true; })                                  | Integer       |
| (if (true) { 1; })                                    | Integer       |
| (if (true) { 1; } else { 1; })                        | Integer       |
| (if (true) { 1; } else if (false) { 1; } else { 1; }) | Integer       |

Scenario: expressions can have only one type
Given an expression <expression>
And exception expected
When types are attached to nodes
Then the exception <exception> is thrown

Examples:
| expression                                                    | exception                                                     |
| (if (true) { 1; } else { 1.0; })                              | ch.pollet.thorium.analysis.exceptions.InvalidTypeException    |
| (if (true) { 1; } else if (false) { 1.0; } else { true; })    | ch.pollet.thorium.analysis.exceptions.InvalidTypeException    |
| (if (true) { 1; } else if (false) { 1; } else { true; })      | ch.pollet.thorium.analysis.exceptions.InvalidTypeException    |