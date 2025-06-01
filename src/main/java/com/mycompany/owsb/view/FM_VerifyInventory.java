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
    private JLabel statusLabel;

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
        setSize(1000, 700);
        
        // Create main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(245, 245, 245));
        
        // Title panel
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        titlePanel.setBackground(new Color(70, 130, 180));
        titlePanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        
        JLabel titleLabel = new JLabel("Inventory Update Verification");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);
        
        mainPanel.add(titlePanel, BorderLayout.NORTH);
        
        // Create status panel - FIXED: Use BorderLayout.SOUTH instead of AFTER_LAST_LINE
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusPanel.setBackground(new Color(245, 245, 245));
        statusPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 5, 20));
        
        statusLabel = new JLabel("Loading received orders...");
        statusLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        statusLabel.setForeground(new Color(100, 100, 100));
        statusPanel.add(statusLabel);
        
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
        inventoryTable.setRowHeight(28);
        inventoryTable.getTableHeader().setReorderingAllowed(false);
        inventoryTable.getTableHeader().setBackground(new Color(60, 120, 160));
        inventoryTable.getTableHeader().setForeground(Color.WHITE);
        inventoryTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        
        // Set column widths
        inventoryTable.getColumnModel().getColumn(0).setPreferredWidth(80);  // Order ID
        inventoryTable.getColumnModel().getColumn(1).setPreferredWidth(80);  // Item ID
        inventoryTable.getColumnModel().getColumn(2).setPreferredWidth(120); // Supplier
        inventoryTable.getColumnModel().getColumn(3).setPreferredWidth(70);  // Quantity
        inventoryTable.getColumnModel().getColumn(4).setPreferredWidth(100); // Total Price
        inventoryTable.getColumnModel().getColumn(5).setPreferredWidth(100); // Order Date
        inventoryTable.getColumnModel().getColumn(6).setPreferredWidth(100); // Received Date
        inventoryTable.getColumnModel().getColumn(7).setPreferredWidth(80);  // Status
        inventoryTable.getColumnModel().getColumn(8).setPreferredWidth(80);  // PR ID
        
        // Add alternating row colors
        inventoryTable.setRowSelectionAllowed(true);
        inventoryTable.setSelectionBackground(new Color(184, 207, 229));
        
        JScrollPane scrollPane = new JScrollPane(inventoryTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Create button panel - IMPROVED: Better layout and spacing
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        buttonPanel.setBackground(new Color(245, 245, 245));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        
        // Initialize buttons with proper sizing
        verifyBtn = createStyledButton("Verify Selected Items", new Color(34, 139, 34));
        verifyBtn.addActionListener(e -> verifySelectedItems());
        
        proceedToPaymentBtn = createStyledButton("Proceed to Payment", new Color(255, 140, 0));
        proceedToPaymentBtn.addActionListener(e -> proceedToPayment());
        
        refreshBtn = createStyledButton("Refresh", new Color(70, 130, 180));
        refreshBtn.addActionListener(e -> loadReceivedOrders());
        
        backBtn = createStyledButton("Back to Dashboard", new Color(105, 105, 105));
        backBtn.addActionListener(e -> goBackToMainMenu());
        
        // Add buttons to panel
        buttonPanel.add(verifyBtn);
        buttonPanel.add(proceedToPaymentBtn);
        buttonPanel.add(refreshBtn);
        buttonPanel.add(backBtn);
        
        // Create bottom panel that contains both status and buttons
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(new Color(245, 245, 245));
        bottomPanel.add(statusPanel, BorderLayout.NORTH);
        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
        
        // ADDED: Force layout and repaint
        pack();
        setSize(1000, 700); // Reset size after pack
        validate();
        repaint();
    }
    
    /**
     * Helper method to create styled buttons consistently
     */
    private JButton createStyledButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setBackground(backgroundColor);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(160, 40)); // Fixed size for consistency
        return button;
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
                statusLabel.setText("No received orders found for verification.");
                statusLabel.setForeground(new Color(255, 140, 0));
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
            
            statusLabel.setText(String.format("Found %d orders ready for verification.", receivedOrders.size()));
            statusLabel.setForeground(new Color(34, 139, 34));
            
        } catch (IOException e) {
            statusLabel.setText("Error loading orders: " + e.getMessage());
            statusLabel.setForeground(Color.RED);
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
                "Please select at least one item to verify.", 
                "No Selection", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        StringBuilder confirmMessage = new StringBuilder("Are you sure you want to verify the following items?\n\n");
        for (int row : selectedRows) {
            String orderID = (String) tableModel.getValueAt(row, 0);
            String itemID = (String) tableModel.getValueAt(row, 1);
            confirmMessage.append("• Order ").append(orderID).append(" - Item ").append(itemID).append("\n");
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            confirmMessage.toString(), 
            "Confirm Verification", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                int successCount = 0;
                for (int row : selectedRows) {
                    String orderID = (String) tableModel.getValueAt(row, 0);
                    if (updateOrderStatus(orderID, "VERIFIED")) {
                        successCount++;
                    }
                }
                
                if (successCount > 0) {
                    JOptionPane.showMessageDialog(this, 
                        String.format("Successfully verified %d item(s)!\nThese items are now ready for payment processing.", successCount), 
                        "Verification Complete", 
                        JOptionPane.INFORMATION_MESSAGE);
                    loadReceivedOrders(); // Refresh the table
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Failed to verify any items. Please try again.", 
                        "Verification Failed", 
                        JOptionPane.ERROR_MESSAGE);
                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, 
                    "Error during verification: " + e.getMessage(), 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
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
                // REMOVED the break statement - continue processing all matching lines
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