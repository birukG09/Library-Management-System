package com.library.model;

/**
 * Librarian class extending Person - demonstrates inheritance
 */
public class Librarian extends Person {
    private String employeeId;
    private String department;
    private double salary;
    private String accessLevel;
    
    public Librarian() {
        super();
        this.accessLevel = "STANDARD";
    }
    
    public Librarian(String id, String firstName, String lastName, String email, String phone, 
                    String employeeId, String department, double salary) {
        super(id, firstName, lastName, email, phone);
        this.employeeId = employeeId;
        this.department = department;
        this.salary = salary;
        this.accessLevel = "STANDARD";
    }
    
    // Method overriding from Person class
    @Override
    public String getPersonType() {
        return "Librarian";
    }
    
    // Method overloading for salary operations
    public void setSalary(double salary) {
        this.salary = salary;
    }
    
    public void setSalary(double salary, String currency) {
        this.salary = salary;
        // Could implement currency conversion logic here
    }
    
    // Getters and setters
    public String getEmployeeId() {
        return employeeId;
    }
    
    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }
    
    public String getDepartment() {
        return department;
    }
    
    public void setDepartment(String department) {
        this.department = department;
    }
    
    public double getSalary() {
        return salary;
    }
    
    public String getAccessLevel() {
        return accessLevel;
    }
    
    public void setAccessLevel(String accessLevel) {
        this.accessLevel = accessLevel;
    }
    
    @Override
    public String toString() {
        return "Librarian{" +
                "id='" + getId() + '\'' +
                ", name='" + getFullName() + '\'' +
                ", employeeId='" + employeeId + '\'' +
                ", department='" + department + '\'' +
                ", accessLevel='" + accessLevel + '\'' +
                '}';
    }
}
