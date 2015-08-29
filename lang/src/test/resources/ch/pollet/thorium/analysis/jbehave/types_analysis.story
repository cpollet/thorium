Scenario: types are attached to expression nodes
Given an expression <expression>
When types are attached to nodes
Then root node is of types <types>

Examples:
| expression                        | types         |
| true                              | Boolean       |
| 1                                 | Integer       |
| 1.0                               | Float         |
| 1 * 1                             | Integer       |
| 1 + 1                             | Integer       |
| (1)                               | Integer       |
| 1 = 1                             | Integer       |
| ({ 1; })                          | Integer       |
| ({ 1; true; })                    | Boolean       |
| ({ 1; { true; } })                | Boolean       |
| ({ 1 if true; })                  | Integer       |
| ({ 1 unless true; })              | Integer       |
| (if (true) { 1; } else { 1; })    | Integer       |