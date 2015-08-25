Scenario: boolean operators
Given operation is <left> <operator> <right>
When decode operator
And evaluate
Then the result is <result>

Examples:
| left      | operator  | right     | result    |
| true      | +         | true      | true      |
| true      | +         | false     | true      |
| true      | +         | Boolean   | true      |
| false     | +         | true      | true      |
| false     | +         | false     | false     |
| false     | +         | Boolean   | Boolean   |
| Boolean   | +         | true      | true      |
| Boolean   | +         | false     | Boolean   |
| true      | *         | true      | true      |
| true      | *         | false     | false     |
| true      | *         | Boolean   | Boolean   |
| false     | *         | true      | false     |
| false     | *         | false     | false     |
| false     | *         | Boolean   | false     |
| Boolean   | *         | true      | Boolean   |
| Boolean   | *         | false     | false     |

Scenario: boolean operator with unsupported operand