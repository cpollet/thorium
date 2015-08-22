# Method call
## non-nullable expression
expression . method()  -> do something, return something

## nullable expression
expression ?. method() -> do nothing, return null

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
