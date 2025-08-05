# justfile for jlox project

# Run jlox in REPL mode
run:
    mvn exec:java -Dexec.mainClass="com.craftinginterpreters.lox.Lox"

# Run tests
test:
    mvn test

# Run AST generator
gen:
    mvn exec:java -Dexec.mainClass="com.craftinginterpreters.tool.GenerateAst" -Dexec.args="${PWD}/src/main/java/com/craftinginterpreters/lox"