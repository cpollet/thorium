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

Scenario: assignment expressions
Given an expression <expression>
When being executed
Then the symbol table contains <symbol>
Then the symbol <symbol> has value <value> of type <type>

Examples:
| expression            | symbol    | value | type              |
| a = 1                 | a         | 1     | IntegerType       |
| a = 1.0               | a         | 1.0   | FloatType         |
