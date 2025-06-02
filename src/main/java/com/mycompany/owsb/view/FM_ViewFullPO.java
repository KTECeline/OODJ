package com.mycompany.owsb.view;

import com.mycompany.owsb.model.FinanceManager;
import com.mycompany.owsb.model.Finance_ViewPO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.List;
import java.util.ArrayList;

/**
 * Panel for Finance Manager to view all Purchase Orders with filtering capabilities
 */
public class FM_ViewFullPO extends javax.swing.JFrame {
    private final FinanceManagerWindow parentWindow;
    private final FinanceManager financeManager;
    private JTable poTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JComboBox<String> statusFilter;
    private JComboBox<String> supplierFilter;
    private JButton searchBtn;
    private JButton refreshBtn;
    private JButton backBtn;
    private JButton clearFiltersBtn;
    private JLabel totalAmountLabel;
    private JLabel recordCountLabel;
    private List<Finance_ViewPO> allPurchaseOrders;

    /**
     * Creates new form FM_ViewFullPO
     */
    public FM_ViewFullPO(FinanceManagerWindow parent, FinanceManager manager) {
        this.parentWindow = parent;
        this.financeManager = manager;
        this.allPurchaseOrders = new ArrayList<>();
        initComponents();
        loadAllPOs();
        setLocationRelativeTo(parent);
    }

    /**
     * Initialize the components
     */
    private void initComponents() {
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Purchase Order Management - Finance Manager");
        setSize(1200, 750); // Keep slightly larger due to additional filter elements
        
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
        JLabel titleLabel = new JLabel("Purchase Order Management");
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
        JLabel subtitleLabel = new JLabel("All Purchase Orders");
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
     * Create filter panel with search and filter options
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
        
        // Search by Order ID
        gbc.gridx = 0; gbc.gridy = 0;
        filterPanel.add(new JLabel("Search Order ID:"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        searchField = new JTextField("Enter Order ID");
        searchField.setFont(new Font("Arial", Font.PLAIN, 12));
        searchField.setForeground(Color.GRAY);
        searchField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (searchField.getText().equals("Enter Order ID")) {
                    searchField.setText("");
                    searchField.setForeground(Color.BLACK);
                }
            }
            
            @Override
            public void focusLost(FocusEvent e) {
                if (searchField.getText().isEmpty()) {
                    searchField.setText("Enter Order ID");
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
                searchPurchaseOrder();
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
        statusFilter = new JComboBox<>(statuses);
        statusFilter.setFont(new Font("Arial", Font.PLAIN, 12));
        statusFilter.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                applyFilters();
            }
        });
        filterPanel.add(statusFilter, gbc);
        
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
        supplierFilter = new JComboBox<>();
        supplierFilter.setFont(new Font("Arial", Font.PLAIN, 12));
        supplierFilter.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                applyFilters();
            }
        });
        filterPanel.add(supplierFilter, gbc);
        
        return filterPanel;
    }
    
    /**
     * Create table panel with purchase orders
     */
    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(new Color(245, 245, 245));
        
        // Create table
        String[] columnNames = {"Order ID", "Item ID", "Supplier ID", "Quantity", 
                               "Total Price", "Order Date", "Status", "PR ID", "Created By"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };
        
        poTable = new JTable(tableModel);
        poTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        poTable.setRowHeight(30);
        poTable.getTableHeader().setReorderingAllowed(false);
        poTable.setFont(new Font("Arial", Font.PLAIN, 12));
        poTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        poTable.setSelectionBackground(new Color(184, 207, 229));
        poTable.setGridColor(new Color(220, 220, 220));
        
        // Set column widths to match FM_ViewPO
        poTable.getColumnModel().getColumn(0).setPreferredWidth(80);  // Order ID
        poTable.getColumnModel().getColumn(1).setPreferredWidth(80);  // Item ID
        poTable.getColumnModel().getColumn(2).setPreferredWidth(100); // Supplier ID
        poTable.getColumnModel().getColumn(3).setPreferredWidth(70);  // Quantity
        poTable.getColumnModel().getColumn(4).setPreferredWidth(100); // Total Price (matching FM_ViewPO)
        poTable.getColumnModel().getColumn(5).setPreferredWidth(120); // Order Date (matching FM_ViewPO)
        poTable.getColumnModel().getColumn(6).setPreferredWidth(80);  // Status
        poTable.getColumnModel().getColumn(7).setPreferredWidth(80);  // PR ID
        poTable.getColumnModel().getColumn(8).setPreferredWidth(100); // Created By
        
        JScrollPane scrollPane = new JScrollPane(poTable);
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
        
        // Action buttons panel
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
        
        totalAmountLabel = new JLabel("Total Amount: $0.00"); // Match FM_ViewPO currency format
        totalAmountLabel.setFont(new Font("Arial", Font.BOLD, 12));
        totalAmountLabel.setForeground(new Color(51, 51, 51));
        
        summaryPanel.add(recordCountLabel);
        summaryPanel.add(Box.createHorizontalStrut(30));
        summaryPanel.add(totalAmountLabel);
        
        return summaryPanel;
    }
    
    /**
     * Create action buttons panel matching FM_ViewPO style
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
            "Reload all purchase order records", new Color(70, 130, 180));
        refreshBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadAllPOs();
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
     * Load all purchase orders into the table
     */
    private void loadAllPOs() {
        try {
            // Clear existing data
            tableModel.setRowCount(0);
            
            // Load all purchase order records
            allPurchaseOrders = Finance_ViewPO.loadPurchaseOrderRecords();
            
            if (allPurchaseOrders.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "No purchase order records found.", 
                    "Information", 
                    JOptionPane.INFORMATION_MESSAGE);
                updateSummaryInfo(new ArrayList<>());
                return;
            }
            
            // Update table using the existing method from Finance_ViewPO
            Finance_ViewPO.updatePurchaseOrderTableInUI(allPurchaseOrders, poTable);
            
            // Update supplier filter dropdown
            updateSupplierFilter();
            
            // Update summary information
            updateSummaryInfo(allPurchaseOrders);
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error loading purchase orders: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Update supplier filter dropdown with available suppliers
     */
    private void updateSupplierFilter() {
        supplierFilter.removeAllItems();
        supplierFilter.addItem("All");
        
        // Get unique supplier IDs
        java.util.Set<String> suppliers = new java.util.HashSet<>();
        for (Finance_ViewPO po : allPurchaseOrders) {
            suppliers.add(po.getSupplierID());
        }
        
        // Add to dropdown
        for (String supplier : suppliers) {
            supplierFilter.addItem(supplier);
        }
    }
    
    /**
     * Search for a specific purchase order by Order ID
     */
    private void searchPurchaseOrder() {
        Finance_ViewPO.searchAndDisplayPurchaseOrderInTable(searchField, poTable, allPurchaseOrders);
        
        // Update summary for search results
        List<Finance_ViewPO> displayedOrders = getDisplayedOrders();
        updateSummaryInfo(displayedOrders);
    }
    
    /**
     * Apply status and supplier filters
     */
    private void applyFilters() {
        String selectedStatus = (String) statusFilter.getSelectedItem();
        String selectedSupplier = (String) supplierFilter.getSelectedItem();
        
        // Apply status filter first
        if (selectedStatus != null && !selectedStatus.equals("All")) {
            Finance_ViewPO.filterByStatus(selectedStatus, poTable, allPurchaseOrders);
        } else if (selectedSupplier != null && !selectedSupplier.equals("All")) {
            // If no status filter but supplier filter is selected
            Finance_ViewPO.filterBySupplier(selectedSupplier, poTable, allPurchaseOrders);
        } else {
            // No filters applied, show all
            Finance_ViewPO.updatePurchaseOrderTableInUI(allPurchaseOrders, poTable);
        }
        
        // If both filters are applied, we need to apply them sequentially
        if (selectedStatus != null && !selectedStatus.equals("All") && 
            selectedSupplier != null && !selectedSupplier.equals("All")) {
            
            // Get current filtered list and apply supplier filter
            List<Finance_ViewPO> statusFiltered = getDisplayedOrders();
            Finance_ViewPO.filterBySupplier(selectedSupplier, poTable, statusFiltered);
        }
        
        // Update summary for filtered results
        List<Finance_ViewPO> displayedOrders = getDisplayedOrders();
        updateSummaryInfo(displayedOrders);
    }
    
    /**
     * Get currently displayed orders from the table
     */
    private List<Finance_ViewPO> getDisplayedOrders() {
        List<Finance_ViewPO> displayedOrders = new ArrayList<>();
        
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String orderID = (String) tableModel.getValueAt(i, 0);
            // Find the corresponding Finance_ViewPO object
            for (Finance_ViewPO po : allPurchaseOrders) {
                if (po.getOrderID().equals(orderID)) {
                    displayedOrders.add(po);
                    break;
                }
            }
        }
        
        return displayedOrders;
    }
    
    /**
     * Clear all filters and show all records
     */
    private void clearAllFilters() {
        statusFilter.setSelectedIndex(0); // "All"
        supplierFilter.setSelectedIndex(0); // "All"
        searchField.setText("Enter Order ID");
        searchField.setForeground(Color.GRAY);
        
        // Reload all data
        Finance_ViewPO.updatePurchaseOrderTableInUI(allPurchaseOrders, poTable);
        updateSummaryInfo(allPurchaseOrders);
    }
    
    /**
     * Update summary information labels
     */
    private void updateSummaryInfo(List<Finance_ViewPO> orders) {
        int recordCount = orders.size();
        double totalAmount = 0.0;
        
        for (Finance_ViewPO po : orders) {
            totalAmount += po.getTotalPrice();
        }
        
        recordCountLabel.setText("Records: " + recordCount);
        totalAmountLabel.setText("Total Amount: $" + String.format("%.2f", totalAmount)); // Match FM_ViewPO currency format
    }
    
    /**
     * Go back to the main Finance Manager window
     */
    private void goBackToMainMenu() {
        this.dispose();
        parentWindow.setVisible(true);
    }
}