package com.library.base;

import java.time.LocalDateTime;

/**
 * Abstract class representing a user account in the library system
 * Demonstrates partial abstraction
 */
public abstract class Account {
    protected String accountId;
    protected String username;
    protected String passwordHash;
    protected LocalDateTime lastLoginTime;
    protected boolean isLocked;
    protected int failedLoginAttempts;
    
    public Account() {
        this.isLocked = false;
        this.failedLoginAttempts = 0;
    }
    
    public Account(String accountId, String username, String passwordHash) {
        this();
        this.accountId = accountId;
        this.username = username;
        this.passwordHash = passwordHash;
    }
    
    // Abstract methods to be implemented by subclasses
    public abstract boolean validateCredentials(String username, String password);
    public abstract String getAccountType();
    public abstract boolean hasPermission(String operation);
    
    // Concrete methods with common functionality
    public void recordSuccessfulLogin() {
        this.lastLoginTime = LocalDateTime.now();
        this.failedLoginAttempts = 0;
        this.isLocked = false;
    }
    
    public void recordFailedLogin() {
        this.failedLoginAttempts++;
        if (this.failedLoginAttempts >= 3) {
            this.isLocked = true;
        }
    }
    
    public boolean isAccountLocked() {
        return isLocked;
    }
    
    public void unlockAccount() {
        this.isLocked = false;
        this.failedLoginAttempts = 0;
    }
    
    // Getters and setters
    public String getAccountId() {
        return accountId;
    }
    
    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getPasswordHash() {
        return passwordHash;
    }
    
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }
    
    public LocalDateTime getLastLoginTime() {
        return lastLoginTime;
    }
    
    public void setLastLoginTime(LocalDateTime lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }
    
    public int getFailedLoginAttempts() {
        return failedLoginAttempts;
    }
    
    public void setFailedLoginAttempts(int failedLoginAttempts) {
        this.failedLoginAttempts = failedLoginAttempts;
    }
}
