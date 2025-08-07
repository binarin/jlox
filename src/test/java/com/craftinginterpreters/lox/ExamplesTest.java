package com.craftinginterpreters.lox;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.DisplayName;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

/**
 * Test class that dynamically creates tests based on .lox files in the 't' directory.
 * Each .lox file is run through the Lox interpreter and the output is compared to
 * the corresponding .lox.out file.
 */
public class ExamplesTest {

    private static final Path TEST_DIR = Paths.get("t");
    
    /**
     * Generates dynamic tests for all .lox files in the 't' directory and its subdirectories.
     * Files and directories are processed in alphabetical order.
     * 
     * @return a collection of dynamic tests
     * @throws IOException if an I/O error occurs
     */
    @TestFactory
    @DisplayName("Lox Examples Tests")
    public List<DynamicTest> loxExamplesTests() throws IOException {
        if (!Files.exists(TEST_DIR)) {
            return Collections.emptyList();
        }
        
        List<DynamicTest> dynamicTests = new ArrayList<>();
        collectTests(TEST_DIR, dynamicTests);
        return dynamicTests;
    }
    
    /**
     * Recursively collects tests from the given directory and its subdirectories.
     * 
     * @param directory the directory to collect tests from
     * @param tests the list to add tests to
     * @throws IOException if an I/O error occurs
     */
    private void collectTests(Path directory, List<DynamicTest> tests) throws IOException {
        // Get all entries in the directory and sort them alphabetically
        List<Path> entries = Files.list(directory)
                .sorted()
                .collect(Collectors.toList());
        
        for (Path entry : entries) {
            if (Files.isDirectory(entry)) {
                // Recursively process subdirectories
                collectTests(entry, tests);
            } else if (entry.toString().endsWith(".lox")) {
                // Create a test for each .lox file
                Path outFile = Paths.get(entry.toString() + ".out");
                if (Files.exists(outFile)) {
                    String testName = directory.relativize(entry).toString();
                    tests.add(createTest(testName, entry, outFile));
                }
            }
        }
    }
    
    /**
     * Creates a dynamic test for the given .lox file and its corresponding .lox.out file.
     * 
     * @param testName the name of the test
     * @param loxFile the .lox file to run
     * @param outFile the .lox.out file containing the expected output
     * @return a dynamic test
     */
    private DynamicTest createTest(String testName, Path loxFile, Path outFile) {
        return dynamicTest(testName, () -> {
            // Capture standard output
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PrintStream originalOut = System.out;
            System.setOut(new PrintStream(outputStream));
            
            try {
                // Run the .lox file using reflection to access the private runFile method
                runLoxFile(loxFile.toString());
                
                // Get the actual output
                String actualOutput = outputStream.toString(StandardCharsets.UTF_8).trim();
                
                // Get the expected output
                String expectedOutput = Files.readString(outFile, StandardCharsets.UTF_8).trim();
                
                // Compare the outputs
                assertEquals(expectedOutput, actualOutput, 
                        "Output from " + loxFile + " does not match expected output in " + outFile);
            } finally {
                // Restore standard output
                System.setOut(originalOut);
            }
        });
    }
    
    /**
     * Runs a Lox file using reflection to access the private runFile method.
     * 
     * @param path the path to the Lox file
     * @throws Exception if an error occurs
     */
    private void runLoxFile(String path) throws Exception {
        // Reset the error flags in Lox class
        setStaticField(Lox.class, "hadError", false);
        setStaticField(Lox.class, "hadRuntimeError", false);
        
        // Read the file content
        byte[] bytes = Files.readAllBytes(Paths.get(path));
        String source = new String(bytes, StandardCharsets.UTF_8);
        
        // Get the interpreter instance
        Interpreter interpreter = (Interpreter) getStaticField(Lox.class, "interpreter");
        
        // Create scanner, parser, and get statements
        Scanner scanner = new Scanner(source);
        List<Token> tokens = scanner.scanTokens();
        Parser parser = new Parser(tokens);
        List<Stmt> statements = parser.parse();
        
        // Check for errors
        if ((boolean) getStaticField(Lox.class, "hadError")) {
            return;
        }
        
        // Interpret the statements
        Method interpretMethod = interpreter.getClass().getDeclaredMethod("interpret", List.class);
        interpretMethod.setAccessible(true);
        interpretMethod.invoke(interpreter, statements);
    }
    
    /**
     * Sets the value of a static field using reflection.
     * 
     * @param clazz the class containing the field
     * @param fieldName the name of the field
     * @param value the value to set
     * @throws Exception if an error occurs
     */
    private void setStaticField(Class<?> clazz, String fieldName, Object value) throws Exception {
        java.lang.reflect.Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(null, value);
    }
    
    /**
     * Gets the value of a static field using reflection.
     * 
     * @param clazz the class containing the field
     * @param fieldName the name of the field
     * @return the value of the field
     * @throws Exception if an error occurs
     */
    private Object getStaticField(Class<?> clazz, String fieldName) throws Exception {
        java.lang.reflect.Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(null);
    }
}