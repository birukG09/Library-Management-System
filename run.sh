#!/bin/bash

# Library Management System Startup Script
# This script compiles and runs the Java Library Management System

echo "=== Library Management System ==="
echo "Initializing application..."

# Create necessary directories
mkdir -p config
mkdir -p logs
mkdir -p reports
mkdir -p backups

# Set Java classpath
CLASSPATH="."

# Check if SQLite JDBC driver is available
SQLITE_JAR="sqlite-jdbc-3.42.0.0.jar"
if [ ! -f "$SQLITE_JAR" ]; then
    echo "Downloading SQLite JDBC driver..."
    curl -L "https://repo1.maven.org/maven2/org/xerial/sqlite-jdbc/3.42.0.0/sqlite-jdbc-3.42.0.0.jar" -o "$SQLITE_JAR"
    if [ $? -eq 0 ]; then
        echo "SQLite JDBC driver downloaded successfully."
    else
        echo "Failed to download SQLite JDBC driver. Please download manually."
        echo "URL: https://repo1.maven.org/maven2/org/xerial/sqlite-jdbc/3.42.0.0/sqlite-jdbc-3.42.0.0.jar"
        exit 1
    fi
fi

# Add SQLite JDBC driver to classpath
CLASSPATH="$CLASSPATH:$SQLITE_JAR"

# Compile Java source files
echo "Compiling Java source files..."
find src -name "*.java" -print0 | xargs -0 javac -cp "$CLASSPATH" -d .

if [ $? -eq 0 ]; then
    echo "Compilation successful."
else
    echo "Compilation failed. Please check for errors."
    exit 1
fi

# Run the application
echo "Starting Library Management System..."
echo ""

java -cp "$CLASSPATH" com.library.LibraryManagementSystem

echo ""
echo "Thank you for using Library Management System!"
