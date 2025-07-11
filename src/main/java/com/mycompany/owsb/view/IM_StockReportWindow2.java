package com.mycompany.owsb.view;

import com.mycompany.owsb.model.IM_StockReport2;
import com.mycompany.owsb.model.Item;
import com.mycompany.owsb.model.User;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.DefaultTableModel;

public class IM_StockReportWindow2 extends javax.swing.JFrame {

    private final User loggedInUser;
    
    public IM_StockReportWindow2(User loggedInUser) {
        this.loggedInUser = loggedInUser;
        initComponents();

        //Handle back end connection
        IM_StockReport2 report = new IM_StockReport2();
        //Display the list of item ID for filter
        ItemSelect.setListData(report.getItemIDs());

        //Listener for list selection to fetch stock movement data
        ItemSelect.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String selectedItemID = ItemSelect.getSelectedValue();
                if (selectedItemID != null) {
                    //If data found, dispaly the purchase order data for the selected item
                    DefaultTableModel model = report.getStockMovementsForItem(selectedItemID);
                    StockMovementTable.setModel(model);
                }
            }
        });
    }


    public void lowStockFilter() {
        //Get all the items data
        List<Item> items = Item.loadItems();
        List<String> filteredItemIDs = new ArrayList<>();

        //Filter items that have low stock level if the checkbox is checked
        if (LowStockCheckBox.isSelected()) {
            for (Item item : items) {
                if ("Low".equalsIgnoreCase(item.getStockLevel())) {
                    filteredItemIDs.add(item.getItemID());
                }
            }
        //Else display all items
        } else {
            for (Item item : items) {
                filteredItemIDs.add(item.getItemID());
            }
        }

        //Refresh the list and clear the table
        String[] itemArray = filteredItemIDs.toArray(new String[0]);
        ItemSelect.setListData(itemArray);
        ((DefaultTableModel) StockMovementTable.getModel()).setRowCount(0);
    }   

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        StockMovementTable = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        ItemSelect = new javax.swing.JList<>();
        Back = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        LowStockCheckBox = new javax.swing.JCheckBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        StockMovementTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Stock Received ID", "Purchase Order ID", "Item ID", "Amount Received", "Date Received", "User ID"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        StockMovementTable.setColumnSelectionAllowed(true);
        StockMovementTable.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(StockMovementTable);
        StockMovementTable.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        jLabel1.setText("Filter:");

        ItemSelect.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jScrollPane2.setViewportView(ItemSelect);

        Back.setBackground(new java.awt.Color(102, 102, 102));
        Back.setFont(new java.awt.Font("Heiti TC", 0, 12)); // NOI18N
        Back.setForeground(new java.awt.Color(255, 255, 255));
        Back.setText("<");
        Back.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BackActionPerformed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel2.setText("Stock Movement Report");

        LowStockCheckBox.setText("Low Stock");
        LowStockCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                LowStockCheckBoxActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(11, Short.MAX_VALUE)
                .addComponent(Back, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(LowStockCheckBox, javax.swing.GroupLayout.Alignment.TRAILING))
                            .addComponent(jLabel1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 617, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(4, 4, 4)
                .addComponent(jLabel2)
                .addGap(16, 16, 16)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(LowStockCheckBox))
                            .addComponent(Back))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 279, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 329, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(19, Short.MAX_VALUE))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    public void BackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BackActionPerformed
        //Close current window
        this.dispose();
        //Navigate back to the main page of inventory manager
        new InventoryManagerWindow(loggedInUser).setVisible(true);
    }//GEN-LAST:event_BackActionPerformed

    //Call the function when the checkbox is updated
    private void LowStockCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_LowStockCheckBoxActionPerformed
        lowStockFilter();
    }//GEN-LAST:event_LowStockCheckBoxActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton Back;
    private javax.swing.JList<String> ItemSelect;
    private javax.swing.JCheckBox LowStockCheckBox;
    private javax.swing.JTable StockMovementTable;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    // End of variables declaration//GEN-END:variables
}
