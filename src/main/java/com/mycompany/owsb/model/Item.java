/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.owsb.model;

import java.awt.Color;
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
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

/**
 *
 * @author timi
 */
public class Item {
    private final String itemID;
    private String itemName;
    private int stock;
    private double cost;
    private double price;
    private boolean stockLevel;
    
    // String representing the file path for item data
    private static final String ITEM_FILE = "data/items.txt";

    public Item(String itemID, String itemName, int stock, double cost, double price, boolean stockLevel) {
        this.itemID = itemID;
        this.itemName = itemName;
        this.stock = stock;
        this.cost = cost;
        this.price = price;
        this.stockLevel = stockLevel;
    }
    
    //Getter
    public String getItemID() {
        return itemID;
    }
    
    public String getItemName() {
        return itemName;
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
    
    public String getStockLevel() {
        return stock < 10 ? "Low" : "Normal";
    }

    
    
    //Setter
    public void setItemName(String itemName) { 
        this.itemName = itemName; 
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
    
    public void setStockLevel(boolean stockLevel) { 
        this.stockLevel = stockLevel; 
    }
    
    // Convert object to string to save it in file
    @Override
    public String toString() {
        return itemID + "," + itemName + "," + stock + "," + cost + "," + price + "," + stockLevel;
    }
    
    // Convert the line in the file from String to object
    public static Item fromString(String line) {
        String[] parts = line.split(",");  // Split the line by commas
        String itemID = parts[0];
        String itemName = parts[1];
        int stock = Integer.parseInt(parts[2]);
        double cost = Double.parseDouble(parts[3]);
        double price = Double.parseDouble(parts[4]);
        boolean stockLevel = Boolean.parseBoolean(parts[5]);
        
        return new Item(itemID, itemName, stock, cost, price, stockLevel);  // Return a new Item object
    }
    
    public static boolean isLowStock(int stock) {
        return stock < 10;
    }


    // Method to auto GENERATE ITEM ID from the last one
    public static String generateNextItemID(List<Item> itemList) {
        int maxNumber = 0;
        for (Item item : itemList) {
            String id = item.getItemID();
            if (id.startsWith("IT")) {
                try {
                    int num = Integer.parseInt(id.substring(2));
                    if (num > maxNumber) {
                        maxNumber = num;
                    }
                } catch (NumberFormatException ignored) {}
            }
        }
        return String.format("IT%04d", maxNumber + 1);
    }
    
    
    // Method to load Items from a file
    public static List<Item> loadItems() {
        List<Item> itemList = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(ITEM_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Item item = Item.fromString(line);
                
                // Update lowStockAlert based on current stock
                item.setStockLevel(Item.isLowStock(item.getStock()));
            
                itemList.add(item);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return itemList;
    }

    
    // Method to update item table in the UI
    public static void updateItemTableInUI(List<Item> itemList, JTable targetTable) {
        String[] columnNames = {"Item ID", "Name", "Stock", "Cost (RM)", "Price (RM)", "Stock Level"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);

        for (Item item : itemList) {
            Object[] row = {
                item.getItemID(),
                item.getItemName(),
                item.getStock(),
                item.getCost(),
                item.getPrice(),
                item.getStockLevel()
            };
            tableModel.addRow(row);
        }

        targetTable.setModel(tableModel);
        autoResizeColumnWidths(targetTable);
        applyRowColorBasedOnStockLevel(targetTable);
        
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
                    item.getStock(),
                    item.getCost(),
                    item.getPrice(),
                    item.getStockLevel()
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


    public static void applyRowColorBasedOnStockLevel(JTable table) {
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                String stockLevel = table.getModel().getValueAt(row, 5).toString(); // Column index 6 = "Stock Level"

                if (stockLevel.equalsIgnoreCase("Low")) {
                    c.setBackground(new Color(255, 204, 204)); // Light red
                } else if (stockLevel.equalsIgnoreCase("Normal")) {
                    c.setBackground(new Color(204, 255, 204)); // Light green
                } else {
                    c.setBackground(Color.WHITE);
                }

                if (isSelected) {
                    c.setBackground(table.getSelectionBackground());
                }

                return c;
            }
        });
    }
     
    public static Item findById(String itemId) {
        List<Item> allItems = loadItems();
        for (Item item : allItems) {
            if (item.getItemID().equals(itemId)) {
                return item;
            }
        }
        return null;
    }  

    public static boolean updateStock(String itemId, int qtyToAdd) {
        String file = "data/items.txt";
        List<String> updatedLines = new ArrayList<>();
        boolean updated = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts[0].equals(itemId)) {
                    int qty = Integer.parseInt(parts[3]);
                    parts[3] = String.valueOf(qty + qtyToAdd); // update quantity
                    updated = true;
                }
                updatedLines.add(String.join(",", parts));
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (String l : updatedLines) {
                writer.write(l);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return updated;
    }
    
    public static void filterStockLevel(String filter, JTable table, List<Item> itemList) {
        String[] columnNames = {"Item ID", "Name", "Stock", "Cost (RM)", "Price (RM)", "Stock Level"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        for (Item item : itemList) {
            boolean include = false;
            String stockLevel = item.getStockLevel();
            if (filter.equals("All")) {
                include = true;
            } else if (filter.equals("Low") && stockLevel.equalsIgnoreCase("Low")) {
                include = true;
            } else if (filter.equals("Normal") && stockLevel.equalsIgnoreCase("Normal")) {
                include = true;
            }

            if (include) {
                Object[] row = {
                    item.getItemID(),
                    item.getItemName(),
                    item.getStock(),
                    item.getCost(),
                    item.getPrice(),
                    item.getStockLevel()
                };
                model.addRow(row);
            }
        }

        table.setModel(model);
        autoResizeColumnWidths(table);
        applyRowColorBasedOnStockLevel(table);

        if (model.getRowCount() == 0) {
            JOptionPane.showMessageDialog(null, "No items found for filter: " + filter, "No Results", JOptionPane.INFORMATION_MESSAGE);
        }
    }

}