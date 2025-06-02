/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.owsb.model;

import java.awt.Color;
import java.awt.Component;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

/**
 *
 * @author timi
 */
public class Finance_ViewPO {
    private String orderID;
    private String itemID;
    private String supplierID;
    private int quantity;
    private double totalPrice;
    private LocalDate orderDate;
    private String status;
    private String prID;
    private String userID;
    
    // String representing the file path for purchase order data
    private static final String PURCHASE_ORDER_FILE = "data/purchase_order.txt";
    
    // Purchase order status constants - REVISED
    public static final String STATUS_PENDING = "PENDING";
    public static final String STATUS_APPROVED = "APPROVED";
    public static final String STATUS_REJECTED = "REJECTED";
    public static final String STATUS_UNFULFILLED = "UNFULFILLED";
    public static final String STATUS_RECEIVED = "RECEIVED";
    public static final String STATUS_COMPLETED = "COMPLETED";
    
    public Finance_ViewPO(String orderID, String itemID, String supplierID, int quantity, 
                         double totalPrice, LocalDate orderDate, String status, String prID, String userID) {
        this.orderID = orderID;
        this.itemID = itemID;
        this.supplierID = supplierID;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
        this.orderDate = orderDate;
        this.status = status;
        this.prID = prID;
        this.userID = userID;
    }
    
    // Getters
    public String getOrderID() {
        return orderID;
    }
    
    public String getItemID() {
        return itemID;
    }
    
    public String getSupplierID() {
        return supplierID;
    }
    
    public int getQuantity() {
        return quantity;
    }
    
    public double getTotalPrice() {
        return totalPrice;
    }
    
    public LocalDate getOrderDate() {
        return orderDate;
    }
    
    public String getStatus() {
        return status;
    }
    
    public String getPrID() {
        return prID;
    }
    
    public String getUserID() {
        return userID;
    }
    
    // Setters
    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }
    
    public void setItemID(String itemID) {
        this.itemID = itemID;
    }
    
    public void setSupplierID(String supplierID) {
        this.supplierID = supplierID;
    }
    
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    
    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }
    
    public void setOrderDate(LocalDate orderDate) {
        this.orderDate = orderDate;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public void setPrID(String prID) {
        this.prID = prID;
    }
    
    public void setUserID(String userID) {
        this.userID = userID;
    }
    
    // Convert object to string to save it in file
    @Override
    public String toString() {
        return orderID + "," + itemID + "," + supplierID + "," + quantity + "," + 
               totalPrice + "," + orderDate + "," + status + "," + prID + "," + userID;
    }
    
    // Convert the line in the file from String to object
    public static Finance_ViewPO fromString(String line) {
        String[] parts = line.split(",", 9);  // Split into exactly 9 parts
        
        String orderID = parts[0];
        String itemID = parts[1];
        String supplierID = parts[2];
        int quantity = Integer.parseInt(parts[3]);
        double totalPrice = Double.parseDouble(parts[4]);
        LocalDate orderDate = LocalDate.parse(parts[5]);
        String status = parts[6];
        String prID = parts[7];
        String userID = parts[8];
        
        return new Finance_ViewPO(orderID, itemID, supplierID, quantity, totalPrice, orderDate, status, prID, userID);
    }
    
    // Method to load Purchase Order records from a file
    public static List<Finance_ViewPO> loadPurchaseOrderRecords() {
        List<Finance_ViewPO> purchaseOrderList = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(PURCHASE_ORDER_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                try {
                    Finance_ViewPO purchaseOrder = Finance_ViewPO.fromString(line);
                    purchaseOrderList.add(purchaseOrder);
                } catch (Exception e) {
                    System.err.println("Error parsing purchase order record: " + line + " - " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading purchase order records: " + e.getMessage());
        }
        return purchaseOrderList;
    }
    
    // Method to save Purchase Order records to file
    public static boolean savePurchaseOrderRecords(List<Finance_ViewPO> purchaseOrderList) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(PURCHASE_ORDER_FILE))) {
            for (Finance_ViewPO purchaseOrder : purchaseOrderList) {
                writer.write(purchaseOrder.toString());
                writer.newLine();
            }
            return true;
        } catch (IOException e) {
            System.err.println("Error saving purchase order records: " + e.getMessage());
            return false;
        }
    }
    
    // Method to update purchase order table in the UI
    public static void updatePurchaseOrderTableInUI(List<Finance_ViewPO> purchaseOrderList, JTable targetTable) {
        String[] columnNames = {"Order ID", "Item ID", "Supplier ID", "Quantity", "Total Price (RM)", "Order Date", "Status", "PR ID", "User ID"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);

        for (Finance_ViewPO purchaseOrder : purchaseOrderList) {
            Object[] row = {
                purchaseOrder.getOrderID(),
                purchaseOrder.getItemID(),
                purchaseOrder.getSupplierID(),
                purchaseOrder.getQuantity(),
                String.format("%.2f", purchaseOrder.getTotalPrice()),
                purchaseOrder.getOrderDate().toString(),
                purchaseOrder.getStatus(),
                purchaseOrder.getPrID(),
                purchaseOrder.getUserID()
            };
            tableModel.addRow(row);
        }

        targetTable.setModel(tableModel);
        autoResizeColumnWidths(targetTable);
        applyRowColorBasedOnStatus(targetTable);
    }
    
    // Search and display purchase order record in table
    public static void searchAndDisplayPurchaseOrderInTable(JTextField searchField, JTable table, List<Finance_ViewPO> purchaseOrderList) {
        String searchID = searchField.getText().trim().toUpperCase();
        boolean found = false;

        if (purchaseOrderList.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No purchase order records loaded.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0); // Clear previous table data

        for (Finance_ViewPO purchaseOrder : purchaseOrderList) {
            if (purchaseOrder.getOrderID().equalsIgnoreCase(searchID)) {
                Object[] row = {
                    purchaseOrder.getOrderID(),
                    purchaseOrder.getItemID(),
                    purchaseOrder.getSupplierID(),
                    purchaseOrder.getQuantity(),
                    String.format("%.2f", purchaseOrder.getTotalPrice()),
                    purchaseOrder.getOrderDate().toString(),
                    purchaseOrder.getStatus(),
                    purchaseOrder.getPrID(),
                    purchaseOrder.getUserID()
                };
                model.addRow(row); // Add matched purchase order record to the table
                found = true;
                break;
            }
        }

        if (!found) {
            JOptionPane.showMessageDialog(null, "Order ID not found.", "Not Found", JOptionPane.INFORMATION_MESSAGE);
            
            // Reload full purchase order list into table
            updatePurchaseOrderTableInUI(purchaseOrderList, table);
        }
        // Reset Search Field
        searchField.setText("Enter Order ID");
    }
    
    // Auto resize column widths
    public static void autoResizeColumnWidths(JTable table) {
        final TableColumnModel columnModel = table.getColumnModel();
        for (int column = 0; column < table.getColumnCount(); column++) {
            int width = 100; // Minimum width
            for (int row = 0; row < table.getRowCount(); row++) {
                TableCellRenderer renderer = table.getCellRenderer(row, column);
                Component comp = table.prepareRenderer(renderer, row, column);
                width = Math.max(comp.getPreferredSize().width + 15, width);
            }
            columnModel.getColumn(column).setPreferredWidth(width);
        }
    }
    
    // Apply row colors based on purchase order status - UPDATED for new statuses
    public static void applyRowColorBasedOnStatus(JTable table) {
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                        boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                String status = table.getModel().getValueAt(row, 6).toString(); // Column index 6 = "Status"

                if (status.equalsIgnoreCase(STATUS_COMPLETED)) {
                    c.setBackground(new Color(204, 255, 204)); // Light green
                } else if (status.equalsIgnoreCase(STATUS_PENDING)) {
                    c.setBackground(new Color(255, 255, 204)); // Light yellow
                } else if (status.equalsIgnoreCase(STATUS_REJECTED)) {
                    c.setBackground(new Color(255, 204, 204)); // Light red
                } else if (status.equalsIgnoreCase(STATUS_APPROVED)) {
                    c.setBackground(new Color(204, 229, 255)); // Light blue
                } else if (status.equalsIgnoreCase(STATUS_UNFULFILLED)) {
                    c.setBackground(new Color(255, 229, 204)); // Light orange
                } else if (status.equalsIgnoreCase(STATUS_RECEIVED)) {
                    c.setBackground(new Color(229, 204, 255)); // Light purple
                } else {
                    c.setBackground(Color.WHITE);
                }

                if (isSelected) {
                    c.setBackground(table.getSelectionBackground());
                }

                return c;
            }
        });
    }
    
    // Find purchase order record by order ID
    public static Finance_ViewPO findByOrderID(String orderID) {
        List<Finance_ViewPO> allPurchaseOrderRecords = loadPurchaseOrderRecords();
        for (Finance_ViewPO purchaseOrder : allPurchaseOrderRecords) {
            if (purchaseOrder.getOrderID().equalsIgnoreCase(orderID)) {
                return purchaseOrder;
            }
        }
        return null;
    }
    
    // Filter purchase order records by status
    public static void filterByStatus(String statusFilter, JTable table, List<Finance_ViewPO> purchaseOrderList) {
        String[] columnNames = {"Order ID", "Item ID", "Supplier ID", "Quantity", "Total Price (RM)", "Order Date", "Status", "PR ID", "User ID"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        for (Finance_ViewPO purchaseOrder : purchaseOrderList) {
            boolean include = false;
            String status = purchaseOrder.getStatus();
            
            if (statusFilter.equals("All")) {
                include = true;
            } else if (statusFilter.equalsIgnoreCase(status)) {
                include = true;
            }

            if (include) {
                Object[] row = {
                    purchaseOrder.getOrderID(),
                    purchaseOrder.getItemID(),
                    purchaseOrder.getSupplierID(),
                    purchaseOrder.getQuantity(),
                    String.format("%.2f", purchaseOrder.getTotalPrice()),
                    purchaseOrder.getOrderDate().toString(),
                    purchaseOrder.getStatus(),
                    purchaseOrder.getPrID(),
                    purchaseOrder.getUserID()
                };
                model.addRow(row);
            }
        }

        table.setModel(model);
        autoResizeColumnWidths(table);
        applyRowColorBasedOnStatus(table);

        if (model.getRowCount() == 0) {
            JOptionPane.showMessageDialog(null, "No purchase order records found for status: " + statusFilter, "No Results", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    // Filter purchase order records by supplier
    public static void filterBySupplier(String supplierFilter, JTable table, List<Finance_ViewPO> purchaseOrderList) {
        String[] columnNames = {"Order ID", "Item ID", "Supplier ID", "Quantity", "Total Price (RM)", "Order Date", "Status", "PR ID", "User ID"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        for (Finance_ViewPO purchaseOrder : purchaseOrderList) {
            boolean include = false;
            String supplier = purchaseOrder.getSupplierID();
            
            if (supplierFilter.equals("All")) {
                include = true;
            } else if (supplierFilter.equalsIgnoreCase(supplier)) {
                include = true;
            }

            if (include) {
                Object[] row = {
                    purchaseOrder.getOrderID(),
                    purchaseOrder.getItemID(),
                    purchaseOrder.getSupplierID(),
                    purchaseOrder.getQuantity(),
                    String.format("%.2f", purchaseOrder.getTotalPrice()),
                    purchaseOrder.getOrderDate().toString(),
                    purchaseOrder.getStatus(),
                    purchaseOrder.getPrID(),
                    purchaseOrder.getUserID()
                };
                model.addRow(row);
            }
        }

        table.setModel(model);
        autoResizeColumnWidths(table);
        applyRowColorBasedOnStatus(table);

        if (model.getRowCount() == 0) {
            JOptionPane.showMessageDialog(null, "No purchase order records found for supplier: " + supplierFilter, "No Results", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    // Get total amount by status
    public static double getTotalAmountByStatus(List<Finance_ViewPO> purchaseOrderList, String status) {
        double total = 0.0;
        for (Finance_ViewPO purchaseOrder : purchaseOrderList) {
            if (purchaseOrder.getStatus().equalsIgnoreCase(status)) {
                total += purchaseOrder.getTotalPrice();
            }
        }
        return total;
    }
    
    // Get total amount for a specific supplier
    public static double getTotalAmountBySupplier(List<Finance_ViewPO> purchaseOrderList, String supplierID) {
        double total = 0.0;
        for (Finance_ViewPO purchaseOrder : purchaseOrderList) {
            if (purchaseOrder.getSupplierID().equalsIgnoreCase(supplierID)) {
                total += purchaseOrder.getTotalPrice();
            }
        }
        return total;
    }
    
    // Update purchase order status
    public static boolean updatePurchaseOrderStatus(String orderID, String newStatus) {
        List<Finance_ViewPO> purchaseOrderList = loadPurchaseOrderRecords();
        boolean updated = false;
        
        for (Finance_ViewPO purchaseOrder : purchaseOrderList) {
            if (purchaseOrder.getOrderID().equalsIgnoreCase(orderID)) {
                purchaseOrder.setStatus(newStatus);
                updated = true;
                break;
            }
        }
        
        if (updated) {
            return savePurchaseOrderRecords(purchaseOrderList);
        }
        
        return false;
    }
    
    // Get purchase orders by supplier ID
    public static List<Finance_ViewPO> getPurchaseOrdersBySupplier(String supplierID) {
        List<Finance_ViewPO> allOrders = loadPurchaseOrderRecords();
        List<Finance_ViewPO> supplierOrders = new ArrayList<>();
        
        for (Finance_ViewPO order : allOrders) {
            if (order.getSupplierID().equalsIgnoreCase(supplierID)) {
                supplierOrders.add(order);
            }
        }
        
        return supplierOrders;
    }
    
    // Get purchase orders by date range
    public static List<Finance_ViewPO> getPurchaseOrdersByDateRange(LocalDate startDate, LocalDate endDate) {
        List<Finance_ViewPO> allOrders = loadPurchaseOrderRecords();
        List<Finance_ViewPO> filteredOrders = new ArrayList<>();
        
        for (Finance_ViewPO order : allOrders) {
            LocalDate orderDate = order.getOrderDate();
            if ((orderDate.isEqual(startDate) || orderDate.isAfter(startDate)) && 
                (orderDate.isEqual(endDate) || orderDate.isBefore(endDate))) {
                filteredOrders.add(order);
            }
        }
        
        return filteredOrders;
    }
}