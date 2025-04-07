/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.owsb.model;

/**
 *
 * @author timi
 */
public class Sales {
    private String salesID;
    private String itemID;
    private int quantitySold;
    private double pricePerUnit;
    private double totalAmount;
    private String date;
    private String remarks;

    public Sales(String salesID, String itemID, int quantitySold, double pricePerUnit, String date, String remarks) {
        this.salesID = salesID;
        this.itemID = itemID;
        this.quantitySold = quantitySold;
        this.pricePerUnit = pricePerUnit;
        this.totalAmount = quantitySold * pricePerUnit;
        this.date = date;
        this.remarks = remarks;
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

    public double getTotalAmount() {
        return totalAmount;
    }

    public String getDate() {
        return date;
    }

    public String getRemarks() {
        return remarks;
    }
    
    // Convert object to string to save it in file
    @Override
    public String toString() {
        return salesID + "," + itemID + "," + quantitySold + "," + pricePerUnit + "," + totalAmount + "," + date + "," + remarks;
    }
    
    // Convert the line in the file from String to object
    public static Sales fromString(String line) {
        String[] parts = line.split(",");
        return new Sales(
            parts[0],
            parts[1],
            Integer.parseInt(parts[2]),
            Double.parseDouble(parts[3]),
            parts[5],
            parts[6]
        );
    }
}
