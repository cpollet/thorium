Scenario: types attached to all symbols
Given a compilation unit <unit>
When types are attached to nodes
Then the symbol <symbol> is of type <type>

Examples:
| unit                                                                  | symbol    | type      |
| def A = 1;                                                            | A         | Integer   |
| def Integer A = 1;                                                    | A         | Integer   |
| def a = 1;                                                            | a         | Integer   |
| def Integer? a;                                                       | a         | Integer?  |
| def Integer a = 1;                                                    | a         | Integer   |
| def Integer? a = 1;                                                   | a         | Integer?  |
| def Integer? a; a = 1;                                                | a         | Integer?  |
| def a; a = 1;                                                         | a         | Integer?  |
| def a = 1; def b = a + 1;                                             | b         | Integer   |
| def b; def a = b; b = 1;                                              | a         | Integer?  |
| def b; def a = b; b = 1 if true;                                      | a         | Integer?  |
| def b; def a = b; b = 1 unless true;                                  | a         | Integer?  |
| def a = 1.0; def b = a * 1;                                           | b         | Float     |
| def a = ({ def b = 1.0; }); def b = 1;                                | a         | Float     |
| def a = ({ def b = 1.0; }); def b = 1;                                | b         | Integer   |
| def b; def a = ({ b; }); b = 1;                                       | a         | Integer?  |
| def a; if (true) { a = 1; }                                           | a         | Integer?  |
| def Boolean? a; def b = !a;                                           | b         | Boolean?  |
| def a = 0; for (a = 0; a < 1; a = a + 1) { ; }                        | a         | Integer   |
| def Boolean? a; for (def a = 0; a < 1; a = a + 1) { ; }               | a         | Boolean?  |
| def Float? a; if (false) { def Integer? a; } else { def Integer? a; } | a         | Float?    |
| def a = true; for (def a = 0; a < 1; a = a + 1) { ; }                 | a         | Boolean   |

Scenario: types are attached to expression nodes
Given an expression <expression>
When types are attached to nodes
Then root node is of type <type>

Examples:
| expression                                                                        | type          |
| true                                                                              | Boolean       |
| false                                                                             | Boolean       |
| 1                                                                                 | Integer       |
| 1.0                                                                               | Float         |
| 1 * 1                                                                             | Integer       |
| 1 + 1                                                                             | Integer       |
| 1 + 1.0                                                                           | Float         |
| (1)                                                                               | Integer       |
| ({ 1; })                                                                          | Integer       |
| ({ 1; true; })                                                                    | Boolean       |
| ({ 1; { true; } })                                                                | Boolean       |
| ({ 1 if true; })                                                                  | Integer?      |
| ({ 1 unless true; })                                                              | Integer?      |
| (if (true) { 1; })                                                                | Integer?      |
| (if (true) { 1; } else { 1; })                                                    | Integer       |
| (if (true) { 1; } else if (false) { 1; } else { 1; })                             | Integer       |
| (if (false) { 1; } else if (false) { 1; })                                        | Integer?      |
| (if (false) { def Integer? a; })                                                  | Integer?      |
| (if (false) { def Integer? a; } else { def Integer? b; })                         | Integer?      |
| (if (false) { def Integer? a; } else if (true) { 1; } else { def Integer? c; })   | Integer?      |
| (if (false) { def Integer? a; } else { 1; })                                      | Integer?      |
| 1 > 1                                                                             | Boolean       |
| 1 >= 1                                                                            | Boolean       |
| 1 < 1                                                                             | Boolean       |
| 1 <= 1                                                                            | Boolean       |
| 1.0 > 1                                                                           | Boolean       |
| 1.0 >= 1                                                                          | Boolean       |
| 1.0 < 1                                                                           | Boolean       |
| 1.0 <= 1                                                                          | Boolean       |
| 1 > 1.0                                                                           | Boolean       |
| 1 >= 1.0                                                                          | Boolean       |
| 1 < 1.0                                                                           | Boolean       |
| 1 <= 1.0                                                                          | Boolean       |
| 1.0 > 1.0                                                                         | Boolean       |
| 1.0 >= 1.0                                                                        | Boolean       |
| 1.0 < 1.0                                                                         | Boolean       |
| 1.0 <= 1.0                                                                        | Boolean       |
| !true                                                                             | Boolean       |
| !false                                                                            | Boolean       |
| (while (true) { 1; })                                                             | Integer?      |
| (for (1; true; 1) { 1; })                                                         | Integer?      |

Scenario: failing statements with only one exception
Given a compilation unit <unit>
And exception expected
When types are attached to nodes
Then the exception <exception> is thrown with message matching <message>

Examples:
| unit                                                          | exception                                         | message                                                                                                       |
| def a;                                                        | .analysis.exceptions.InvalidTypeException         | Type expected, but got Void on line [0-9]+:[0-9]+ \(def\).                                                    |
| def Integer a = 1.0;                                          | .analysis.exceptions.InvalidTypeException         | Incompatible types found on line [0-9]+:[0-9]+ \(1.0\): Float is no assignable to Integer.                    |
| def a = b; def b = 1;                                         | .analysis.exceptions.InvalidSymbolException       | Identifier b not found on line [0-9]+:[0-9]+ \(b\).                                                           |
| def b; if (true) { def b = 1; }                               | .analysis.exceptions.InvalidTypeException         | Type expected, but got Void on line [0-9]+:[0-9]+ \(def\).                                                    |
| def a = 1; def b = 1.0; a = b;                                | .analysis.exceptions.InvalidTypeException         | Incompatible types found on line [0-9]+:[0-9]+ \(a\): Float is no assignable to Integer.                      |
| (if (true) { 1; } else { 1.0; });                             | .analysis.exceptions.InvalidTypeException         | Ambiguous type found on line [0-9]+:[0-9]+ \(\(\): expected only one, but got Float\?, Integer\?.             |
| (if (true) { 1; } else if (false) { 1.0; } else { true; });   | .analysis.exceptions.InvalidTypeException         | Ambiguous type found on line [0-9]+:[0-9]+ \(\(\): expected only one, but got Boolean\?, Float\?, Integer\?.  |
| (if (true) { 1; } else if (false) { 1; } else { true; });     | .analysis.exceptions.InvalidTypeException         | Ambiguous type found on line [0-9]+:[0-9]+ \(\(\): expected only one, but got Boolean\?, Integer\?.           |
| 1 + true;                                                     | .analysis.exceptions.InvalidSymbolException       | Method Integer\.\+\(Boolean\) not implemented on line [0-9]+:[0-9]+ \(1\).                                |
| def A = 1; A = 2;                                             | .analysis.exceptions.InvalidAssignmentException   | Invalid assignment found on line [0-9]+:[0-9]+ \(A\): unable to change a constant value.                  |
| a = 1 if true;                                                | .analysis.exceptions.InvalidSymbolException       | Identifier a not found on line [0-9]+:[0-9]+ \(a\).                                                       |
| def Integer? a; def Integer? a;                               | .analysis.exceptions.InvalidSymbolException       | Identifier a already defined on line [0-9]+:[0-9]+ \(def\) \(was on line [0-9]+:[0-9]+ \(def\)\).         |
| def Integer a = 0; def Float a = 0.0;                         | .analysis.exceptions.InvalidSymbolException       | Identifier a already defined on line [0-9]+:[0-9]+ \(def\) \(was on line [0-9]+:[0-9]+ \(def\)\).         |
| def Integer? a; a = 1 if 1;                                   | .analysis.exceptions.InvalidTypeException         | Invalid type found on line [0-9]+:[0-9]+ \(1\): expected Boolean but got Integer.                         |
| def Integer? a; a = 1 unless 1;                               | .analysis.exceptions.InvalidTypeException         | Invalid type found on line [0-9]+:[0-9]+ \(1\): expected Boolean but got Integer.                         |
| def Integer? a; a = 1 while 1;                                | .analysis.exceptions.InvalidTypeException         | Invalid type found on line [0-9]+:[0-9]+ \(1\): expected Boolean but got Integer.                         |
| def Integer? a; a = 1 until 1;                                | .analysis.exceptions.InvalidTypeException         | Invalid type found on line [0-9]+:[0-9]+ \(1\): expected Boolean but got Integer.                         |
| 1 < true;                                                     | .analysis.exceptions.InvalidSymbolException       | Method Integer\.<\(Boolean\) not implemented on line [0-9]+:[0-9]+ \(1\).                                 |
| 1 <= true;                                                    | .analysis.exceptions.InvalidSymbolException       | Method Integer\.<=\(Boolean\) not implemented on line [0-9]+:[0-9]+ \(1\).                                |
| 1 > true;                                                     | .analysis.exceptions.InvalidSymbolException       | Method Integer\.>\(Boolean\) not implemented on line [0-9]+:[0-9]+ \(1\).                                 |
| 1 >= true;                                                    | .analysis.exceptions.InvalidSymbolException       | Method Integer\.>=\(Boolean\) not implemented on line [0-9]+:[0-9]+ \(1\).                                |
| 1.0 < true;                                                   | .analysis.exceptions.InvalidSymbolException       | Method Float\.<\(Boolean\) not implemented on line [0-9]+:[0-9]+ \(1\.0\).                                |
| 1.0 <= true;                                                  | .analysis.exceptions.InvalidSymbolException       | Method Float\.<=\(Boolean\) not implemented on line [0-9]+:[0-9]+ \(1\.0\).                               |
| 1.0 > true;                                                   | .analysis.exceptions.InvalidSymbolException       | Method Float\.>\(Boolean\) not implemented on line [0-9]+:[0-9]+ \(1\.0\).                                |
| 1.0 >= true;                                                  | .analysis.exceptions.InvalidSymbolException       | Method Float\.>=\(Boolean\) not implemented on line [0-9]+:[0-9]+ \(1\.0\).                               |
| !1;                                                           | .analysis.exceptions.InvalidSymbolException       | Method Integer\.!\(\) not implemented on line [0-9]+:[0-9]+ \(!\).                                        |
| !1.0;                                                         | .analysis.exceptions.InvalidSymbolException       | Method Float\.!\(\) not implemented on line [0-9]+:[0-9]+ \(!\).                                          |
| def a; if (a = true) { ; }                                    | .analysis.exceptions.InvalidTypeException         | Invalid type found on line [0-9]+:[0-9]+ \(a\): expected Boolean but got Boolean\?.                       |
| def a; for (; a = true; ) { ; }                               | .analysis.exceptions.InvalidTypeException         | Invalid type found on line [0-9]+:[0-9]+ \(a\): expected Boolean but got Boolean\?.                       |
| def Integer? a; def b; a = 1 if b; b = true;                  | .analysis.exceptions.InvalidTypeException         | Invalid type found on line [0-9]+:[0-9]+ \(b\): expected Boolean but got Void\?.                          |
| def Integer? a; def b; a = 1 unless b; b = true;              | .analysis.exceptions.InvalidTypeException         | Invalid type found on line [0-9]+:[0-9]+ \(b\): expected Boolean but got Void\?.                          |
| def Integer? a; def b; a = 1 while b; b = true;               | .analysis.exceptions.InvalidTypeException         | Invalid type found on line [0-9]+:[0-9]+ \(b\): expected Boolean but got Void\?.                          |
| def Integer? a; def b; a = 1 until b; b = true;               | .analysis.exceptions.InvalidTypeException         | Invalid type found on line [0-9]+:[0-9]+ \(b\): expected Boolean but got Void\?.                          |
| def a; for (a = 0; a < 1; a = a + 1) { ; }                    | .analysis.exceptions.InvalidTypeException         | Invalid type found on line [0-9]+:[0-9]+ \(a\): expected Boolean but got Boolean\?.                       |
| def Integer? A = 1;                                           | .analysis.exceptions.InvalidTypeException         | Invalid type found on line [0-9]+:[0-9]+ \(def\): expected Integer but got Integer\?.                     |
| def Integer a;                                                | .analysis.exceptions.InvalidTypeException         | Invalid type found on line [0-9]+:[0-9]+ \(def\): expected Integer\? but got Integer.                     |

Scenario: failing statements with only one exception
Given a compilation unit <unit>
And <n> exceptions expected
When types are attached to nodes
Then the exception <i> is <exception> with message matching <message>

Examples:
| unit                  | n | i | exception                                     | message                                                                               |
| a;                    | 2 | 0 | .analysis.exceptions.InvalidSymbolException   | Identifier a not found on line [0-9]+:[0-9]+ \(a\).                                   |
| a;                    | 2 | 1 | .analysis.exceptions.InvalidTypeException     | Type expected, but got Void on line [0-9]+:[0-9]+ \(a\).                              |
| a + 1;                | 2 | 0 | .analysis.exceptions.InvalidSymbolException   | Identifier a not found on line [0-9]+:[0-9]+ \(a\).                                   |
| a + 1;                | 2 | 1 | .analysis.exceptions.InvalidTypeException     | Type expected, but got Void on line [0-9]+:[0-9]+ \(a\).                              |
| if (b = true) { ; }   | 2 | 0 | .analysis.exceptions.InvalidSymbolException   | Identifier b not found on line [0-9]+:[0-9]+ \(b\).                                   |
| if (b = true) { ; }   | 2 | 1 | .analysis.exceptions.InvalidTypeException     | Invalid type found on line [0-9]+:[0-9]+ \(b\): expected Boolean but got Boolean\?.   |
