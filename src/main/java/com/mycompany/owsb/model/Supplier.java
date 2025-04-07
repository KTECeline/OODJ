/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.owsb.model;

/**
 *
 * @author timi
 */
public class Supplier {
    private String supplierID;
    private String supplierName;
    private String itemId;
    private String email;

    public Supplier(String supplierID, String supplierName, String itemId, String email) {
        this.supplierID = supplierID;
        this.supplierName = supplierName;
        this.itemId = itemId;
        this.email = email;
    }
    
    // Getters
    public String getSupplierID() {
        return supplierID;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public String getItemId() {
        return itemId;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String toString() {
        return supplierID + "," + supplierName + "," + itemId + "," + email;
    }

    public static Supplier fromString(String line) {
        String[] parts = line.split(",");
        return new Supplier(
            parts[0],  // supplierID
            parts[1],  // supplierName
            parts[2],  // itemId
            parts[3]   // email
        );
    }
}
