package com.mycompany.owsb.model;

import java.time.LocalDate;

// Finance_PurchaseOrder class represents the initial reviewing of orders with status "PENDING"
public class Finance_PurchaseOrder {
    // PO fields
    private String orderID;
    private String itemID;
    private String supplierID;
    private int quantity;
    private double totalPrice;
    private LocalDate orderDate;
    private String status;  
    private String prID;   
    private String userID;  

    // Constructor for new purchase order
    public Finance_PurchaseOrder(String orderID, String itemID, String supplierID, 
                          int quantity, double totalPrice, LocalDate orderDate,
                          String status, String prID, String userID) {
        this.orderID = orderID;
        this.itemID = itemID;
        this.supplierID = supplierID;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
        this.orderDate = orderDate;
        this.status = status;
        this.prID = prID;
        this.userID = userID;
    }

    // Getters
    public String getOrderID() { return orderID; }
    public String getItemID() { return itemID; }
    public String getSupplierID() { return supplierID; }
    public int getQuantity() { return quantity; }
    public double getTotalPrice() { return totalPrice; }
    public LocalDate getOrderDate() { return orderDate; }
    public String getStatus() { return status; }
    public String getPrID() { return prID; }
    public String getUserID() { return userID; }

    // Setters
    public void setStatus(String status) { 
        if (status.equals("PENDING") || status.equals("APPROVED") || 
            status.equals("REJECTED")) {
            this.status = status; 
        }
    }
    
    @Override
    public String toString() {
        return String.join(",",
            orderID, itemID, supplierID,
            String.valueOf(quantity),
            String.valueOf(totalPrice),
            orderDate.toString(),
            status, prID, userID
        );
    }
}