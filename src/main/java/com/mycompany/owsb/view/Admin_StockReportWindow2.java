/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.owsb.view;

import com.mycompany.owsb.model.User;
import java.awt.event.ActionEvent;

/**
 * Admin view of Stock Report Window 2 that inherits from the inventory manager's stock report window
 * but overrides the back button functionality.
 * @author yyhao
 */
public class Admin_StockReportWindow2 extends IM_StockReportWindow2 {
    
    private User loggedInUser;
    
    /**
     * Constructor that takes in the logged in user
     * @param loggedInUser The current logged in user
     */
    public Admin_StockReportWindow2(User loggedInUser) {
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