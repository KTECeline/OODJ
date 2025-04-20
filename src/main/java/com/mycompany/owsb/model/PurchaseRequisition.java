package com.mycompany.owsb.model;

import static com.mycompany.owsb.model.PurchaseOrder.loadPurchaseOrders;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
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

    private static final String PURCHASE_REQUISITION_FILE = "data/purchase_requisition.txt";
    
    
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
    
    public static void update(PurchaseRequisition updatedPr) {
        List<PurchaseRequisition> all = loadPurchaseRequisition();
        for (int i = 0; i < all.size(); i++) {
            if (all.get(i).getPrID().equals(updatedPr.getPrID())) {
                all.set(i, updatedPr);
                break;
            }
        }
        saveAll(all);
    }

public static void saveAll(List<PurchaseRequisition> requisitions) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(PURCHASE_REQUISITION_FILE))) {
            for (PurchaseRequisition pr : requisitions) {
                writer.write(pr.toString());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error saving purchase requisitions: " + e.getMessage());
        }
    }
public static String generateNewPrId() {
        List<PurchaseRequisition> all = loadPurchaseRequisition();
        int maxId = 0;
        for (PurchaseRequisition pr : all) {
            try {
                int idNum = Integer.parseInt(pr.getPrID().substring(2));
                if (idNum > maxId) maxId = idNum;
            } catch (NumberFormatException e) {
                // Skip if ID format is invalid
            }
        }
        return String.format("PR%04d", maxId + 1);
    }
}
