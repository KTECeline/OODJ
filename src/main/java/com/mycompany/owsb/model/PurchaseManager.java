package com.mycompany.owsb.model;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Frame;
import java.awt.GridLayout;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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
 * Manages purchase orders and requisitions, implementing business logic for the Purchase Order Management System.
 */
public class PurchaseManager extends Manager implements ManagePOInterface {
    private static final String AUDITPO_FILE = "data/audit_log.txt";
    private static final String PURCHASE_REQUISITION_FILE = "data/purchase_requisition.txt";
    
    private final User loggedInUser;
    private final Purchase_DataAcess dataAccess;
    private final Purchase_GuiUpdater guiUpdater;

    public PurchaseManager(User loggedInUser) {
        super(loggedInUser);
        this.loggedInUser = loggedInUser;
        this.dataAccess = new Purchase_DataAcess();
        this.guiUpdater = new Purchase_GuiUpdater();
    }

    /**
     * Checks if the user is authorized to perform an action.
     * @param action The action to authorize.
     * @return True if authorized, false otherwise.
     */
    public boolean isAllowedToPerform(String action) {
        if (getLoggedInUser() == null || getLoggedInUser().getRole() == null ||
            !(getLoggedInUser().getRole().equalsIgnoreCase("Purchase Manager") ||
              getLoggedInUser().getRole().equalsIgnoreCase("Administrator") ||
              getLoggedInUser().getRole().equalsIgnoreCase("Root Administrator"))) {
            return false;
        }
        
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

    @Override
    public List<PurchaseOrder> generatePurchaseOrders(String supplierId, String createdBy, List<PurchaseRequestItemGroup> prGroups) {
        return generatePurchaseOrdersFromMultiplePRs(supplierId, createdBy, prGroups);
    }

    /**
     * Generates POs from multiple PRs.
     */
    public List<PurchaseOrder> generatePurchaseOrdersFromMultiplePRs(String supplierId, String createdBy, List<PurchaseRequestItemGroup> prGroups) {
        validateInputs(supplierId, createdBy, prGroups);
        Supplier supplier = getSupplierOrThrow(supplierId);
        List<SupplierItem> supplierItems = SupplierItem.loadSupplierItems();
        List<PurchaseOrder> generatedPOs = new ArrayList<>();
        AuditLog auditLog = new AuditLog();

        for (PurchaseRequestItemGroup prGroup : prGroups) {
            PurchaseOrder po = processSinglePRGroup(prGroup, supplierId, createdBy, supplierItems, auditLog);
            if (po != null) {
                generatedPOs.add(po);
            }
        }
        return generatedPOs;
    }

    private void validateInputs(String supplierId, String createdBy, List<PurchaseRequestItemGroup> prGroups) {
        if (supplierId == null || createdBy == null || prGroups == null || prGroups.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Invalid input: supplier, creator, or PRs missing", "Error", JOptionPane.ERROR_MESSAGE);
            throw new IllegalArgumentException("Invalid input provided");
        }
    }

    private Supplier getSupplierOrThrow(String supplierId) {
        Supplier supplier = Supplier.findById(supplierId);
        if (supplier == null) {
            JOptionPane.showMessageDialog(null, "Supplier not found: " + supplierId, "Error", JOptionPane.ERROR_MESSAGE);
            throw new IllegalArgumentException("Supplier not found");
        }
        return supplier;
    }

    private PurchaseOrder processSinglePRGroup(PurchaseRequestItemGroup prGroup, String supplierId, 
            String createdBy, List<SupplierItem> supplierItems, AuditLog auditLog) {
        String prId = prGroup.getPrId();
        List<String> approvedItemIds = prGroup.getItemIds();
        PurchaseRequisition pr = validatePR(prId);
        List<PurchaseRequisitionItem> allPrItems = getAllItemsForPR(prId);
        List<PurchaseRequisitionItem> selectedItems = getApprovedItems(prId, approvedItemIds);

        if (selectedItems.isEmpty()) return null;

        String poId = Optional.ofNullable(dataAccess.findExistingPOId(prId)).orElse(PurchaseOrder.generateNewOrderId());
        PurchaseOrder po = new PurchaseOrder(poId, supplierId, new SimpleDateFormat("yyyy-MM-dd").format(new Date()), prId, createdBy);
        StringBuilder itemDetails = new StringBuilder();
        Set<String> existingPoItems = getExistingPOItemsForPR(prId);

        boolean hasNewItems = addValidItemsToPO(po, selectedItems, allPrItems, existingPoItems, supplierItems, itemDetails, supplierId, prId);

        if (hasNewItems) {
            dataAccess.appendPurchaseOrder(po);
            auditLog.logPOCreation(loggedInUser.getUsername(), loggedInUser.getRole(), poId, prId, supplierId, itemDetails.toString());
            updatePRStatusIfFullyProcessed(pr, allPrItems);
            return po;
        }
        return null;
    }

    private PurchaseRequisition validatePR(String prId) {
        PurchaseRequisition pr = PurchaseRequisition.findById(prId);
        if (pr == null) {
            JOptionPane.showMessageDialog(null, "Purchase Requisition not found: " + prId, "Error", JOptionPane.ERROR_MESSAGE);
            throw new IllegalArgumentException("PR not found");
        }
        if (!pr.getStatus().equalsIgnoreCase("PENDING")) {
            JOptionPane.showMessageDialog(null, "Only pending requisitions can be converted to POs: " + prId, "Error", JOptionPane.ERROR_MESSAGE);
            throw new IllegalStateException("PR not pending");
        }
        return pr;
    }

    private List<PurchaseRequisitionItem> getApprovedItems(String prId, List<String> approvedItemIds) {
        List<PurchaseRequisitionItem> allItems = PurchaseRequisitionItem.loadPurchaseRequisitionItems();
        List<PurchaseRequisitionItem> result = new ArrayList<>();
        for (PurchaseRequisitionItem item : allItems) {
            if (item.getPrID().equalsIgnoreCase(prId) && approvedItemIds.contains(item.getItemID())) {
                result.add(item);
            }
        }
        return result;
    }

    private List<PurchaseRequisitionItem> getAllItemsForPR(String prId) {
        List<PurchaseRequisitionItem> allItems = PurchaseRequisitionItem.loadPurchaseRequisitionItems();
        List<PurchaseRequisitionItem> result = new ArrayList<>();
        for (PurchaseRequisitionItem item : allItems) {
            if (item.getPrID().equalsIgnoreCase(prId)) {
                result.add(item);
            }
        }
        return result;
    }

    private Set<String> getExistingPOItemsForPR(String prId) {
        Set<String> existingItems = new HashSet<>();
        List<String> lines = dataAccess.readPurchaseOrders();
        for (String line : lines) {
            String[] parts = line.split(",");
            if (parts.length >= 8 && parts[7].equalsIgnoreCase(prId)) {
                existingItems.add(parts[1]);
            }
        }
        return existingItems;
    }
    
     private void showDuplicateWarning(String itemId, String prId, List<PurchaseRequisitionItem> allItems, Set<String> existingPoItems) {
        List<String> unprocessed = new ArrayList<>();
        for (PurchaseRequisitionItem item : allItems) {
            String id = item.getItemID();
            if (!existingPoItems.contains(id)) {
                unprocessed.add(id);
            }
        }
        String msg = "Item " + itemId + " for PR " + prId + " is already generated but still pending. " +
                     "Unprocessed items: " + (unprocessed.isEmpty() ? "None" : String.join(", ", unprocessed));
        JOptionPane.showMessageDialog(null, msg, "Duplicate PO Attempt", JOptionPane.WARNING_MESSAGE);
    }
     
     

    private boolean addValidItemsToPO(PurchaseOrder po, List<PurchaseRequisitionItem> selectedItems, List<PurchaseRequisitionItem> allItems,
                                     Set<String> existingPoItems, List<SupplierItem> supplierItems, StringBuilder itemDetails, String supplierId, String prId) {
        boolean added = false;
        for (PurchaseRequisitionItem prItem : selectedItems) {
            String itemId = prItem.getItemID();
            if (existingPoItems.contains(itemId)) {
                showDuplicateWarning(itemId, prId, allItems, existingPoItems);
                continue;
            }
            Item item = Item.findById(itemId);
            if (item == null) {
                JOptionPane.showMessageDialog(null, "Item not found: " + itemId, "Error", JOptionPane.ERROR_MESSAGE);
                continue;
            }
            if (!isSuppliedBySupplier(itemId, supplierId, supplierItems)) {
                JOptionPane.showMessageDialog(null, "Supplier does not supply item: " + itemId, "Error", JOptionPane.ERROR_MESSAGE);
                continue;
            }
            double totalPrice = prItem.getQuantity() * item.getCost();
            PurchaseOrder.PurchaseOrderItem poItem = new PurchaseOrder.PurchaseOrderItem(itemId, prItem.getQuantity(), totalPrice, "PENDING");
            poItem.setPrId(prId);
            po.addItem(poItem);
            itemDetails.append(itemId).append(":Qty=").append(prItem.getQuantity()).append(";");
            added = true;
        }
        return added;
    }


    private boolean isSuppliedBySupplier(String itemId, String supplierId, List<SupplierItem> supplierItems) {
        for (SupplierItem si : supplierItems) {
            if (si.getItemID().equalsIgnoreCase(itemId) && si.getSupplierID().equalsIgnoreCase(supplierId)) {
                return true;
            }
        }
        return false;
    }

    private void updatePRStatusIfFullyProcessed(PurchaseRequisition pr, List<PurchaseRequisitionItem> allItems) {
        Set<String> prItemIds = new HashSet<>();
        for (PurchaseRequisitionItem item : allItems) {
            prItemIds.add(item.getItemID());
        }
        Set<String> poItemIds = getExistingPOItemsForPR(pr.getPrID());
        if (prItemIds.equals(poItemIds)) {
            pr.setStatus("APPROVED");
            PurchaseRequisition.update(pr);
        }
    }

    @Override
    public void editPOItem(String poId, String itemId, String newSupplierId, 
            int newQuantity, double newTotalPrice, String newStatus) {
        updatePurchaseOrderItem(poId, itemId, newSupplierId, newQuantity, 
                newTotalPrice, newStatus);
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

        List<String> poLines = dataAccess.readPurchaseOrders();
        for (String line : poLines) {
            String[] parts = line.split(",");
            if (parts.length >= 9 && parts[0].equalsIgnoreCase(poId) && parts[1].equalsIgnoreCase(itemId)) {
                found = true;
                orderDate = parts[5];
                originalStatus = parts[6];
                prId = parts[7];
                createdBy = parts[8];
                originalSupplierId = parts[2];
                lines.add(poId + "," + itemId + "," + newSupplierId + "," + newQuantity + "," + newTotalPrice + "," +
                          orderDate + "," + newStatus + "," + prId + "," + createdBy);
            } else {
                lines.add(line);
            }
        }

        if (!found) {
            JOptionPane.showMessageDialog(null, "Purchase Order item not found: PO " + poId + ", Item " + itemId, "Error", JOptionPane.ERROR_MESSAGE);
            throw new IllegalArgumentException("Purchase Order item not found");
        }

        dataAccess.writePurchaseOrders(lines);

        if (!originalStatus.equals(newStatus)) {
            AuditLog auditLog = new AuditLog();
            String action = "PO Item Update/Edit changed by Purchase Manager";
            String details = poId + "," + itemId + "," + originalStatus + "->" + newStatus;
            auditLog.logAction(loggedInUser.getUsername(), loggedInUser.getRole(), action, details);
        }

        if ((newStatus.equalsIgnoreCase("REJECTED") || newStatus.equalsIgnoreCase("UNFULFILLED")) &&
            !newSupplierId.equalsIgnoreCase(originalSupplierId) && prId != null) {
            PurchaseRequisition pr = PurchaseRequisition.findById(prId);
            if (pr != null) {
                pr.setStatus("PENDING");
                PurchaseRequisition.update(pr);
            }
        }
    }

    @Override
    public void deletePOItem(String poId, String itemId) {
        deletePurchaseOrderItem(poId, itemId);
    }


    public void deletePurchaseOrderItem(String poId, String itemId) {
        if (!isAllowedToPerform("delete PurchaseOrder")) {
            throw new IllegalStateException("Authentication failed for delete PurchaseOrder");
        }
        List<String> poLines = new ArrayList<>();
        boolean found = false;
        String prId = null;
        boolean isPending = true;

        // Read PO file and validate PR ID + Item ID
        List<String> lines = dataAccess.readPurchaseOrders();
        for (String line : lines) {
            String[] parts = line.split(",");
            if (parts.length >= 9 && parts[0].equalsIgnoreCase(poId) && parts[1].equalsIgnoreCase(itemId)) {
                found = true;
                prId = parts[7]; // Capture PR ID
                if (!parts[6].equalsIgnoreCase("PENDING")) {
                    isPending = false;
                }
            } else {
                poLines.add(line);
            }
        }

        if (!found) {
            JOptionPane.showMessageDialog(null, "Purchase Order item not found: PO " + poId + ", Item " + itemId, "Error", JOptionPane.ERROR_MESSAGE);
            throw new IllegalArgumentException("Purchase Order item not found");
        }

        if (!isPending) {
            JOptionPane.showMessageDialog(null, "Only pending order items can be deleted.", "Error", JOptionPane.ERROR_MESSAGE);
            throw new IllegalStateException("Only pending order items can be deleted");
        }

        // Write back updated PO file
        dataAccess.writePurchaseOrders(poLines);

        // Log PO deletion
        AuditLog auditLog = new AuditLog();
        auditLog.logPODeletion(loggedInUser.getUsername(), loggedInUser.getRole(), poId, itemId);

        // Update PR status to PENDING if PR ID is valid
        if (prId != null) {
            List<String> prLines = dataAccess.readPurchaseRequisitions();
            List<String> updatedPrLines = new ArrayList<>();
            boolean prFound = false;

            for (String line : prLines) {
                String[] parts = line.split(",", 5);
                if (parts.length >= 5 && parts[0].equalsIgnoreCase(prId)) {
                    prFound = true;
                    updatedPrLines.add(parts[0] + "," + parts[1] + "," + parts[2] + "," + parts[3] + ",PENDING");
                } else {
                    updatedPrLines.add(line);
                }
            }

            if (prFound) {
                dataAccess.writePurchaseRequisitions(updatedPrLines);
                auditLog.logAction(loggedInUser.getUsername(), loggedInUser.getRole(),
                    "PR Status Update", "PR " + prId + " set to PENDING due to PO item deletion");
            } else {
                JOptionPane.showMessageDialog(null, "Purchase Requisition not found: PR " + prId, "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    
    @Override
    public void rejectPurchaseRequisitionItem(String prId, String itemId) {
       
        // Validate PR ID and Item ID in purchase_requisition_item.txt
        boolean itemFound = false;
        List<PurchaseRequisitionItem> prItems = PurchaseRequisitionItem.loadPurchaseRequisitionItems();
        for (PurchaseRequisitionItem item : prItems) {
            if (item.getPrID().equalsIgnoreCase(prId) && item.getItemID().equalsIgnoreCase(itemId)) {
                itemFound = true;
                break;
            }
        }
        if (!itemFound) {
            JOptionPane.showMessageDialog(null, "Item " + itemId + " not found for PR " + prId, "Error", JOptionPane.ERROR_MESSAGE);
            throw new IllegalArgumentException("PR item not found");
        }

        // Check if PR exists and is PENDING
        List<String> prLines = dataAccess.readPurchaseRequisitions();
        boolean prFound = false;
        String prStatus = null;
        for (String line : prLines) {
            String[] parts = line.split(",", 5);
            if (parts.length >= 5 && parts[0].equalsIgnoreCase(prId)) {
                prFound = true;
                prStatus = parts[4];
                break;
            }
        }
        if (!prFound) {
            JOptionPane.showMessageDialog(null, "Purchase Requisition not found: PR " + prId, "Error", JOptionPane.ERROR_MESSAGE);
            throw new IllegalArgumentException("Purchase Requisition not found");
        }
        if (!prStatus.equalsIgnoreCase("PENDING")) {
            JOptionPane.showMessageDialog(null, "Only pending requisitions can be rejected: PR " + prId, "Error", JOptionPane.ERROR_MESSAGE);
            throw new IllegalStateException("PR not pending");
        }

        // Check if PO exists for this PR ID and Item ID
        List<String> poLines = dataAccess.readPurchaseOrders();
        for (String line : poLines) {
            String[] parts = line.split(",");
            if (parts.length >= 9 && parts[7].equalsIgnoreCase(prId) && parts[1].equalsIgnoreCase(itemId)) {
                JOptionPane.showMessageDialog(null, "Cannot reject: PO already exists for PR " + prId + ", Item " + itemId, "Error", JOptionPane.ERROR_MESSAGE);
                throw new IllegalStateException("PO already exists");
            }
        }

        // Update PR status to REJECTED
        List<String> updatedPrLines = new ArrayList<>();
        for (String line : prLines) {
            String[] parts = line.split(",", 5);
            if (parts.length >= 5 && parts[0].equalsIgnoreCase(prId)) {
                updatedPrLines.add(parts[0] + "," + parts[1] + "," + parts[2] + "," + parts[3] + ",REJECTED");
            } else {
                updatedPrLines.add(line);
            }
        }

        dataAccess.writePurchaseRequisitions(updatedPrLines);

        // Log the rejection
        AuditLog auditLog = new AuditLog();
        auditLog.logAction(loggedInUser.getUsername(), loggedInUser.getRole(),
            "PR Item Rejection", "PR " + prId + ", Item " + itemId + " rejected");
    }

    @Override
    public List<PurchaseOrder> viewPOs(String status, String supplierId) {
        List<PurchaseOrder> pos = getAllPurchaseOrders();
        List<PurchaseOrder> filteredPos = new ArrayList<>();
        for (PurchaseOrder po : pos) {
            boolean matches = true;
            if (status != null && !status.isEmpty()) {
                boolean statusMatch = false;
                List<PurchaseOrder.PurchaseOrderItem> items = po.getItems();
                for (PurchaseOrder.PurchaseOrderItem item : items) {
                    if (item.getStatus().equalsIgnoreCase(status)) {
                        statusMatch = true;
                        break;
                    }
                }
                if (!statusMatch) {
                    matches = false;
                }
            }
            if (supplierId != null && !supplierId.isEmpty()) {
                if (!po.getSupplierID().equalsIgnoreCase(supplierId)) {
                    matches = false;
                }
            }
            if (matches) {
                filteredPos.add(po);
            }
        }
        return filteredPos;
    }

    @Override
    public List<PurchaseOrder> searchPOs(String poId) {
        List<PurchaseOrder> pos = getAllPurchaseOrders();
        List<PurchaseOrder> results = new ArrayList<>();
        if (poId == null || poId.trim().isEmpty()) {
            return pos;
        }
        String searchLower = poId.toLowerCase();
        for (PurchaseOrder po : pos) {
            if (po.getOrderID().toLowerCase().contains(searchLower)) {
                results.add(po);
            }
        }
        return results;
    }

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

    public List<PurchaseOrder> getOrdersByStatus(String status) {
        List<PurchaseOrder> result = new ArrayList<>();
        List<PurchaseOrder> allOrders = getAllPurchaseOrders();
        for (PurchaseOrder po : allOrders) {
            List<PurchaseOrder.PurchaseOrderItem> items = po.getItems();
            for (PurchaseOrder.PurchaseOrderItem item : items) {
                if (item.getStatus().equalsIgnoreCase(status)) {
                    result.add(po);
                    break;
                }
            }
        }
        return result;
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

    public void loadViewPR(String statusFilter, JTable targetTable) {
        guiUpdater.loadViewPR(statusFilter, targetTable);
    }

    public void performSearchOrFilter(JTextField searchField, JComboBox<String> filter, JTable prTable) {
        guiUpdater.performSearchOrFilter(searchField, filter, prTable);
    }
}