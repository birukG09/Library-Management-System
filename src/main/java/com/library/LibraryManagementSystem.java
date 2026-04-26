package com.library;

import com.library.ui.MainMenu;
import com.library.util.DatabaseConnection;
import com.library.util.FileLogger;
import com.library.exception.DatabaseException;


public class LibraryManagementSystem {
    private static final FileLogger logger = FileLogger.getInstance();
    
    public static void main(String[] args) {
        logger.log("INFO", "Library Management System starting...");
        
        try {
            
            DatabaseConnection.getInstance().initializeDatabase();
            logger.log("INFO", "Database initialized successfully");
            
            MainMenu mainMenu = new MainMenu();
            mainMenu.displayMenu();
            
        } catch (DatabaseException e) {
            logger.log("ERROR", "Failed to initialize database: " + e.getMessage());
            System.err.println("Failed to start application: " + e.getMessage());
        } catch (Exception e) {
            logger.log("ERROR", "Unexpected error: " + e.getMessage());
            System.err.println("Unexpected error occurred: " + e.getMessage());
        } finally {
            logger.log("INFO", "Library Management System shutting down...");
        }
    }
}
