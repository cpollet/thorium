Scenario: integer methods
Given method is <left> <method> <right>
When decode method
And evaluate
Then the result is <result>

Examples:
| left      | method    | right     | result    |
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

Scenario: integer method types
Given method is <left> <method> <right>
When decode method
Then the result type is <type>

Examples:
| left      | method    | right     | type      |
| Integer   | +         | Integer   | Integer   |
| Integer   | +         | Float     | Float     |
| Integer   | *         | Integer   | Integer   |
| Integer   | *         | Float     | Float     |

Scenario: integer method with unsupported parameter
Given method is <left> <method> <right>
Given an exception <exception> is expected
When decode method
Then the exception was thrown with message <message>

Examples:
| left      | method    | right     | exception                                                     | message                                       |
| Integer   | +         | Boolean   | ch.pollet.thorium.analysis.exceptions.MethodNotFoundException  | Method +(Boolean) not implemented on Integer  |
| Integer   | *         | Boolean   | ch.pollet.thorium.analysis.exceptions.MethodNotFoundException  | Method *(Boolean) not implemented on Integer  |