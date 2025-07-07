package com.library.exception;

/**
 * Base custom exception class for the Library Management System
 * Demonstrates custom exception handling
 */
public class LibraryException extends Exception {
    private final String errorCode;
    private final long timestamp;
    
    public LibraryException(String message) {
        super(message);
        this.errorCode = "LIB_GENERAL_ERROR";
        this.timestamp = System.currentTimeMillis();
    }
    
    public LibraryException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
        this.timestamp = System.currentTimeMillis();
    }
    
    public LibraryException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "LIB_GENERAL_ERROR";
        this.timestamp = System.currentTimeMillis();
    }
    
    public LibraryException(String message, String errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.timestamp = System.currentTimeMillis();
    }
    
    public String getErrorCode() {
        return errorCode;
    }
    
    public long getTimestamp() {
        return timestamp;
    }
    
    @Override
    public String toString() {
        return String.format("LibraryException [%s]: %s (Timestamp: %d)", 
            errorCode, getMessage(), timestamp);
    }
}
