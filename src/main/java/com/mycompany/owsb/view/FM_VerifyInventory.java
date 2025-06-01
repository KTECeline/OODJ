package com.mycompany.owsb.view;

import com.mycompany.owsb.model.FinanceManager;
import com.mycompany.owsb.model.Finance_VerifyInventory;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Window for Finance Manager to verify inventory updates for received items
 */
public class FM_VerifyInventory extends javax.swing.JFrame {
    private static final String PURCHASE_ORDER_FILE = "data/purchase_order.txt";
    
    private final FinanceManagerWindow parentWindow;
    private final FinanceManager financeManager;
    private JTable inventoryTable;
    private DefaultTableModel tableModel;
    private JButton verifyBtn;
    private JButton refreshBtn;
    private JButton backBtn;
    private JButton proceedToPaymentBtn;

    /**
     * Creates new FM_VerifyInventory window
     */
    public FM_VerifyInventory(FinanceManagerWindow parent, FinanceManager manager) {
        this.parentWindow = parent;
        this.financeManager = manager;
        initComponents();
        loadReceivedOrders();
        setLocationRelativeTo(parent);
    }

    /**
     * Initialize the components
     */
    private void initComponents() {
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Verify Inventory Updates - Finance Manager");
        setSize(1000, 650); // Match FM_ViewPO window size
        
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
        JLabel titleLabel = new JLabel("Verify Inventory Updates");
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
     * Create the main content panel with table and action buttons
     */
    private JPanel createContentPanel() {
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(new Color(245, 245, 245));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        
        // Subtitle
        JLabel subtitleLabel = new JLabel("Received Orders Ready for Verification");
        subtitleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        subtitleLabel.setForeground(new Color(51, 51, 51));
        subtitleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        contentPanel.add(subtitleLabel, BorderLayout.NORTH);
        
        // Create table
        String[] columnNames = {"Order ID", "Item ID", "Supplier", "Quantity", 
                               "Total Price", "Order Date", "Received Date", "Status", "PR ID"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };
        
        inventoryTable = new JTable(tableModel);
        inventoryTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        inventoryTable.setRowHeight(30); // Match FM_ViewPO row height
        inventoryTable.getTableHeader().setReorderingAllowed(false);
        inventoryTable.setFont(new Font("Arial", Font.PLAIN, 12)); // Match FM_ViewPO font
        inventoryTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12)); // Match FM_ViewPO header font
        inventoryTable.setSelectionBackground(new Color(184, 207, 229)); // Match FM_ViewPO selection color
        inventoryTable.setGridColor(new Color(220, 220, 220)); // Match FM_ViewPO grid color
        
        // Set column widths (adjusted for additional columns)
        inventoryTable.getColumnModel().getColumn(0).setPreferredWidth(80);  // Order ID
        inventoryTable.getColumnModel().getColumn(1).setPreferredWidth(80);  // Item ID
        inventoryTable.getColumnModel().getColumn(2).setPreferredWidth(100); // Supplier
        inventoryTable.getColumnModel().getColumn(3).setPreferredWidth(70);  // Quantity
        inventoryTable.getColumnModel().getColumn(4).setPreferredWidth(100); // Total Price
        inventoryTable.getColumnModel().getColumn(5).setPreferredWidth(120); // Order Date
        inventoryTable.getColumnModel().getColumn(6).setPreferredWidth(120); // Received Date
        inventoryTable.getColumnModel().getColumn(7).setPreferredWidth(80);  // Status
        inventoryTable.getColumnModel().getColumn(8).setPreferredWidth(80);  // PR ID
        
        JScrollPane scrollPane = new JScrollPane(inventoryTable);
        scrollPane.setBorder(BorderFactory.createLoweredBevelBorder()); // Match FM_ViewPO border
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Create action button panel (matching FM_ViewPO button style)
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
        verifyBtn = createStyledActionButton("Verify Selected", 
            "Review and verify selected inventory items", new Color(34, 139, 34));
        verifyBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                verifySelectedItems();
            }
        });
        
        proceedToPaymentBtn = createStyledActionButton("Proceed to Payment", 
            "Continue to payment processing for verified items", new Color(255, 140, 0));
        proceedToPaymentBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                proceedToPayment();
            }
        });
        
        refreshBtn = createStyledActionButton("Refresh List", 
            "Reload received orders from database", new Color(70, 130, 180));
        refreshBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadReceivedOrders();
            }
        });
        
        // Add buttons to panel in a row (matching FM_ViewPO layout)
        gbc.gridx = 0; gbc.gridy = 0;
        actionPanel.add(verifyBtn, gbc);
        
        gbc.gridx = 1; gbc.gridy = 0;
        actionPanel.add(proceedToPaymentBtn, gbc);
        
        gbc.gridx = 2; gbc.gridy = 0;
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
     * Load orders with "RECEIVED" status for verification
     */
    private void loadReceivedOrders() {
        try {
            // Clear existing data
            tableModel.setRowCount(0);
            
            List<Finance_VerifyInventory> receivedOrders = getReceivedOrders();
            
            if (receivedOrders.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "No received orders found for verification.", 
                    "Information", 
                    JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            // Add data to table
            for (Finance_VerifyInventory order : receivedOrders) {
                Object[] row = {
                    order.getOrderID(),
                    order.getItemID(),
                    order.getSupplierID(),
                    order.getQuantity(),
                    String.format("$%.2f", order.getTotalPrice()),
                    order.getOrderDate().toString(),
                    order.getReceivedDate() != null ? order.getReceivedDate().toString() : "N/A",
                    order.getStatus(),
                    order.getPrID()
                };
                tableModel.addRow(row);
            }
            
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, 
                "Error loading received orders: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Get all orders with "RECEIVED" status
     */
    private List<Finance_VerifyInventory> getReceivedOrders() throws IOException {
        List<Finance_VerifyInventory> receivedOrders = new ArrayList<>();
        
        if (!Files.exists(Paths.get(PURCHASE_ORDER_FILE))) {
            return receivedOrders;
        }
        
        List<String> lines = Files.readAllLines(Paths.get(PURCHASE_ORDER_FILE));
        
        for (String line : lines) {
            if (line.trim().isEmpty()) continue;
            
            String[] parts = line.split(",");
            if (parts.length >= 9 && parts[6].equalsIgnoreCase("RECEIVED")) {
                receivedOrders.add(new Finance_VerifyInventory(
                    parts[0].trim(), 
                    parts[1].trim(), 
                    parts[2].trim(),
                    Integer.parseInt(parts[3].trim()),
                    Double.parseDouble(parts[4].trim()),
                    LocalDate.parse(parts[5].trim()),
                    parts[6].trim(), 
                    parts[7].trim(), 
                    parts[8].trim()
                ));
            }
        }
        return receivedOrders;
    }
    
    /**
     * Verify the selected inventory items
     */
    private void verifySelectedItems() {
        int[] selectedRows = inventoryTable.getSelectedRows();
        if (selectedRows.length == 0) {
            JOptionPane.showMessageDialog(this, 
                "Please select one or more items to verify.", 
                "No Selection", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Get selected order IDs
        List<String> orderIDs = new ArrayList<>();
        for (int row : selectedRows) {
            orderIDs.add((String) tableModel.getValueAt(row, 0));
        }
        
        String message = selectedRows.length == 1 ? 
            "Are you sure you want to verify Order: " + orderIDs.get(0) + "?" :
            "Are you sure you want to verify " + selectedRows.length + " orders?\n" +
            "Order IDs: " + String.join(", ", orderIDs);
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            message, 
            "Confirm Verification", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            int successCount = 0;
            List<String> failedOrders = new ArrayList<>();
            
            for (String orderID : orderIDs) {
                try {
                    boolean success = updateOrderStatus(orderID, "VERIFIED");
                    if (success) {
                        successCount++;
                    } else {
                        failedOrders.add(orderID);
                    }
                } catch (IOException e) {
                    failedOrders.add(orderID);
                }
            }
            
            // Show results
            if (successCount == orderIDs.size()) {
                String successMessage = successCount == 1 ? 
                    "Order has been verified successfully!" :
                    successCount + " orders have been verified successfully!";
                JOptionPane.showMessageDialog(this, 
                    successMessage, 
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
            } else if (successCount > 0) {
                JOptionPane.showMessageDialog(this, 
                    successCount + " orders verified successfully.\n" +
                    "Failed to verify: " + String.join(", ", failedOrders), 
                    "Partial Success", 
                    JOptionPane.WARNING_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Failed to verify any orders: " + String.join(", ", failedOrders), 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
            
            loadReceivedOrders(); // Refresh the table
        }
    }
    
    /**
     * Update order status in the file
     */
    private boolean updateOrderStatus(String orderID, String newStatus) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get(PURCHASE_ORDER_FILE));
        boolean found = false;
        
        // Process ALL lines with matching PO ID, not just the first one
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line.trim().isEmpty()) continue;
            
            String[] parts = line.split(",");
            if (parts[0].trim().equals(orderID)) {
                parts[6] = newStatus; // Update status
                lines.set(i, String.join(",", parts));
                found = true;
                // Continue processing all matching lines
            }
        }
        
        if (found) {
            Files.write(Paths.get(PURCHASE_ORDER_FILE), lines);
            return true;
        }
        return false;
    }
    
    /**
     * Proceed to payment for verified items
     */
    private void proceedToPayment() {
        try {
            // Check if there are any verified orders ready for payment
            List<Finance_VerifyInventory> verifiedOrders = getVerifiedOrders();
            
            if (verifiedOrders.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "No verified orders found ready for payment processing.\n" +
                    "Please verify some orders first before proceeding to payment.", 
                    "No Verified Orders", 
                    JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            // Navigate to payment processing window
            FM_Payment paymentWindow = new FM_Payment(parentWindow, financeManager);
            paymentWindow.setVisible(true);
            this.dispose(); // Close current window
            
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, 
                "Error checking verified orders: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Get all orders with "VERIFIED" status ready for payment
     */
    private List<Finance_VerifyInventory> getVerifiedOrders() throws IOException {
        List<Finance_VerifyInventory> verifiedOrders = new ArrayList<>();
        
        if (!Files.exists(Paths.get(PURCHASE_ORDER_FILE))) {
            return verifiedOrders;
        }
        
        List<String> lines = Files.readAllLines(Paths.get(PURCHASE_ORDER_FILE));
        
        for (String line : lines) {
            if (line.trim().isEmpty()) continue;
            
            String[] parts = line.split(",");
            if (parts.length >= 9 && parts[6].equalsIgnoreCase("VERIFIED")) {
                verifiedOrders.add(new Finance_VerifyInventory(
                    parts[0].trim(), 
                    parts[1].trim(), 
                    parts[2].trim(),
                    Integer.parseInt(parts[3].trim()),
                    Double.parseDouble(parts[4].trim()),
                    LocalDate.parse(parts[5].trim()),
                    parts[6].trim(), 
                    parts[7].trim(), 
                    parts[8].trim()
                ));
            }
        }
        return verifiedOrders;
    }
    
    /**
     * Go back to the main Finance Manager window
     */
    private void goBackToMainMenu() {
        this.dispose();
        parentWindow.setVisible(true);
    }
}