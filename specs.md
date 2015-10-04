This document is a work in progress, its content may change at any time.

# Variables and consants
 * Variables names must match ```[a-z][a-zA-Z0-9_]*```
 * Constant names must match ```[A-Z0-9_]+```

Both variables and constants must be declared using the following syntax: ```def (Type)? varname (= expression)? ;```

I other words, in one of the following ways:

    def a;
    def Integer a;
    def a = 1;
    def Integer a = b + 1;

# Statements
## Conditional statements
Statements can be suffixed with with either ```if expression``` or ```unless expression```, such as in

    // a = min(a, 5)
    a = 5 if a < 5;
    // a = max(a, 5)
    a = 5 unless a < 5;

## Repeatable statements
Much like conditional statements, a statement can be suffixed with ```while expression``` or ```until expression``` to repeat it as long as needed:

    // make sure a > 0
    a = a * 10 while a < 0;
    // make sure a > 0
    a = a * 10 until a >= 0;

# Control structures
Thorium supports standard control structures: ```if```, ```for```, ```while```. ```{``` and ```}``` ae mandatory. General symtax is:

    if (expression) {
        // somehting
    } else if (expression) {
        // something else
    } else {
        // something even different
    }
    
    while (expression) {
        // something
    }
    
    // condition must be evaluated as a Boolean
    for (initialization?; condition?; afterthought?) {
        // something
    }
    
    // not implemented, yet
    for (element in collection) {
    }


# Object creation
## Constructor / Destructors
    class A {
        // constructor
        A() {
        }
    }

## Creation
    instance = (new)? Class(parameters);
    instance = Class.new(parameters);

# Method
    public +(Integer): Integer {
    }
    public +(Integer?): Integer? {
    }
    public +?(Integer?): Integer? {
    }
    
    Integer  +  Integer
    Integer  +  Integer?
    Integer? +? Integer  // same as Integer? .? + Integer
    Integer? +? Integer? // same as Integer? .? + Integer?

# Method call
## non-nullable expression
    expression . method()  // -> do something, return something; invalid if expression can be null

## nullable expression
    expression ?. method() // -> do nothing, return null if expression is null; otherwise, same as . 

## nullable arguments
    Integer(1) + Integer   // -> depends on implementation

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
        
        Dog() {
            this.walker = Mammal();
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

# Class members, getters & setters
General form is ```def (Type)? varname ({ getter/setter })? (= expression)? (: delegate)? ;```

    // generate public getter, protected setter
    def String a { public get; protected set; };
    // is the same as
    def String a {
        public get { return a; }
        protected set(_) { a = _; }
    };
    
    // generate public getter, public setter
    def String b { public };

To access a member from within its class:

    // access directly, without using getter/setter
    this->a = 1;
    b = this->a + 1;
    
    // access through getter/setter
    this.a = 1;
    b = this.a + 1;

# Type intersection
Using ```&``` operator:

    // the instance stored in myVar must implement both A and B
    def A & B myVar;

Using the ```|``` operator:

    // the instance stored in myVar must implement the common methods of A and B:
    def A | B myVar;

# Type aliases
Using ```=>``` operator:

    List<Integer> => ListOfInteger;
    AAndB => A & B;
    AOrB => A | B;

# With keyword
Work in progress...

    with (e : expression) {
        .method1();
        .method2(args);
        other.method(e);
        other.method(.method3());
    }

is a syntactic sugar for

    {
        def e = expression;
        e.method1();
        e.method2(args);
        other.method(e);
        other.method(w.method3());
    }

# Visibility

x         | class | subclasses | package | world 
:-------- | :---: | :--------: | :-----: | :---: 
          | Y     | N          | N       | N     
private   | Y     | N          | N       | N     
protected | Y     | Y          | N       | N     
package   | Y     | Y          | Y       | N     
public    | Y     | Y          | Y       | Y     

