Scenario: statements with symbols
Given a list of statements <statements>
When being executed
Then the symbol table contains <symbols>
And the symbols <symbols> have values <values> of types <types>
And the symbols <undefined-symbols> are not defined
And the stack is empty

Examples:
| statements                    | symbols   | values    | types                                 | undefined-symbols |
| a = 1; b = a; a = 2;          | a,b       | 2,1       | IntegerType,IntegerType               |                   |
| a = 1; B = a; a = 2;          | a,B       | 2,1       | IntegerType,IntegerType               |                   |
| A; A = 1;                     | A         | 1         | IntegerType                           |                   |
| a = 1; c = b = a + 1;         | a,b,c     | 1,2,2     | IntegerType,IntegerType,IntegerType   |                   |
| a = 1; c = b = a * 1.0;       | a,b,c     | 1,1.0,1.0 | IntegerType,FloatType,FloatType       |                   |
| a = 1; a = a + 1;             | a         | 2         | IntegerType                           |                   |
| a = 1; { a = a + 1; }         | a         | 2         | IntegerType                           |                   |
| a = 1; b = ({ a + 1; });      | a,b       | 1,2       | IntegerType,IntegerType               |                   |
| a = 1; { b = 1; }             | a         | 1         | IntegerType                           | b                 |
| a = 1; ;;                     | a         | 1         | IntegerType                           |                   |
| a = 1 if true;                | a         | 1         | IntegerType                           |                   |
| a = 0; a = 1 if false;        | a         | 0         | IntegerType                           |                   |
| a = 1 unless false;           | a         | 1         | IntegerType                           |                   |
| a = 0; a = 1 unless true;     | a         | 0         | IntegerType                           |                   |

Scenario: statements with result
Given a list of statements <statements>
When being executed
Then the statement result is <result> of type <type>
And the stack is empty

Examples:
| statements                    | result    | type          |
| 1;                            | 1         | IntegerType   |
| ({ 1; });                     | 1         | IntegerType   |
| a = 1; b = 2.0; a * b;        | 2.0       | FloatType     |
| a = 1; a * 2.0;               | 2.0       | FloatType     |
| a = 1 * 2; b = a * 2; b * 1;  | 4         | IntegerType   |
| { 2; }                        | 2         | IntegerType   |
| a = 1; { a * 2; }             | 2         | IntegerType   |
| a = 1; b = 2;                 | 2         | IntegerType   |
| 1 + 1 if true;                | 2         | IntegerType   |
| 1 + 1 if false;               | N/A       | NullType      |

Scenario: statements with exceptions
Given a list of statements <statements>
Given exception expected
When being executed
Then the exception <exception> is thrown with message <message>

Examples:
| statements                    | exception                                                             | message                                           |
| a; b = a;                     | ch.pollet.thorium.semantic.exception.InvalidAssignmentSourceException | Cannot assign from Symbol(a: Void)                |
| A = 1; A = 2;                 | ch.pollet.thorium.semantic.exception.InvalidAssignmentTargetException | Cannot assign Integer(2) to Symbol(A: Integer(1)) |
