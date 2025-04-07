/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.owsb;

/**
 *
 * @author timi
 */
public class Sales {
    String salesID;
    String itemID;
    int quantitySold;
    double pricePerUnit;
    double totalAmount;
    String date;
    String remarks;

    public Sales(String salesID, String itemID, int quantitySold, double pricePerUnit, String date, String remarks) {
        this.salesID = salesID;
        this.itemID = itemID;
        this.quantitySold = quantitySold;
        this.pricePerUnit = pricePerUnit;
        this.totalAmount = quantitySold * pricePerUnit;
        this.date = date;
        this.remarks = remarks;
    }

    @Override
    public String toString() {
        return salesID + "," + itemID + "," + quantitySold + "," + pricePerUnit + "," + totalAmount + "," + date + "," + remarks;
    }

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
