/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.mycompany.owsb.view;


import com.mycompany.owsb.model.PurchaseManager;
import com.mycompany.owsb.model.PurchaseOrder;
import com.mycompany.owsb.model.PurchaseRequisition;
import com.mycompany.owsb.model.SupplierItem;
import com.mycompany.owsb.model.User;
import com.mycompany.owsb.model.WindowUtil;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;


/**
 *
 * @author timi
 */
public class Admin_ManagePurchaseOrder extends javax.swing.JFrame {

    private final PurchaseManager purchaseManager;
    
    private double unitCost = 0.0;
    /**
     * Creates new form SmManageDailySalesWindow
     */
    public Admin_ManagePurchaseOrder(PurchaseManager purchaseManager) {
        this.purchaseManager = purchaseManager;
        
        initComponents();
        setupWindowListener();
        
        loadPOsIntoList();
        setupTableSelectionListener();
        setupQuantityFieldListener();
        String username = purchaseManager.getLoggedInUser().getUsername();
    }
    
     private void setupWindowListener() {
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                dispose(); // Close the current window
            }
        });
    }
     
   
    
    private void loadPOsIntoList() {
        // Load the list of Purchase Orders from the purchase order file
        List<PurchaseOrder> allPOs = purchaseManager.getAllPurchaseOrders();
    List<PurchaseRequisition> allPRs = purchaseManager.getAllRequisitions();;
        // Update the JList and details area in the UI with the loaded Purchase Orders
        PurchaseOrder.updatePOTableInUI(allPOs, allPRs, poTable);
    }
    

    
   /* public void filterPOTableByStatus() {
    String selectedStatus = Filter.getSelectedItem().toString(); // e.g., "Pending", "Approved", "All"
    List<PurchaseOrder> allPOs = purchaseManager.getAllPurchaseOrders();
    List<PurchaseRequisition> allPRs = purchaseManager.getAllRequisitions();
    
    List<PurchaseOrder> filteredPOs;

    if (selectedStatus.equalsIgnoreCase("All")) {
        filteredPOs = allPOs; // No filtering
    } else {
        filteredPOs = allPOs.stream()
            .filter(po -> po.getStatus().equalsIgnoreCase(selectedStatus))
            .collect(Collectors.toList());
    }

    PurchaseOrder.updatePOTableInUI(filteredPOs, allPRs, poTable);
}*/
    public void filterPOTableByStatus() {
    String selectedStatus = Filter.getSelectedItem().toString();
    List<PurchaseOrder> allPOs = purchaseManager.getAllPurchaseOrders();
    List<PurchaseRequisition> allPRs = purchaseManager.getAllRequisitions();

    List<PurchaseOrder> filteredPOs = new ArrayList<>();

        if (selectedStatus.equalsIgnoreCase("ALL")) {
            filteredPOs.addAll(allPOs);
        } else {
            for (PurchaseOrder po : allPOs) {
                // Check if any item in this PO matches the selected status
                boolean hasMatchingItem = false;
                for (PurchaseOrder.PurchaseOrderItem item : po.getItems()) {
                    if (item.getStatus().equalsIgnoreCase(selectedStatus)) {
                        hasMatchingItem = true;
                        break;
                    }
                }
                if (hasMatchingItem) {
                    filteredPOs.add(po);
                }
            }}

    PurchaseOrder.updatePOTableInUI(filteredPOs, allPRs, poTable);
}


    /*private void setupTableSelectionListener() {
        poTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    boolean rowSelected = poTable.getSelectedRow() != -1;
                    dltBtn.setEnabled(rowSelected);
                }
            }
        });
    }*/
    
    private void setupTableSelectionListener() {
        poTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    boolean rowSelected = poTable.getSelectedRow() != -1;
                    dltBtn.setEnabled(rowSelected);
                    jButton7.setEnabled(rowSelected);
                    updateFieldsFromSelectedRow();
                }
            }
        });
    }

    private void updateFieldsFromSelectedRow() {
    int selectedRow = poTable.getSelectedRow();
    if (selectedRow == -1) {
        clearFields();
        return;
    }

    String orderId = poTable.getValueAt(selectedRow, 0).toString();
    String itemId = poTable.getValueAt(selectedRow, 1).toString();
    String supplierId = poTable.getValueAt(selectedRow, 2).toString();
    String quantityStr = poTable.getValueAt(selectedRow, 3).toString();
    String totalPriceStr = poTable.getValueAt(selectedRow, 4).toString();
    String orderDate = poTable.getValueAt(selectedRow, 5).toString();
    String status = poTable.getValueAt(selectedRow, 6).toString();
    String prId = poTable.getValueAt(selectedRow, 7).toString();
    String requiredDate = poTable.getValueAt(selectedRow, 8).toString();
    String createdBy = poTable.getValueAt(selectedRow, 9).toString();

    // Calculate unit cost
    try {
        int quantity = Integer.parseInt(quantityStr);
        double totalPrice = Double.parseDouble(totalPriceStr);
        unitCost = quantity > 0 ? totalPrice / quantity : 0.0;
    } catch (NumberFormatException e) {
        unitCost = 0.0;
    }

    // Update fields
    poField.setText(orderId);
    itemField.setText(itemId);
    quantityField.setText(quantityStr);
    totalField.setText(totalPriceStr);
    dateLbl.setText(orderDate);
    statusField.setSelectedItem(status);
    PRField.setText(prId);
    RequiredField.setText(requiredDate);
    createdField.setText(createdBy);

    // Populate SupplierField with valid suppliers without streams
    SupplierField.removeAllItems();
    List<SupplierItem> supplierItems = SupplierItem.loadSupplierItems();
    List<String> validSuppliers = new ArrayList<>();

    for (SupplierItem si : supplierItems) {
        if (si.getItemID().equalsIgnoreCase(itemId)) {
            String supId = si.getSupplierID();
            // Add if not already in list (distinct)
            boolean exists = false;
            for (String s : validSuppliers) {
                if (s.equalsIgnoreCase(supId)) {
                    exists = true;
                    break;
                }
            }
            if (!exists) {
                validSuppliers.add(supId);
            }
        }
    }

    // Sort validSuppliers alphabetically
    Collections.sort(validSuppliers, String.CASE_INSENSITIVE_ORDER);

    for (String supId : validSuppliers) {
        SupplierField.addItem(supId);
    }

    SupplierField.setSelectedItem(supplierId);
}

    
    private void clearFields() {
        poField.setText("");
        itemField.setText("");
        SupplierField.removeAllItems();
        quantityField.setText("");
        totalField.setText("");
        dateLbl.setText("");
        statusField.setSelectedIndex(0);
        PRField.setText("");
        RequiredField.setText("");
        createdField.setText("");
        unitCost = 0.0;
    }

    private void setupQuantityFieldListener() {
        quantityField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { updateTotalPrice(); }
            @Override
            public void removeUpdate(DocumentEvent e) { updateTotalPrice(); }
            @Override
            public void changedUpdate(DocumentEvent e) { updateTotalPrice(); }

            private void updateTotalPrice() {
                try {
                    int quantity = Integer.parseInt(quantityField.getText().trim());
                    if (quantity > 0) {
                        double totalPrice = quantity * unitCost;
                        totalField.setText(String.format("%.1f", totalPrice));
                    } else {
                        totalField.setText("0.0");
                    }
                } catch (NumberFormatException ex) {
                    totalField.setText("0.0");
                }
            }
        });
    }
    
    
    private void editSelectedPO() {
        int selectedRow = poTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a purchase order item to edit.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String orderId = poField.getText();
        String itemId = itemField.getText();
        String newSupplierId = SupplierField.getSelectedItem() != null ? SupplierField.getSelectedItem().toString() : "";
        String newStatus = statusField.getSelectedItem().toString();

        if (newSupplierId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a valid supplier.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            int newQuantity = Integer.parseInt(quantityField.getText().trim());
            if (newQuantity <= 0) {
                JOptionPane.showMessageDialog(this, "Quantity must be positive.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            double newTotalPrice = Double.parseDouble(totalField.getText());
            purchaseManager.updatePurchaseOrderItem(orderId, itemId, newSupplierId, newQuantity, newTotalPrice, newStatus);
            filterPOTableByStatus();
            JOptionPane.showMessageDialog(this, "Purchase order item updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid quantity format.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException | IllegalStateException ex) {
            // Error shown in updatePurchaseOrderItem
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Unexpected error updating PO item: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
private void deleteSelectedPO() {
        int selectedRow = poTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a purchase order item to delete.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String orderId = poTable.getValueAt(selectedRow, 0).toString();
        String itemId = poTable.getValueAt(selectedRow, 1).toString().split(" - ")[0];

        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete PO " + orderId + " (Item: " + itemId + ")?",
            "Confirm Deletion",
            JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                purchaseManager.deletePurchaseOrderItem(orderId, itemId);
                filterPOTableByStatus();
                JOptionPane.showMessageDialog(this, "Purchase order item deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (IllegalArgumentException | IllegalStateException e) {
                // Error already shown via JOptionPane in deletePurchaseOrderItem
                filterPOTableByStatus();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Unexpected error deleting PO item: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        dltBtn = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        poLbl = new javax.swing.JLabel();
        itemLbl = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        poField = new javax.swing.JLabel();
        itemField = new javax.swing.JLabel();
        SupplierField = new javax.swing.JComboBox<>();
        jLabel8 = new javax.swing.JLabel();
        quantityField = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        totalField = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        dateLbl = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        statusField = new javax.swing.JComboBox<>();
        jLabel13 = new javax.swing.JLabel();
        PRField = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        RequiredField = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        createdField = new javax.swing.JLabel();
        searchField = new javax.swing.JTextField();
        jButton6 = new javax.swing.JButton();
        Filter = new javax.swing.JComboBox<>();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jScrollPane6 = new javax.swing.JScrollPane();
        poTable = new javax.swing.JTable();
        jButton11 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        dltBtn.setBackground(new java.awt.Color(51, 51, 51));
        dltBtn.setForeground(new java.awt.Color(255, 255, 255));
        dltBtn.setText("Delete PO");
        dltBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dltBtnActionPerformed(evt);
            }
        });

        jButton7.setBackground(new java.awt.Color(255, 51, 51));
        jButton7.setText("Edit PO");
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        poLbl.setText("PO ID:");

        itemLbl.setText("Item ID:");

        jLabel3.setText("Supplier ID:");

        poField.setText("poID");

        itemField.setText("item");

        jLabel8.setText("Quantity:");

        quantityField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                quantityFieldActionPerformed(evt);
            }
        });

        jLabel10.setText("Total:");

        totalField.setText("Total");

        jLabel11.setText("OrderDate:");

        dateLbl.setText("Order Date");

        jLabel12.setText("Status:");

        statusField.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "PENDING", "APPROVED", "REJECTED", "RECEIVED", "UNFULFILLED", "COMPLETED" }));

        jLabel13.setText("PR ID:");

        PRField.setText("PR id");

        jLabel14.setText("Required Date:");

        RequiredField.setText("Required Date");

        jLabel15.setText("Created by:");

        createdField.setText("User ID");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(16, 16, 16)
                        .addComponent(jLabel15)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel3Layout.createSequentialGroup()
                                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel13))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                    .addGroup(jPanel3Layout.createSequentialGroup()
                                        .addComponent(jLabel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addGap(43, 43, 43)))
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel3Layout.createSequentialGroup()
                                        .addComponent(PRField, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(0, 0, Short.MAX_VALUE))
                                    .addComponent(createdField, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addGroup(jPanel3Layout.createSequentialGroup()
                                        .addComponent(RequiredField, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addGap(22, 22, 22))
                                    .addComponent(statusField, 0, 134, Short.MAX_VALUE)))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addGap(45, 45, 45)
                                .addComponent(SupplierField, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel10))
                                .addGap(49, 49, 49)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel3Layout.createSequentialGroup()
                                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(totalField, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(dateLbl, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGap(0, 0, Short.MAX_VALUE))
                                    .addComponent(quantityField)))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(jPanel3Layout.createSequentialGroup()
                                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(poLbl)
                                            .addComponent(itemLbl))
                                        .addGap(68, 68, 68)
                                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(itemField, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(poField, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addGap(16, 16, 16)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(15, 15, 15)
                        .addComponent(poLbl))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(poField)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(itemLbl)
                    .addComponent(itemField))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 10, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(SupplierField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(quantityField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(totalField))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(dateLbl))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(statusField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(PRField))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14)
                    .addComponent(RequiredField))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel15)
                    .addComponent(createdField))
                .addGap(27, 27, 27))
        );

        searchField.setText("Search PO");
        searchField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchFieldActionPerformed(evt);
            }
        });

        jButton6.setBackground(new java.awt.Color(255, 204, 204));
        jButton6.setText("Search");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        Filter.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "ALL", "PENDING", "APPROVED", "VERIFIED", "REJECTED", "RECEIVED", "UNFULFILLED", "COMPLETED" }));
        Filter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                FilterActionPerformed(evt);
            }
        });

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        poTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Order ID", "Supplier ID", "Order Date", "Status", "PR ID", "PO List"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jScrollPane6.setViewportView(poTable);

        jScrollPane1.setViewportView(jScrollPane6);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 697, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(30, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 321, Short.MAX_VALUE))
        );

        jButton11.setText("Home");
        jButton11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton11ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(282, 282, 282)
                        .addComponent(dltBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(41, 41, 41)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(49, 49, 49)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jButton11)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(searchField, javax.swing.GroupLayout.PREFERRED_SIZE, 397, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton6)
                                .addGap(103, 103, 103)
                                .addComponent(Filter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(46, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton7, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(140, 140, 140))))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGap(0, 27, Short.MAX_VALUE)
                .addComponent(jButton11)
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(searchField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton6)
                            .addComponent(Filter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(dltBtn)
                    .addComponent(jButton7))
                .addGap(15, 15, 15))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        // TODO add your handling code here:
        editSelectedPO();
    }//GEN-LAST:event_jButton7ActionPerformed

    private void searchFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_searchFieldActionPerformed

    private void dltBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dltBtnActionPerformed
        // TODO add your handling code here:
        deleteSelectedPO();
       
    }//GEN-LAST:event_dltBtnActionPerformed

    private void jButton11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton11ActionPerformed
        // TODO add your handling code here:
        this.dispose();
    }//GEN-LAST:event_jButton11ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        // TODO add your handling code here:
        String searchText = searchField.getText().trim();
        List<PurchaseOrder> allPOs = purchaseManager.getAllPurchaseOrders();
        List<PurchaseRequisition> allPRs = purchaseManager.getAllRequisitions(); 

        PurchaseOrder.searchAndDisplayPO(searchField, poTable, allPOs, allPRs);

    }//GEN-LAST:event_jButton6ActionPerformed

    private void FilterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_FilterActionPerformed
        // TODO add your handling code here:
         filterPOTableByStatus();
    }//GEN-LAST:event_FilterActionPerformed

    private void quantityFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_quantityFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_quantityFieldActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Admin_ManagePurchaseOrder.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Admin_ManagePurchaseOrder.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Admin_ManagePurchaseOrder.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Admin_ManagePurchaseOrder.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> Filter;
    private javax.swing.JLabel PRField;
    private javax.swing.JLabel RequiredField;
    private javax.swing.JComboBox<String> SupplierField;
    private javax.swing.JLabel createdField;
    private javax.swing.JLabel dateLbl;
    private javax.swing.JButton dltBtn;
    private javax.swing.JLabel itemField;
    private javax.swing.JLabel itemLbl;
    private javax.swing.JButton jButton11;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JLabel poField;
    private javax.swing.JLabel poLbl;
    private javax.swing.JTable poTable;
    private javax.swing.JTextField quantityField;
    private javax.swing.JTextField searchField;
    private javax.swing.JComboBox<String> statusField;
    private javax.swing.JLabel totalField;
    // End of variables declaration//GEN-END:variables
}
