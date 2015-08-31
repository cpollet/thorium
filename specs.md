# Variables
Should be declared as:
    var name (:type)? (=value)? ;

# Object creation
instance = (new)? Class(parameters);

# Method call
## non-nullable expression
expression . method()  -> do something, return something; invalid if expression can be null

## nullable expression
expression ?. method() -> do nothing, return null if expression is null; otherwise, same as . 

## nullable arguments
Integer(1) + Integer   -> ?

# Getters / Setters
    get:public set:protected String a;
    public String b;
    get:public String c;

# Visibility
|           | class | subclasses | package | world |  
| public    | Y     | Y          | Y       | Y     |
| package   | Y     | Y          | Y       | N     |
| protected | Y     | Y          | N       | N     |
| private   | Y     | N          | N       | N     |
