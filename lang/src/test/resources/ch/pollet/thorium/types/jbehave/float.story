Scenario: integer operators
Given operation is <left> <operator> <right>
When decode operator
And evaluate
Then the result is <result>

Examples:
| left      | operator  | right     | result    |
| 1.0       | +         | -1.0      | 0.0       |
| 1.0       | +         | 0.0       | 1.0       |
| 1.0       | +         | 0         | 1.0       |
| 1.0       | +         | Integer   | Float     |
| 1.0       | +         | Float     | Float     |
| Float     | +         | 0         | Float     |
| Float     | +         | 0.0       | Float     |
| 1.0       | *         | -1.0      | -1.0      |
| 1.0       | *         | 0         | 0.0       |
| 1.0       | *         | 0.0       | 0.0       |
| 1.0       | *         | Integer   | Float     |
| 1.0       | *         | Float     | Float     |
| 0.0       | *         | Integer   | 0.0       |
| 0.0       | *         | Float     | 0.0       |
| Float     | *         | 0         | 0.0       |
| Float     | *         | 0.0       | 0.0       |

Scenario: integer operator with unsupported operand