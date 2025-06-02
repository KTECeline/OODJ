package com.mycompany.owsb.model;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import javax.swing.table.DefaultTableModel;

// Finance_Report class handles financial reporting functionalities based on finance_payment.txt records.
public class Finance_Report {
    // Finance report class to generate financial summaries and detailed reports
    private List<Payment> allPayments;
    private LocalDate filterStartDate;
    private LocalDate filterEndDate;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    
    public Finance_Report() {
        this.allPayments = Payment.loadFinanceRecords();
        this.filterStartDate = null;
        this.filterEndDate = null;
    }
    
    // ============ DATE FILTER METHODS ============
    
    /**
     * Set date range filter
     * @param startDate Start date (inclusive)
     * @param endDate End date (inclusive)
     */
    public void setDateFilter(LocalDate startDate, LocalDate endDate) {
        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date cannot be after end date");
        }
        this.filterStartDate = startDate;
        this.filterEndDate = endDate;
    }
    
    /**
     * Set date filter from strings
     * @param startDateStr Start date in dd/MM/yyyy format
     * @param endDateStr End date in dd/MM/yyyy format
     */
    public void setDateFilter(String startDateStr, String endDateStr) {
        LocalDate startDate = (startDateStr != null && !startDateStr.trim().isEmpty()) 
            ? LocalDate.parse(startDateStr, DATE_FORMATTER) : null;
        LocalDate endDate = (endDateStr != null && !endDateStr.trim().isEmpty()) 
            ? LocalDate.parse(endDateStr, DATE_FORMATTER) : null;
        setDateFilter(startDate, endDate);
    }
    
    /**
     * Clear date filter
     */
    public void clearDateFilter() {
        this.filterStartDate = null;
        this.filterEndDate = null;
    }
    
    /**
     * Check if date filter is active
     */
    public boolean isDateFilterActive() {
        return filterStartDate != null || filterEndDate != null;
    }
    
    /**
     * Get current date filter info
     */
    public Map<String, String> getDateFilterInfo() {
        Map<String, String> info = new HashMap<>();
        info.put("startDate", filterStartDate != null ? filterStartDate.format(DATE_FORMATTER) : "Not Set");
        info.put("endDate", filterEndDate != null ? filterEndDate.format(DATE_FORMATTER) : "Not Set");
        info.put("active", String.valueOf(isDateFilterActive()));
        return info;
    }
    
    /**
     * Get filtered payments based on current date filter
     */
    private List<Payment> getFilteredPayments() {
        if (!isDateFilterActive()) {
            return allPayments;
        }
        
        return allPayments.stream()
                .filter(payment -> {
                    LocalDate paymentDate = payment.getPaymentDate();
                    boolean afterStart = (filterStartDate == null) || 
                                       (!paymentDate.isBefore(filterStartDate));
                    boolean beforeEnd = (filterEndDate == null) || 
                                      (!paymentDate.isAfter(filterEndDate));
                    return afterStart && beforeEnd;
                })
                .collect(Collectors.toList());
    }
    
    // ============ SUMMARY REPORT ============
    
    /**
     * Generate financial summary report (with date filtering)
     */
    public Map<String, Object> generateSummaryReport() {
        Map<String, Object> summary = new HashMap<>();
        List<Payment> filteredPayments = getFilteredPayments();
        
        // Total payments count
        int totalPayments = filteredPayments.size();
        
        // Total amount
        double totalAmount = filteredPayments.stream()
                .mapToDouble(Payment::getAmount)
                .sum();
        
        // Completed vs Pending breakdown
        long completedCount = filteredPayments.stream()
                .mapToLong(p -> p.getStatus().equalsIgnoreCase(Payment.STATUS_COMPLETED) ? 1 : 0)
                .sum();
        
        long pendingCount = filteredPayments.stream()
                .mapToLong(p -> p.getStatus().equalsIgnoreCase(Payment.STATUS_PENDING) ? 1 : 0)
                .sum();
        
        double completedAmount = filteredPayments.stream()
                .filter(p -> p.getStatus().equalsIgnoreCase(Payment.STATUS_COMPLETED))
                .mapToDouble(Payment::getAmount)
                .sum();
        
        double pendingAmount = filteredPayments.stream()
                .filter(p -> p.getStatus().equalsIgnoreCase(Payment.STATUS_PENDING))
                .mapToDouble(Payment::getAmount)
                .sum();
        
        // Basic statistics
        double averagePayment = totalPayments > 0 ? totalAmount / totalPayments : 0;
        double completionRate = totalPayments > 0 ? (double) completedCount / totalPayments * 100 : 0;
        
        summary.put("totalPayments", totalPayments);
        summary.put("totalAmount", totalAmount);
        summary.put("completedCount", completedCount);
        summary.put("pendingCount", pendingCount);
        summary.put("completedAmount", completedAmount);
        summary.put("pendingAmount", pendingAmount);
        summary.put("averagePayment", averagePayment);
        summary.put("completionRate", completionRate);
        summary.put("dateFiltered", isDateFilterActive());
        summary.put("dateRange", getDateFilterDescription());
        
        return summary;
    }
    
    /**
     * Generate table model for summary report
     */
    public DefaultTableModel generateSummaryTableModel() {
        String[] columns = {"Metric", "Value"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        
        Map<String, Object> summary = generateSummaryReport();
        
        // Add date filter info if active
        if (isDateFilterActive()) {
            model.addRow(new Object[]{"Report Period", summary.get("dateRange")});
            model.addRow(new Object[]{"", ""}); // Empty row for spacing
        }
        
        model.addRow(new Object[]{"Total Payments", summary.get("totalPayments")});
        model.addRow(new Object[]{"Total Amount (RM)", String.format("%.2f", (Double) summary.get("totalAmount"))});
        model.addRow(new Object[]{"Average Payment (RM)", String.format("%.2f", (Double) summary.get("averagePayment"))});
        model.addRow(new Object[]{"Completion Rate (%)", String.format("%.1f", (Double) summary.get("completionRate"))});
        model.addRow(new Object[]{"Completed Payments", summary.get("completedCount")});
        model.addRow(new Object[]{"Completed Amount (RM)", String.format("%.2f", (Double) summary.get("completedAmount"))});
        model.addRow(new Object[]{"Pending Payments", summary.get("pendingCount")});
        model.addRow(new Object[]{"Pending Amount (RM)", String.format("%.2f", (Double) summary.get("pendingAmount"))});
        
        return model;
    }
    
    // ============ DETAILED REPORT ============
    
    /**
     * Get all payments for detailed report (filtered)
     */
    public List<Payment> getAllPayments() {
        return new ArrayList<>(getFilteredPayments());
    }
    
    /**
     * Generate table model for detailed report
     */
    public DefaultTableModel generateDetailedTableModel() {
        String[] columns = {"Payment ID", "Supplier", "Amount (RM)", "Status", "Date", "Payment Method"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        List<Payment> filteredPayments = getFilteredPayments();
        
        for (Payment payment : filteredPayments) {
            Object[] row = {
                payment.getPaymentID(),
                payment.getSupplierID(),
                String.format("%.2f", payment.getAmount()),
                payment.getStatus(),
                payment.getPaymentDate().format(DATE_FORMATTER),
                payment.getPaymentMethod()
            };
            model.addRow(row);
        }
        
        return model;
    }
    
    // ============ PRESET DATE FILTERS ============
    
    /**
     * Set filter to current month
     */
    public void setCurrentMonthFilter() {
        LocalDate now = LocalDate.now();
        LocalDate startOfMonth = now.withDayOfMonth(1);
        LocalDate endOfMonth = now.withDayOfMonth(now.lengthOfMonth());
        setDateFilter(startOfMonth, endOfMonth);
    }
    
    /**
     * Set filter to last month
     */
    public void setLastMonthFilter() {
        LocalDate now = LocalDate.now();
        LocalDate lastMonth = now.minusMonths(1);
        LocalDate startOfLastMonth = lastMonth.withDayOfMonth(1);
        LocalDate endOfLastMonth = lastMonth.withDayOfMonth(lastMonth.lengthOfMonth());
        setDateFilter(startOfLastMonth, endOfLastMonth);
    }
    
    /**
     * Set filter to current year
     */
    public void setCurrentYearFilter() {
        LocalDate now = LocalDate.now();
        LocalDate startOfYear = now.withDayOfYear(1);
        LocalDate endOfYear = now.withDayOfYear(now.lengthOfYear());
        setDateFilter(startOfYear, endOfYear);
    }
    
    /**
     * Set filter to last 30 days
     */
    public void setLast30DaysFilter() {
        LocalDate now = LocalDate.now();
        LocalDate thirtyDaysAgo = now.minusDays(30);
        setDateFilter(thirtyDaysAgo, now);
    }
    
    /**
     * Set filter to last 7 days
     */
    public void setLast7DaysFilter() {
        LocalDate now = LocalDate.now();
        LocalDate sevenDaysAgo = now.minusDays(7);
        setDateFilter(sevenDaysAgo, now);
    }
    
    // ============ EXPORT FUNCTIONALITY ============
    
    /**
     * Export both reports to text file (with date filtering)
     */
    public boolean exportReportsToFile(String filename) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            writer.write("FINANCIAL REPORTS");
            writer.newLine();
            writer.write("Generated on: " + LocalDate.now().format(DATE_FORMATTER));
            writer.newLine();
            
            // Add date filter info
            if (isDateFilterActive()) {
                writer.write("Report Period: " + getDateFilterDescription());
                writer.newLine();
            }
            
            writer.write("=====================================");
            writer.newLine();
            writer.newLine();
            
            // Summary Report
            Map<String, Object> summary = generateSummaryReport();
            writer.write("SUMMARY REPORT");
            writer.newLine();
            writer.write("--------------");
            writer.newLine();
            writer.write("Total Payments: " + summary.get("totalPayments"));
            writer.newLine();
            writer.write("Total Amount: RM " + String.format("%.2f", (Double) summary.get("totalAmount")));
            writer.newLine();
            writer.write("Average Payment: RM " + String.format("%.2f", (Double) summary.get("averagePayment")));
            writer.newLine();
            writer.write("Completion Rate: " + String.format("%.1f", (Double) summary.get("completionRate")) + "%");
            writer.newLine();
            writer.newLine();
            writer.write("BREAKDOWN:");
            writer.newLine();
            writer.write("Completed: " + summary.get("completedCount") + " payments, RM " + 
                        String.format("%.2f", (Double) summary.get("completedAmount")));
            writer.newLine();
            writer.write("Pending: " + summary.get("pendingCount") + " payments, RM " + 
                        String.format("%.2f", (Double) summary.get("pendingAmount")));
            writer.newLine();
            writer.newLine();
            
            // Detailed Report
            writer.write("DETAILED REPORT");
            writer.newLine();
            writer.write("---------------");
            writer.newLine();
            writer.write(String.format("%-12s %-15s %-12s %-12s %-12s %-15s", 
                        "Payment ID", "Supplier", "Amount(RM)", "Status", "Date", "Method"));
            writer.newLine();
            writer.write("--------------------------------------------------------------------------------");
            writer.newLine();
            
            List<Payment> filteredPayments = getFilteredPayments();
            for (Payment payment : filteredPayments) {
                writer.write(String.format("%-12s %-15s %-12.2f %-12s %-12s %-15s",
                    payment.getPaymentID(),
                    payment.getSupplierID(),
                    payment.getAmount(),
                    payment.getStatus(),
                    payment.getPaymentDate().format(DATE_FORMATTER),
                    payment.getPaymentMethod()));
                writer.newLine();
            }
            
            return true;
        } catch (IOException e) {
            System.err.println("Error exporting reports: " + e.getMessage());
            return false;
        }
    }
    
    // ============ UTILITY METHODS ============
    
    /**
     * Get description of current date filter
     */
    private String getDateFilterDescription() {
        if (!isDateFilterActive()) {
            return "All Dates";
        }
        
        String start = (filterStartDate != null) ? filterStartDate.format(DATE_FORMATTER) : "Beginning";
        String end = (filterEndDate != null) ? filterEndDate.format(DATE_FORMATTER) : "Present";
        
        return start + " to " + end;
    }
    
    /**
     * Get statistics about date range coverage
     */
    public Map<String, Object> getDateRangeStats() {
        Map<String, Object> stats = new HashMap<>();
        
        if (allPayments.isEmpty()) {
            stats.put("earliestDate", "No data");
            stats.put("latestDate", "No data");
            stats.put("totalDays", 0);
            return stats;
        }
        
        LocalDate earliest = allPayments.stream()
                .map(Payment::getPaymentDate)
                .min(LocalDate::compareTo)
                .orElse(LocalDate.now());
                
        LocalDate latest = allPayments.stream()
                .map(Payment::getPaymentDate)
                .max(LocalDate::compareTo)
                .orElse(LocalDate.now());
        
        stats.put("earliestDate", earliest.format(DATE_FORMATTER));
        stats.put("latestDate", latest.format(DATE_FORMATTER));
        stats.put("totalDays", earliest.until(latest).getDays() + 1);
        stats.put("filteredCount", getFilteredPayments().size());
        stats.put("totalCount", allPayments.size());
        
        return stats;
    }
    
    /**
     * Refresh payment data
     */
    public void refreshData() {
        this.allPayments = Payment.loadFinanceRecords();
    }
}