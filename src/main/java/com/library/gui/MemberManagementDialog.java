package com.library.gui;

import com.library.model.Member;
import com.library.service.MemberService;
import com.library.exception.DatabaseException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Dialog for adding and editing members
 */
public class MemberManagementDialog extends JDialog {
    private final MemberService memberService;
    private Member currentMember;
    
    // Form fields
    private JTextField idField;
    private JTextField firstNameField;
    private JTextField lastNameField;
    private JTextField emailField;
    private JTextField phoneField;
    private JComboBox<String> membershipTypeCombo;
    private JTextField membershipExpiryField;
    private JCheckBox activeCheckBox;
    
    public MemberManagementDialog(Frame parent, MemberService memberService) {
        this(parent, memberService, null);
    }
    
    public MemberManagementDialog(Frame parent, MemberService memberService, Member member) {
        super(parent, member == null ? "Add New Member" : "Edit Member", true);
        this.memberService = memberService;
        this.currentMember = member;
        
        initializeDialog();
        if (member != null) {
            populateFields(member);
        }
    }
    
    private void initializeDialog() {
        setSize(450, 350);
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout());
        
        // Create form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Member ID
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Member ID:"), gbc);
        gbc.gridx = 1;
        idField = new JTextField(20);
        formPanel.add(idField, gbc);
        
        // First Name
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("First Name:"), gbc);
        gbc.gridx = 1;
        firstNameField = new JTextField(20);
        formPanel.add(firstNameField, gbc);
        
        // Last Name
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Last Name:"), gbc);
        gbc.gridx = 1;
        lastNameField = new JTextField(20);
        formPanel.add(lastNameField, gbc);
        
        // Email
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        emailField = new JTextField(20);
        formPanel.add(emailField, gbc);
        
        // Phone
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Phone:"), gbc);
        gbc.gridx = 1;
        phoneField = new JTextField(20);
        formPanel.add(phoneField, gbc);
        
        // Membership Type
        gbc.gridx = 0; gbc.gridy = 5;
        formPanel.add(new JLabel("Membership Type:"), gbc);
        gbc.gridx = 1;
        String[] membershipTypes = {"STANDARD", "PREMIUM", "STUDENT", "FACULTY"};
        membershipTypeCombo = new JComboBox<>(membershipTypes);
        formPanel.add(membershipTypeCombo, gbc);
        
        // Membership Expiry
        gbc.gridx = 0; gbc.gridy = 6;
        formPanel.add(new JLabel("Membership Expiry (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1;
        membershipExpiryField = new JTextField(20);
        formPanel.add(membershipExpiryField, gbc);
        
        // Active
        gbc.gridx = 0; gbc.gridy = 7;
        formPanel.add(new JLabel("Active:"), gbc);
        gbc.gridx = 1;
        activeCheckBox = new JCheckBox();
        activeCheckBox.setSelected(true);
        formPanel.add(activeCheckBox, gbc);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton saveButton = new JButton(currentMember == null ? "Add Member" : "Update Member");
        JButton cancelButton = new JButton("Cancel");
        
        saveButton.addActionListener(new SaveButtonListener());
        cancelButton.addActionListener(e -> dispose());
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void populateFields(Member member) {
        idField.setText(member.getId());
        idField.setEditable(false); // ID shouldn't be editable for existing members
        firstNameField.setText(member.getFirstName());
        lastNameField.setText(member.getLastName());
        emailField.setText(member.getEmail());
        phoneField.setText(member.getPhone());
        membershipTypeCombo.setSelectedItem(member.getMembershipType());
        membershipExpiryField.setText(member.getMembershipExpiry().toString());
        activeCheckBox.setSelected(member.isActive());
    }
    
    private class SaveButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                // Validate input
                if (idField.getText().trim().isEmpty() ||
                    firstNameField.getText().trim().isEmpty() ||
                    lastNameField.getText().trim().isEmpty() ||
                    emailField.getText().trim().isEmpty() ||
                    phoneField.getText().trim().isEmpty() ||
                    membershipExpiryField.getText().trim().isEmpty()) {
                    
                    JOptionPane.showMessageDialog(MemberManagementDialog.this,
                        "Please fill in all fields.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Parse date
                LocalDate membershipExpiry;
                try {
                    membershipExpiry = LocalDate.parse(membershipExpiryField.getText().trim(),
                        DateTimeFormatter.ISO_LOCAL_DATE);
                } catch (DateTimeParseException ex) {
                    JOptionPane.showMessageDialog(MemberManagementDialog.this,
                        "Invalid date format. Please use YYYY-MM-DD format.", 
                        "Date Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Create or update member
                if (currentMember == null) {
                    // Adding new member
                    Member newMember = new Member(
                        idField.getText().trim(),
                        firstNameField.getText().trim(),
                        lastNameField.getText().trim(),
                        emailField.getText().trim(),
                        phoneField.getText().trim(),
                        (String) membershipTypeCombo.getSelectedItem()
                    );
                    newMember.setMembershipExpiry(membershipExpiry);
                    newMember.setActive(activeCheckBox.isSelected());
                    
                    memberService.registerMember(newMember);
                    JOptionPane.showMessageDialog(MemberManagementDialog.this,
                        "Member added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    // Updating existing member
                    currentMember.setFirstName(firstNameField.getText().trim());
                    currentMember.setLastName(lastNameField.getText().trim());
                    currentMember.setEmail(emailField.getText().trim());
                    currentMember.setPhone(phoneField.getText().trim());
                    currentMember.setMembershipType((String) membershipTypeCombo.getSelectedItem());
                    currentMember.setMembershipExpiry(membershipExpiry);
                    currentMember.setActive(activeCheckBox.isSelected());
                    
                    memberService.updateMember(currentMember);
                    JOptionPane.showMessageDialog(MemberManagementDialog.this,
                        "Member updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                }
                
                dispose();
                
            } catch (DatabaseException ex) {
                JOptionPane.showMessageDialog(MemberManagementDialog.this,
                    "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(MemberManagementDialog.this,
                    "An error occurred: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}