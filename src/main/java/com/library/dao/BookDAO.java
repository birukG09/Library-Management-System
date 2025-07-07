package com.library.dao;

import com.library.model.Book;
import com.library.util.DatabaseConnection;
import com.library.exception.DatabaseException;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Book entities
 * Implements CRUD operations using JDBC
 */
public class BookDAO {
    private final DatabaseConnection dbConnection;
    
    public BookDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }
    
    public void createBook(Book book) throws DatabaseException {
        String sql = "INSERT INTO books (isbn, title, author, category, publisher, publication_date, total_copies, available_copies, is_active) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, book.getIsbn());
            stmt.setString(2, book.getTitle());
            stmt.setString(3, book.getAuthor());
            stmt.setString(4, book.getCategory());
            stmt.setString(5, book.getPublisher());
            stmt.setDate(6, Date.valueOf(book.getPublicationDate()));
            stmt.setInt(7, book.getTotalCopies());
            stmt.setInt(8, book.getAvailableCopies());
            stmt.setBoolean(9, book.isActive());
            
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to create book: " + e.getMessage(), e);
        }
    }
    
    public Book findByIsbn(String isbn) throws DatabaseException {
        String sql = "SELECT * FROM books WHERE isbn = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, isbn);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToBook(rs);
            }
            return null;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find book: " + e.getMessage(), e);
        }
    }
    
    public List<Book> findAll() throws DatabaseException {
        String sql = "SELECT * FROM books WHERE is_active = true ORDER BY title";
        List<Book> books = new ArrayList<>();
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                books.add(mapResultSetToBook(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to retrieve books: " + e.getMessage(), e);
        }
        
        return books;
    }
    
    public List<Book> searchBooks(String searchTerm) throws DatabaseException {
        String sql = "SELECT * FROM books WHERE is_active = true AND (title LIKE ? OR author LIKE ? OR category LIKE ? OR isbn LIKE ?) ORDER BY title";
        List<Book> books = new ArrayList<>();
        String searchPattern = "%" + searchTerm + "%";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            stmt.setString(3, searchPattern);
            stmt.setString(4, searchPattern);
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                books.add(mapResultSetToBook(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to search books: " + e.getMessage(), e);
        }
        
        return books;
    }
    
    public void updateBook(Book book) throws DatabaseException {
        String sql = "UPDATE books SET title = ?, author = ?, category = ?, publisher = ?, publication_date = ?, total_copies = ?, available_copies = ?, is_active = ? WHERE isbn = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, book.getTitle());
            stmt.setString(2, book.getAuthor());
            stmt.setString(3, book.getCategory());
            stmt.setString(4, book.getPublisher());
            stmt.setDate(5, Date.valueOf(book.getPublicationDate()));
            stmt.setInt(6, book.getTotalCopies());
            stmt.setInt(7, book.getAvailableCopies());
            stmt.setBoolean(8, book.isActive());
            stmt.setString(9, book.getIsbn());
            
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new DatabaseException("Book not found for update: " + book.getIsbn());
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to update book: " + e.getMessage(), e);
        }
    }
    
    public void deleteBook(String isbn) throws DatabaseException {
        String sql = "UPDATE books SET is_active = false WHERE isbn = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, isbn);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new DatabaseException("Book not found for deletion: " + isbn);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to delete book: " + e.getMessage(), e);
        }
    }
    
    public List<Book> findAvailableBooks() throws DatabaseException {
        String sql = "SELECT * FROM books WHERE is_active = true AND available_copies > 0 ORDER BY title";
        List<Book> books = new ArrayList<>();
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                books.add(mapResultSetToBook(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to retrieve available books: " + e.getMessage(), e);
        }
        
        return books;
    }
    
    private Book mapResultSetToBook(ResultSet rs) throws SQLException {
        Book book = new Book();
        book.setIsbn(rs.getString("isbn"));
        book.setTitle(rs.getString("title"));
        book.setAuthor(rs.getString("author"));
        book.setCategory(rs.getString("category"));
        book.setPublisher(rs.getString("publisher"));
        book.setPublicationDate(rs.getDate("publication_date").toLocalDate());
        book.setTotalCopies(rs.getInt("total_copies"));
        book.setAvailableCopies(rs.getInt("available_copies"));
        book.setActive(rs.getBoolean("is_active"));
        return book;
    }
}
