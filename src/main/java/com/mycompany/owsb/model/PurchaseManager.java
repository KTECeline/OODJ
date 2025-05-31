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
import java.util.List;
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

    /*public PurchaseOrder generatePurchaseOrders(String prId, String supplierId, String createdBy, List<String> approvedItemIds) {
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
}*/



    /*public List<PurchaseOrder> generatePurchaseOrdersFromMultiplePRs(String supplierId, String createdBy, Map<String, List<String>> approvedItemsByPR) {
        if (!isAllowedToPerform("generatePurchaseOrdersFromMultiplePRs")) {
            throw new IllegalStateException("Authentication failed for generatePurchaseOrdersFromMultiplePRs");
        }
        
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
            String poId = findExistingPOId(prId);
            if (poId == null) {
                poId = PurchaseOrder.generateNewOrderId();
            }

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
}*/
    
   /* public List<PurchaseOrder> generatePurchaseOrdersFromMultiplePRs(
        String supplierId, 
        String createdBy, 
        List<PurchaseRequestItemGroup> prGroups) {

    // Authentication check
    if (!isAllowedToPerform("generatePurchaseOrdersFromMultiplePRs")) {
        throw new IllegalStateException("Authentication failed for generatePurchaseOrdersFromMultiplePRs");
    }

    // Basic input validation
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

    // Loop over each PurchaseRequestItemGroup (PR + its item IDs)
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

        // Load all PR items and find those that match prId and item IDs
        List<PurchaseRequisitionItem> allItems = PurchaseRequisitionItem.loadPurchaseRequisitionItems();
        List<PurchaseRequisitionItem> matchedItems = new ArrayList<>();
        
        for (PurchaseRequisitionItem item : allItems) {
            if (item.getPrID().equalsIgnoreCase(prId) && approvedItemIds.contains(item.getItemID())) {
                matchedItems.add(item);
            }
        }

        if (matchedItems.isEmpty()) {
            continue; // no items approved for this PR, skip to next
        }

        List<PurchaseOrder.PurchaseOrderItem> poItems = new ArrayList<>();
        boolean atLeastOneApproved = false;

        for (PurchaseRequisitionItem prItem : matchedItems) {
            Item item = Item.findById(prItem.getItemID());
            if (item == null) {
                JOptionPane.showMessageDialog(null, "Item not found: " + prItem.getItemID(), "Error", JOptionPane.ERROR_MESSAGE);
                throw new IllegalArgumentException("Item not found: " + prItem.getItemID());
            }

            // Check if supplier supplies this item
            boolean isSupplied = false;
            for (SupplierItem si : supplierItems) {
                if (si.getSupplierID().equalsIgnoreCase(supplierId) && si.getItemID().equalsIgnoreCase(prItem.getItemID())) {
                    isSupplied = true;
                    break;
                }
            }

            if (!isSupplied) {
                JOptionPane.showMessageDialog(null, "Supplier " + supplierId + " does not supply item " + prItem.getItemID(), "Error", JOptionPane.ERROR_MESSAGE);
                throw new IllegalArgumentException("Supplier does not supply item: " + prItem.getItemID());
            }

            double totalPrice = prItem.getQuantity() * item.getCost();
            PurchaseOrder.PurchaseOrderItem poItem = new PurchaseOrder.PurchaseOrderItem(prItem.getItemID(), prItem.getQuantity(), totalPrice);
            poItem.setPrId(prId);
            poItems.add(poItem);
            atLeastOneApproved = true;
        }

        if (atLeastOneApproved) {
            // Check if PO already exists for this PR; if not generate a new one
            String poId = findExistingPOId(prId);
            if (poId == null) {
                poId = PurchaseOrder.generateNewOrderId();
            }

            String orderDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
            PurchaseOrder po = new PurchaseOrder(poId, supplierId, orderDate, "PENDING", prId, createdBy);

            for (PurchaseOrder.PurchaseOrderItem poItem : poItems) {
                po.addItem(poItem);
            }

            // Save PO to file
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(PURCHASE_ORDER_FILE, true))) {
                for (PurchaseOrder.PurchaseOrderItem poItem : po.getItems()) {
                    String line = String.format("%s,%s,%s,%d,%.1f,%s,%s,%s,%s",
                            po.getOrderID(),
                            poItem.getItemID(),
                            po.getSupplierID(),
                            poItem.getQuantity(),
                            poItem.getTotalPrice(),
                            po.getOrderDate(),
                            po.getStatus(),
                            po.getPrId(),
                            po.getCreatedBy()
                    );
                    writer.write(line);
                    writer.newLine();
                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Failed to save purchase order: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                throw new RuntimeException("Failed to save purchase order", e);
            }

            // Update PR status
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
} */
    
    public List<PurchaseOrder> generatePurchaseOrdersFromMultiplePRs(
        String supplierId, 
        String createdBy, 
        List<PurchaseRequestItemGroup> prGroups) {

        if (!isAllowedToPerform("generatePurchaseOrdersFromMultiplePRs")) {
            throw new IllegalStateException("Authentication failed for generatePurchaseOrdersFromMultiplePRs");
        }

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

            List<PurchaseRequisitionItem> allItems = PurchaseRequisitionItem.loadPurchaseRequisitionItems();
            List<PurchaseRequisitionItem> matchedItems = new ArrayList<>();

            for (PurchaseRequisitionItem item : allItems) {
                if (item.getPrID().equalsIgnoreCase(prId) && approvedItemIds.contains(item.getItemID())) {
                    matchedItems.add(item);
                }
            }

            if (matchedItems.isEmpty()) {
                continue;
            }

            List<PurchaseOrder.PurchaseOrderItem> poItems = new ArrayList<>();
            boolean atLeastOneApproved = false;
            StringBuilder itemDetails = new StringBuilder();

            for (PurchaseRequisitionItem prItem : matchedItems) {
                Item item = Item.findById(prItem.getItemID());
                if (item == null) {
                    JOptionPane.showMessageDialog(null, "Item not found: " + prItem.getItemID(), "Error", JOptionPane.ERROR_MESSAGE);
                    throw new IllegalArgumentException("Item not found: " + prItem.getItemID());
                }

                boolean isSupplied = false;
                for (SupplierItem si : supplierItems) {
                    if (si.getSupplierID().equalsIgnoreCase(supplierId) && si.getItemID().equalsIgnoreCase(prItem.getItemID())) {
                        isSupplied = true;
                        break;
                    }
                }

                if (!isSupplied) {
                    JOptionPane.showMessageDialog(null, "Supplier " + supplierId + " does not supply item " + prItem.getItemID(), "Error", JOptionPane.ERROR_MESSAGE);
                    throw new IllegalArgumentException("Supplier does not supply item: " + prItem.getItemID());
                }

                double totalPrice = prItem.getQuantity() * item.getCost();
                PurchaseOrder.PurchaseOrderItem poItem = new PurchaseOrder.PurchaseOrderItem(prItem.getItemID(), prItem.getQuantity(), totalPrice);
                poItem.setPrId(prId);
                poItems.add(poItem);
                atLeastOneApproved = true;
                itemDetails.append(prItem.getItemID()).append(":Qty=").append(prItem.getQuantity()).append(";");
            }

            if (atLeastOneApproved) {
                String poId = findExistingPOId(prId);
                if (poId == null) {
                    poId = PurchaseOrder.generateNewOrderId();
                }

                String orderDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
                PurchaseOrder po = new PurchaseOrder(poId, supplierId, orderDate, "PENDING", prId, createdBy);

                for (PurchaseOrder.PurchaseOrderItem poItem : poItems) {
                    po.addItem(poItem);
                }

                try (BufferedWriter writer = new BufferedWriter(new FileWriter(PURCHASE_ORDER_FILE, true))) {
                    for (PurchaseOrder.PurchaseOrderItem poItem : po.getItems()) {
                        String line = String.format("%s,%s,%s,%d,%.1f,%s,%s,%s,%s",
                                po.getOrderID(),
                                poItem.getItemID(),
                                po.getSupplierID(),
                                poItem.getQuantity(),
                                poItem.getTotalPrice(),
                                po.getOrderDate(),
                                po.getStatus(),
                                po.getPrId(),
                                po.getCreatedBy()
                        );
                        writer.write(line);
                        writer.newLine();
                    }
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(null, "Failed to save purchase order: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    throw new RuntimeException("Failed to save purchase order", e);
                }

                pr.setStatus("APPROVED");
                PurchaseRequisition.update(pr);
                generatedPOs.add(po);

                // Log PO creation
                auditLog.logPOCreation(loggedInUser.getUsername(), loggedInUser.getRole(), poId, prId, supplierId, itemDetails.toString());
            }
        }

        if (generatedPOs.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No approved items found in any PR.", "Error", JOptionPane.ERROR_MESSAGE);
            throw new IllegalArgumentException("No approved items");
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
     * Edits an existing Purchase Order's item quantity or supplier.
     * @param poId The ID of the Purchase Order to edit.
     * @param itemId The ID of the item to edit.
     * @param newQuantity The new quantity for the item.
     * @param newSupplierId The new supplier ID.
     * @return The updated PurchaseOrder object.
     */
    /*public PurchaseOrder editPurchaseOrder(String poId, String itemId, int newQuantity, String newSupplierId) {
        
        if (!isAllowedToPerform("editPurchaseOrder")) {
            throw new IllegalStateException("Authentication failed for editPurchaseOrder");
        }
        
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
    } */

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
  
