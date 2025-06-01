package com.mycompany.owsb.model;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class FinanceManager extends Manager {
    private static final String PURCHASE_ORDER_FILE = "data/purchase_order.txt";
    private static final String PAYMENT_DETAILS_FILE = "data/payment_details.txt";
    private User loggedInUser;

    public FinanceManager(User loggedInUser) {
        super(loggedInUser);
           this.loggedInUser = loggedInUser;
    }

    @Override
    public boolean isAllowedToPerform(String action) {
        if (getDepartment().equalsIgnoreCase("Finance Manager") ||
            getDepartment().equalsIgnoreCase("Administrator") ||
            getDepartment().equalsIgnoreCase("Root Administrator")
            ) {
            return action.equalsIgnoreCase("ApprovePO") ||
                   action.equalsIgnoreCase("VerifyInventory") ||
                   action.equalsIgnoreCase("ProcessPayment") ||
                   action.equalsIgnoreCase("GenerateFinancialReport") ||
                   action.equalsIgnoreCase("ViewPR") ||
                   action.equalsIgnoreCase("ViewPO");
        }
        return false;
    }

    // Get all PENDING POs for approval
    public List<Finance_PurchaseOrder> getPendingPOs() throws IOException {
        List<Finance_PurchaseOrder> pendingOrders = new ArrayList<>();
        
        if (!Files.exists(Paths.get(PURCHASE_ORDER_FILE))) {
            return pendingOrders;
        }
        
        List<String> lines = Files.readAllLines(Paths.get(PURCHASE_ORDER_FILE));
        
        for (String line : lines) {
            if (line.trim().isEmpty()) continue;
            
            String[] parts = line.split(",");
            if (parts.length >= 9 && parts[6].equalsIgnoreCase("PENDING")) {
                pendingOrders.add(new Finance_PurchaseOrder(
                    parts[0].trim(), parts[1].trim(), parts[2].trim(),
                    Integer.parseInt(parts[3].trim()),
                    Double.parseDouble(parts[4].trim()),
                    LocalDate.parse(parts[5].trim()),
                    parts[6].trim(), parts[7].trim(), parts[8].trim()
                ));
            }
        }
        return pendingOrders;
    }

    // Get all RECEIVED POs for inventory verification
    public List<Finance_VerifyInventory> getReceivedPOsForVerification() throws IOException {
        List<Finance_VerifyInventory> receivedOrders = new ArrayList<>();
        
        if (!Files.exists(Paths.get(PURCHASE_ORDER_FILE))) {
            return receivedOrders;
        }
        
        List<String> lines = Files.readAllLines(Paths.get(PURCHASE_ORDER_FILE));
        
        for (String line : lines) {
            if (line.trim().isEmpty()) continue;
            
            String[] parts = line.split(",");
            if (parts.length >= 9 && parts[6].equalsIgnoreCase("RECEIVED")) {
                receivedOrders.add(new Finance_VerifyInventory(
                    parts[0].trim(), parts[1].trim(), parts[2].trim(),
                    Integer.parseInt(parts[3].trim()),
                    Double.parseDouble(parts[4].trim()),
                    LocalDate.parse(parts[5].trim()),
                    parts[6].trim(), parts[7].trim(), parts[8].trim()
                ));
            }
        }
        return receivedOrders;
    }

    // Get all VERIFIED POs ready for payment processing
    public List<Finance_VerifyInventory> getVerifiedPOsForPayment() throws IOException {
        List<Finance_VerifyInventory> verifiedOrders = new ArrayList<>();
        
        if (!Files.exists(Paths.get(PURCHASE_ORDER_FILE))) {
            return verifiedOrders;
        }
        
        List<String> lines = Files.readAllLines(Paths.get(PURCHASE_ORDER_FILE));
        
        for (String line : lines) {
            if (line.trim().isEmpty()) continue;
            
            String[] parts = line.split(",");
            if (parts.length >= 9 && parts[6].equalsIgnoreCase("VERIFIED")) {
                verifiedOrders.add(new Finance_VerifyInventory(
                    parts[0].trim(), parts[1].trim(), parts[2].trim(),
                    Integer.parseInt(parts[3].trim()),
                    Double.parseDouble(parts[4].trim()),
                    LocalDate.parse(parts[5].trim()),
                    parts[6].trim(), parts[7].trim(), parts[8].trim()
                ));
            }
        }
        return verifiedOrders;
    }

    // Approve/Reject a PO - FIXED to handle multiple line items with same PO ID
    public boolean updatePOStatus(String poId, String newStatus) throws IOException {
        if (!isAllowedToPerform("ApprovePO") || 
            (!newStatus.equals("APPROVED") && !newStatus.equals("REJECTED"))) {
            return false;
        }
        
        if (!Files.exists(Paths.get(PURCHASE_ORDER_FILE))) {
            return false;
        }
        
        List<String> lines = Files.readAllLines(Paths.get(PURCHASE_ORDER_FILE));
        boolean found = false;
        
        // Process ALL lines with matching PO ID, not just the first one
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line.trim().isEmpty()) continue;
            
            String[] parts = line.split(",");
            if (parts[0].trim().equals(poId)) {
                Finance_PurchaseOrder po = new Finance_PurchaseOrder(
                    parts[0].trim(), parts[1].trim(), parts[2].trim(),
                    Integer.parseInt(parts[3].trim()),
                    Double.parseDouble(parts[4].trim()),
                    LocalDate.parse(parts[5].trim()),
                    parts[6].trim(), parts[7].trim(), parts[8].trim()
                );
                po.setStatus(newStatus);
                lines.set(i, po.toString());
                found = true;
                // REMOVED the break statement - continue processing all matching lines
            }
        }
        
        if (found) {
            Files.write(Paths.get(PURCHASE_ORDER_FILE), lines);
             AuditLog auditLog = new AuditLog();
        String action = "PO Status Update by Finance Manager";
        String details = String.format("PO ID: %s, New Status: %s", poId, newStatus);
        auditLog.logAction(loggedInUser.getUsername(), loggedInUser.getRole(), action, details);
            return true;
        }
        
        return false;
    }

    // Verify inventory update (change RECEIVED to VERIFIED)
    public boolean verifyInventoryUpdate(String poId) throws IOException {
        if (!isAllowedToPerform("VerifyInventory")) {
            return false;
        }
        
        if (!Files.exists(Paths.get(PURCHASE_ORDER_FILE))) {
            return false;
        }
        
        List<String> lines = Files.readAllLines(Paths.get(PURCHASE_ORDER_FILE));
        boolean found = false;
        
        // Process ALL lines with matching PO ID, not just the first one
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line.trim().isEmpty()) continue;
            
            String[] parts = line.split(",");
            if (parts[0].trim().equals(poId) && parts[6].equalsIgnoreCase("RECEIVED")) {
                // Change status from RECEIVED to VERIFIED
                parts[6] = "VERIFIED";
                lines.set(i, String.join(",", parts));
                found = true;
                // REMOVED break statement - continue processing all matching lines
            }
        }
        
        if (found) {
            Files.write(Paths.get(PURCHASE_ORDER_FILE), lines);
             AuditLog auditLog = new AuditLog();
            String action = "PO Status Update by Finance Manager";
            String details = String.format("PO ID: %s, New Status: VERIFIED", poId);
            auditLog.logAction(loggedInUser.getUsername(), loggedInUser.getRole(), action, details);

            return true;
        }
        return false;
    }

    // Process payment for a verified PO
    public boolean processPayment(String poId, String paymentMethod) throws IOException {
        if (!isAllowedToPerform("ProcessPayment")) {
            return false;
        }
        
        // First, get the PO details
        Finance_VerifyInventory po = getVerifiedPOById(poId);
        if (po == null || !po.isReadyForPayment()) {
            return false;
        }
        
        // Load existing payments to generate new payment ID
        List<Finance_Payment> existingPayments = getAllPayments();
        String paymentId = Finance_Payment.generateNextPaymentID(existingPayments);
        
        // Create new payment record
        Finance_Payment payment = new Finance_Payment(
            paymentId, po.getOrderID(), po.getSupplierID(), 
            po.getTotalPrice(), paymentMethod
        );
        payment.completePayment();
        
        // Save payment to file
        boolean paymentSaved = savePayment(payment);
        
        if (paymentSaved) {
            // Update PO status to COMPLETED
            return updatePOStatusToCompleted(poId);
        }
        
        return false;
    }

    // Get verified PO by ID
    private Finance_VerifyInventory getVerifiedPOById(String poId) throws IOException {
        if (!Files.exists(Paths.get(PURCHASE_ORDER_FILE))) {
            return null;
        }
        
        List<String> lines = Files.readAllLines(Paths.get(PURCHASE_ORDER_FILE));
        
        for (String line : lines) {
            if (line.trim().isEmpty()) continue;
            
            String[] parts = line.split(",");
            if (parts.length >= 9 && parts[0].trim().equals(poId) && 
                parts[6].equalsIgnoreCase("VERIFIED")) {
                return new Finance_VerifyInventory(
                    parts[0].trim(), parts[1].trim(), parts[2].trim(),
                    Integer.parseInt(parts[3].trim()),
                    Double.parseDouble(parts[4].trim()),
                    LocalDate.parse(parts[5].trim()),
                    parts[6].trim(), parts[7].trim(), parts[8].trim()
                );
            }
        }
        return null;
    }

    // Save payment to file
    private boolean savePayment(Finance_Payment payment) throws IOException {
        try {
            // Ensure directory exists
            Files.createDirectories(Paths.get(PAYMENT_DETAILS_FILE).getParent());
            
            // Append payment to file
            String paymentLine = payment.toString() + System.lineSeparator();
            Files.write(Paths.get(PAYMENT_DETAILS_FILE), paymentLine.getBytes(), 
                       StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Update PO status to COMPLETED after payment
    private boolean updatePOStatusToCompleted(String poId) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get(PURCHASE_ORDER_FILE));
        boolean found = false;
        
        // Process ALL lines with matching PO ID, not just the first one
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line.trim().isEmpty()) continue;
            
            String[] parts = line.split(",");
            if (parts[0].trim().equals(poId)) {
                parts[6] = "COMPLETED";
                lines.set(i, String.join(",", parts));
                found = true;
                // REMOVED break statement - continue processing all matching lines
            }
        }
        
        if (found) {
            Files.write(Paths.get(PURCHASE_ORDER_FILE), lines);
             AuditLog auditLog = new AuditLog();
            String action = "Inventory Verified by Finance Manager";
            String details = String.format("PO ID: %s, Status changed to VERIFIED", poId);
            auditLog.logAction(loggedInUser.getUsername(), loggedInUser.getRole(), action, details);

            return true;
        }
        return false;
    }

    // Get all payments - FIXED VERSION
    public List<Finance_Payment> getAllPayments() throws IOException {
        List<Finance_Payment> payments = new ArrayList<>();
        
        if (!Files.exists(Paths.get(PAYMENT_DETAILS_FILE))) {
            return payments;
        }
        
        List<String> lines = Files.readAllLines(Paths.get(PAYMENT_DETAILS_FILE));
        
        for (String line : lines) {
            if (line.trim().isEmpty()) continue;
            
            String[] parts = line.split(",");
            
            // Option 1: If constructor takes 7 parameters (paymentId, orderId, supplierId, amount, date, method, userId)
            if (parts.length >= 7) {
                payments.add(new Finance_Payment(
                    parts[0].trim(),                           // paymentId
                    parts[1].trim(),                           // orderId  
                    parts[2].trim(),                           // supplierId
                    Double.parseDouble(parts[3].trim()),       // amount
                    LocalDate.parse(parts[4].trim()),          // date
                    parts[5].trim(),                           // paymentMethod
                    parts[6].trim()                            // userId
                ));
            }

        }
        return payments;
    }

    // Get payments by supplier
    public List<Finance_Payment> getPaymentsBySupplier(String supplierId) throws IOException {
        List<Finance_Payment> allPayments = getAllPayments();
        List<Finance_Payment> supplierPayments = new ArrayList<>();
        
        for (Finance_Payment payment : allPayments) {
            if (payment.getSupplierID().equals(supplierId)) {
                supplierPayments.add(payment);
            }
        }
        return supplierPayments;
    }

    // Calculate total payments made
    public double getTotalPaymentsMade() throws IOException {
        List<Finance_Payment> payments = getAllPayments();
        double total = 0.0;
        
        for (Finance_Payment payment : payments) {
            if (payment.isCompleted()) {
                total += payment.getTotalAmount();
            }
        }
        return total;
    }
}