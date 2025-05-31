/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.mycompany.owsb.view;

import com.mycompany.owsb.model.Item;
import com.mycompany.owsb.model.PurchaseOrder;
import com.mycompany.owsb.model.PurchaseRequisition;
import com.mycompany.owsb.model.SalesManager;
import com.mycompany.owsb.model.User;
import com.mycompany.owsb.model.WindowUtil;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

/**
 *
 * @author timi
 */
public class SalesManagerWindow extends javax.swing.JFrame {
    // Store the currently logged-in user
    private final User loggedInUser;

    // Instance of SalesManager to call sales-related methods
    private final SalesManager salesManager;

    // List to hold all loaded purchase orders
    private List<PurchaseOrder> purchaseOrderList;
    private List<PurchaseRequisition> allPRs;
    private List<Item> itemList;


    /**
     * Creates new form SalesManagerWindow
     * @param loggedInUser
     * @param salesManager
     */
    public SalesManagerWindow(User loggedInUser, SalesManager salesManager) {
        this.loggedInUser = loggedInUser;
        this.salesManager = salesManager;
        initComponents();
        setBackgroundImage();
        
        String username = loggedInUser.getUsername();
        loggedInUsernameLabel.setText("Welcome, " + username);
        
        // Check and update low stock button
        updateLowStockAlertButton();
    }
    
    // Method to show the UserWindow and ensure the user list is updated
    public void showSmWindow() {
        loadPOsIntoList();
        setVisible(true);  // Show the window
    }
    
    private void setBackgroundImage() {
        // Load the image
        ImageIcon icon = new ImageIcon(getClass().getResource("/background.jpg"));
        Image image = icon.getImage();

        // Create a custom panel to hold the background image
        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(image, 0, 0, getWidth(), getHeight(), this);
            }
        };

        // Set the layout to null for absolute positioning
        backgroundPanel.setLayout(null);

        // Set the size of the background panel
        backgroundPanel.setPreferredSize(new Dimension(730, 500));
        
        backgroundPanel.setBackground(new Color(155,237,255));

        // Set the custom panel as the content pane
        setContentPane(backgroundPanel);

        // Re-add existing components to the background panel
        backgroundPanel.add(loggedInUsernameLabel);
        backgroundPanel.add(usernamePanel);
        backgroundPanel.add(logOutButton);
        backgroundPanel.add(lowStockAlertButton);
        backgroundPanel.add(ManageSuppliersButton);
        backgroundPanel.add(ManageSalesButton);
        backgroundPanel.add(ManagePRButton);
        backgroundPanel.add(ManageItemsButton); 
        backgroundPanel.add(backButton);
        backgroundPanel.add(searchField);
        backgroundPanel.add(searchButton);
        backgroundPanel.add(FilterPO);
        backgroundPanel.add(poTable);
        backgroundPanel.add(jScrollPane1);
        
        // Add JTable instances to JScrollPane components
        jScrollPane1.setViewportView(poTable);

        pack(); // Adjusts frame size based on the preferred size of the content pane
        revalidate();
        repaint();
    }
    
    // Method to load Purchase Orders from file and display them in the UI list
    private void loadPOsIntoList() {
        // Load the list of Purchase Orders from the purchase order file
        purchaseOrderList = PurchaseOrder.loadPurchaseOrders();
        allPRs = PurchaseRequisition.loadPurchaseRequisition();
        // Update the JList and details area in the UI with the loaded Purchase Orders
        PurchaseOrder.updatePOTableInUI(purchaseOrderList, allPRs, poTable);
    }
    
    private void updateLowStockAlertButton() {
        itemList = Item.loadItems();
        
        int lowStockCount = 0;
        for (Item item : itemList) {
            if (Item.isLowStock(item.getStock())) {
                lowStockCount++;
            }
        }

        if (lowStockCount > 0) {
            lowStockAlertButton.setText("(" + lowStockCount + ")" + " Low Stock Alert !");
            lowStockAlertButton.setVisible(true);
        } else {
            lowStockAlertButton.setVisible(false);
        }
    }
    
     
     public void filterPOTableByStatus() {
    String selectedStatus = FilterPO.getSelectedItem().toString();
    List<PurchaseOrder> allPOs = PurchaseOrder.loadPurchaseOrders();
    List<PurchaseRequisition> allPRs = PurchaseRequisition.loadPurchaseRequisition();

    List<PurchaseOrder> filteredPOs = new ArrayList<>();

    if (selectedStatus.equalsIgnoreCase("ALL")) {
        filteredPOs.addAll(allPOs);
    } else {
        for (PurchaseOrder po : allPOs) {
            if (po.getStatus().equalsIgnoreCase(selectedStatus)) {
                filteredPOs.add(po);
            }
        }
    }

    PurchaseOrder.updatePOTableInUI(filteredPOs, allPRs, poTable);
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
        ManageSalesButton = new javax.swing.JButton();
        ManagePRButton = new javax.swing.JButton();
        searchField = new javax.swing.JTextField();
        searchButton = new javax.swing.JButton();
        logOutButton = new javax.swing.JButton();
        lowStockAlertButton = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        poTable = new javax.swing.JTable();
        FilterPO = new javax.swing.JComboBox<>();
        backButton = new javax.swing.JButton();
        usernamePanel = new javax.swing.JPanel();
        loggedInUsernameLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Sales Manager");

        ManageItemsButton.setBackground(new java.awt.Color(189, 131, 248));
        ManageItemsButton.setFont(new java.awt.Font("Heiti TC", 0, 12)); // NOI18N
        ManageItemsButton.setText("Manage Items");
        ManageItemsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ManageItemsButtonActionPerformed(evt);
            }
        });

        ManageSuppliersButton.setBackground(new java.awt.Color(214, 174, 255));
        ManageSuppliersButton.setFont(new java.awt.Font("Heiti TC", 0, 12)); // NOI18N
        ManageSuppliersButton.setText("Manage Suppliers");
        ManageSuppliersButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ManageSuppliersButtonActionPerformed(evt);
            }
        });

        ManageSalesButton.setBackground(new java.awt.Color(186, 152, 255));
        ManageSalesButton.setFont(new java.awt.Font("Heiti TC", 0, 12)); // NOI18N
        ManageSalesButton.setText("Manage Sales");
        ManageSalesButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ManageSalesButtonActionPerformed(evt);
            }
        });

        ManagePRButton.setBackground(new java.awt.Color(176, 137, 255));
        ManagePRButton.setFont(new java.awt.Font("Heiti TC", 0, 12)); // NOI18N
        ManagePRButton.setText("Manage Purchase Requisition");
        ManagePRButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ManagePRButtonActionPerformed(evt);
            }
        });

        searchField.setFont(new java.awt.Font("Heiti TC", 0, 12)); // NOI18N
        searchField.setForeground(new java.awt.Color(51, 51, 51));
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

        logOutButton.setFont(new java.awt.Font("Heiti TC", 0, 12)); // NOI18N
        logOutButton.setForeground(new java.awt.Color(51, 51, 51));
        logOutButton.setText("Log Out");
        logOutButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                logOutButtonActionPerformed(evt);
            }
        });

        lowStockAlertButton.setBackground(new java.awt.Color(255, 51, 0));
        lowStockAlertButton.setFont(new java.awt.Font("Heiti TC", 0, 12)); // NOI18N
        lowStockAlertButton.setForeground(new java.awt.Color(255, 255, 255));
        lowStockAlertButton.setText("Low Stock Alert!");
        lowStockAlertButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lowStockAlertButtonActionPerformed(evt);
            }
        });

        poTable.setFont(new java.awt.Font("Heiti TC", 0, 12)); // NOI18N
        poTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        poTable.setIntercellSpacing(new java.awt.Dimension(0, 2));
        poTable.setSelectionBackground(new java.awt.Color(102, 102, 102));
        poTable.setSelectionForeground(new java.awt.Color(255, 255, 255));
        jScrollPane1.setViewportView(poTable);

        FilterPO.setFont(new java.awt.Font("Heiti TC", 0, 12)); // NOI18N
        FilterPO.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "ALL", "PENDING", "APPROVED", "REJECTED", "RECEIVED", "UNFULFILLED", "COMPLETED" }));
        FilterPO.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                FilterPOActionPerformed(evt);
            }
        });

        backButton.setBackground(new java.awt.Color(102, 102, 102));
        backButton.setFont(new java.awt.Font("Heiti TC", 0, 12)); // NOI18N
        backButton.setForeground(new java.awt.Color(255, 255, 255));
        backButton.setText("<");
        backButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                backButtonActionPerformed(evt);
            }
        });

        usernamePanel.setBackground(new java.awt.Color(255, 255, 255));

        loggedInUsernameLabel.setFont(new java.awt.Font("Heiti TC", 0, 12)); // NOI18N
        loggedInUsernameLabel.setForeground(new java.awt.Color(51, 51, 51));
        loggedInUsernameLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        loggedInUsernameLabel.setText("Welcome");

        javax.swing.GroupLayout usernamePanelLayout = new javax.swing.GroupLayout(usernamePanel);
        usernamePanel.setLayout(usernamePanelLayout);
        usernamePanelLayout.setHorizontalGroup(
            usernamePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, usernamePanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(loggedInUsernameLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(291, 291, 291))
        );
        usernamePanelLayout.setVerticalGroup(
            usernamePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(usernamePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(loggedInUsernameLabel)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(usernamePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGap(33, 33, 33)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 655, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(logOutButton)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lowStockAlertButton, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(ManageSuppliersButton, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(ManageSalesButton, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(ManagePRButton))
                                .addGroup(layout.createSequentialGroup()
                                    .addGap(2, 2, 2)
                                    .addComponent(backButton, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(searchField, javax.swing.GroupLayout.PREFERRED_SIZE, 253, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(searchButton, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addGroup(layout.createSequentialGroup()
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(ManageItemsButton, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(FilterPO, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                .addContainerGap(43, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(usernamePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(logOutButton)
                    .addComponent(lowStockAlertButton, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(27, 27, 27)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ManageSalesButton, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ManagePRButton, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ManageSuppliersButton, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ManageItemsButton, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(searchField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(searchButton)
                    .addComponent(FilterPO, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(backButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 265, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(27, 27, 27))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void ManageItemsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ManageItemsButtonActionPerformed
        SmManageItemsWindow manageItemsWindow = new SmManageItemsWindow(this, salesManager);
        manageItemsWindow.setVisible(true);
        this.setVisible(false); // Hide current window
    }//GEN-LAST:event_ManageItemsButtonActionPerformed

    private void ManageSuppliersButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ManageSuppliersButtonActionPerformed
        SmManageSuppliersWindow manageSuppliersWindow = new SmManageSuppliersWindow(this, salesManager);
        manageSuppliersWindow.setVisible(true);
        this.setVisible(false); // Hide current window
    }//GEN-LAST:event_ManageSuppliersButtonActionPerformed

    private void ManagePRButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ManagePRButtonActionPerformed
        SmManagePrWindow createPrWindow = new SmManagePrWindow(this, salesManager);
        createPrWindow.setVisible(true);
        this.setVisible(false); // Hide current window
    }//GEN-LAST:event_ManagePRButtonActionPerformed

    private void searchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchButtonActionPerformed
        //PurchaseOrder.searchAndDisplayPO(searchField, poDetails, purchaseOrderList);
        PurchaseOrder.searchAndDisplayPO(searchField, poTable, purchaseOrderList, allPRs);
    }//GEN-LAST:event_searchButtonActionPerformed

    private void searchFieldMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_searchFieldMouseClicked
        searchField.setText("");
    }//GEN-LAST:event_searchFieldMouseClicked

    private void ManageSalesButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ManageSalesButtonActionPerformed
        SmManageDailySalesWindow manageDailySalesWindow = new SmManageDailySalesWindow(this, salesManager);
        manageDailySalesWindow.setVisible(true);
        this.setVisible(false); // Hide current window
    }//GEN-LAST:event_ManageSalesButtonActionPerformed

    private void logOutButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_logOutButtonActionPerformed
        WindowUtil.logoutAndRedirectToLogin(this);
    }//GEN-LAST:event_logOutButtonActionPerformed

    private void lowStockAlertButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lowStockAlertButtonActionPerformed
        SmManageItemsWindow manageItemsWindow = new SmManageItemsWindow(this, salesManager);
        manageItemsWindow.setLowStockFilterChecked(true);  // set the checkbox to checked
        manageItemsWindow.setVisible(true);
        this.setVisible(false); // Hide current window
    }//GEN-LAST:event_lowStockAlertButtonActionPerformed

    private void FilterPOActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_FilterPOActionPerformed
        filterPOTableByStatus();
    }//GEN-LAST:event_FilterPOActionPerformed

    private void backButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_backButtonActionPerformed
        // Update JTable to the latest
        PurchaseOrder.updatePOTableInUI(purchaseOrderList, allPRs, poTable);
    }//GEN-LAST:event_backButtonActionPerformed

    /**
     * @param args the command line arguments
     */

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> FilterPO;
    private javax.swing.JButton ManageItemsButton;
    private javax.swing.JButton ManagePRButton;
    private javax.swing.JButton ManageSalesButton;
    private javax.swing.JButton ManageSuppliersButton;
    private javax.swing.JButton backButton;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton logOutButton;
    private javax.swing.JLabel loggedInUsernameLabel;
    private javax.swing.JButton lowStockAlertButton;
    private javax.swing.JTable poTable;
    private javax.swing.JButton searchButton;
    private javax.swing.JTextField searchField;
    private javax.swing.JPanel usernamePanel;
    // End of variables declaration//GEN-END:variables
}
