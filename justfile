# justfile for jlox project

# Run jlox in REPL mode
run:
    mvn exec:java -Dexec.mainClass="com.craftinginterpreters.lox.Lox"

# Run tests
test:
    mvn test

# Run AST generator
gen:
    java ./src/main/java/com/craftinginterpreters/tool/GenerateAst.java "${PWD}/src/main/java/com/craftinginterpreters/lox"