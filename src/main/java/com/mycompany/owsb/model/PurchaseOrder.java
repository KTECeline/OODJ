/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.owsb.model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;


/**
 *
 * @author timi
 */


public class PurchaseOrder {
    private String orderID;
    private String itemID;
    private int quantity;
    private String supplierID;
    private double unitPrice;
    private double totalPrice;
    private String orderDate;
    private String status;
    private String createdBy;
    private String prId;
    
    // String representing the file path for purchase order data
    private static final String PURCHASE_ORDER_FILE = "data/purchase_order.txt";
    
    // Constructor
    public PurchaseOrder(String orderID, String itemID, int quantity, String supplierID, double unitPrice, String orderDate, String status,
                        String prId, String createdBy) {
        
        if (orderID == null || orderID.isEmpty()) {
            throw new IllegalArgumentException("Order ID cannot be null or empty");
        }
        if (itemID == null || itemID.isEmpty()) {
            throw new IllegalArgumentException("Item ID cannot be null or empty");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        if (supplierID == null || supplierID.isEmpty()) {
            throw new IllegalArgumentException("Supplier ID cannot be null or empty");
        }
        if (unitPrice <= 0) {
            throw new IllegalArgumentException("Unit price must be positive");
        }
        if (orderDate == null || orderDate.isEmpty()) {
            throw new IllegalArgumentException("Order date cannot be null or empty");
        }
        if (!isValidStatus(status)) {
            throw new IllegalArgumentException("Invalid status value");
        }
        this.orderID = orderID;
        this.itemID = itemID;
        this.quantity = quantity;
        this.supplierID = supplierID;
        this.unitPrice = unitPrice;
        this.totalPrice = quantity * unitPrice;
        this.orderDate = orderDate;
        this.status = status;
        this.prId= prId;
        this.createdBy=createdBy;
    }
    
    private boolean isValidStatus(String status) {
    return status != null && 
           (status.equals("PENDING") || 
            status.equals("APPROVED") || 
            status.equals("REJECTED") || 
            status.equals("UNFULFILLED") ||
            status.equals("RECEIVED") ||
            status.equals("COMPLETED"));
}
    
    public String getOrderID() {
        return orderID;
    }

    public String getItemID() {
        return itemID;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getSupplierID() {
        return supplierID;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public String getStatus() {
        return status;
    }
    public String getPrId(){
        return prId;
    }
    public String getCreatedBy(){
        return createdBy;
    }
    
    public void setStatus(String status){
        this.status=status;
    }
    public void setQuantity(int quantity){
        this.quantity= quantity;
        
    }
    public void setTotalPrice(double totalPrice){
        this.totalPrice=totalPrice;
    }
    
    public void setSupplierID(String supplierID){
        this.supplierID=supplierID;
    }
    
    // Method to convert a Purchase Order to a String for saving to a file
    @Override
    public String toString() {
        return orderID + "," + itemID + "," + quantity + "," + supplierID + "," + unitPrice + "," + totalPrice + "," + orderDate + "," + status +
                "," + prId + "," + createdBy;
    }

    public static PurchaseOrder fromString(String orderString) {
        String[] orderData = orderString.split(",");

        // Parse the fields from the string array
        String orderID = orderData[0];                       // Purchase Order ID
        String itemID = orderData[1];                        // Item ID
        int quantity = Integer.parseInt(orderData[2]);       // Quantity
        String supplierID = orderData[3];                    // Supplier ID
        double unitPrice = Double.parseDouble(orderData[4]); // Unit Price
        String orderDate = orderData[6];                     // Order Date (as a String)
        String status = orderData[7];                        // Order Status (Pending, Approved, etc.)
        String prId =orderData[8];
        String createdBy = orderData[9];
        // Return a new PurchaseOrder object
        return new PurchaseOrder(orderID, itemID, quantity, supplierID, unitPrice, orderDate, status,prId,createdBy);
    }
    
    public String getFormattedDetails() {
        return "Purchase Order ID: " + orderID + "\n\n" +
               "Item ID: " + itemID + "\n\n" +
               "Quantity: " + quantity + "\n\n" +
               "Supplier ID: " + supplierID + "\n\n" +
               "Unit Price: " + getUnitPrice() + "\n\n" +
               "Total Price: " + getTotalPrice() + "\n\n" +
               "Order Date: " + orderDate + "\n\n" +
               "Status: " + getStatus() + "\n\n"+ 
               "PO Id: " + getPrId()+ "\n\n"+ 
               "Created By: " + getCreatedBy();
               
    }

    public static List<PurchaseOrder> loadPurchaseOrders() {
        List<PurchaseOrder> poList = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(PURCHASE_ORDER_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                PurchaseOrder po = PurchaseOrder.fromString(line);
                poList.add(po);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return poList;
    }

    public static void updatePOListInUI(List<PurchaseOrder> poList, JList<String> targetList, JTextArea detailArea) {
        DefaultListModel<String> listModel = new DefaultListModel<>();
        for (PurchaseOrder po : poList) {
            listModel.addElement(po.getOrderID());
        }
        targetList.setModel(listModel);

        // List click listener
        targetList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String selectedID = targetList.getSelectedValue();
                for (PurchaseOrder po : poList) {
                    if (po.getOrderID().equals(selectedID)) {
                        detailArea.setText(po.getFormattedDetails());
                        break;
                    }
                }
            }
        });
    }
    
    public static void searchAndDisplayPO(JTextField searchField, JTextArea detailsArea, java.util.List<PurchaseOrder> poList) {
        String searchID = searchField.getText().trim().toUpperCase();
        boolean found = false;

        if (poList.isEmpty()) {
            detailsArea.setText("No Purchase Orders loaded.");
            return;
        }

        for (PurchaseOrder po : poList) {
            if (po.getOrderID().equalsIgnoreCase(searchID)) {
                detailsArea.setText(po.getFormattedDetails());
                found = true;
                break;
            }
        }

        if (!found) {
            detailsArea.setText("Purchase Order ID not found.");
        }
    }


    
    public static void saveToFile(List<PurchaseOrder> poList, String filename) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename))) {
            for (PurchaseOrder po : poList) {
                bw.write(po.toString());
                bw.newLine();
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Failed to save purchase orders.");
        }
    }
    
    public static PurchaseOrder findById(String orderId) {
    List<PurchaseOrder> allOrders = loadPurchaseOrders();
    return allOrders.stream()
            .filter(po -> po.getOrderID().equals(orderId))
            .findFirst()
            .orElse(null);
    }
    
    private static int lastOrderId=0;
    public static String generateNewOrderId(){
        if(lastOrderId ==0){
            List<PurchaseOrder> allOrders = loadPurchaseOrders();
            lastOrderId= allOrders.stream()
                    .mapToInt(po ->Integer.parseInt(po.getOrderID().substring(2)))
                    .max()
                    .orElse(0);
        }
        return "PO" + (++lastOrderId);
    }
    
    public static void update(PurchaseOrder updatedPo) {
        List<PurchaseOrder> orders = loadPurchaseOrders();
        boolean found = false;

        // Update the matching purchase order
        for (int i = 0; i < orders.size(); i++) {
            if (orders.get(i).getOrderID().equals(updatedPo.getOrderID())) {
                orders.set(i, updatedPo);
                found = true;
                break;
            }
        }

        if (!found) {
            throw new IllegalArgumentException("Purchase Order not found for update");
        }

        // Rewrite the file with updated data
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("data/purchase_order.txt"))) {
            for (PurchaseOrder po : orders) {
                writer.write(String.format("%s,%s,%s,%d,%.2f,%s,%s,%s,%s",
                        po.getOrderID(),
                        po.getItemID(),
                        po.getSupplierID(),
                        po.getQuantity(),
                        po.getTotalPrice(),
                        po.getOrderDate(),
                        po.getStatus(),
                        po.getPrId(),
                        po.getCreatedBy()));
                writer.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException("Error updating purchase order file: " + e.getMessage());
        }
    }
}
