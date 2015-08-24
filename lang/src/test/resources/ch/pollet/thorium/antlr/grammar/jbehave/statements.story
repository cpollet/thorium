

Scenario: statements with exceptions
Given a list of statements <statements>
Given exception expected
When being executed
Then the exception <exception> is thrown with message <message>

Examples:
| statements                    | exception                                                             | message                                           |
|-- a = 1; b = 1.0; a = b;        | ch.pollet.thorium.semantic.exception.InvalidTypeException             | Float is no assignable to Integer                 |
|-- a; b = a;                     | ch.pollet.thorium.semantic.exception.InvalidAssignmentSourceException | Cannot assign from Symbol(a: Void(Void))          |
| A = 1; A = 2;                 | ch.pollet.thorium.semantic.exception.InvalidAssignmentTargetException | Cannot assign Integer(2) to Symbol(A: Integer(1)) |
|-- a = 1; b = 1.0; c = a = b;    | ch.pollet.thorium.semantic.exception.InvalidTypeException             | Float is no assignable to Integer                 |
|-- a = 1; { b = a + 1; }; c = b; | ch.pollet.thorium.semantic.exception.InvalidAssignmentSourceException | Cannot assign from Symbol(b: Void(Void))          |