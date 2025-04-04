/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.owsb;

/**
 *
 * @author timi
 */
public class Item {
    String itemID;
    String itemName;
    String supplierId;
    int stock;
    double cost;
    double price;

    public Item(String itemID, String itemName, String supplierId, int stock, double cost, double price) {
        this.itemID = itemID;
        this.itemName = itemName;
        this.supplierId = supplierId;
        this.stock = stock;
        this.cost = cost;
        this.price = price;
    }

    @Override
    public String toString() {
        return itemID + "," + itemName + "," + supplierId + "," + stock + "," + cost + "," + price;
    }

    public static Item fromString(String line) {
        String[] parts = line.split(",");
        return new Item(
            parts[0],                    // itemID
            parts[1],                    // itemName
            parts[2],                    // supplierId
            Integer.parseInt(parts[3]),  // stock
            Double.parseDouble(parts[4]),// cost
            Double.parseDouble(parts[5]) // price
        );
    }
}

