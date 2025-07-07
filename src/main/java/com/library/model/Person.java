package com.library.model;

import java.time.LocalDate;

/**
 * Abstract base class representing a person in the library system
 * Demonstrates inheritance and abstraction
 */
public abstract class Person {
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private LocalDate registrationDate;
    
    // Constructor
    public Person(String id, String firstName, String lastName, String email, String phone) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.registrationDate = LocalDate.now();
    }
    
    // Default constructor
    public Person() {
        this.registrationDate = LocalDate.now();
    }
    
    // Abstract method to be implemented by subclasses
    public abstract String getPersonType();
    
    // Method overloading demonstration
    public String getFullName() {
        return firstName + " " + lastName;
    }
    
    public String getFullName(boolean includeTitle) {
        if (includeTitle) {
            return getPersonType() + " " + firstName + " " + lastName;
        }
        return getFullName();
    }
    
    // Getters and setters (encapsulation)
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getFirstName() {
        return firstName;
    }
    
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    public String getLastName() {
        return lastName;
    }
    
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public LocalDate getRegistrationDate() {
        return registrationDate;
    }
    
    public void setRegistrationDate(LocalDate registrationDate) {
        this.registrationDate = registrationDate;
    }
    
    @Override
    public String toString() {
        return "Person{" +
                "id='" + id + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", registrationDate=" + registrationDate +
                '}';
    }
}
