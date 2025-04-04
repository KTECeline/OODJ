/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author timi
 */
public class PurchaseRequisition {
    String prID;
    String itemID;
    int quantity;
    String requiredDate;
    String supplierID;
    String raisedBy;
    double unitCost;
    double totalCost;
    String status;

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

    @Override
    public String toString() {
        return prID + "," + itemID + "," + quantity + "," + requiredDate + "," + supplierID + "," +
               raisedBy + "," + unitCost + "," + totalCost + "," + status;
    }

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
}

