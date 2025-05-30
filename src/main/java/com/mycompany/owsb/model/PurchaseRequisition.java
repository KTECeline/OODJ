package com.mycompany.owsb.model;

import java.awt.Color;
import java.awt.Component;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableCellRenderer;
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
    private String supplierID;
    private LocalDate requiredDate;
    private String raisedBy;
    private String status;

    private static final String PURCHASE_REQUISITION_FILE = "data/purchase_requisition.txt";

    public PurchaseRequisition(String prID, String supplierID, LocalDate requiredDate, String raisedBy, String status) {
        this.prID = prID;
        this.supplierID = supplierID;
        this.requiredDate = requiredDate;
        this.raisedBy = raisedBy;
        this.status = status;
        this.items = new ArrayList<>();
    }


    public String getPrID() {
        return prID;
    }

    public List<PurchaseRequisitionItem> getItems() {
        return items;
    }
    
    public String getSupplierID() {
        return supplierID;
    }
    
    public LocalDate getRequiredDate() {
        return requiredDate;
    }

    public String getRaisedBy() {
        return raisedBy;
    }

    public String getStatus() {
        return status;
    }
    
    public void setSupplierID(String supplierID) {
            this.supplierID = supplierID;
    }
    
    public void setRequiredDate(LocalDate requiredDate) {
        this.requiredDate = requiredDate;
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
        return prID + "," + supplierID + "," + requiredDate + "," + raisedBy + "," + status;
    }

    public static PurchaseRequisition fromString(String line) {
        String[] parts = line.split(",", 5);
        String prId = parts[0];
        String supplierId = parts[1];
        LocalDate requiredDate = LocalDate.parse(parts[2]);  // assumes stored as yyyy-MM-dd
        String raisedBy = parts[3];
        String status = parts[4];

        PurchaseRequisition pr = new PurchaseRequisition(prId, supplierId, requiredDate, raisedBy, status);

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
    
    public static void updatePRTableInUI(List<PurchaseRequisition> prList, List<PurchaseRequisitionItem> prItemList, List<Item> itemList, JTable targetTable) {
        String[] columnNames = {"PR ID", "Item ID", "Supplier ID", "Quantity", "Required Date", "Raised By", "Unit Cost (RM)", "Total Cost (RM)", "Status"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);

        for (PurchaseRequisition pr : prList) {
            for (PurchaseRequisitionItem prItem : prItemList) {
                if (prItem.getPrID().equals(pr.getPrID())) {

                    // Get item name from Item class
                    String itemName = "";
                    for (Item itemObj : itemList) {
                        if (itemObj.getItemID().equalsIgnoreCase(prItem.getItemID())) {
                            itemName = itemObj.getItemName();
                            break;
                        }
                    }

                    Object[] row = {
                        pr.getPrID(),
                        prItem.getItemID() + " - " + itemName,
                        pr.getSupplierID(),
                        prItem.getQuantity(),
                        pr.getRequiredDate(),
                        pr.getRaisedBy(),
                        prItem.getUnitCost(),
                        prItem.getTotalCost(),
                        pr.getStatus()
                    };
                    tableModel.addRow(row);
                }
            }
        }


        targetTable.setModel(tableModel);
        applyColorBasedOnPrID(targetTable);
        autoResizeColumnWidths(targetTable);
    }


    // Search and display PR by PR ID
    public static void searchAndDisplayPRInTable(JTextField searchField, JTable table, List<PurchaseRequisition> prList, List<Item> itemList, List<PurchaseRequisitionItem> prItemList) {
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
                        pr.getSupplierID(),                       
                        item.getQuantity(),
                        pr.getRequiredDate(),
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
            updatePRTableInUI(prList, prItemList, itemList, table);
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
    
    public static void applyColorBasedOnPrID(JTable prTable) {
        Map<String, Color> prIdColorMap = new HashMap<>();
        Color[] colors = {
            new Color(255, 255, 224),  // light yellow
            new Color(200, 220, 255)  // blue
        };
        int[] colorIndex = {0};  // use array to mutate inside inner class

        prTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                String prId = table.getValueAt(row, 0).toString();  // PR ID is column 0

                if (!prIdColorMap.containsKey(prId)) {
                    prIdColorMap.put(prId, colors[colorIndex[0] % colors.length]);
                    colorIndex[0]++;
                }

                if (isSelected) {
                    c.setBackground(table.getSelectionBackground());
                    c.setForeground(table.getSelectionForeground());
                } else {
                    c.setBackground(prIdColorMap.get(prId));
                    c.setForeground(Color.BLACK);
                }

                return c;
            }
        });
}
    
    public void setPRItems(List<PurchaseRequisitionItem> items) {
    this.items = items;
}

public List<PurchaseRequisitionItem> getPRItems() {
    return items;
}



    
}
