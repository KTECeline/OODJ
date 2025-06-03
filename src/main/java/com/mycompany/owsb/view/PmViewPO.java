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
public class PmViewPO extends javax.swing.JFrame {
     private final PurchaseManagerWindow parentWindow;
    private final PurchaseManager purchaseManager;
    
    private double unitCost = 0.0;
    /**
     * Creates new form SmManageDailySalesWindow
     * @param parentWindow
     */
    public PmViewPO(PurchaseManagerWindow parentWindow, PurchaseManager purchaseManager) {
        this.parentWindow = parentWindow;
        this.purchaseManager = purchaseManager;
        
        initComponents();
        setupWindowListener();
        
        loadPOsIntoList();
        setupTableSelectionListener();
        setupQuantityFieldListener();
        String username = purchaseManager.getLoggedInUser().getUsername();
        Usernamelbl.setText(username);
    }
    
     private void setupWindowListener() {
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                parentWindow.showPmWindow(); // Show the homepage
                dispose(); // Close the current window
            }
        });
    }
     
   
    
    private void loadPOsIntoList() {
        // Load the list of Purchase Orders from the purchase order file
        List<PurchaseOrder> allPOs = purchaseManager.getAllPurchaseOrders();
    List<PurchaseRequisition> allPRs = purchaseManager.getAllRequisitions();
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
        List<PurchaseOrder> allPOs = PurchaseOrder.loadPurchaseOrders();
        List<PurchaseRequisition> allPRs = PurchaseRequisition.loadPurchaseRequisition();

        List<PurchaseOrder> filteredPOs = new ArrayList<>();

        for (PurchaseOrder po : allPOs) {
            List<PurchaseOrder.PurchaseOrderItem> filteredItems = new ArrayList<>();

            if (selectedStatus.equalsIgnoreCase("ALL")) {
                filteredItems.addAll(po.getItems());
            } else {
                for (PurchaseOrder.PurchaseOrderItem item : po.getItems()) {
                    if (item.getStatus().equalsIgnoreCase(selectedStatus)) {
                        filteredItems.add(item);
                    }
                }
            }

            if (!filteredItems.isEmpty()) {
                PurchaseOrder filteredPO = new PurchaseOrder(
                    po.getOrderID(),
                    po.getSupplierID(),
                    po.getOrderDate(),
                    po.getPrId(),
                    po.getCreatedBy()
                );

                // Add only the matching items
                for (PurchaseOrder.PurchaseOrderItem item : filteredItems) {
                    filteredPO.addItem(item);
                }

                filteredPOs.add(filteredPO);
            }
        }


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

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        Usernamelbl = new javax.swing.JLabel();
        jToggleButton1 = new javax.swing.JToggleButton();
        searchField = new javax.swing.JTextField();
        jButton6 = new javax.swing.JButton();
        Filter = new javax.swing.JComboBox<>();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jScrollPane6 = new javax.swing.JScrollPane();
        poTable = new javax.swing.JTable();
        jButton11 = new javax.swing.JButton();
        jButton12 = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        dltBtn = new javax.swing.JButton();
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
        jButton7 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setText("Purchase Order Management System");

        jLabel2.setText("Logged in as: ");

        Usernamelbl.setText("jLabel3");

        jToggleButton1.setText("Log Out");
        jToggleButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButton1ActionPerformed(evt);
            }
        });

        searchField.setText("Search PO");
        searchField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchFieldActionPerformed(evt);
            }
        });

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
                .addContainerGap(27, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 311, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jButton11.setText("Home");
        jButton11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton11ActionPerformed(evt);
            }
        });

        jButton12.setText("View Item");
        jButton12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton12ActionPerformed(evt);
            }
        });

        jButton1.setText("View Supplier");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("View PR");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setText("View PO");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        dltBtn.setText("Delete PO");
        dltBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dltBtnActionPerformed(evt);
            }
        });

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));

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

        statusField.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "PENDING", "APPROVED", "REJECTED", "RECEIVED", "UNFULFILLED", "VERIFIED", "COMPLETED" }));

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
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(jPanel3Layout.createSequentialGroup()
                                        .addComponent(poLbl)
                                        .addGap(21, 21, 21))
                                    .addGroup(jPanel3Layout.createSequentialGroup()
                                        .addComponent(itemLbl)
                                        .addGap(18, 18, 18)))
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(poField, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 89, Short.MAX_VALUE)
                                    .addComponent(itemField, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(quantityField, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                                        .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(dateLbl, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                                        .addComponent(jLabel10)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(totalField, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                                        .addComponent(jLabel3)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(SupplierField, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(6, 6, 6)))
                        .addGap(16, 16, 16))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel15)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(createdField, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(statusField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(6, 6, 6))))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(RequiredField, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(22, 22, 22)))
                .addContainerGap())
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(51, 51, 51)
                .addComponent(jLabel13)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(PRField, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(poLbl)
                    .addComponent(poField))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 13, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(itemLbl)
                    .addComponent(itemField))
                .addGap(18, 18, 18)
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

        jButton7.setText("Edit PO");
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(52, 52, 52)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jButton11)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(Usernamelbl, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 107, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jToggleButton1)
                        .addGap(35, 35, 35))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jButton12, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(83, 83, 83)
                        .addComponent(jButton1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 107, Short.MAX_VALUE)
                        .addComponent(jButton2)
                        .addGap(88, 88, 88)
                        .addComponent(jButton3)
                        .addGap(76, 76, 76))))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(8, 8, 8)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel1)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(searchField, javax.swing.GroupLayout.PREFERRED_SIZE, 397, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(70, 70, 70)
                                        .addComponent(Filter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(40, 40, 40)
                                .addComponent(jButton6)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(288, 288, 288)
                        .addComponent(dltBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton7, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(59, 59, 59))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(16, 16, 16)
                        .addComponent(jToggleButton1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 31, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton3)
                            .addComponent(jButton2)
                            .addComponent(jButton1)
                            .addComponent(jButton12)
                            .addComponent(jButton11))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 31, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(23, 23, 23)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(Usernamelbl)
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(searchField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(Filter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton6))
                        .addGap(18, 18, 18)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(dltBtn)
                    .addComponent(jButton7))
                .addContainerGap())
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jToggleButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButton1ActionPerformed
        // TODO add your handling code here:
        WindowUtil.logoutAndRedirectToLogin(this);
        
    }//GEN-LAST:event_jToggleButton1ActionPerformed

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
        WindowUtil.goBack(this, parentWindow);
    }//GEN-LAST:event_jButton11ActionPerformed

    private void jButton12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton12ActionPerformed
        // TODO add your handling code here:
        PmViewItem approveWindow = new PmViewItem(parentWindow, purchaseManager);
        approveWindow.setVisible(true);
        this.setVisible(false);
    }//GEN-LAST:event_jButton12ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        PmViewSupplier supplierWindow = new PmViewSupplier(parentWindow, purchaseManager);
        supplierWindow.setVisible(true);
        this.setVisible(false);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        PmViewPR nextWindow = new PmViewPR(parentWindow, purchaseManager);
        nextWindow.setVisible(true);
        this.setVisible(false);

    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
        PmViewPO nextWindow = new PmViewPO(parentWindow, purchaseManager);
        nextWindow.setVisible(true);
        this.setVisible(false);
    }//GEN-LAST:event_jButton3ActionPerformed

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
            java.util.logging.Logger.getLogger(PmViewPO.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(PmViewPO.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(PmViewPO.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(PmViewPO.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
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
    private javax.swing.JLabel Usernamelbl;
    private javax.swing.JLabel createdField;
    private javax.swing.JLabel dateLbl;
    private javax.swing.JButton dltBtn;
    private javax.swing.JLabel itemField;
    private javax.swing.JLabel itemLbl;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton11;
    private javax.swing.JButton jButton12;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JToggleButton jToggleButton1;
    private javax.swing.JLabel poField;
    private javax.swing.JLabel poLbl;
    private javax.swing.JTable poTable;
    private javax.swing.JTextField quantityField;
    private javax.swing.JTextField searchField;
    private javax.swing.JComboBox<String> statusField;
    private javax.swing.JLabel totalField;
    // End of variables declaration//GEN-END:variables
}
