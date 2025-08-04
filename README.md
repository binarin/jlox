# jlox Interpreter

jlox is an interpreter for the Lox programming language, as described in the book "Crafting Interpreters" by Robert Nystrom.

## Build Instructions

### Prerequisites
- Java Development Kit (JDK) 8 or higher
- Maven 3.6 or higher

### Building with Maven

The project uses Maven as its build system. Here are the basic commands:

```bash
# Clean and compile
mvn clean compile

# Run tests
mvn test

# Package into a JAR file
mvn package

# Run the REPL
mvn exec:java

# Run a Lox script
mvn exec:java -Dexec.args="path/to/script.lox"
```

You can also use the provided build script:

```bash
# Make the script executable (if needed)
chmod +x build.sh

# Run the build script
./build.sh
```

### Running the JAR directly

After packaging, you can run the JAR file directly:

```bash
# REPL Mode
java -jar target/jlox-1.0-SNAPSHOT.jar

# Script Mode
java -jar target/jlox-1.0-SNAPSHOT.jar path/to/script.lox
```

## Project Structure

The project follows the standard Maven directory structure:

- `src/main/java/` - Source code
- `src/test/java/` - Test code

## Testing

The project uses JUnit for testing. Tests can be run with:

```bash
mvn test
```

## Development Guidelines

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