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

/**
 *
 * @author timi
 */
public class PurchaseRequisitionItem {
    private String prID;
    private String itemID;
    private int quantity;
    private double unitCost;
    private double totalCost;
    
    private static final String PURCHASE_REQUISITION_ITEM_FILE = "data/purchase_requisition_item.txt";

    public PurchaseRequisitionItem(String prID, String itemID, int quantity, double unitCost) {
        this.prID = prID;
        this.itemID = itemID;
        this.quantity = quantity;
        this.unitCost = unitCost;
        this.totalCost = quantity * unitCost;
    }

    public String getPrID() {
        return prID;
    }
    
    public String getItemID() {
        return itemID;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getUnitCost() {
        return unitCost;
    }

    public double getTotalCost() {
        return totalCost;
    }

    @Override
    public String toString() {
        return prID + "," + itemID + "," + quantity + "," + unitCost;
    }

    public static PurchaseRequisitionItem fromString(String line) {
        String[] parts = line.split(",");
        return new PurchaseRequisitionItem(
            parts[0],
            parts[1],
            Integer.parseInt(parts[2]),
            Double.parseDouble(parts[3])
        );
    }
    
    
    
    public static List<PurchaseRequisitionItem> loadPurchaseRequisitionItems() {
        List<PurchaseRequisitionItem> itemList = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(PURCHASE_REQUISITION_ITEM_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                PurchaseRequisitionItem item = PurchaseRequisitionItem.fromString(line);
                itemList.add(item);
            }
        } catch (IOException e) {
            System.err.println("Error loading purchase requisition items: " + e.getMessage());
        }

        return itemList;
    }
}
