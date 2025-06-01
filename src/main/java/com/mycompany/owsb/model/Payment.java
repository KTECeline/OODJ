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
import java.util.Arrays;
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
public class Payment {
    private final String paymentID;
    private List<String> orderIDs;
    private String supplierID;
    private double amount;
    private LocalDate paymentDate;
    private String paymentMethod;
    private String status;
    
    // String representing the file path for finance payment data
    private static final String FINANCE_FILE = "data/finance_payment.txt";
    
    // Payment status constants
    public static final String STATUS_PENDING = "PENDING";
    public static final String STATUS_APPROVED = "APPROVED";
    public static final String STATUS_REJECTED = "REJECTED";
    public static final String STATUS_UNFULFILLED = "UNFULFILLED";
    public static final String STATUS_RECEIVED = "RECEIVED";
    public static final String STATUS_VERIFIED = "VERIFIED";
    public static final String STATUS_COMPLETED = "COMPLETED";
    
    // Payment method constants
    public static final String METHOD_BANK_TRANSFER = "Bank Transfer";
    public static final String METHOD_CHECK = "Check";
    public static final String METHOD_CASH = "Cash";
    public static final String METHOD_CREDIT_CARD = "Credit Card";

    public Payment(String paymentID, List<String> orderIDs, String supplierID, double amount, 
                   LocalDate paymentDate, String paymentMethod, String status) {
        this.paymentID = paymentID;
        this.orderIDs = new ArrayList<>(orderIDs);
        this.supplierID = supplierID;
        this.amount = amount;
        this.paymentDate = paymentDate;
        this.paymentMethod = paymentMethod;
        this.status = status;
    }
    
    // Getters
    public String getPaymentID() {
        return paymentID;
    }
    
    public List<String> getOrderIDs() {
        return new ArrayList<>(orderIDs);
    }
    
    public String getOrderIDsAsString() {
        return String.join(";", orderIDs);
    }
    
    public String getSupplierID() {
        return supplierID;
    }
    
    public double getAmount() {
        return amount;
    }
    
    public LocalDate getPaymentDate() {
        return paymentDate;
    }
    
    public String getPaymentMethod() {
        return paymentMethod;
    }
    
    public String getStatus() {
        return status;
    }
    
    // Setters
    public void setOrderIDs(List<String> orderIDs) {
        this.orderIDs = new ArrayList<>(orderIDs);
    }
    
    public void setSupplierID(String supplierID) {
        this.supplierID = supplierID;
    }
    
    public void setAmount(double amount) {
        this.amount = amount;
    }
    
    public void setPaymentDate(LocalDate paymentDate) {
        this.paymentDate = paymentDate;
    }
    
    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    // Convert object to string to save it in file
    @Override
    public String toString() {
        return paymentID + "," + getOrderIDsAsString() + "," + supplierID + "," + 
               amount + "," + paymentDate + "," + paymentMethod + "," + status;
    }
    
    // Convert the line in the file from String to object
    public static Payment fromString(String line) {
        String[] parts = line.split(",", 7);  // Split into exactly 7 parts
        
        String paymentID = parts[0];
        
        // Parse order IDs (can be multiple separated by semicolon)
        List<String> orderIDs = new ArrayList<>();
        if (!parts[1].trim().isEmpty()) {
            String[] orderIDArray = parts[1].split(";");
            for (String orderID : orderIDArray) {
                orderIDs.add(orderID.trim());
            }
        }
        
        String supplierID = parts[2];
        double amount = Double.parseDouble(parts[3]);
        LocalDate paymentDate = LocalDate.parse(parts[4]);
        String paymentMethod = parts[5];
        String status = parts[6];
        
        return new Payment(paymentID, orderIDs, supplierID, amount, paymentDate, paymentMethod, status);
    }
    
    // Method to auto GENERATE PAYMENT ID from the last one
    public static String generateNextPaymentID(List<Payment> financeList) {
        int maxNumber = 0;
        for (Payment finance : financeList) {
            String id = finance.getPaymentID();
            if (id.startsWith("PY")) {
                try {
                    int num = Integer.parseInt(id.substring(2));
                    if (num > maxNumber) {
                        maxNumber = num;
                    }
                } catch (NumberFormatException ignored) {}
            }
        }
        return String.format("PY%04d", maxNumber + 1);
    }
    
    // Method to load Payment records from a file
    public static List<Payment> loadFinanceRecords() {
        List<Payment> financeList = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(FINANCE_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                try {
                    Payment finance = Payment.fromString(line);
                    financeList.add(finance);
                } catch (Exception e) {
                    System.err.println("Error parsing finance record: " + line + " - " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading finance records: " + e.getMessage());
        }
        return financeList;
    }
    
    // Method to save Payment records to file
    public static boolean saveFinanceRecords(List<Payment> financeList) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FINANCE_FILE))) {
            for (Payment finance : financeList) {
                writer.write(finance.toString());
                writer.newLine();
            }
            return true;
        } catch (IOException e) {
            System.err.println("Error saving finance records: " + e.getMessage());
            return false;
        }
    }
    
    // Method to update finance table in the UI
    public static void updateFinanceTableInUI(List<Payment> financeList, JTable targetTable) {
        String[] columnNames = {"Payment ID", "Order IDs", "Supplier ID", "Amount (RM)", "Payment Date", "Payment Method", "Status"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);

        for (Payment finance : financeList) {
            Object[] row = {
                finance.getPaymentID(),
                finance.getOrderIDsAsString(),
                finance.getSupplierID(),
                String.format("%.2f", finance.getAmount()),
                finance.getPaymentDate().toString(),
                finance.getPaymentMethod(),
                finance.getStatus()
            };
            tableModel.addRow(row);
        }

        targetTable.setModel(tableModel);
        autoResizeColumnWidths(targetTable);
        applyRowColorBasedOnStatus(targetTable);
    }
    
    // Search and display finance record in table
    public static void searchAndDisplayFinanceInTable(JTextField searchField, JTable table, List<Payment> financeList) {
        String searchID = searchField.getText().trim().toUpperCase();
        boolean found = false;

        if (financeList.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No finance records loaded.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0); // Clear previous table data

        for (Payment finance : financeList) {
            if (finance.getPaymentID().equalsIgnoreCase(searchID)) {
                Object[] row = {
                    finance.getPaymentID(),
                    finance.getOrderIDsAsString(),
                    finance.getSupplierID(),
                    String.format("%.2f", finance.getAmount()),
                    finance.getPaymentDate().toString(),
                    finance.getPaymentMethod(),
                    finance.getStatus()
                };
                model.addRow(row); // Add matched finance record to the table
                found = true;
                
                searchField.setText(""); // Reset Search Field
                break;
            }
        }

        if (!found) {
            JOptionPane.showMessageDialog(null, "Payment ID not found.", "Not Found", JOptionPane.INFORMATION_MESSAGE);
            
            // Reload full finance list into table
            updateFinanceTableInUI(financeList, table);
        }
        // Reset Search Field
        searchField.setText("Enter Payment ID");
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
    
    // Apply row colors based on payment status
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
                } else if (status.equalsIgnoreCase(STATUS_UNFULFILLED)) {
                    c.setBackground(new Color(224, 224, 224)); // Light gray
                } else if (status.equalsIgnoreCase(STATUS_APPROVED)) {
                    c.setBackground(new Color(204, 229, 255)); // Light blue
                } else if (status.equalsIgnoreCase(STATUS_RECEIVED)) {
                    c.setBackground(new Color(255, 229, 204)); // Light orange
                } else if (status.equalsIgnoreCase(STATUS_VERIFIED)) {
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
    
    // Find finance record by payment ID
    public static Payment findByPaymentID(String paymentID) {
        List<Payment> allFinanceRecords = loadFinanceRecords();
        for (Payment finance : allFinanceRecords) {
            if (finance.getPaymentID().equalsIgnoreCase(paymentID)) {
                return finance;
            }
        }
        return null;
    }
    
    // Filter finance records by status
    public static void filterByStatus(String statusFilter, JTable table, List<Payment> financeList) {
        String[] columnNames = {"Payment ID", "Order IDs", "Supplier ID", "Amount (RM)", "Payment Date", "Payment Method", "Status"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        for (Payment finance : financeList) {
            boolean include = false;
            String status = finance.getStatus();
            
            if (statusFilter.equals("All")) {
                include = true;
            } else if (statusFilter.equalsIgnoreCase(status)) {
                include = true;
            }

            if (include) {
                Object[] row = {
                    finance.getPaymentID(),
                    finance.getOrderIDsAsString(),
                    finance.getSupplierID(),
                    String.format("%.2f", finance.getAmount()),
                    finance.getPaymentDate().toString(),
                    finance.getPaymentMethod(),
                    finance.getStatus()
                };
                model.addRow(row);
            }
        }

        table.setModel(model);
        autoResizeColumnWidths(table);
        applyRowColorBasedOnStatus(table);

        if (model.getRowCount() == 0) {
            JOptionPane.showMessageDialog(null, "No finance records found for status: " + statusFilter, "No Results", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    // Filter finance records by payment method
    public static void filterByPaymentMethod(String methodFilter, JTable table, List<Payment> financeList) {
        String[] columnNames = {"Payment ID", "Order IDs", "Supplier ID", "Amount (RM)", "Payment Date", "Payment Method", "Status"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        for (Payment finance : financeList) {
            boolean include = false;
            String method = finance.getPaymentMethod();
            
            if (methodFilter.equals("All")) {
                include = true;
            } else if (methodFilter.equalsIgnoreCase(method)) {
                include = true;
            }

            if (include) {
                Object[] row = {
                    finance.getPaymentID(),
                    finance.getOrderIDsAsString(),
                    finance.getSupplierID(),
                    String.format("%.2f", finance.getAmount()),
                    finance.getPaymentDate().toString(),
                    finance.getPaymentMethod(),
                    finance.getStatus()
                };
                model.addRow(row);
            }
        }

        table.setModel(model);
        autoResizeColumnWidths(table);
        applyRowColorBasedOnStatus(table);

        if (model.getRowCount() == 0) {
            JOptionPane.showMessageDialog(null, "No finance records found for payment method: " + methodFilter, "No Results", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    // Get total amount by status
    public static double getTotalAmountByStatus(List<Payment> financeList, String status) {
        double total = 0.0;
        for (Payment finance : financeList) {
            if (finance.getStatus().equalsIgnoreCase(status)) {
                total += finance.getAmount();
            }
        }
        return total;
    }
    
    // Get total amount for a specific supplier
    public static double getTotalAmountBySupplier(List<Payment> financeList, String supplierID) {
        double total = 0.0;
        for (Payment finance : financeList) {
            if (finance.getSupplierID().equalsIgnoreCase(supplierID)) {
                total += finance.getAmount();
            }
        }
        return total;
    }
    
    // Update payment status
    public static boolean updatePaymentStatus(String paymentID, String newStatus) {
        List<Payment> financeList = loadFinanceRecords();
        boolean updated = false;
        
        for (Payment finance : financeList) {
            if (finance.getPaymentID().equalsIgnoreCase(paymentID)) {
                finance.setStatus(newStatus);
                updated = true;
                break;
            }
        }
        
        if (updated) {
            return saveFinanceRecords(financeList);
        }
        
        return false;
    }
}