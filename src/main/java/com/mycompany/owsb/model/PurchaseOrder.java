package com.mycompany.owsb.model;

import java.awt.Color;
import java.awt.Component;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/**
 * Represents a Purchase Order in the OWSB system, consolidating multiple items.
 */
public class PurchaseOrder {
    private String orderID;
    private String supplierID;
    private String orderDate;
    private String status;
    private String prId;
    private String createdBy;
    private List<PurchaseOrder.PurchaseOrderItem> items;
   
    private static final String PURCHASE_ORDER_FILE = "data/purchase_order.txt";

    // Inner class to represent an item in a PO
    public static class PurchaseOrderItem {
        private String itemID;
        private int quantity;
        private double totalPrice;
        private String prId; 

        public PurchaseOrderItem(String itemID, int quantity, double totalPrice) {
            if (itemID == null || itemID.isEmpty()) {
                throw new IllegalArgumentException("Item ID cannot be null or empty");
            }
            if (quantity <= 0) {
                throw new IllegalArgumentException("Quantity must be positive");
            }
            if (totalPrice < 0) {
                throw new IllegalArgumentException("Total price cannot be negative");
            }
            this.itemID = itemID;
            this.quantity = quantity;
            this.totalPrice = totalPrice;
        }

        public String getItemID() {
            return itemID;
        }

        public int getQuantity() {
            return quantity;
        }

        public double getTotalPrice() {
            return totalPrice;
        }

        public void setQuantity(int quantity) {
            if (quantity <= 0) {
                throw new IllegalArgumentException("Quantity must be positive");
            }
            this.quantity = quantity;
        }

        public void setTotalPrice(double totalPrice) {
            if (totalPrice < 0) {
                throw new IllegalArgumentException("Total price cannot be negative");
            }
            this.totalPrice = totalPrice;
        }

        public void setPrId(String prId) {
        this.prId = prId;
    }
   
    }

    // Constructor
    public PurchaseOrder(String orderID, String supplierID, String orderDate, String status, String prId, String createdBy) {
        if (orderID == null || orderID.isEmpty()) {
            throw new IllegalArgumentException("Order ID cannot be null or empty");
        }
        if (supplierID == null || supplierID.isEmpty()) {
            throw new IllegalArgumentException("Supplier ID cannot be null or empty");
        }
        if (orderDate == null || orderDate.isEmpty()) {
            throw new IllegalArgumentException("Order date cannot be null or empty");
        }
        if (!isValidStatus(status)) {
            throw new IllegalArgumentException("Invalid status value");
        }
        this.orderID = orderID;
        this.supplierID = supplierID;
        this.orderDate = orderDate;
        this.status = status;
        this.prId = prId;
        this.createdBy = createdBy;
        this.items = new ArrayList<>();
    }

    private boolean isValidStatus(String status) {
        return status != null && 
               (status.equals("PENDING") || 
                status.equals("APPROVED") || 
                status.equals("REJECTED") || 
                status.equals("UNFULFILLED") ||
                status.equals("RECEIVED") ||
                status.equals("COMPLETED"));
    }
    
    public String getOrderID() {
        return orderID;
    }

    public String getSupplierID() {
        return supplierID;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public String getStatus() {
        return status;
    }

    public String getPrId() {
        return prId;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public List<PurchaseOrderItem> getItems() {
        return items;
    }

    // Setters
    
    
    
    public void setStatus(String status) {
        if (!isValidStatus(status)) {
            throw new IllegalArgumentException("Invalid status value");
        }
        this.status = status;
    }

    public void setSupplierID(String supplierID) {
        if (supplierID == null || supplierID.isEmpty()) {
            throw new IllegalArgumentException("Supplier ID cannot be null or empty");
        }
        this.supplierID = supplierID;
    }

    public void addItem(PurchaseOrderItem item) {
        items.add(item);
    }

    public double getTotalPrice() {
        return items.stream().mapToDouble(PurchaseOrderItem::getTotalPrice).sum();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (PurchaseOrderItem item : items) {
            sb.append(orderID).append(",")
              .append(item.getItemID()).append(",")
              .append(supplierID).append(",")
              .append(item.getQuantity()).append(",")
              .append(item.getTotalPrice()).append(",")
              .append(orderDate).append(",")
              .append(status).append(",")
              .append(prId).append(",")
              .append(createdBy).append("\n");
        }
        return sb.toString().trim();
    }

    public static PurchaseOrder fromString(String orderString) {
    String[] orderData = orderString.split(",", 9);

    if (orderData.length < 9) {
        throw new IllegalArgumentException("Malformed line in purchase_order.txt: " + orderString);
    }

    String orderID = orderData[0];
    String itemID = orderData[1];
    String supplierID = orderData[2];
    int quantity = Integer.parseInt(orderData[3]);
    double totalPrice = Double.parseDouble(orderData[4]);
    String orderDate = orderData[5];
    String status = orderData[6];
    String prId = orderData[7];
    String createdBy = orderData[8];

    PurchaseOrder po = new PurchaseOrder(orderID, supplierID, orderDate, status, prId, createdBy);
    po.addItem(new PurchaseOrderItem(itemID, quantity, totalPrice));
    return po;
}


    public String getFormattedDetails() {
        StringBuilder sb = new StringBuilder();
        sb.append("Purchase Order ID: ").append(orderID).append("\n")
          .append("Supplier ID: ").append(supplierID).append("\n")
          .append("Order Date: ").append(orderDate).append("\n")
          .append("Status: ").append(status).append("\n")
          .append("PR ID: ").append(prId).append("\n")
          .append("Created By: ").append(createdBy).append("\n")
          .append("Items:\n");
        for (PurchaseOrderItem item : items) {
            sb.append("  - Item ID: ").append(item.getItemID())
              .append(", Quantity: ").append(item.getQuantity())
              .append(", Total Price: ").append(item.getTotalPrice()).append("\n");
        }
        sb.append("Total Price: ").append(getTotalPrice());
        return sb.toString();
    }

    public static List<PurchaseOrder> loadPurchaseOrders() {
        List<PurchaseOrder> poList = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(PURCHASE_ORDER_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                PurchaseOrder newPo = fromString(line);
                boolean merged = false;
                for (PurchaseOrder existingPo : poList) {
                    if (existingPo.getOrderID().equals(newPo.getOrderID())) {
                        existingPo.addItem(newPo.getItems().get(0));
                        merged = true;
                        break;
                    }
                }
                if (!merged) {
                    poList.add(newPo);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return poList;
    }

    public static void update(PurchaseOrder updatedPo) {
        List<PurchaseOrder> orders = loadPurchaseOrders();
        boolean found = false;

        for (int i = 0; i < orders.size(); i++) {
            if (orders.get(i).getOrderID().equals(updatedPo.getOrderID())) {
                orders.set(i, updatedPo);
                found = true;
                break;
            }
        }

        if (!found) {
            throw new IllegalArgumentException("Purchase Order not found for update");
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(PURCHASE_ORDER_FILE))) {
            for (PurchaseOrder po : orders) {
                writer.write(po.toString());
                writer.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException("Error updating purchase order file: " + e.getMessage());
        }
    }

    public static PurchaseOrder findById(String orderId) {
        List<PurchaseOrder> allOrders = loadPurchaseOrders();
        return allOrders.stream()
                .filter(po -> po.getOrderID().equals(orderId))
                .findFirst()
                .orElse(null);
    }

    public static String generateNewOrderId() {
        List<PurchaseOrder> allOrders = loadPurchaseOrders();
        int maxId = allOrders.stream()
                .mapToInt(po -> {
                    try {
                        return Integer.parseInt(po.getOrderID().substring(2));
                    } catch (NumberFormatException e) {
                        return 0;
                    }
                })
                .max()
                .orElse(0);
        return String.format("PO%04d", maxId + 1);
    }

    public static void updatePOTableInUI(List<PurchaseOrder> poList, List<PurchaseRequisition> prList, JTable targetTable) {
    String[] columnNames = {"PO ID", "Item ID", "Supplier ID", "Quantity", "Total Price (RM)", 
                            "Order Date", "Status", "PR ID", "Required Date", "Created By"};
    DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);

    for (PurchaseOrder po : poList) {
        PurchaseRequisition pr = getRequisitionById(po.getPrId(), prList);
        String requiredDate = (pr != null) ? pr.getRequiredDate().toString() : "N/A";

        for (PurchaseOrderItem item : po.getItems()) {
            Object[] row = {
                po.getOrderID(),
                item.getItemID(),
                po.getSupplierID(),
                item.getQuantity(),
                item.getTotalPrice(),
                po.getOrderDate(),
                po.getStatus(),
                po.getPrId(),
                requiredDate,  // <-- from PurchaseRequisition
                po.getCreatedBy()
            };
            tableModel.addRow(row);
        }
    }
    applyStatusColorRenderer(targetTable);
    targetTable.setModel(tableModel);
    Item.autoResizeColumnWidths(targetTable);
    
}

private static PurchaseRequisition getRequisitionById(String prID, List<PurchaseRequisition> prList) {
    for (PurchaseRequisition pr : prList) {
        if (pr.getPrID().equalsIgnoreCase(prID)) {
            return pr;
        }
    }
    return null;
}

    
    public static void searchAndDisplayPO(JTextField searchField, JTable targetTable, List<PurchaseOrder> poList, List<PurchaseRequisition> prList) {
    String searchID = searchField.getText().trim().toUpperCase();
    boolean found = false;

    if (poList.isEmpty()) {
        JOptionPane.showMessageDialog(null, "No Purchase Orders loaded.", "Warning", JOptionPane.WARNING_MESSAGE);
        return;
    }

    DefaultTableModel model = new DefaultTableModel(
        new String[]{"PO ID", "Item ID", "Supplier ID", "Quantity", "Total Price (RM)", 
                     "Order Date", "Status", "PR ID", "Required Date", "Created By"}, 0);

    for (PurchaseOrder po : poList) {
        if (po.getOrderID().equalsIgnoreCase(searchID)) {
            // Find matching Purchase Requisition
            LocalDate requiredDate = null;
            for (PurchaseRequisition pr : prList) {
                if (pr.getPrID().equals(po.getPrId())) {
                    requiredDate = pr.getRequiredDate();
                    break;
                }
            }

            for (PurchaseOrderItem item : po.getItems()) {
                Object[] row = {
                    po.getOrderID(),
                    item.getItemID(),
                    po.getSupplierID(),
                    item.getQuantity(),
                    item.getTotalPrice(),
                    po.getOrderDate(),
                    po.getStatus(),
                    po.getPrId(),
                    requiredDate != null ? requiredDate.toString() : "N/A",
                    po.getCreatedBy()
                };
                model.addRow(row);
            }
            found = true;
            break;
        }
    }

    targetTable.setModel(model);
    applyStatusColorRenderer(targetTable);
    Item.autoResizeColumnWidths(targetTable);

    if (!found) {
        JOptionPane.showMessageDialog(null, "Purchase Order ID not found.", "Not Found", JOptionPane.INFORMATION_MESSAGE);
        updatePOTableInUI(poList, prList, targetTable); // Refresh with full table
    }
    
    searchField.setText("Enter PO ID");
}

    
   public static void applyStatusColorRenderer(JTable table) {
    table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            String status = table.getValueAt(row, 6).toString(); // Column index of "Status"

            if (!isSelected) {
                switch (status.toUpperCase()) {
                    case "REJECTED":
                        c.setBackground(new Color(255, 204, 204)); // Light red
                        break;
                    case "COMPLETED":
                        c.setBackground(new Color(204, 255, 204)); // Light green
                        break;
                    case "APPROVED":
                        c.setBackground(new Color(229, 204, 255)); // Light purple
                        break;
                    case "PENDING":
                        c.setBackground(Color.WHITE); // White
                        break;
                    case "RECEIVED":
                        c.setBackground(new Color(200, 220, 255)); // Light Blue
                        break;
                    case "UNFULFILLED":
                        c.setBackground(new Color(255, 255, 204)); // Light Yellow
                        break;
                    default:
                        c.setBackground(Color.WHITE); // Default
                        break;
                }
            
            }

            return c;
        }
    });
}


    
}