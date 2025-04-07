/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.owsb.model;

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
    
    
    @Override
    public String toString() {
        return itemID + "," + itemName + "," + supplierId + "," + stock + "," + cost + "," + price;
    }

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
    
    

}

