/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.owsb.model;

import static com.mycompany.owsb.model.PurchaseOrder.loadPurchaseOrders;
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
public class Item {
    private String itemID;
    private String itemName;
    private String supplierId;
    private int stock;
    private double cost;
    private double price;
    
    // String representing the file path for item data
    private static final String ITEM_FILE = "data/items.txt";

    public Item(String itemID, String itemName, String supplierId, int stock, double cost, double price) {
        this.itemID = itemID;
        this.itemName = itemName;
        this.supplierId = supplierId;
        this.stock = stock;
        this.cost = cost;
        this.price = price;
    }
    
    //Getter
    public String getItemID() {
        return itemID;
    }
    public String getItemName() {
        return itemName;
    }
    public String getSupplierId() {
        return supplierId;
    }
    public int getStock() {
        return stock;
    }
    public double getCost() {
        return cost;
    }
    public double getPrice() {
        return price;
    }
    
    
    //Setter
    public void setItemName(String itemName) { 
        this.itemName = itemName; 
    }
    public void setSupplierId(String supplierId) { 
        this.supplierId = supplierId; 
    }
    public void setStock(int stock) { 
        this.stock = stock; 
    }
    public void setCost(double cost) { 
        this.cost = cost; 
    }
    public void setPrice(double price) { 
        this.price = price; 
    }
    
    // Convert object to string to save it in file
    @Override
    public String toString() {
        return itemID + "," + itemName + "," + supplierId + "," + stock + "," + cost + "," + price;
    }
    
    // Convert the line in the file from String to object
    public static Item fromString(String line) {
        String[] parts = line.split(",");  // Split the line by commas
        String itemID = parts[0];
        String itemName = parts[1];
        String supplierId = parts[2];
        int stock = Integer.parseInt(parts[3]);
        double cost = Double.parseDouble(parts[4]);
        double price = Double.parseDouble(parts[5]);
        
        return new Item(itemID, itemName, supplierId, stock, cost, price);  // Return a new Item object
    }
    

    // Method to load Items from a file
    public static List<Item> loadItems() {
        List<Item> itemList = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(ITEM_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Item item = Item.fromString(line);
                itemList.add(item);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return itemList;
    }

    
    // Method to update item table in the UI
    public static void updateItemTableInUI(List<Item> itemList, JTable targetTable) {
        String[] columnNames = {"Item ID", "Name", "Supplier ID", "Stock", "Cost (RM)", "Price (RM)"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);

        for (Item item : itemList) {
            Object[] row = {
                item.getItemID(),
                item.getItemName(),
                item.getSupplierId(),
                item.getStock(),
                item.getCost(),
                item.getPrice()
            };
            tableModel.addRow(row);
        }

        targetTable.setModel(tableModel);
        autoResizeColumnWidths(targetTable);
        
    }


    public static void searchAndDisplayItemInTable(JTextField searchField, JTable table, List<Item> itemList) {
        String searchID = searchField.getText().trim().toUpperCase();
        boolean found = false;

        if (itemList.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No items loaded.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0); // Clear previous table data

        for (Item item : itemList) {
            if (item.getItemID().equalsIgnoreCase(searchID)) {
                Object[] row = {
                    item.getItemID(),
                    item.getItemName(),
                    item.getSupplierId(),
                    item.getStock(),
                    item.getCost(),
                    item.getPrice()
                };
                model.addRow(row); // Add matched item to the table
                found = true;
                
                searchField.setText(""); // Reset Search Field
                break;
            }
        }

        if (!found) {
            JOptionPane.showMessageDialog(null, "Item ID not found.", "Not Found", JOptionPane.INFORMATION_MESSAGE);
            
            // Reload full item list into table
            updateItemTableInUI(itemList, table);
        }
        // Reset Search Field
        searchField.setText("Enter Item ID");
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




    // Method to save Items to a file
    public static void saveToFile(List<Item> itemList) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ITEM_FILE))) {
            for (Item item : itemList) {
                writer.write(item.toString());
                writer.newLine();
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Failed to save items.");
        }
    }
    
     public static List<Item> loadItem() {
        List<Item> itemList = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(ITEM_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Item item = Item.fromString(line);
                itemList.add(item);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return itemList;
    }
     
    public static Item findById(String itemId) {
    List<Item> allOrders = loadItem();
    return allOrders.stream()
            .filter(item -> item.getItemID().equals(itemId))
            .findFirst()
            .orElse(null);
    

}}

