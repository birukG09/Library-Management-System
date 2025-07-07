package com.library.util;

import com.library.exception.DatabaseException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Singleton class managing database connections and initialization
 */
public class DatabaseConnection {
    private static DatabaseConnection instance;
    private final String databaseUrl;
    private final ConfigReader configReader;
    private final FileLogger logger;
    
    private DatabaseConnection() {
        this.configReader = ConfigReader.getInstance();
        this.logger = FileLogger.getInstance();
        this.databaseUrl = "jdbc:sqlite:" + configReader.getProperty("db.name", "library.db");
    }
    
    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }
    
    public Connection getConnection() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
            return DriverManager.getConnection(databaseUrl);
        } catch (ClassNotFoundException e) {
            throw new SQLException("SQLite JDBC driver not found", e);
        }
    }
    
    public void initializeDatabase() throws DatabaseException {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            
            // Create books table
            String createBooksTable = """
                CREATE TABLE IF NOT EXISTS books (
                    isbn TEXT PRIMARY KEY,
                    title TEXT NOT NULL,
                    author TEXT NOT NULL,
                    category TEXT NOT NULL,
                    publisher TEXT NOT NULL,
                    publication_date DATE NOT NULL,
                    total_copies INTEGER NOT NULL,
                    available_copies INTEGER NOT NULL,
                    is_active BOOLEAN DEFAULT TRUE
                )
            """;
            stmt.execute(createBooksTable);
            
            // Create members table
            String createMembersTable = """
                CREATE TABLE IF NOT EXISTS members (
                    id TEXT PRIMARY KEY,
                    first_name TEXT NOT NULL,
                    last_name TEXT NOT NULL,
                    email TEXT NOT NULL UNIQUE,
                    phone TEXT NOT NULL,
                    membership_type TEXT NOT NULL,
                    membership_expiry DATE NOT NULL,
                    borrowed_books_count INTEGER DEFAULT 0,
                    is_active BOOLEAN DEFAULT TRUE,
                    registration_date DATE NOT NULL
                )
            """;
            stmt.execute(createMembersTable);
            
            // Create borrow_records table
            String createBorrowRecordsTable = """
                CREATE TABLE IF NOT EXISTS borrow_records (
                    record_id TEXT PRIMARY KEY,
                    member_id TEXT NOT NULL,
                    isbn TEXT NOT NULL,
                    borrow_date DATE NOT NULL,
                    due_date DATE NOT NULL,
                    return_date DATE,
                    status TEXT NOT NULL,
                    fine_amount REAL DEFAULT 0.0,
                    FOREIGN KEY (member_id) REFERENCES members(id),
                    FOREIGN KEY (isbn) REFERENCES books(isbn)
                )
            """;
            stmt.execute(createBorrowRecordsTable);
            
            // Create indexes for better performance
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_books_title ON books(title)");
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_books_author ON books(author)");
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_books_category ON books(category)");
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_members_name ON members(last_name, first_name)");
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_borrow_records_member ON borrow_records(member_id)");
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_borrow_records_status ON borrow_records(status)");
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_borrow_records_due_date ON borrow_records(due_date)");
            
            logger.log("INFO", "Database tables initialized successfully");
            
        } catch (SQLException e) {
            throw new DatabaseException("Failed to initialize database: " + e.getMessage(), e);
        }
    }
    
    public void testConnection() throws DatabaseException {
        try (Connection conn = getConnection()) {
            if (conn != null && !conn.isClosed()) {
                logger.log("INFO", "Database connection test successful");
            }
        } catch (SQLException e) {
            throw new DatabaseException("Database connection test failed: " + e.getMessage(), e);
        }
    }
}
