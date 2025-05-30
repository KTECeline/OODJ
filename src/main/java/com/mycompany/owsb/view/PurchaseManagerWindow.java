package com.mycompany.owsb.view;

import com.mycompany.owsb.model.Item;
import com.mycompany.owsb.model.PurchaseManager;
import com.mycompany.owsb.model.PurchaseOrder;
import com.mycompany.owsb.model.PurchaseRequisition;
import com.mycompany.owsb.model.PurchaseRequisitionItem;
import com.mycompany.owsb.model.User;
import com.mycompany.owsb.view.LoginWindow;
import com.mycompany.owsb.view.PmViewItem;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.table.DefaultTableModel;
import javax.swing.JOptionPane;
/**


/**
 *
 * @author timi
 */
public class PurchaseManagerWindow extends javax.swing.JFrame {
    private final User loggedInUser;

    private final PurchaseManager purchaseManager;
    private List<PurchaseOrder> purchaseOrderList;
    private List<PurchaseRequisition> prList;
    private List<Item> itemList;
    private List<PurchaseRequisitionItem> prItemList;

    /**
     * Creates new form PurchaseManagerWindow
     */
    public PurchaseManagerWindow(User loggedInUser, PurchaseManager purchaseManager) {
        this.loggedInUser = loggedInUser;
        this.purchaseManager= purchaseManager;
        initComponents();
        jTextField1.setText("Enter PR ID");
        loadPRTable("All");
        loadSummaryLabels();

    }

    public void showPmWindow() {
        setVisible(true);  // Show the window
    }
    
    public void initializeFilterComboBox() {
        Filter.removeAllItems();
        Filter.addItem("All");
        Filter.addItem("PENDING");
        Filter.addItem("APPROVED");
        Filter.addItem("REJECTED");
        Filter.addActionListener(e -> {
            String statusFilter = Filter.getSelectedItem().toString();
            loadPRTable(statusFilter);
        });
    }
    
    private void loadPRTable(String statusFilter) {
    // Always load fresh data
    List<PurchaseRequisition> allPRs = purchaseManager.getAllRequisitions();
        System.out.println("Total PRs" + allPRs.size());// Make sure this returns fresh full data0
    List<PurchaseRequisitionItem> prItems = PurchaseRequisitionItem.loadPurchaseRequisitionItems();
    List<Item> items = purchaseManager.getAllItems();

    // Table model with editable checkbox column
    DefaultTableModel model = new DefaultTableModel(
        new String[]{"PR ID", "Item ID", "Supplier ID", "Quantity", "Required Date", "Raised By", "Unit Cost", "Total Cost", "Status", "Select"},
        0
    ) {
        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return columnIndex == 9 ? Boolean.class : super.getColumnClass(columnIndex);
        }

        @Override
        public boolean isCellEditable(int row, int column) {
            return column == 9; // Only checkbox editable
        }
    };

    // Filter and populate
    for (PurchaseRequisition pr : allPRs) {
        if (statusFilter.equalsIgnoreCase("All") || pr.getStatus().equalsIgnoreCase(statusFilter)) {
            for (PurchaseRequisitionItem prItem : prItems) {
                if (prItem.getPrID().equalsIgnoreCase(pr.getPrID())) {
                    String itemName = items.stream()
                        .filter(item -> item.getItemID().equalsIgnoreCase(prItem.getItemID()))
                        .findFirst()
                        .map(Item::getItemName)
                        .orElse("");
                    
                    Object[] row = {
                        pr.getPrID(),
                        prItem.getItemID() + " - " + itemName,
                        pr.getSupplierID(),
                        prItem.getQuantity(),
                        pr.getRequiredDate(),
                        pr.getRaisedBy(),
                        prItem.getUnitCost(),
                        prItem.getTotalCost(),
                        pr.getStatus(),
                        false // Default unchecked
                    };
                    model.addRow(row);
                }
            }
        }
    }

    // Set and refresh table
    prTable.setModel(model);
    PurchaseRequisition.autoResizeColumnWidths(prTable);
    PurchaseRequisition.applyColorBasedOnPrID(prTable); // Optional styling
}

    
     private void loadSummaryLabels() {
        int totalItems = purchaseManager.getAllItems().size();
        int totalSuppliers = purchaseManager.getAllSuppliers().size();
        long pendingPRs = purchaseManager.getAllRequisitions().stream()
                             .filter(pr -> pr.getStatus().equalsIgnoreCase("PENDING"))
                             .count();
        int pendingPOs = purchaseManager.getOrdersByStatus("PENDING").size();

       lblTotalItems.setText(Integer.toString(totalItems));
        lblTotalSuppliers.setText(Integer.toString( totalSuppliers));
        lblPendingPRs.setText(Long.toString(pendingPRs));
        lblPendingPOs.setText(Integer.toString (pendingPOs));
}
    
    private List<String> getApprovedItemIDsFromPR(String prId) {
    List<PurchaseRequisitionItem> prItems = PurchaseRequisitionItem.loadPurchaseRequisitionItems();
    List<String> itemIds = new ArrayList<>();

    for (PurchaseRequisitionItem item : prItems) {
        if (item.getPrID().equalsIgnoreCase(prId)) {
            itemIds.add(item.getItemID());
        }
    }

    return itemIds;
}

    private void generatePOsFromSelectedRows() {
    DefaultTableModel model = (DefaultTableModel) prTable.getModel();
    if (model.getRowCount() == 0) {
        JOptionPane.showMessageDialog(this, "Table is empty. No PRs to process.", "Warning", JOptionPane.WARNING_MESSAGE);
        return;
    }

    // supplierId → (prId → itemIds)
    Map<String, Map<String, List<String>>> supplierToPRsMap = new HashMap<>();
    boolean hasSelections = false;

    for (int row = 0; row < model.getRowCount(); row++) {
        Boolean isSelected = (Boolean) model.getValueAt(row, 9);
        if (isSelected != null && isSelected) {
            hasSelections = true;
            String prId = model.getValueAt(row, 0).toString();
            String itemId = model.getValueAt(row, 1).toString().split(" - ")[0];
            String supplierId = model.getValueAt(row, 2).toString();
            String status = model.getValueAt(row, 8).toString();

            if (!status.equalsIgnoreCase("PENDING")) {
                JOptionPane.showMessageDialog(this, "Only PENDING PRs can be selected for PO generation. Invalid PR: " + prId, "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            supplierToPRsMap
                .computeIfAbsent(supplierId, k -> new HashMap<>()) // supplierId group
                .computeIfAbsent(prId, k -> new ArrayList<>())     // PR group under supplier
                .add(itemId);
        }
    }

    if (!hasSelections) {
        JOptionPane.showMessageDialog(this, "No rows selected for PO generation.", "Warning", JOptionPane.WARNING_MESSAGE);
        return;
    }

    try {
        int poCount = 0;
        String createdBy = purchaseManager.getLoggedInUser().getUserId();

        for (String supplierId : supplierToPRsMap.keySet()) {
            Map<String, List<String>> approvedItemsByPR = supplierToPRsMap.get(supplierId);
            purchaseManager.generatePurchaseOrdersFromMultiplePRs(supplierId, createdBy, approvedItemsByPR);
            poCount++;
        }

        String statusFilter = Filter.getSelectedItem().toString();
        loadPRTable(statusFilter);

        JOptionPane.showMessageDialog(this, "Successfully generated " + poCount + " purchase order(s).", "Success", JOptionPane.INFORMATION_MESSAGE);

        for (int row = 0; row < model.getRowCount(); row++) {
            model.setValueAt(false, row, 9);
        }
    } catch (IllegalArgumentException | IllegalStateException e) {
        String statusFilter = Filter.getSelectedItem().toString();
        loadPRTable(statusFilter);
        JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Unexpected error generating POs: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
        LoggedIn = new javax.swing.JLabel();
        jToggleButton1 = new javax.swing.JToggleButton();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        lblTotalSuppliers = new javax.swing.JLabel();
        lblTotalItems = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        lblPendingPRs = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        lblPendingPOs = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jButton6 = new javax.swing.JButton();
        Filter = new javax.swing.JComboBox<>();
        jButton7 = new javax.swing.JButton();
        jScrollPane7 = new javax.swing.JScrollPane();
        jScrollPane6 = new javax.swing.JScrollPane();
        prTable = new javax.swing.JTable();
        jButton8 = new javax.swing.JButton();
        Itembtn = new javax.swing.JButton();
        Supplierbtn = new javax.swing.JButton();
        PRbtn = new javax.swing.JButton();
        pendingbtn = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setText("Purchase Order Management System");

        jLabel2.setText("Logged in as: ");

        LoggedIn.setText("jLabel3");

        jToggleButton1.setText("Log Out");
        jToggleButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButton1ActionPerformed(evt);
            }
        });

        jLabel3.setText("Total Items: ");

        jLabel4.setText("Total Suppliers: ");

        lblTotalSuppliers.setText("jLabel5");

        lblTotalItems.setText("jLabel6");

        jLabel7.setText("Pending PRs:");

        lblPendingPRs.setText("jLabel8");

        jLabel9.setText("Pending POs:");

        lblPendingPOs.setText("jLabel10");

        jTextField1.setText("Search PR");

        jButton6.setText("Search");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        Filter.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "ALL", "PENDING", "APPROVED", "REJECTED" }));
        Filter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                FilterActionPerformed(evt);
            }
        });

        jButton7.setText("Generate PO");
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        prTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "PR ID", "Item ID", "Quantity", "Unit Cost", "Total Cost", "Select"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.Double.class, java.lang.Double.class, java.lang.Boolean.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jScrollPane6.setViewportView(prTable);

        jScrollPane7.setViewportView(jScrollPane6);

        jButton8.setText("Home");

        Itembtn.setText("View Item");
        Itembtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ItembtnActionPerformed(evt);
            }
        });

        Supplierbtn.setText("View Supplier");
        Supplierbtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SupplierbtnActionPerformed(evt);
            }
        });

        PRbtn.setText("View PR");
        PRbtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                PRbtnActionPerformed(evt);
            }
        });

        pendingbtn.setText("View PO");
        pendingbtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pendingbtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(281, 281, 281)
                .addComponent(jLabel1)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(34, 34, 34)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(LoggedIn, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(42, 42, 42)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 397, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(Filter, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 584, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jToggleButton1)
                                    .addComponent(jButton7)))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel3)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(lblTotalItems, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(29, 29, 29)
                                        .addComponent(jLabel4))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jButton8)
                                        .addGap(36, 36, 36)
                                        .addComponent(Itembtn)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(Supplierbtn)
                                        .addGap(39, 39, 39)
                                        .addComponent(PRbtn)
                                        .addGap(36, 36, 36)
                                        .addComponent(pendingbtn))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(lblTotalSuppliers, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(27, 27, 27)
                                        .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(lblPendingPRs, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(22, 22, 22)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(jButton6)
                                            .addGroup(layout.createSequentialGroup()
                                                .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(lblPendingPOs, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)))))))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(11, 11, 11)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(LoggedIn)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(19, 19, 19)
                        .addComponent(jToggleButton1)))
                .addGap(14, 14, 14)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton8)
                    .addComponent(Itembtn)
                    .addComponent(Supplierbtn)
                    .addComponent(PRbtn)
                    .addComponent(pendingbtn))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel4)
                        .addComponent(lblTotalSuppliers)
                        .addComponent(lblTotalItems)
                        .addComponent(jLabel7)
                        .addComponent(lblPendingPRs)
                        .addComponent(jLabel9)
                        .addComponent(lblPendingPOs)))
                .addGap(39, 39, 39)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton6)
                    .addComponent(Filter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(294, 294, 294)
                        .addComponent(jButton7))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 303, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(38, Short.MAX_VALUE))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jToggleButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButton1ActionPerformed
        // TODO add your handling code here:
        this.dispose(); // Closes the current SalesManagerWindow

        LoginWindow loginWindow = new LoginWindow();
        loginWindow.setVisible(true);
        
    }//GEN-LAST:event_jToggleButton1ActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
            generatePOsFromSelectedRows();     
    }//GEN-LAST:event_jButton7ActionPerformed

    private void ItembtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ItembtnActionPerformed
        // TODO add your handling code here:
        PmViewItem viewItemsWindow = new PmViewItem(this, purchaseManager);
        viewItemsWindow.setVisible(true);
        this.setVisible(false); // Hide current window
    }//GEN-LAST:event_ItembtnActionPerformed

    private void SupplierbtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SupplierbtnActionPerformed
        // TODO add your handling code here:
        PmViewSupplier viewSupplierWindow = new PmViewSupplier(this, purchaseManager);
        viewSupplierWindow.setVisible(true);
        this.setVisible(false);
    }//GEN-LAST:event_SupplierbtnActionPerformed

    private void PRbtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_PRbtnActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_PRbtnActionPerformed

    private void pendingbtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pendingbtnActionPerformed
        // TODO add your handling code here:
        PmViewPO viewPOWindow = new PmViewPO(this, purchaseManager);
        viewPOWindow.setVisible(true);
        this.setVisible(false);

    }//GEN-LAST:event_pendingbtnActionPerformed

    private void FilterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_FilterActionPerformed
        // TODO add your handling code here:
        String searchQuery = jTextField1.getText().trim();
            String statusFilter = Filter.getSelectedItem().toString();
            loadPRTable(statusFilter);
    }//GEN-LAST:event_FilterActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        // TODO add your handling code here:
        String searchQuery = jTextField1.getText().trim();
if (searchQuery.isEmpty() || searchQuery.equalsIgnoreCase("Enter PR ID")) {
    String statusFilter = Filter.getSelectedItem().toString();
    loadPRTable(statusFilter);
} else {
    List<PurchaseRequisition> allPRs = purchaseManager.getAllRequisitions();
    List<PurchaseRequisitionItem> prItems = PurchaseRequisitionItem.loadPurchaseRequisitionItems();
    List<Item> items = purchaseManager.getAllItems();

    // 🔥 Link items to each PR so pr.getItems() will work
    for (PurchaseRequisition pr : allPRs) {
        List<PurchaseRequisitionItem> itemsForPR = prItems.stream()
            .filter(item -> item.getPrID().equalsIgnoreCase(pr.getPrID()))
            .collect(Collectors.toList());
        pr.setPRItems(itemsForPR); // Make sure your PurchaseRequisition class has this setter
    }

    // ✅ Now call your unchanged method
    PurchaseRequisition.searchAndDisplayPRInTable(jTextField1, prTable, allPRs, items, prItems);
}

    }//GEN-LAST:event_jButton6ActionPerformed

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
            java.util.logging.Logger.getLogger(PurchaseManagerWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(PurchaseManagerWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(PurchaseManagerWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(PurchaseManagerWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
       
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> Filter;
    private javax.swing.JButton Itembtn;
    private javax.swing.JLabel LoggedIn;
    private javax.swing.JButton PRbtn;
    private javax.swing.JButton Supplierbtn;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JToggleButton jToggleButton1;
    private javax.swing.JLabel lblPendingPOs;
    private javax.swing.JLabel lblPendingPRs;
    private javax.swing.JLabel lblTotalItems;
    private javax.swing.JLabel lblTotalSuppliers;
    private javax.swing.JButton pendingbtn;
    private javax.swing.JTable prTable;
    // End of variables declaration//GEN-END:variables
}
