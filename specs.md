This document is a work in progress, its content may change at any time.

# Variables and consants
 * Variables names must match ```[a-z][a-zA-Z0-9_$]*```
 * Constant names must match ```[A-Z0-9_$]+```

Both variables and constants must be declared using the following syntax: ```def (Type)? varname (= expression)? ;```

I other words, in one of the following ways:

    def a;
    def Integer a;
    def a = 1;
    def Integer a = b + 1;

# Conditional statements
Statements can be suffixed with with either ```if expression``` or ```unless expression```, such as in

    // a = min(a, 5)
    a = 5 if a < 5;
    // a = max(a, 5)
    a = 5 unless a < 5

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
    instance = Class.new(parameters);


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
        Walker walker : delegate;
        
        +Dog() {
            this.walker = Mammal();
        }
    }

## Delegate syntax
If two delegate implements the same method, an error occurs.

    // delegates all Walker's public methods to walker
    Walker walker : delegate;
    
    // delegates all Walker's public methods but a to walker
    Walker walker : delegate but a;
    
    // delegates all Walker's public methods but a and b to walker
    Walker walker : delegate but a, b;          
    
    // delegates only Walker's public methods a to walker
    Walker walker : delegate a;
    
    // delegates only Walker's public methods a and b to walker
    Walker walker : delegate a, b;

# Traits
to be defined

# Getters / Setters
    // generate public getter, protected setter
    get:public set:protected String a;
    
    // generate public getter, public setter
    public String b;
    
    // generate public getter, private setter
    get:public String c;

# Visibility

x         | class | subclasses | package | world 
:-------- | :---: | :--------: | :-----: | :---: 
          | Y     | N          | N       | N
public    | Y     | Y          | Y       | Y     
package   | Y     | Y          | Y       | N     
protected | Y     | Y          | N       | N     
private   | Y     | N          | N       | N     

