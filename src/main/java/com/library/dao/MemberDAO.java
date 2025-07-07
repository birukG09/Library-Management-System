package com.library.dao;

import com.library.model.Member;
import com.library.util.DatabaseConnection;
import com.library.exception.DatabaseException;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Member entities
 */
public class MemberDAO {
    private final DatabaseConnection dbConnection;
    
    public MemberDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }
    
    public void createMember(Member member) throws DatabaseException {
        String sql = "INSERT INTO members (id, first_name, last_name, email, phone, membership_type, membership_expiry, borrowed_books_count, is_active, registration_date) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, member.getId());
            stmt.setString(2, member.getFirstName());
            stmt.setString(3, member.getLastName());
            stmt.setString(4, member.getEmail());
            stmt.setString(5, member.getPhone());
            stmt.setString(6, member.getMembershipType());
            stmt.setDate(7, Date.valueOf(member.getMembershipExpiry()));
            stmt.setInt(8, member.getBorrowedBooksCount());
            stmt.setBoolean(9, member.isActive());
            stmt.setDate(10, Date.valueOf(member.getRegistrationDate()));
            
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to create member: " + e.getMessage(), e);
        }
    }
    
    public Member findById(String id) throws DatabaseException {
        String sql = "SELECT * FROM members WHERE id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToMember(rs);
            }
            return null;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find member: " + e.getMessage(), e);
        }
    }
    
    public List<Member> findAll() throws DatabaseException {
        String sql = "SELECT * FROM members WHERE is_active = true ORDER BY last_name, first_name";
        List<Member> members = new ArrayList<>();
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                members.add(mapResultSetToMember(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to retrieve members: " + e.getMessage(), e);
        }
        
        return members;
    }
    
    public List<Member> searchMembers(String searchTerm) throws DatabaseException {
        String sql = "SELECT * FROM members WHERE is_active = true AND (first_name LIKE ? OR last_name LIKE ? OR email LIKE ? OR id LIKE ?) ORDER BY last_name, first_name";
        List<Member> members = new ArrayList<>();
        String searchPattern = "%" + searchTerm + "%";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            stmt.setString(3, searchPattern);
            stmt.setString(4, searchPattern);
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                members.add(mapResultSetToMember(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to search members: " + e.getMessage(), e);
        }
        
        return members;
    }
    
    public void updateMember(Member member) throws DatabaseException {
        String sql = "UPDATE members SET first_name = ?, last_name = ?, email = ?, phone = ?, membership_type = ?, membership_expiry = ?, borrowed_books_count = ?, is_active = ? WHERE id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, member.getFirstName());
            stmt.setString(2, member.getLastName());
            stmt.setString(3, member.getEmail());
            stmt.setString(4, member.getPhone());
            stmt.setString(5, member.getMembershipType());
            stmt.setDate(6, Date.valueOf(member.getMembershipExpiry()));
            stmt.setInt(7, member.getBorrowedBooksCount());
            stmt.setBoolean(8, member.isActive());
            stmt.setString(9, member.getId());
            
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new DatabaseException("Member not found for update: " + member.getId());
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to update member: " + e.getMessage(), e);
        }
    }
    
    public void deleteMember(String id) throws DatabaseException {
        String sql = "UPDATE members SET is_active = false WHERE id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, id);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new DatabaseException("Member not found for deletion: " + id);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to delete member: " + e.getMessage(), e);
        }
    }
    
    private Member mapResultSetToMember(ResultSet rs) throws SQLException {
        Member member = new Member();
        member.setId(rs.getString("id"));
        member.setFirstName(rs.getString("first_name"));
        member.setLastName(rs.getString("last_name"));
        member.setEmail(rs.getString("email"));
        member.setPhone(rs.getString("phone"));
        member.setMembershipType(rs.getString("membership_type"));
        member.setMembershipExpiry(rs.getDate("membership_expiry").toLocalDate());
        member.setBorrowedBooksCount(rs.getInt("borrowed_books_count"));
        member.setActive(rs.getBoolean("is_active"));
        member.setRegistrationDate(rs.getDate("registration_date").toLocalDate());
        return member;
    }
}
