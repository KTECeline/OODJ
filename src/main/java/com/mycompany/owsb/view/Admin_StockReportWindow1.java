/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.owsb.view;

import com.mycompany.owsb.model.User;
import java.awt.event.ActionEvent;

/**
 * Admin view of Stock Report Window 1 that inherits from the inventory manager's stock report window
 * but overrides the back button functionality.
 * @author yyhao
 */
public class Admin_StockReportWindow1 extends IM_StockReportWindow1 {
    
    private User loggedInUser;
    
    /**
     * Constructor that takes in the logged in user
     * @param loggedInUser The current logged in user
     */
    public Admin_StockReportWindow1(User loggedInUser) {
        super(loggedInUser);  // Call parent constructor with the user
        this.loggedInUser = loggedInUser;
    }
    
    /**
     * Override the back button functionality
     */
    @Override
    public void BackActionPerformed(ActionEvent evt) {
        // Dispose the current window
        dispose();
    }
}