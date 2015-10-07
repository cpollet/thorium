Scenario: types attached to all symbols
Given a compilation unit <unit>
When types are attached to nodes
Then the symbol <symbol> is of type <type>

Examples:
| unit                                          | symbol    | type      |
| public Integer test() { ; } def a = test();   | a         | Integer   |
| def a = test(); public Integer test() { ; }   | a         | Integer   |