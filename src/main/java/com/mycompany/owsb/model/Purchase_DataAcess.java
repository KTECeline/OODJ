/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.owsb.model;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
/**
 *
 * @author leopa
 */
public class Purchase_DataAcess {
     private static final String PURCHASE_ORDER_FILE = "data/purchase_order.txt";
      private static final String PURCHASE_REQUISITION_FILE = "data/purchase_requisition.txt";
     
     /**
     * Reads all lines from the purchase order file.
     * @return List of lines from the file.
     * @throws RuntimeException if reading fails.
     */
    public List<String> readPurchaseOrders() {
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(PURCHASE_ORDER_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Failed to read purchase orders: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            throw new RuntimeException("Failed to read purchase orders", e);
        }
        return lines;
    }

    /**
     * Writes lines to the purchase order file, overwriting existing content.
     * @param lines Lines to write.
     * @throws RuntimeException if writing fails.
     */
    public void writePurchaseOrders(List<String> lines) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(PURCHASE_ORDER_FILE))) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Failed to save purchase orders: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            throw new RuntimeException("Failed to save purchase orders", e);
        }
    }

    /**
     * Appends purchase order items to the file.
     * @param po PurchaseOrder to save.
     * @throws RuntimeException if writing fails.
     */
    public void appendPurchaseOrder(PurchaseOrder po) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(PURCHASE_ORDER_FILE, true))) {
            for (PurchaseOrder.PurchaseOrderItem item : po.getItems()) {
                String line = String.format("%s,%s,%s,%d,%.1f,%s,%s,%s,%s",
                        po.getOrderID(), item.getItemID(), po.getSupplierID(),
                        item.getQuantity(), item.getTotalPrice(), po.getOrderDate(),
                        "PENDING", po.getPrId(), po.getCreatedBy());
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Failed to save purchase order: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            throw new RuntimeException("Failed to save purchase order", e);
        }
    }

    /**
     * Finds the PO ID associated with a PR ID.
     * @param prId The purchase requisition ID.
     * @return The PO ID if found, null otherwise.
     */
    public String findExistingPOId(String prId) {
        List<String> lines = readPurchaseOrders();
        for (String line : lines) {
            String[] parts = line.split(",");
            if (parts.length >= 8 && parts[7].equalsIgnoreCase(prId)) {
                return parts[0];
            }
        }
        return null;
    }
    
     public List<String> readPurchaseRequisitions() {
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(PURCHASE_REQUISITION_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Failed to read purchase requisitions: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            throw new RuntimeException("Failed to read purchase requisitions", e);
        }
        return lines;
    }

    /**
     * Writes lines to the purchase requisition file, overwriting existing content.
     * @param lines Lines to write.
     * @throws RuntimeException if writing fails.
     */
    public void writePurchaseRequisitions(List<String> lines) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(PURCHASE_REQUISITION_FILE))) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Failed to save purchase requisitions: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            throw new RuntimeException("Failed to save purchase requisitions", e);
        }
    }
}

