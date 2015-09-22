Scenario: invalid inputs
Given exception expected
And a list of statements <statements>
Then the exception <exception> is thrown

Examples:
| statements        | exception                                             |
| 1                 | org.antlr.v4.runtime.misc.ParseCancellationException  |
| (1)               | org.antlr.v4.runtime.misc.ParseCancellationException  |
| { 1 }             | org.antlr.v4.runtime.misc.ParseCancellationException  |
| a = { 1; };       | org.antlr.v4.runtime.misc.ParseCancellationException  |
| { 1; } + 1;       | org.antlr.v4.runtime.misc.ParseCancellationException  |
| def Integer A;    | org.antlr.v4.runtime.misc.ParseCancellationException  |
