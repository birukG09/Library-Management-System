package com.library.service;

import com.library.dao.BookDAO;
import com.library.model.Book;
import com.library.exception.BookNotFoundException;
import com.library.exception.DatabaseException;
import com.library.util.FileLogger;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

/**
 * Service class for Book-related business logic
 */
public class BookService {
    private final BookDAO bookDAO;
    private final FileLogger logger;
    
    public BookService() {
        this.bookDAO = new BookDAO();
        this.logger = FileLogger.getInstance();
    }
    
    public void addBook(Book book) throws DatabaseException {
        try {
            // Check if book already exists
            Book existingBook = bookDAO.findByIsbn(book.getIsbn());
            if (existingBook != null) {
                throw new DatabaseException("Book with ISBN " + book.getIsbn() + " already exists");
            }
            
            bookDAO.createBook(book);
            logger.log("INFO", "Book added: " + book.getTitle() + " (ISBN: " + book.getIsbn() + ")");
        } catch (DatabaseException e) {
            logger.log("ERROR", "Failed to add book: " + e.getMessage());
            throw e;
        }
    }
    
    public Book findBookByIsbn(String isbn) throws BookNotFoundException, DatabaseException {
        try {
            Book book = bookDAO.findByIsbn(isbn);
            if (book == null) {
                throw new BookNotFoundException("Book not found with ISBN: " + isbn);
            }
            return book;
        } catch (DatabaseException e) {
            logger.log("ERROR", "Failed to find book: " + e.getMessage());
            throw e;
        }
    }
    
    public List<Book> getAllBooks() throws DatabaseException {
        try {
            return bookDAO.findAll();
        } catch (DatabaseException e) {
            logger.log("ERROR", "Failed to retrieve books: " + e.getMessage());
            throw e;
        }
    }
    
    public List<Book> searchBooks(String searchTerm) throws DatabaseException {
        try {
            List<Book> books = bookDAO.searchBooks(searchTerm);
            logger.log("INFO", "Book search performed for: " + searchTerm + ", found " + books.size() + " results");
            return books;
        } catch (DatabaseException e) {
            logger.log("ERROR", "Failed to search books: " + e.getMessage());
            throw e;
        }
    }
    
    public void updateBook(Book book) throws BookNotFoundException, DatabaseException {
        try {
            // Verify book exists
            Book existingBook = bookDAO.findByIsbn(book.getIsbn());
            if (existingBook == null) {
                throw new BookNotFoundException("Book not found with ISBN: " + book.getIsbn());
            }
            
            bookDAO.updateBook(book);
            logger.log("INFO", "Book updated: " + book.getTitle() + " (ISBN: " + book.getIsbn() + ")");
        } catch (DatabaseException e) {
            logger.log("ERROR", "Failed to update book: " + e.getMessage());
            throw e;
        }
    }
    
    public void deleteBook(String isbn) throws BookNotFoundException, DatabaseException {
        try {
            // Verify book exists
            Book existingBook = bookDAO.findByIsbn(isbn);
            if (existingBook == null) {
                throw new BookNotFoundException("Book not found with ISBN: " + isbn);
            }
            
            bookDAO.deleteBook(isbn);
            logger.log("INFO", "Book deleted: ISBN " + isbn);
        } catch (DatabaseException e) {
            logger.log("ERROR", "Failed to delete book: " + e.getMessage());
            throw e;
        }
    }
    
    public List<Book> getAvailableBooks() throws DatabaseException {
        try {
            return bookDAO.findAvailableBooks();
        } catch (DatabaseException e) {
            logger.log("ERROR", "Failed to retrieve available books: " + e.getMessage());
            throw e;
        }
    }
    
    public Set<String> getUniqueCategories() throws DatabaseException {
        try {
            List<Book> books = bookDAO.findAll();
            Set<String> categories = new HashSet<>();
            for (Book book : books) {
                categories.add(book.getCategory());
            }
            return categories;
        } catch (DatabaseException e) {
            logger.log("ERROR", "Failed to retrieve categories: " + e.getMessage());
            throw e;
        }
    }
    
    public boolean isBookAvailable(String isbn) throws DatabaseException {
        try {
            Book book = bookDAO.findByIsbn(isbn);
            return book != null && book.isAvailable();
        } catch (DatabaseException e) {
            logger.log("ERROR", "Failed to check book availability: " + e.getMessage());
            throw e;
        }
    }
}
