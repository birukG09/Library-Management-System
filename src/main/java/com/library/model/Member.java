package com.library.model;

import com.library.interfaces.Searchable;
import java.time.LocalDate;

/**
 * Member class extending Person - demonstrates inheritance
 * Implements Searchable interface
 */
public class Member extends Person implements Searchable {
    private String membershipType;
    private LocalDate membershipExpiry;
    private int borrowedBooksCount;
    private boolean isActive;
    
    public Member() {
        super();
        this.membershipType = "STANDARD";
        this.membershipExpiry = LocalDate.now().plusYears(1);
        this.borrowedBooksCount = 0;
        this.isActive = true;
    }
    
    public Member(String id, String firstName, String lastName, String email, String phone, String membershipType) {
        super(id, firstName, lastName, email, phone);
        this.membershipType = membershipType;
        this.membershipExpiry = LocalDate.now().plusYears(1);
        this.borrowedBooksCount = 0;
        this.isActive = true;
    }
    
    // Method overriding from Person class
    @Override
    public String getPersonType() {
        return "Member";
    }
    
    // Implementation of Searchable interface
    @Override
    public boolean matches(String searchTerm) {
        String lowerSearchTerm = searchTerm.toLowerCase();
        return getId().toLowerCase().contains(lowerSearchTerm) ||
               getFirstName().toLowerCase().contains(lowerSearchTerm) ||
               getLastName().toLowerCase().contains(lowerSearchTerm) ||
               getEmail().toLowerCase().contains(lowerSearchTerm) ||
               membershipType.toLowerCase().contains(lowerSearchTerm);
    }
    
    // Business logic methods
    public boolean canBorrowBooks() {
        return isActive && membershipExpiry.isAfter(LocalDate.now()) && borrowedBooksCount < getMaxBorrowLimit();
    }
    
    public int getMaxBorrowLimit() {
        switch (membershipType.toUpperCase()) {
            case "PREMIUM":
                return 10;
            case "STUDENT":
                return 5;
            default:
                return 3;
        }
    }
    
    // Getters and setters
    public String getMembershipType() {
        return membershipType;
    }
    
    public void setMembershipType(String membershipType) {
        this.membershipType = membershipType;
    }
    
    public LocalDate getMembershipExpiry() {
        return membershipExpiry;
    }
    
    public void setMembershipExpiry(LocalDate membershipExpiry) {
        this.membershipExpiry = membershipExpiry;
    }
    
    public int getBorrowedBooksCount() {
        return borrowedBooksCount;
    }
    
    public void setBorrowedBooksCount(int borrowedBooksCount) {
        this.borrowedBooksCount = borrowedBooksCount;
    }
    
    public boolean isActive() {
        return isActive;
    }
    
    public void setActive(boolean active) {
        isActive = active;
    }
    
    @Override
    public String toString() {
        return "Member{" +
                "id='" + getId() + '\'' +
                ", name='" + getFullName() + '\'' +
                ", membershipType='" + membershipType + '\'' +
                ", membershipExpiry=" + membershipExpiry +
                ", borrowedBooksCount=" + borrowedBooksCount +
                ", isActive=" + isActive +
                '}';
    }
}
