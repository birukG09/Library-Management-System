package com.library.interfaces;

import java.util.List;
import java.util.Map;

/**
 * Interface for generating various reports in the library system
 */
public interface ReportGenerator {
    /**
     * Generate a report of overdue books
     * @return List of overdue records with details
     */
    List<String> generateOverdueReport();
    
    /**
     * Generate member activity report
     * @return Map containing member statistics
     */
    Map<String, Object> generateMemberActivityReport();
    
    /**
     * Generate book popularity report
     * @return List of books sorted by popularity
     */
    List<String> generateBookPopularityReport();
    
    /**
     * Export report to file
     * @param reportData the report data to export
     * @param filename the filename for the export
     * @return true if export was successful
     */
    boolean exportReportToFile(List<String> reportData, String filename);
}
