This document is a work in progress, its content may change at any time.

# Variables and consants
 * Variables names must match ```[a-z][a-zA-Z0-9_$]*```
 * Constant names must match ```[A-Z0-9_$]+```

Both variables and constants must be declared using the following syntax: ```def varname (: Type)? (= expression)? ;```

I other words, in one of the following ways:

    def a;
    def a : Integer;
    def a = 1;
    def a : Integer = b + 1;

# Conditional statements
Statements can be suffixed with with either ```if expression``` or ```unless expression```, such as in

    // a = min(a, 5)
    a = 5 if a < 5;

# Repeatable statements
Not implemented, yet.
Much like conditional statements, a statement can be suffixed with ```while expression``` to repeat it as long as needed:

    // make sure a > 0
    a = a * 10 while a < 0;

# Object creation
## Constructor / Destructors
    class A {
        // constructor
        +A() {
        }
        
        // destructor
        -A() {
        }
    }

## Creation
    instance = (new)? Class(parameters);


# Method call
## non-nullable expression
    expression . method()  // -> do something, return something; invalid if expression can be null

## nullable expression
    expression ?. method() // -> do nothing, return null if expression is null; otherwise, same as . 

## nullable arguments
    Integer(1) + Integer   // -> ?

# Composition over inheritance
Thorium does not feature class inheritance. The only tool available to extends an object's functionality is to "wrap" it
in another one.

    interface Walker {
        public walk();
    }
    
    class Mammal implements Walker {
        public walk() {
            // do something
        }
    }
    
    class Dog implements Walker {
        Walker walker : delegate all;
        
        +Dog() {
            this.walker = Walker();
        }
    }

## Delegate syntax
If two delegate implements the same method, an error occurs.

    // delegates all Walker's public methods to walker
    Walker walker : delegate all;
    
    // delegates all Walker's public methods but a to walker
    Walker walker : delegate all but a;
    
    // delegates all Walker's public methods but a and b to walker
    Walker walker : delegate all but a, b;          
    
    // delegates only Walker's public methods a to walker
    Walker walker : delegate a;
    
    // delegates only Walker's public methods a and b to walker
    Walker walker : delegate a, b;

# Traits
to be defined

# Getters / Setters
    get:public set:protected String a;
    public String b;
    get:public String c;

# Visibility

x         | class | subclasses | package | world 
:-------- | :---: | :--------: | :-----: | :---: 
public    | Y     | Y          | Y       | Y     
package   | Y     | Y          | Y       | N     
protected | Y     | Y          | N       | N     
private   | Y     | N          | N       | N     

