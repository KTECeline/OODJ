/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.owsb.model;
import java.util.List;

/**
 *
 * @author timi
 */

public interface ManagePOInterface {
    // Generate a PO from a purchase requisition
    List<PurchaseOrder> generatePO(String supplierId, String createdBy, String prId, List<String> itemIds);

    // Edit an existing PO item
    void editPOItem(String poId, String itemId, String newSupplierId, int newQuantity, double newTotalPrice, String newStatus);

    // Delete a PO item
    void deletePOItem(String poId, String itemId);

    // View all POs
    List<PurchaseOrder> viewPOs();

    // Search POs by ID
    List<PurchaseOrder> searchPOs(String poId);
}
