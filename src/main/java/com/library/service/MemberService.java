package com.library.service;

import com.library.dao.MemberDAO;
import com.library.model.Member;
import com.library.exception.MemberNotFoundException;
import com.library.exception.DatabaseException;
import com.library.util.FileLogger;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * Service class for Member-related business logic
 */
public class MemberService {
    private final MemberDAO memberDAO;
    private final FileLogger logger;
    
    public MemberService() {
        this.memberDAO = new MemberDAO();
        this.logger = FileLogger.getInstance();
    }
    
    public void registerMember(Member member) throws DatabaseException {
        try {
            // Check if member already exists
            Member existingMember = memberDAO.findById(member.getId());
            if (existingMember != null) {
                throw new DatabaseException("Member with ID " + member.getId() + " already exists");
            }
            
            memberDAO.createMember(member);
            logger.log("INFO", "Member registered: " + member.getFullName() + " (ID: " + member.getId() + ")");
        } catch (DatabaseException e) {
            logger.log("ERROR", "Failed to register member: " + e.getMessage());
            throw e;
        }
    }
    
    public Member findMemberById(String id) throws MemberNotFoundException, DatabaseException {
        try {
            Member member = memberDAO.findById(id);
            if (member == null) {
                throw new MemberNotFoundException("Member not found with ID: " + id);
            }
            return member;
        } catch (DatabaseException e) {
            logger.log("ERROR", "Failed to find member: " + e.getMessage());
            throw e;
        }
    }
    
    public List<Member> getAllMembers() throws DatabaseException {
        try {
            return memberDAO.findAll();
        } catch (DatabaseException e) {
            logger.log("ERROR", "Failed to retrieve members: " + e.getMessage());
            throw e;
        }
    }
    
    public List<Member> searchMembers(String searchTerm) throws DatabaseException {
        try {
            List<Member> members = memberDAO.searchMembers(searchTerm);
            logger.log("INFO", "Member search performed for: " + searchTerm + ", found " + members.size() + " results");
            return members;
        } catch (DatabaseException e) {
            logger.log("ERROR", "Failed to search members: " + e.getMessage());
            throw e;
        }
    }
    
    public void updateMember(Member member) throws MemberNotFoundException, DatabaseException {
        try {
            // Verify member exists
            Member existingMember = memberDAO.findById(member.getId());
            if (existingMember == null) {
                throw new MemberNotFoundException("Member not found with ID: " + member.getId());
            }
            
            memberDAO.updateMember(member);
            logger.log("INFO", "Member updated: " + member.getFullName() + " (ID: " + member.getId() + ")");
        } catch (DatabaseException e) {
            logger.log("ERROR", "Failed to update member: " + e.getMessage());
            throw e;
        }
    }
    
    public void deleteMember(String id) throws MemberNotFoundException, DatabaseException {
        try {
            // Verify member exists
            Member existingMember = memberDAO.findById(id);
            if (existingMember == null) {
                throw new MemberNotFoundException("Member not found with ID: " + id);
            }
            
            memberDAO.deleteMember(id);
            logger.log("INFO", "Member deleted: ID " + id);
        } catch (DatabaseException e) {
            logger.log("ERROR", "Failed to delete member: " + e.getMessage());
            throw e;
        }
    }
    
    public boolean canMemberBorrowBooks(String memberId) throws DatabaseException {
        try {
            Member member = memberDAO.findById(memberId);
            return member != null && member.canBorrowBooks();
        } catch (DatabaseException e) {
            logger.log("ERROR", "Failed to check member borrowing eligibility: " + e.getMessage());
            throw e;
        }
    }
    
    public Map<String, Integer> getMembershipTypeStats() throws DatabaseException {
        try {
            List<Member> members = memberDAO.findAll();
            Map<String, Integer> stats = new HashMap<>();
            
            for (Member member : members) {
                String type = member.getMembershipType();
                stats.put(type, stats.getOrDefault(type, 0) + 1);
            }
            
            return stats;
        } catch (DatabaseException e) {
            logger.log("ERROR", "Failed to generate membership stats: " + e.getMessage());
            throw e;
        }
    }
}
