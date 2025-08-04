package com.craftinginterpreters.lox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Main class for the Lox interpreter.
 * Handles running scripts from files or in interactive mode.
 */
public class Lox {
    private static boolean hadError;

    public static void main(String[] args) throws IOException {
        switch (args.length) {
            case 0 -> runPrompt();
            case 1 -> runFile(args[0]);
            default -> {
                System.out.println("Usage: jlox [script]");
                System.exit(64);
            }
        }
    }

    /**
     * Runs a Lox script from a file.
     *
     * @param path the path to the script file
     * @throws IOException if an I/O error occurs
     */
    private static void runFile(String path) throws IOException {
        var bytes = Files.readAllBytes(Path.of(path));
        run(new String(bytes, StandardCharsets.UTF_8));

        if (hadError) {
            System.exit(65);
        }
    }

    /**
     * Runs the Lox interpreter in interactive mode (REPL).
     *
     * @throws IOException if an I/O error occurs
     */
    private static void runPrompt() throws IOException {
        try (var input = new InputStreamReader(System.in);
             var reader = new BufferedReader(input)) {

            while (true) {
                System.out.print("> ");
                var line = reader.readLine();
                if (line == null) break;

                run(line);
                hadError = false;
            }
        }
    }

    /**
     * Runs the Lox interpreter on the given source code.
     *
     * @param source the source code to run
     */
    private static void run(String source) {
        var scanner = new Scanner(source);
        var tokens = scanner.scanTokens();

        tokens.forEach(System.out::println);
    }

    /**
     * Reports an error at the specified line.
     *
     * @param line the line number where the error occurred
     * @param message the error message
     */
    static void error(int line, String message) {
        report(line, "", message);
    }

    /**
     * Reports an error with detailed information.
     *
     * @param line the line number where the error occurred
     * @param where additional context about where the error occurred
     * @param message the error message
     */
    private static void report(int line, String where, String message) {
        System.err.printf("[line %d] Error%s: %s%n", line, where, message);
        hadError = true;
    }
}
