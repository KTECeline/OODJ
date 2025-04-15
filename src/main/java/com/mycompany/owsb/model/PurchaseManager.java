package com.mycompany.owsb.model;

import java.util.ArrayList;
import java.util.List;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
 
/**
 *
 * @author leopa
 */
public class PurchaseManager extends User {
    private static final String ITEM_FILE = "data/items.txt";
    private static final String PO_FILE = "data/purchase_order.txt";
    private static final String PR_FILE="data/purchase_requisition.txt";
    private static final String SUPPRLIERS_FILE= "data/supplier.txt";
    
       
        
        public PurchaseManager(String userId, String username, String password, String role){
            super(userId, username, password, role);
        }
        
        public PurchaseOrder generatePurchaseOrder (String prId, String supplierID, String createdBy){
            PurchaseRequisition pr = PurchaseRequisition.findById(prId);
            if(pr== null){
                throw new IllegalArgumentException("PurchaseRequisition not found");
            if (!pr.getStatus().equals("PENDING")){
                throw new IllegalStateException("Only pending requisition can be converted to POs");
            
            Item item = Item.findById(pr.getItemID());
            if(item==null){
                throw new IllegaArgumentException("Item not found");
            }
            
            String poId = PurchaseOrder.generateNewOrderId();
            PurchaseOrder po =new PurchaseOrder(
            poId,
            pr.getItemID(),
            pr.getQuantity(),
            supplierId,
            item.getPrice(),
            new Date().toString(),
            "PENDING",
            prId,
            this.getUserId()
            );
            
            PurchaseOrder.saveToFile(po);
            
            pr.setStatus("PROCESSED");
            PurchaseRequisition.update(pr);
            }
            }
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
            throw new IllegalArgumentException("Purchase Order not found");
        }
        
        if (po.getStatus().equals("PENDING")) {
            po.setStatus("APPROVED");
            PurchaseOrder.update(po);
        } else {
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
