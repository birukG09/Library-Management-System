package com.library.gui;

import com.library.model.Book;
import com.library.service.BookService;
import com.library.exception.DatabaseException;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.ArrayList;

/**
 * Dialog for searching books
 */
public class BookSearchDialog extends JDialog {
    private final BookService bookService;
    private JTextField searchField;
    private JTable resultTable;
    private BookSearchTableModel tableModel;
    private List<Book> searchResults;
    
    public BookSearchDialog(Frame parent, BookService bookService) {
        super(parent, "Search Books", true);
        this.bookService = bookService;
        this.searchResults = new ArrayList<>();
        
        initializeDialog();
    }
    
    private void initializeDialog() {
        setSize(750, 450);
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout());
        
        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout());
        searchPanel.add(new JLabel("Search:"));
        searchField = new JTextField(20);
        JButton searchButton = new JButton("Search");
        JButton clearButton = new JButton("Clear");
        
        searchButton.addActionListener(new SearchButtonListener());
        clearButton.addActionListener(e -> clearResults());
        
        // Enter key support for search
        searchField.addActionListener(new SearchButtonListener());
        
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(clearButton);
        
        // Results table
        tableModel = new BookSearchTableModel();
        resultTable = new JTable(tableModel);
        resultTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        resultTable.setRowHeight(25);
        
        JScrollPane scrollPane = new JScrollPane(resultTable);
        scrollPane.setPreferredSize(new Dimension(700, 300));
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dispose());
        buttonPanel.add(closeButton);
        
        add(searchPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        
        // Initial empty state
        showEmptyState();
    }
    
    private void showEmptyState() {
        // You can customize this to show instructions
        searchResults.clear();
        tableModel.fireTableDataChanged();
    }
    
    private void clearResults() {
        searchField.setText("");
        showEmptyState();
    }
    
    private class SearchButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String searchTerm = searchField.getText().trim();
            if (searchTerm.isEmpty()) {
                JOptionPane.showMessageDialog(BookSearchDialog.this,
                    "Please enter a search term.", "No Search Term", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            try {
                searchResults = bookService.searchBooks(searchTerm);
                tableModel.fireTableDataChanged();
                
                if (searchResults.isEmpty()) {
                    JOptionPane.showMessageDialog(BookSearchDialog.this,
                        "No books found matching your search criteria.",
                        "No Results", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (DatabaseException ex) {
                JOptionPane.showMessageDialog(BookSearchDialog.this,
                    "Error searching books: " + ex.getMessage(),
                    "Search Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private class BookSearchTableModel extends AbstractTableModel {
        private final String[] columnNames = {
            "ISBN", "Title", "Author", "Category", "Available", "Total Copies"
        };
        
        @Override
        public int getRowCount() {
            return searchResults.size();
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
            Book book = searchResults.get(rowIndex);
            switch (columnIndex) {
                case 0: return book.getIsbn();
                case 1: return book.getTitle();
                case 2: return book.getAuthor();
                case 3: return book.getCategory();
                case 4: return book.getAvailableCopies();
                case 5: return book.getTotalCopies();
                default: return "";
            }
        }
        
        @Override
        public Class<?> getColumnClass(int columnIndex) {
            switch (columnIndex) {
                case 4:
                case 5: return Integer.class;
                default: return String.class;
            }
        }
    }
}