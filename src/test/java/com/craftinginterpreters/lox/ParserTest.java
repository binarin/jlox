package com.craftinginterpreters.lox;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.assertj.core.api.Assertions;

import java.util.ArrayList;
import java.util.List;

import static com.craftinginterpreters.lox.TokenType.*;

public class ParserTest {

    @Test
    @DisplayName("Test parsing ternary operator expression")
    public void testTernaryOperatorParsing() {
        // Test case 1: Basic ternary - "true ? 1 : 2"
        List<Token> tokens = new ArrayList<>();

        // true
        tokens.add(new Token(TRUE, "true", true, 1));
        // ?
        tokens.add(new Token(QUESTION, "?", null, 1));
        // 1
        tokens.add(new Token(NUMBER, "1", 1.0, 1));
        // :
        tokens.add(new Token(COLON, ":", null, 1));
        // 2
        tokens.add(new Token(NUMBER, "2", 2.0, 1));
        // EOF
        tokens.add(new Token(EOF, "", null, 1));

        // Parse the tokens
        Parser parser = new Parser(tokens);
        Expr expr = parser.parse();

        // Convert the AST to string representation
        String result = new AstPrinter().print(expr);

        // The expected AST structure for "true ? 1 : 2"
        String expected = "(?: true 1.0 2.0)";

        // Assert the expected output
        Assertions.assertThat(result).isEqualTo(expected);

        // Test case 2: Precedence - "1 == 2 ? 3 + 4 : 5 * 6"
        // This tests that ternary has lower precedence than equality, addition, and multiplication
        tokens.clear();

        // 1
        tokens.add(new Token(NUMBER, "1", 1.0, 1));
        // ==
        tokens.add(new Token(EQUAL_EQUAL, "==", null, 1));
        // 2
        tokens.add(new Token(NUMBER, "2", 2.0, 1));
        // ?
        tokens.add(new Token(QUESTION, "?", null, 1));
        // 3
        tokens.add(new Token(NUMBER, "3", 3.0, 1));
        // +
        tokens.add(new Token(PLUS, "+", null, 1));
        // 4
        tokens.add(new Token(NUMBER, "4", 4.0, 1));
        // :
        tokens.add(new Token(COLON, ":", null, 1));
        // 5
        tokens.add(new Token(NUMBER, "5", 5.0, 1));
        // *
        tokens.add(new Token(STAR, "*", null, 1));
        // 6
        tokens.add(new Token(NUMBER, "6", 6.0, 1));
        // EOF
        tokens.add(new Token(EOF, "", null, 1));

        // Parse the tokens
        parser = new Parser(tokens);
        expr = parser.parse();

        // Convert the AST to string representation
        result = new AstPrinter().print(expr);

        // The expected AST structure for "1 == 2 ? 3 + 4 : 5 * 6"
        // The condition should be (== 1.0 2.0), the true branch should be (+ 3.0 4.0),
        // and the false branch should be (* 5.0 6.0)
        expected = "(?: (== 1.0 2.0) (+ 3.0 4.0) (* 5.0 6.0))";

        // Assert the expected output
        Assertions.assertThat(result).isEqualTo(expected);

        // Test case 3: Right associativity - "true ? 1 : false ? 2 : 3"
        // This should parse as "true ? 1 : (false ? 2 : 3)" not "(true ? 1 : false) ? 2 : 3"
        tokens.clear();

        // true
        tokens.add(new Token(TRUE, "true", true, 1));
        // ?
        tokens.add(new Token(QUESTION, "?", null, 1));
        // 1
        tokens.add(new Token(NUMBER, "1", 1.0, 1));
        // :
        tokens.add(new Token(COLON, ":", null, 1));
        // false
        tokens.add(new Token(FALSE, "false", false, 1));
        // ?
        tokens.add(new Token(QUESTION, "?", null, 1));
        // 2
        tokens.add(new Token(NUMBER, "2", 2.0, 1));
        // :
        tokens.add(new Token(COLON, ":", null, 1));
        // 3
        tokens.add(new Token(NUMBER, "3", 3.0, 1));
        // EOF
        tokens.add(new Token(EOF, "", null, 1));

        // Parse the tokens
        parser = new Parser(tokens);
        expr = parser.parse();

        // Convert the AST to string representation
        result = new AstPrinter().print(expr);

        // The expected AST structure for "true ? 1 : false ? 2 : 3" with right associativity
        // should be "(?: true 1.0 (?: false 2.0 3.0))" not "(?: (?: true 1.0 false) 2.0 3.0)"
        expected = "(?: true 1.0 (?: false 2.0 3.0))";

        // Assert the expected output
        Assertions.assertThat(result).isEqualTo(expected);
    }

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
