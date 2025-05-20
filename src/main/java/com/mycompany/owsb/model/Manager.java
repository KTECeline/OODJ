/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.owsb.model;

/**
 *
 * @author timi
 */
public abstract class Manager {
    private final User loggedInUser; // Store the logged-in user
    private String department; // Store the department

    
    // Constructor
    public Manager(User loggedInUser) {
        this.loggedInUser = loggedInUser;
        this.department = loggedInUser.getRole();
    }
    
     // Getters
    public User getLoggedInUser() {
        return loggedInUser;
    }

    public String getDepartment() {
        return department;
    }

    // Abstract method for role-specific authorization
    public abstract boolean isAllowedToPerform(String action);
}