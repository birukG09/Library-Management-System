package com.library.gui;

import com.library.model.BorrowRecord;
import com.library.service.BorrowingService;
import com.library.exception.DatabaseException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Dialog for returning books
 */
public class ReturnBookDialog extends JDialog {
    private final BorrowingService borrowingService;
    private JTextField recordIdField;
    
    public ReturnBookDialog(Frame parent, BorrowingService borrowingService) {
        super(parent, "Return Book", true);
        this.borrowingService = borrowingService;
        
        initializeDialog();
    }
    
    private void initializeDialog() {
        setSize(400, 150);
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout());
        
        // Create form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Record ID
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Borrow Record ID:"), gbc);
        gbc.gridx = 1;
        recordIdField = new JTextField(20);
        formPanel.add(recordIdField, gbc);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton returnButton = new JButton("Return Book");
        JButton cancelButton = new JButton("Cancel");
        
        returnButton.addActionListener(new ReturnButtonListener());
        cancelButton.addActionListener(e -> dispose());
        
        buttonPanel.add(returnButton);
        buttonPanel.add(cancelButton);
        
        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private class ReturnButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String recordId = recordIdField.getText().trim();
            
            if (recordId.isEmpty()) {
                JOptionPane.showMessageDialog(ReturnBookDialog.this,
                    "Please enter the Borrow Record ID.", 
                    "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            try {
                BorrowRecord record = borrowingService.returnBook(recordId);
                
                String message = String.format(
                    "Book returned successfully!\n\n" +
                    "Record ID: %s\n" +
                    "Return Date: %s\n" +
                    "Fine Amount: $%.2f",
                    record.getRecordId(),
                    record.getReturnDate().toString(),
                    record.getFineAmount()
                );
                
                JOptionPane.showMessageDialog(ReturnBookDialog.this,
                    message, "Success", JOptionPane.INFORMATION_MESSAGE);
                
                dispose();
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(ReturnBookDialog.this,
                    "Error returning book: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}