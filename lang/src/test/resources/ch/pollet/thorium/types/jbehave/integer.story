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
| 1         | +         | Integer?  | Integer?  |
| 1         | +         | Float?    | Float?    |
| Integer?  | +         | 0         | Integer?  |
| Integer?  | +         | 0.0       | Float?    |
| 1         | *         | -1        | -1        |
| 1         | *         | 0         | 0         |
| 1         | *         | 0.0       | 0.0       |
| 1         | *         | Integer?  | Integer?  |
| 1         | *         | Float?    | Float?    |
| 0         | *         | Integer?  | 0         |
| 0         | *         | Float?    | 0.0       |
| Integer?  | *         | 0         | 0         |
| Integer?  | *         | 0.0       | 0.0       |
| 1         | <         | 2         | true      |
| 1         | <         | 1         | false     |
| 1         | <         | 1.0       | false     |
| 1         | <=        | 2         | true      |
| 1         | <=        | 1         | true      |
| 1         | <=        | 1.0       | true      |
| 2         | >         | 1         | true      |
| 2         | >         | 2         | false     |
| 2         | >         | 2.0       | false     |
| 2         | >=        | 1         | true      |
| 2         | >=        | 2         | true      |
| 2         | >=        | 2.0       | true      |
| Integer?  | <         | 1         | Boolean?  |
| 1         | <         | Integer?  | Boolean?  |
| Integer?  | <=        | 1         | Boolean?  |
| 1         | <=        | Integer?  | Boolean?  |
| Integer?  | >         | 1         | Boolean?  |
| 1         | >         | Integer?  | Boolean?  |
| Integer?  | >=        | 1         | Boolean?  |

Scenario: integer method types
Given method is <left> <method> <right>
When decode method
Then the result type is <type>

Examples:
| left      | method    | right     | type      |
| Integer?  | +         | Integer?  | Integer?  |
| Integer?  | +         | Float?    | Float?    |
| Integer?  | *         | Integer?  | Integer?  |
| Integer?  | *         | Float?    | Float?    |

Scenario: integer method with unsupported parameter
Given method is <left> <method> <right>
And exception expected
When decode method
Then the method is not found

Examples:
| left      | method    | right     |
| Integer?  | +         | Boolean?  |
| Integer?  | *         | Boolean?  |

Scenario: types compatibility
Given target type <target>
When verify compatibility with source type <source>
Then target and source types are <compatible>

Examples:
| target    | source    | compatible    |
| Integer?  | Integer?  | yes           |
| Integer?  | Integer   | yes           |
| Integer   | Integer   | yes           |
| Void?     | Integer   | yes           |
| Void?     | Integer?  | yes           |
| Void      | Integer   | yes           |
| Integer   | Integer?  | no            |
| Void      | Integer?  | no            |