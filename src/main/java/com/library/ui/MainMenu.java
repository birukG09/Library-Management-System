package com.library.ui;

import com.library.service.BookService;
import com.library.service.MemberService;
import com.library.service.BorrowingService;
import com.library.service.ReportService;
import com.library.model.Book;
import com.library.model.Member;
import com.library.model.BorrowRecord;
import com.library.exception.*;
import com.library.util.FileLogger;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Command Line Interface for the Library Management System
 * Provides a menu-driven interface for all system operations
 */
public class MainMenu {
    private final Scanner scanner;
    private final BookService bookService;
    private final MemberService memberService;
    private final BorrowingService borrowingService;
    private final ReportService reportService;
    private final FileLogger logger;
    
    public MainMenu() {
        this.scanner = new Scanner(System.in);
        this.bookService = new BookService();
        this.memberService = new MemberService();
        this.borrowingService = new BorrowingService();
        this.reportService = new ReportService();
        this.logger = FileLogger.getInstance();
    }
    
    public void displayMenu() {
        System.out.println("=== Welcome to Library Management System ===");
        
        while (true) {
            try {
                printMainMenu();
                int choice = getIntInput("Enter your choice: ");
                
                switch (choice) {
                    case 1:
                        handleBookManagement();
                        break;
                    case 2:
                        handleMemberManagement();
                        break;
                    case 3:
                        handleBorrowingOperations();
                        break;
                    case 4:
                        handleReports();
                        break;
                    case 5:
                        System.out.println("Thank you for using Library Management System!");
                        return;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
                
                System.out.println("\nPress Enter to continue...");
                scanner.nextLine();
                
            } catch (Exception e) {
                System.out.println("An error occurred: " + e.getMessage());
                logger.log("ERROR", "Menu error: " + e.getMessage());
                System.out.println("Press Enter to continue...");
                scanner.nextLine();
            }
        }
    }
    
    private void printMainMenu() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("               MAIN MENU");
        System.out.println("=".repeat(50));
        System.out.println("1. Book Management");
        System.out.println("2. Member Management");
        System.out.println("3. Borrowing Operations");
        System.out.println("4. Reports");
        System.out.println("5. Exit");
        System.out.println("=".repeat(50));
    }
    
    private void handleBookManagement() {
        while (true) {
            System.out.println("\n--- BOOK MANAGEMENT ---");
            System.out.println("1. Add New Book");
            System.out.println("2. Search Books");
            System.out.println("3. View All Books");
            System.out.println("4. Update Book");
            System.out.println("5. Delete Book");
            System.out.println("6. View Available Books");
            System.out.println("7. Back to Main Menu");
            
            int choice = getIntInput("Enter your choice: ");
            
            try {
                switch (choice) {
                    case 1:
                        addNewBook();
                        break;
                    case 2:
                        searchBooks();
                        break;
                    case 3:
                        viewAllBooks();
                        break;
                    case 4:
                        updateBook();
                        break;
                    case 5:
                        deleteBook();
                        break;
                    case 6:
                        viewAvailableBooks();
                        break;
                    case 7:
                        return;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }
    
    private void addNewBook() throws DatabaseException {
        System.out.println("\n--- ADD NEW BOOK ---");
        
        String isbn = getStringInput("Enter ISBN: ");
        String title = getStringInput("Enter Title: ");
        String author = getStringInput("Enter Author: ");
        String category = getStringInput("Enter Category: ");
        String publisher = getStringInput("Enter Publisher: ");
        LocalDate publicationDate = getDateInput("Enter Publication Date (yyyy-MM-dd): ");
        int totalCopies = getIntInput("Enter Total Copies: ");
        
        Book book = new Book(isbn, title, author, category, publisher, publicationDate, totalCopies);
        bookService.addBook(book);
        
        System.out.println("Book added successfully!");
    }
    
    private void searchBooks() throws DatabaseException {
        System.out.println("\n--- SEARCH BOOKS ---");
        String searchTerm = getStringInput("Enter search term (ISBN, title, author, or category): ");
        
        List<Book> books = bookService.searchBooks(searchTerm);
        
        if (books.isEmpty()) {
            System.out.println("No books found matching: " + searchTerm);
        } else {
            System.out.println("\nSearch Results:");
            displayBookList(books);
        }
    }
    
    private void viewAllBooks() throws DatabaseException {
        System.out.println("\n--- ALL BOOKS ---");
        List<Book> books = bookService.getAllBooks();
        
        if (books.isEmpty()) {
            System.out.println("No books found in the library.");
        } else {
            displayBookList(books);
        }
    }
    
    private void updateBook() throws DatabaseException, BookNotFoundException {
        System.out.println("\n--- UPDATE BOOK ---");
        String isbn = getStringInput("Enter ISBN of book to update: ");
        
        Book book = bookService.findBookByIsbn(isbn);
        System.out.println("Current book details:");
        System.out.println(book);
        
        System.out.println("\nEnter new details (press Enter to keep current value):");
        
        String title = getOptionalStringInput("Title [" + book.getTitle() + "]: ");
        if (!title.isEmpty()) book.setTitle(title);
        
        String author = getOptionalStringInput("Author [" + book.getAuthor() + "]: ");
        if (!author.isEmpty()) book.setAuthor(author);
        
        String category = getOptionalStringInput("Category [" + book.getCategory() + "]: ");
        if (!category.isEmpty()) book.setCategory(category);
        
        String publisher = getOptionalStringInput("Publisher [" + book.getPublisher() + "]: ");
        if (!publisher.isEmpty()) book.setPublisher(publisher);
        
        String totalCopiesStr = getOptionalStringInput("Total Copies [" + book.getTotalCopies() + "]: ");
        if (!totalCopiesStr.isEmpty()) {
            book.setTotalCopies(Integer.parseInt(totalCopiesStr));
        }
        
        bookService.updateBook(book);
        System.out.println("Book updated successfully!");
    }
    
    private void deleteBook() throws DatabaseException, BookNotFoundException {
        System.out.println("\n--- DELETE BOOK ---");
        String isbn = getStringInput("Enter ISBN of book to delete: ");
        
        Book book = bookService.findBookByIsbn(isbn);
        System.out.println("Book to delete:");
        System.out.println(book);
        
        String confirm = getStringInput("Are you sure you want to delete this book? (yes/no): ");
        if ("yes".equalsIgnoreCase(confirm)) {
            bookService.deleteBook(isbn);
            System.out.println("Book deleted successfully!");
        } else {
            System.out.println("Delete operation cancelled.");
        }
    }
    
    private void viewAvailableBooks() throws DatabaseException {
        System.out.println("\n--- AVAILABLE BOOKS ---");
        List<Book> books = bookService.getAvailableBooks();
        
        if (books.isEmpty()) {
            System.out.println("No books are currently available.");
        } else {
            displayBookList(books);
        }
    }
    
    private void handleMemberManagement() {
        while (true) {
            System.out.println("\n--- MEMBER MANAGEMENT ---");
            System.out.println("1. Register New Member");
            System.out.println("2. Search Members");
            System.out.println("3. View All Members");
            System.out.println("4. Update Member");
            System.out.println("5. Delete Member");
            System.out.println("6. View Member Details");
            System.out.println("7. Back to Main Menu");
            
            int choice = getIntInput("Enter your choice: ");
            
            try {
                switch (choice) {
                    case 1:
                        registerNewMember();
                        break;
                    case 2:
                        searchMembers();
                        break;
                    case 3:
                        viewAllMembers();
                        break;
                    case 4:
                        updateMember();
                        break;
                    case 5:
                        deleteMember();
                        break;
                    case 6:
                        viewMemberDetails();
                        break;
                    case 7:
                        return;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }
    
    private void registerNewMember() throws DatabaseException {
        System.out.println("\n--- REGISTER NEW MEMBER ---");
        
        String id = getStringInput("Enter Member ID: ");
        String firstName = getStringInput("Enter First Name: ");
        String lastName = getStringInput("Enter Last Name: ");
        String email = getStringInput("Enter Email: ");
        String phone = getStringInput("Enter Phone: ");
        
        System.out.println("Membership Types: STANDARD, PREMIUM, STUDENT");
        String membershipType = getStringInput("Enter Membership Type: ").toUpperCase();
        
        Member member = new Member(id, firstName, lastName, email, phone, membershipType);
        memberService.registerMember(member);
        
        System.out.println("Member registered successfully!");
    }
    
    private void searchMembers() throws DatabaseException {
        System.out.println("\n--- SEARCH MEMBERS ---");
        String searchTerm = getStringInput("Enter search term (ID, name, or email): ");
        
        List<Member> members = memberService.searchMembers(searchTerm);
        
        if (members.isEmpty()) {
            System.out.println("No members found matching: " + searchTerm);
        } else {
            System.out.println("\nSearch Results:");
            displayMemberList(members);
        }
    }
    
    private void viewAllMembers() throws DatabaseException {
        System.out.println("\n--- ALL MEMBERS ---");
        List<Member> members = memberService.getAllMembers();
        
        if (members.isEmpty()) {
            System.out.println("No members found.");
        } else {
            displayMemberList(members);
        }
    }
    
    private void updateMember() throws DatabaseException, MemberNotFoundException {
        System.out.println("\n--- UPDATE MEMBER ---");
        String id = getStringInput("Enter Member ID to update: ");
        
        Member member = memberService.findMemberById(id);
        System.out.println("Current member details:");
        System.out.println(member);
        
        System.out.println("\nEnter new details (press Enter to keep current value):");
        
        String firstName = getOptionalStringInput("First Name [" + member.getFirstName() + "]: ");
        if (!firstName.isEmpty()) member.setFirstName(firstName);
        
        String lastName = getOptionalStringInput("Last Name [" + member.getLastName() + "]: ");
        if (!lastName.isEmpty()) member.setLastName(lastName);
        
        String email = getOptionalStringInput("Email [" + member.getEmail() + "]: ");
        if (!email.isEmpty()) member.setEmail(email);
        
        String phone = getOptionalStringInput("Phone [" + member.getPhone() + "]: ");
        if (!phone.isEmpty()) member.setPhone(phone);
        
        memberService.updateMember(member);
        System.out.println("Member updated successfully!");
    }
    
    private void deleteMember() throws DatabaseException, MemberNotFoundException {
        System.out.println("\n--- DELETE MEMBER ---");
        String id = getStringInput("Enter Member ID to delete: ");
        
        Member member = memberService.findMemberById(id);
        System.out.println("Member to delete:");
        System.out.println(member);
        
        String confirm = getStringInput("Are you sure you want to delete this member? (yes/no): ");
        if ("yes".equalsIgnoreCase(confirm)) {
            memberService.deleteMember(id);
            System.out.println("Member deleted successfully!");
        } else {
            System.out.println("Delete operation cancelled.");
        }
    }
    
    private void viewMemberDetails() throws DatabaseException, MemberNotFoundException {
        System.out.println("\n--- MEMBER DETAILS ---");
        String id = getStringInput("Enter Member ID: ");
        
        Member member = memberService.findMemberById(id);
        System.out.println("\nMember Information:");
        System.out.println(member);
        
        // Show borrowing history
        List<BorrowRecord> borrowHistory = borrowingService.getMemberBorrowHistory(id);
        System.out.println("\nBorrowing History:");
        if (borrowHistory.isEmpty()) {
            System.out.println("No borrowing history found.");
        } else {
            displayBorrowRecordList(borrowHistory);
        }
    }
    
    private void handleBorrowingOperations() {
        while (true) {
            System.out.println("\n--- BORROWING OPERATIONS ---");
            System.out.println("1. Borrow Book");
            System.out.println("2. Return Book");
            System.out.println("3. View Member's Active Borrows");
            System.out.println("4. View All Borrow Records");
            System.out.println("5. View Overdue Books");
            System.out.println("6. Back to Main Menu");
            
            int choice = getIntInput("Enter your choice: ");
            
            try {
                switch (choice) {
                    case 1:
                        borrowBook();
                        break;
                    case 2:
                        returnBook();
                        break;
                    case 3:
                        viewMemberActiveBorrows();
                        break;
                    case 4:
                        viewAllBorrowRecords();
                        break;
                    case 5:
                        viewOverdueBooks();
                        break;
                    case 6:
                        return;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }
    
    private void borrowBook() throws DatabaseException, BookNotFoundException, MemberNotFoundException {
        System.out.println("\n--- BORROW BOOK ---");
        
        String memberId = getStringInput("Enter Member ID: ");
        String isbn = getStringInput("Enter Book ISBN: ");
        
        BorrowRecord record = borrowingService.borrowBook(memberId, isbn);
        
        System.out.println("Book borrowed successfully!");
        System.out.println("Record ID: " + record.getRecordId());
        System.out.println("Due Date: " + record.getDueDate());
    }
    
    private void returnBook() throws DatabaseException {
        System.out.println("\n--- RETURN BOOK ---");
        
        String recordId = getStringInput("Enter Borrow Record ID: ");
        
        BorrowRecord record = borrowingService.returnBook(recordId);
        
        System.out.println("Book returned successfully!");
        if (record.getFineAmount() > 0) {
            System.out.println("Fine Amount: $" + String.format("%.2f", record.getFineAmount()));
        }
    }
    
    private void viewMemberActiveBorrows() throws DatabaseException {
        System.out.println("\n--- MEMBER'S ACTIVE BORROWS ---");
        
        String memberId = getStringInput("Enter Member ID: ");
        
        List<BorrowRecord> activeBorrows = borrowingService.getMemberActiveBorrows(memberId);
        
        if (activeBorrows.isEmpty()) {
            System.out.println("No active borrows found for member: " + memberId);
        } else {
            displayBorrowRecordList(activeBorrows);
        }
    }
    
    private void viewAllBorrowRecords() throws DatabaseException {
        System.out.println("\n--- ALL BORROW RECORDS ---");
        
        List<BorrowRecord> allRecords = borrowingService.getAllBorrowRecords();
        
        if (allRecords.isEmpty()) {
            System.out.println("No borrow records found.");
        } else {
            displayBorrowRecordList(allRecords);
        }
    }
    
    private void viewOverdueBooks() throws DatabaseException {
        System.out.println("\n--- OVERDUE BOOKS ---");
        
        List<BorrowRecord> overdueRecords = borrowingService.getOverdueRecords();
        
        if (overdueRecords.isEmpty()) {
            System.out.println("No overdue books found.");
        } else {
            displayBorrowRecordList(overdueRecords);
            
            double totalFines = borrowingService.calculateTotalFines(overdueRecords);
            System.out.println("\nTotal Outstanding Fines: $" + String.format("%.2f", totalFines));
        }
    }
    
    private void handleReports() {
        while (true) {
            System.out.println("\n--- REPORTS ---");
            System.out.println("1. Overdue Books Report");
            System.out.println("2. Member Activity Report");
            System.out.println("3. Book Popularity Report");
            System.out.println("4. Library Statistics Report");
            System.out.println("5. Export Report to File");
            System.out.println("6. Back to Main Menu");
            
            int choice = getIntInput("Enter your choice: ");
            
            try {
                switch (choice) {
                    case 1:
                        generateOverdueReport();
                        break;
                    case 2:
                        generateMemberActivityReport();
                        break;
                    case 3:
                        generateBookPopularityReport();
                        break;
                    case 4:
                        generateLibraryStatisticsReport();
                        break;
                    case 5:
                        exportReportToFile();
                        break;
                    case 6:
                        return;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }
    
    private void generateOverdueReport() {
        System.out.println("\n--- OVERDUE BOOKS REPORT ---");
        
        List<String> report = reportService.generateOverdueReport();
        for (String line : report) {
            System.out.println(line);
        }
    }
    
    private void generateMemberActivityReport() {
        System.out.println("\n--- MEMBER ACTIVITY REPORT ---");
        
        Map<String, Object> report = reportService.generateMemberActivityReport();
        
        System.out.println("Total Members: " + report.get("totalMembers"));
        System.out.println("Active Members: " + report.get("activeMembers"));
        System.out.println("Currently Borrowed Books: " + report.get("currentlyBorrowedBooks"));
        
        @SuppressWarnings("unchecked")
        Map<String, Long> membershipTypes = (Map<String, Long>) report.get("membershipTypeDistribution");
        System.out.println("\nMembership Type Distribution:");
        for (Map.Entry<String, Long> entry : membershipTypes.entrySet()) {
            System.out.println("  " + entry.getKey() + ": " + entry.getValue());
        }
        
        @SuppressWarnings("unchecked")
        List<Map.Entry<String, Long>> topBorrowers = (List<Map.Entry<String, Long>>) report.get("topBorrowers");
        System.out.println("\nTop Borrowers:");
        for (Map.Entry<String, Long> entry : topBorrowers) {
            System.out.println("  " + entry.getKey() + ": " + entry.getValue() + " books");
        }
    }
    
    private void generateBookPopularityReport() {
        System.out.println("\n--- BOOK POPULARITY REPORT ---");
        
        List<String> report = reportService.generateBookPopularityReport();
        for (String line : report) {
            System.out.println(line);
        }
    }
    
    private void generateLibraryStatisticsReport() {
        System.out.println("\n--- LIBRARY STATISTICS REPORT ---");
        
        List<String> report = reportService.generateLibraryStatisticsReport();
        for (String line : report) {
            System.out.println(line);
        }
    }
    
    private void exportReportToFile() {
        System.out.println("\n--- EXPORT REPORT TO FILE ---");
        System.out.println("1. Overdue Books Report");
        System.out.println("2. Book Popularity Report");
        System.out.println("3. Library Statistics Report");
        
        int reportChoice = getIntInput("Select report to export: ");
        String filename = getStringInput("Enter filename (without extension): ") + ".txt";
        
        List<String> reportData = null;
        
        switch (reportChoice) {
            case 1:
                reportData = reportService.generateOverdueReport();
                break;
            case 2:
                reportData = reportService.generateBookPopularityReport();
                break;
            case 3:
                reportData = reportService.generateLibraryStatisticsReport();
                break;
            default:
                System.out.println("Invalid report choice.");
                return;
        }
        
        if (reportService.exportReportToFile(reportData, filename)) {
            System.out.println("Report exported successfully to: " + filename);
        } else {
            System.out.println("Failed to export report.");
        }
    }
    
    // Helper methods for displaying data
    private void displayBookList(List<Book> books) {
        System.out.println(String.format("%-15s %-30s %-20s %-15s %-10s", 
            "ISBN", "Title", "Author", "Category", "Available"));
        System.out.println("-".repeat(95));
        
        for (Book book : books) {
            String title = book.getTitle();
            if (title.length() > 28) {
                title = title.substring(0, 28) + "..";
            }
            String author = book.getAuthor();
            if (author.length() > 18) {
                author = author.substring(0, 18) + "..";
            }
            
            System.out.println(String.format("%-15s %-30s %-20s %-15s %-10s", 
                book.getIsbn(),
                title,
                author,
                book.getCategory(),
                book.getAvailableCopies() + "/" + book.getTotalCopies()));
        }
    }
    
    private void displayMemberList(List<Member> members) {
        System.out.println(String.format("%-15s %-25s %-25s %-15s %-10s", 
            "ID", "Name", "Email", "Membership", "Status"));
        System.out.println("-".repeat(95));
        
        for (Member member : members) {
            String email = member.getEmail();
            if (email.length() > 23) {
                email = email.substring(0, 23) + "..";
            }
            
            System.out.println(String.format("%-15s %-25s %-25s %-15s %-10s", 
                member.getId(),
                member.getFullName(),
                email,
                member.getMembershipType(),
                member.isActive() ? "Active" : "Inactive"));
        }
    }
    
    private void displayBorrowRecordList(List<BorrowRecord> records) {
        System.out.println(String.format("%-20s %-15s %-15s %-12s %-12s %-10s %-8s", 
            "Record ID", "Member ID", "ISBN", "Borrow Date", "Due Date", "Status", "Fine"));
        System.out.println("-".repeat(100));
        
        for (BorrowRecord record : records) {
            String recordId = record.getRecordId();
            if (recordId.length() > 18) {
                recordId = recordId.substring(0, 18) + "..";
            }
            
            String returnDate = record.getReturnDate() != null ? record.getReturnDate().toString() : "-";
            
            System.out.println(String.format("%-20s %-15s %-15s %-12s %-12s %-10s $%-7.2f", 
                recordId,
                record.getMemberId(),
                record.getIsbn(),
                record.getBorrowDate().toString(),
                record.getDueDate().toString(),
                record.getStatus(),
                record.getFineAmount()));
        }
    }
    
    // Input validation helper methods
    private String getStringInput(String prompt) {
        System.out.print(prompt);
        String input = scanner.nextLine().trim();
        while (input.isEmpty()) {
            System.out.print("Input cannot be empty. " + prompt);
            input = scanner.nextLine().trim();
        }
        return input;
    }
    
    private String getOptionalStringInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }
    
    private int getIntInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                String input = scanner.nextLine().trim();
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }
    
    private LocalDate getDateInput(String prompt) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        while (true) {
            try {
                System.out.print(prompt);
                String input = scanner.nextLine().trim();
                return LocalDate.parse(input, formatter);
            } catch (DateTimeParseException e) {
                System.out.println("Please enter date in format yyyy-MM-dd (e.g., 2023-12-25).");
            }
        }
    }
}
