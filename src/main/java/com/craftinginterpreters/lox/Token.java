package com.craftinginterpreters.lox;

/**
 * Represents a token in the Lox language.
 * A token is a unit of source code that the scanner recognizes.
 */
public class Token {
    // Using public final fields for immutability while maintaining compatibility with tests
    public final TokenType type;
    public final String lexeme;
    public final Object literal;
    public final int line;

    /**
     * Creates a new token with the given properties.
     *
     * @param type the type of the token
     * @param lexeme the original text of the token
     * @param literal the literal value of the token (for literals)
     * @param line the line number where the token appears
     */
    public Token(TokenType type, String lexeme, Object literal, int line) {
        this.type = type;
        this.lexeme = lexeme;
        this.literal = literal;
        this.line = line;
    }

    @Override
    public String toString() {
        return "%s %s %s".formatted(type, lexeme, literal);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Token other = (Token) obj;
        return type == other.type &&
               line == other.line &&
               (lexeme == null ? other.lexeme == null : lexeme.equals(other.lexeme)) &&
               (literal == null ? other.literal == null : literal.equals(other.literal));
    }

    @Override
    public int hashCode() {
        int result = type != null ? type.hashCode() : 0;
        result = 31 * result + (lexeme != null ? lexeme.hashCode() : 0);
        result = 31 * result + (literal != null ? literal.hashCode() : 0);
        result = 31 * result + line;
        return result;
    }
}
