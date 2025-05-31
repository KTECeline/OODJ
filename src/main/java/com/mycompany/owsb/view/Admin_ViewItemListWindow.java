/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.owsb.view;

import com.mycompany.owsb.model.User;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.SwingUtilities;

/**
 * Admin view of item list window that inherits from the inventory manager's item list window
 * but overrides the back button functionality.
 * @author yyhao
 */
public class Admin_ViewItemListWindow extends IM_ViewItemListWindow {
    
    private User loggedInUser;
    
    /**
     * Constructor that takes in the logged in user
     * @param loggedInUser The current logged in user
     */
    public Admin_ViewItemListWindow(User loggedInUser) {
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