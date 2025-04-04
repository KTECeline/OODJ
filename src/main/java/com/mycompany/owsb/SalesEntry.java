/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.owsb;

/**
 *
 * @author timi
 */
public class SalesEntry {
    String itemID;
    int quantitySold;
    double pricePerUnit;
    double totalAmount;
    String salesManagerId;
    String date;
    String remarks;

    public SalesEntry(String itemID, int quantitySold, double pricePerUnit, String salesManagerId, String date, String remarks) {
        this.itemID = itemID;
        this.quantitySold = quantitySold;
        this.pricePerUnit = pricePerUnit;
        this.totalAmount = quantitySold * pricePerUnit;
        this.salesManagerId = salesManagerId;
        this.date = date;
        this.remarks = remarks;
    }

    @Override
    public String toString() {
        return itemID + "," + quantitySold + "," + pricePerUnit + "," + totalAmount + "," + salesManagerId + "," + date + "," + remarks;
    }

    public static SalesEntry fromString(String line) {
        String[] parts = line.split(",");
        return new SalesEntry(
            parts[0],
            Integer.parseInt(parts[1]),
            Double.parseDouble(parts[2]),
            parts[4],
            parts[5],
            parts[6]
        );
    }
}
