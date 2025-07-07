package com.library.gui;

import com.library.model.Book;
import com.library.service.BookService;
import com.library.exception.DatabaseException;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.ArrayList;

/**
 * Dialog for displaying list of books
 */
public class BookListDialog extends JDialog {
    private final BookService bookService;
    private JTable bookTable;
    private BookTableModel tableModel;
    private List<Book> books;
    
    public BookListDialog(Frame parent, BookService bookService) {
        super(parent, "Book List", true);
        this.bookService = bookService;
        this.books = new ArrayList<>();
        
        initializeDialog();
        loadBooks();
    }
    
    private void initializeDialog() {
        setSize(800, 500);
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout());
        
        // Create table
        tableModel = new BookTableModel();
        bookTable = new JTable(tableModel);
        bookTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        bookTable.setRowHeight(25);
        
        // Add double-click listener for editing
        bookTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    editSelectedBook();
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(bookTable);
        scrollPane.setPreferredSize(new Dimension(750, 350));
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        
        JButton addButton = new JButton("Add Book");
        JButton editButton = new JButton("Edit Book");
        JButton deleteButton = new JButton("Delete Book");
        JButton refreshButton = new JButton("Refresh");
        JButton closeButton = new JButton("Close");
        
        addButton.addActionListener(e -> addNewBook());
        editButton.addActionListener(e -> editSelectedBook());
        deleteButton.addActionListener(e -> deleteSelectedBook());
        refreshButton.addActionListener(e -> loadBooks());
        closeButton.addActionListener(e -> dispose());
        
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(closeButton);
        
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void loadBooks() {
        try {
            books = bookService.getAllBooks();
            tableModel.fireTableDataChanged();
            
            if (books.isEmpty()) {
                JLabel noDataLabel = new JLabel("No books found in the library.", SwingConstants.CENTER);
                noDataLabel.setFont(new Font("Arial", Font.ITALIC, 14));
                noDataLabel.setForeground(Color.GRAY);
            }
        } catch (DatabaseException e) {
            JOptionPane.showMessageDialog(this,
                "Error loading books: " + e.getMessage(),
                "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void addNewBook() {
        BookManagementDialog dialog = new BookManagementDialog((Frame) getParent(), bookService);
        dialog.setVisible(true);
        loadBooks(); // Refresh the list
    }
    
    private void editSelectedBook() {
        int selectedRow = bookTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Please select a book to edit.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Book selectedBook = books.get(selectedRow);
        BookManagementDialog dialog = new BookManagementDialog((Frame) getParent(), bookService, selectedBook);
        dialog.setVisible(true);
        loadBooks(); // Refresh the list
    }
    
    private void deleteSelectedBook() {
        int selectedRow = bookTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Please select a book to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Book selectedBook = books.get(selectedRow);
        int result = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete the book:\n" + selectedBook.getTitle() + "?",
            "Confirm Delete", JOptionPane.YES_NO_OPTION);
        
        if (result == JOptionPane.YES_OPTION) {
            try {
                bookService.deleteBook(selectedBook.getIsbn());
                JOptionPane.showMessageDialog(this,
                    "Book deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadBooks(); // Refresh the list
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                    "Error deleting book: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private class BookTableModel extends AbstractTableModel {
        private final String[] columnNames = {
            "ISBN", "Title", "Author", "Category", "Publisher", "Copies", "Available", "Active"
        };
        
        @Override
        public int getRowCount() {
            return books.size();
        }
        
        @Override
        public int getColumnCount() {
            return columnNames.length;
        }
        
        @Override
        public String getColumnName(int column) {
            return columnNames[column];
        }
        
        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            Book book = books.get(rowIndex);
            switch (columnIndex) {
                case 0: return book.getIsbn();
                case 1: return book.getTitle();
                case 2: return book.getAuthor();
                case 3: return book.getCategory();
                case 4: return book.getPublisher();
                case 5: return book.getTotalCopies();
                case 6: return book.getAvailableCopies();
                case 7: return book.isActive() ? "Yes" : "No";
                default: return "";
            }
        }
        
        @Override
        public Class<?> getColumnClass(int columnIndex) {
            switch (columnIndex) {
                case 5:
                case 6: return Integer.class;
                default: return String.class;
            }
        }
    }
}