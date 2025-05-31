package com.mycompany.owsb.model;

import java.time.LocalDate;
import java.util.List;

public class Finance_Payment {
    private String paymentID;      // pyID - Primary key
    private String orderID;        // Reference to Purchase Order
    private String supplierID;     // Supplier being paid
    private double totalAmount;    // Payment amount
    private LocalDate paymentDate; // pyDate - When payment was made
    private String paymentMethod;  // pyMethod - How payment was made
    private String status;         // Payment status (PENDING, COMPLETED, FAILED)

    // Constructor for new payment
    public Finance_Payment(String paymentID, String orderID, String supplierID, 
                          double totalAmount, String paymentMethod) {
        this.paymentID = paymentID;
        this.orderID = orderID;
        this.supplierID = supplierID;
        this.totalAmount = totalAmount;
        this.paymentDate = LocalDate.now();
        this.paymentMethod = paymentMethod;
        this.status = "PENDING";
    }

    // Constructor with all fields (for loading from file)
    public Finance_Payment(String paymentID, String orderID, String supplierID, 
                          double totalAmount, LocalDate paymentDate, String paymentMethod, 
                          String status) {
        this.paymentID = paymentID;
        this.orderID = orderID;
        this.supplierID = supplierID;
        this.totalAmount = totalAmount;
        this.paymentDate = paymentDate;
        this.paymentMethod = paymentMethod;
        this.status = status;
    }

    // Getters
    public String getPaymentID() { return paymentID; }
    public String getOrderID() { return orderID; }
    public String getSupplierID() { return supplierID; }
    public double getTotalAmount() { return totalAmount; }
    public LocalDate getPaymentDate() { return paymentDate; }
    public String getPaymentMethod() { return paymentMethod; }
    public String getStatus() { return status; }

    // Setters
    public void setStatus(String status) { 
        if (status.equals("PENDING") || status.equals("COMPLETED") || status.equals("FAILED")) {
            this.status = status; 
        }
    }
    
    public void setPaymentDate(LocalDate paymentDate) { 
        this.paymentDate = paymentDate; 
    }

    // Complete the payment
    public void completePayment() {
        this.status = "COMPLETED";
        this.paymentDate = LocalDate.now();
    }

    // Check if payment is completed
    public boolean isCompleted() {
        return "COMPLETED".equalsIgnoreCase(this.status);
    }

    @Override
    public String toString() {
        return String.join(",",
            paymentID, orderID, supplierID,
            String.valueOf(totalAmount),
            paymentDate.toString(),
            paymentMethod, status
        );
    }

    // Create display summary for UI
    public String getDisplaySummary() {
        return String.format("Payment: %s | PO: %s | Supplier: %s | Amount: $%.2f | Status: %s", 
                           paymentID, orderID, supplierID, totalAmount, status);
    }

    // Generate next payment ID
    public static String generateNextPaymentID(List<Finance_Payment> existingPayments) {
        int maxId = 0;
        for (Finance_Payment payment : existingPayments) {
            String id = payment.getPaymentID();
            if (id.startsWith("PY")) {
                try {
                    int num = Integer.parseInt(id.substring(2));
                    maxId = Math.max(maxId, num);
                } catch (NumberFormatException e) {
                    // Skip invalid IDs
                }
            }
        }
        return String.format("PY%04d", maxId + 1);
    }
}