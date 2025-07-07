package com.library.exception;

/**
 * Custom exception for book not found scenarios
 */
public class BookNotFoundException extends LibraryException {
    
    public BookNotFoundException(String message) {
        super(message, "BOOK_NOT_FOUND");
    }
    
    public BookNotFoundException(String message, Throwable cause) {
        super(message, "BOOK_NOT_FOUND", cause);
    }
    
    public static BookNotFoundException forIsbn(String isbn) {
        return new BookNotFoundException("Book not found with ISBN: " + isbn);
    }
}
