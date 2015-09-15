Scenario: statements with symbols
Given a list of statements <statements>
When being executed
Then the symbol table contains <symbols>
And the symbols <symbols> have values <values> of types <types>
And the symbols <undefined-symbols> are not defined
And the stack is empty

Examples:
| statements                                                    | symbols   | values    | types                     | undefined-symbols |
| a = 1; b = a; a = 2;                                          | a,b       | 2,1       | Integer,Integer           |                   |
| a = 1; B = a; a = 2;                                          | a,B       | 2,1       | Integer,Integer           |                   |
| A; A = 1;                                                     | A         | 1         | Integer                   |                   |
| a = 1; c = b = a + 1;                                         | a,b,c     | 1,2,2     | Integer,Integer,Integer   |                   |
| a = 1; c = b = a * 1.0;                                       | a,b,c     | 1,1.0,1.0 | Integer,Float,Float       |                   |
| a = 1; a = a + 1;                                             | a         | 2         | Integer                   |                   |
| a = 1; { a = a + 1; }                                         | a         | 2         | Integer                   |                   |
| a = 1; b = ({ a + 1; });                                      | a,b       | 1,2       | Integer,Integer           |                   |
| a = 1; { b = 1; }                                             | a         | 1         | Integer                   | b                 |
| a = 1; ;;                                                     | a         | 1         | Integer                   |                   |
| a = 1 if true;                                                | a         | 1         | Integer                   |                   |
| a = 0; a = 1 if false;                                        | a         | 0         | Integer                   |                   |
| a = 1 unless false;                                           | a         | 1         | Integer                   |                   |
| a = 0; a = 1 unless true;                                     | a         | 0         | Integer                   |                   |
| a = 0; a = a + 1 while a < 5;                                 | a         | 5         | Integer                   |                   |
| a = 0; a = a + 1 until a >= 5;                                | a         | 5         | Integer                   |                   |
| a = 0; while (a < 3) { a = a + 1; }                           | a         | 3         | Integer                   |                   |
| b = 0; a = 0; for (b = 0; b < 3; b = b + 1) { a = a + 1; }    | a         | 3         | Integer                   |                   |
| def a;                                                        | a         |           | Void                      |                   |
| def Integer a;                                                | a         |           | Void                      |                   |
| def a = 1;                                                    | a         | 1         | Integer                   |                   |
| def A = 1;                                                    | A         | 1         | Integer                   |                   |

Scenario: statements with result
Given a list of statements <statements>
When being executed
Then the statement result is <result> of type <type>
And the stack is empty

Examples:
| statements                    | result    | type      |
| 1;                            | 1         | Integer   |
| ({ 1; });                     | 1         | Integer   |
| a = 1; b = 2.0; a * b;        | 2.0       | Float     |
| a = 1; a * 2.0;               | 2.0       | Float     |
| a = 1 * 2; b = a * 2; b * 1;  | 4         | Integer   |
| { 2; }                        | 2         | Integer   |
| a = 1; { a * 2; }             | 2         | Integer   |
| a = 1; b = 2;                 | 2         | Integer   |
| 1 + 1 if true;                | 2         | Integer   |
| 1 + 1 if false;               | N/A       | Void      |
| !true;                        | false     | Boolean   |
| !false;                       | true      | Boolean   |
