# justfile for jlox project

# Run jlox in REPL mode
run:
    mvn exec:java -Dexec.mainClass="com.craftinginterpreters.lox.Lox"

# Run tests
test:
    mvn test

# Run AST generator
gen:
    mvn compile
    java -cp target/classes com.craftinginterpreters.tool.GenerateAst "${PWD}/src/main/java/com/craftinginterpreters/lox"