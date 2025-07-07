package com.library.dao;

import com.library.model.BorrowRecord;
import com.library.util.DatabaseConnection;
import com.library.exception.DatabaseException;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for BorrowRecord entities
 */
public class BorrowRecordDAO {
    private final DatabaseConnection dbConnection;
    
    public BorrowRecordDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }
    
    public void createBorrowRecord(BorrowRecord record) throws DatabaseException {
        String sql = "INSERT INTO borrow_records (record_id, member_id, isbn, borrow_date, due_date, return_date, status, fine_amount) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, record.getRecordId());
            stmt.setString(2, record.getMemberId());
            stmt.setString(3, record.getIsbn());
            stmt.setDate(4, Date.valueOf(record.getBorrowDate()));
            stmt.setDate(5, Date.valueOf(record.getDueDate()));
            stmt.setDate(6, record.getReturnDate() != null ? Date.valueOf(record.getReturnDate()) : null);
            stmt.setString(7, record.getStatus());
            stmt.setDouble(8, record.getFineAmount());
            
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to create borrow record: " + e.getMessage(), e);
        }
    }
    
    public BorrowRecord findById(String recordId) throws DatabaseException {
        String sql = "SELECT * FROM borrow_records WHERE record_id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, recordId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToBorrowRecord(rs);
            }
            return null;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find borrow record: " + e.getMessage(), e);
        }
    }
    
    public List<BorrowRecord> findByMemberId(String memberId) throws DatabaseException {
        String sql = "SELECT * FROM borrow_records WHERE member_id = ? ORDER BY borrow_date DESC";
        List<BorrowRecord> records = new ArrayList<>();
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, memberId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                records.add(mapResultSetToBorrowRecord(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find borrow records for member: " + e.getMessage(), e);
        }
        
        return records;
    }
    
    public List<BorrowRecord> findActiveByMemberId(String memberId) throws DatabaseException {
        String sql = "SELECT * FROM borrow_records WHERE member_id = ? AND status = 'BORROWED' ORDER BY due_date";
        List<BorrowRecord> records = new ArrayList<>();
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, memberId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                records.add(mapResultSetToBorrowRecord(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find active borrow records: " + e.getMessage(), e);
        }
        
        return records;
    }
    
    public List<BorrowRecord> findOverdueRecords() throws DatabaseException {
        String sql = "SELECT * FROM borrow_records WHERE status = 'BORROWED' AND due_date < ? ORDER BY due_date";
        List<BorrowRecord> records = new ArrayList<>();
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, Date.valueOf(LocalDate.now()));
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                records.add(mapResultSetToBorrowRecord(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find overdue records: " + e.getMessage(), e);
        }
        
        return records;
    }
    
    public void updateBorrowRecord(BorrowRecord record) throws DatabaseException {
        String sql = "UPDATE borrow_records SET return_date = ?, status = ?, fine_amount = ? WHERE record_id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, record.getReturnDate() != null ? Date.valueOf(record.getReturnDate()) : null);
            stmt.setString(2, record.getStatus());
            stmt.setDouble(3, record.getFineAmount());
            stmt.setString(4, record.getRecordId());
            
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new DatabaseException("Borrow record not found for update: " + record.getRecordId());
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to update borrow record: " + e.getMessage(), e);
        }
    }
    
    public List<BorrowRecord> findAll() throws DatabaseException {
        String sql = "SELECT * FROM borrow_records ORDER BY borrow_date DESC";
        List<BorrowRecord> records = new ArrayList<>();
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                records.add(mapResultSetToBorrowRecord(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to retrieve borrow records: " + e.getMessage(), e);
        }
        
        return records;
    }
    
    private BorrowRecord mapResultSetToBorrowRecord(ResultSet rs) throws SQLException {
        BorrowRecord record = new BorrowRecord();
        record.setRecordId(rs.getString("record_id"));
        record.setMemberId(rs.getString("member_id"));
        record.setIsbn(rs.getString("isbn"));
        record.setBorrowDate(rs.getDate("borrow_date").toLocalDate());
        record.setDueDate(rs.getDate("due_date").toLocalDate());
        
        Date returnDate = rs.getDate("return_date");
        if (returnDate != null) {
            record.setReturnDate(returnDate.toLocalDate());
        }
        
        record.setStatus(rs.getString("status"));
        record.setFineAmount(rs.getDouble("fine_amount"));
        return record;
    }
}
