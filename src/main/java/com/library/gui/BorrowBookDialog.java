package com.library.gui;

import com.library.model.BorrowRecord;
import com.library.service.BorrowingService;
import com.library.service.BookService;
import com.library.service.MemberService;
import com.library.exception.DatabaseException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Dialog for borrowing books
 */
public class BorrowBookDialog extends JDialog {
    private final BorrowingService borrowingService;
    private final BookService bookService;
    private final MemberService memberService;
    
    private JTextField memberIdField;
    private JTextField isbnField;
    
    public BorrowBookDialog(Frame parent, BorrowingService borrowingService, 
                           BookService bookService, MemberService memberService) {
        super(parent, "Borrow Book", true);
        this.borrowingService = borrowingService;
        this.bookService = bookService;
        this.memberService = memberService;
        
        initializeDialog();
    }
    
    private void initializeDialog() {
        setSize(400, 200);
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout());
        
        // Create form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Member ID
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Member ID:"), gbc);
        gbc.gridx = 1;
        memberIdField = new JTextField(20);
        formPanel.add(memberIdField, gbc);
        
        // ISBN
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Book ISBN:"), gbc);
        gbc.gridx = 1;
        isbnField = new JTextField(20);
        formPanel.add(isbnField, gbc);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton borrowButton = new JButton("Borrow Book");
        JButton cancelButton = new JButton("Cancel");
        
        borrowButton.addActionListener(new BorrowButtonListener());
        cancelButton.addActionListener(e -> dispose());
        
        buttonPanel.add(borrowButton);
        buttonPanel.add(cancelButton);
        
        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private class BorrowButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String memberId = memberIdField.getText().trim();
            String isbn = isbnField.getText().trim();
            
            if (memberId.isEmpty() || isbn.isEmpty()) {
                JOptionPane.showMessageDialog(BorrowBookDialog.this,
                    "Please fill in both Member ID and Book ISBN.", 
                    "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            try {
                BorrowRecord record = borrowingService.borrowBook(memberId, isbn);
                
                String message = String.format(
                    "Book borrowed successfully!\n\n" +
                    "Record ID: %s\n" +
                    "Due Date: %s",
                    record.getRecordId(),
                    record.getDueDate().toString()
                );
                
                JOptionPane.showMessageDialog(BorrowBookDialog.this,
                    message, "Success", JOptionPane.INFORMATION_MESSAGE);
                
                dispose();
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(BorrowBookDialog.this,
                    "Error borrowing book: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}