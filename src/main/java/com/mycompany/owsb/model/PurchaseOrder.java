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
    

    
    // Constructor
    public PurchaseOrder(String orderID, String itemID, int quantity, String supplierID, double unitPrice, String orderDate, String status) {
        this.orderID = orderID;
        this.itemID = itemID;
        this.quantity = quantity;
        this.supplierID = supplierID;
        this.unitPrice = unitPrice;
        this.totalPrice = quantity * unitPrice;
        this.orderDate = orderDate;
        this.status = status;
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

    
    // Method to convert a Purchase Order to a String for saving to a file
    public String toString() {
        return orderID + "," + itemID + "," + quantity + "," + supplierID + "," + unitPrice + "," + totalPrice + "," + orderDate + "," + status;
    }

    public static PurchaseOrder fromString(String orderString) {
        String[] orderData = orderString.split(",");

        // Parse the fields from the string array
        String orderID = orderData[0];         // Purchase Order ID
        String itemID = orderData[1];                  // Item ID
        int quantity = Integer.parseInt(orderData[2]); // Quantity
        String supplierID = orderData[3];              // Supplier ID
        double unitPrice = Double.parseDouble(orderData[4]); // Unit Price
        String orderDate = orderData[6];               // Order Date (as a String)
        String status = orderData[7];                  // Order Status (Pending, Approved, etc.)

        // Return a new PurchaseOrder object
        return new PurchaseOrder(orderID, itemID, quantity, supplierID, unitPrice, orderDate, status);
    }
    
    public String getFormattedDetails() {
        return "Purchase Order ID: " + orderID + "\n\n" +
               "Item ID: " + itemID + "\n\n" +
               "Quantity: " + quantity + "\n\n" +
               "Supplier ID: " + supplierID + "\n\n" +
               "Unit Price: " + getUnitPrice() + "\n\n" +
               "Total Price: " + getTotalPrice() + "\n\n" +
               "Order Date: " + orderDate + "\n\n" +
               "Status: " + getStatus();
    }

    public static List<PurchaseOrder> loadFromFile(String filePath) {
        List<PurchaseOrder> poList = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
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

}
