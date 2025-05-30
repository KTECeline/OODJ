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
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author timi
 */
public class SalesItem {
    private String salesID;
    private String itemID;
    private int quantitySold;
    private double pricePerUnit;
    private double subtotal;  // quantity * price per unit

    private static final String SALES_ITEM_FILE = "data/sales_item.txt";
        
    public SalesItem(String salesID, String itemID, int quantitySold, double pricePerUnit) {
        this.salesID = salesID;
        this.itemID = itemID;
        this.quantitySold = quantitySold;
        this.pricePerUnit = pricePerUnit;
        this.subtotal = quantitySold * pricePerUnit;
    }
    
    // Getters
    public String getSalesID() { 
        return salesID; 
    }
    
    public String getItemID() { 
        return itemID; 
    }
    
    public int getQuantitySold() { 
        return quantitySold; 
    } 
    
    public double getPricePerUnit() { 
        return pricePerUnit; 
    }
    
    public double getSubtotal() { 
        return subtotal; 
    }

    // Setters
    public void setSalesID(String salesID) { 
        this.salesID = salesID; 
    }
    public void setItemID(String itemID) { 
        this.itemID = itemID; 
    }

    
    public void setQuantitySold(int quantitySold) { 
        this.quantitySold = quantitySold; 
    }

    
    public void setPricePerUnit(double pricePerUnit) { 
        this.pricePerUnit = pricePerUnit; 
    }
    
    public void setSubtotal(double subtotal) { 
        this.subtotal = subtotal; 
    }


    
    @Override
    public String toString() {
        return salesID + "," + itemID + "," + quantitySold + "," + pricePerUnit;
    }

    public static SalesItem fromString(String line) {
        String[] parts = line.split(",", -1);
        SalesItem item = new SalesItem(
            parts[0],  // salesID
            parts[1],  // itemID
            Integer.parseInt(parts[2]),  // quantitySold
            Double.parseDouble(parts[3]) // pricePerUnit
        );
        return item;
    }
    
    public static List<SalesItem> loadSalesItems() {
        List<SalesItem> itemList = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(SALES_ITEM_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                SalesItem item = SalesItem.fromString(line);
                itemList.add(item);
            }
        } catch (IOException e) {
            System.err.println("Error loading sales items: " + e.getMessage());
        }

        return itemList;
    }
    
    public static void linkSalesAndItems(List<Sales> salesList, List<SalesItem> itemList) {
        for (Sales sale : salesList) {
            for (SalesItem item : itemList) {
                if (item.getSalesID().equalsIgnoreCase(sale.getSalesID())) {
                    sale.addItem(item);
                }
            }
        }
    }

    // Display the sales items for a specific sales ID inside the table
    public static void displaySalesItemsInTable(String salesID, List<SalesItem> salesItemList, List<Item> itemList, JTable salesItemTable) {
        String[] columns = {"Item ID", "Item Name", "Quantity", "Unit Price", "Subtotal"};
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0);

        for (SalesItem item : salesItemList) {
            if (item.getSalesID().equals(salesID)) {
                // Find the item name by matching item ID
                String itemName = "";
                for (Item i : itemList) {
                    if (i.getItemID().equals(item.getItemID())) {
                        itemName = i.getItemName();
                        break;
                    }
                }
                Object[] row = {
                    item.getItemID(),
                    itemName,
                    item.getQuantitySold(),
                    String.format("%.2f", item.getPricePerUnit()),
                    String.format("%.2f", item.getSubtotal())
                };
                tableModel.addRow(row);
            }
        }

        salesItemTable.setModel(tableModel);
    }



}

