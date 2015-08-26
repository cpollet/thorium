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
Given operation is <left> <operator> <right>
Given an exception <exception> is expected
When decode operator
Then the exception was thrown with message <message>

Examples:
| left      | operator  | right     | exception                                                     | message                                       |
| Boolean   | +         | Integer   | ch.pollet.thorium.semantic.exception.MethodNotFoundException  | Method +(Integer) not implemented on Boolean  |
| Boolean   | +         | Float     | ch.pollet.thorium.semantic.exception.MethodNotFoundException  | Method +(Float) not implemented on Boolean    |
| Boolean   | *         | Integer   | ch.pollet.thorium.semantic.exception.MethodNotFoundException  | Method *(Integer) not implemented on Boolean  |
| Boolean   | *         | Float     | ch.pollet.thorium.semantic.exception.MethodNotFoundException  | Method *(Float) not implemented on Boolean    |