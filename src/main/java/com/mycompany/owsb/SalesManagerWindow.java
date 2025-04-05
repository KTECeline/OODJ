/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.mycompany.owsb;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;

/**
 *
 * @author timi
 */
public class SalesManagerWindow extends javax.swing.JFrame {
    private final User loggedInUser;
    
    private final String PURCHASE_ORDER_FILE = "purchase_order.txt";
    
    private List<PurchaseOrder> purchaseOrderList = new ArrayList<>();
    

    /**
     * Creates new form SalesManagerWindow
     */
    public SalesManagerWindow(User loggedInUser) {
        this.loggedInUser = loggedInUser;
        initComponents();
        updatePurchaseOrderList(); // Update the list on UI load
    }
    
    // Method to show the UserWindow and ensure the user list is updated
    public void showSmWindow() {
        updatePurchaseOrderList();  // Ensure the list is up to date
        setVisible(true);  // Show the window
    }
    
    public List<PurchaseOrder> loadPurchaseOrders() {
        List<PurchaseOrder> purchaseOrderList = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(PURCHASE_ORDER_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                PurchaseOrder po = PurchaseOrder.fromString(line);
                purchaseOrderList.add(po);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "An error occurred while reading PO file: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        return purchaseOrderList;
    }

    // Update Purchase Order list in JList
    public void updatePurchaseOrderList() {
        List<PurchaseOrder> poListData = loadPurchaseOrders();
        DefaultListModel<String> listModel = new DefaultListModel<>();
        
        // Load lines from file
        List<String> lines = FileUtil.readLines(PURCHASE_ORDER_FILE);

        // Add new Purchase Requisitions to the list
        for (String line : lines) {
            PurchaseOrder po = PurchaseOrder.fromString(line);
            purchaseOrderList.add(po);  // Add the actual PurchaseRequisition object
            listModel.addElement(po.purchaseOrderID);  // Only display the itemID in the list
        }

        poList.setModel(listModel);
    }




    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        ManageItemsButton = new javax.swing.JButton();
        ManageSuppliersButton = new javax.swing.JButton();
        EnterDailySalesButton = new javax.swing.JButton();
        CreatePRButton = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        poDetails = new javax.swing.JTextArea();
        jScrollPane2 = new javax.swing.JScrollPane();
        poList = new javax.swing.JList<>();
        searchField = new javax.swing.JTextField();
        searchButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Sales Manager");

        ManageItemsButton.setFont(new java.awt.Font("Heiti TC", 0, 12)); // NOI18N
        ManageItemsButton.setText("Manage Items");
        ManageItemsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ManageItemsButtonActionPerformed(evt);
            }
        });

        ManageSuppliersButton.setFont(new java.awt.Font("Heiti TC", 0, 12)); // NOI18N
        ManageSuppliersButton.setText("Manage Suppliers");
        ManageSuppliersButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ManageSuppliersButtonActionPerformed(evt);
            }
        });

        EnterDailySalesButton.setFont(new java.awt.Font("Heiti TC", 0, 12)); // NOI18N
        EnterDailySalesButton.setText("Manage Daily Sales");

        CreatePRButton.setFont(new java.awt.Font("Heiti TC", 0, 12)); // NOI18N
        CreatePRButton.setText("Manage Purchase Requisition");
        CreatePRButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CreatePRButtonActionPerformed(evt);
            }
        });

        poDetails.setEditable(false);
        poDetails.setColumns(20);
        poDetails.setFont(new java.awt.Font("Heiti TC", 0, 12)); // NOI18N
        poDetails.setRows(5);
        jScrollPane1.setViewportView(poDetails);

        poList.setFont(new java.awt.Font("Heiti TC", 0, 12)); // NOI18N
        poList.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        poList.addAncestorListener(new javax.swing.event.AncestorListener() {
            public void ancestorAdded(javax.swing.event.AncestorEvent evt) {
                poListAncestorAdded(evt);
            }
            public void ancestorMoved(javax.swing.event.AncestorEvent evt) {
            }
            public void ancestorRemoved(javax.swing.event.AncestorEvent evt) {
            }
        });
        jScrollPane2.setViewportView(poList);

        searchField.setFont(new java.awt.Font("Heiti TC", 0, 12)); // NOI18N
        searchField.setForeground(new java.awt.Color(102, 102, 102));
        searchField.setText("Enter Purchase Order ID");
        searchField.setToolTipText("");
        searchField.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                searchFieldMouseClicked(evt);
            }
        });

        searchButton.setFont(new java.awt.Font("Heiti TC", 0, 12)); // NOI18N
        searchButton.setText("Search");
        searchButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(33, 33, 33)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(searchField, javax.swing.GroupLayout.PREFERRED_SIZE, 472, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(searchButton, javax.swing.GroupLayout.DEFAULT_SIZE, 88, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(CreatePRButton, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 201, Short.MAX_VALUE)
                            .addComponent(EnterDailySalesButton, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(ManageSuppliersButton, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(ManageItemsButton, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jScrollPane2)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 347, Short.MAX_VALUE))))
                .addContainerGap(37, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(64, 64, 64)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(searchField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(searchButton))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 234, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(ManageItemsButton, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ManageSuppliersButton, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(5, 5, 5)
                        .addComponent(EnterDailySalesButton, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(CreatePRButton, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(32, Short.MAX_VALUE))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void ManageItemsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ManageItemsButtonActionPerformed
        SmManageItemsWindow manageItemsWindow = new SmManageItemsWindow(this);
        manageItemsWindow.setVisible(true);
        this.setVisible(false); // Hide current window
    }//GEN-LAST:event_ManageItemsButtonActionPerformed

    private void poListAncestorAdded(javax.swing.event.AncestorEvent evt) {//GEN-FIRST:event_poListAncestorAdded
        updatePurchaseOrderList(); // Updates the JList with data from the file

        if (poList.getListSelectionListeners().length == 0) {
            poList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
                @Override
                public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                    if (!evt.getValueIsAdjusting()) {
                        int selectedIndex = poList.getSelectedIndex();

                        if (selectedIndex >= 0 && selectedIndex < purchaseOrderList.size()) {
                            PurchaseOrder selectedPO = purchaseOrderList.get(selectedIndex);
                            
                            // Debugging: Check if we got a valid Purchase Order
                            System.out.println("Selected PO: " + selectedPO.purchaseOrderID);

                            // Show the purchase order details
                            poDetails.setText(
                                "PO ID: " + selectedPO.purchaseOrderID + "\n\n" +
                                "Item Code: " + selectedPO.itemID + "\n\n" +
                                "Quantity: " + selectedPO.quantity + "\n\n" +
                                "Supplier ID: " + selectedPO.supplierID + "\n\n" +
                                "Unit Price: " + selectedPO.unitPrice + "\n\n" +
                                "Total Price: " + selectedPO.totalPrice + "\n\n" +
                                "Order Date: " + selectedPO.orderDate + "\n\n" +
                                "Status: " + selectedPO.status
                            );
                        } else {
                            poDetails.setText("No Purchase Order selected.");
                        }
                    }
                }
            });
        }

    }//GEN-LAST:event_poListAncestorAdded

    private void ManageSuppliersButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ManageSuppliersButtonActionPerformed
        SmManageSuppliersWindow manageSuppliersWindow = new SmManageSuppliersWindow(this);
        manageSuppliersWindow.setVisible(true);
        this.setVisible(false); // Hide current window
    }//GEN-LAST:event_ManageSuppliersButtonActionPerformed

    private void CreatePRButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CreatePRButtonActionPerformed
        SmManagePrWindow createPrWindow = new SmManagePrWindow(this);
        createPrWindow.setVisible(true);
        this.setVisible(false); // Hide current window
    }//GEN-LAST:event_CreatePRButtonActionPerformed

    private void searchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchButtonActionPerformed
                                           
        // Get the search query from the search field and convert to uppercase
        String searchText = searchField.getText().trim().toUpperCase();

        if (searchText.isEmpty()) {
            // If the search field is empty, show all purchase orders
            updatePurchaseOrderList();
            poDetails.setText(""); // Clear the details text area
        } else {
            // Filter the purchase orders based on purchaseOrderID
            List<PurchaseOrder> filteredList = new ArrayList<>();

            for (PurchaseOrder po : purchaseOrderList) {
                if (po.purchaseOrderID != null && po.purchaseOrderID.contains(searchText)) { // Case-insensitive search
                    filteredList.add(po);
                }
            }

            // Update the JList with filtered results using update method
            updatePurchaseOrderList();

            // If a matching Purchase Order is found, show its details in the text area
            if (!filteredList.isEmpty()) {
                PurchaseOrder selectedPO = filteredList.get(0); // Taking the first match
                poDetails.setText(
                    "PO ID: " + selectedPO.purchaseOrderID + "\n\n" +
                    "Item Code: " + selectedPO.itemID + "\n\n" +
                    "Quantity: " + selectedPO.quantity + "\n\n" +
                    "Supplier ID: " + selectedPO.supplierID + "\n\n" +
                    "Unit Price: " + selectedPO.unitPrice + "\n\n" +
                    "Total Price: " + selectedPO.totalPrice + "\n\n" +
                    "Order Date: " + selectedPO.orderDate + "\n\n" +
                    "Status: " + selectedPO.status
                );
            } else {
                poDetails.setText("No matching Purchase Order found.");
            }
        }
    }//GEN-LAST:event_searchButtonActionPerformed

    private void searchFieldMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_searchFieldMouseClicked
        searchField.setText("");
    }//GEN-LAST:event_searchFieldMouseClicked

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
            java.util.logging.Logger.getLogger(SalesManagerWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(SalesManagerWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(SalesManagerWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(SalesManagerWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new SalesManagerWindow(null).setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton CreatePRButton;
    private javax.swing.JButton EnterDailySalesButton;
    private javax.swing.JButton ManageItemsButton;
    private javax.swing.JButton ManageSuppliersButton;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextArea poDetails;
    private javax.swing.JList<String> poList;
    private javax.swing.JButton searchButton;
    private javax.swing.JTextField searchField;
    // End of variables declaration//GEN-END:variables
}
