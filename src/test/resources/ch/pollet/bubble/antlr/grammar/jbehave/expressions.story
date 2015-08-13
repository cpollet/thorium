Given an expression <expression>
When being executed
Then the result is <expectedResult> of type <type>

Examples:
| expression            | expectedResult    | type        |
| 1 + 1                 | 2                 | IntegerType |
| 1 + 1 + 1             | 3                 | IntegerType |
| 2 * 2                 | 4                 | IntegerType |
| 2 * 2 * 2             | 8                 | IntegerType |
| 0 + 1 * 2             | 2                 | IntegerType |
| 0 + (1 * 2)           | 2                 | IntegerType |
| 0 * 1 + 2             | 2                 | IntegerType |
| 0 * (1 + 2)           | 0                 | IntegerType |
| 2 * (2 + 2 * (2 + 2)) | 20                | IntegerType |
| 1.0 + 1.0             | 2.0               | FloatType   |
| 1.0 * 1.0             | 1.0               | FloatType   |