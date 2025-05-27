package com.mycompany.owsb.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.JOptionPane;

/**
 *
 * @author leopa
 */
public class PurchaseManager extends User {
    private static final String ITEM_FILE = "data/items.txt";
    private static final String PURCHASE_ORDER_FILE = "data/purchase_order.txt";
    private static final String PURCHASE_REQUISITION_FILE = "data/purchase_requisition.txt";
    private static final String SUPPLIERS_FILE = "data/supplier.txt";

    public PurchaseManager(String userId, String username, String password, String role) {
        super(userId, username, password, role);
    }

    public List<PurchaseOrder> generatePurchaseOrders(String prId, String supplierID, String createdBy) {
        // 1. Validate PurchaseRequisition
        PurchaseRequisition pr = PurchaseRequisition.findById(prId);
        if (pr == null) {
            JOptionPane.showMessageDialog(null, "Purchase Requisition not found.", "Error", JOptionPane.ERROR_MESSAGE);
            throw new IllegalArgumentException("Purchase Requisition not found");
        }

        // 2. Check if PR is pending
        if (!pr.getStatus().equalsIgnoreCase("PENDING")) {
            JOptionPane.showMessageDialog(null, "Only pending requisitions can be converted to POs.", "Error", JOptionPane.ERROR_MESSAGE);
            throw new IllegalStateException("Only pending requisitions can be converted to POs");
        }

        List<PurchaseOrder> generatedPOs = new ArrayList<>();

        // 3. For each item, generate a PurchaseOrder
        for (PurchaseRequisitionItem prItem : pr.getItems()) {
            Item item = Item.findById(prItem.getItemID());
            if (item == null) {
                JOptionPane.showMessageDialog(null, "Item not found: " + prItem.getItemID(), "Error", JOptionPane.ERROR_MESSAGE);
                continue; // skip this item, continue with others
            }

            String poId = PurchaseOrder.generateNewOrderId();
            PurchaseOrder po = new PurchaseOrder(
                    poId,
                    prItem.getItemID(),
                    prItem.getQuantity(),
                    supplierID,
                    item.getPrice(),
                    new Date().toString(),
                    "PENDING",
                    prId,
                    createdBy // use passed-in createdBy (instead of this.getUserId())
            );

            generatedPOs.add(po);
        }

        return generatedPOs;
    }


      public PurchaseOrder editPurchaseOrder(String poId, int newQuantity, String newSupplierId) {
        // 1. Find the PO
        PurchaseOrder po = PurchaseOrder.findById(poId);
        if (po == null) {
            throw new IllegalArgumentException("Purchase Order not found");
        }
        
        // 2. Validate PO can be edited
        if (!po.getStatus().equals("PENDING")) {
            throw new IllegalStateException("Only pending orders can be modified");
        }
        
        // 3. Update the PO
        po.setQuantity(newQuantity);
        po.setSupplierID(newSupplierId);
        po.setTotalPrice(newQuantity * po.getUnitPrice());
        
        // 4. Save changes
        PurchaseOrder.update(po);
        
        return po;
    }

    public PurchaseOrder approvePurchaseOrder(String poId) {
        PurchaseOrder po = PurchaseOrder.findById(poId);
        if (po == null) {
            JOptionPane.showMessageDialog(null, "Purchase Order not found.", "Error", JOptionPane.ERROR_MESSAGE);
            throw new IllegalArgumentException("Purchase Order not found");
        }

        if (po.getStatus().equals("PENDING")) {
            po.setStatus("APPROVED");
            PurchaseOrder.update(po);
        } else {
            JOptionPane.showMessageDialog(null, "Only pending orders can be approved.", "Error", JOptionPane.ERROR_MESSAGE);
            throw new IllegalStateException("Only pending orders can be approved");
        }

        return po;
    }

    // View Operations

    public List<PurchaseOrder> getAllPurchaseOrders() {
        return PurchaseOrder.loadPurchaseOrders();
    }

    public List<PurchaseRequisition> getAllRequisitions() {
        return PurchaseRequisition.loadPurchaseRequisition();
    }

    public List<Item> getAllItems() {
        return Item.loadItems();
    }

    public List<Supplier> getAllSuppliers() {
        return Supplier.loadSuppliers();
    }

    // Filter Methods

    public List<PurchaseOrder> getOrdersByStatus(String status) {
        List<PurchaseOrder> allOrders = getAllPurchaseOrders();
        List<PurchaseOrder> filtered = new ArrayList<>();

        for (PurchaseOrder po : allOrders) {
            if (po.getStatus().equalsIgnoreCase(status)) {
                filtered.add(po);
            }
        }

        return filtered;
    }

    public List<PurchaseOrder> getOrdersBySupplier(String supplierId) {
        List<PurchaseOrder> allOrders = getAllPurchaseOrders();
        List<PurchaseOrder> filtered = new ArrayList<>();

        for (PurchaseOrder po : allOrders) {
            if (po.getSupplierID().equalsIgnoreCase(supplierId)) {
                filtered.add(po);
            }
        }

        return filtered;
    }

    // Business Logic

    public double calculateTotalOrderValue() {
        List<PurchaseOrder> orders = getAllPurchaseOrders();
        double total = 0;

        for (PurchaseOrder po : orders) {
            total += po.getTotalPrice();
        }

        return total;
    }

    public int countOrdersByStatus(String status) {
        return getOrdersByStatus(status).size();
    }

    // Validation Methods

    public boolean canEditOrder(String poId) {
        PurchaseOrder po = PurchaseOrder.findById(poId);
        return po != null && po.getStatus().equals("PENDING");
    }

    public boolean canApproveOrder(String poId) {
        PurchaseOrder po = PurchaseOrder.findById(poId);
        return po != null && po.getStatus().equals("PENDING");
    }
}