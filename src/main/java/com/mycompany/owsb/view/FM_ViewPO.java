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
        poTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
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
                approveSelectedPO();
            }
        });
        
        rejectBtn = new JButton("Reject Selected");
        rejectBtn.setBackground(new Color(220, 20, 60));
        rejectBtn.setForeground(Color.WHITE);
        rejectBtn.setFont(new Font("Arial", Font.BOLD, 12));
        rejectBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                rejectSelectedPO();
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
        
        backBtn = new JButton("Back to Main Menu");
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
     * Approve the selected purchase order
     */
    private void approveSelectedPO() {
        int selectedRow = poTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Please select a purchase order to approve.", 
                "No Selection", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String orderID = (String) tableModel.getValueAt(selectedRow, 0);
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to approve Purchase Order: " + orderID + "?", 
            "Confirm Approval", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                boolean success = financeManager.updatePOStatus(orderID, "APPROVED");
                if (success) {
                    JOptionPane.showMessageDialog(this, 
                        "Purchase Order " + orderID + " has been approved successfully!", 
                        "Success", 
                        JOptionPane.INFORMATION_MESSAGE);
                    loadPendingPOs(); // Refresh the table
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Failed to approve Purchase Order. Please try again.", 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, 
                    "Error updating purchase order: " + e.getMessage(), 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * Reject the selected purchase order
     */
    private void rejectSelectedPO() {
        int selectedRow = poTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Please select a purchase order to reject.", 
                "No Selection", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String orderID = (String) tableModel.getValueAt(selectedRow, 0);
        
        // Ask for rejection reason
        String reason = JOptionPane.showInputDialog(this, 
            "Please provide a reason for rejecting Purchase Order " + orderID + ":", 
            "Rejection Reason", 
            JOptionPane.QUESTION_MESSAGE);
        
        if (reason != null && !reason.trim().isEmpty()) {
            int confirm = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to reject Purchase Order: " + orderID + "?\nReason: " + reason, 
                "Confirm Rejection", 
                JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    boolean success = financeManager.updatePOStatus(orderID, "REJECTED");
                    if (success) {
                        JOptionPane.showMessageDialog(this, 
                            "Purchase Order " + orderID + " has been rejected.\nReason: " + reason, 
                            "Success", 
                            JOptionPane.INFORMATION_MESSAGE);
                        loadPendingPOs(); // Refresh the table
                    } else {
                        JOptionPane.showMessageDialog(this, 
                            "Failed to reject Purchase Order. Please try again.", 
                            "Error", 
                            JOptionPane.ERROR_MESSAGE);
                    }
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(this, 
                        "Error updating purchase order: " + e.getMessage(), 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        } else if (reason != null) {
            JOptionPane.showMessageDialog(this, 
                "Rejection reason cannot be empty.", 
                "Invalid Input", 
                JOptionPane.WARNING_MESSAGE);
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