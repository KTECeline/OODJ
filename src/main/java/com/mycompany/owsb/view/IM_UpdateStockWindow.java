package com.mycompany.owsb.view;

import com.mycompany.owsb.model.IM_UpdateStock;
import com.mycompany.owsb.model.User;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.time.LocalDate;

public class IM_UpdateStockWindow extends javax.swing.JFrame {

    private final User loggedInUser;
    private final IM_UpdateStock updater;
    
    public IM_UpdateStockWindow(User loggedInUser) {
        this.loggedInUser = loggedInUser;
        initComponents();
        updater = new IM_UpdateStock();
        //Load initial data for table
        populateTable();
    }

    private void populateTable() {
        DefaultTableModel model = (DefaultTableModel) PurchaseOrderTable.getModel();
        //Clear existing data before adding new data
        model.setRowCount(0);
        PurchaseOrderTable.clearSelection();
        //Call backend method to retrieve all approved and unfulfilled purchase orders
        for (String[] po : updater.getUnfulfilledApprovedPOs()) {
            model.addRow(po);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        Back = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        PurchaseOrderTable = new javax.swing.JTable();
        jLabel2 = new javax.swing.JLabel();
        ReceivedAmountField = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        NewStockReceivedField = new javax.swing.JTextField();
        ConfirmButton = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        SelectedOrderField = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        ReceivedDateField = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        ItemIdField = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        Back.setBackground(new java.awt.Color(102, 102, 102));
        Back.setFont(new java.awt.Font("Heiti TC", 0, 12)); // NOI18N
        Back.setForeground(new java.awt.Color(255, 255, 255));
        Back.setText("<");
        Back.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BackActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel1.setText("Approved Purchase Order");

        PurchaseOrderTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Order_ID", "Item_ID", "Supplier_ID", "Quantity", "Total Price", "Order Date", "Status", "PR_ID", "User_ID"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        PurchaseOrderTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                TableRowSelected(evt);
            }
        });
        jScrollPane1.setViewportView(PurchaseOrderTable);

        jLabel2.setText("Selected Order:");

        ReceivedAmountField.setEditable(false);

        jLabel3.setText("New Stock Received:");

        ConfirmButton.setText("Confirm");
        ConfirmButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ConfirmButtonActionPerformed(evt);
            }
        });

        jLabel4.setText("Received Amount:");

        SelectedOrderField.setEditable(false);

        jLabel5.setText("Received Date:");

        jLabel6.setText("Item ID:");

        ItemIdField.setEditable(false);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel2)
                                    .addComponent(jLabel4))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(SelectedOrderField, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(ReceivedAmountField, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel6)
                                .addGap(18, 18, 18)
                                .addComponent(ItemIdField, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel3)
                            .addComponent(jLabel5))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(NewStockReceivedField, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ConfirmButton)
                            .addComponent(ReceivedDateField, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(Back, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 753, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(24, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(Back)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 296, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(NewStockReceivedField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(SelectedOrderField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addGap(7, 7, 7)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(ReceivedDateField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(ItemIdField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel6)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ConfirmButton)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(ReceivedAmountField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel4)))
                .addGap(12, 12, 12))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    //Perform stock update after confirm button clicked
    private void ConfirmButtonActionPerformed(java.awt.event.ActionEvent evt) {    
        //Get the selected row purchase order data                                          
        int selectedRow = PurchaseOrderTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a purchase order.");
            return;
        }

        String orderId = SelectedOrderField.getText();
        String itemId = PurchaseOrderTable.getValueAt(selectedRow, 1).toString();
        //Get user ID of the Inventory Manager that perform the action
        String userId = loggedInUser.getUserId();
        
        //Validate the date format entered
        String receivedDate = ReceivedDateField.getText().trim();
        if (!receivedDate.matches("\\d{4}-\\d{2}-\\d{2}")) {
            JOptionPane.showMessageDialog(this, "Invalid date format. Please use YYYY-MM-DD.");
            return;
        }

        int quantityOrdered = Integer.parseInt(PurchaseOrderTable.getValueAt(selectedRow, 3).toString());
        
        String inputStr = NewStockReceivedField.getText();
        if (inputStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter the new stock received.");
            return;
        }
        //Convert the number from string type to integer
        int newReceived = Integer.parseInt(inputStr);
        if (newReceived <= 0) {
            JOptionPane.showMessageDialog(this, "Received amount must be integer and positive.");
            return;
            }

        int previouslyReceived = Integer.parseInt(ReceivedAmountField.getText().trim());
        //Calculate the total amount of stock received
        int combinedTotal = previouslyReceived + newReceived;

        // Check if combined received amount exceeds order quantity
        if (combinedTotal > quantityOrdered) {
            int option = JOptionPane.showConfirmDialog(this,
                    "The total received (" + combinedTotal + ") exceeds the ordered quantity (" + quantityOrdered + ").\n" +
                    "Are you sure you want to confirm this stock update?",
                    "Confirm Over-Receiving",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);
            //Not continue to update stock if select NO
            if (option != JOptionPane.YES_OPTION) {
                return;
            }
        }
        
        //Confirmation to update stock
        int confirm = JOptionPane.showConfirmDialog(this,
            "Confirm stock update for Order ID " + orderId + "?",
            "Confirmation", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            //Call the update stock method at backend with required variables
            boolean success = updater.updateStock(orderId, itemId, newReceived, quantityOrdered, receivedDate, userId);
            //Refresh the data display on the table if the stock updated successfully
            if (success) {
                JOptionPane.showMessageDialog(this, "Stock updated successfully.");
                populateTable();
                SelectedOrderField.setText("");
                ReceivedAmountField.setText("");
                NewStockReceivedField.setText("");
                } else {
                    JOptionPane.showMessageDialog(this, "Update failed. Check logs.");
                    }
            }   
    }

    //Detect the purchase order selected by Inventory Manager
    private void TableRowSelected(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_TableRowSelected
        ReceivedDateField.setText(LocalDate.now().toString());
        int row = PurchaseOrderTable.getSelectedRow();
        if (row >= 0) {
            String orderId = PurchaseOrderTable.getValueAt(row, 0).toString();
            SelectedOrderField.setText(orderId);
            String itemId = PurchaseOrderTable.getValueAt(row, 1).toString();
            ItemIdField.setText(itemId);
            int received = updater.getTotalReceivedAmount(orderId,itemId);
            ReceivedAmountField.setText(String.valueOf(received));
        }
    }//GEN-LAST:event_TableRowSelected

    public void BackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BackActionPerformed
        //Close current window
        this.dispose();
        //Navigate back to the Main Page of Inventory Manager
        new InventoryManagerWindow(loggedInUser).setVisible(true);
    }//GEN-LAST:event_BackActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton Back;
    private javax.swing.JButton ConfirmButton;
    private javax.swing.JTextField ItemIdField;
    private javax.swing.JTextField NewStockReceivedField;
    private javax.swing.JTable PurchaseOrderTable;
    private javax.swing.JTextField ReceivedAmountField;
    private javax.swing.JTextField ReceivedDateField;
    private javax.swing.JTextField SelectedOrderField;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
}
