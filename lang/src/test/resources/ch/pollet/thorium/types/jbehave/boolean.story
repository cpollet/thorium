Scenario: boolean method
Given method is <left> <method> <right>
When decode method
And evaluate
Then the result is <result>

Examples:
| left      | method    | right     | result    |
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

Scenario: boolean method types
Given method is <left> <method> <right>
When decode method
Then the result type is <type>

Examples:
| left      | method    | right     | type      |
| Boolean   | +         | Boolean   | Boolean   |
| Boolean   | *         | Boolean   | Boolean   |

Scenario: boolean method with unsupported parameter
Given method is <left> <method> <right>
Given an exception <exception> is expected
When decode method
Then the exception was thrown with message <message>

Examples:
| left      | method    | right     | exception                                                     | message                                       |
| Boolean   | +         | Integer   | ch.pollet.thorium.analysis.exceptions.MethodNotFoundException  | Method +(Integer) not implemented on Boolean  |
| Boolean   | +         | Float     | ch.pollet.thorium.analysis.exceptions.MethodNotFoundException  | Method +(Float) not implemented on Boolean    |
| Boolean   | *         | Integer   | ch.pollet.thorium.analysis.exceptions.MethodNotFoundException  | Method *(Integer) not implemented on Boolean  |
| Boolean   | *         | Float     | ch.pollet.thorium.analysis.exceptions.MethodNotFoundException  | Method *(Float) not implemented on Boolean    |