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
        setSize(1200, 700);
        
        // Create main panel with BorderLayout (matching FM_ViewPO)
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(245, 245, 245));
        
        // Header panel (matching FM_ViewPO style)
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Date filter panel
        JPanel filterPanel = createDateFilterPanel();
        
        // Content panel
        JPanel contentPanel = createContentPanel();
        
        // Combine filter and content panels
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(filterPanel, BorderLayout.NORTH);
        centerPanel.add(contentPanel, BorderLayout.CENTER);
        
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        
        // Footer panel (matching FM_ViewPO style)
        JPanel footerPanel = createFooterPanel();
        mainPanel.add(footerPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    /**
     * Create the header panel with title and back button (matching FM_ViewPO style)
     */
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(51, 51, 51));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        // Title label
        JLabel titleLabel = new JLabel("Financial Reports");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        
        // Date info label
        JLabel dateLabel = new JLabel("Generated: " + LocalDate.now().format(DATE_FORMATTER));
        dateLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        dateLabel.setForeground(new Color(200, 200, 200));
        
        // Title panel with date
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(new Color(51, 51, 51));
        titlePanel.add(titleLabel, BorderLayout.WEST);
        titlePanel.add(dateLabel, BorderLayout.EAST);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(new Color(51, 51, 51));
        
        backBtn = new JButton("Back to Dashboard");
        backBtn.setBackground(new Color(105, 105, 105));
        backBtn.setForeground(Color.WHITE);
        backBtn.setFont(new Font("Arial", Font.BOLD, 12));
        backBtn.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        backBtn.setFocusPainted(false);
        backBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                goBack();
            }
        });
        
        buttonPanel.add(backBtn);
        
        headerPanel.add(titlePanel, BorderLayout.WEST);
        headerPanel.add(buttonPanel, BorderLayout.EAST);
        
        return headerPanel;
    }
    
    /**
     * Create the date filter panel
     */
    private JPanel createDateFilterPanel() {
        JPanel filterPanel = new JPanel(new BorderLayout());
        filterPanel.setBackground(new Color(245, 245, 245));
        filterPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(10, 30, 10, 30),
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(70, 130, 180), 1),
                "Date Filter Options",
                0, 0,
                new Font("Arial", Font.BOLD, 12),
                new Color(70, 130, 180)
            )
        ));
        
        // Main filter content panel
        JPanel filterContent = new JPanel(new GridBagLayout());
        filterContent.setBackground(new Color(245, 245, 245));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        
        // Preset filters row
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
        JLabel presetLabel = new JLabel("Quick Filters:");
        presetLabel.setFont(new Font("Arial", Font.BOLD, 12));
        filterContent.add(presetLabel, gbc);
        
        String[] presetOptions = {
            "No Filter", "Last 7 Days", "Last 30 Days", 
            "Current Month", "Last Month", "Current Year"
        };
        
        gbc.gridx = 1;
        presetFilterCombo = new JComboBox<>(presetOptions);
        presetFilterCombo.setPreferredSize(new Dimension(150, 25));
        presetFilterCombo.setFont(new Font("Arial", Font.PLAIN, 11));
        presetFilterCombo.addActionListener(e -> applyPresetFilter());
        filterContent.add(presetFilterCombo, gbc);
        
        // Custom range row
        gbc.gridx = 0; gbc.gridy = 1;
        JLabel customLabel = new JLabel("Custom Range:");
        customLabel.setFont(new Font("Arial", Font.BOLD, 12));
        filterContent.add(customLabel, gbc);
        
        gbc.gridx = 1;
        JPanel customPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        customPanel.setBackground(new Color(245, 245, 245));
        
        JLabel startLabel = new JLabel("From:");
        startLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        startDateField = new JTextField(8);
        startDateField.setFont(new Font("Arial", Font.PLAIN, 11));
        startDateField.setToolTipText("Enter date in dd/MM/yyyy format");
        
        JLabel endLabel = new JLabel("To:");
        endLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        endDateField = new JTextField(8);
        endDateField.setFont(new Font("Arial", Font.PLAIN, 11));
        endDateField.setToolTipText("Enter date in dd/MM/yyyy format");
        
        applyFilterBtn = createSmallButton("Apply Filter", new Color(34, 139, 34));
        applyFilterBtn.addActionListener(e -> applyCustomFilter());
        
        clearFilterBtn = createSmallButton("Clear Filter", new Color(220, 20, 60));
        clearFilterBtn.addActionListener(e -> clearDateFilter());
        
        customPanel.add(startLabel);
        customPanel.add(startDateField);
        customPanel.add(endLabel);
        customPanel.add(endDateField);
        customPanel.add(applyFilterBtn);
        customPanel.add(clearFilterBtn);
        
        filterContent.add(customPanel, gbc);
        
        // Filter status row
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        filterStatusLabel = new JLabel("Filter Status: No filter applied");
        filterStatusLabel.setFont(new Font("Arial", Font.ITALIC, 11));
        filterStatusLabel.setForeground(new Color(105, 105, 105));
        filterContent.add(filterStatusLabel, gbc);
        
        filterPanel.add(filterContent, BorderLayout.CENTER);
        
        return filterPanel;
    }
    
    /**
     * Create small styled button for filter panel
     */
    private JButton createSmallButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setBackground(backgroundColor);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 10));
        button.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(85, 22));
        return button;
    }
    
    /**
     * Create the main content panel
     */
    private JPanel createContentPanel() {
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(new Color(245, 245, 245));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 30, 20, 30));
        
        // Subtitle
        JLabel subtitleLabel = new JLabel("Financial Report Analysis");
        subtitleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        subtitleLabel.setForeground(new Color(51, 51, 51));
        subtitleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        contentPanel.add(subtitleLabel, BorderLayout.NORTH);
        
        // Create tabbed pane for the two reports
        tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(new Color(245, 245, 245));
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 12));
        
        // Add the two report tabs
        tabbedPane.addTab("Summary Report", createSummaryPanel());
        tabbedPane.addTab("Detailed Report", createDetailedPanel());
        
        contentPanel.add(tabbedPane, BorderLayout.CENTER);
        
        // Action buttons panel (matching FM_ViewPO style)
        JPanel actionPanel = createActionPanel();
        contentPanel.add(actionPanel, BorderLayout.SOUTH);
        
        return contentPanel;
    }
    
    /**
     * Create action buttons panel with styled buttons matching FM_ViewPO
     */
    private JPanel createActionPanel() {
        JPanel actionPanel = new JPanel(new GridBagLayout());
        actionPanel.setBackground(new Color(245, 245, 245));
        actionPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        
        // Create styled buttons matching FM_ViewPO style
        exportReportBtn = createStyledActionButton("Export Reports", 
            "Export financial reports to file", new Color(70, 130, 180));
        exportReportBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                exportReports();
            }
        });
        
        refreshBtn = createStyledActionButton("Refresh Data", 
            "Reload all financial data", new Color(34, 139, 34));
        refreshBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refreshData();
            }
        });
        
        // Add buttons to panel in a row
        gbc.gridx = 0; gbc.gridy = 0;
        actionPanel.add(exportReportBtn, gbc);
        
        gbc.gridx = 1; gbc.gridy = 0;
        actionPanel.add(refreshBtn, gbc);
        
        return actionPanel;
    }
    
    /**
     * Create a styled action button matching FM_ViewPO style
     */
    private JButton createStyledActionButton(String title, String description, Color bgColor) {
        JButton button = new JButton();
        button.setLayout(new BorderLayout());
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(200, 80));
        
        JLabel titleLabel = new JLabel(title, JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 13));
        titleLabel.setForeground(Color.WHITE);
        
        JLabel descLabel = new JLabel("<html><center>" + description + "</center></html>", JLabel.CENTER);
        descLabel.setFont(new Font("Arial", Font.PLAIN, 10));
        descLabel.setForeground(new Color(230, 230, 230));
        
        button.add(titleLabel, BorderLayout.CENTER);
        button.add(descLabel, BorderLayout.SOUTH);
        
        // Add hover effect (matching FM_ViewPO)
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor.brighter());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });
        
        return button;
    }
    
    /**
     * Create footer panel with system info and status (matching FM_ViewPO style)
     */
    private JPanel createFooterPanel() {
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setBackground(new Color(245, 245, 245));
        footerPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 15, 0));
        
        // Status panel
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusPanel.setBackground(new Color(245, 245, 245));
        statusPanel.setBorder(BorderFactory.createEmptyBorder(0, 25, 0, 25));
        
        statusLabel = new JLabel("Reports loaded successfully.");
        statusLabel.setFont(new Font("Arial", Font.ITALIC, 11));
        statusLabel.setForeground(new Color(34, 139, 34));
        
        statusPanel.add(statusLabel);
        
        // System info panel
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        infoPanel.setBackground(new Color(245, 245, 245));
        
        JLabel footerLabel = new JLabel("OWSB - AUTOMATED PURCHASE ORDER MANAGEMENT SYSTEM");
        footerLabel.setFont(new Font("Arial", Font.ITALIC, 10));
        footerLabel.setForeground(Color.GRAY);
        
        infoPanel.add(footerLabel);
        
        footerPanel.add(statusPanel, BorderLayout.WEST);
        footerPanel.add(infoPanel, BorderLayout.CENTER);
        
        return footerPanel;
    }
    
    /**
     * Create the summary report panel
     */
    private JPanel createSummaryPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(245, 245, 245));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Summary description
        JLabel descLabel = new JLabel("<html><b>Summary Report</b><br>" +
            "Overview of total payments, amounts, and completion statistics<br>" +
            "<i>Use date filters above to focus on specific time periods</i></html>");
        descLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        descLabel.setForeground(new Color(51, 51, 51));
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
        summaryTable.setRowHeight(30);
        summaryTable.setFont(new Font("Arial", Font.PLAIN, 12));
        summaryTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        summaryTable.setSelectionBackground(new Color(184, 207, 229));
        summaryTable.setGridColor(new Color(220, 220, 220));
        
        // Set column widths
        summaryTable.getColumnModel().getColumn(0).setPreferredWidth(250);
        summaryTable.getColumnModel().getColumn(1).setPreferredWidth(200);
        
        JScrollPane summaryScrollPane = new JScrollPane(summaryTable);
        summaryScrollPane.setBorder(BorderFactory.createLoweredBevelBorder());
        summaryScrollPane.setPreferredSize(new Dimension(500, 300));
        
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
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Detailed description
        JLabel descLabel = new JLabel("<html><b>Detailed Report</b><br>" +
            "Complete list of all payments with full details<br>" +
            "<i>Filtered results based on current date filter settings</i></html>");
        descLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        descLabel.setForeground(new Color(51, 51, 51));
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
        detailedTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        detailedTable.setRowHeight(30);
        detailedTable.getTableHeader().setReorderingAllowed(false);
        detailedTable.setFont(new Font("Arial", Font.PLAIN, 12));
        detailedTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        detailedTable.setSelectionBackground(new Color(184, 207, 229));
        detailedTable.setGridColor(new Color(220, 220, 220));
        
        // Set column widths
        detailedTable.getColumnModel().getColumn(0).setPreferredWidth(100);
        detailedTable.getColumnModel().getColumn(1).setPreferredWidth(120);
        detailedTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        detailedTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        detailedTable.getColumnModel().getColumn(4).setPreferredWidth(100);
        detailedTable.getColumnModel().getColumn(5).setPreferredWidth(120);
        
        JScrollPane detailedScrollPane = new JScrollPane(detailedTable);
        detailedScrollPane.setBorder(BorderFactory.createLoweredBevelBorder());
        
        panel.add(descLabel, BorderLayout.NORTH);
        panel.add(detailedScrollPane, BorderLayout.CENTER);
        
        return panel;
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