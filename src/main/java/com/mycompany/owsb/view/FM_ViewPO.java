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
        setSize(1000, 650);
        
        // Create main panel with BorderLayout (matching FinanceManagerWindow)
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(245, 245, 245));
        
        // Header panel (matching FinanceManagerWindow style)
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Content panel
        JPanel contentPanel = createContentPanel();
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        
        // Footer panel (matching FinanceManagerWindow style)
        JPanel footerPanel = createFooterPanel();
        mainPanel.add(footerPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    /**
     * Create the header panel with title and back button (matching FinanceManagerWindow style)
     */
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(51, 51, 51));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        // Title label
        JLabel titleLabel = new JLabel("Purchase Order Approval");
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
        JLabel subtitleLabel = new JLabel("Pending Purchase Orders");
        subtitleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        subtitleLabel.setForeground(new Color(51, 51, 51));
        subtitleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        contentPanel.add(subtitleLabel, BorderLayout.NORTH);
        
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
        
        // Set column widths
        poTable.getColumnModel().getColumn(0).setPreferredWidth(80);  // Order ID
        poTable.getColumnModel().getColumn(1).setPreferredWidth(80);  // Item ID
        poTable.getColumnModel().getColumn(2).setPreferredWidth(100); // Supplier ID
        poTable.getColumnModel().getColumn(3).setPreferredWidth(70);  // Quantity
        poTable.getColumnModel().getColumn(4).setPreferredWidth(100); // Total Price
        poTable.getColumnModel().getColumn(5).setPreferredWidth(120); // Order Date
        poTable.getColumnModel().getColumn(6).setPreferredWidth(80);  // Status
        poTable.getColumnModel().getColumn(7).setPreferredWidth(80);  // PR ID
        poTable.getColumnModel().getColumn(8).setPreferredWidth(100); // Created By
        
        JScrollPane scrollPane = new JScrollPane(poTable);
        scrollPane.setBorder(BorderFactory.createLoweredBevelBorder());
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Create action button panel (matching FinanceManagerWindow button style)
        JPanel actionPanel = createActionPanel();
        contentPanel.add(actionPanel, BorderLayout.SOUTH);
        
        return contentPanel;
    }
    
    /**
     * Create action buttons panel with styled buttons matching FinanceManagerWindow
     */
    private JPanel createActionPanel() {
        JPanel actionPanel = new JPanel(new GridBagLayout());
        actionPanel.setBackground(new Color(245, 245, 245));
        actionPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        
        // Create styled buttons matching FinanceManagerWindow style
        approveBtn = createStyledActionButton("Approve Selected", 
            "Approve the selected purchase orders", new Color(34, 139, 34));
        approveBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                approveSelectedPOs();
            }
        });
        
        rejectBtn = createStyledActionButton("Reject Selected", 
            "Reject the selected purchase orders", new Color(220, 20, 60));
        rejectBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                rejectSelectedPOs();
            }
        });
        
        refreshBtn = createStyledActionButton("Refresh List", 
            "Reload pending purchase orders", new Color(70, 130, 180));
        refreshBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadPendingPOs();
            }
        });
        
        // Add buttons to panel in a row
        gbc.gridx = 0; gbc.gridy = 0;
        actionPanel.add(approveBtn, gbc);
        
        gbc.gridx = 1; gbc.gridy = 0;
        actionPanel.add(rejectBtn, gbc);
        
        gbc.gridx = 2; gbc.gridy = 0;
        actionPanel.add(refreshBtn, gbc);
        
        return actionPanel;
    }
    
    /**
     * Create a styled action button matching FinanceManagerWindow style
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
        
        // Add hover effect (matching FinanceManagerWindow)
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
     * Create footer panel with system info (matching FinanceManagerWindow style)
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