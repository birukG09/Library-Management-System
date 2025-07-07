package com.library.model;

import com.library.interfaces.Searchable;
import java.time.LocalDate;

/**
 * Book model class implementing Searchable interface
 */
public class Book implements Searchable {
    private String isbn;
    private String title;
    private String author;
    private String category;
    private String publisher;
    private LocalDate publicationDate;
    private int totalCopies;
    private int availableCopies;
    private boolean isActive;
    
    public Book() {
        this.isActive = true;
    }
    
    public Book(String isbn, String title, String author, String category, String publisher, 
                LocalDate publicationDate, int totalCopies) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.category = category;
        this.publisher = publisher;
        this.publicationDate = publicationDate;
        this.totalCopies = totalCopies;
        this.availableCopies = totalCopies;
        this.isActive = true;
    }
    
    // Implementation of Searchable interface
    @Override
    public boolean matches(String searchTerm) {
        String lowerSearchTerm = searchTerm.toLowerCase();
        return isbn.toLowerCase().contains(lowerSearchTerm) ||
               title.toLowerCase().contains(lowerSearchTerm) ||
               author.toLowerCase().contains(lowerSearchTerm) ||
               category.toLowerCase().contains(lowerSearchTerm) ||
               publisher.toLowerCase().contains(lowerSearchTerm);
    }
    
    // Business logic methods
    public boolean isAvailable() {
        return isActive && availableCopies > 0;
    }
    
    public boolean borrowBook() {
        if (isAvailable()) {
            availableCopies--;
            return true;
        }
        return false;
    }
    
    public boolean returnBook() {
        if (availableCopies < totalCopies) {
            availableCopies++;
            return true;
        }
        return false;
    }
    
    // Getters and setters
    public String getIsbn() {
        return isbn;
    }
    
    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getAuthor() {
        return author;
    }
    
    public void setAuthor(String author) {
        this.author = author;
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    public String getPublisher() {
        return publisher;
    }
    
    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }
    
    public LocalDate getPublicationDate() {
        return publicationDate;
    }
    
    public void setPublicationDate(LocalDate publicationDate) {
        this.publicationDate = publicationDate;
    }
    
    public int getTotalCopies() {
        return totalCopies;
    }
    
    public void setTotalCopies(int totalCopies) {
        this.totalCopies = totalCopies;
    }
    
    public int getAvailableCopies() {
        return availableCopies;
    }
    
    public void setAvailableCopies(int availableCopies) {
        this.availableCopies = availableCopies;
    }
    
    public boolean isActive() {
        return isActive;
    }
    
    public void setActive(boolean active) {
        isActive = active;
    }
    
    @Override
    public String toString() {
        return "Book{" +
                "isbn='" + isbn + '\'' +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", category='" + category + '\'' +
                ", availableCopies=" + availableCopies +
                "/" + totalCopies +
                '}';
    }
}
