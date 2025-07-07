package com.library.gui;

import com.library.model.Member;
import com.library.service.MemberService;
import com.library.exception.DatabaseException;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.ArrayList;

/**
 * Dialog for displaying list of members
 */
public class MemberListDialog extends JDialog {
    private final MemberService memberService;
    private JTable memberTable;
    private MemberTableModel tableModel;
    private List<Member> members;
    
    public MemberListDialog(Frame parent, MemberService memberService) {
        super(parent, "Member List", true);
        this.memberService = memberService;
        this.members = new ArrayList<>();
        
        initializeDialog();
        loadMembers();
    }
    
    private void initializeDialog() {
        setSize(800, 500);
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout());
        
        // Create table
        tableModel = new MemberTableModel();
        memberTable = new JTable(tableModel);
        memberTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        memberTable.setRowHeight(25);
        
        // Add double-click listener for editing
        memberTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    editSelectedMember();
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(memberTable);
        scrollPane.setPreferredSize(new Dimension(750, 350));
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        
        JButton addButton = new JButton("Add Member");
        JButton editButton = new JButton("Edit Member");
        JButton deleteButton = new JButton("Delete Member");
        JButton refreshButton = new JButton("Refresh");
        JButton closeButton = new JButton("Close");
        
        addButton.addActionListener(e -> addNewMember());
        editButton.addActionListener(e -> editSelectedMember());
        deleteButton.addActionListener(e -> deleteSelectedMember());
        refreshButton.addActionListener(e -> loadMembers());
        closeButton.addActionListener(e -> dispose());
        
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(closeButton);
        
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void loadMembers() {
        try {
            members = memberService.getAllMembers();
            tableModel.fireTableDataChanged();
        } catch (DatabaseException e) {
            JOptionPane.showMessageDialog(this,
                "Error loading members: " + e.getMessage(),
                "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void addNewMember() {
        MemberManagementDialog dialog = new MemberManagementDialog((Frame) getParent(), memberService);
        dialog.setVisible(true);
        loadMembers(); // Refresh the list
    }
    
    private void editSelectedMember() {
        int selectedRow = memberTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Please select a member to edit.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Member selectedMember = members.get(selectedRow);
        MemberManagementDialog dialog = new MemberManagementDialog((Frame) getParent(), memberService, selectedMember);
        dialog.setVisible(true);
        loadMembers(); // Refresh the list
    }
    
    private void deleteSelectedMember() {
        int selectedRow = memberTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Please select a member to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Member selectedMember = members.get(selectedRow);
        int result = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete the member:\n" + selectedMember.getFullName() + "?",
            "Confirm Delete", JOptionPane.YES_NO_OPTION);
        
        if (result == JOptionPane.YES_OPTION) {
            try {
                memberService.deleteMember(selectedMember.getId());
                JOptionPane.showMessageDialog(this,
                    "Member deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadMembers(); // Refresh the list
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                    "Error deleting member: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private class MemberTableModel extends AbstractTableModel {
        private final String[] columnNames = {
            "ID", "Name", "Email", "Phone", "Type", "Expiry", "Books Borrowed", "Active"
        };
        
        @Override
        public int getRowCount() {
            return members.size();
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
            Member member = members.get(rowIndex);
            switch (columnIndex) {
                case 0: return member.getId();
                case 1: return member.getFullName();
                case 2: return member.getEmail();
                case 3: return member.getPhone();
                case 4: return member.getMembershipType();
                case 5: return member.getMembershipExpiry().toString();
                case 6: return member.getBorrowedBooksCount();
                case 7: return member.isActive() ? "Yes" : "No";
                default: return "";
            }
        }
        
        @Override
        public Class<?> getColumnClass(int columnIndex) {
            switch (columnIndex) {
                case 6: return Integer.class;
                default: return String.class;
            }
        }
    }
}