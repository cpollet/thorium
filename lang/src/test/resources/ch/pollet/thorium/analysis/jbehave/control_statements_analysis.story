Scenario: control statements analysis that don't exception
Given a list of statements <statements>
When types are attached to nodes
Then no exceptions were thrown

Examples:
| statements                                |
| if (true) { 1; } else if (false) { 1.0; } |

Scenario: control statements analysis that throw exception
Given a list of statements <statements>
And exception expected
When types are attached to nodes
Then the exception <exception> is thrown with message matching <message>

Examples:
| statements                                | exception                                                     | message                                                                       |
| if (1) { 1; }                             | ch.pollet.thorium.analysis.exceptions.InvalidTypeException    | Invalid type found on line [0-9]+:[0-9]+: expected Boolean but got Integer.   |
| if (true) { 1; } else if (1) { 1; }       | ch.pollet.thorium.analysis.exceptions.InvalidTypeException    | Invalid type found on line [0-9]+:[0-9]+: expected Boolean but got Integer.   |