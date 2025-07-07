package com.library.service;

import com.library.interfaces.ReportGenerator;
import com.library.dao.BorrowRecordDAO;
import com.library.dao.BookDAO;
import com.library.dao.MemberDAO;
import com.library.model.BorrowRecord;
import com.library.model.Book;
import com.library.model.Member;
import com.library.exception.DatabaseException;
import com.library.util.FileLogger;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service class implementing ReportGenerator interface
 * Handles all reporting functionality in the system
 */
public class ReportService implements ReportGenerator {
    private final BorrowRecordDAO borrowRecordDAO;
    private final BookDAO bookDAO;
    private final MemberDAO memberDAO;
    private final FileLogger logger;
    
    public ReportService() {
        this.borrowRecordDAO = new BorrowRecordDAO();
        this.bookDAO = new BookDAO();
        this.memberDAO = new MemberDAO();
        this.logger = FileLogger.getInstance();
    }
    
    @Override
    public List<String> generateOverdueReport() {
        List<String> report = new ArrayList<>();
        report.add("=== OVERDUE BOOKS REPORT ===");
        report.add("Generated on: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        report.add("");
        
        try {
            List<BorrowRecord> overdueRecords = borrowRecordDAO.findOverdueRecords();
            
            if (overdueRecords.isEmpty()) {
                report.add("No overdue books found.");
            } else {
                report.add(String.format("%-15s %-20s %-15s %-12s %-10s", 
                    "Member ID", "Book Title", "ISBN", "Due Date", "Fine"));
                report.add("-".repeat(80));
                
                for (BorrowRecord record : overdueRecords) {
                    try {
                        Book book = bookDAO.findByIsbn(record.getIsbn());
                        String title = book != null ? book.getTitle() : "Unknown";
                        if (title.length() > 18) {
                            title = title.substring(0, 18) + "..";
                        }
                        
                        record.calculateFine(); // Update fine calculation
                        
                        report.add(String.format("%-15s %-20s %-15s %-12s $%-9.2f", 
                            record.getMemberId(),
                            title,
                            record.getIsbn(),
                            record.getDueDate().toString(),
                            record.getFineAmount()));
                    } catch (DatabaseException e) {
                        logger.log("ERROR", "Error processing overdue record: " + e.getMessage());
                    }
                }
                
                double totalFines = overdueRecords.stream()
                    .mapToDouble(BorrowRecord::getFineAmount)
                    .sum();
                report.add("-".repeat(80));
                report.add(String.format("Total Outstanding Fines: $%.2f", totalFines));
            }
        } catch (DatabaseException e) {
            report.add("Error generating overdue report: " + e.getMessage());
            logger.log("ERROR", "Failed to generate overdue report: " + e.getMessage());
        }
        
        return report;
    }
    
    @Override
    public Map<String, Object> generateMemberActivityReport() {
        Map<String, Object> report = new HashMap<>();
        
        try {
            List<Member> allMembers = memberDAO.findAll();
            List<BorrowRecord> allRecords = borrowRecordDAO.findAll();
            
            // Total members
            report.put("totalMembers", allMembers.size());
            
            // Active members (those who have borrowed books)
            Set<String> activeMembers = allRecords.stream()
                .map(BorrowRecord::getMemberId)
                .collect(Collectors.toSet());
            report.put("activeMembers", activeMembers.size());
            
            // Membership type distribution
            Map<String, Long> membershipTypes = allMembers.stream()
                .collect(Collectors.groupingBy(Member::getMembershipType, Collectors.counting()));
            report.put("membershipTypeDistribution", membershipTypes);
            
            // Top borrowers (top 5)
            Map<String, Long> borrowCounts = allRecords.stream()
                .collect(Collectors.groupingBy(BorrowRecord::getMemberId, Collectors.counting()));
            
            List<Map.Entry<String, Long>> topBorrowers = borrowCounts.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(5)
                .collect(Collectors.toList());
            report.put("topBorrowers", topBorrowers);
            
            // Currently borrowed books
            long currentlyBorrowedBooks = allRecords.stream()
                .filter(record -> "BORROWED".equals(record.getStatus()))
                .count();
            report.put("currentlyBorrowedBooks", currentlyBorrowedBooks);
            
            logger.log("INFO", "Member activity report generated successfully");
            
        } catch (DatabaseException e) {
            report.put("error", "Failed to generate member activity report: " + e.getMessage());
            logger.log("ERROR", "Failed to generate member activity report: " + e.getMessage());
        }
        
        return report;
    }
    
    @Override
    public List<String> generateBookPopularityReport() {
        List<String> report = new ArrayList<>();
        report.add("=== BOOK POPULARITY REPORT ===");
        report.add("Generated on: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        report.add("");
        
        try {
            List<BorrowRecord> allRecords = borrowRecordDAO.findAll();
            
            // Count borrows per book
            Map<String, Long> borrowCounts = allRecords.stream()
                .collect(Collectors.groupingBy(BorrowRecord::getIsbn, Collectors.counting()));
            
            // Sort by popularity
            List<Map.Entry<String, Long>> sortedBooks = borrowCounts.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .collect(Collectors.toList());
            
            if (sortedBooks.isEmpty()) {
                report.add("No borrowing activity found.");
            } else {
                report.add(String.format("%-15s %-30s %-20s %-12s", 
                    "ISBN", "Title", "Author", "Borrow Count"));
                report.add("-".repeat(80));
                
                for (Map.Entry<String, Long> entry : sortedBooks) {
                    try {
                        Book book = bookDAO.findByIsbn(entry.getKey());
                        if (book != null) {
                            String title = book.getTitle();
                            if (title.length() > 28) {
                                title = title.substring(0, 28) + "..";
                            }
                            String author = book.getAuthor();
                            if (author.length() > 18) {
                                author = author.substring(0, 18) + "..";
                            }
                            
                            report.add(String.format("%-15s %-30s %-20s %-12d", 
                                entry.getKey(),
                                title,
                                author,
                                entry.getValue()));
                        }
                    } catch (DatabaseException e) {
                        logger.log("ERROR", "Error processing book popularity data: " + e.getMessage());
                    }
                }
            }
        } catch (DatabaseException e) {
            report.add("Error generating book popularity report: " + e.getMessage());
            logger.log("ERROR", "Failed to generate book popularity report: " + e.getMessage());
        }
        
        return report;
    }
    
    @Override
    public boolean exportReportToFile(List<String> reportData, String filename) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (String line : reportData) {
                writer.write(line);
                writer.newLine();
            }
            logger.log("INFO", "Report exported to file: " + filename);
            return true;
        } catch (IOException e) {
            logger.log("ERROR", "Failed to export report to file: " + e.getMessage());
            return false;
        }
    }
    
    // Additional reporting methods
    public List<String> generateLibraryStatisticsReport() {
        List<String> report = new ArrayList<>();
        report.add("=== LIBRARY STATISTICS REPORT ===");
        report.add("Generated on: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        report.add("");
        
        try {
            // Book statistics
            List<Book> allBooks = bookDAO.findAll();
            int totalBooks = allBooks.size();
            int availableBooks = (int) allBooks.stream().filter(Book::isAvailable).count();
            
            Set<String> uniqueAuthors = allBooks.stream()
                .map(Book::getAuthor)
                .collect(Collectors.toSet());
            
            Set<String> categories = allBooks.stream()
                .map(Book::getCategory)
                .collect(Collectors.toSet());
            
            report.add("BOOK INVENTORY:");
            report.add("Total Books: " + totalBooks);
            report.add("Available Books: " + availableBooks);
            report.add("Books Currently Borrowed: " + (totalBooks - availableBooks));
            report.add("Unique Authors: " + uniqueAuthors.size());
            report.add("Categories: " + categories.size());
            report.add("");
            
            // Member statistics
            List<Member> allMembers = memberDAO.findAll();
            int totalMembers = allMembers.size();
            int activeMembers = (int) allMembers.stream().filter(Member::isActive).count();
            
            report.add("MEMBERSHIP:");
            report.add("Total Members: " + totalMembers);
            report.add("Active Members: " + activeMembers);
            report.add("");
            
            // Borrowing statistics
            List<BorrowRecord> allRecords = borrowRecordDAO.findAll();
            long totalBorrows = allRecords.size();
            long activeBorrows = allRecords.stream()
                .filter(record -> "BORROWED".equals(record.getStatus()))
                .count();
            long overdueBooks = borrowRecordDAO.findOverdueRecords().size();
            
            report.add("BORROWING ACTIVITY:");
            report.add("Total Borrows (All Time): " + totalBorrows);
            report.add("Currently Borrowed: " + activeBorrows);
            report.add("Overdue Books: " + overdueBooks);
            
        } catch (DatabaseException e) {
            report.add("Error generating statistics: " + e.getMessage());
            logger.log("ERROR", "Failed to generate library statistics: " + e.getMessage());
        }
        
        return report;
    }
}
