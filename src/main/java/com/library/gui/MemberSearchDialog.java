package com.library.gui;

import com.library.model.Member;
import com.library.service.MemberService;
import com.library.exception.DatabaseException;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.ArrayList;

/**
 * Dialog for searching members
 */
public class MemberSearchDialog extends JDialog {
    private final MemberService memberService;
    private JTextField searchField;
    private JTable resultTable;
    private MemberSearchTableModel tableModel;
    private List<Member> searchResults;
    
    public MemberSearchDialog(Frame parent, MemberService memberService) {
        super(parent, "Search Members", true);
        this.memberService = memberService;
        this.searchResults = new ArrayList<>();
        
        initializeDialog();
    }
    
    private void initializeDialog() {
        setSize(700, 400);
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
        tableModel = new MemberSearchTableModel();
        resultTable = new JTable(tableModel);
        resultTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        resultTable.setRowHeight(25);
        
        JScrollPane scrollPane = new JScrollPane(resultTable);
        scrollPane.setPreferredSize(new Dimension(650, 250));
        
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
                JOptionPane.showMessageDialog(MemberSearchDialog.this,
                    "Please enter a search term.", "No Search Term", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            try {
                searchResults = memberService.searchMembers(searchTerm);
                tableModel.fireTableDataChanged();
                
                if (searchResults.isEmpty()) {
                    JOptionPane.showMessageDialog(MemberSearchDialog.this,
                        "No members found matching your search criteria.",
                        "No Results", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (DatabaseException ex) {
                JOptionPane.showMessageDialog(MemberSearchDialog.this,
                    "Error searching members: " + ex.getMessage(),
                    "Search Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private class MemberSearchTableModel extends AbstractTableModel {
        private final String[] columnNames = {
            "ID", "Name", "Email", "Phone", "Type", "Status"
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
            Member member = searchResults.get(rowIndex);
            switch (columnIndex) {
                case 0: return member.getId();
                case 1: return member.getFullName();
                case 2: return member.getEmail();
                case 3: return member.getPhone();
                case 4: return member.getMembershipType();
                case 5: return member.isActive() ? "Active" : "Inactive";
                default: return "";
            }
        }
    }
}