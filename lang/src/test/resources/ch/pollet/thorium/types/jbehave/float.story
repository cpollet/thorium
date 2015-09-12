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
| 1.0       | <         | 2.0       | true      |
| 1.0       | <         | 1.0       | false     |
| 1.0       | <         | 1         | false     |
| 1.0       | <=        | 2.0       | true      |
| 1.0       | <=        | 1.0       | true      |
| 1.0       | <=        | 1         | true      |
| 2.0       | >         | 1.0       | true      |
| 2.0       | >         | 2.0       | false     |
| 2.0       | >         | 2         | false     |
| 2.0       | >=        | 1.0       | true      |
| 2.0       | >=        | 2.0       | true      |
| 2.0       | >=        | 2         | true      |
| Float     | <         | 1         | Boolean   |
| 1         | <         | Float     | Boolean   |
| Float     | <=        | 1         | Boolean   |
| 1         | <=        | Float     | Boolean   |
| Float     | >         | 1         | Boolean   |
| 1         | >         | Float     | Boolean   |
| Float     | >=        | 1         | Boolean   |

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