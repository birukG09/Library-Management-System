package com.library.gui;

import com.library.model.Book;
import com.library.service.BookService;
import com.library.exception.DatabaseException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Dialog for adding and editing books
 */
public class BookManagementDialog extends JDialog {
    private final BookService bookService;
    private Book currentBook;
    
    // Form fields
    private JTextField isbnField;
    private JTextField titleField;
    private JTextField authorField;
    private JTextField categoryField;
    private JTextField publisherField;
    private JTextField publicationDateField;
    private JSpinner totalCopiesSpinner;
    private JSpinner availableCopiesSpinner;
    private JCheckBox activeCheckBox;
    
    public BookManagementDialog(Frame parent, BookService bookService) {
        this(parent, bookService, null);
    }
    
    public BookManagementDialog(Frame parent, BookService bookService, Book book) {
        super(parent, book == null ? "Add New Book" : "Edit Book", true);
        this.bookService = bookService;
        this.currentBook = book;
        
        initializeDialog();
        if (book != null) {
            populateFields(book);
        }
    }
    
    private void initializeDialog() {
        setSize(450, 400);
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout());
        
        // Create form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // ISBN
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("ISBN:"), gbc);
        gbc.gridx = 1;
        isbnField = new JTextField(20);
        formPanel.add(isbnField, gbc);
        
        // Title
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Title:"), gbc);
        gbc.gridx = 1;
        titleField = new JTextField(20);
        formPanel.add(titleField, gbc);
        
        // Author
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Author:"), gbc);
        gbc.gridx = 1;
        authorField = new JTextField(20);
        formPanel.add(authorField, gbc);
        
        // Category
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Category:"), gbc);
        gbc.gridx = 1;
        categoryField = new JTextField(20);
        formPanel.add(categoryField, gbc);
        
        // Publisher
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Publisher:"), gbc);
        gbc.gridx = 1;
        publisherField = new JTextField(20);
        formPanel.add(publisherField, gbc);
        
        // Publication Date
        gbc.gridx = 0; gbc.gridy = 5;
        formPanel.add(new JLabel("Publication Date (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1;
        publicationDateField = new JTextField(20);
        formPanel.add(publicationDateField, gbc);
        
        // Total Copies
        gbc.gridx = 0; gbc.gridy = 6;
        formPanel.add(new JLabel("Total Copies:"), gbc);
        gbc.gridx = 1;
        totalCopiesSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 9999, 1));
        formPanel.add(totalCopiesSpinner, gbc);
        
        // Available Copies
        gbc.gridx = 0; gbc.gridy = 7;
        formPanel.add(new JLabel("Available Copies:"), gbc);
        gbc.gridx = 1;
        availableCopiesSpinner = new JSpinner(new SpinnerNumberModel(1, 0, 9999, 1));
        formPanel.add(availableCopiesSpinner, gbc);
        
        // Active
        gbc.gridx = 0; gbc.gridy = 8;
        formPanel.add(new JLabel("Active:"), gbc);
        gbc.gridx = 1;
        activeCheckBox = new JCheckBox();
        activeCheckBox.setSelected(true);
        formPanel.add(activeCheckBox, gbc);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton saveButton = new JButton(currentBook == null ? "Add Book" : "Update Book");
        JButton cancelButton = new JButton("Cancel");
        
        saveButton.addActionListener(new SaveButtonListener());
        cancelButton.addActionListener(e -> dispose());
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void populateFields(Book book) {
        isbnField.setText(book.getIsbn());
        isbnField.setEditable(false); // ISBN shouldn't be editable for existing books
        titleField.setText(book.getTitle());
        authorField.setText(book.getAuthor());
        categoryField.setText(book.getCategory());
        publisherField.setText(book.getPublisher());
        publicationDateField.setText(book.getPublicationDate().toString());
        totalCopiesSpinner.setValue(book.getTotalCopies());
        availableCopiesSpinner.setValue(book.getAvailableCopies());
        activeCheckBox.setSelected(book.isActive());
    }
    
    private class SaveButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                // Validate input
                if (isbnField.getText().trim().isEmpty() ||
                    titleField.getText().trim().isEmpty() ||
                    authorField.getText().trim().isEmpty() ||
                    categoryField.getText().trim().isEmpty() ||
                    publisherField.getText().trim().isEmpty() ||
                    publicationDateField.getText().trim().isEmpty()) {
                    
                    JOptionPane.showMessageDialog(BookManagementDialog.this,
                        "Please fill in all fields.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Parse date
                LocalDate publicationDate;
                try {
                    publicationDate = LocalDate.parse(publicationDateField.getText().trim(),
                        DateTimeFormatter.ISO_LOCAL_DATE);
                } catch (DateTimeParseException ex) {
                    JOptionPane.showMessageDialog(BookManagementDialog.this,
                        "Invalid date format. Please use YYYY-MM-DD format.", 
                        "Date Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Create or update book
                if (currentBook == null) {
                    // Adding new book
                    Book newBook = new Book(
                        isbnField.getText().trim(),
                        titleField.getText().trim(),
                        authorField.getText().trim(),
                        categoryField.getText().trim(),
                        publisherField.getText().trim(),
                        publicationDate,
                        (Integer) totalCopiesSpinner.getValue()
                    );
                    newBook.setAvailableCopies((Integer) availableCopiesSpinner.getValue());
                    newBook.setActive(activeCheckBox.isSelected());
                    
                    bookService.addBook(newBook);
                    JOptionPane.showMessageDialog(BookManagementDialog.this,
                        "Book added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    // Updating existing book
                    currentBook.setTitle(titleField.getText().trim());
                    currentBook.setAuthor(authorField.getText().trim());
                    currentBook.setCategory(categoryField.getText().trim());
                    currentBook.setPublisher(publisherField.getText().trim());
                    currentBook.setPublicationDate(publicationDate);
                    currentBook.setTotalCopies((Integer) totalCopiesSpinner.getValue());
                    currentBook.setAvailableCopies((Integer) availableCopiesSpinner.getValue());
                    currentBook.setActive(activeCheckBox.isSelected());
                    
                    bookService.updateBook(currentBook);
                    JOptionPane.showMessageDialog(BookManagementDialog.this,
                        "Book updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                }
                
                dispose();
                
            } catch (DatabaseException ex) {
                JOptionPane.showMessageDialog(BookManagementDialog.this,
                    "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(BookManagementDialog.this,
                    "An error occurred: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}