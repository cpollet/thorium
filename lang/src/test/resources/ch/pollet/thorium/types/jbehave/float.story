Scenario: float methods
Given method is <left> <method> <right>
When decode method
And evaluate
Then the result is <result>

Examples:
| left      | method    | right     | result    |
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

Scenario: float method types
Given method is <left> <method> <right>
When decode method
Then the result type is <type>

Examples:
| left      | method    | right     | type      |
| Float     | +         | Integer   | Float     |
| Float     | +         | Float     | Float     |
| Float     | *         | Integer   | Float     |
| Float     | *         | Float     | Float     |

Scenario: float method with unsupported parameter
Given method is <left> <method> <right>
When decode method
Then the method is not found

Examples:
| left      | method    | right     |
| Float     | +         | Boolean   |
| Float     | *         | Boolean   |