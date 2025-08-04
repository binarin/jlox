package com.craftinginterpreters.lox;

import org.junit.Before;
import org.junit.Test;
import org.junit.After;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.craftinginterpreters.lox.TokenType.*;

/**
 * A test class for the Lox Scanner that compares actual tokens with expected tokens
 */
public class ScannerTest {
    private int testsPassed = 0;
    private int testsFailed = 0;

    @Before
    public void setUp() {
        testsPassed = 0;
        testsFailed = 0;
    }

    @After
    public void tearDown() {
        System.out.println("\nTest Summary:");
        System.out.println("Tests Passed: " + testsPassed);
        System.out.println("Tests Failed: " + testsFailed);
    }

    @Test
    public void testSimpleExpression() {
        testScanner("1 + 2", Arrays.asList(
            new Token(NUMBER, "1", 1.0, 1),
            new Token(PLUS, "+", null, 1),
            new Token(NUMBER, "2", 2.0, 1),
            new Token(EOF, "", null, 1)
        ));
    }
    
    @Test
    public void testString() {
        testScanner("\"Hello, world!\"", Arrays.asList(
            new Token(STRING, "\"Hello, world!\"", "Hello, world!", 1),
            new Token(EOF, "", null, 1)
        ));
    }
    
    @Test
    public void testKeywords() {
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
    public void testMultilineLexing() {
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
    public void testMultilineString() {
        testScanner("\"Hello,\nworld!\"", Arrays.asList(
            new Token(STRING, "\"Hello,\nworld!\"", "Hello,\nworld!", 2),
            new Token(EOF, "", null, 2)
        ));
    }
    
    @Test
    public void testSingleLineComments() {
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
    public void testSingleLineCommentAtEndOfFile() {
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
    public void testMultilineComments() {
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
    public void testMultilineCommentsInMiddleOfCode() {
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
    public void testNestedLookingComments() {
        testScanner("/* Comment with * and / characters */var a = 1;", Arrays.asList(
            new Token(VAR, "var", null, 1),
            new Token(IDENTIFIER, "a", null, 1),
            new Token(EQUAL, "=", null, 1),
            new Token(NUMBER, "1", 1.0, 1),
            new Token(SEMICOLON, ";", null, 1),
            new Token(EOF, "", null, 1)
        ));
    }
    
    private void testScanner(String source, List<Token> expectedTokens) {
        System.out.println("Testing: " + source);
        Scanner scanner = new Scanner(source);
        List<Token> actualTokens = scanner.scanTokens();
        
        assertEquals("Token count mismatch", expectedTokens.size(), actualTokens.size());
        
        for (int i = 0; i < expectedTokens.size(); i++) {
            Token expectedToken = expectedTokens.get(i);
            Token actualToken = actualTokens.get(i);
            
            assertEquals("Token type mismatch at position " + i, expectedToken.type, actualToken.type);
            assertEquals("Token lexeme mismatch at position " + i, expectedToken.lexeme, actualToken.lexeme);
            
            // Compare literal (with special handling for null and numeric values)
            if (expectedToken.literal == null) {
                assertNull("Token literal should be null at position " + i, actualToken.literal);
            } else if (expectedToken.literal instanceof Double && actualToken.literal instanceof Double) {
                // Compare double values with a small epsilon for floating-point precision
                double epsilon = 0.0001;
                assertTrue("Token numeric literal mismatch at position " + i, 
                    Math.abs((Double)expectedToken.literal - (Double)actualToken.literal) <= epsilon);
            } else {
                assertEquals("Token literal mismatch at position " + i, expectedToken.literal, actualToken.literal);
            }
            
            assertEquals("Token line number mismatch at position " + i, expectedToken.line, actualToken.line);
        }
        
        // Track test results for summary
        testsPassed++;
        System.out.println("✓ Test PASSED");
        System.out.println("------------------------------");
    }
}