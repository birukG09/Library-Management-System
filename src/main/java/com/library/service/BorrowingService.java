package com.library.service;

import com.library.dao.BookDAO;
import com.library.dao.MemberDAO;
import com.library.dao.BorrowRecordDAO;
import com.library.model.Book;
import com.library.model.Member;
import com.library.model.BorrowRecord;
import com.library.exception.BookNotFoundException;
import com.library.exception.MemberNotFoundException;
import com.library.exception.DatabaseException;
import com.library.util.FileLogger;
import java.util.List;
import java.util.UUID;

/**
 * Service class handling book borrowing and returning operations
 */
public class BorrowingService {
    private final BookDAO bookDAO;
    private final MemberDAO memberDAO;
    private final BorrowRecordDAO borrowRecordDAO;
    private final FileLogger logger;
    
    public BorrowingService() {
        this.bookDAO = new BookDAO();
        this.memberDAO = new MemberDAO();
        this.borrowRecordDAO = new BorrowRecordDAO();
        this.logger = FileLogger.getInstance();
    }
    
    public BorrowRecord borrowBook(String memberId, String isbn) throws DatabaseException, BookNotFoundException, MemberNotFoundException {
        try {
            // Validate member
            Member member = memberDAO.findById(memberId);
            if (member == null) {
                throw new MemberNotFoundException("Member not found with ID: " + memberId);
            }
            
            if (!member.canBorrowBooks()) {
                throw new DatabaseException("Member cannot borrow books. Check membership status and borrowed book limit.");
            }
            
            // Validate book
            Book book = bookDAO.findByIsbn(isbn);
            if (book == null) {
                throw new BookNotFoundException("Book not found with ISBN: " + isbn);
            }
            
            if (!book.isAvailable()) {
                throw new DatabaseException("Book is not available for borrowing");
            }
            
            // Create borrow record
            String recordId = UUID.randomUUID().toString();
            BorrowRecord borrowRecord = new BorrowRecord(recordId, memberId, isbn);
            borrowRecordDAO.createBorrowRecord(borrowRecord);
            
            // Update book availability
            book.borrowBook();
            bookDAO.updateBook(book);
            
            // Update member's borrowed books count
            member.setBorrowedBooksCount(member.getBorrowedBooksCount() + 1);
            memberDAO.updateMember(member);
            
            logger.log("INFO", "Book borrowed - Member: " + memberId + ", Book: " + isbn + ", Record: " + recordId);
            return borrowRecord;
            
        } catch (DatabaseException | BookNotFoundException | MemberNotFoundException e) {
            logger.log("ERROR", "Failed to borrow book: " + e.getMessage());
            throw e;
        }
    }
    
    public BorrowRecord returnBook(String recordId) throws DatabaseException {
        try {
            // Find borrow record
            BorrowRecord borrowRecord = borrowRecordDAO.findById(recordId);
            if (borrowRecord == null) {
                throw new DatabaseException("Borrow record not found: " + recordId);
            }
            
            if (!"BORROWED".equals(borrowRecord.getStatus())) {
                throw new DatabaseException("Book has already been returned");
            }
            
            // Calculate fine and update record
            borrowRecord.returnBook();
            borrowRecordDAO.updateBorrowRecord(borrowRecord);
            
            // Update book availability
            Book book = bookDAO.findByIsbn(borrowRecord.getIsbn());
            if (book != null) {
                book.returnBook();
                bookDAO.updateBook(book);
            }
            
            // Update member's borrowed books count
            Member member = memberDAO.findById(borrowRecord.getMemberId());
            if (member != null) {
                member.setBorrowedBooksCount(Math.max(0, member.getBorrowedBooksCount() - 1));
                memberDAO.updateMember(member);
            }
            
            logger.log("INFO", "Book returned - Record: " + recordId + ", Fine: $" + borrowRecord.getFineAmount());
            return borrowRecord;
            
        } catch (DatabaseException e) {
            logger.log("ERROR", "Failed to return book: " + e.getMessage());
            throw e;
        }
    }
    
    public List<BorrowRecord> getMemberBorrowHistory(String memberId) throws DatabaseException {
        try {
            return borrowRecordDAO.findByMemberId(memberId);
        } catch (DatabaseException e) {
            logger.log("ERROR", "Failed to retrieve member borrow history: " + e.getMessage());
            throw e;
        }
    }
    
    public List<BorrowRecord> getMemberActiveBorrows(String memberId) throws DatabaseException {
        try {
            return borrowRecordDAO.findActiveByMemberId(memberId);
        } catch (DatabaseException e) {
            logger.log("ERROR", "Failed to retrieve active borrows: " + e.getMessage());
            throw e;
        }
    }
    
    public List<BorrowRecord> getOverdueRecords() throws DatabaseException {
        try {
            return borrowRecordDAO.findOverdueRecords();
        } catch (DatabaseException e) {
            logger.log("ERROR", "Failed to retrieve overdue records: " + e.getMessage());
            throw e;
        }
    }
    
    public List<BorrowRecord> getAllBorrowRecords() throws DatabaseException {
        try {
            return borrowRecordDAO.findAll();
        } catch (DatabaseException e) {
            logger.log("ERROR", "Failed to retrieve all borrow records: " + e.getMessage());
            throw e;
        }
    }
    
    // Method overloading demonstration
    public double calculateTotalFines(String memberId) throws DatabaseException {
        List<BorrowRecord> records = getMemberBorrowHistory(memberId);
        return calculateTotalFines(records);
    }
    
    public double calculateTotalFines(List<BorrowRecord> records) {
        return records.stream()
                .mapToDouble(BorrowRecord::getFineAmount)
                .sum();
    }
}
