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

    public PurchaseOrder generatePurchaseOrder(String prId, String supplierID, String createdBy) {
        // 1. Validate PurchaseRequisition
        PurchaseRequisition pr = PurchaseRequisition.findById(prId);
        if (pr == null) {
            JOptionPane.showMessageDialog(null, "Purchase Requisition not found.", "Error", JOptionPane.ERROR_MESSAGE);
            throw new IllegalArgumentException("Purchase Requisition not found");
        }

        // 2. Check if PR is pending
        if (!pr.getStatus().equals("PENDING")) {
            JOptionPane.showMessageDialog(null, "Only pending requisitions can be converted to POs.", "Error", JOptionPane.ERROR_MESSAGE);
            throw new IllegalStateException("Only pending requisitions can be converted to POs");
        }

        // 3. Validate Item
        Item item = Item.findById(pr.getItemID());
        if (item == null) {
            JOptionPane.showMessageDialog(null, "Item not found.", "Error", JOptionPane.ERROR_MESSAGE);
            throw new IllegalArgumentException("Item not found");
        }

        // 4. Create new PurchaseOrder
        String poId = PurchaseOrder.generateNewOrderId();
        PurchaseOrder po = new PurchaseOrder(
                poId,
                pr.getItemID(),
                pr.getQuantity(),
                supplierID,
                item.getPrice(),
                new Date().toString(),
                "PENDING",
                prId,
                this.getUserId()
        );

        // 5. Save PurchaseOrder
        List<PurchaseOrder> poList = new ArrayList<>();
        poList.add(po);
        PurchaseOrder.saveToFile(poList, PURCHASE_ORDER_FILE);

        // 6. Update PurchaseRequisition status
        pr.setStatus("PROCESSED");
        PurchaseRequisition.update(pr);

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