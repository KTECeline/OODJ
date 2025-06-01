package com.mycompany.owsb.model;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Frame;
import java.awt.GridLayout;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;

/**
 * Represents a Purchase Manager user with functionalities to view items, suppliers,
 * purchase requisitions, generate purchase orders, and manage (edit/delete) purchase orders.
 */
public class PurchaseManager extends Manager implements ManageItemInterface, ManagePOInterface {

    private static final String PURCHASE_ORDER_FILE = "data/purchase_order.txt";
    private static final String PURCHASE_REQUISITION_FILE = "data/purchase_requisition.txt";
    private static final String ITEMS_FILE = "data/items.txt";
    private static final String SUPPLIERS_FILE = "data/suppliers.txt";
    private static final String AUDITPO_FILE = "data/audit_log.txt";

    private final User loggedInUser;

    public PurchaseManager(User loggedInUser) {
        super(loggedInUser);
        this.loggedInUser = loggedInUser;

    }

    public boolean isAllowedToPerform(String action) {
        // Check if user is logged in and role matches "Sales"


        if (getLoggedInUser() == null || getLoggedInUser().getRole() == null || 
            !(getLoggedInUser().getRole().equalsIgnoreCase("Purchase Manager") ||
            getLoggedInUser().getRole().equalsIgnoreCase("Administrator") ||
            getLoggedInUser().getRole().equalsIgnoreCase("Root Administrator"))) {
            return false;
        }

        // Create dialog for password input
        JDialog dialog = new JDialog((Frame) null, "Password Verification", true);
        dialog.setLayout(new BorderLayout());
        dialog.setBackground(Color.white);

        JPanel panel = new JPanel(new GridLayout(2, 2, 5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        panel.setBackground(Color.white);

        JLabel passwordLabel = new JLabel("Enter Password:");
        JPasswordField passwordField = new JPasswordField(20);
        JLabel errorLabel = new JLabel();
        errorLabel.setForeground(Color.RED);

        panel.add(passwordLabel);
        panel.add(passwordField);
        panel.add(new JLabel());
        panel.add(errorLabel);

        JButton submit = new JButton("Submit");
        JButton cancel = new JButton("Cancel");
        submit.setBackground(Color.red);
        submit.setForeground(Color.white);
        cancel.setBackground(Color.black);
        cancel.setForeground(Color.white);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.white);
        buttonPanel.add(submit);
        buttonPanel.add(cancel);

        dialog.add(panel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.pack();
        dialog.setLocationRelativeTo(null);

        final boolean[] isAuthenticated = {false};

        submit.addActionListener(e -> {
            errorLabel.setText("");
            String enteredPassword = new String(passwordField.getPassword()).trim();
            if (enteredPassword.isEmpty()) {
                errorLabel.setText("*Password is required.");
            } else if (getLoggedInUser().getPassword().equals(enteredPassword)) {
                isAuthenticated[0] = true;
                dialog.dispose();
            } else {
                errorLabel.setText("*Incorrect password.");
            }
        });

        cancel.addActionListener(e -> dialog.dispose());

        dialog.setVisible(true);

        return isAuthenticated[0];
    }

    /**
     * Generates a single Purchase Order consolidating all items from a Purchase Requisition.
     * @param prId The ID of the Purchase Requisition.
     * @param supplierId The ID of the supplier for the PO.
     * @param createdBy The ID of the Purchase Manager creating the PO.
     * @return The generated PurchaseOrder object.
     */

   public List<PurchaseOrder> generatePurchaseOrdersFromMultiplePRs(
            String supplierId, 
            String createdBy, 
            List<PurchaseRequestItemGroup> prGroups) {

        // Input validation
        if (supplierId == null || createdBy == null || prGroups == null || prGroups.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Invalid input: supplier, creator, or PRs missing", "Error", JOptionPane.ERROR_MESSAGE);
            throw new IllegalArgumentException("Invalid input provided");
        }

        Supplier supplier = Supplier.findById(supplierId);
        if (supplier == null) {
            JOptionPane.showMessageDialog(null, "Supplier not found: " + supplierId, "Error", JOptionPane.ERROR_MESSAGE);
            throw new IllegalArgumentException("Supplier not found");
        }

        List<SupplierItem> supplierItems = SupplierItem.loadSupplierItems();
        List<PurchaseOrder> generatedPOs = new ArrayList<>();
        AuditLog auditLog = new AuditLog();

        // Process each PR group
        for (PurchaseRequestItemGroup prGroup : prGroups) {
            String prId = prGroup.getPrId();
            List<String> approvedItemIds = prGroup.getItemIds();

            PurchaseRequisition pr = PurchaseRequisition.findById(prId);
            if (pr == null) {
                JOptionPane.showMessageDialog(null, "Purchase Requisition not found: " + prId, "Error", JOptionPane.ERROR_MESSAGE);
                throw new IllegalArgumentException("Purchase Requisition not found: " + prId);
            }

            if (!pr.getStatus().equalsIgnoreCase("PENDING")) {
                JOptionPane.showMessageDialog(null, "Only pending requisitions can be converted to POs: " + prId, "Error", JOptionPane.ERROR_MESSAGE);
                throw new IllegalStateException("Only pending requisitions can be converted to POs");
            }

            // Filter PR items for approved items
            List<PurchaseRequisitionItem> prItems = PurchaseRequisitionItem.loadPurchaseRequisitionItems()
                    .stream()
                    .filter(item -> item.getPrID().equalsIgnoreCase(prId) && approvedItemIds.contains(item.getItemID()))
                    .toList();

            if (prItems.isEmpty()) {
                continue; // Skip if no approved items for this PR
            }

            // Check if a PO already exists for this PR
            String poId = findExistingPOId(prId);
            if (poId == null) {
                poId = PurchaseOrder.generateNewOrderId();
            }

            PurchaseOrder po = new PurchaseOrder(poId, supplierId, new SimpleDateFormat("yyyy-MM-dd").format(new Date()), "PENDING", prId, createdBy);
            StringBuilder itemDetails = new StringBuilder();
            boolean atLeastOneApproved = false;

            // Load existing PO entries to check for duplicates
            Set<String> existingPoItems = new HashSet<>();
            try (BufferedReader reader = new BufferedReader(new FileReader(PURCHASE_ORDER_FILE))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length >= 8 && parts[7].equalsIgnoreCase(prId)) {
                        existingPoItems.add(parts[1]); // Item ID
                    }
                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Failed to read purchase orders: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                throw new RuntimeException("Failed to read purchase orders", e);
            }

            // Get all PR items for this PR to identify unprocessed items
            List<PurchaseRequisitionItem> allPrItemsForPR = PurchaseRequisitionItem.loadPurchaseRequisitionItems()
                    .stream()
                    .filter(item -> item.getPrID().equalsIgnoreCase(prId))
                    .toList();

            for (PurchaseRequisitionItem prItem : prItems) {
                String itemId = prItem.getItemID();

                // Check for duplicate PR+Item ID
                if (existingPoItems.contains(itemId)) {
                    // Find unprocessed items in the same PR
                    List<String> unprocessedItems = allPrItemsForPR.stream()
                            .map(PurchaseRequisitionItem::getItemID)
                            .filter(id -> !existingPoItems.contains(id))
                            .collect(Collectors.toList());
                    
                    String unprocessedItemsStr = unprocessedItems.isEmpty() ? "None" : String.join(", ", unprocessedItems);
                    JOptionPane.showMessageDialog(null,
                            "Item " + itemId + " for PR " + prId + " is already generated but still pending. " +
                            "Unprocessed items in PR " + prId + ": " + unprocessedItemsStr,
                            "Duplicate PO Attempt", JOptionPane.WARNING_MESSAGE);
                    continue; // Skip this item
                }

                Item item = Item.findById(itemId);
                if (item == null) {
                    JOptionPane.showMessageDialog(null, "Item not found: " + itemId, "Error", JOptionPane.ERROR_MESSAGE);
                    throw new IllegalArgumentException("Item not found: " + itemId);
                }

                // Verify supplier supplies this item
                boolean isSupplied = supplierItems.stream()
                        .anyMatch(si -> si.getSupplierID().equalsIgnoreCase(supplierId) && 
                                        si.getItemID().equalsIgnoreCase(itemId));
                if (!isSupplied) {
                    JOptionPane.showMessageDialog(null, "Supplier " + supplierId + " does not supply item " + itemId, "Error", JOptionPane.ERROR_MESSAGE);
                    throw new IllegalArgumentException("Supplier does not supply item: " + itemId);
                }

                // Create PO item with PENDING status
                double totalPrice = prItem.getQuantity() * item.getCost();
                PurchaseOrder.PurchaseOrderItem poItem = new PurchaseOrder.PurchaseOrderItem(itemId, prItem.getQuantity(), totalPrice);
                poItem.setPrId(prId);
                po.addItem(poItem);
                atLeastOneApproved = true;
                itemDetails.append(itemId).append(":Qty=").append(prItem.getQuantity()).append(";");
            }

            if (atLeastOneApproved) {
                // Save PO to file with PENDING status for each line
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(PURCHASE_ORDER_FILE, true))) {
                    for (PurchaseOrder.PurchaseOrderItem poItem : po.getItems()) {
                        String line = String.format("%s,%s,%s,%d,%.1f,%s,%s,%s,%s",
                                po.getOrderID(),
                                poItem.getItemID(),
                                po.getSupplierID(),
                                poItem.getQuantity(),
                                poItem.getTotalPrice(),
                                po.getOrderDate(),
                                "PENDING", // PO item status is PENDING
                                po.getPrId(),
                                po.getCreatedBy());
                        writer.write(line);
                        writer.newLine();
                    }
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(null, "Failed to save purchase order: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    throw new RuntimeException("Failed to save purchase order", e);
                }

                generatedPOs.add(po);
                auditLog.logPOCreation(loggedInUser.getUsername(), loggedInUser.getRole(), poId, prId, supplierId, itemDetails.toString());

                // Check if all PR items have corresponding PO entries
                Set<String> prItemIds = new HashSet<>();
                for (PurchaseRequisitionItem item : allPrItemsForPR) {
                    prItemIds.add(item.getItemID());
                }

                Set<String> poItemIds = new HashSet<>();
                try (BufferedReader reader = new BufferedReader(new FileReader(PURCHASE_ORDER_FILE))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        String[] parts = line.split(",");
                        if (parts.length >= 8 && parts[7].equalsIgnoreCase(prId)) {
                            poItemIds.add(parts[1]); // Item ID
                        }
                    }
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(null, "Failed to read purchase orders: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    throw new RuntimeException("Failed to read purchase orders", e);
                }

                if (prItemIds.equals(poItemIds)) {
                    pr.setStatus("APPROVED");
                    PurchaseRequisition.update(pr);
                }
            }
        }

        

        return generatedPOs;
    }
      

public static String findExistingPOId(String prId) {
    try (BufferedReader reader = new BufferedReader(new FileReader(PURCHASE_ORDER_FILE))) {
        String line;
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(",");
            if (parts.length >= 8) {
                String existingPoId = parts[0];
                String existingPrId = parts[7];

                if (existingPrId.equalsIgnoreCase(prId)) {
                    return existingPoId; // Found an existing PO ID for the PR
                }
            }
        }
    } catch (IOException e) {
        e.printStackTrace();
    }
    return null; // Not found
}

    /**
     * Deletes a Purchase Order if it is in PENDING status.
     * @param poId The ID of the Purchase Order to delete.
     */
    public void deletePurchaseOrderItem(String poId, String itemId) {
        if (!isAllowedToPerform("delete PurchaseOrder")) {
            throw new IllegalStateException("Authentication failed for delete PurchaseOrder");
        }

        List<String> lines = new ArrayList<>();
        boolean found = false;
        String prId = null;
        boolean isPending = true;
        boolean hasRemainingItems = false;

        // Read purchase_order.txt and filter out the specific item
        try (BufferedReader reader = new BufferedReader(new FileReader(PURCHASE_ORDER_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 9 && parts[0].equalsIgnoreCase(poId) && parts[1].equalsIgnoreCase(itemId)) {
                    found = true;
                    prId = parts[7]; // PR ID
                    if (!parts[6].equalsIgnoreCase("PENDING")) {
                        isPending = false;
                    }
                } else {
                    lines.add(line);
                    if (parts.length >= 9 && parts[0].equalsIgnoreCase(poId)) {
                        hasRemainingItems = true;
                    }
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Failed to read purchase orders.", "Error", JOptionPane.ERROR_MESSAGE);
            throw new RuntimeException("Failed to read purchase orders", e);
        }

        if (!found) {
            JOptionPane.showMessageDialog(null, "Purchase Order item not found: PO " + poId + ", Item " + itemId, "Error", JOptionPane.ERROR_MESSAGE);
            throw new IllegalArgumentException("Purchase Order item not found");
        }

        if (!isPending) {
            JOptionPane.showMessageDialog(null, "Only pending order items can be deleted.", "Error", JOptionPane.ERROR_MESSAGE);
            throw new IllegalStateException("Only pending order items can be deleted");
        }

        // Write back remaining lines
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(PURCHASE_ORDER_FILE))) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Failed to save purchase orders.", "Error", JOptionPane.ERROR_MESSAGE);
            throw new RuntimeException("Failed to save purchase orders", e);
        }

        AuditLog auditLog = new AuditLog();
        auditLog.logPODeletion(loggedInUser.getUsername(), loggedInUser.getRole(), poId, itemId);

        // Update PR status if no items remain
        if (!hasRemainingItems && prId != null) {
            PurchaseRequisition pr = PurchaseRequisition.findById(prId);
            if (pr != null) {
                pr.setStatus("PENDING");
                PurchaseRequisition.update(pr);
            }
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
        PurchaseOrder.updatePOTableInUI(getAllPurchaseOrders(), getAllRequisitions(), targetTable);
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
        PurchaseOrder.searchAndDisplayPO(searchField, table, getAllPurchaseOrders(), getAllRequisitions());
    }

    @Override
    public List<PurchaseOrder> searchPOs(String poId) {
        return PurchaseOrder.loadPurchaseOrders().stream()
                .filter(po -> po.getOrderID().toLowerCase().contains(poId.toLowerCase()))
                .toList();
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

    public Stats getSummaryStats() {
    int totalItems = getAllItems().size();
    int totalSuppliers = getAllSuppliers().size();

    int pendingPOs = getOrdersByStatus("PENDING").size();
    long pendingPRs = 0;
    for (PurchaseRequisition pr : getAllRequisitions()) {
        if (pr.getStatus().equalsIgnoreCase("PENDING")) {
            pendingPRs++;
        }
    }

    String username = loggedInUser.getUsername();

    return new Stats(totalItems, totalSuppliers, pendingPRs, pendingPOs, username);
}

    public List<PurchaseRequisition> getFilteredRequisitions(String statusFilter) {
    List<PurchaseRequisition> allPRs = getAllRequisitions(); 
    List<PurchaseRequisitionItem> allItems = PurchaseRequisitionItem.loadPurchaseRequisitionItems();

    // Attach items to PRs
    for (PurchaseRequisition pr : allPRs) {
        List<PurchaseRequisitionItem> itemsForPR = new ArrayList<>();
        for (PurchaseRequisitionItem item : allItems) {
            if (item.getPrID().equalsIgnoreCase(pr.getPrID())) {
                itemsForPR.add(item);
            }
        }
        pr.setPRItems(itemsForPR);
    }

    if ("ALL".equalsIgnoreCase(statusFilter)) {
        return allPRs;
    }

    List<PurchaseRequisition> filtered = new ArrayList<>();
    for (PurchaseRequisition pr : allPRs) {
        if (statusFilter.equalsIgnoreCase(pr.getStatus())) {
            filtered.add(pr);
        }
    }

    return filtered;
}


    public void performSearchOrFilter(JTextField searchField, JComboBox<String> Filter, JTable PrTable) {
    String searchQuery = searchField.getText().trim();

    if (searchQuery.isEmpty() || "Enter PR ID".equalsIgnoreCase(searchQuery)) {
        String selectedStatus = Filter.getSelectedItem().toString();
        loadViewPR(selectedStatus, PrTable);
    } else {
        List<PurchaseRequisition> allPRs = getAllRequisitions();
        List<PurchaseRequisitionItem> prItems = PurchaseRequisitionItem.loadPurchaseRequisitionItems();
        List<Item> items = getAllItems();

        for (PurchaseRequisition pr : allPRs) {
            List<PurchaseRequisitionItem> itemsForPR = new ArrayList<>();
            for (PurchaseRequisitionItem item : prItems) {
                if (item.getPrID().equalsIgnoreCase(pr.getPrID())) {
                    itemsForPR.add(item);
                }
            }
            pr.setPRItems(itemsForPR);
        }

        PurchaseRequisition.searchAndDisplayPRInTable(searchField, PrTable, allPRs, items, prItems);
    }
}


    public void loadViewPR(String statusFilter, JTable targetTable) {
    List<PurchaseRequisition> filteredPRs = getFilteredRequisitions(statusFilter);
    List<PurchaseRequisitionItem> prItems = PurchaseRequisitionItem.loadPurchaseRequisitionItems();
    List<Item> items = getAllItems();

    PurchaseRequisition.updatePRTableInUI(filteredPRs, prItems, items, targetTable);
}


    public void updatePurchaseOrderItem(String poId, String itemId, String newSupplierId, int newQuantity, double newTotalPrice, String newStatus) {
    if (!isAllowedToPerform("updatePurchaseOrderItem")) {
        throw new IllegalStateException("Authentication failed for updatePurchaseOrderItem");
    }

    List<String> lines = new ArrayList<>();
    boolean found = false;
    String orderDate = null;
    String prId = null;
    String createdBy = null;
    String originalSupplierId = null;
    String originalStatus = null;

    // Validate supplier manually
    List<SupplierItem> supplierItems = SupplierItem.loadSupplierItems();
    boolean isValidSupplier = false;
    for (SupplierItem si : supplierItems) {
        if (si.getSupplierID().equalsIgnoreCase(newSupplierId) && si.getItemID().equalsIgnoreCase(itemId)) {
            isValidSupplier = true;
            break;
        }
    }
    if (!isValidSupplier) {
        JOptionPane.showMessageDialog(null, "Supplier " + newSupplierId + " does not supply item " + itemId, "Error", JOptionPane.ERROR_MESSAGE);
        throw new IllegalArgumentException("Invalid supplier for item");
    }

    // Validate status
    String[] allowedStatuses = {"PENDING", "APPROVED", "REJECTED", "RECEIVED", "UNFULFILLED", "COMPLETED", "VERIFIED"};
    boolean validStatus = false;
    for (String s : allowedStatuses) {
        if (s.equalsIgnoreCase(newStatus)) {
            validStatus = true;
            break;
        }
    }
    if (!validStatus) {
        JOptionPane.showMessageDialog(null, "Invalid status: " + newStatus, "Error", JOptionPane.ERROR_MESSAGE);
        throw new IllegalArgumentException("Invalid status");
    }

    try (BufferedReader reader = new BufferedReader(new FileReader(PURCHASE_ORDER_FILE))) {
        String line;
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(",");
            if (parts.length >= 9 && parts[0].equalsIgnoreCase(poId) && parts[1].equalsIgnoreCase(itemId)) {
                found = true;
                orderDate = parts[5];
                originalStatus = parts[6];
                prId = parts[7];
                createdBy = parts[8];
                originalSupplierId = parts[2];

                lines.add(poId + "," + itemId + "," + newSupplierId + "," + newQuantity + "," + newTotalPrice + "," + orderDate + "," + newStatus + "," + prId + "," + createdBy);
            } else {
                lines.add(line);
            }
        }
    } catch (IOException e) {
        JOptionPane.showMessageDialog(null, "Failed to read purchase orders.", "Error", JOptionPane.ERROR_MESSAGE);
        throw new RuntimeException("Failed to read purchase orders", e);
    }

    if (!found) {
        JOptionPane.showMessageDialog(null, "Purchase Order item not found: PO " + poId + ", Item " + itemId, "Error", JOptionPane.ERROR_MESSAGE);
        throw new IllegalArgumentException("Purchase Order item not found");
    }

    try (BufferedWriter writer = new BufferedWriter(new FileWriter(PURCHASE_ORDER_FILE))) {
        for (String line : lines) {
            writer.write(line);
            writer.newLine();
        }
    } catch (IOException e) {
        JOptionPane.showMessageDialog(null, "Failed to save purchase orders.", "Error", JOptionPane.ERROR_MESSAGE);
        throw new RuntimeException("Failed to save purchase orders", e);
    }

  if (!originalStatus.equals(newStatus)) {
            AuditLog auditLog = new AuditLog();
        String action = "PO Item Update/Edit changed by Purchase Manager";
        String details = poId + "," + itemId + "," + originalStatus + "->" + newStatus;
        auditLog.logAction(loggedInUser.getUsername(), loggedInUser.getRole(), action, details);

        }

    // Conditional PR update
    if ((newStatus.equalsIgnoreCase("REJECTED") || newStatus.equalsIgnoreCase("UNFULFILLED"))
        && !newSupplierId.equalsIgnoreCase(originalSupplierId) && prId != null) {
        PurchaseRequisition pr = PurchaseRequisition.findById(prId);
        if (pr != null) {
            pr.setStatus("PENDING");
            PurchaseRequisition.update(pr);
        }
    }
}

    @Override
    public List<PurchaseOrder> generatePO(String supplierId, String createdBy, String prId, List<String> itemIds) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void editPOItem(String poId, String itemId, String newSupplierId, int newQuantity, double newTotalPrice, String newStatus) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void deletePOItem(String poId, String itemId) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public List<PurchaseOrder> viewPOs() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
