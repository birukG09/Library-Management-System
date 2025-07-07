package com.library.model;

import java.time.LocalDate;

/**
 * BorrowRecord model class to track book borrowing transactions
 */
public class BorrowRecord {
    private String recordId;
    private String memberId;
    private String isbn;
    private LocalDate borrowDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private String status; // BORROWED, RETURNED, OVERDUE
    private double fineAmount;
    
    public BorrowRecord() {
        this.borrowDate = LocalDate.now();
        this.dueDate = LocalDate.now().plusDays(14); // 2 weeks borrowing period
        this.status = "BORROWED";
        this.fineAmount = 0.0;
    }
    
    public BorrowRecord(String recordId, String memberId, String isbn) {
        this();
        this.recordId = recordId;
        this.memberId = memberId;
        this.isbn = isbn;
    }
    
    // Business logic methods
    public boolean isOverdue() {
        return returnDate == null && LocalDate.now().isAfter(dueDate);
    }
    
    public long getDaysOverdue() {
        if (isOverdue()) {
            return LocalDate.now().toEpochDay() - dueDate.toEpochDay();
        }
        return 0;
    }
    
    public double calculateFine() {
        if (isOverdue()) {
            long daysOverdue = getDaysOverdue();
            fineAmount = daysOverdue * 0.50; // $0.50 per day fine
        }
        return fineAmount;
    }
    
    public void returnBook() {
        this.returnDate = LocalDate.now();
        this.status = "RETURNED";
        calculateFine();
    }
    
    // Getters and setters
    public String getRecordId() {
        return recordId;
    }
    
    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }
    
    public String getMemberId() {
        return memberId;
    }
    
    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }
    
    public String getIsbn() {
        return isbn;
    }
    
    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }
    
    public LocalDate getBorrowDate() {
        return borrowDate;
    }
    
    public void setBorrowDate(LocalDate borrowDate) {
        this.borrowDate = borrowDate;
    }
    
    public LocalDate getDueDate() {
        return dueDate;
    }
    
    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }
    
    public LocalDate getReturnDate() {
        return returnDate;
    }
    
    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public double getFineAmount() {
        return fineAmount;
    }
    
    public void setFineAmount(double fineAmount) {
        this.fineAmount = fineAmount;
    }
    
    @Override
    public String toString() {
        return "BorrowRecord{" +
                "recordId='" + recordId + '\'' +
                ", memberId='" + memberId + '\'' +
                ", isbn='" + isbn + '\'' +
                ", borrowDate=" + borrowDate +
                ", dueDate=" + dueDate +
                ", returnDate=" + returnDate +
                ", status='" + status + '\'' +
                ", fineAmount=" + fineAmount +
                '}';
    }
}
