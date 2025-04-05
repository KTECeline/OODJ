/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.owsb;

import java.util.Date;

/**
 *
 * @author timi
 */


public class PurchaseOrder {
    String purchaseOrderID;
    String itemID;
    int quantity;
    String supplierID;
    double unitPrice;
    double totalPrice;
    String orderDate;
    String status;

    // Constructor
    public PurchaseOrder(String purchaseOrderID, String itemID, int quantity, String supplierID, double unitPrice, String orderDate, String status) {
        this.purchaseOrderID = purchaseOrderID;
        this.itemID = itemID;
        this.quantity = quantity;
        this.supplierID = supplierID;
        this.unitPrice = unitPrice;
        this.totalPrice = quantity * unitPrice;
        this.orderDate = orderDate;
        this.status = status;
    }
    
    // Method to convert a Purchase Order to a String for saving to a file
    public String toString() {
        return purchaseOrderID + "," + itemID + "," + quantity + "," + supplierID + "," + unitPrice + "," + totalPrice + "," + orderDate + "," + status;
    }

    public static PurchaseOrder fromString(String orderString) {
        String[] orderData = orderString.split(",");

        // Parse the fields from the string array
        String purchaseOrderID = orderData[0];         // Purchase Order ID
        String itemID = orderData[1];                  // Item ID
        int quantity = Integer.parseInt(orderData[2]); // Quantity
        String supplierID = orderData[3];              // Supplier ID
        double unitPrice = Double.parseDouble(orderData[4]); // Unit Price
        String orderDate = orderData[6];               // Order Date (as a String)
        String status = orderData[7];                  // Order Status (Pending, Approved, etc.)

        // Return a new PurchaseOrder object
        return new PurchaseOrder(purchaseOrderID, itemID, quantity, supplierID, unitPrice, orderDate, status);
    }
}
