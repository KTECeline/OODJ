package com.mycompany.owsb.model;

import java.awt.Component;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author timi
 */
public class PurchaseRequisition {
    private String prID;
    private List<PurchaseRequisitionItem> items;
    private String requiredDate;
    private String supplierID;
    private String raisedBy;
    private String status;

    private static final String PURCHASE_REQUISITION_FILE = "data/purchase_requisition.txt";

    public PurchaseRequisition(String prID, String requiredDate, String supplierID, String raisedBy, String status) {
        this.prID = prID;
        this.requiredDate = requiredDate;
        this.supplierID = supplierID;
        this.raisedBy = raisedBy;
        this.status = status;
        this.items = new ArrayList<>();  // <== make sure you initialize this!
    }


    public String getPrID() {
        return prID;
    }

    public List<PurchaseRequisitionItem> getItems() {
        return items;
    }

    public String getRequiredDate() {
        return requiredDate;
    }

    public String getSupplierID() {
        return supplierID;
    }

    public String getRaisedBy() {
        return raisedBy;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getTotalCost() {
        double total = 0.0;
        for (PurchaseRequisitionItem item : items) {
            total += item.getTotalCost();
        }
        return total;
    }

    @Override
    public String toString() {
        return prID + "," + requiredDate + "," + supplierID + "," + raisedBy + "," + status;
    }

    public static PurchaseRequisition fromString(String line) {
        String[] parts = line.split(",", 5);
        String prId = parts[0];
        String requiredDate = parts[1];
        String supplierId = parts[2];
        String raisedBy = parts[3];
        String status = parts[4];

        PurchaseRequisition pr = new PurchaseRequisition(prId, requiredDate, supplierId, raisedBy, status);

        return pr;
    }

    public void addItem(PurchaseRequisitionItem item) {
    this.items.add(item);
    }

    
    public static List<PurchaseRequisition> loadPurchaseRequisition() {
        List<PurchaseRequisition> prList = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(PURCHASE_REQUISITION_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                PurchaseRequisition pr = PurchaseRequisition.fromString(line);
                prList.add(pr);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return prList;
    }
    
    public static PurchaseRequisition findById(String prId) {
    List<PurchaseRequisition> allOrders = loadPurchaseRequisition();
    return allOrders.stream()
            .filter(po -> po.getPrID().equals(prId))
            .findFirst()
            .orElse(null);
}
    
    public static void update(PurchaseRequisition updatedPr) {
        List<PurchaseRequisition> all = loadPurchaseRequisition();
        for (int i = 0; i < all.size(); i++) {
            if (all.get(i).getPrID().equals(updatedPr.getPrID())) {
                all.set(i, updatedPr);
                break;
            }
        }
        saveAll(all);
    }

    public static void saveAll(List<PurchaseRequisition> requisitions) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(PURCHASE_REQUISITION_FILE))) {
            for (PurchaseRequisition pr : requisitions) {
                writer.write(pr.toString());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error saving purchase requisitions: " + e.getMessage());
        }
    }
    
    public static String generateNextPRId() {
        List<PurchaseRequisition> all = loadPurchaseRequisition();
        int maxId = 0;
        for (PurchaseRequisition pr : all) {
            try {
                int idNum = Integer.parseInt(pr.getPrID().substring(2));
                if (idNum > maxId) maxId = idNum;
            } catch (NumberFormatException e) {
                // Skip if ID format is invalid
            }
        }
        return String.format("PR%04d", maxId + 1);
    }
    
    public static void updatePRTableInUI(List<PurchaseRequisition> prList, List<PurchaseRequisitionItem> prItemList, JTable targetTable) {
        String[] columnNames = {"PR ID", "Item ID", "Quantity", "Required Date", "Supplier ID", "Raised By", "Unit Cost (RM)", "Total Cost (RM)", "Status"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);

        for (PurchaseRequisition pr : prList) {
            for (PurchaseRequisitionItem item : prItemList) {
                if (item.getPrID().equals(pr.getPrID())) {
                    Object[] row = {
                        pr.getPrID(),
                        item.getItemID(),
                        item.getQuantity(),
                        pr.getRequiredDate(),
                        pr.getSupplierID(),
                        pr.getRaisedBy(),
                        item.getUnitCost(),
                        item.getTotalCost(),
                        pr.getStatus()
                    };
                    tableModel.addRow(row);
                }
            }
        }

        targetTable.setModel(tableModel);
        autoResizeColumnWidths(targetTable);
    }


    // Search and display PR by PR ID
    public static void searchAndDisplayPRInTable(JTextField searchField, JTable table, List<PurchaseRequisition> prList, List<PurchaseRequisitionItem> prItemList) {
        String searchPRID = searchField.getText().trim().toUpperCase();
        boolean found = false;

        if (prList.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No purchase requisitions loaded.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0); // Clear previous data

        for (PurchaseRequisition pr : prList) {
            if (pr.getPrID().equalsIgnoreCase(searchPRID)) {
                for (PurchaseRequisitionItem item : pr.getItems()) {
                    Object[] row = {
                        pr.getPrID(),
                        item.getItemID(),
                        item.getQuantity(),
                        pr.getRequiredDate(),
                        pr.getSupplierID(),
                        pr.getRaisedBy(),
                        item.getUnitCost(),
                        item.getTotalCost(),
                        pr.getStatus()
                    };
                    model.addRow(row);
                }
                found = true;
                break; // Found, no need to keep looping
            }
        }

        if (!found) {
            JOptionPane.showMessageDialog(null, "Purchase Requisition ID not found.", "Not Found", JOptionPane.INFORMATION_MESSAGE);
            // Reload full PR list
            updatePRTableInUI(prList, prItemList, table);
        }
        // Reset search field prompt
        searchField.setText("Enter PR ID");
    }

    
    public static void autoResizeColumnWidths(JTable table) {
        final TableColumnModel columnModel = table.getColumnModel();
        for (int column = 0; column < table.getColumnCount(); column++) {
            int width = 50; // Minimum width
            for (int row = 0; row < table.getRowCount(); row++) {
                TableCellRenderer renderer = table.getCellRenderer(row, column);
                Component comp = table.prepareRenderer(renderer, row, column);
                width = Math.max(comp.getPreferredSize().width + 10, width);
            }
            columnModel.getColumn(column).setPreferredWidth(width);
        }
    }
    
}
