#!/bin/bash
# build.sh - Script to build and run the jlox interpreter using Maven

# Exit on error
set -e

# Change to the project directory
cd "$(dirname "$0")"

echo "Building jlox with Maven..."

# Clean and compile
echo "Compiling..."
mvn clean compile

# Run tests
echo "Running tests..."
mvn test

# Package into JAR
echo "Packaging..."
mvn package

echo "Build complete. JAR file created in target directory."

# Instructions for running
echo ""
echo "To run the jlox interpreter in REPL mode:"
echo "mvn exec:java"
echo ""
echo "To run a Lox script file:"
echo "mvn exec:java -Dexec.args=\"path/to/script.lox\""
echo ""
echo "Alternatively, you can run the JAR directly:"
echo "java -jar target/jlox-1.0-SNAPSHOT.jar [script]"