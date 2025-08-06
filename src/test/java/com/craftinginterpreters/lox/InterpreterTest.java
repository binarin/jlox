package com.craftinginterpreters.lox;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.assertj.core.api.Assertions;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

/**
 * Test class for the Interpreter.
 * Tests the evaluation of expressions by parsing text and interpreting the resulting expressions.
 */
public class InterpreterTest {
    private Interpreter interpreter;
    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    void setUp() {
        interpreter = new Interpreter();
        // Redirect System.out to capture interpreter output
        System.setOut(new PrintStream(outputStream));
    }

    @org.junit.jupiter.api.AfterEach
    void tearDown() {
        // Restore original System.out
        System.setOut(originalOut);
    }

    /**
     * Helper method to parse a source string into an expression.
     * 
     * @param source the source code to parse
     * @return the parsed expression
     */
    private Expr parseExpression(String source) {
        Scanner scanner = new Scanner(source);
        List<Token> tokens = scanner.scanTokens();
        Parser parser = new Parser(tokens);
        return parser.parse();
    }

    /**
     * Helper method to evaluate an expression and return the output.
     * 
     * @param source the source code to evaluate
     * @return the string output from the interpreter
     */
    private String evaluateExpression(String source) {
        outputStream.reset(); // Clear previous output
        Expr expression = parseExpression(source);
        interpreter.interpret(expression);
        return outputStream.toString().trim(); // Get output and trim whitespace
    }

    @Test
    @DisplayName("Test interpreting literal expressions")
    void testLiteralExpressions() {
        // Test number literal
        Assertions.assertThat(evaluateExpression("123")).isEqualTo("123");
        
        // Test string literal
        Assertions.assertThat(evaluateExpression("\"hello\"")).isEqualTo("hello");
        
        // Test boolean literals
        Assertions.assertThat(evaluateExpression("true")).isEqualTo("true");
        Assertions.assertThat(evaluateExpression("false")).isEqualTo("false");
        
        // Test nil literal
        Assertions.assertThat(evaluateExpression("nil")).isEqualTo("nil");
    }

    @Test
    @DisplayName("Test interpreting unary expressions")
    void testUnaryExpressions() {
        // Test negation
        Assertions.assertThat(evaluateExpression("-123")).isEqualTo("-123");
        Assertions.assertThat(evaluateExpression("-0")).isEqualTo("-0"); // Interpreter returns "-0" for negating zero
        
        // Test logical not
        Assertions.assertThat(evaluateExpression("!true")).isEqualTo("false");
        Assertions.assertThat(evaluateExpression("!false")).isEqualTo("true");
        Assertions.assertThat(evaluateExpression("!!true")).isEqualTo("true");
    }

    @Test
    @DisplayName("Test interpreting binary arithmetic expressions")
    void testBinaryArithmeticExpressions() {
        // Test addition
        Assertions.assertThat(evaluateExpression("1 + 2")).isEqualTo("3");
        
        // Test subtraction
        Assertions.assertThat(evaluateExpression("5 - 3")).isEqualTo("2");
        
        // Test multiplication
        Assertions.assertThat(evaluateExpression("2 * 3")).isEqualTo("6");
        
        // Test division
        Assertions.assertThat(evaluateExpression("8 / 2")).isEqualTo("4");
        
        // Test complex arithmetic expression
        Assertions.assertThat(evaluateExpression("2 * (3 + 4) - 1")).isEqualTo("13");
    }

    @Test
    @DisplayName("Test interpreting string concatenation")
    void testStringConcatenation() {
        Assertions.assertThat(evaluateExpression("\"hello\" + \" \" + \"world\"")).isEqualTo("hello world");
    }

    @Test
    @DisplayName("Test interpreting comparison expressions")
    void testComparisonExpressions() {
        // Test greater than
        Assertions.assertThat(evaluateExpression("5 > 3")).isEqualTo("true");
        Assertions.assertThat(evaluateExpression("3 > 5")).isEqualTo("false");
        
        // Test greater than or equal
        Assertions.assertThat(evaluateExpression("5 >= 5")).isEqualTo("true");
        Assertions.assertThat(evaluateExpression("3 >= 5")).isEqualTo("false");
        
        // Test less than
        Assertions.assertThat(evaluateExpression("3 < 5")).isEqualTo("true");
        Assertions.assertThat(evaluateExpression("5 < 3")).isEqualTo("false");
        
        // Test less than or equal
        Assertions.assertThat(evaluateExpression("5 <= 5")).isEqualTo("true");
        Assertions.assertThat(evaluateExpression("5 <= 3")).isEqualTo("false");
    }

    @Test
    @DisplayName("Test interpreting equality expressions")
    void testEqualityExpressions() {
        // Test equality
        Assertions.assertThat(evaluateExpression("1 == 1")).isEqualTo("true");
        Assertions.assertThat(evaluateExpression("1 == 2")).isEqualTo("false");
        Assertions.assertThat(evaluateExpression("\"hello\" == \"hello\"")).isEqualTo("true");
        Assertions.assertThat(evaluateExpression("\"hello\" == \"world\"")).isEqualTo("false");
        
        // Test inequality
        Assertions.assertThat(evaluateExpression("1 != 2")).isEqualTo("true");
        Assertions.assertThat(evaluateExpression("1 != 1")).isEqualTo("false");
    }

    @Test
    @DisplayName("Test interpreting grouping expressions")
    void testGroupingExpressions() {
        Assertions.assertThat(evaluateExpression("(1 + 2) * 3")).isEqualTo("9");
        Assertions.assertThat(evaluateExpression("1 + (2 * 3)")).isEqualTo("7");
        Assertions.assertThat(evaluateExpression("(1 + 2) * (3 + 4)")).isEqualTo("21");
    }

    @Test
    @DisplayName("Test interpreting ternary expressions")
    void testTernaryExpressions() {
        Assertions.assertThat(evaluateExpression("true ? 1 : 2")).isEqualTo("1");
        Assertions.assertThat(evaluateExpression("false ? 1 : 2")).isEqualTo("2");
        Assertions.assertThat(evaluateExpression("1 < 2 ? \"less\" : \"greater\"")).isEqualTo("less");
        Assertions.assertThat(evaluateExpression("1 > 2 ? \"less\" : \"greater\"")).isEqualTo("greater");
        
        Assertions.assertThat(evaluateExpression("true ? (false ? 1 : 2) : 3")).isEqualTo("2");
    }

    @Test
    @DisplayName("Test type errors")
    void testTypeErrors() {
        // For testing runtime errors, we need to check if Lox.hadRuntimeError is set
        // We'll use a custom method that resets the error flag before each test
        
        // Test adding a number to a string (should cause a runtime error)
        boolean hasError = hasRuntimeError("1 + \"hello\"");
        Assertions.assertThat(hasError).isTrue();
        
        // Test subtracting strings (should cause a runtime error)
        hasError = hasRuntimeError("\"hello\" - \"world\"");
        Assertions.assertThat(hasError).isTrue();
        
        // The current implementation doesn't throw a runtime error for division by zero
        // Instead, it follows IEEE 754 floating-point behavior (returns Infinity)
        Assertions.assertThat(evaluateExpression("1 / 0")).isEqualTo("Infinity");
        
        // Test using comparison operators with strings (should cause a runtime error)
        hasError = hasRuntimeError("\"hello\" > \"world\"");
        Assertions.assertThat(hasError).isTrue();
    }
    
    /**
     * Helper method to check if evaluating an expression causes a runtime error.
     * 
     * @param source the source code to evaluate
     * @return true if a runtime error occurred, false otherwise
     */
    private boolean hasRuntimeError(String source) {
        // Create a field to track runtime errors
        try {
            // Use reflection to access and reset the private static field hadRuntimeError in Lox
            java.lang.reflect.Field hadRuntimeErrorField = Lox.class.getDeclaredField("hadRuntimeError");
            hadRuntimeErrorField.setAccessible(true);
            hadRuntimeErrorField.set(null, false);
            
            // Evaluate the expression
            Expr expression = parseExpression(source);
            interpreter.interpret(expression);
            
            // Check if a runtime error occurred
            return (boolean) hadRuntimeErrorField.get(null);
        } catch (Exception e) {
            // If we can't access the field, assume no error
            return false;
        }
    }
}