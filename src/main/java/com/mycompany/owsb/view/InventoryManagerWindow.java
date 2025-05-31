package com.mycompany.owsb.view;

import com.mycompany.owsb.model.InventoryManager;
import com.mycompany.owsb.model.User;
import com.mycompany.owsb.model.WindowUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class InventoryManagerWindow extends javax.swing.JFrame {
    private final User loggedInUser;

    public InventoryManagerWindow(User loggedInUser) {
        this.loggedInUser = loggedInUser;
        initComponents();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        itemListBtn = new javax.swing.JButton();
        updateStockBtn = new javax.swing.JButton();
        stockReportBtn = new javax.swing.JButton();
        LogOutButton = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(255, 255, 255));

        itemListBtn.setText("Item List");
        itemListBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                itemListBtnActionPerformed(evt);
            }
        });

        updateStockBtn.setText("Update Stock");
        updateStockBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                updateStockBtnActionPerformed(evt);
            }
        });

        stockReportBtn.setText("Stock Report");
        stockReportBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stockReportBtnActionPerformed(evt);
            }
        });

        LogOutButton.setText("Log Out");
        LogOutButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                LogOutButtonActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Segoe UI", 0, 36)); // NOI18N
        jLabel1.setText("Inventory Management");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(41, 41, 41)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(LogOutButton)
                            .addComponent(jLabel1)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(137, 137, 137)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(stockReportBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(updateStockBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(itemListBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(23, 42, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(32, 32, 32)
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addComponent(LogOutButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(itemListBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(updateStockBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(stockReportBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(23, Short.MAX_VALUE))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void itemListBtnActionPerformed(java.awt.event.ActionEvent evt) {
        InventoryManager im = new InventoryManager(loggedInUser);
        //Check if the logged-in user have the permission to view item list or not
        if (im.isAllowedToPerform("ViewItemList")) {
            //Navigate to view item list window if permission granted
            new IM_ViewItemListWindow(loggedInUser).setVisible(true);
            this.setVisible(false);
        } else {
            //Display error message if unauthorized user
            JOptionPane.showMessageDialog(this, "You are not authorized to view item list.");
        }
    }

    private void updateStockBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updateStockBtnActionPerformed
        InventoryManager im = new InventoryManager(loggedInUser);
        //Check if the logged-in user have the permission to update stock or not
        if (im.isAllowedToPerform("UpdateStock")) {
            //Navigate to update stock window if permission granted
            new IM_UpdateStockWindow(loggedInUser).setVisible(true);
            this.setVisible(false);
        } else {
            //Display error message if unauthorized user
            JOptionPane.showMessageDialog(this, "You are not authorized to update stock.");
        }
    }//GEN-LAST:event_updateStockBtnActionPerformed

    private void stockReportBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stockReportBtnActionPerformed
        InventoryManager im = new InventoryManager(loggedInUser);
        //Check if the logged-in user have the permission to view stock report or not
        if (im.isAllowedToPerform("StockReport")) {
            // Options for the report
            String[] options = {"Overall Report", "Stock Movement Report"};

            // Show option dialog
            int choice = JOptionPane.showOptionDialog(
                null,
                "Choose report type:",
                "Stock Report Selection",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                options,
                options[0]
            );

            // Handle option
            if (choice == 0) {
                //Navigate to overall stock report window
                new IM_StockReportWindow1(loggedInUser).setVisible(true);
                this.setVisible(false);
            } else if (choice == 1) {
                //Navigate to stock movement report window
                new IM_StockReportWindow2(loggedInUser).setVisible(true);
                this.setVisible(false);
            }
        } else {
            //Display error message if unauthorized user
            JOptionPane.showMessageDialog(this, "You are not authorized to view stock report.");
        }
    }//GEN-LAST:event_stockReportBtnActionPerformed

    private void LogOutButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_LogOutButtonActionPerformed
        //Use method in windowutil class to log out
        WindowUtil.logoutAndRedirectToLogin(this);
    }//GEN-LAST:event_LogOutButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton LogOutButton;
    private javax.swing.JButton itemListBtn;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JButton stockReportBtn;
    private javax.swing.JButton updateStockBtn;
    // End of variables declaration//GEN-END:variables
}
