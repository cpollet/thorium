Scenario: statements with symbols
Given a list of statements <statements>
When being executed
Then the symbol table contains <symbols>
Then the symbols <symbols> have values <values> of types <types>

Examples:
| statements            | symbols   | values    | types                     |
| a = 1; b = a; a = 2;  | a,b       | 2,1       | IntegerType,IntegerType   |
| a = 1; B = a; a = 2;  | a,B       | 2,1       | IntegerType,IntegerType   |
| A; A = 1;             | A         | 1         | IntegerType               |

Scenario: statements with result
Given a list of statements <statements>
When being executed
Then the result is <result> of type <type>

Examples:
| statements                    | result    | type          |
| a = 1; b = 2.0; a * b;        | 2.0       | FloatType     |
| a = 1; a * 2.0;               | 2.0       | FloatType     |
| a = 1 * 2; b = a * 2; b * 1;  | 4         | IntegerType   |

Scenario: statements with exceptions
Given a list of statements <statements>
When being executed
Then the exception <exception> is thrown with message <message>

Examples:
| statements                    | exception                                                             | message                                                                       |
| a = 1; b = 1.0; a = b;        | ch.pollet.thorium.semantic.exception.InvalidTypeException             | Float is no assignable to Integer                                             |
| a; b = a;                     | ch.pollet.thorium.semantic.exception.InvalidAssignmentSourceException | Cannot assign from Symbol{name='a', type=null, value=null}                    |
| A = 1; A = 2;                 | ch.pollet.thorium.semantic.exception.InvalidAssignmentTargetException | Cannot assign to Symbol{name='A', type=Integer, value=IntegerType{value=1}}   |