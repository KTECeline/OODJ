package com.mycompany.owsb.view;

import com.mycompany.owsb.model.FinanceManager;
import com.mycompany.owsb.model.Finance_ViewPR;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.List;
import java.util.ArrayList;

/**
 * Panel for Finance Manager to view and manage Purchase Requisitions
 */
public class FM_ViewFullPR extends javax.swing.JFrame {
    private final FinanceManagerWindow parentWindow;
    private final FinanceManager financeManager;
    private JTable prTable;
    private DefaultTableModel tableModel;
    private JButton refreshBtn;
    private JButton backBtn;
    private JComboBox<String> statusFilterCombo;
    private JComboBox<String> supplierFilterCombo;
    private JTextField searchField;
    private JButton searchBtn;
    private JButton clearFiltersBtn;
    private JLabel totalAmountLabel;
    private JLabel recordCountLabel;
    private List<Finance_ViewPR> allPRRecords;

    /**
     * Creates new form FM_ViewFullPR
     */
    public FM_ViewFullPR(FinanceManagerWindow parent, FinanceManager manager) {
        this.parentWindow = parent;
        this.financeManager = manager;
        this.allPRRecords = new ArrayList<>();
        initComponents();
        loadAllPRs();
        setLocationRelativeTo(parent);
    }

    /**
     * Initialize the components
     */
    private void initComponents() {
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Purchase Requisition Management - Finance Manager");
        setSize(1200, 750); // Match FM_ViewFullPO window size
        
        // Create main panel with BorderLayout (matching FM_ViewPO)
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(245, 245, 245));
        
        // Header panel (matching FM_ViewPO style)
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Content panel
        JPanel contentPanel = createContentPanel();
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        
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
        JLabel titleLabel = new JLabel("Purchase Requisition Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        
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
                goBackToMainMenu();
            }
        });
        
        buttonPanel.add(backBtn);
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(buttonPanel, BorderLayout.EAST);
        
        return headerPanel;
    }
    
    /**
     * Create the main content panel with filters, table and action buttons
     */
    private JPanel createContentPanel() {
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(new Color(245, 245, 245));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        
        // Subtitle
        JLabel subtitleLabel = new JLabel("All Purchase Requisitions");
        subtitleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        subtitleLabel.setForeground(new Color(51, 51, 51));
        subtitleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        contentPanel.add(subtitleLabel, BorderLayout.NORTH);
        
        // Filter panel above the table
        JPanel filterPanel = createFilterPanel();
        JPanel filterWrapper = new JPanel(new BorderLayout());
        filterWrapper.setBackground(new Color(245, 245, 245));
        filterWrapper.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        filterWrapper.add(filterPanel, BorderLayout.CENTER);
        
        // Create table panel
        JPanel tablePanel = createTablePanel();
        
        // Combine filter and table
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(new Color(245, 245, 245));
        centerPanel.add(filterWrapper, BorderLayout.NORTH);
        centerPanel.add(tablePanel, BorderLayout.CENTER);
        
        contentPanel.add(centerPanel, BorderLayout.CENTER);
        
        // Create action button panel and summary panel
        JPanel bottomPanel = createBottomPanel();
        contentPanel.add(bottomPanel, BorderLayout.SOUTH);
        
        return contentPanel;
    }
    
    /**
     * Create filter panel with search and filter options (matching FM_ViewFullPO)
     */
    private JPanel createFilterPanel() {
        JPanel filterPanel = new JPanel(new GridBagLayout());
        filterPanel.setBackground(new Color(245, 245, 245));
        filterPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), 
            "Search & Filter Options",
            0, 2, new Font("Arial", Font.BOLD, 12), new Color(51, 51, 51)));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Search by PR ID
        gbc.gridx = 0; gbc.gridy = 0;
        filterPanel.add(new JLabel("Search PR ID:"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        searchField = new JTextField("Enter PR ID");
        searchField.setFont(new Font("Arial", Font.PLAIN, 12));
        searchField.setForeground(Color.GRAY);
        searchField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (searchField.getText().equals("Enter PR ID")) {
                    searchField.setText("");
                    searchField.setForeground(Color.BLACK);
                }
            }
            
            @Override
            public void focusLost(FocusEvent e) {
                if (searchField.getText().isEmpty()) {
                    searchField.setText("Enter PR ID");
                    searchField.setForeground(Color.GRAY);
                }
            }
        });
        filterPanel.add(searchField, gbc);
        
        gbc.gridx = 2; gbc.gridy = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        searchBtn = new JButton("Search");
        searchBtn.setBackground(new Color(70, 130, 180));
        searchBtn.setForeground(Color.WHITE);
        searchBtn.setFont(new Font("Arial", Font.BOLD, 11));
        searchBtn.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        searchBtn.setFocusPainted(false);
        searchBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performSearch();
            }
        });
        filterPanel.add(searchBtn, gbc);
        
        // Status filter
        gbc.gridx = 0; gbc.gridy = 1;
        filterPanel.add(new JLabel("Filter by Status:"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        String[] statuses = {"All", "PENDING", "APPROVED", "REJECTED", "UNFULFILLED", "RECEIVED", "COMPLETED"};
        statusFilterCombo = new JComboBox<>(statuses);
        statusFilterCombo.setFont(new Font("Arial", Font.PLAIN, 12));
        statusFilterCombo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                applyFilters();
            }
        });
        filterPanel.add(statusFilterCombo, gbc);
        
        // Clear Filters button - positioned under Search button
        gbc.gridx = 2; gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        clearFiltersBtn = new JButton("Clear Filters");
        clearFiltersBtn.setBackground(new Color(70, 130, 180));
        clearFiltersBtn.setForeground(Color.WHITE);
        clearFiltersBtn.setFont(new Font("Arial", Font.BOLD, 11));
        clearFiltersBtn.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        clearFiltersBtn.setFocusPainted(false);
        clearFiltersBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearAllFilters();
            }
        });
        filterPanel.add(clearFiltersBtn, gbc);
        
        // Supplier filter
        gbc.gridx = 0; gbc.gridy = 2;
        filterPanel.add(new JLabel("Filter by Supplier:"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 2;
        supplierFilterCombo = new JComboBox<>();
        supplierFilterCombo.setFont(new Font("Arial", Font.PLAIN, 12));
        supplierFilterCombo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                applyFilters();
            }
        });
        filterPanel.add(supplierFilterCombo, gbc);
        
        return filterPanel;
    }
    
    /**
     * Create table panel with purchase requisitions
     */
    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(new Color(245, 245, 245));
        
        // Create table
        String[] columnNames = {"PR ID", "Supplier ID", "Required Date", "Raised By", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };
        
        prTable = new JTable(tableModel);
        prTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        prTable.setRowHeight(30); // Match FM_ViewFullPO row height
        prTable.getTableHeader().setReorderingAllowed(false);
        prTable.setFont(new Font("Arial", Font.PLAIN, 12));
        prTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        prTable.setSelectionBackground(new Color(184, 207, 229));
        prTable.setGridColor(new Color(220, 220, 220));
        
        // Set column widths (adjusted to match FM_ViewFullPO style)
        prTable.getColumnModel().getColumn(0).setPreferredWidth(100); // PR ID
        prTable.getColumnModel().getColumn(1).setPreferredWidth(120); // Supplier ID
        prTable.getColumnModel().getColumn(2).setPreferredWidth(140); // Required Date
        prTable.getColumnModel().getColumn(3).setPreferredWidth(120); // Raised By
        prTable.getColumnModel().getColumn(4).setPreferredWidth(100); // Status
        
        // Apply color coding based on status
        applyStatusColorCoding();
        
        JScrollPane scrollPane = new JScrollPane(prTable);
        scrollPane.setBorder(BorderFactory.createLoweredBevelBorder());
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        
        return tablePanel;
    }
    
    /**
     * Create bottom panel with action buttons and summary info
     */
    private JPanel createBottomPanel() {
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(new Color(245, 245, 245));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0)); // Match FM_ViewPO spacing
        
        // Summary panel
        JPanel summaryPanel = createSummaryPanel();
        bottomPanel.add(summaryPanel, BorderLayout.NORTH);
        
        // Action buttons panel (only Refresh button)
        JPanel actionPanel = createActionPanel();
        bottomPanel.add(actionPanel, BorderLayout.CENTER);
        
        return bottomPanel;
    }
    
    /**
     * Create summary panel with statistics
     */
    private JPanel createSummaryPanel() {
        JPanel summaryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        summaryPanel.setBackground(new Color(245, 245, 245));
        summaryPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), 
            "Summary Information",
            0, 2, new Font("Arial", Font.BOLD, 12), new Color(51, 51, 51)));
        
        recordCountLabel = new JLabel("Records: 0");
        recordCountLabel.setFont(new Font("Arial", Font.BOLD, 12));
        recordCountLabel.setForeground(new Color(51, 51, 51));
        
        summaryPanel.add(recordCountLabel);
        
        return summaryPanel;
    }
    
    /**
     * Create action buttons panel matching FM_ViewPO style (only Refresh button)
     */
    private JPanel createActionPanel() {
        JPanel actionPanel = new JPanel(new GridBagLayout());
        actionPanel.setBackground(new Color(245, 245, 245));
        actionPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        
        // Create only the Refresh button matching FM_ViewPO style
        refreshBtn = createStyledActionButton("Refresh List", 
            "Reload all purchase requisition records", new Color(70, 130, 180));
        refreshBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadAllPRs();
            }
        });
        
        gbc.gridx = 0; gbc.gridy = 0;
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
        button.setPreferredSize(new Dimension(200, 80)); // Match FM_ViewPO button size
        
        JLabel titleLabel = new JLabel(title, JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 13)); // Match FM_ViewPO font size
        titleLabel.setForeground(Color.WHITE);
        
        JLabel descLabel = new JLabel("<html><center>" + description + "</center></html>", JLabel.CENTER);
        descLabel.setFont(new Font("Arial", Font.PLAIN, 10)); // Match FM_ViewPO font size
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
     * Create footer panel with system info (matching FM_ViewPO style)
     */
    private JPanel createFooterPanel() {
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footerPanel.setBackground(new Color(245, 245, 245));
        footerPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 15, 0));
        
        JLabel footerLabel = new JLabel("OWSB - AUTOMATED PURCHASE ORDER MANAGEMENT SYSTEM");
        footerLabel.setFont(new Font("Arial", Font.ITALIC, 10));
        footerLabel.setForeground(Color.GRAY);
        
        footerPanel.add(footerLabel);
        return footerPanel;
    }
    
    /**
     * Load all purchase requisitions into the table and populate filters
     */
    private void loadAllPRs() {
        try {
            // Clear existing data
            tableModel.setRowCount(0);
            
            // Load all PR records
            allPRRecords = Finance_ViewPR.loadPRRecords();
            
            if (allPRRecords.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "No purchase requisition records found.", 
                    "Information", 
                    JOptionPane.INFORMATION_MESSAGE);
                updateSummaryInfo(new ArrayList<>());
                return;
            }
            
            // Populate supplier filter
            populateSupplierFilter();
            
            // Update table with all records
            updateTableWithRecords(allPRRecords);
            
            // Update summary information
            updateSummaryInfo(allPRRecords);
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error loading purchase requisitions: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Populate supplier filter combo box with unique suppliers
     */
    private void populateSupplierFilter() {
        supplierFilterCombo.removeAllItems();
        supplierFilterCombo.addItem("All");
        
        // Get unique supplier IDs
        java.util.Set<String> suppliers = new java.util.HashSet<>();
        for (Finance_ViewPR pr : allPRRecords) {
            suppliers.add(pr.getSupplierID());
        }
        
        // Add to dropdown
        for (String supplier : suppliers) {
            supplierFilterCombo.addItem(supplier);
        }
    }
    
    /**
     * Update table with given PR records
     */
    private void updateTableWithRecords(List<Finance_ViewPR> records) {
        tableModel.setRowCount(0);
        
        for (Finance_ViewPR pr : records) {
            Object[] row = {
                pr.getPrID(),
                pr.getSupplierID(),
                pr.getRequiredDate().toString(),
                pr.getRaisedBy(),
                pr.getStatus()
            };
            tableModel.addRow(row);
        }
        
        applyStatusColorCoding();
    }
    
    /**
     * Apply status-based color coding to table rows
     */
    private void applyStatusColorCoding() {
        prTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                        boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                if (row < table.getModel().getRowCount()) {
                    String status = table.getModel().getValueAt(row, 4).toString(); // Column index 4 = "Status"

                    if (status.equalsIgnoreCase(Finance_ViewPR.STATUS_APPROVED)) {
                        c.setBackground(new Color(204, 255, 204)); // Light green
                    } else if (status.equalsIgnoreCase(Finance_ViewPR.STATUS_PENDING)) {
                        c.setBackground(new Color(255, 255, 204)); // Light yellow
                    } else if (status.equalsIgnoreCase(Finance_ViewPR.STATUS_REJECTED)) {
                        c.setBackground(new Color(255, 204, 204)); // Light red
                    } else if (status.equalsIgnoreCase("UNFULFILLED")) {
                        c.setBackground(new Color(255, 204, 153)); // Light orange
                    } else if (status.equalsIgnoreCase("RECEIVED")) {
                        c.setBackground(new Color(204, 255, 255)); // Light cyan
                    } else if (status.equalsIgnoreCase(Finance_ViewPR.STATUS_COMPLETED)) {
                        c.setBackground(new Color(229, 204, 255)); // Light purple
                    } else {
                        c.setBackground(Color.WHITE);
                    }

                    if (isSelected) {
                        c.setBackground(table.getSelectionBackground());
                    }
                }

                return c;
            }
        });
    }
    
    /**
     * Apply filters based on selected status and supplier
     */
    private void applyFilters() {
        String selectedStatus = (String) statusFilterCombo.getSelectedItem();
        String selectedSupplier = (String) supplierFilterCombo.getSelectedItem();
        
        List<Finance_ViewPR> filteredRecords = new ArrayList<>();
        
        for (Finance_ViewPR pr : allPRRecords) {
            boolean includeStatus = selectedStatus.equals("All") || pr.getStatus().equalsIgnoreCase(selectedStatus);
            boolean includeSupplier = selectedSupplier.equals("All") || pr.getSupplierID().equalsIgnoreCase(selectedSupplier);
            
            if (includeStatus && includeSupplier) {
                filteredRecords.add(pr);
            }
        }
        
        updateTableWithRecords(filteredRecords);
        updateSummaryInfo(filteredRecords);
        
        if (filteredRecords.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "No records found matching the selected filters.", 
                "No Results", 
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    /**
     * Perform search by PR ID
     */
    private void performSearch() {
        String searchText = searchField.getText().trim();
        if (searchText.isEmpty() || searchText.equals("Enter PR ID")) {
            // If no search text, show all records with current filters
            applyFilters();
            return;
        }
        
        List<Finance_ViewPR> searchResults = new ArrayList<>();
        for (Finance_ViewPR pr : allPRRecords) {
            if (pr.getPrID().toLowerCase().contains(searchText.toLowerCase())) {
                searchResults.add(pr);
            }
        }
        
        updateTableWithRecords(searchResults);
        updateSummaryInfo(searchResults);
        
        if (searchResults.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "No purchase requisitions found matching: " + searchText, 
                "Search Results", 
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    /**
     * Clear all filters and show all records
     */
    private void clearAllFilters() {
        statusFilterCombo.setSelectedIndex(0); // "All"
        supplierFilterCombo.setSelectedIndex(0); // "All"
        searchField.setText("Enter PR ID");
        searchField.setForeground(Color.GRAY);
        
        // Reload all data
        updateTableWithRecords(allPRRecords);
        updateSummaryInfo(allPRRecords);
    }
    
    /**
     * Update summary information labels
     */
    private void updateSummaryInfo(List<Finance_ViewPR> records) {
        int recordCount = records.size();
        recordCountLabel.setText("Records: " + recordCount);
    }
    
    /**
     * Go back to the main Finance Manager window
     */
    private void goBackToMainMenu() {
        this.dispose();
        parentWindow.setVisible(true);
    }
}