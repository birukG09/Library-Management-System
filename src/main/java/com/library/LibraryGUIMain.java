package com.library;

import com.library.gui.LibraryGUI;
import com.library.util.DatabaseConnection;
import com.library.exception.DatabaseException;

import javax.swing.SwingUtilities;
import javax.swing.JOptionPane;


public class LibraryGUIMain {
    public static void main(String[] args) {
        try {
            
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
