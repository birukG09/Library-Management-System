package com.library.gui;

import com.library.service.ReportService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * Dialog for displaying reports
 */
public class ReportDialog extends JDialog {
    private final ReportService reportService;
    private final String reportType;
    private JTextArea reportArea;
    
    public ReportDialog(Frame parent, ReportService reportService, String reportType) {
        super(parent, reportType, true);
        this.reportService = reportService;
        this.reportType = reportType;
        
        initializeDialog();
        generateReport();
    }
    
    private void initializeDialog() {
        setSize(700, 500);
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout());
        
        // Create report area
        reportArea = new JTextArea();
        reportArea.setEditable(false);
        reportArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        reportArea.setBackground(Color.WHITE);
        reportArea.setMargin(new Insets(10, 10, 10, 10));
        
        JScrollPane scrollPane = new JScrollPane(reportArea);
        scrollPane.setPreferredSize(new Dimension(650, 400));
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        
        JButton refreshButton = new JButton("Refresh");
        JButton exportButton = new JButton("Export to File");
        JButton closeButton = new JButton("Close");
        
        refreshButton.addActionListener(e -> generateReport());
        exportButton.addActionListener(new ExportButtonListener());
        closeButton.addActionListener(e -> dispose());
        
        buttonPanel.add(refreshButton);
        buttonPanel.add(exportButton);
        buttonPanel.add(closeButton);
        
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void generateReport() {
        try {
            List<String> reportData = null;
            
            switch (reportType) {
                case "Overdue Books Report":
                    reportData = reportService.generateOverdueReport();
                    break;
                case "Book Popularity Report":
                    reportData = reportService.generateBookPopularityReport();
                    break;
                case "Library Statistics Report":
                    reportData = reportService.generateLibraryStatisticsReport();
                    break;
                default:
                    reportData = List.of("Unknown report type: " + reportType);
            }
            
            StringBuilder reportText = new StringBuilder();
            for (String line : reportData) {
                reportText.append(line).append("\n");
            }
            
            reportArea.setText(reportText.toString());
            reportArea.setCaretPosition(0); // Scroll to top
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error generating report: " + e.getMessage(),
                "Report Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private class ExportButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Save Report");
            fileChooser.setSelectedFile(new java.io.File(reportType.replace(" ", "_") + ".txt"));
            
            int userSelection = fileChooser.showSaveDialog(ReportDialog.this);
            
            if (userSelection == JFileChooser.APPROVE_OPTION) {
                java.io.File fileToSave = fileChooser.getSelectedFile();
                
                try {
                    java.nio.file.Files.write(fileToSave.toPath(), 
                        reportArea.getText().getBytes());
                    
                    JOptionPane.showMessageDialog(ReportDialog.this,
                        "Report exported successfully to:\n" + fileToSave.getAbsolutePath(),
                        "Export Success", JOptionPane.INFORMATION_MESSAGE);
                        
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(ReportDialog.this,
                        "Error exporting report: " + ex.getMessage(),
                        "Export Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
}