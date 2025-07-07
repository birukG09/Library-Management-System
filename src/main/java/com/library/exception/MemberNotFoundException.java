package com.library.exception;

/**
 * Custom exception for member not found scenarios
 */
public class MemberNotFoundException extends LibraryException {
    
    public MemberNotFoundException(String message) {
        super(message, "MEMBER_NOT_FOUND");
    }
    
    public MemberNotFoundException(String message, Throwable cause) {
        super(message, "MEMBER_NOT_FOUND", cause);
    }
    
    public static MemberNotFoundException forId(String memberId) {
        return new MemberNotFoundException("Member not found with ID: " + memberId);
    }
}
