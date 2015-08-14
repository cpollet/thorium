Scenario: statements with symbols
Given a list of statements <statements>
When being executed
Then the symbol table contains <symbols>
Then the symbols <symbols> have values <values> of types <types>

Examples:
| statements            | symbols   | values    | types                     |
| a = 1; b = a; a = 2;  | a,b       | 2,1       | IntegerType,IntegerType   |

Scenario: statements with result
Given a list of statements <statements>
When being executed
Then the result is <result> of type <type>

Examples:
| statements                | result    | type      |
| a = 1; b = 2.0; a * b;    | 2.0       | FloatType |
