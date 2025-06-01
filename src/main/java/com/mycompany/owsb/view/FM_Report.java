package com.mycompany.owsb.view;

import com.mycompany.owsb.model.FinanceManager;
import com.mycompany.owsb.model.Finance_Report;
import com.mycompany.owsb.model.Payment;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * Window for Finance Manager to generate and view financial reports
 */
public class FM_Report extends javax.swing.JFrame {
    
    private final FinanceManagerWindow parentWindow;
    private final FinanceManager financeManager;
    private final Finance_Report financeReport;
    private JTabbedPane tabbedPane;
    private JTable summaryTable;
    private JTable statusTable;
    private JTable supplierTable;
    private JTable trendsTable;
    private JLabel statusLabel;
    private JComboBox<String> monthCombo;
    private JComboBox<String> yearCombo;
    private JComboBox<String> statusFilterCombo;
    private JButton generateReportBtn;
    private JButton exportReportBtn;
    private JButton refreshBtn;
    private JButton backBtn;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    /**
     * Creates new FM_Report window
     */
    public FM_Report(FinanceManagerWindow parent, FinanceManager manager) {
        this.parentWindow = parent;
        this.financeManager = manager;
        this.financeReport = new Finance_Report();
        initComponents();
        loadFinancialSummary();
        setLocationRelativeTo(parent);
    }

    /**
     * Initialize the components
     */
    private void initComponents() {
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Financial Reports - Finance Manager");
        setSize(1400, 800);
        
        // Create main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(245, 245, 245));
        
        // Title panel
        JPanel titlePanel = createTitlePanel();
        mainPanel.add(titlePanel, BorderLayout.NORTH);
        
        // Create tabbed pane for different reports
        tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(new Color(245, 245, 245));
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 12));
        
        // Add tabs
        tabbedPane.addTab("Financial Summary", createSummaryPanel());
        tabbedPane.addTab("Monthly Report", createMonthlyReportPanel());
        tabbedPane.addTab("Status Reports", createStatusReportPanel());
        tabbedPane.addTab("Supplier Analysis", createSupplierPanel());
        tabbedPane.addTab("Payment Trends", createTrendsPanel());
        
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        
        // Bottom panel with controls and status
        JPanel bottomPanel = createBottomPanel();
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
        
        // Force layout
        pack();
        setSize(1400, 800);
        validate();
        repaint();
    }
    
    /**
     * Create the title panel
     */
    private JPanel createTitlePanel() {
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(new Color(255, 140, 0));
        titlePanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JLabel titleLabel = new JLabel("Financial Reports & Analytics");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        
        // Date info panel
        JPanel datePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        datePanel.setBackground(new Color(255, 140, 0));
        
        JLabel dateLabel = new JLabel("Report Generated: " + LocalDate.now().format(DATE_FORMATTER));
        dateLabel.setForeground(Color.WHITE);
        dateLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        
        datePanel.add(dateLabel);
        
        titlePanel.add(titleLabel, BorderLayout.WEST);
        titlePanel.add(datePanel, BorderLayout.EAST);
        
        return titlePanel;
    }
    
    /**
     * Create the financial summary panel
     */
    private JPanel createSummaryPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(245, 245, 245));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Summary table
        String[] columns = {"Metric", "Value"};
        DefaultTableModel summaryModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        summaryTable = new JTable(summaryModel);
        summaryTable.setRowHeight(30);
        summaryTable.getTableHeader().setBackground(new Color(255, 140, 0));
        summaryTable.getTableHeader().setForeground(Color.WHITE);
        summaryTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        summaryTable.setFont(new Font("Arial", Font.PLAIN, 12));
        
        // Set column widths
        summaryTable.getColumnModel().getColumn(0).setPreferredWidth(200);
        summaryTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        
        JScrollPane summaryScrollPane = new JScrollPane(summaryTable);
        summaryScrollPane.setPreferredSize(new Dimension(400, 300));
        
        // Key metrics panel
        JPanel metricsPanel = new JPanel(new GridLayout(2, 2, 15, 15));
        metricsPanel.setBackground(new Color(245, 245, 245));
        metricsPanel.setBorder(BorderFactory.createTitledBorder("Key Performance Indicators"));
        
        JPanel totalPanel = createMetricCard("Total Payments", "Loading...", new Color(34, 139, 34));
        JPanel completedPanel = createMetricCard("Completion Rate", "Loading...", new Color(70, 130, 180));
        JPanel pendingPanel = createMetricCard("Pending Amount", "Loading...", new Color(255, 140, 0));
        JPanel avgPanel = createMetricCard("Average Payment", "Loading...", new Color(147, 112, 219));
        
        metricsPanel.add(totalPanel);
        metricsPanel.add(completedPanel);
        metricsPanel.add(pendingPanel);
        metricsPanel.add(avgPanel);
        
        // Layout
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(summaryScrollPane, BorderLayout.WEST);
        topPanel.add(metricsPanel, BorderLayout.CENTER);
        
        panel.add(topPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Create a metric card for KPIs
     */
    private JPanel createMetricCard(String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(color);
        card.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        JLabel titleLabel = new JLabel(title, JLabel.CENTER);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 12));
        
        JLabel valueLabel = new JLabel(value, JLabel.CENTER);
        valueLabel.setForeground(Color.WHITE);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 18));
        
        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        
        return card;
    }
    
    /**
     * Create the monthly report panel
     */
    private JPanel createMonthlyReportPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(245, 245, 245));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Controls panel
        JPanel controlsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controlsPanel.setBackground(new Color(245, 245, 245));
        
        JLabel monthLabel = new JLabel("Month:");
        monthLabel.setFont(new Font("Arial", Font.BOLD, 12));
        
        String[] months = {"January", "February", "March", "April", "May", "June",
                          "July", "August", "September", "October", "November", "December"};
        monthCombo = new JComboBox<>(months);
        monthCombo.setSelectedIndex(LocalDate.now().getMonthValue() - 1);
        
        JLabel yearLabel = new JLabel("Year:");
        yearLabel.setFont(new Font("Arial", Font.BOLD, 12));
        
        String[] years = {"2023", "2024", "2025"};
        yearCombo = new JComboBox<>(years);
        yearCombo.setSelectedItem(String.valueOf(LocalDate.now().getYear()));
        
        JButton generateMonthlyBtn = createStyledButton("Generate Monthly Report", new Color(70, 130, 180));
        generateMonthlyBtn.addActionListener(e -> generateMonthlyReport());
        
        controlsPanel.add(monthLabel);
        controlsPanel.add(monthCombo);
        controlsPanel.add(Box.createHorizontalStrut(15));
        controlsPanel.add(yearLabel);
        controlsPanel.add(yearCombo);
        controlsPanel.add(Box.createHorizontalStrut(15));
        controlsPanel.add(generateMonthlyBtn);
        
        // Monthly report table
        String[] monthlyColumns = {"Order ID", "Supplier", "Amount", "Status", "Payment Date", "Method"};
        DefaultTableModel monthlyModel = new DefaultTableModel(monthlyColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable monthlyTable = new JTable(monthlyModel);
        monthlyTable.setRowHeight(28);
        monthlyTable.getTableHeader().setBackground(new Color(70, 130, 180));
        monthlyTable.getTableHeader().setForeground(Color.WHITE);
        monthlyTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        
        JScrollPane monthlyScrollPane = new JScrollPane(monthlyTable);
        
        panel.add(controlsPanel, BorderLayout.NORTH);
        panel.add(monthlyScrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Create the status report panel
     */
    private JPanel createStatusReportPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(245, 245, 245));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Status filter panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.setBackground(new Color(245, 245, 245));
        
        JLabel filterLabel = new JLabel("Filter by Status:");
        filterLabel.setFont(new Font("Arial", Font.BOLD, 12));
        
        String[] statuses = {"All", "PENDING", "COMPLETED", "CANCELLED"};
        statusFilterCombo = new JComboBox<>(statuses);
        
        JButton filterBtn = createStyledButton("Apply Filter", new Color(147, 112, 219));
        filterBtn.addActionListener(e -> filterByStatus());
        
        JButton pendingReportBtn = createStyledButton("Pending Payments", new Color(255, 140, 0));
        pendingReportBtn.addActionListener(e -> showPendingPayments());
        
        JButton overdueReportBtn = createStyledButton("Overdue Payments", new Color(220, 20, 60));
        overdueReportBtn.addActionListener(e -> showOverduePayments());
        
        filterPanel.add(filterLabel);
        filterPanel.add(statusFilterCombo);
        filterPanel.add(filterBtn);
        filterPanel.add(Box.createHorizontalStrut(20));
        filterPanel.add(pendingReportBtn);
        filterPanel.add(overdueReportBtn);
        
        // Status table
        String[] statusColumns = {"Payment ID", "Order IDs", "Supplier", "Amount", "Date", "Method", "Status"};
        DefaultTableModel statusModel = new DefaultTableModel(statusColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        statusTable = new JTable(statusModel);
        statusTable.setRowHeight(28);
        statusTable.getTableHeader().setBackground(new Color(147, 112, 219));
        statusTable.getTableHeader().setForeground(Color.WHITE);
        statusTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        
        JScrollPane statusScrollPane = new JScrollPane(statusTable);
        
        panel.add(filterPanel, BorderLayout.NORTH);
        panel.add(statusScrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Create the supplier analysis panel
     */
    private JPanel createSupplierPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(245, 245, 245));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Supplier table
        String[] supplierColumns = {"Supplier ID", "Total Payments", "Total Amount", "Average Payment", 
                                   "Completed", "Pending", "Others"};
        DefaultTableModel supplierModel = new DefaultTableModel(supplierColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        supplierTable = new JTable(supplierModel);
        supplierTable.setRowHeight(28);
        supplierTable.getTableHeader().setBackground(new Color(34, 139, 34));
        supplierTable.getTableHeader().setForeground(Color.WHITE);
        supplierTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        
        JScrollPane supplierScrollPane = new JScrollPane(supplierTable);
        
        panel.add(supplierScrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Create the trends analysis panel
     */
    private JPanel createTrendsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(245, 245, 245));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Trends table
        String[] trendsColumns = {"Period", "Total Amount", "Payment Count", "Average Payment"};
        DefaultTableModel trendsModel = new DefaultTableModel(trendsColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        trendsTable = new JTable(trendsModel);
        trendsTable.setRowHeight(28);
        trendsTable.getTableHeader().setBackground(new Color(220, 20, 60));
        trendsTable.getTableHeader().setForeground(Color.WHITE);
        trendsTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        
        JScrollPane trendsScrollPane = new JScrollPane(trendsTable);
        
        panel.add(trendsScrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Create the bottom control panel
     */
    private JPanel createBottomPanel() {
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(new Color(245, 245, 245));
        
        // Status panel
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusPanel.setBackground(new Color(245, 245, 245));
        statusPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 5, 20));
        
        statusLabel = new JLabel("Financial reports loaded successfully.");
        statusLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        statusLabel.setForeground(new Color(100, 100, 100));
        
        statusPanel.add(statusLabel);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        buttonPanel.setBackground(new Color(245, 245, 245));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        
        generateReportBtn = createStyledButton("Generate All Reports", new Color(34, 139, 34));
        generateReportBtn.addActionListener(e -> generateAllReports());
        
        exportReportBtn = createStyledButton("Export to File", new Color(70, 130, 180));
        exportReportBtn.addActionListener(e -> exportReport());
        
        refreshBtn = createStyledButton("Refresh Data", new Color(255, 140, 0));
        refreshBtn.addActionListener(e -> refreshAllData());
        
        backBtn = createStyledButton("Back to Dashboard", new Color(105, 105, 105));
        backBtn.addActionListener(e -> goBackToMainMenu());
        
        buttonPanel.add(generateReportBtn);
        buttonPanel.add(exportReportBtn);
        buttonPanel.add(refreshBtn);
        buttonPanel.add(backBtn);
        
        bottomPanel.add(statusPanel, BorderLayout.NORTH);
        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        return bottomPanel;
    }
    
    /**
     * Helper method to create styled buttons
     */
    private JButton createStyledButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setBackground(backgroundColor);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(180, 40));
        return button;
    }
    
    /**
     * Load financial summary data
     */
    private void loadFinancialSummary() {
        try {
            // Refresh data first
            financeReport.refreshData();
            
            // Load summary table
            DefaultTableModel summaryModel = financeReport.generateSummaryTableModel();
            summaryTable.setModel(summaryModel);
            
            // Load supplier report
            DefaultTableModel supplierModel = financeReport.generateSupplierReportTableModel();
            supplierTable.setModel(supplierModel);
            
            // Load payment trends
            loadPaymentTrends();
            
            // Update KPI cards
            updateKPICards();
            
            statusLabel.setText("Financial reports loaded successfully.");
            statusLabel.setForeground(new Color(34, 139, 34));
            
        } catch (Exception e) {
            statusLabel.setText("Error loading financial data: " + e.getMessage());
            statusLabel.setForeground(Color.RED);
        }
    }
    
    /**
     * Load payment trends data
     */
    private void loadPaymentTrends() {
        Map<String, Object> trends = financeReport.generatePaymentTrends();
        
        @SuppressWarnings("unchecked")
        Map<String, Double> monthlyTrends = (Map<String, Double>) trends.get("monthlyTrends");
        
        DefaultTableModel trendsModel = (DefaultTableModel) trendsTable.getModel();
        trendsModel.setRowCount(0);
        
        for (Map.Entry<String, Double> entry : monthlyTrends.entrySet()) {
            Object[] row = {
                entry.getKey(),
                String.format("RM %.2f", entry.getValue()),
                "N/A", // Payment count would need additional logic
                "N/A"  // Average would need additional logic
            };
            trendsModel.addRow(row);
        }
    }
    
    /**
     * Update KPI cards with actual data
     */
    private void updateKPICards() {
        Map<String, Object> summary = financeReport.generateFinancialSummary();
        
        // This would need to be implemented by updating the metric cards
        // For now, we'll update the status label with key metrics
        int totalPayments = (Integer) summary.get("totalPayments");
        double totalAmount = (Double) summary.get("totalAmount");
        double avgPayment = (Double) summary.get("averagePayment");
        double completionRate = financeReport.getCompletionRate();
        
        String metrics = String.format(
            "Key Metrics - Total: %d payments | Amount: RM %.2f | Average: RM %.2f | Completion: %.1f%%",
            totalPayments, totalAmount, avgPayment, completionRate
        );
        
        statusLabel.setText(metrics);
    }
    
    /**
     * Generate monthly report
     */
    private void generateMonthlyReport() {
        int month = monthCombo.getSelectedIndex() + 1;
        int year = Integer.parseInt((String) yearCombo.getSelectedItem());
        
        Map<String, Object> monthlyReport = financeReport.generateMonthlyReport(year, month);
        
        JOptionPane.showMessageDialog(this,
            String.format("Monthly Report for %s %d:\n\n" +
                         "Period: %s\n" +
                         "Total Payments: %s\n" +
                         "Total Amount: RM %.2f",
                         monthCombo.getSelectedItem(), year,
                         monthlyReport.get("period"),
                         monthlyReport.get("totalPayments"),
                         (Double) monthlyReport.get("totalAmount")),
            "Monthly Report",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Filter payments by status
     */
    private void filterByStatus() {
        String selectedStatus = (String) statusFilterCombo.getSelectedItem();
        
        if ("All".equals(selectedStatus)) {
            // Load all payments - would need to implement this method
            statusLabel.setText("Showing all payments");
        } else {
            DefaultTableModel statusModel = financeReport.generateStatusReportTableModel(selectedStatus);
            statusTable.setModel(statusModel);
            statusLabel.setText("Filtered by status: " + selectedStatus);
        }
    }
    
    /**
     * Show pending payments report
     */
    private void showPendingPayments() {
        Map<String, Object> pendingReport = financeReport.generatePendingPaymentsReport();
        
        JOptionPane.showMessageDialog(this,
            String.format("Pending Payments Report:\n\n" +
                         "Total Pending: %s\n" +
                         "Total Amount: RM %.2f",
                         pendingReport.get("totalPending"),
                         (Double) pendingReport.get("totalAmount")),
            "Pending Payments",
            JOptionPane.WARNING_MESSAGE);
    }
    
    /**
     * Show overdue payments report
     */
    private void showOverduePayments() {
        Map<String, Object> overdueReport = financeReport.generateOverduePaymentsReport();
        
        JOptionPane.showMessageDialog(this,
            String.format("Overdue Payments Report:\n\n" +
                         "Total Overdue: %s\n" +
                         "Total Amount: RM %.2f\n\n" +
                         "Note: Payments pending for more than 30 days",
                         overdueReport.get("totalOverdue"),
                         (Double) overdueReport.get("totalAmount")),
            "Overdue Payments",
            JOptionPane.ERROR_MESSAGE);
    }
    
    /**
     * Generate all reports
     */
    private void generateAllReports() {
        try {
            statusLabel.setText("Generating all reports...");
            statusLabel.setForeground(new Color(255, 140, 0));
            
            // Refresh all data
            refreshAllData();
            
            statusLabel.setText("All reports generated successfully!");
            statusLabel.setForeground(new Color(34, 139, 34));
            
            JOptionPane.showMessageDialog(this,
                "All financial reports have been generated and updated successfully!",
                "Reports Generated",
                JOptionPane.INFORMATION_MESSAGE);
                
        } catch (Exception e) {
            statusLabel.setText("Error generating reports: " + e.getMessage());
            statusLabel.setForeground(Color.RED);
        }
    }
    
    /**
     * Export report to file
     */
    private void exportReport() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Financial Report");
        fileChooser.setSelectedFile(new java.io.File("Financial_Report_" + 
            LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".txt"));
        
        int userSelection = fileChooser.showSaveDialog(this);
        
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            String filename = fileChooser.getSelectedFile().getAbsolutePath();
            
            if (financeReport.exportReportToFile(filename)) {
                JOptionPane.showMessageDialog(this,
                    "Financial report exported successfully to:\n" + filename,
                    "Export Successful",
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                    "Failed to export financial report. Please try again.",
                    "Export Failed",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * Refresh all data
     */
    private void refreshAllData() {
        statusLabel.setText("Refreshing financial data...");
        statusLabel.setForeground(new Color(255, 140, 0));
        
        loadFinancialSummary();
        
        statusLabel.setText("Financial data refreshed successfully.");
        statusLabel.setForeground(new Color(34, 139, 34));
    }
    
    /**
     * Go back to the main Finance Manager window
     */
    private void goBackToMainMenu() {
        this.dispose();
        parentWindow.setVisible(true);
    }
}