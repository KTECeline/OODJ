/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.owsb.model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

/**
 *
 * @author timi
 */
public class Supplier {
    private String supplierID;
    private String supplierName;
    private String itemId;
    private String email;
    
    // String representing the file path for item data
    private static final String SUPPLIER_FILE = "data/suppliers.txt";

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

    // Convert object to string to save it in file
    @Override
    public String toString() {
        return supplierID + "," + supplierName + "," + itemId + "," + email;
    }

    // Convert the line in the file from String to object
    public static Supplier fromString(String line) {
        String[] parts = line.split(",");
        return new Supplier(
            parts[0],  // supplierID
            parts[1],  // supplierName
            parts[2],  // itemId
            parts[3]   // email
        );
    }
    
    // Method to load Items from a file
    public static List<Supplier> loadSuppliers() {
        List<Supplier> supplierList = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(SUPPLIER_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Supplier supplier = Supplier.fromString(line);
                supplierList.add(supplier);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error loading suppliers: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        return supplierList;
    }
    
}
