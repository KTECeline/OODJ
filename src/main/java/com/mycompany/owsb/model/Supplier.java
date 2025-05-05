/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.owsb.model;

import java.awt.Component;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

/**
 *
 * @author timi
 */
public class Supplier {
    private String supplierID;
    private String supplierName;
    private String email;
    
    // String representing the file path for item data
    private static final String SUPPLIER_FILE = "data/suppliers.txt";

    public Supplier(String supplierID, String supplierName, String email) {
        this.supplierID = supplierID;
        this.supplierName = supplierName;
        this.email = email;
    }
    
    // Getters
    public String getSupplierID() {
        return supplierID;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public String getEmail() {
        return email;
    }

    
    // Setters
    public void setSupplierID(String supplierID) {
        this.supplierID = supplierID;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    
    // Convert object to string to save it in file
    @Override
    public String toString() {
        return supplierID + "," + supplierName + "," + email;
    }
   

    // Convert the line in the file from String to object
    public static Supplier fromString(String line) {
        String[] parts = line.split(",");
        return new Supplier(
            parts[0],  // supplierID
            parts[1],  // supplierName
            parts[2]  // email
        );
    }
    
    
    // Method to update supplier table in the UI
    public static void updateSupplierTableInUI(List<Supplier> supplierList, JTable targetTable) {
        String[] columnNames = {"Supplier ID", "Supplier Name", "Email"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);

        for (Supplier supplier : supplierList) {
            Object[] row = {
                supplier.getSupplierID(),
                supplier.getSupplierName(),
                supplier.getEmail()
            };
            tableModel.addRow(row);
        }

        targetTable.setModel(tableModel);
        autoResizeColumnWidths(targetTable);
        
    }


    public static void searchAndDisplaySupplierInTable(JTextField searchField, JTable table, List<Supplier> supplierList) {
        String searchID = searchField.getText().trim().toUpperCase();
        boolean found = false;

        if (supplierList.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No suppliers loaded.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0); // Clear previous table data

        for (Supplier supplier : supplierList) {
            if (supplier.getSupplierID().equalsIgnoreCase(searchID)) {
                Object[] row = {
                    supplier.getSupplierID(),
                    supplier.getSupplierName(),
                    supplier.getEmail()
                };
                model.addRow(row); // Add matched supplier to the table
                found = true;
                
                searchField.setText(""); // Reset Search Field
                break;
            }
        }

        if (!found) {
            JOptionPane.showMessageDialog(null, "Supplier ID not found.", "Not Found", JOptionPane.INFORMATION_MESSAGE);
            
            // Reload full supplier list into table
            updateSupplierTableInUI(supplierList, table);
        }
        // Reset Search Field
        searchField.setText("Enter Supplier ID");
    }
    
    
    public static void autoResizeColumnWidths(JTable table) {
        final TableColumnModel columnModel = table.getColumnModel();
        for (int column = 0; column < table.getColumnCount(); column++) {
            int width = 50; // Minimum width
            for (int row = 0; row < table.getRowCount(); row++) {
                TableCellRenderer renderer = table.getCellRenderer(row, column);
                Component comp = table.prepareRenderer(renderer, row, column);
                width = Math.max(comp.getPreferredSize().width + 10, width);
            }
            columnModel.getColumn(column).setPreferredWidth(width);
        }
    }


    // Method to save Suppliers to a file
    public static void saveToFile(List<Supplier> supplierList) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(SUPPLIER_FILE))) {
            for (Supplier supplier : supplierList) {
                writer.write(supplier.toString());
                writer.newLine();
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Failed to save suppliers.");
        }
    }
    
     public static List<Supplier> loadSuppliers() {
        List<Supplier> supplierList = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(SUPPLIER_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Supplier supplier = Supplier.fromString(line);
                supplierList.add(supplier);
            }
        } catch (IOException e) {}
        return supplierList;
    }
     
    public static Supplier findById(String supplierId) {
    List<Supplier> allSuppliers = loadSuppliers();
    return allSuppliers.stream()
            .filter(supplier -> supplier.getSupplierID().equals(supplierId))
            .findFirst()
            .orElse(null);
    }
    
    
}
