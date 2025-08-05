package com.craftinginterpreters.lox;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.assertj.core.api.Assertions;

public class AstPrinterTest {

    @Test
    @DisplayName("Test printing a complex expression")
    public void testPrintComplexExpression() {
        // Create the expression: (- 123 * (group 45.67))
        Expr expression = new Expr.Binary(
                new Expr.Unary(
                        new Token(TokenType.MINUS, "-", null, 1),
                        new Expr.Literal(123)
                ),
                new Token(TokenType.STAR, "*", null, 1),
                new Expr.Grouping(
                        new Expr.Literal(45.67)
                )
        );
        
        // Print the expression using AstPrinter
        String result = new AstPrinter().print(expression);
        
        // Assert the expected output
        Assertions.assertThat(result).isEqualTo("(* (- 123) (group 45.67))");
    }
}