/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.owsb.model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author timi
 */
public class Sales {
    private String salesID;
    private LocalDate date;
    private double totalAmount;
    private String createdBy;
    private String remarks;
    private List<SalesItem> items;
    
    private static final String SALES_FILE = "data/sales.txt";
    

    public Sales(String salesID, LocalDate date, String createdBy, String remarks) {
        this.salesID = salesID;
        this.date = date;
        this.totalAmount = 0;
        this.createdBy = createdBy;
        this.remarks = remarks;
        this.items = new ArrayList<>();
    }

    // Getters
    public List<SalesItem> getItems() {
        return items;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public String getSalesID() {
        return salesID;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getRemarks() {
        return remarks;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    // Setters
    public void setSalesID(String salesID) {
        this.salesID = salesID;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public void setItems(List<SalesItem> items) {
        this.items = items;
        this.totalAmount = 0;
        for (SalesItem item : items) {
            this.totalAmount += item.getQuantitySold() * item.getPricePerUnit();
        }
    }
    
    // add item adn update total amount
    public void addItem(SalesItem item) {
        items.add(item);
        totalAmount += item.getSubtotal();
    }

    // Convert object to string to save it in file
    @Override
    public String toString() {
        return salesID + "," + date + "," + totalAmount + "," + createdBy + "," + remarks;
    }

    
    public static Sales fromString(String line) {
        String[] parts = line.split(",", 5);  // allow empty fields safely

        String salesID = parts[0];
        LocalDate date = LocalDate.parse(parts[1]);  // yyyy-MM-dd
        double totalAmount = Double.parseDouble(parts[2]);
        String createdBy = parts[3];
        String remarks = parts[4];

        Sales sale = new Sales(salesID, date, createdBy, remarks);
        sale.setTotalAmount(totalAmount);

        return sale;
    }


    
    public static List<Sales> loadSales() {
        List<Sales> salesList = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(SALES_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Sales sale = Sales.fromString(line);
                salesList.add(sale);
            }
        } catch (IOException e) {
            System.err.println("Error loading sales: " + e.getMessage());
        }

        return salesList;
    }
    
    // Method to auto GENERATE SUPPLIER ID from the last one
    public static String generateNextSalesID(List<Sales> salesList) {
        int maxNumber = 0;
        for (Sales sales : salesList) {
            String id = sales.getSalesID();
            if (id.startsWith("SA")) {
                try {
                    int num = Integer.parseInt(id.substring(2));
                    if (num > maxNumber) {
                        maxNumber = num;
                    }
                } catch (NumberFormatException ignored) {}
            }
        }
        return String.format("SA%04d", maxNumber + 1);
    }

    
    // Display the sales header details (like date, created by, total) in the text area
    public static void displaySalesDetails(String salesID, List<Sales> salesList, JTextArea salesDetailsArea) {
        for (Sales sale : salesList) {
            if (sale.getSalesID().equals(salesID)) {
                StringBuilder sb = new StringBuilder();
                sb.append("Sales ID: ").append(sale.getSalesID()).append("\n")
                  .append("Date: ").append(sale.getDate()).append("\n")
                  .append("Created By: ").append(sale.getCreatedBy()).append("\n")
                  .append("Remarks: ").append(sale.getRemarks()).append("\n")
                  .append("Total Amount: RM ").append(String.format("%.2f", sale.getTotalAmount())).append("\n");
                salesDetailsArea.setText(sb.toString());
                break;
            }
        }
    }


    // Main method to refresh the sales list, details area, and items table in the UI
    public static void updateSalesUI(List<Sales> salesList, List<SalesItem> salesItemList, List<Item> itemList,
                              JList<String> salesIDList, JTextArea salesDetailsArea, JTable salesItemTable) {

        // Populate the sales ID list on the left
        DefaultListModel<String> listModel = new DefaultListModel<>();
        for (Sales sale : salesList) {
            listModel.addElement(sale.getSalesID());
        }
        salesIDList.setModel(listModel);

        if (!salesList.isEmpty()) {
            // Automatically select the first sale and show its details + items
            salesIDList.setSelectedIndex(0);
            String selectedSalesID = salesList.get(0).getSalesID();

            displaySalesDetails(selectedSalesID, salesList, salesDetailsArea);
            SalesItem.displaySalesItemsInTable(selectedSalesID, salesItemList, itemList, salesItemTable);
        } else {
            // If no sales, clear the area and table
            salesDetailsArea.setText("No sales records available.");
            salesItemTable.setModel(new DefaultTableModel(new String[]{"Item ID", "Item Name", "Quantity", "Unit Price", "Subtotal"}, 0));
        }
    }
    
    public static void searchAndDisplaySales(JTextField searchField, List<Sales> salesList, List<SalesItem> salesItemList, List<Item> itemList,
                              JList<String> salesIDList, JTextArea salesDetailsArea, JTable salesItemTable) {
        
        String searchID = searchField.getText().trim().toUpperCase();

        // Filter sales list
        List<Sales> matchingSales = new ArrayList<>();
        for (Sales sale : salesList) {
            if (sale.getSalesID().equalsIgnoreCase(searchID)) {
                matchingSales.add(sale);
            }
        }

        // Update UI with matching sales
        Sales.updateSalesUI(matchingSales, salesItemList, itemList, salesIDList, salesDetailsArea, salesItemTable);

        if (matchingSales.isEmpty()) {
            salesDetailsArea.setText("No matching sales found.");
            salesItemTable.setModel(new DefaultTableModel(new String[]{"Item ID", "Item Name", "Quantity", "Unit Price", "Subtotal"}, 0));
        }
    }


}
