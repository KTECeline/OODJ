package com.mycompany.owsb.model;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;
import javax.swing.table.DefaultTableModel;

/**
 * Finance Report Generator
 * Generates comprehensive financial reports from payment data
 * 
 * @author Generated
 */
public class Finance_Report {
    
    private List<Payment> allPayments;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    
    public Finance_Report() {
        this.allPayments = Payment.loadFinanceRecords();
    }
    
    // ============ SUMMARY REPORTS ============
    
    /**
     * Generate overall financial summary
     */
    public Map<String, Object> generateFinancialSummary() {
        Map<String, Object> summary = new HashMap<>();
        
        // Total payments
        double totalAmount = allPayments.stream()
                .mapToDouble(Payment::getAmount)
                .sum();
        
        // Count by status
        Map<String, Long> statusCounts = allPayments.stream()
                .collect(Collectors.groupingBy(Payment::getStatus, Collectors.counting()));
        
        // Amount by status
        Map<String, Double> statusAmounts = allPayments.stream()
                .collect(Collectors.groupingBy(Payment::getStatus, 
                        Collectors.summingDouble(Payment::getAmount)));
        
        // Payment methods distribution
        Map<String, Long> methodCounts = allPayments.stream()
                .collect(Collectors.groupingBy(Payment::getPaymentMethod, Collectors.counting()));
        
        summary.put("totalPayments", allPayments.size());
        summary.put("totalAmount", totalAmount);
        summary.put("statusCounts", statusCounts);
        summary.put("statusAmounts", statusAmounts);
        summary.put("methodCounts", methodCounts);
        summary.put("averagePayment", totalAmount / Math.max(1, allPayments.size()));
        
        return summary;
    }
    
    /**
     * Generate monthly financial report
     */
    public Map<String, Object> generateMonthlyReport(int year, int month) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.with(TemporalAdjusters.lastDayOfMonth());
        
        List<Payment> monthlyPayments = allPayments.stream()
                .filter(p -> !p.getPaymentDate().isBefore(startDate) && 
                           !p.getPaymentDate().isAfter(endDate))
                .collect(Collectors.toList());
        
        Map<String, Object> report = new HashMap<>();
        
        double totalAmount = monthlyPayments.stream()
                .mapToDouble(Payment::getAmount)
                .sum();
        
        Map<String, Double> statusAmounts = monthlyPayments.stream()
                .collect(Collectors.groupingBy(Payment::getStatus, 
                        Collectors.summingDouble(Payment::getAmount)));
        
        Map<String, Double> supplierAmounts = monthlyPayments.stream()
                .collect(Collectors.groupingBy(Payment::getSupplierID, 
                        Collectors.summingDouble(Payment::getAmount)));
        
        report.put("period", startDate.format(DATE_FORMATTER) + " - " + endDate.format(DATE_FORMATTER));
        report.put("totalPayments", monthlyPayments.size());
        report.put("totalAmount", totalAmount);
        report.put("statusBreakdown", statusAmounts);
        report.put("supplierBreakdown", supplierAmounts);
        report.put("payments", monthlyPayments);
        
        return report;
    }
    
    /**
     * Generate supplier-wise financial report
     */
    public Map<String, Object> generateSupplierReport() {
        Map<String, List<Payment>> supplierPayments = allPayments.stream()
                .collect(Collectors.groupingBy(Payment::getSupplierID));
        
        Map<String, Object> report = new HashMap<>();
        
        for (Map.Entry<String, List<Payment>> entry : supplierPayments.entrySet()) {
            String supplierID = entry.getKey();
            List<Payment> payments = entry.getValue();
            
            Map<String, Object> supplierData = new HashMap<>();
            supplierData.put("totalPayments", payments.size());
            supplierData.put("totalAmount", payments.stream().mapToDouble(Payment::getAmount).sum());
            supplierData.put("averagePayment", payments.stream().mapToDouble(Payment::getAmount).average().orElse(0));
            supplierData.put("statusBreakdown", payments.stream()
                    .collect(Collectors.groupingBy(Payment::getStatus, Collectors.counting())));
            supplierData.put("methodBreakdown", payments.stream()
                    .collect(Collectors.groupingBy(Payment::getPaymentMethod, Collectors.counting())));
            
            report.put(supplierID, supplierData);
        }
        
        return report;
    }
    
    // ============ STATUS-BASED REPORTS ============
    
    /**
     * Get payments by status
     */
    public List<Payment> getPaymentsByStatus(String status) {
        return allPayments.stream()
                .filter(p -> p.getStatus().equalsIgnoreCase(status))
                .collect(Collectors.toList());
    }
    
    /**
     * Generate pending payments report
     */
    public Map<String, Object> generatePendingPaymentsReport() {
        List<Payment> pendingPayments = getPaymentsByStatus(Payment.STATUS_PENDING);
        
        Map<String, Object> report = new HashMap<>();
        report.put("totalPending", pendingPayments.size());
        report.put("totalAmount", pendingPayments.stream().mapToDouble(Payment::getAmount).sum());
        report.put("payments", pendingPayments);
        
        // Group by supplier
        Map<String, Double> supplierAmounts = pendingPayments.stream()
                .collect(Collectors.groupingBy(Payment::getSupplierID, 
                        Collectors.summingDouble(Payment::getAmount)));
        report.put("supplierBreakdown", supplierAmounts);
        
        return report;
    }
    
    /**
     * Generate overdue payments report (pending payments older than 30 days)
     */
    public Map<String, Object> generateOverduePaymentsReport() {
        LocalDate thirtyDaysAgo = LocalDate.now().minusDays(30);
        
        List<Payment> overduePayments = allPayments.stream()
                .filter(p -> p.getStatus().equalsIgnoreCase(Payment.STATUS_PENDING))
                .filter(p -> p.getPaymentDate().isBefore(thirtyDaysAgo))
                .collect(Collectors.toList());
        
        Map<String, Object> report = new HashMap<>();
        report.put("totalOverdue", overduePayments.size());
        report.put("totalAmount", overduePayments.stream().mapToDouble(Payment::getAmount).sum());
        report.put("payments", overduePayments);
        
        return report;
    }
    
    // ============ ANALYTICAL REPORTS ============
    
    /**
     * Generate payment trends analysis
     */
    public Map<String, Object> generatePaymentTrends() {
        Map<String, Object> trends = new HashMap<>();
        
        // Monthly trends (last 12 months)
        Map<String, Double> monthlyTrends = new LinkedHashMap<>();
        LocalDate currentDate = LocalDate.now();
        
        for (int i = 11; i >= 0; i--) {
            LocalDate monthStart = currentDate.minusMonths(i).withDayOfMonth(1);
            LocalDate monthEnd = monthStart.with(TemporalAdjusters.lastDayOfMonth());
            
            double monthlyTotal = allPayments.stream()
                    .filter(p -> !p.getPaymentDate().isBefore(monthStart) && 
                               !p.getPaymentDate().isAfter(monthEnd))
                    .mapToDouble(Payment::getAmount)
                    .sum();
            
            monthlyTrends.put(monthStart.format(DateTimeFormatter.ofPattern("MMM yyyy")), monthlyTotal);
        }
        
        trends.put("monthlyTrends", monthlyTrends);
        
        // Top suppliers by amount
        Map<String, Double> topSuppliers = allPayments.stream()
                .collect(Collectors.groupingBy(Payment::getSupplierID, 
                        Collectors.summingDouble(Payment::getAmount)))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .limit(10)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new));
        
        trends.put("topSuppliers", topSuppliers);
        
        return trends;
    }
    
    // ============ TABLE MODEL GENERATORS ============
    
    /**
     * Generate table model for financial summary
     */
    public DefaultTableModel generateSummaryTableModel() {
        String[] columns = {"Metric", "Value"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        
        Map<String, Object> summary = generateFinancialSummary();
        
        model.addRow(new Object[]{"Total Payments", summary.get("totalPayments")});
        model.addRow(new Object[]{"Total Amount (RM)", String.format("%.2f", (Double) summary.get("totalAmount"))});
        model.addRow(new Object[]{"Average Payment (RM)", String.format("%.2f", (Double) summary.get("averagePayment"))});
        
        // Add status breakdown
        @SuppressWarnings("unchecked")
        Map<String, Double> statusAmounts = (Map<String, Double>) summary.get("statusAmounts");
        for (Map.Entry<String, Double> entry : statusAmounts.entrySet()) {
            model.addRow(new Object[]{entry.getKey() + " Amount (RM)", String.format("%.2f", entry.getValue())});
        }
        
        return model;
    }
    
    /**
     * Generate table model for payments by status
     */
    public DefaultTableModel generateStatusReportTableModel(String status) {
        String[] columns = {"Payment ID", "Order IDs", "Supplier ID", "Amount (RM)", "Payment Date", "Payment Method"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        
        List<Payment> filteredPayments = getPaymentsByStatus(status);
        
        for (Payment payment : filteredPayments) {
            Object[] row = {
                payment.getPaymentID(),
                payment.getOrderIDsAsString(),
                payment.getSupplierID(),
                String.format("%.2f", payment.getAmount()),
                payment.getPaymentDate().format(DATE_FORMATTER),
                payment.getPaymentMethod()
            };
            model.addRow(row);
        }
        
        return model;
    }
    
    /**
     * Generate table model for supplier breakdown
     */
    public DefaultTableModel generateSupplierReportTableModel() {
        String[] columns = {"Supplier ID", "Total Payments", "Total Amount (RM)", "Average Payment (RM)", "Completed", "Pending", "Others"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        
        Map<String, Object> supplierReport = generateSupplierReport();
        
        for (Map.Entry<String, Object> entry : supplierReport.entrySet()) {
            String supplierID = entry.getKey();
            @SuppressWarnings("unchecked")
            Map<String, Object> data = (Map<String, Object>) entry.getValue();
            
            @SuppressWarnings("unchecked")
            Map<String, Long> statusBreakdown = (Map<String, Long>) data.get("statusBreakdown");
            
            Object[] row = {
                supplierID,
                data.get("totalPayments"),
                String.format("%.2f", (Double) data.get("totalAmount")),
                String.format("%.2f", (Double) data.get("averagePayment")),
                statusBreakdown.getOrDefault(Payment.STATUS_COMPLETED, 0L),
                statusBreakdown.getOrDefault(Payment.STATUS_PENDING, 0L),
                statusBreakdown.entrySet().stream()
                        .filter(e -> !e.getKey().equals(Payment.STATUS_COMPLETED) && 
                                   !e.getKey().equals(Payment.STATUS_PENDING))
                        .mapToLong(Map.Entry::getValue)
                        .sum()
            };
            model.addRow(row);
        }
        
        return model;
    }
    
    // ============ EXPORT FUNCTIONALITY ============
    
    /**
     * Export financial report to text file
     */
    public boolean exportReportToFile(String filename) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            writer.write("FINANCIAL REPORT");
            writer.newLine();
            writer.write("Generated on: " + LocalDate.now().format(DATE_FORMATTER));
            writer.newLine();
            writer.write("=====================================");
            writer.newLine();
            writer.newLine();
            
            // Financial Summary
            Map<String, Object> summary = generateFinancialSummary();
            writer.write("FINANCIAL SUMMARY");
            writer.newLine();
            writer.write("----------------");
            writer.newLine();
            writer.write("Total Payments: " + summary.get("totalPayments"));
            writer.newLine();
            writer.write("Total Amount: RM " + String.format("%.2f", (Double) summary.get("totalAmount")));
            writer.newLine();
            writer.write("Average Payment: RM " + String.format("%.2f", (Double) summary.get("averagePayment")));
            writer.newLine();
            writer.newLine();
            
            // Status Breakdown
            writer.write("STATUS BREAKDOWN");
            writer.newLine();
            writer.write("---------------");
            writer.newLine();
            @SuppressWarnings("unchecked")
            Map<String, Double> statusAmounts = (Map<String, Double>) summary.get("statusAmounts");
            for (Map.Entry<String, Double> entry : statusAmounts.entrySet()) {
                writer.write(entry.getKey() + ": RM " + String.format("%.2f", entry.getValue()));
                writer.newLine();
            }
            writer.newLine();
            
            // Payment Methods
            writer.write("PAYMENT METHODS");
            writer.newLine();
            writer.write("--------------");
            writer.newLine();
            @SuppressWarnings("unchecked")
            Map<String, Long> methodCounts = (Map<String, Long>) summary.get("methodCounts");
            for (Map.Entry<String, Long> entry : methodCounts.entrySet()) {
                writer.write(entry.getKey() + ": " + entry.getValue() + " payments");
                writer.newLine();
            }
            writer.newLine();
            
            // Pending Payments Alert
            Map<String, Object> pendingReport = generatePendingPaymentsReport();
            writer.write("PENDING PAYMENTS ALERT");
            writer.newLine();
            writer.write("---------------------");
            writer.newLine();
            writer.write("Total Pending: " + pendingReport.get("totalPending"));
            writer.newLine();
            writer.write("Pending Amount: RM " + String.format("%.2f", (Double) pendingReport.get("totalAmount")));
            writer.newLine();
            
            return true;
        } catch (IOException e) {
            System.err.println("Error exporting report: " + e.getMessage());
            return false;
        }
    }
    
    // ============ UTILITY METHODS ============
    
    /**
     * Refresh payment data
     */
    public void refreshData() {
        this.allPayments = Payment.loadFinanceRecords();
    }
    
    /**
     * Get payments within date range
     */
    public List<Payment> getPaymentsInDateRange(LocalDate startDate, LocalDate endDate) {
        return allPayments.stream()
                .filter(p -> !p.getPaymentDate().isBefore(startDate) && 
                           !p.getPaymentDate().isAfter(endDate))
                .collect(Collectors.toList());
    }
    
    /**
     * Calculate completion rate
     */
    public double getCompletionRate() {
        long completedCount = allPayments.stream()
                .mapToLong(p -> p.getStatus().equalsIgnoreCase(Payment.STATUS_COMPLETED) ? 1 : 0)
                .sum();
        
        return allPayments.isEmpty() ? 0.0 : (double) completedCount / allPayments.size() * 100;
    }
    
    /**
     * Get largest payment
     */
    public Payment getLargestPayment() {
        return allPayments.stream()
                .max(Comparator.comparing(Payment::getAmount))
                .orElse(null);
    }
    
    /**
     * Get most recent payment
     */
    public Payment getMostRecentPayment() {
        return allPayments.stream()
                .max(Comparator.comparing(Payment::getPaymentDate))
                .orElse(null);
    }
}