package com.mycompany.owsb.model;

import java.time.LocalDate;

public class Finance_VerifyInventory {
    private String orderID;
    private String itemID;
    private String supplierID;
    private int quantity;
    private double totalPrice;
    private LocalDate orderDate;
    private String status;  // Should be "RECEIVED" for verification
    private String prID;    // Purchase Requisition ID
    private String userID;  // User who created the PO
    private LocalDate receivedDate;  // When it was marked as received
    private boolean verified; // Whether FM has verified the inventory update

    public Finance_VerifyInventory(String orderID, String itemID, String supplierID, 
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
        this.receivedDate = LocalDate.now(); // Assume received today for verification
        this.verified = false;
    }

    // Constructor with all fields including verification status
    public Finance_VerifyInventory(String orderID, String itemID, String supplierID, 
                                 int quantity, double totalPrice, LocalDate orderDate,
                                 String status, String prID, String userID, 
                                 LocalDate receivedDate, boolean verified) {
        this.orderID = orderID;
        this.itemID = itemID;
        this.supplierID = supplierID;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
        this.orderDate = orderDate;
        this.status = status;
        this.prID = prID;
        this.userID = userID;
        this.receivedDate = receivedDate;
        this.verified = verified;
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
    public LocalDate getReceivedDate() { return receivedDate; }
    public boolean isVerified() { return verified; }

    // Setters
    public void setStatus(String status) { 
        if (status.equals("RECEIVED") || status.equals("VERIFIED") || status.equals("PAID")) {
            this.status = status; 
        }
    }
    
    public void setVerified(boolean verified) { 
        this.verified = verified; 
        if (verified && this.status.equals("RECEIVED")) {
            this.status = "VERIFIED"; // Update status when verified
        }
    }
    
    public void setReceivedDate(LocalDate receivedDate) { 
        this.receivedDate = receivedDate; 
    }

    // Check if this item is ready for verification (status = RECEIVED)
    public boolean isReadyForVerification() {
        return "RECEIVED".equalsIgnoreCase(this.status);
    }

    // Check if this item is ready for payment (status = VERIFIED)
    public boolean isReadyForPayment() {
        return "VERIFIED".equalsIgnoreCase(this.status) && this.verified;
    }
    
    @Override
    public String toString() {
        return String.join(",",
            orderID, itemID, supplierID,
            String.valueOf(quantity),
            String.valueOf(totalPrice),
            orderDate.toString(),
            status, prID, userID,
            receivedDate != null ? receivedDate.toString() : "",
            String.valueOf(verified)
        );
    }

    // Create summary string for display purposes
    public String getDisplaySummary() {
        return String.format("PO: %s | Item: %s | Qty: %d | Supplier: %s | Total: $%.2f", 
                           orderID, itemID, quantity, supplierID, totalPrice);
    }
}