Scenario: binary operators expressions
Given an expression <expression>
When being executed
Then the stack contains <count> elements
And the result is <result> of type <type>

Examples:
| expression                                                | result            | type      | count |
| 1 + 1                                                     | 2                 | Integer   | 1     |
| 1 + 1 + 1                                                 | 3                 | Integer   | 1     |
| 2 * 2                                                     | 4                 | Integer   | 1     |
| 2 * 2 * 2                                                 | 8                 | Integer   | 1     |
| 0 + 1 * 2                                                 | 2                 | Integer   | 1     |
| 0 + (1 * 2)                                               | 2                 | Integer   | 1     |
| 0 * 1 + 2                                                 | 2                 | Integer   | 1     |
| 0 * (1 + 2)                                               | 0                 | Integer   | 1     |
| 2 * (2 + 2 * (2 + 2))                                     | 20                | Integer   | 1     |
| 1.0 + 1.0                                                 | 2.0               | Float     | 1     |
| 1.0 * 1.0                                                 | 1.0               | Float     | 1     |
| 1.0 + 1                                                   | 2.0               | Float     | 1     |
| 1 + 1.0                                                   | 2.0               | Float     | 1     |
| 1.0 * 1                                                   | 1.0               | Float     | 1     |
| 1 * 1.0                                                   | 1.0               | Float     | 1     |
| a = 1.0                                                   | 1.0               | Float     | 1     |
| ({ 1.0; })                                                | 1.0               | Float     | 1     |
| true                                                      | true              | Boolean   | 1     |
| false                                                     | false             | Boolean   | 1     |
| true + false                                              | true              | Boolean   | 1     |
| true * false                                              | false             | Boolean   | 1     |
| (if (true) { 1; } else { 2; })                            | 1                 | Integer   | 1     |
| (if (false) { 1; } else { 2; })                           | 2                 | Integer   | 1     |
| (if (false) { 1; } else if (true) { 2; } else { 3; })     | 2                 | Integer   | 1     |
| (if (false) { 1; } else if (false) { 2; } else { 3; })    | 3                 | Integer   | 1     |
| (if (true) { 1; })                                        | 1                 | Integer   | 1     |
| (if (false) { 1; })                                       | N/A               | Void      | 1     |
| (if (false) { 1; } else if (false) { 2; })                | N/A               | Void      | 1     |

Scenario: assignment expressions
Given an expression <expression>
When being executed
Then the symbol table contains <symbols>
Then the symbols <symbols> have values <values> of types <types>
Then the symbols <undefined-symbols> are not defined

Examples:
| expression                | symbols   | values    | types             | undefined-symbols |
| a = 1                     | a         | 1         | Integer           |                   |
| a = 1.0                   | a         | 1.0       | Float             |                   |
| A = 1                     | A         | 1         | Integer           |                   |
| A = 1.0                   | A         | 1.0       | Float             |                   |
| b = (a = 1) * 1.0         | a,b       | 1,1.0     | Integer,Float     |                   |
| b = a = 1 * 1.0           | a,b       | 1.0,1.0   | Float,Float       |                   |
| b = ({ a = 1; a + 1; })   | b         | 2         | Integer           | a                 |

Scenario: expressions with exceptions
Given an expression <expression>
Given exception expected
When being executed
Then the exception <exception> is thrown

Examples:
| expression    | exception                             |
| true + 1      | .data.method.MethodNotFoundException  |
| 1 + true      | .data.method.MethodNotFoundException  |