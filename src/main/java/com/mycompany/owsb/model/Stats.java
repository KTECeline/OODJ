/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.owsb.model;

/**
 *
 * @author leopa
 */
public class Stats {
    public int totalItems;
    public int totalSuppliers;
    public long pendingPRs;
    public int pendingPOs;
    public String username;
    // Constructor
    public Stats(int totalItems, int totalSuppliers, long pendingPRs, int pendingPOs, String username) {
        this.totalItems = totalItems;
        this.totalSuppliers = totalSuppliers;
        this.pendingPRs = pendingPRs;
        this.pendingPOs = pendingPOs;
        this.username = username;
    }

    // Getters
    public int getTotalItems() { return totalItems; }
    public int getTotalSuppliers() { return totalSuppliers; }
    public long getPendingPRs() { return pendingPRs; }
    public int getPendingPOs() { return pendingPOs; }
    public String getUsername() { return username; }
}
