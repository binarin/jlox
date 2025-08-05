package com.craftinginterpreters.lox;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.craftinginterpreters.lox.TokenType.*;

/**
 * Scanner class that performs lexical analysis on Lox source code.
 * Converts source text into tokens for the parser.
 */
class Scanner {
    private final String source;
    private final List<Token> tokens = new ArrayList<>();
    private int start = 0;
    private int current = 0;
    private int line = 1;

    // Map of Lox keywords to their corresponding token types
    private static final Map<String, TokenType> keywords = Map.ofEntries(
        Map.entry("and", AND),
        Map.entry("class", CLASS),
        Map.entry("else", ELSE),
        Map.entry("false", FALSE),
        Map.entry("for", FOR),
        Map.entry("fun", FUN),
        Map.entry("if", IF),
        Map.entry("nil", NIL),
        Map.entry("or", OR),
        Map.entry("print", PRINT),
        Map.entry("return", RETURN),
        Map.entry("super", SUPER),
        Map.entry("this", THIS),
        Map.entry("true", TRUE),
        Map.entry("var", VAR),
        Map.entry("while", WHILE)
    );

    /**
     * Creates a new Scanner for the given source code.
     *
     * @param source the source code to scan
     */
    Scanner(String source) {
        this.source = source;
    }

    /**
     * Scans the source code and returns a list of tokens.
     *
     * @return the list of tokens
     */
    List<Token> scanTokens() {
        while (!isAtEnd()) {
            // We are at the beginning of the next lexeme
            start = current;
            scanToken();
        }
        tokens.add(new Token(EOF, "", null, line));
        return tokens;
    }

    /**
     * Checks if we've reached the end of the source code.
     *
     * @return true if we're at the end of the source code, false otherwise
     */
    private boolean isAtEnd() {
        return current >= source.length();
    }

    /**
     * Scans a single token from the source code.
     */
    private void scanToken() {
        var c = advance();
        
        switch (c) {
            // Single-character tokens
            case '(' -> addToken(LEFT_PAREN);
            case ')' -> addToken(RIGHT_PAREN);
            case '{' -> addToken(LEFT_BRACE);
            case '}' -> addToken(RIGHT_BRACE);
            case ',' -> addToken(COMMA);
            case '.' -> addToken(DOT);
            case '-' -> addToken(MINUS);
            case '+' -> addToken(PLUS);
            case ';' -> addToken(SEMICOLON);
            case '*' -> addToken(STAR);
            
            // One or two character tokens
            case '!' -> addToken(match('=') ? BANG_EQUAL : BANG);
            case '=' -> addToken(match('=') ? EQUAL_EQUAL : EQUAL);
            case '<' -> addToken(match('=') ? LESS_EQUAL : LESS);
            case '>' -> addToken(match('=') ? GREATER_EQUAL : GREATER);
            
            // Handle comments and division operator
            case '/' -> {
                if (match('/')) {
                    // Single-line comment: consume until end of line
                    while (peek() != '\n' && !isAtEnd()) {
                        advance();
                    }
                } else if (match('*')) {
                    // Multi-line comment: consume until closing */
                    while (!isAtEnd()) {
                        if (peek() == '*' && peekNext() == '/') {
                            // Found the closing */
                            advance(); // consume *
                            advance(); // consume /
                            break;
                        } else if (peek() == '\n') {
                            line++;
                        }
                        advance();
                    }
                } else {
                    addToken(SLASH);
                }
            }
            
            // Whitespace
            case ' ', '\r', '\t' -> { /* Ignore whitespace */ }
            case '\n' -> line++;
            
            // String literals
            case '"' -> string();
            
            // Handle other characters
            default -> {
                if (isDigit(c)) {
                    number();
                } else if (isAlpha(c)) {
                    identifier();
                } else {
                    Lox.report(line, "scanner default case", "Unexpected character.");
                }
            }
        }
    }

    /**
     * Processes an identifier token.
     * Identifiers are variable names, function names, etc.
     */
    private void identifier() {
        while (isAlphaNumeric(peek())) {
            advance();
        }
        
        var text = source.substring(start, current);
        var type = keywords.getOrDefault(text, IDENTIFIER);
        addToken(type);
    }

    /**
     * Checks if a character is alphanumeric.
     *
     * @param c the character to check
     * @return true if the character is alphanumeric, false otherwise
     */
    private boolean isAlphaNumeric(char c) {
        return isAlpha(c) || isDigit(c);
    }

    /**
     * Checks if a character is alphabetic or underscore.
     *
     * @param c the character to check
     * @return true if the character is alphabetic or underscore, false otherwise
     */
    private boolean isAlpha(char c) {
        return (c >= 'a' && c <= 'z') ||
               (c >= 'A' && c <= 'Z') ||
               c == '_';
    }

    /**
     * Checks if a character is a digit.
     *
     * @param c the character to check
     * @return true if the character is a digit, false otherwise
     */
    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    /**
     * Processes a number token.
     * Numbers can be integers or floating-point.
     */
    private void number() {
        // Consume integer part
        while (isDigit(peek())) {
            advance();
        }

        // Look for a fractional part
        if (peek() == '.' && isDigit(peekNext())) {
            // Consume the "."
            advance();
            
            // Consume fractional part
            while (isDigit(peek())) {
                advance();
            }
        }

        // Parse the number and add the token
        var value = Double.parseDouble(source.substring(start, current));
        addToken(NUMBER, value);
    }

    /**
     * Looks ahead two characters in the source code.
     *
     * @return the character two positions ahead, or '\0' if at the end
     */
    private char peekNext() {
        return (current + 1 >= source.length()) ? '\0' : source.charAt(current + 1);
    }

    /**
     * Processes a string token.
     * Strings are enclosed in double quotes.
     */
    private void string() {
        // Consume characters until closing quote or end of file
        while (peek() != '"' && !isAtEnd()) {
            if (peek() == '\n') {
                line++; // Support multi-line strings
            }
            advance();
        }

        // Check for unterminated string
        if (isAtEnd()) {
            Lox.report(line, "string scanner", "Unterminated string.");
            return;
        }

        // Consume the closing quote
        advance();

        // Extract the string value (without the quotes)
        var value = source.substring(start + 1, current - 1);
        addToken(STRING, value);
    }

    /**
     * Looks ahead one character in the source code without consuming it.
     *
     * @return the next character, or '\0' if at the end
     */
    private char peek() {
        return isAtEnd() ? '\0' : source.charAt(current);
    }

    /**
     * Checks if the next character matches the expected character.
     * If it does, consumes the character and returns true.
     *
     * @param expected the expected character
     * @return true if the next character matches, false otherwise
     */
    private boolean match(char expected) {
        if (isAtEnd() || source.charAt(current) != expected) {
            return false;
        }
        
        current++;
        return true;
    }

    /**
     * Consumes the current character and returns it.
     *
     * @return the current character
     */
    private char advance() {
        return source.charAt(current++);
    }

    /**
     * Adds a token with no literal value.
     *
     * @param type the token type
     */
    private void addToken(TokenType type) {
        addToken(type, null);
    }

    /**
     * Adds a token with a literal value.
     *
     * @param type the token type
     * @param literal the literal value
     */
    private void addToken(TokenType type, Object literal) {
        var text = source.substring(start, current);
        tokens.add(new Token(type, text, literal, line));
    }
}
