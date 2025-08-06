package com.craftinginterpreters.lox;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.assertj.core.api.Assertions;

import java.util.ArrayList;
import java.util.List;

import static com.craftinginterpreters.lox.TokenType.*;

public class ParserTest {

    @Test
    @DisplayName("Test parsing comma operator expression")
    public void testCommaOperatorParsing() {
        // Test case: 1, 2 + (3, 4), "a" == 6
        List<Token> tokens = new ArrayList<>();
        
        // 1
        tokens.add(new Token(NUMBER, "1", 1.0, 1));
        // ,
        tokens.add(new Token(COMMA, ",", null, 1));
        // 2
        tokens.add(new Token(NUMBER, "2", 2.0, 1));
        // +
        tokens.add(new Token(PLUS, "+", null, 1));
        // (
        tokens.add(new Token(LEFT_PAREN, "(", null, 1));
        // 3
        tokens.add(new Token(NUMBER, "3", 3.0, 1));
        // ,
        tokens.add(new Token(COMMA, ",", null, 1));
        // 4
        tokens.add(new Token(NUMBER, "4", 4.0, 1));
        // )
        tokens.add(new Token(RIGHT_PAREN, ")", null, 1));
        // ,
        tokens.add(new Token(COMMA, ",", null, 1));
        // "a"
        tokens.add(new Token(STRING, "\"a\"", "a", 1));
        // ==
        tokens.add(new Token(EQUAL_EQUAL, "==", null, 1));
        // 6
        tokens.add(new Token(NUMBER, "6", 6.0, 1));
        // EOF
        tokens.add(new Token(EOF, "", null, 1));
        
        // Parse the tokens
        Parser parser = new Parser(tokens);
        Expr expr = parser.parse();
        
        // Convert the AST to string representation
        String result = new AstPrinter().print(expr);
        
        // The expected AST structure for "1, 2 + (3, 4), "a" == 6" based on actual output
        String expected = "(, (, 1.0 (+ 2.0 (group (, 3.0 4.0)))) (== a 6.0))";
        
        // Assert the expected output
        Assertions.assertThat(result).isEqualTo(expected);
    }
}