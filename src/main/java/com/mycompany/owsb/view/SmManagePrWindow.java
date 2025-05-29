/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.mycompany.owsb.view;

import com.mycompany.owsb.model.Item;
import com.mycompany.owsb.model.PurchaseRequisition;
import com.mycompany.owsb.model.PurchaseRequisitionItem;
import com.mycompany.owsb.model.SalesManager;
import com.mycompany.owsb.model.Supplier;
import com.mycompany.owsb.model.SupplierItem;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

/**
 *
 * @author timi
 */
public class SmManagePrWindow extends javax.swing.JFrame {
    private final SalesManagerWindow parentWindow;
    
    private PurchaseRequisition pr;
    private SalesManager salesManager;
        
    
    private final String PR_FILE = "data/purchase_requisition.txt";

    private java.util.List<PurchaseRequisition> prDataList = new ArrayList<>();
    private java.util.List<PurchaseRequisitionItem> prItemDataList = new ArrayList<>();
    private java.util.List<Item> itemDataList = new ArrayList<>();
    private java.util.List<Supplier> supplierDataList = new ArrayList<>();
    private java.util.List<SupplierItem> supplierItemDataList = new ArrayList<>();

    /**
     * Creates new form NewJFrame
     * @param parentWindow
     */
    public SmManagePrWindow(SalesManagerWindow parentWindow, SalesManager salesManager) {
        this.parentWindow = parentWindow;
        this.salesManager = salesManager;
        initComponents();
        loadPRsIntoTable();
        setupWindowListener();        
    }
    
    // close button go back to menu instead of close system  
    private void setupWindowListener() {
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                parentWindow.showSmWindow(); // Show the homepage
                dispose(); // Close the current window
            }
        });
    }
    
    // Load PRs and their items, stitch them, display in table
    private void loadPRsIntoTable() {
        prDataList = PurchaseRequisition.loadPurchaseRequisition();
        prItemDataList = PurchaseRequisitionItem.loadPurchaseRequisitionItems();
        supplierItemDataList = SupplierItem.loadSupplierItems();
        itemDataList = Item.loadItems();
        
        // Attach items to their parent PR
        for (PurchaseRequisitionItem item : prItemDataList) {
            for (PurchaseRequisition pr : prDataList) {
                if (pr.getPrID().equals(item.getPrID())) {
                    pr.addItem(item);
                    break;
                }
            }
        }

        // Update JTable with stitched data
        PurchaseRequisition.updatePRTableInUI(prDataList, prItemDataList, itemDataList, prTable);
    }


    // Prompt user for PR ID to edit
    private void promptForPRID() {
        String inputPRID = JOptionPane.showInputDialog(
            null,
            "Enter the Purchase Requisition ID to edit:",
            "PR ID",
            JOptionPane.PLAIN_MESSAGE
        );

        if (inputPRID != null && !inputPRID.trim().isEmpty()) {
            inputPRID = inputPRID.trim().toUpperCase();

            // Find matching PR
            PurchaseRequisition prToEdit = null;
            for (PurchaseRequisition pr : prDataList) {
                if (pr.getPrID().equalsIgnoreCase(inputPRID)) {
                    prToEdit = pr;
                    break;
                }
            }

            if (prToEdit != null) {
                // Collect all item IDs under this PR
                List<String> matchingItemIDs = new ArrayList<>();
                for (PurchaseRequisitionItem item : prItemDataList) {
                    if (item.getPrID().equalsIgnoreCase(inputPRID)) {
                        matchingItemIDs.add(item.getItemID());
                    }
                }

                if (!matchingItemIDs.isEmpty()) {
                    // Show dropdown for user to select item
                    String selectedItemID = (String) JOptionPane.showInputDialog(
                        null,
                        "Select the Item ID under this PR to edit:",
                        "Select Item ID",
                        JOptionPane.PLAIN_MESSAGE,
                        null,
                        matchingItemIDs.toArray(),
                        matchingItemIDs.get(0)
                    );

                    if (selectedItemID != null && !selectedItemID.trim().isEmpty()) {
                        // Find matching PR item
                        PurchaseRequisitionItem itemToEdit = null;
                        for (PurchaseRequisitionItem item : prItemDataList) {
                            if (item.getPrID().equalsIgnoreCase(inputPRID) &&
                                item.getItemID().equalsIgnoreCase(selectedItemID)) {
                                itemToEdit = item;
                                break;
                            }
                        }

                        if (itemToEdit != null) {
                            // Open edit window
                            salesManager.editPurchaseRequisition(
                                this,
                                prToEdit,
                                itemToEdit,
                                prDataList,
                                prItemDataList,
                                supplierItemDataList,
                                itemDataList,
                                supplierDataList,
                                prTable
                            );

                            // Refresh table
                            PurchaseRequisition.updatePRTableInUI(prDataList, prItemDataList, itemDataList, prTable);
                        } else {
                            JOptionPane.showMessageDialog(null, "Item ID not found under this PR.");
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "No Item ID selected.");
                    }

                } else {
                    JOptionPane.showMessageDialog(null, "No items found under this PR.");
                }

            } else {
                JOptionPane.showMessageDialog(null, "Purchase Requisition ID not found.");
            }

        } else {
            JOptionPane.showMessageDialog(null, "Please enter a valid Purchase Requisition ID.");
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

        jPanel3 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        prTable = new javax.swing.JTable();
        backButton = new javax.swing.JButton();
        addPRButton = new javax.swing.JButton();
        searchField = new javax.swing.JTextField();
        searchButton = new javax.swing.JButton();
        editItemButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Purchase Requisition");

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));

        prTable.setFont(new java.awt.Font("Heiti TC", 0, 12)); // NOI18N
        prTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "PR ID", "Item ID", "Quantity", "Supplier ID", "Raised By", "Unit Cost", "Total Cost"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        prTable.setGridColor(new java.awt.Color(102, 102, 102));
        prTable.setRowHeight(25);
        prTable.setSelectionBackground(new java.awt.Color(51, 51, 51));
        prTable.setSelectionForeground(new java.awt.Color(255, 255, 255));
        jScrollPane3.setViewportView(prTable);

        backButton.setBackground(new java.awt.Color(102, 102, 102));
        backButton.setFont(new java.awt.Font("Heiti TC", 0, 12)); // NOI18N
        backButton.setForeground(new java.awt.Color(255, 255, 255));
        backButton.setText("<");
        backButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                backButtonActionPerformed(evt);
            }
        });

        addPRButton.setBackground(new java.awt.Color(255, 51, 51));
        addPRButton.setFont(new java.awt.Font("Heiti TC", 0, 12)); // NOI18N
        addPRButton.setForeground(new java.awt.Color(255, 255, 255));
        addPRButton.setText("Add");
        addPRButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addPRButtonActionPerformed(evt);
            }
        });

        searchField.setFont(new java.awt.Font("Heiti TC", 0, 12)); // NOI18N
        searchField.setForeground(new java.awt.Color(51, 51, 51));
        searchField.setText("Enter PR ID");
        searchField.setToolTipText("");
        searchField.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                searchFieldMouseClicked(evt);
            }
        });

        searchButton.setBackground(new java.awt.Color(102, 102, 102));
        searchButton.setFont(new java.awt.Font("Heiti TC", 0, 12)); // NOI18N
        searchButton.setForeground(new java.awt.Color(255, 255, 255));
        searchButton.setText("Search");
        searchButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchButtonActionPerformed(evt);
            }
        });

        editItemButton.setBackground(new java.awt.Color(255, 204, 0));
        editItemButton.setFont(new java.awt.Font("Heiti TC", 0, 12)); // NOI18N
        editItemButton.setText("Edit");
        editItemButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editItemButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 741, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(addPRButton, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(editItemButton, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(backButton, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(searchField, javax.swing.GroupLayout.PREFERRED_SIZE, 594, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(searchButton, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(44, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(43, 43, 43)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE, false)
                    .addComponent(searchField)
                    .addComponent(backButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(searchButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(addPRButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(editItemButton))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 288, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(43, 43, 43))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void backButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_backButtonActionPerformed
        // Update JTable to the latest
        PurchaseRequisition.updatePRTableInUI(prDataList, prItemDataList, itemDataList, prTable);
    }//GEN-LAST:event_backButtonActionPerformed

    private void searchFieldMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_searchFieldMouseClicked
        searchField.setText("");
    }//GEN-LAST:event_searchFieldMouseClicked

    private void searchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchButtonActionPerformed
        // Reload data from file
        loadPRsIntoTable();
        
        PurchaseRequisition.searchAndDisplayPRInTable(searchField, prTable, prDataList, itemDataList, prItemDataList);
    }//GEN-LAST:event_searchButtonActionPerformed

    private void addPRButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addPRButtonActionPerformed
        supplierDataList = Supplier.loadSuppliers();
        supplierItemDataList = SupplierItem.loadSupplierItems();
        itemDataList = Item.loadItems();
        salesManager.addPurchaseRequisition(this, itemDataList, prDataList, prItemDataList, supplierDataList, supplierItemDataList, prTable);
    }//GEN-LAST:event_addPRButtonActionPerformed

    private void editItemButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editItemButtonActionPerformed
        itemDataList = Item.loadItems();
        supplierDataList = Supplier.loadSuppliers();
        
        int selectedRow = prTable.getSelectedRow();

        if (selectedRow != -1) {
            String selectedPrID = prTable.getValueAt(selectedRow, 0).toString();
            String selectedItemIDWithName = prTable.getValueAt(selectedRow, 1).toString();  // column 1: "IT0001 - Laptop"
            String selectedItemID = selectedItemIDWithName.split(" - ")[0].trim();         // get only "IT0001"

            int confirm = JOptionPane.showConfirmDialog(
                null,
                "Are you sure you want to edit PR " + selectedPrID + " for item " + selectedItemIDWithName + "?",
                "Confirm Edit",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
            );

            if (confirm == JOptionPane.YES_OPTION) {
                PurchaseRequisition prToEdit = null;
                PurchaseRequisitionItem itemToEdit = null;

                // Find the PR
                for (PurchaseRequisition pr : prDataList) {
                    if (pr.getPrID().equalsIgnoreCase(selectedPrID)) {
                        prToEdit = pr;
                        break;
                    }
                }

                // Find the PR item (needs both PR ID + Item ID)
                for (PurchaseRequisitionItem prItem : prItemDataList) {
                    if (prItem.getPrID().equalsIgnoreCase(selectedPrID) &&
                        prItem.getItemID().equalsIgnoreCase(selectedItemID)) {
                        itemToEdit = prItem;
                        break;
                    }
                }

                if (prToEdit != null && itemToEdit != null) {
                    salesManager.editPurchaseRequisition(
                        this,  // parent
                        prToEdit,
                        itemToEdit,
                        prDataList,
                        prItemDataList,
                        supplierItemDataList,
                        itemDataList,
                        supplierDataList,
                        prTable
                    );
                    PurchaseRequisition.updatePRTableInUI(prDataList, prItemDataList, itemDataList, prTable);
                } else {
                    JOptionPane.showMessageDialog(null, "Could not find matching PR or item.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }

        } else {
            promptForPRID();
        }
    }//GEN-LAST:event_editItemButtonActionPerformed

    /**
     * @param args the command line arguments
     */


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addPRButton;
    private javax.swing.JButton backButton;
    private javax.swing.JButton editItemButton;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTable prTable;
    private javax.swing.JButton searchButton;
    private javax.swing.JTextField searchField;
    // End of variables declaration//GEN-END:variables
}
