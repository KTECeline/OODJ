package com.mycompany.owsb.model;

import static com.mycompany.owsb.model.PurchaseOrder.loadPurchaseOrders;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author timi
 */
public class PurchaseRequisition {
    private String prID;
    private String itemID;
    private int quantity;
    private String requiredDate;
    private String supplierID;
    private String raisedBy;
    private double unitCost;
    private double totalCost;
    private String status;

    public PurchaseRequisition(String prID, String itemID, int quantity, String requiredDate, String supplierID,
                               String raisedBy, double unitCost, String status) {
        this.prID = prID;
        this.itemID = itemID;
        this.quantity = quantity;
        this.requiredDate = requiredDate;
        this.supplierID = supplierID;
        this.raisedBy = raisedBy;
        this.unitCost = unitCost;
        this.totalCost = quantity * unitCost;
        this.status = status;
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

    public String getRequiredDate() {
        return requiredDate;
    }

    public String getSupplierID() {
        return supplierID;
    }

    public String getRaisedBy() {
        return raisedBy;
    }

    public double getUnitCost() {
        return unitCost;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public String getStatus() {
        return status;
    }
    public void setStatus(String status){
        this.status=status;
    }
    // Convert object to string to save it in file
    @Override
    public String toString() {
        return prID + "," + itemID + "," + quantity + "," + requiredDate + "," + supplierID + "," +
               raisedBy + "," + unitCost + "," + totalCost + "," + status;
    }
    
    // Convert the line in the file from String to object
    public static PurchaseRequisition fromString(String line) {
        String[] parts = line.split(",");
        return new PurchaseRequisition(
            parts[0],                       // prId
            parts[1],                       // itemCode
            Integer.parseInt(parts[2]),     // quantity
            parts[3],                       // requiredDate
            parts[4],                       // supplierId
            parts[5],                       // raisedBy
            Double.parseDouble(parts[6]),   // unitCost
            parts[8]                        // status (skip parts[7] since totalCost is calculated)
        );
    }
    
    public static List<PurchaseRequisition> loadPurchaseRequisition() {
        List<PurchaseRequisition> prList = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(PURCHASE_REQUISITION_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                PurchaseRequisition pr = PurchaseRequisition.fromString(line);
                prList.add(pr);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return prList;
    }
    public static PurchaseRequisition findById(String prId) {
    List<PurchaseRequisition> allOrders = loadPurchaseRequisition();
    return allOrders.stream()
            .filter(po -> po.getPrID().equals(prId))
            .findFirst()
            .orElse(null);
}

