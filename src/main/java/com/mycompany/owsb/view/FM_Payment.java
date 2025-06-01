package com.mycompany.owsb.view;

import com.mycompany.owsb.model.FinanceManager;
import com.mycompany.owsb.model.Finance_Payment;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Window for Finance Manager to process payments for verified orders
 */
public class FM_Payment extends javax.swing.JFrame {
    private static final String PURCHASE_ORDER_FILE = "data/purchase_order.txt";
    private static final String PAYMENT_FILE = "data/finance_payment.txt";
    
    private final FinanceManagerWindow parentWindow;
    private final FinanceManager financeManager;
    private JTable paymentTable;
    private DefaultTableModel tableModel;
    private JButton processPaymentBtn;
    private JButton viewPaymentHistoryBtn;
    private JButton refreshBtn;
    private JButton backBtn;
    private JLabel statusLabel;
    private JComboBox<String> paymentMethodCombo;
    private JLabel totalAmountLabel;

    /**
     * Creates new FM_Payment window
     */
    public FM_Payment(FinanceManagerWindow parent, FinanceManager manager) {
        this.parentWindow = parent;
        this.financeManager = manager;
        initComponents();
        loadVerifiedOrders();
        setLocationRelativeTo(parent);
    }

    /**
     * Initialize the components
     */
    private void initComponents() {
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Process Payments - Finance Manager");
        setSize(1200, 750);
        
        // Create main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(245, 245, 245));
        
        // Title panel
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(new Color(147, 112, 219));
        titlePanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JLabel titleLabel = new JLabel("Payment Processing");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        
        // Payment method selection
        JPanel methodPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        methodPanel.setBackground(new Color(147, 112, 219));
        
        JLabel methodLabel = new JLabel("Payment Method:");
        methodLabel.setForeground(Color.WHITE);
        methodLabel.setFont(new Font("Arial", Font.BOLD, 12));
        
        paymentMethodCombo = new JComboBox<>(new String[]{
            "Bank Transfer", "Check", "Credit Card", "Cash"
        });
        paymentMethodCombo.setPreferredSize(new Dimension(150, 25));
        
        methodPanel.add(methodLabel);
        methodPanel.add(Box.createHorizontalStrut(10));
        methodPanel.add(paymentMethodCombo);
        
        titlePanel.add(titleLabel, BorderLayout.WEST);
        titlePanel.add(methodPanel, BorderLayout.EAST);
        
        mainPanel.add(titlePanel, BorderLayout.NORTH);
        
        // Create table
        String[] columnNames = {"Select", "Order ID", "Item ID", "Supplier", "Quantity", 
                               "Unit Price", "Total Amount", "Order Date", "Verified Date", "PR ID"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 0; // Only the checkbox column is editable
            }
            
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0) {
                    return Boolean.class; // Checkbox column
                }
                return String.class;
            }
        };
        
        paymentTable = new JTable(tableModel);
        paymentTable.setRowHeight(28);
        paymentTable.getTableHeader().setReorderingAllowed(false);
        paymentTable.getTableHeader().setBackground(new Color(120, 90, 180));
        paymentTable.getTableHeader().setForeground(Color.WHITE);
        paymentTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        
        // Set column widths
        paymentTable.getColumnModel().getColumn(0).setPreferredWidth(50);   // Select
        paymentTable.getColumnModel().getColumn(1).setPreferredWidth(80);   // Order ID
        paymentTable.getColumnModel().getColumn(2).setPreferredWidth(80);   // Item ID
        paymentTable.getColumnModel().getColumn(3).setPreferredWidth(120);  // Supplier
        paymentTable.getColumnModel().getColumn(4).setPreferredWidth(70);   // Quantity
        paymentTable.getColumnModel().getColumn(5).setPreferredWidth(80);   // Unit Price
        paymentTable.getColumnModel().getColumn(6).setPreferredWidth(100);  // Total Amount
        paymentTable.getColumnModel().getColumn(7).setPreferredWidth(100);  // Order Date
        paymentTable.getColumnModel().getColumn(8).setPreferredWidth(100);  // Verified Date
        paymentTable.getColumnModel().getColumn(9).setPreferredWidth(80);   // PR ID
        
        // Add selection listener to update total
        tableModel.addTableModelListener(e -> updateTotalAmount());
        
        // Add alternating row colors
        paymentTable.setRowSelectionAllowed(true);
        paymentTable.setSelectionBackground(new Color(200, 190, 230));
        
        JScrollPane scrollPane = new JScrollPane(paymentTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        // FIXED: Create bottom panel that contains both status and buttons (like VerifyInventory)
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(new Color(245, 245, 245));
        
        // Create status and total panel
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBackground(new Color(245, 245, 245));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 5, 20));
        
        statusLabel = new JLabel("Loading verified orders ready for payment...");
        statusLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        statusLabel.setForeground(new Color(100, 100, 100));
        
        totalAmountLabel = new JLabel("Total Selected: $0.00");
        totalAmountLabel.setFont(new Font("Arial", Font.BOLD, 14));
        totalAmountLabel.setForeground(new Color(147, 112, 219));
        
        infoPanel.add(statusLabel, BorderLayout.WEST);
        infoPanel.add(totalAmountLabel, BorderLayout.EAST);
        
        // Create button panel - IMPROVED: Better layout like VerifyInventory
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        buttonPanel.setBackground(new Color(245, 245, 245));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        
        // Initialize buttons with consistent styling
        processPaymentBtn = createStyledButton("Process Selected Payments", new Color(34, 139, 34));
        processPaymentBtn.addActionListener(e -> processSelectedPayments());
        
        viewPaymentHistoryBtn = createStyledButton("View Payment History", new Color(70, 130, 180));
        viewPaymentHistoryBtn.addActionListener(e -> viewPaymentHistory());
        
        refreshBtn = createStyledButton("Refresh", new Color(255, 140, 0));
        refreshBtn.addActionListener(e -> loadVerifiedOrders());
        
        backBtn = createStyledButton("Back to Dashboard", new Color(105, 105, 105));
        backBtn.addActionListener(e -> goBackToMainMenu());
        
        // Add buttons to panel
        buttonPanel.add(processPaymentBtn);
        buttonPanel.add(viewPaymentHistoryBtn);
        buttonPanel.add(refreshBtn);
        buttonPanel.add(backBtn);
        
        // Add both info panel and button panel to bottom panel
        bottomPanel.add(infoPanel, BorderLayout.NORTH);
        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        // FIXED: Add bottom panel to main panel with correct constraint
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
        
        // ADDED: Force layout and repaint like VerifyInventory
        pack();
        setSize(1200, 750); // Reset size after pack
        validate();
        repaint();
    }
    
    /**
     * Helper method to create styled buttons consistently (like VerifyInventory)
     */
    private JButton createStyledButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setBackground(backgroundColor);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(180, 40)); // Fixed size for consistency
        return button;
    }
    
    /**
     * Load orders with "VERIFIED" status for payment processing
     */
    private void loadVerifiedOrders() {
        try {
            // Clear existing data
            tableModel.setRowCount(0);
            
            List<Finance_VerifyInventory> verifiedOrders = getVerifiedOrders();
            
            if (verifiedOrders.isEmpty()) {
                statusLabel.setText("No verified orders found ready for payment.");
                statusLabel.setForeground(new Color(255, 140, 0));
                return;
            }
            
            // Add data to table
            for (Finance_VerifyInventory order : verifiedOrders) {
                double unitPrice = order.getTotalPrice() / order.getQuantity();
                Object[] row = {
                    Boolean.FALSE, // Checkbox - initially unchecked
                    order.getOrderID(),
                    order.getItemID(),
                    order.getSupplierID(),
                    order.getQuantity(),
                    String.format("$%.2f", unitPrice),
                    String.format("$%.2f", order.getTotalPrice()),
                    order.getOrderDate().toString(),
                    LocalDate.now().toString(), // Verified date (current date)
                    order.getPrID()
                };
                tableModel.addRow(row);
            }
            
            statusLabel.setText(String.format("Found %d orders ready for payment processing.", verifiedOrders.size()));
            statusLabel.setForeground(new Color(34, 139, 34));
            
        } catch (IOException e) {
            statusLabel.setText("Error loading orders: " + e.getMessage());
            statusLabel.setForeground(Color.RED);
            JOptionPane.showMessageDialog(this, 
                "Error loading verified orders: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Update specific item status in the purchase order file
     * Uses both Order ID and Item ID to identify the exact line
     */
    private boolean updateItemStatus(String orderID, String itemID, String newStatus) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get(PURCHASE_ORDER_FILE));
        boolean found = false;
        
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line.trim().isEmpty()) continue;
            
            String[] parts = line.split(",");
            if (parts[0].trim().equals(orderID) && parts[1].trim().equals(itemID)) {
                parts[6] = newStatus; // Update status
                lines.set(i, String.join(",", parts));
                found = true;
                break; // Only update this specific item
            }
        }
        
        if (found) {
            Files.write(Paths.get(PURCHASE_ORDER_FILE), lines);
            return true;
        }
        return false;
    }
    
    /**
     * Get all orders with "VERIFIED" status
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
     * Update the total amount based on selected items
     */
    private void updateTotalAmount() {
        double total = 0.0;
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            Boolean selected = (Boolean) tableModel.getValueAt(i, 0);
            if (selected != null && selected) {
                String amountStr = (String) tableModel.getValueAt(i, 6);
                // Remove $ and parse
                double amount = Double.parseDouble(amountStr.replace("$", "").replace(",", ""));
                total += amount;
            }
        }
        totalAmountLabel.setText(String.format("Total Selected: $%.2f", total));
    }
    
    /**
     * Process payments for selected orders
     */
    private void processSelectedPayments() {
        List<Integer> selectedRows = new ArrayList<>();
        double totalAmount = 0.0;
        
        // Find selected rows
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            Boolean selected = (Boolean) tableModel.getValueAt(i, 0);
            if (selected != null && selected) {
                selectedRows.add(i);
                String amountStr = (String) tableModel.getValueAt(i, 6);
                double amount = Double.parseDouble(amountStr.replace("$", "").replace(",", ""));
                totalAmount += amount;
            }
        }
        
        if (selectedRows.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Please select at least one order to process payment.", 
                "No Selection", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Confirm payment processing
        String paymentMethod = (String) paymentMethodCombo.getSelectedItem();
        StringBuilder confirmMessage = new StringBuilder();
        confirmMessage.append(String.format("Confirm payment processing:\n\n"));
        confirmMessage.append(String.format("Payment Method: %s\n", paymentMethod));
        confirmMessage.append(String.format("Total Amount: $%.2f\n", totalAmount));
        confirmMessage.append(String.format("Number of Orders: %d\n\n", selectedRows.size()));
        confirmMessage.append("Selected Orders:\n");
        
        for (int row : selectedRows) {
            String orderID = (String) tableModel.getValueAt(row, 1);
            String supplier = (String) tableModel.getValueAt(row, 3);
            String amount = (String) tableModel.getValueAt(row, 6);
            confirmMessage.append(String.format("• %s - %s - %s\n", orderID, supplier, amount));
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            confirmMessage.toString(), 
            "Confirm Payment Processing", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                List<Finance_Payment> existingPayments = loadExistingPayments();
                List<Finance_Payment> newPayments = new ArrayList<>();
                int successCount = 0;
                
                // Group orders by supplier for batch payments
                Map<String, List<Integer>> supplierGroups = new HashMap<>();
                for (int row : selectedRows) {
                    String supplier = (String) tableModel.getValueAt(row, 3);
                    supplierGroups.computeIfAbsent(supplier, k -> new ArrayList<>()).add(row);
                }
                
                // Create payments for each supplier group
                for (Map.Entry<String, List<Integer>> entry : supplierGroups.entrySet()) {
                    String supplier = entry.getKey();
                    List<Integer> rows = entry.getValue();
                    
                    double supplierTotal = 0.0;
                    StringBuilder orderIDs = new StringBuilder();
                    
                    for (int row : rows) {
                        String orderID = (String) tableModel.getValueAt(row, 1);
                        String amountStr = (String) tableModel.getValueAt(row, 6);
                        double amount = Double.parseDouble(amountStr.replace("$", "").replace(",", ""));
                        
                        supplierTotal += amount;
                        if (orderIDs.length() > 0) orderIDs.append(";");
                        orderIDs.append(orderID);
                        
                        String itemID = (String) tableModel.getValueAt(row, 2);
                        updateItemStatus(orderID, itemID, "COMPLETED");
                        successCount++;
                    }
                    
                    // Create payment record
                    String paymentID = Finance_Payment.generateNextPaymentID(existingPayments);
                    Finance_Payment payment = new Finance_Payment(
                        paymentID,
                        orderIDs.toString(), // Multiple order IDs separated by semicolon
                        supplier,
                        supplierTotal,
                        LocalDate.now(),
                        paymentMethod,
                        "COMPLETED"
                    );
                    
                    newPayments.add(payment);
                    existingPayments.add(payment);
                }
                
                // Save payments to file
                savePayments(newPayments);
                
                if (successCount > 0) {
                    JOptionPane.showMessageDialog(this, 
                        String.format("Successfully processed %d payment(s)!\n" +
                                     "Created %d payment record(s) for %d supplier(s).\n" +
                                     "Total amount processed: $%.2f", 
                                     successCount, newPayments.size(), supplierGroups.size(), totalAmount), 
                        "Payment Processing Complete", 
                        JOptionPane.INFORMATION_MESSAGE);
                    loadVerifiedOrders(); // Refresh the table
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Failed to process any payments. Please try again.", 
                        "Payment Processing Failed", 
                        JOptionPane.ERROR_MESSAGE);
                }
                
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, 
                    "Error during payment processing: " + e.getMessage(), 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * Load existing payments from file
     */
    private List<Finance_Payment> loadExistingPayments() throws IOException {
        List<Finance_Payment> payments = new ArrayList<>();
        
        if (!Files.exists(Paths.get(PAYMENT_FILE))) {
            return payments;
        }
        
        List<String> lines = Files.readAllLines(Paths.get(PAYMENT_FILE));
        
        for (String line : lines) {
            if (line.trim().isEmpty()) continue;
            
            String[] parts = line.split(",");
            if (parts.length >= 7) {
                Finance_Payment payment = new Finance_Payment(
                    parts[0].trim(),                           // paymentID
                    parts[1].trim(),                           // orderID
                    parts[2].trim(),                           // supplierID
                    Double.parseDouble(parts[3].trim()),       // totalAmount
                    LocalDate.parse(parts[4].trim()),          // paymentDate
                    parts[5].trim(),                           // paymentMethod
                    parts[6].trim()                            // status
                );
                payments.add(payment);
            }
        }
        return payments;
    }
    
    /**
     * Save new payments to file
     */
    private void savePayments(List<Finance_Payment> newPayments) throws IOException {
        // Create directory if it doesn't exist
        Files.createDirectories(Paths.get("data"));
        
        List<String> lines = new ArrayList<>();
        
        // Load existing payments if file exists
        if (Files.exists(Paths.get(PAYMENT_FILE))) {
            lines.addAll(Files.readAllLines(Paths.get(PAYMENT_FILE)));
        }
        
        // Add new payments
        for (Finance_Payment payment : newPayments) {
            lines.add(payment.toString());
        }
        
        Files.write(Paths.get(PAYMENT_FILE), lines);
    }
    
    /**
     * Update order status in the purchase order file
     */
    private boolean updateOrderStatus(String orderID, String newStatus) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get(PURCHASE_ORDER_FILE));
        boolean found = false;
        
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line.trim().isEmpty()) continue;
            
            String[] parts = line.split(",");
            if (parts[0].trim().equals(orderID)) {
                parts[6] = newStatus; // Update status
                lines.set(i, String.join(",", parts));
                found = true;
                // Remove break to update all occurrences
            }
        }
        
        if (found) {
            Files.write(Paths.get(PURCHASE_ORDER_FILE), lines);
            return true;
        }
        return false;
    }
    
    /**
     * View payment history
     */
    private void viewPaymentHistory() {
        try {
            List<Finance_Payment> payments = loadExistingPayments();
            
            if (payments.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "No payment history found.", 
                    "Payment History", 
                    JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            // Create payment history dialog
            JDialog historyDialog = new JDialog(this, "Payment History", true);
            historyDialog.setSize(800, 500);
            historyDialog.setLocationRelativeTo(this);
            
            String[] historyColumns = {"Payment ID", "Order ID(s)", "Supplier", "Amount", 
                                      "Payment Date", "Method", "Status"};
            DefaultTableModel historyModel = new DefaultTableModel(historyColumns, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            
            for (Finance_Payment payment : payments) {
                Object[] row = {
                    payment.getPaymentID(),
                    payment.getOrderID(),
                    payment.getSupplierID(),
                    String.format("$%.2f", payment.getTotalAmount()),
                    payment.getPaymentDate().toString(),
                    payment.getPaymentMethod(),
                    payment.getStatus()
                };
                historyModel.addRow(row);
            }
            
            JTable historyTable = new JTable(historyModel);
            historyTable.setRowHeight(25);
            historyTable.getTableHeader().setBackground(new Color(147, 112, 219));
            historyTable.getTableHeader().setForeground(Color.WHITE);
            
            JScrollPane historyScrollPane = new JScrollPane(historyTable);
            historyDialog.add(historyScrollPane);
            
            historyDialog.setVisible(true);
            
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, 
                "Error loading payment history: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Go back to the main Finance Manager window
     */
    private void goBackToMainMenu() {
        this.dispose();
        parentWindow.setVisible(true);
    }
}