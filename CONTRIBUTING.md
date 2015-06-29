# How to Contribute to RxRecyclerView

1. Fork\!
2. Make your changes, test your code\!
3. Pull Request\!
4. Code Review / Changes\!
5. Profit\!

That's basically all there is to it.  If Contributing becomes a regular thing, I'll add a checkstyle.
Basically, here's some things to watch out for:

1. Do not use mX variable naming
2. Do stick to Java best practices
3. Do stick a space between operators, braces, etc.
4. Keep it small\! That means no Guava...
5. Please, please, please test your code.  At least make certain that the Espresso tests still pass.

Things I'd like see contributed are:

1. Simple scenario Adapter classes
2. More Activities and Espresso tests for them

As always, any contributions are awesome, and I'm interested in hearing feedback from users.

## Code Guidelines:

```java

// If this shouldn't be extensible, finalize it.
public class X extends Y {
    
    // Static final Variables in all caps
    private static final int VARIABLE_NAME = 0;
    
    // ENUMs are ALL CAPS
    public enum TYPE {
        FIRST, SECOND, THIRD;
    }
    
    // None of this m____ stuff.  Use is___ before booleans
    private final int otherVar;
    private boolean isFalse = true;
    
    public X() {
        otherVar = 0;
    }
    
    // If we're in an extendible class, finalize methods as necessary.
    public final void x() {
        // be minimalistic
        return x == 0 ? x : y;
    }
    
    // Use proper spacing between braces and operators.
    public void y() {
        if (x == 0) {
            doZ();
        } else {
            // Note else goes on same line as if
            doW();
        }
    }
}

```