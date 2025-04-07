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
public class Item {
    private String itemID;
    private String itemName;
    private String supplierId;
    private int stock;
    private double cost;
    private double price;

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
    
    
    // Method to return the formatted details of the Item
    public String getFormattedDetails() {
        return "Item ID: " + itemID + "\n\n" +
               "Item Name: " + itemName + "\n\n" +
               "Supplier ID: " + supplierId + "\n\n" +
               "Stock: " + stock + "\n\n" +
               "Cost: " + cost + "\n\n" +
               "Price: " + price;
    }

    // Method to load Items from a file
    public static List<Item> loadFromFile(String filePath) {
        List<Item> itemList = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
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

    // Method to update the list of items in the UI
    public static void updateItemListInUI(List<Item> itemList, JList<String> targetList, JTextArea detailArea) {
        DefaultListModel<String> listModel = new DefaultListModel<>();
        for (Item item : itemList) {
            listModel.addElement(item.getItemID());
        }
        targetList.setModel(listModel);

        // List click listener
        targetList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String selectedID = targetList.getSelectedValue();
                for (Item item : itemList) {
                    if (item.getItemID().equals(selectedID)) {
                        detailArea.setText(item.getFormattedDetails());
                        break;
                    }
                }
            }
        });
    }

    // Method to search and display an Item's details
    public static void searchAndDisplayItem(JTextField searchField, JTextArea detailsArea, List<Item> itemList) {
        String searchID = searchField.getText().trim().toUpperCase();
        boolean found = false;

        if (itemList.isEmpty()) {
            detailsArea.setText("No Items loaded.");
            return;
        }

        for (Item item : itemList) {
            if (item.getItemID().equalsIgnoreCase(searchID)) {
                detailsArea.setText(item.getFormattedDetails());
                found = true;
                break;
            }
        }

        if (!found) {
            detailsArea.setText("Item ID not found.");
        }
    }

    // Method to save Items to a file
    public static void saveToFile(List<Item> itemList, String filename) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (Item item : itemList) {
                writer.write(item.toString());
                writer.newLine();
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Failed to save items.");
        }
    }
    
    
    

}

