package com.craftinginterpreters.lox;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.assertj.core.api.Assertions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.craftinginterpreters.lox.TokenType.*;

/**
 * A test class for the Lox Scanner that compares actual tokens with expected tokens.
 * Uses JUnit 5 (Jupiter) for testing.
 */
class ScannerTest {
    @BeforeEach
    void setUp() {
        // Setup code if needed in the future
    }

    @AfterEach
    void tearDown() {
        // Teardown code if needed in the future
    }

    @Test
    @DisplayName("Test scanning of a simple expression")
    void testSimpleExpression() {
        testScanner("1 + 2", Arrays.asList(
            new Token(NUMBER, "1", 1.0, 1),
            new Token(PLUS, "+", null, 1),
            new Token(NUMBER, "2", 2.0, 1),
            new Token(EOF, "", null, 1)
        ));
    }
    
    @Test
    @DisplayName("Test scanning of string literals")
    void testString() {
        testScanner("\"Hello, world!\"", Arrays.asList(
            new Token(STRING, "\"Hello, world!\"", "Hello, world!", 1),
            new Token(EOF, "", null, 1)
        ));
    }
    
    @Test
    @DisplayName("Test scanning of Lox keywords")
    void testKeywords() {
        testScanner("if (true) { print \"yes\"; } else { print \"no\"; }", Arrays.asList(
            new Token(IF, "if", null, 1),
            new Token(LEFT_PAREN, "(", null, 1),
            new Token(TRUE, "true", null, 1),
            new Token(RIGHT_PAREN, ")", null, 1),
            new Token(LEFT_BRACE, "{", null, 1),
            new Token(PRINT, "print", null, 1),
            new Token(STRING, "\"yes\"", "yes", 1),
            new Token(SEMICOLON, ";", null, 1),
            new Token(RIGHT_BRACE, "}", null, 1),
            new Token(ELSE, "else", null, 1),
            new Token(LEFT_BRACE, "{", null, 1),
            new Token(PRINT, "print", null, 1),
            new Token(STRING, "\"no\"", "no", 1),
            new Token(SEMICOLON, ";", null, 1),
            new Token(RIGHT_BRACE, "}", null, 1),
            new Token(EOF, "", null, 1)
        ));
    }
    
    @Test
    @DisplayName("Test scanning of multiline code")
    void testMultilineLexing() {
        testScanner("var a = 1;\nvar b = 2;", Arrays.asList(
            new Token(VAR, "var", null, 1),
            new Token(IDENTIFIER, "a", null, 1),
            new Token(EQUAL, "=", null, 1),
            new Token(NUMBER, "1", 1.0, 1),
            new Token(SEMICOLON, ";", null, 1),
            new Token(VAR, "var", null, 2),
            new Token(IDENTIFIER, "b", null, 2),
            new Token(EQUAL, "=", null, 2),
            new Token(NUMBER, "2", 2.0, 2),
            new Token(SEMICOLON, ";", null, 2),
            new Token(EOF, "", null, 2)
        ));
    }
    
    @Test
    @DisplayName("Test scanning of multiline strings")
    void testMultilineString() {
        testScanner("\"Hello,\nworld!\"", Arrays.asList(
            new Token(STRING, "\"Hello,\nworld!\"", "Hello,\nworld!", 2),
            new Token(EOF, "", null, 2)
        ));
    }
    
    @Test
    @DisplayName("Test scanning with single-line comments")
    void testSingleLineComments() {
        testScanner("var a = 1; // This is a comment\nvar b = 2;", Arrays.asList(
            new Token(VAR, "var", null, 1),
            new Token(IDENTIFIER, "a", null, 1),
            new Token(EQUAL, "=", null, 1),
            new Token(NUMBER, "1", 1.0, 1),
            new Token(SEMICOLON, ";", null, 1),
            new Token(VAR, "var", null, 2),
            new Token(IDENTIFIER, "b", null, 2),
            new Token(EQUAL, "=", null, 2),
            new Token(NUMBER, "2", 2.0, 2),
            new Token(SEMICOLON, ";", null, 2),
            new Token(EOF, "", null, 2)
        ));
    }
    
    @Test
    @DisplayName("Test scanning with single-line comment at end of file")
    void testSingleLineCommentAtEndOfFile() {
        testScanner("var a = 1; // This is a comment", Arrays.asList(
            new Token(VAR, "var", null, 1),
            new Token(IDENTIFIER, "a", null, 1),
            new Token(EQUAL, "=", null, 1),
            new Token(NUMBER, "1", 1.0, 1),
            new Token(SEMICOLON, ";", null, 1),
            new Token(EOF, "", null, 1)
        ));
    }
    
    @Test
    @DisplayName("Test scanning with multiline comments")
    void testMultilineComments() {
        testScanner("/* This is a\nmultiline comment */var a = 1;", Arrays.asList(
            new Token(VAR, "var", null, 2),
            new Token(IDENTIFIER, "a", null, 2),
            new Token(EQUAL, "=", null, 2),
            new Token(NUMBER, "1", 1.0, 2),
            new Token(SEMICOLON, ";", null, 2),
            new Token(EOF, "", null, 2)
        ));
    }
    
    @Test
    @DisplayName("Test scanning with multiline comments in middle of code")
    void testMultilineCommentsInMiddleOfCode() {
        testScanner("var a = 1; /* This is a\nmultiline comment\nwith multiple lines */var b = 2;", Arrays.asList(
            new Token(VAR, "var", null, 1),
            new Token(IDENTIFIER, "a", null, 1),
            new Token(EQUAL, "=", null, 1),
            new Token(NUMBER, "1", 1.0, 1),
            new Token(SEMICOLON, ";", null, 1),
            new Token(VAR, "var", null, 3),
            new Token(IDENTIFIER, "b", null, 3),
            new Token(EQUAL, "=", null, 3),
            new Token(NUMBER, "2", 2.0, 3),
            new Token(SEMICOLON, ";", null, 3),
            new Token(EOF, "", null, 3)
        ));
    }
    
    @Test
    @DisplayName("Test scanning with comments containing * and / characters")
    void testNestedLookingComments() {
        testScanner("/* Comment with * and / characters */var a = 1;", Arrays.asList(
            new Token(VAR, "var", null, 1),
            new Token(IDENTIFIER, "a", null, 1),
            new Token(EQUAL, "=", null, 1),
            new Token(NUMBER, "1", 1.0, 1),
            new Token(SEMICOLON, ";", null, 1),
            new Token(EOF, "", null, 1)
        ));
    }
    
    /**
     * Helper method to test the scanner with a given source code and expected tokens.
     * 
     * @param source the source code to scan
     * @param expectedTokens the expected tokens from scanning the source
     */
    private void testScanner(String source, List<Token> expectedTokens) {
        System.out.println("Testing: " + source);
        var scanner = new Scanner(source);
        var actualTokens = scanner.scanTokens();
        
        Assertions.assertThat(actualTokens)
            .as("Token count mismatch")
            .hasSize(expectedTokens.size());
        
        for (var i = 0; i < expectedTokens.size(); i++) {
            var expectedToken = expectedTokens.get(i);
            var actualToken = actualTokens.get(i);
            
            // Verify token type
            Assertions.assertThat(actualToken.type)
                .as("Token type mismatch at position %d", i)
                .isEqualTo(expectedToken.type);
                
            // Verify lexeme
            Assertions.assertThat(actualToken.lexeme)
                .as("Token lexeme mismatch at position %d", i)
                .isEqualTo(expectedToken.lexeme);
            
            // Verify literal value (with special handling for null and numeric values)
            if (expectedToken.literal == null) {
                Assertions.assertThat(actualToken.literal)
                    .as("Token literal should be null at position %d", i)
                    .isNull();
            } else if (expectedToken.literal instanceof Double && actualToken.literal instanceof Double) {
                // Compare double values with a small epsilon for floating-point precision
                var epsilon = 0.0001;
                Assertions.assertThat((Double)actualToken.literal)
                    .as("Token numeric literal mismatch at position %d", i)
                    .isCloseTo((Double)expectedToken.literal, Assertions.within(epsilon));
            } else {
                Assertions.assertThat(actualToken.literal)
                    .as("Token literal mismatch at position %d", i)
                    .isEqualTo(expectedToken.literal);
            }
            
            // Verify line number
            Assertions.assertThat(actualToken.line)
                .as("Token line number mismatch at position %d", i)
                .isEqualTo(expectedToken.line);
        }
        
        System.out.println("✓ Test PASSED");
        System.out.println("------------------------------");
    }
}