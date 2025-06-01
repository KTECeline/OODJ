package com.mycompany.owsb.view;

import com.mycompany.owsb.model.FinanceManager;
import com.mycompany.owsb.model.Finance_Report;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Map;

/**
 * Enhanced Window for Finance Manager Reports with Date Filtering
 * Shows Summary Report and Detailed Report with date range filtering capabilities
 */
public class FM_Report extends javax.swing.JFrame {
    
    private final FinanceManagerWindow parentWindow;
    private final FinanceManager financeManager;
    private final Finance_Report financeReport;
    private JTabbedPane tabbedPane;
    private JTable summaryTable;
    private JTable detailedTable;
    private JLabel statusLabel;
    private JButton exportReportBtn;
    private JButton refreshBtn;
    private JButton backBtn;
    
    // Date filtering components
    private JTextField startDateField;
    private JTextField endDateField;
    private JButton applyFilterBtn;
    private JButton clearFilterBtn;
    private JComboBox<String> presetFilterCombo;
    private JLabel filterStatusLabel;
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    /**
     * Creates new enhanced FM_Report window with date filtering
     */
    public FM_Report(FinanceManagerWindow parent, FinanceManager manager) {
        this.parentWindow = parent;
        this.financeManager = manager;
        this.financeReport = new Finance_Report();
        initComponents();
        loadReports();
        updateFilterStatus();
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
        
        // Date filter panel
        JPanel filterPanel = createDateFilterPanel();
        mainPanel.add(filterPanel, BorderLayout.NORTH);
        
        // Create tabbed pane for the two reports
        tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(new Color(245, 245, 245));
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 14));
        
        // Add the two report tabs
        tabbedPane.addTab("Summary Report", createSummaryPanel());
        tabbedPane.addTab("Detailed Report", createDetailedPanel());
        
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        
        // Bottom panel with controls
        JPanel bottomPanel = createBottomPanel();
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    /**
     * Create the title panel
     */
    private JPanel createTitlePanel() {
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(new Color(255, 140, 0));
        titlePanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
        
        JLabel titleLabel = new JLabel("Financial Reports");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        
        JLabel dateLabel = new JLabel("Generated: " + LocalDate.now().format(DATE_FORMATTER));
        dateLabel.setForeground(Color.WHITE);
        dateLabel.setFont(new Font("Arial", Font.ITALIC, 14));
        
        titlePanel.add(titleLabel, BorderLayout.WEST);
        titlePanel.add(dateLabel, BorderLayout.EAST);
        
        return titlePanel;
    }
    
    /**
     * Create the date filter panel
     */
    private JPanel createDateFilterPanel() {
        JPanel filterPanel = new JPanel(new BorderLayout());
        filterPanel.setBackground(new Color(230, 230, 250));
        filterPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(70, 130, 180), 2),
            "Date Filter Options",
            0, 0,
            new Font("Arial", Font.BOLD, 14),
            new Color(70, 130, 180)
        ));
        
        // Top row - Preset filters
        JPanel presetPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        presetPanel.setBackground(new Color(230, 230, 250));
        
        JLabel presetLabel = new JLabel("Quick Filters:");
        presetLabel.setFont(new Font("Arial", Font.BOLD, 12));
        
        String[] presetOptions = {
            "No Filter",
            "Last 7 Days",
            "Last 30 Days",
            "Current Month",
            "Last Month",
            "Current Year"
        };
        
        presetFilterCombo = new JComboBox<>(presetOptions);
        presetFilterCombo.setPreferredSize(new Dimension(150, 30));
        presetFilterCombo.addActionListener(e -> applyPresetFilter());
        
        presetPanel.add(presetLabel);
        presetPanel.add(presetFilterCombo);
        
        // Middle row - Custom date range
        JPanel customPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        customPanel.setBackground(new Color(230, 230, 250));
        
        JLabel customLabel = new JLabel("Custom Range:");
        customLabel.setFont(new Font("Arial", Font.BOLD, 12));
        
        JLabel startLabel = new JLabel("From:");
        startDateField = new JTextField(10);
        startDateField.setToolTipText("Enter date in dd/MM/yyyy format");
        
        JLabel endLabel = new JLabel("To:");
        endDateField = new JTextField(10);
        endDateField.setToolTipText("Enter date in dd/MM/yyyy format");
        
        applyFilterBtn = createSmallButton("Apply Filter", new Color(34, 139, 34));
        applyFilterBtn.addActionListener(e -> applyCustomFilter());
        
        clearFilterBtn = createSmallButton("Clear Filter", new Color(220, 20, 60));
        clearFilterBtn.addActionListener(e -> clearDateFilter());
        
        customPanel.add(customLabel);
        customPanel.add(startLabel);
        customPanel.add(startDateField);
        customPanel.add(endLabel);
        customPanel.add(endDateField);
        customPanel.add(applyFilterBtn);
        customPanel.add(clearFilterBtn);
        
        // Bottom row - Filter status
        JPanel statusFilterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        statusFilterPanel.setBackground(new Color(230, 230, 250));
        
        filterStatusLabel = new JLabel("Filter Status: No filter applied");
        filterStatusLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        filterStatusLabel.setForeground(new Color(105, 105, 105));
        
        statusFilterPanel.add(filterStatusLabel);
        
        // Combine all rows
        JPanel combinedPanel = new JPanel(new BorderLayout());
        combinedPanel.setBackground(new Color(230, 230, 250));
        combinedPanel.add(presetPanel, BorderLayout.NORTH);
        combinedPanel.add(customPanel, BorderLayout.CENTER);
        combinedPanel.add(statusFilterPanel, BorderLayout.SOUTH);
        
        filterPanel.add(combinedPanel, BorderLayout.CENTER);
        
        return filterPanel;
    }
    
    /**
     * Create small styled button for filter panel
     */
    private JButton createSmallButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setBackground(backgroundColor);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 11));
        button.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(100, 25));
        return button;
    }
    
    /**
     * Apply preset filter based on combo box selection
     */
    private void applyPresetFilter() {
        String selected = (String) presetFilterCombo.getSelectedItem();
        
        try {
            switch (selected) {
                case "No Filter":
                    financeReport.clearDateFilter();
                    startDateField.setText("");
                    endDateField.setText("");
                    break;
                case "Last 7 Days":
                    financeReport.setLast7DaysFilter();
                    break;
                case "Last 30 Days":
                    financeReport.setLast30DaysFilter();
                    break;
                case "Current Month":
                    financeReport.setCurrentMonthFilter();
                    break;
                case "Last Month":
                    financeReport.setLastMonthFilter();
                    break;
                case "Current Year":
                    financeReport.setCurrentYearFilter();
                    break;
            }
            
            updateFilterStatus();
            loadReports();
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error applying preset filter: " + e.getMessage(),
                "Filter Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Apply custom date filter
     */
    private void applyCustomFilter() {
        String startDateStr = startDateField.getText().trim();
        String endDateStr = endDateField.getText().trim();
        
        try {
            // Reset combo box to indicate custom filter
            presetFilterCombo.setSelectedIndex(0);
            
            financeReport.setDateFilter(
                startDateStr.isEmpty() ? null : startDateStr,
                endDateStr.isEmpty() ? null : endDateStr
            );
            
            updateFilterStatus();
            loadReports();
            
            statusLabel.setText("Custom date filter applied successfully");
            statusLabel.setForeground(new Color(34, 139, 34));
            
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this,
                "Invalid date format. Please use dd/MM/yyyy format.\nExample: 01/01/2024",
                "Date Format Error",
                JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this,
                "Invalid date range: " + e.getMessage(),
                "Date Range Error",
                JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error applying date filter: " + e.getMessage(),
                "Filter Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Clear date filter
     */
    private void clearDateFilter() {
        financeReport.clearDateFilter();
        startDateField.setText("");
        endDateField.setText("");
        presetFilterCombo.setSelectedIndex(0);
        
        updateFilterStatus();
        loadReports();
        
        statusLabel.setText("Date filter cleared");
        statusLabel.setForeground(new Color(34, 139, 34));
    }
    
    /**
     * Update filter status label
     */
    private void updateFilterStatus() {
        Map<String, String> filterInfo = financeReport.getDateFilterInfo();
        
        if (Boolean.parseBoolean(filterInfo.get("active"))) {
            String startDate = filterInfo.get("startDate");
            String endDate = filterInfo.get("endDate");
            
            filterStatusLabel.setText(String.format("Filter Active: %s to %s", 
                startDate.equals("Not Set") ? "Beginning" : startDate,
                endDate.equals("Not Set") ? "Present" : endDate));
            filterStatusLabel.setForeground(new Color(34, 139, 34));
        } else {
            filterStatusLabel.setText("Filter Status: No filter applied");
            filterStatusLabel.setForeground(new Color(105, 105, 105));
        }
    }
    
    /**
     * Create the summary report panel
     */
    private JPanel createSummaryPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(245, 245, 245));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Summary description
        JLabel descLabel = new JLabel("<html><h3>Summary Report</h3>" +
            "Overview of total payments, amounts, and completion statistics<br>" +
            "<i>Use date filters above to focus on specific time periods</i></html>");
        descLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        
        // Summary table
        String[] columns = {"Metric", "Value"};
        DefaultTableModel summaryModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        summaryTable = new JTable(summaryModel);
        summaryTable.setRowHeight(35);
        summaryTable.setFont(new Font("Arial", Font.PLAIN, 14));
        summaryTable.getTableHeader().setBackground(new Color(70, 130, 180));
        summaryTable.getTableHeader().setForeground(Color.WHITE);
        summaryTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        
        // Set column widths
        summaryTable.getColumnModel().getColumn(0).setPreferredWidth(250);
        summaryTable.getColumnModel().getColumn(1).setPreferredWidth(200);
        
        JScrollPane summaryScrollPane = new JScrollPane(summaryTable);
        summaryScrollPane.setPreferredSize(new Dimension(500, 400));
        
        // Center the table
        JPanel tablePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        tablePanel.setBackground(new Color(245, 245, 245));
        tablePanel.add(summaryScrollPane);
        
        panel.add(descLabel, BorderLayout.NORTH);
        panel.add(tablePanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Create the detailed report panel
     */
    private JPanel createDetailedPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(245, 245, 245));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Detailed description
        JLabel descLabel = new JLabel("<html><h3>Detailed Report</h3>" +
            "Complete list of all payments with full details<br>" +
            "<i>Filtered results based on current date filter settings</i></html>");
        descLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        
        // Detailed table
        String[] columns = {"Payment ID", "Supplier", "Amount (RM)", "Status", "Date", "Payment Method"};
        DefaultTableModel detailedModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        detailedTable = new JTable(detailedModel);
        detailedTable.setRowHeight(30);
        detailedTable.setFont(new Font("Arial", Font.PLAIN, 12));
        detailedTable.getTableHeader().setBackground(new Color(34, 139, 34));
        detailedTable.getTableHeader().setForeground(Color.WHITE);
        detailedTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        
        // Set column widths
        detailedTable.getColumnModel().getColumn(0).setPreferredWidth(100);
        detailedTable.getColumnModel().getColumn(1).setPreferredWidth(120);
        detailedTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        detailedTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        detailedTable.getColumnModel().getColumn(4).setPreferredWidth(100);
        detailedTable.getColumnModel().getColumn(5).setPreferredWidth(120);
        
        JScrollPane detailedScrollPane = new JScrollPane(detailedTable);
        
        panel.add(descLabel, BorderLayout.NORTH);
        panel.add(detailedScrollPane, BorderLayout.CENTER);
        
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
        statusPanel.setBorder(BorderFactory.createEmptyBorder(10, 25, 5, 25));
        
        statusLabel = new JLabel("Reports loaded successfully.");
        statusLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        statusLabel.setForeground(new Color(34, 139, 34));
        
        statusPanel.add(statusLabel);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        buttonPanel.setBackground(new Color(245, 245, 245));
        
        exportReportBtn = createStyledButton("Export Reports", new Color(70, 130, 180));
        exportReportBtn.addActionListener(e -> exportReports());
        
        refreshBtn = createStyledButton("Refresh Data", new Color(255, 140, 0));
        refreshBtn.addActionListener(e -> refreshData());
        
        backBtn = createStyledButton("Back to Dashboard", new Color(105, 105, 105));
        backBtn.addActionListener(e -> goBack());
        
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
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBorder(BorderFactory.createEmptyBorder(12, 25, 12, 25));
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(180, 45));
        return button;
    }
    
    /**
     * Load both reports
     */
    private void loadReports() {
        try {
            // Refresh data first
            financeReport.refreshData();
            
            // Load summary report
            DefaultTableModel summaryModel = financeReport.generateSummaryTableModel();
            summaryTable.setModel(summaryModel);
            
            // Load detailed report
            DefaultTableModel detailedModel = financeReport.generateDetailedTableModel();
            detailedTable.setModel(detailedModel);
            
            // Update status with summary info
            Map<String, Object> summary = financeReport.generateSummaryReport();
            String filterInfo = financeReport.isDateFilterActive() ? " (Filtered)" : "";
            statusLabel.setText(String.format("Reports loaded%s: %d total payments, RM %.2f total amount", 
                filterInfo, (Integer) summary.get("totalPayments"), (Double) summary.get("totalAmount")));
            statusLabel.setForeground(new Color(34, 139, 34));
            
        } catch (Exception e) {
            statusLabel.setText("Error loading reports: " + e.getMessage());
            statusLabel.setForeground(Color.RED);
        }
    }
    
    /**
     * Export both reports to file
     */
    private void exportReports() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Financial Reports");
        
        String filename = "Financial_Reports_" + 
            LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        
        if (financeReport.isDateFilterActive()) {
            filename += "_Filtered";
        }
        
        fileChooser.setSelectedFile(new java.io.File(filename + ".txt"));
        
        int userSelection = fileChooser.showSaveDialog(this);
        
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            String selectedFilename = fileChooser.getSelectedFile().getAbsolutePath();
            
            statusLabel.setText("Exporting reports...");
            statusLabel.setForeground(new Color(255, 140, 0));
            
            if (financeReport.exportReportsToFile(selectedFilename)) {
                statusLabel.setText("Reports exported successfully to: " + selectedFilename);
                statusLabel.setForeground(new Color(34, 139, 34));
                
                String message = "Financial reports exported successfully to:\n" + selectedFilename;
                if (financeReport.isDateFilterActive()) {
                    Map<String, String> filterInfo = financeReport.getDateFilterInfo();
                    message += "\n\nNote: Reports include date filter from " + 
                              filterInfo.get("startDate") + " to " + filterInfo.get("endDate");
                }
                
                JOptionPane.showMessageDialog(this, message, "Export Successful", JOptionPane.INFORMATION_MESSAGE);
            } else {
                statusLabel.setText("Failed to export reports");
                statusLabel.setForeground(Color.RED);
                
                JOptionPane.showMessageDialog(this,
                    "Failed to export reports. Please try again.",
                    "Export Failed",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * Refresh all data
     */
    private void refreshData() {
        statusLabel.setText("Refreshing data...");
        statusLabel.setForeground(new Color(255, 140, 0));
        
        loadReports();
        updateFilterStatus();
    }
    
    /**
     * Go back to the main Finance Manager window
     */
    private void goBack() {
        this.dispose();
        parentWindow.setVisible(true);
    }
}