# jlox Development Guidelines

This document provides guidelines for developing and working with the jlox interpreter.

## Build/Configuration Instructions

### Prerequisites
- Java Development Kit (JDK) 8 or higher

### Building the Project
The project uses a simple directory structure and can be built using the Java compiler directly:

```bash
# Compile all Java files
javac -d out src/com/craftinginterpreters/lox/*.java
```

### Running the Interpreter
After compilation, you can run the interpreter in two modes:

1. **REPL Mode** (interactive prompt):
```bash
java -cp out com.craftinginterpreters.lox.Lox
```

2. **Script Mode** (run a Lox script file):
```bash
java -cp out com.craftinginterpreters.lox.Lox path/to/script.lox
```

## Testing Information

### Creating and Running Tests

Since jlox is a language interpreter, there are two main approaches to testing:

1. **Testing the interpreter components directly** (unit testing)
2. **Testing with Lox scripts** (integration/functional testing)

### Unit Testing

To test individual components of the interpreter (like the Scanner, Parser, etc.), create test classes in the `com.craftinginterpreters.lox` package:

```java
// Example: src/com/craftinginterpreters/lox/ScannerTest.java
package com.craftinginterpreters.lox;

import java.util.List;

public class ScannerTest {
    public static void main(String[] args) {
        // Test cases
        testScanner("1 + 2");
        testScanner("\"Hello, world!\"");
        testScanner("if (true) { print \"yes\"; } else { print \"no\"; }");
    }
    
    private static void testScanner(String source) {
        System.out.println("Testing: " + source);
        Scanner scanner = new Scanner(source);
        List<Token> tokens = scanner.scanTokens();
        
        for (Token token : tokens) {
            System.out.println(token);
        }
        System.out.println("------------------------------");
    }
}
```

Compile and run the test:

```bash
javac -d out src/com/craftinginterpreters/lox/*.java
java -cp out com.craftinginterpreters.lox.ScannerTest
```

### Functional Testing

Create Lox script files to test language features:

```lox
// Example: test.lox
// Variable declaration and assignment
var a = 1;
var b = 2;
print a + b;

// String concatenation
var greeting = "Hello";
var name = "World";
print greeting + " " + name;

// Conditional statement
if (a < b) {
  print "a is less than b";
} else {
  print "a is not less than b";
}
```

Run the test script:

```bash
java -cp out com.craftinginterpreters.lox.Lox test.lox
```

### Test Organization

For larger test suites, consider organizing tests into directories:

- `test/unit/` - For Java unit tests
- `test/functional/` - For Lox script tests

You can create a shell script to run all tests:

```bash
#!/bin/bash
# run_tests.sh

# Compile all Java files
javac -d out src/com/craftinginterpreters/lox/*.java

# Run unit tests
for test in src/com/craftinginterpreters/lox/*Test.java; do
  class_name=$(basename $test .java)
  echo "Running $class_name..."
  java -cp out com.craftinginterpreters.lox.$class_name
done

# Run functional tests
for test in test/functional/*.lox; do
  echo "Running $test..."
  java -cp out com.craftinginterpreters.lox.Lox $test
done
```

## Development Guidelines

### Project Structure

The jlox interpreter follows a simple structure:

- `src/com/craftinginterpreters/lox/` - Contains all source files
  - `Lox.java` - Main entry point for the interpreter
  - `Scanner.java` - Lexical analyzer that converts source code to tokens
  - `Token.java` - Represents a token in the Lox language
  - `TokenType.java` - Enum of all token types

### Code Style

1. **Naming Conventions**
   - Classes: PascalCase (e.g., `Scanner`, `Token`)
   - Methods and variables: camelCase (e.g., `scanTokens`, `isDigit`)
   - Constants: UPPER_SNAKE_CASE (e.g., `LEFT_PAREN`, `RIGHT_BRACE`)

2. **Formatting**
   - Use 4-space indentation
   - Place opening braces on the same line as the declaration
   - Use a space after keywords and before opening parentheses
   - Use a space around operators

3. **Documentation**
   - Add comments for complex logic
   - Document public methods with clear descriptions of parameters and return values

### Implementation Notes

1. **Error Handling**
   - Use the `Lox.error()` method to report errors
   - Continue parsing after errors when possible to report multiple errors

2. **Scanner Implementation**
   - The scanner uses a simple one-character lookahead approach
   - Keywords are stored in a static map for efficient lookup
   - Line numbers are tracked for error reporting

3. **Future Development**
   - The current implementation only includes the scanner (lexical analysis)
   - Future components to implement:
     - Parser: Converts tokens into an abstract syntax tree
     - Interpreter: Executes the abstract syntax tree
     - Environment: Manages variable bindings
     - Functions and Classes: Implements function calls and object-oriented features