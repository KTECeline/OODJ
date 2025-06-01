package com.mycompany.owsb.view;

import com.mycompany.owsb.model.FinanceManager;
import com.mycompany.owsb.model.Finance_PurchaseOrder;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

/**
 * Panel for Finance Manager to view and approve Purchase Orders
 */
public class FM_ViewPO extends javax.swing.JFrame {
    private final FinanceManagerWindow parentWindow;
    private final FinanceManager financeManager;
    private JTable poTable;
    private DefaultTableModel tableModel;
    private JButton approveBtn;
    private JButton rejectBtn;
    private JButton refreshBtn;
    private JButton backBtn;

    /**
     * Creates new form FM_ViewPO
     */
    public FM_ViewPO(FinanceManagerWindow parent, FinanceManager manager) {
        this.parentWindow = parent;
        this.financeManager = manager;
        initComponents();
        loadPendingPOs();
        setLocationRelativeTo(parent);
    }

    /**
     * Initialize the components
     */
    private void initComponents() {
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Purchase Order Approval - Finance Manager");
        setSize(900, 600);
        
        // Create main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        // Title label
        JLabel titleLabel = new JLabel("Pending Purchase Orders", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        
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
        // Changed from SINGLE_SELECTION to MULTIPLE_INTERVAL_SELECTION for multi-row selection
        poTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        poTable.setRowHeight(25);
        poTable.getTableHeader().setReorderingAllowed(false);
        
        // Set column widths
        poTable.getColumnModel().getColumn(0).setPreferredWidth(80);  // Order ID
        poTable.getColumnModel().getColumn(1).setPreferredWidth(80);  // Item ID
        poTable.getColumnModel().getColumn(2).setPreferredWidth(100); // Supplier ID
        poTable.getColumnModel().getColumn(3).setPreferredWidth(70);  // Quantity
        poTable.getColumnModel().getColumn(4).setPreferredWidth(100); // Total Price
        poTable.getColumnModel().getColumn(5).setPreferredWidth(100); // Order Date
        poTable.getColumnModel().getColumn(6).setPreferredWidth(80);  // Status
        poTable.getColumnModel().getColumn(7).setPreferredWidth(80);  // PR ID
        poTable.getColumnModel().getColumn(8).setPreferredWidth(100); // Created By
        
        JScrollPane scrollPane = new JScrollPane(poTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Create button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        
        approveBtn = new JButton("Approve Selected");
        approveBtn.setBackground(new Color(34, 139, 34));
        approveBtn.setForeground(Color.WHITE);
        approveBtn.setFont(new Font("Arial", Font.BOLD, 12));
        approveBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                approveSelectedPOs();
            }
        });
        
        rejectBtn = new JButton("Reject Selected");
        rejectBtn.setBackground(new Color(220, 20, 60));
        rejectBtn.setForeground(Color.WHITE);
        rejectBtn.setFont(new Font("Arial", Font.BOLD, 12));
        rejectBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                rejectSelectedPOs();
            }
        });
        
        refreshBtn = new JButton("Refresh");
        refreshBtn.setBackground(new Color(70, 130, 180));
        refreshBtn.setForeground(Color.WHITE);
        refreshBtn.setFont(new Font("Arial", Font.BOLD, 12));
        refreshBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadPendingPOs();
            }
        });
        
        backBtn = new JButton("Back to Dashboard");
        backBtn.setBackground(new Color(105, 105, 105));
        backBtn.setForeground(Color.WHITE);
        backBtn.setFont(new Font("Arial", Font.BOLD, 12));
        backBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                goBackToMainMenu();
            }
        });
        
        buttonPanel.add(approveBtn);
        buttonPanel.add(rejectBtn);
        buttonPanel.add(refreshBtn);
        buttonPanel.add(backBtn);
        
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    /**
     * Load pending purchase orders into the table
     */
    private void loadPendingPOs() {
        try {
            // Clear existing data
            tableModel.setRowCount(0);
            
            List<Finance_PurchaseOrder> pendingPOs = financeManager.getPendingPOs();
            
            if (pendingPOs.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "No pending purchase orders found.", 
                    "Information", 
                    JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            // Add data to table
            for (Finance_PurchaseOrder po : pendingPOs) {
                Object[] row = {
                    po.getOrderID(),
                    po.getItemID(),
                    po.getSupplierID(),
                    po.getQuantity(),
                    String.format("$%.2f", po.getTotalPrice()),
                    po.getOrderDate().toString(),
                    po.getStatus(),
                    po.getPrID(),
                    po.getUserID()
                };
                tableModel.addRow(row);
            }
            
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, 
                "Error loading purchase orders: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Approve the selected purchase orders
     */
    private void approveSelectedPOs() {
        int[] selectedRows = poTable.getSelectedRows();
        if (selectedRows.length == 0) {
            JOptionPane.showMessageDialog(this, 
                "Please select one or more purchase orders to approve.", 
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
            "Are you sure you want to approve Purchase Order: " + orderIDs.get(0) + "?" :
            "Are you sure you want to approve " + selectedRows.length + " Purchase Orders?\n" +
            "Order IDs: " + String.join(", ", orderIDs);
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            message, 
            "Confirm Approval", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            int successCount = 0;
            List<String> failedOrders = new ArrayList<>();
            
            for (String orderID : orderIDs) {
                try {
                    boolean success = financeManager.updatePOStatus(orderID, "APPROVED");
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
                    "Purchase Order has been approved successfully!" :
                    successCount + " Purchase Orders have been approved successfully!";
                JOptionPane.showMessageDialog(this, 
                    successMessage, 
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
            } else if (successCount > 0) {
                JOptionPane.showMessageDialog(this, 
                    successCount + " Purchase Orders approved successfully.\n" +
                    "Failed to approve: " + String.join(", ", failedOrders), 
                    "Partial Success", 
                    JOptionPane.WARNING_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Failed to approve any Purchase Orders: " + String.join(", ", failedOrders), 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
            
            loadPendingPOs(); // Refresh the table
        }
    }
    
    /**
     * Reject the selected purchase orders
     */
    private void rejectSelectedPOs() {
        int[] selectedRows = poTable.getSelectedRows();
        if (selectedRows.length == 0) {
            JOptionPane.showMessageDialog(this, 
                "Please select one or more purchase orders to reject.", 
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
            "Are you sure you want to reject Purchase Order: " + orderIDs.get(0) + "?" :
            "Are you sure you want to reject " + selectedRows.length + " Purchase Orders?\n" +
            "Order IDs: " + String.join(", ", orderIDs);
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            message, 
            "Confirm Rejection", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            int successCount = 0;
            List<String> failedOrders = new ArrayList<>();
            
            for (String orderID : orderIDs) {
                try {
                    boolean success = financeManager.updatePOStatus(orderID, "REJECTED");
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
                    "Purchase Order has been rejected successfully!" :
                    successCount + " Purchase Orders have been rejected successfully!";
                JOptionPane.showMessageDialog(this, 
                    successMessage, 
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
            } else if (successCount > 0) {
                JOptionPane.showMessageDialog(this, 
                    successCount + " Purchase Orders rejected successfully.\n" +
                    "Failed to reject: " + String.join(", ", failedOrders), 
                    "Partial Success", 
                    JOptionPane.WARNING_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Failed to reject any Purchase Orders: " + String.join(", ", failedOrders), 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
            
            loadPendingPOs(); // Refresh the table
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