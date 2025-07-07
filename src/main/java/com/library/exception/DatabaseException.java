package com.library.exception;

/**
 * Custom exception for database-related errors
 */
public class DatabaseException extends LibraryException {
    
    public DatabaseException(String message) {
        super(message, "DATABASE_ERROR");
    }
    
    public DatabaseException(String message, Throwable cause) {
        super(message, "DATABASE_ERROR", cause);
    }
    
    public DatabaseException(String message, String specificErrorCode) {
        super(message, "DATABASE_ERROR_" + specificErrorCode);
    }
    
    public DatabaseException(String message, String specificErrorCode, Throwable cause) {
        super(message, "DATABASE_ERROR_" + specificErrorCode, cause);
    }
}
