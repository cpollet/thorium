Scenario: binary operators expressions
Given an expression <expression>
When being executed
Then the result is <result> of type <type>

Examples:
| expression            | result            | type          |
| 1 + 1                 | 2                 | IntegerType   |
| 1 + 1 + 1             | 3                 | IntegerType   |
| 2 * 2                 | 4                 | IntegerType   |
| 2 * 2 * 2             | 8                 | IntegerType   |
| 0 + 1 * 2             | 2                 | IntegerType   |
| 0 + (1 * 2)           | 2                 | IntegerType   |
| 0 * 1 + 2             | 2                 | IntegerType   |
| 0 * (1 + 2)           | 0                 | IntegerType   |
| 2 * (2 + 2 * (2 + 2)) | 20                | IntegerType   |
| 1.0 + 1.0             | 2.0               | FloatType     |
| 1.0 * 1.0             | 1.0               | FloatType     |
| 1.0 + 1               | 2.0               | FloatType     |
| 1 + 1.0               | 2.0               | FloatType     |
| 1.0 * 1               | 1.0               | FloatType     |
| 1 * 1.0               | 1.0               | FloatType     |
| a = 1.0               | 1.0               | FloatType     |
| { 1.0; }              | 1.0               | FloatType     |
| true                  | true              | BooleanType   |
| false                 | false             | BooleanType   |
| true + false          | true              | BooleanType   |
| true * false          | false             | BooleanType   |

Scenario: assignment expressions
Given an expression <expression>
When being executed
Then the symbol table contains <symbols>
Then the symbols <symbols> have values <values> of types <types>
Then the symbols <undefinedSymbols> are not defined

Examples:
| expression            | symbols   | values    | types                 | undefinedSymbols  |
| a = 1                 | a         | 1         | IntegerType           |                   |
| a = 1.0               | a         | 1.0       | FloatType             |                   |
| A = 1                 | A         | 1         | IntegerType           |                   |
| A = 1.0               | A         | 1.0       | FloatType             |                   |
| b = (a = 1) * 1.0     | a,b       | 1,1.0     | IntegerType,FloatType |                   |
| b = a = 1 * 1.0       | a,b       | 1.0,1.0   | FloatType,FloatType   |                   |
| b = { a = 1; a + 1; } | b         | 2         | IntegerType           | a                 |

Scenario: expressions with exceptions
Given an expression <expression>
Given exception expected
When being executed
Then the exception <exception> is thrown with message <message>

Examples:
| expression    | exception                                                             | message                                      |
| 1 = 1         | ch.pollet.thorium.semantic.exception.InvalidAssignmentTargetException | Cannot assign to IntegerType{value=1}        |
| 1 = a         | ch.pollet.thorium.semantic.exception.InvalidAssignmentTargetException | Cannot assign to IntegerType{value=1}        |
| 1 = A         | ch.pollet.thorium.semantic.exception.InvalidAssignmentTargetException | Cannot assign to IntegerType{value=1}        |
| true + 1      | ch.pollet.thorium.semantic.exception.MethodNotFoundException          | Method +(Integer) not implemented on Boolean |
| 1 + true      | ch.pollet.thorium.semantic.exception.MethodNotFoundException          | Method +(Boolean) not implemented on Integer |