package com.mycompany.owsb.model;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Represents a Purchase Manager user with functionalities to view items, suppliers,
 * purchase requisitions, generate purchase orders, and manage (edit/delete) purchase orders.
 */
public class PurchaseManager extends Manager implements ManageItemInterface {

    private static final String PURCHASE_ORDER_FILE = "data/purchase_order.txt";
    private static final String PURCHASE_REQUISITION_FILE = "data/purchase_requisition.txt";
    private static final String ITEMS_FILE = "data/items.txt";
    private static final String SUPPLIERS_FILE = "data/suppliers.txt";
    
    
    public PurchaseManager(User loggedInUser) {
        super(loggedInUser);
    
    }

    

    /**
     * Generates a single Purchase Order consolidating all items from a Purchase Requisition.
     * @param prId The ID of the Purchase Requisition.
     * @param supplierId The ID of the supplier for the PO.
     * @param createdBy The ID of the Purchase Manager creating the PO.
     * @return The generated PurchaseOrder object.
     */

    public PurchaseOrder generatePurchaseOrders(String prId, String supplierId, String createdBy, List<String> approvedItemIds) {
    PurchaseRequisition pr = PurchaseRequisition.findById(prId);
    if (pr == null) {
        JOptionPane.showMessageDialog(null, "Purchase Requisition not found: " + prId, "Error", JOptionPane.ERROR_MESSAGE);
        throw new IllegalArgumentException("Purchase Requisition not found");
    }

    if (!pr.getStatus().equalsIgnoreCase("PENDING")) {
        JOptionPane.showMessageDialog(null, "Only pending requisitions can be converted to POs: " + prId, "Error", JOptionPane.ERROR_MESSAGE);
        throw new IllegalStateException("Only pending requisitions can be converted to POs");
    }

    Supplier supplier = Supplier.findById(supplierId);
    if (supplier == null) {
        JOptionPane.showMessageDialog(null, "Supplier not found: " + supplierId, "Error", JOptionPane.ERROR_MESSAGE);
        throw new IllegalArgumentException("Supplier not found");
    }

    List<SupplierItem> supplierItems = SupplierItem.loadSupplierItems();
    List<PurchaseRequisitionItem> approvedItems = new ArrayList<>();
    List<PurchaseRequisitionItem> allPrItems = PurchaseRequisitionItem.loadPurchaseRequisitionItems()
        .stream()
        .filter(item -> item.getPrID().equalsIgnoreCase(prId))
        .collect(Collectors.toList());

    for (PurchaseRequisitionItem prItem : allPrItems) {
        if (!approvedItemIds.contains(prItem.getItemID())) {
            continue;
        }
        Item item = Item.findById(prItem.getItemID());
        if (item == null) {
            JOptionPane.showMessageDialog(null, "Item not found: " + prItem.getItemID(), "Error", JOptionPane.ERROR_MESSAGE);
            throw new IllegalArgumentException("Item not found: " + prItem.getItemID());
        }

        boolean isSupplied = supplierItems.stream()
            .anyMatch(si -> si.getSupplierID().equalsIgnoreCase(supplierId) &&
                           si.getItemID().equalsIgnoreCase(prItem.getItemID()));
        if (!isSupplied) {
            JOptionPane.showMessageDialog(null, "Supplier " + supplierId + " does not supply item " + prItem.getItemID(), "Error", JOptionPane.ERROR_MESSAGE);
            throw new IllegalArgumentException("Supplier does not supply item: " + prItem.getItemID());
        }
        approvedItems.add(prItem);
    }

    if (approvedItems.isEmpty()) {
        pr.setStatus("REJECTED");
        PurchaseRequisition.update(pr);
        JOptionPane.showMessageDialog(null, "No items approved for PO in PR: " + prId, "Error", JOptionPane.ERROR_MESSAGE);
        throw new IllegalArgumentException("No items approved for PO");
    }

    String poId = PurchaseOrder.generateNewOrderId();
    PurchaseOrder po = new PurchaseOrder(poId, supplierId, new SimpleDateFormat("yyyy-MM-dd").format(new Date()), "PENDING", prId, createdBy);

    for (PurchaseRequisitionItem prItem : approvedItems) {
        Item item = Item.findById(prItem.getItemID());
        double totalPrice = prItem.getQuantity() * item.getCost();
        PurchaseOrder.PurchaseOrderItem poItem = new PurchaseOrder.PurchaseOrderItem(
            prItem.getItemID(), prItem.getQuantity(), totalPrice);
        po.addItem(poItem);
    }

    pr.setStatus("APPROVED");
    PurchaseRequisition.update(pr);

    // Save to purchase_order.txt
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(PURCHASE_ORDER_FILE, true))) {
        for (PurchaseOrder.PurchaseOrderItem poItem : po.getItems()) {
            String line = String.format("%s,%s,%s,%d,%.1f,%s,%s,%s,%s",
                po.getOrderID(), poItem.getItemID(), po.getSupplierID(), poItem.getQuantity(),
                poItem.getTotalPrice(), po.getOrderDate(), po.getStatus(), po.getPrId(), po.getCreatedBy());
            writer.write(line);
            writer.newLine();
        }
    } catch (IOException e) {
        JOptionPane.showMessageDialog(null, "Failed to save purchase order: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        throw new RuntimeException("Failed to save purchase order", e);
    }

    return po;
}



    public List<PurchaseOrder> generatePurchaseOrdersFromMultiplePRs(String supplierId, String createdBy, Map<String, List<String>> approvedItemsByPR) {
    if (supplierId == null || createdBy == null || approvedItemsByPR == null || approvedItemsByPR.isEmpty()) {
        JOptionPane.showMessageDialog(null, "Invalid input: supplier, creator or PRs missing", "Error", JOptionPane.ERROR_MESSAGE);
        throw new IllegalArgumentException("Invalid input provided");
    }

    Supplier supplier = Supplier.findById(supplierId);
    if (supplier == null) {
        JOptionPane.showMessageDialog(null, "Supplier not found: " + supplierId, "Error", JOptionPane.ERROR_MESSAGE);
        throw new IllegalArgumentException("Supplier not found");
    }

    List<SupplierItem> supplierItems = SupplierItem.loadSupplierItems();
    List<PurchaseOrder> generatedPOs = new ArrayList<>();

    for (Map.Entry<String, List<String>> entry : approvedItemsByPR.entrySet()) {
        String prId = entry.getKey();
        List<String> approvedItemIds = entry.getValue();

        PurchaseRequisition pr = PurchaseRequisition.findById(prId);
        if (pr == null) {
            JOptionPane.showMessageDialog(null, "Purchase Requisition not found: " + prId, "Error", JOptionPane.ERROR_MESSAGE);
            throw new IllegalArgumentException("Purchase Requisition not found: " + prId);
        }

        if (!pr.getStatus().equalsIgnoreCase("PENDING")) {
            JOptionPane.showMessageDialog(null, "Only pending requisitions can be converted to POs: " + prId, "Error", JOptionPane.ERROR_MESSAGE);
            throw new IllegalStateException("Only pending requisitions can be converted to POs");
        }

        List<PurchaseRequisitionItem> prItems = PurchaseRequisitionItem.loadPurchaseRequisitionItems()
            .stream()
            .filter(item -> item.getPrID().equalsIgnoreCase(prId) && approvedItemIds.contains(item.getItemID()))
            .collect(Collectors.toList());

        if (prItems.isEmpty()) {
            continue;
        }

        List<PurchaseOrder.PurchaseOrderItem> poItems = new ArrayList<>();
        boolean atLeastOneApproved = false;

        for (PurchaseRequisitionItem prItem : prItems) {
            Item item = Item.findById(prItem.getItemID());
            if (item == null) {
                JOptionPane.showMessageDialog(null, "Item not found: " + prItem.getItemID(), "Error", JOptionPane.ERROR_MESSAGE);
                throw new IllegalArgumentException("Item not found: " + prItem.getItemID());
            }

            boolean isSupplied = supplierItems.stream()
                .anyMatch(si -> si.getSupplierID().equalsIgnoreCase(supplierId) &&
                               si.getItemID().equalsIgnoreCase(prItem.getItemID()));
            if (!isSupplied) {
                JOptionPane.showMessageDialog(null, "Supplier " + supplierId + " does not supply item " + prItem.getItemID(), "Error", JOptionPane.ERROR_MESSAGE);
                throw new IllegalArgumentException("Supplier does not supply item: " + prItem.getItemID());
            }

            double totalPrice = prItem.getQuantity() * item.getCost();
            PurchaseOrder.PurchaseOrderItem poItem = new PurchaseOrder.PurchaseOrderItem(
                prItem.getItemID(), prItem.getQuantity(), totalPrice);
            poItem.setPrId(prId);  // set PR ID per PO item
            poItems.add(poItem);
            atLeastOneApproved = true;
        }

        if (atLeastOneApproved) {
            // Create PO ID per PR
            String poId = PurchaseOrder.generateNewOrderId();
            PurchaseOrder po = new PurchaseOrder(poId, supplierId, new SimpleDateFormat("yyyy-MM-dd").format(new Date()), "PENDING", prId, createdBy);

            for (PurchaseOrder.PurchaseOrderItem item : poItems) {
                po.addItem(item);
            }

            // Save this PO to file
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(PURCHASE_ORDER_FILE, true))) {
                for (PurchaseOrder.PurchaseOrderItem poItem : po.getItems()) {
                    String line = String.format("%s,%s,%s,%d,%.1f,%s,%s,%s,%s",
                        po.getOrderID(), poItem.getItemID(), po.getSupplierID(), poItem.getQuantity(),
                        poItem.getTotalPrice(), po.getOrderDate(), po.getStatus(), po.getPrId(), po.getCreatedBy());
                    writer.write(line);
                    writer.newLine();
                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Failed to save purchase order: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                throw new RuntimeException("Failed to save purchase order", e);
            }

            // Update PR status and add to results
            pr.setStatus("APPROVED");
            PurchaseRequisition.update(pr);
            generatedPOs.add(po);
        }
    }

    if (generatedPOs.isEmpty()) {
        JOptionPane.showMessageDialog(null, "No approved items found in any PR.", "Error", JOptionPane.ERROR_MESSAGE);
        throw new IllegalArgumentException("No approved items");
    }

    return generatedPOs;
}




    /**
     * Edits an existing Purchase Order's item quantity or supplier.
     * @param poId The ID of the Purchase Order to edit.
     * @param itemId The ID of the item to edit.
     * @param newQuantity The new quantity for the item.
     * @param newSupplierId The new supplier ID.
     * @return The updated PurchaseOrder object.
     */
    public PurchaseOrder editPurchaseOrder(String poId, String itemId, int newQuantity, String newSupplierId) {
        PurchaseOrder po = PurchaseOrder.findById(poId);
        if (po == null) {
            JOptionPane.showMessageDialog(null, "Purchase Order not found.", "Error", JOptionPane.ERROR_MESSAGE);
            throw new IllegalArgumentException("Purchase Order not found");
        }

        if (!po.getStatus().equals("PENDING")) {
            JOptionPane.showMessageDialog(null, "Only pending orders can be modified.", "Error", JOptionPane.ERROR_MESSAGE);
            throw new IllegalStateException("Only pending orders can be modified");
        }

        Supplier supplier = Supplier.findById(newSupplierId);
        if (supplier == null) {
            JOptionPane.showMessageDialog(null, "Supplier not found.", "Error", JOptionPane.ERROR_MESSAGE);
            throw new IllegalArgumentException("Supplier not found");
        }

        List<SupplierItem> supplierItems = SupplierItem.loadSupplierItems();
        for (PurchaseOrder.PurchaseOrderItem poItem : po.getItems()) {
            boolean isSupplied = supplierItems.stream()
                    .anyMatch(si -> si.getSupplierID().equalsIgnoreCase(newSupplierId) && 
                                   si.getItemID().equalsIgnoreCase(poItem.getItemID()));
            if (!isSupplied) {
                JOptionPane.showMessageDialog(null, "Supplier " + newSupplierId + " does not supply item " + poItem.getItemID(), "Error", JOptionPane.ERROR_MESSAGE);
                throw new IllegalArgumentException("Supplier does not supply item: " + poItem.getItemID());
            }
        }

        boolean itemFound = false;
        for (PurchaseOrder.PurchaseOrderItem poItem : po.getItems()) {
            if (poItem.getItemID().equalsIgnoreCase(itemId)) {
                Item item = Item.findById(itemId);
                if (item == null) {
                    JOptionPane.showMessageDialog(null, "Item not found: " + itemId, "Error", JOptionPane.ERROR_MESSAGE);
                    throw new IllegalArgumentException("Item not found");
                }
                poItem.setQuantity(newQuantity);
                poItem.setTotalPrice(newQuantity * item.getCost());
                itemFound = true;
                break;
            }
        }

        if (!itemFound) {
            JOptionPane.showMessageDialog(null, "Item " + itemId + " not found in PO.", "Error", JOptionPane.ERROR_MESSAGE);
            throw new IllegalArgumentException("Item not found in PO");
        }

        po.setSupplierID(newSupplierId);
        PurchaseOrder.update(po);
        return po;
    }

    /**
     * Deletes a Purchase Order if it is in PENDING status.
     * @param poId The ID of the Purchase Order to delete.
     */
    public void deletePurchaseOrder(String poId) {
        PurchaseOrder po = PurchaseOrder.findById(poId);
        if (po == null) {
            JOptionPane.showMessageDialog(null, "Purchase Order not found.", "Error", JOptionPane.ERROR_MESSAGE);
            throw new IllegalArgumentException("Purchase Order not found");
        }

        if (!po.getStatus().equals("PENDING")) {
            JOptionPane.showMessageDialog(null, "Only pending orders can be deleted.", "Error", JOptionPane.ERROR_MESSAGE);
            throw new IllegalStateException("Only pending orders can be deleted");
        }

        List<PurchaseOrder> poList = PurchaseOrder.loadPurchaseOrders();
        poList.removeIf(p -> p.getOrderID().equals(poId));
        try (java.io.BufferedWriter writer = new java.io.BufferedWriter(new java.io.FileWriter(PURCHASE_ORDER_FILE))) {
            for (PurchaseOrder p : poList) {
                writer.write(p.toString());
                writer.newLine();
            }
        } catch (java.io.IOException e) {
            JOptionPane.showMessageDialog(null, "Failed to save purchase orders.", "Error", JOptionPane.ERROR_MESSAGE);
        }

        PurchaseRequisition pr = PurchaseRequisition.findById(po.getPrId());
        if (pr != null) {
            pr.setStatus("PENDING");
            PurchaseRequisition.update(pr);
        }
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

    // GUI Update Methods
    public void updateItemTable(JTable targetTable) {
        Item.updateItemTableInUI(getAllItems(), targetTable);
    }

    public void updateSupplierTable(JTable targetTable) {
        Supplier.updateSupplierTableInUI(getAllSuppliers(), SupplierItem.loadSupplierItems(), targetTable);
    }

    public void updatePRTable(JTable targetTable) {
    PurchaseRequisition.updatePRTableInUI(getAllRequisitions(), 
                                         PurchaseRequisitionItem.loadPurchaseRequisitionItems(), 
                                         getAllItems(), // Add Item list
                                         targetTable);
}
    public void updatePOTable(JTable targetTable) {
        PurchaseOrder.updatePOTableInUI(getAllPurchaseOrders(), targetTable);
    }

    // Search Methods
    public void searchItem(JTextField searchField, JTable table) {
        Item.searchAndDisplayItemInTable(searchField, table, getAllItems());
    }

    public void searchSupplier(JTextField searchField, JTable table) {
        Supplier.searchAndDisplaySupplierInTable(searchField, table, getAllSuppliers(), SupplierItem.loadSupplierItems());
    }

    public void searchPR(JTextField searchField, JTable table) {
        PurchaseRequisition.searchAndDisplayPRInTable(searchField, table, getAllRequisitions(), getAllItems(),PurchaseRequisitionItem.loadPurchaseRequisitionItems());
    }

    public void searchPO(JTextField searchField, JTable table) {
        PurchaseOrder.searchAndDisplayPO(searchField, table, getAllPurchaseOrders());
    }

    // Filter Methods
    public List<PurchaseOrder> getOrdersByStatus(String status) {
        return getAllPurchaseOrders().stream()
                .filter(po -> po.getStatus().equalsIgnoreCase(status))
                .toList();
    }

    public List<PurchaseOrder> getOrdersBySupplier(String supplierId) {
        return getAllPurchaseOrders().stream()
                .filter(po -> po.getSupplierID().equalsIgnoreCase(supplierId))
                .toList();
    }

    // Business Logic
    public double calculateTotalOrderValue() {
        return getAllPurchaseOrders().stream()
                .mapToDouble(PurchaseOrder::getTotalPrice)
                .sum();
    }

    public int countOrdersByStatus(String status) {
        return getOrdersByStatus(status).size();
    }

    // Validation Methods
    public boolean canEditOrder(String poId) {
        PurchaseOrder po = PurchaseOrder.findById(poId);
        return po != null && po.getStatus().equals("PENDING");
    }

    @Override
    public boolean isAllowedToPerform(String action) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void addItem(JFrame parent, List<Item> itemList, JTable itemTable) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void editItem(Item itemToEdit, List<Item> itemList, JTable itemTable) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void deleteItem(JFrame parent, List<Item> itemList, List<SupplierItem> supplierItemList, List<PurchaseRequisition> prList, List<PurchaseRequisitionItem> prItemList, JTable itemTable) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
    public int generatePOsFromSelections(DefaultTableModel model, String createdBy) {
    if (model.getRowCount() == 0) {
        throw new IllegalStateException("Table is empty. No PRs to process.");
    }

    Map<String, Map<String, List<String>>> selections = new HashMap<>();
    boolean hasSelections = false;

    for (int row = 0; row < model.getRowCount(); row++) {
        Boolean isSelected = (Boolean) model.getValueAt(row, 9);
        if (isSelected != null && isSelected) {
            hasSelections = true;
            String prId = model.getValueAt(row, 0).toString();
            String itemId = model.getValueAt(row, 1).toString().split(" - ")[0];
            String supplierId = model.getValueAt(row, 2).toString();
            String status = model.getValueAt(row, 8).toString();

            if (!status.equalsIgnoreCase("PENDING")) {
                throw new IllegalArgumentException("Only PENDING PRs can be selected. Invalid PR: " + prId);
            }

            selections.computeIfAbsent(prId, k -> new HashMap<>())
                      .computeIfAbsent(supplierId, k -> new ArrayList<>())
                      .add(itemId);
        }
    }

    if (!hasSelections) {
        throw new IllegalArgumentException("No rows selected for PO generation.");
    }

    int poCount = 0;
    for (String prId : selections.keySet()) {
    for (String supplierId : selections.get(prId).keySet()) {
        Map<String, List<String>> approvedItemsByPR = selections.get(prId); // gets map of supplier -> list<itemID>
        generatePurchaseOrdersFromMultiplePRs(supplierId, createdBy, approvedItemsByPR);
        poCount++;
    }
}


    return poCount;
}

    
}

