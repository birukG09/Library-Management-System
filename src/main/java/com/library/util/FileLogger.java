package com.library.util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Singleton utility class for file-based logging
 * Demonstrates file I/O operations using BufferedWriter
 */
public class FileLogger {
    private static FileLogger instance;
    private final String logFileName;
    private final DateTimeFormatter formatter;
    
    private FileLogger() {
        this.logFileName = "library_system.log";
        this.formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    }
    
    public static synchronized FileLogger getInstance() {
        if (instance == null) {
            instance = new FileLogger();
        }
        return instance;
    }
    
    // Method overloading for different log methods
    public void log(String level, String message) {
        log(level, message, null);
    }
    
    public void log(String level, String message, Exception exception) {
        String timestamp = LocalDateTime.now().format(formatter);
        String logEntry = String.format("[%s] %s: %s", timestamp, level, message);
        
        if (exception != null) {
            logEntry += " - Exception: " + exception.getMessage();
        }
        
        writeToFile(logEntry);
    }
    
    private synchronized void writeToFile(String logEntry) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFileName, true))) {
            writer.write(logEntry);
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            // If logging fails, print to console as fallback
            System.err.println("Failed to write to log file: " + e.getMessage());
            System.err.println("Log entry: " + logEntry);
        }
    }
    
    public void logError(String message, Exception exception) {
        log("ERROR", message, exception);
    }
    
    public void logInfo(String message) {
        log("INFO", message);
    }
    
    public void logWarning(String message) {
        log("WARNING", message);
    }
    
    public void logDebug(String message) {
        log("DEBUG", message);
    }
    
    // Method to create a backup of current log
    public boolean createBackup() {
        String backupFileName = "library_system_backup_" + 
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".log";
        
        try (BufferedWriter backup = new BufferedWriter(new FileWriter(backupFileName))) {
            // This is a simple backup - in a real system, you'd copy the existing log file
            backup.write("=== Log Backup Created ===");
            backup.newLine();
            backup.write("Timestamp: " + LocalDateTime.now().format(formatter));
            backup.newLine();
            log("INFO", "Log backup created: " + backupFileName);
            return true;
        } catch (IOException e) {
            log("ERROR", "Failed to create log backup", e);
            return false;
        }
    }
    
    public String getLogFileName() {
        return logFileName;
    }
}
