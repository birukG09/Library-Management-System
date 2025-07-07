package com.library.gui;

import com.library.service.BookService;
import com.library.service.MemberService;
import com.library.service.BorrowingService;
import com.library.service.ReportService;
import com.library.util.DatabaseConnection;
import com.library.util.FileLogger;
import com.library.exception.DatabaseException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Main GUI window for the Library Management System
 */
public class LibraryGUI extends JFrame {
    private final BookService bookService;
    private final MemberService memberService;
    private final BorrowingService borrowingService;
    private final ReportService reportService;
    private final FileLogger logger;
    
    // GUI Components
    private JPanel mainPanel;
    private JMenuBar menuBar;
    
    public LibraryGUI() {
        this.bookService = new BookService();
        this.memberService = new MemberService();
        this.borrowingService = new BorrowingService();
        this.reportService = new ReportService();
        this.logger = FileLogger.getInstance();
        
        initializeGUI();
    }
    
    private void initializeGUI() {
        setTitle("Library Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        
        // Create menu bar
        createMenuBar();
        
        // Create main panel
        createMainPanel();
        

    }
    
    private void createMenuBar() {
        menuBar = new JMenuBar();
        
        // Books Menu
        JMenu booksMenu = new JMenu("Books");
        JMenuItem addBook = new JMenuItem("Add Book");
        JMenuItem viewBooks = new JMenuItem("View All Books");
        JMenuItem searchBooks = new JMenuItem("Search Books");
        
        addBook.addActionListener(e -> openBookManagement());
        viewBooks.addActionListener(e -> openBookList());
        searchBooks.addActionListener(e -> openBookSearch());
        
        booksMenu.add(addBook);
        booksMenu.add(viewBooks);
        booksMenu.add(searchBooks);
        
        // Members Menu
        JMenu membersMenu = new JMenu("Members");
        JMenuItem addMember = new JMenuItem("Add Member");
        JMenuItem viewMembers = new JMenuItem("View All Members");
        JMenuItem searchMembers = new JMenuItem("Search Members");
        
        addMember.addActionListener(e -> openMemberManagement());
        viewMembers.addActionListener(e -> openMemberList());
        searchMembers.addActionListener(e -> openMemberSearch());
        
        membersMenu.add(addMember);
        membersMenu.add(viewMembers);
        membersMenu.add(searchMembers);
        
        // Borrowing Menu
        JMenu borrowingMenu = new JMenu("Borrowing");
        JMenuItem borrowBook = new JMenuItem("Borrow Book");
        JMenuItem returnBook = new JMenuItem("Return Book");
        JMenuItem viewBorrows = new JMenuItem("View Borrow Records");
        
        borrowBook.addActionListener(e -> openBorrowDialog());
        returnBook.addActionListener(e -> openReturnDialog());
        viewBorrows.addActionListener(e -> openBorrowList());
        
        borrowingMenu.add(borrowBook);
        borrowingMenu.add(returnBook);
        borrowingMenu.add(viewBorrows);
        
        // Reports Menu
        JMenu reportsMenu = new JMenu("Reports");
        JMenuItem overdueReport = new JMenuItem("Overdue Books");
        JMenuItem popularityReport = new JMenuItem("Book Popularity");
        JMenuItem statisticsReport = new JMenuItem("Library Statistics");
        
        overdueReport.addActionListener(e -> openOverdueReport());
        popularityReport.addActionListener(e -> openPopularityReport());
        statisticsReport.addActionListener(e -> openStatisticsReport());
        
        reportsMenu.add(overdueReport);
        reportsMenu.add(popularityReport);
        reportsMenu.add(statisticsReport);
        
        menuBar.add(booksMenu);
        menuBar.add(membersMenu);
        menuBar.add(borrowingMenu);
        menuBar.add(reportsMenu);
        
        setJMenuBar(menuBar);
    }
    
    private void createMainPanel() {
        mainPanel = new JPanel(new BorderLayout());
        
        // Header panel
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(70, 130, 180));
        headerPanel.setPreferredSize(new Dimension(getWidth(), 80));
        
        JLabel titleLabel = new JLabel("Library Management System");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        headerPanel.add(titleLabel);
        
        // Content panel with welcome message
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(Color.WHITE);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(20, 20, 20, 20);
        
        JLabel welcomeLabel = new JLabel("<html><center>Welcome to the Library Management System<br>" +
                "Use the menu bar above to access different features</center></html>");
        welcomeLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        contentPanel.add(welcomeLabel, gbc);
        
        // Quick action buttons
        gbc.gridy = 1;
        JPanel buttonPanel = new JPanel(new FlowLayout());
        
        JButton addBookBtn = new JButton("Add Book");
        JButton addMemberBtn = new JButton("Add Member");
        JButton borrowBookBtn = new JButton("Borrow Book");
        JButton viewBooksBtn = new JButton("View Books");
        
        addBookBtn.setPreferredSize(new Dimension(120, 35));
        addMemberBtn.setPreferredSize(new Dimension(120, 35));
        borrowBookBtn.setPreferredSize(new Dimension(120, 35));
        viewBooksBtn.setPreferredSize(new Dimension(120, 35));
        
        addBookBtn.addActionListener(e -> openBookManagement());
        addMemberBtn.addActionListener(e -> openMemberManagement());
        borrowBookBtn.addActionListener(e -> openBorrowDialog());
        viewBooksBtn.addActionListener(e -> openBookList());
        
        buttonPanel.add(addBookBtn);
        buttonPanel.add(addMemberBtn);
        buttonPanel.add(borrowBookBtn);
        buttonPanel.add(viewBooksBtn);
        
        contentPanel.add(buttonPanel, gbc);
        
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        
        add(mainPanel);
    }
    
    // Menu action methods
    private void openBookManagement() {
        new BookManagementDialog(this, bookService).setVisible(true);
    }
    
    private void openBookList() {
        new BookListDialog(this, bookService).setVisible(true);
    }
    
    private void openBookSearch() {
        new BookSearchDialog(this, bookService).setVisible(true);
    }
    
    private void openMemberManagement() {
        new MemberManagementDialog(this, memberService).setVisible(true);
    }
    
    private void openMemberList() {
        new MemberListDialog(this, memberService).setVisible(true);
    }
    
    private void openMemberSearch() {
        new MemberSearchDialog(this, memberService).setVisible(true);
    }
    
    private void openBorrowDialog() {
        new BorrowBookDialog(this, borrowingService, bookService, memberService).setVisible(true);
    }
    
    private void openReturnDialog() {
        new ReturnBookDialog(this, borrowingService).setVisible(true);
    }
    
    private void openBorrowList() {
        new BorrowListDialog(this, borrowingService).setVisible(true);
    }
    
    private void openOverdueReport() {
        new ReportDialog(this, reportService, "Overdue Books Report").setVisible(true);
    }
    
    private void openPopularityReport() {
        new ReportDialog(this, reportService, "Book Popularity Report").setVisible(true);
    }
    
    private void openStatisticsReport() {
        new ReportDialog(this, reportService, "Library Statistics Report").setVisible(true);
    }
    
    public static void main(String[] args) {
        try {
            // Initialize database
            DatabaseConnection.getInstance().initializeDatabase();
            
            SwingUtilities.invokeLater(() -> {
                new LibraryGUI().setVisible(true);
            });
        } catch (DatabaseException e) {
            JOptionPane.showMessageDialog(null, 
                "Failed to initialize database: " + e.getMessage(),
                "Database Error", 
                JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }
}