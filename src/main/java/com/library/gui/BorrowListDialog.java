package com.library.gui;

import com.library.model.BorrowRecord;
import com.library.service.BorrowingService;
import com.library.exception.DatabaseException;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;

/**
 * Dialog for displaying borrow records
 */
public class BorrowListDialog extends JDialog {
    private final BorrowingService borrowingService;
    private JTable borrowTable;
    private BorrowTableModel tableModel;
    private List<BorrowRecord> borrowRecords;
    
    public BorrowListDialog(Frame parent, BorrowingService borrowingService) {
        super(parent, "Borrow Records", true);
        this.borrowingService = borrowingService;
        this.borrowRecords = new ArrayList<>();
        
        initializeDialog();
        loadBorrowRecords();
    }
    
    private void initializeDialog() {
        setSize(900, 500);
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout());
        
        // Create table
        tableModel = new BorrowTableModel();
        borrowTable = new JTable(tableModel);
        borrowTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        borrowTable.setRowHeight(25);
        
        JScrollPane scrollPane = new JScrollPane(borrowTable);
        scrollPane.setPreferredSize(new Dimension(850, 350));
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        
        JButton refreshButton = new JButton("Refresh");
        JButton overdueButton = new JButton("Show Overdue");
        JButton activeButton = new JButton("Show Active");
        JButton allButton = new JButton("Show All");
        JButton closeButton = new JButton("Close");
        
        refreshButton.addActionListener(e -> loadBorrowRecords());
        overdueButton.addActionListener(e -> loadOverdueRecords());
        activeButton.addActionListener(e -> loadActiveRecords());
        allButton.addActionListener(e -> loadBorrowRecords());
        closeButton.addActionListener(e -> dispose());
        
        buttonPanel.add(refreshButton);
        buttonPanel.add(overdueButton);
        buttonPanel.add(activeButton);
        buttonPanel.add(allButton);
        buttonPanel.add(closeButton);
        
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void loadBorrowRecords() {
        try {
            borrowRecords = borrowingService.getAllBorrowRecords();
            tableModel.fireTableDataChanged();
            setTitle("All Borrow Records (" + borrowRecords.size() + ")");
        } catch (DatabaseException e) {
            JOptionPane.showMessageDialog(this,
                "Error loading borrow records: " + e.getMessage(),
                "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void loadOverdueRecords() {
        try {
            borrowRecords = borrowingService.getOverdueRecords();
            tableModel.fireTableDataChanged();
            setTitle("Overdue Records (" + borrowRecords.size() + ")");
        } catch (DatabaseException e) {
            JOptionPane.showMessageDialog(this,
                "Error loading overdue records: " + e.getMessage(),
                "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void loadActiveRecords() {
        try {
            borrowRecords = borrowingService.getAllBorrowRecords();
            // Filter for active records (not returned)
            borrowRecords = borrowRecords.stream()
                .filter(record -> record.getReturnDate() == null)
                .collect(java.util.stream.Collectors.toList());
            tableModel.fireTableDataChanged();
            setTitle("Active Borrow Records (" + borrowRecords.size() + ")");
        } catch (DatabaseException e) {
            JOptionPane.showMessageDialog(this,
                "Error loading active records: " + e.getMessage(),
                "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private class BorrowTableModel extends AbstractTableModel {
        private final String[] columnNames = {
            "Record ID", "Member ID", "ISBN", "Borrow Date", "Due Date", "Return Date", "Status", "Fine"
        };
        
        @Override
        public int getRowCount() {
            return borrowRecords.size();
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
            BorrowRecord record = borrowRecords.get(rowIndex);
            switch (columnIndex) {
                case 0: return record.getRecordId();
                case 1: return record.getMemberId();
                case 2: return record.getIsbn();
                case 3: return record.getBorrowDate().toString();
                case 4: return record.getDueDate().toString();
                case 5: return record.getReturnDate() != null ? record.getReturnDate().toString() : "Not Returned";
                case 6: return record.getStatus();
                case 7: return String.format("$%.2f", record.getFineAmount());
                default: return "";
            }
        }
        
        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return String.class;
        }
    }
}