Scenario: integer operators
Given operation is <left> <operator> <right>
When decode operator
And evaluate
Then the result is <result>

Examples:
| left      | operator  | right     | result    |
| 1         | +         | -1        | 0         |
| 1         | +         | 0         | 1         |
| 1         | +         | 0.0       | 1.0       |
| 1         | +         | Integer   | Integer   |
| 1         | +         | Float     | Float     |
| Integer   | +         | 0         | Integer   |
| Integer   | +         | 0.0       | Float     |
| 1         | *         | -1        | -1        |
| 1         | *         | 0         | 0         |
| 1         | *         | 0.0       | 0.0       |
| 1         | *         | Integer   | Integer   |
| 1         | *         | Float     | Float     |
| 0         | *         | Integer   | 0         |
| 0         | *         | Float     | 0.0       |
| Integer   | *         | 0         | 0         |
| Integer   | *         | 0.0       | 0.0       |

Scenario: integer operator with unsupported operand