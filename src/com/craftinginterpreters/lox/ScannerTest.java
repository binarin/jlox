package com.craftinginterpreters.lox;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.craftinginterpreters.lox.TokenType.*;

/**
 * A test class for the Lox Scanner that compares actual tokens with expected tokens
 */
public class ScannerTest {
    private static int testsPassed = 0;
    private static int testsFailed = 0;

    public static void main(String[] args) {
        // Test simple expression
        testScanner("1 + 2", Arrays.asList(
            new Token(NUMBER, "1", 1.0, 1),
            new Token(PLUS, "+", null, 1),
            new Token(NUMBER, "2", 2.0, 1),
            new Token(EOF, "", null, 1)
        ));
        
        // Test string
        testScanner("\"Hello, world!\"", Arrays.asList(
            new Token(STRING, "\"Hello, world!\"", "Hello, world!", 1),
            new Token(EOF, "", null, 1)
        ));
        
        // Test keywords
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
        
        // Test multiline lexing
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
        
        // Test multiline string
        testScanner("\"Hello,\nworld!\"", Arrays.asList(
            new Token(STRING, "\"Hello,\nworld!\"", "Hello,\nworld!", 2),
            new Token(EOF, "", null, 2)
        ));
        
        // Test single-line comments
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
        
        // Test single-line comment at end of file
        testScanner("var a = 1; // This is a comment", Arrays.asList(
            new Token(VAR, "var", null, 1),
            new Token(IDENTIFIER, "a", null, 1),
            new Token(EQUAL, "=", null, 1),
            new Token(NUMBER, "1", 1.0, 1),
            new Token(SEMICOLON, ";", null, 1),
            new Token(EOF, "", null, 1)
        ));
        
        // Test multiline comments
        testScanner("/* This is a\nmultiline comment */var a = 1;", Arrays.asList(
            new Token(VAR, "var", null, 2),
            new Token(IDENTIFIER, "a", null, 2),
            new Token(EQUAL, "=", null, 2),
            new Token(NUMBER, "1", 1.0, 2),
            new Token(SEMICOLON, ";", null, 2),
            new Token(EOF, "", null, 2)
        ));
        
        // Test multiline comments in the middle of code
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
        
        // Test nested-looking comments (not actually nested, just contains * and / in the content)
        testScanner("/* Comment with * and / characters */var a = 1;", Arrays.asList(
            new Token(VAR, "var", null, 1),
            new Token(IDENTIFIER, "a", null, 1),
            new Token(EQUAL, "=", null, 1),
            new Token(NUMBER, "1", 1.0, 1),
            new Token(SEMICOLON, ";", null, 1),
            new Token(EOF, "", null, 1)
        ));
        
        // Print summary
        System.out.println("\nTest Summary:");
        System.out.println("Tests Passed: " + testsPassed);
        System.out.println("Tests Failed: " + testsFailed);
    }
    
    private static void testScanner(String source, List<Token> expectedTokens) {
        System.out.println("Testing: " + source);
        Scanner scanner = new Scanner(source);
        List<Token> actualTokens = scanner.scanTokens();
        
        boolean testPassed = compareTokens(expectedTokens, actualTokens);
        
        if (testPassed) {
            System.out.println("✓ Test PASSED");
            testsPassed++;
        } else {
            System.out.println("✗ Test FAILED");
            testsFailed++;
        }
        System.out.println("------------------------------");
    }
    
    private static boolean compareTokens(List<Token> expected, List<Token> actual) {
        if (expected.size() != actual.size()) {
            System.out.println("Token count mismatch. Expected: " + expected.size() + 
                               ", Actual: " + actual.size());
            return false;
        }
        
        boolean allMatch = true;
        for (int i = 0; i < expected.size(); i++) {
            Token expectedToken = expected.get(i);
            Token actualToken = actual.get(i);
            
            boolean tokensMatch = compareToken(expectedToken, actualToken);
            if (!tokensMatch) {
                System.out.println("Token mismatch at position " + i + ":");
                System.out.println("  Expected: " + expectedToken);
                System.out.println("  Actual:   " + actualToken);
                allMatch = false;
            }
        }
        
        return allMatch;
    }
    
    private static boolean compareToken(Token expected, Token actual) {
        // Compare token type
        if (expected.type != actual.type) {
            return false;
        }
        
        // Compare lexeme
        if (!expected.lexeme.equals(actual.lexeme)) {
            return false;
        }
        
        // Compare literal (with special handling for null and numeric values)
        if (expected.literal == null && actual.literal != null ||
            expected.literal != null && actual.literal == null) {
            return false;
        }
        
        if (expected.literal != null && actual.literal != null) {
            if (expected.literal instanceof Double && actual.literal instanceof Double) {
                // Compare double values with a small epsilon for floating-point precision
                double epsilon = 0.0001;
                if (Math.abs((Double)expected.literal - (Double)actual.literal) > epsilon) {
                    return false;
                }
            } else if (!expected.literal.equals(actual.literal)) {
                return false;
            }
        }
        
        // Compare line numbers
        if (expected.line != actual.line) {
            return false;
        }
        
        return true;
    }
}