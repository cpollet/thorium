Scenario: general
Given a list of statements <statements>
When being executed
Then the symbols <symbols> have values <values> of types <types>
And the stack is empty

Examples:
| statements                                                        | symbols   | values    | types                                 |
| a = 0; b = 0; r = if (true) { a = 1; -1; } else { b = 1; -2; };   | a,b,r     | 1,0,-1    | IntegerType,IntegerType,IntegerType   |
| a = 0; b = 0; r = if (false) { a = 1; -1; } else { b = 1; -2; };  | a,b,r     | 0,1,-2    | IntegerType,IntegerType,IntegerType   |

Scenario: scope
Given a list of statements <statements>
When being executed
Then the symbols <undefined-symbols> are not defined

Examples:
| statements                | undefined-symbols |
| if (false) { a = 1; };    | a                 |
| if (true) { a = 1; };     | a                 |
