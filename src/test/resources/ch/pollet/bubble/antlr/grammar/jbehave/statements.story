Scenario: statements
Given a list of statements <statements>
When being executed
Then the symbol table contains <symbols>
Then the symbols <symbols> have values <values> of types <types>

Examples:
| statements                | symbols   | values    | types                     |
| a = 1 ; b = a ; a = 2 ;   | a,b       | 2,1       | IntegerType,IntegerType   |