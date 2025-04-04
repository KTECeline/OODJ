/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.owsb;

/**
 *
 * @author timi
 */
public class Supplier {
    String supplierID;
    String supplierName;
    String itemId;
    String contact;

    public Supplier(String supplierID, String supplierName, String itemId, String contact) {
        this.supplierID = supplierID;
        this.supplierName = supplierName;
        this.itemId = itemId;
        this.contact = contact;
    }

    @Override
    public String toString() {
        return supplierID + "," + supplierName + "," + itemId + "," + contact;
    }

    public static Supplier fromString(String line) {
        String[] parts = line.split(",");
        return new Supplier(
            parts[0],  // supplierID
            parts[1],  // supplierName
            parts[2],  // itemId
            parts[3]   // contact
        );
    }
}
