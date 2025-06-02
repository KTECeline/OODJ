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

// This class represents viewing of the purchase requisition.
public class Finance_ViewPR {
    private final String prID;
    private String supplierID;
    private LocalDate requiredDate;
    private String raisedBy;
    private String status;
    
    // String representing the file path for purchase requisition data
    private static final String PR_FILE = "data/purchase_requisition.txt";
    
    // Purchase requisition status constants - UPDATED
    public static final String STATUS_PENDING = "PENDING";
    public static final String STATUS_APPROVED = "APPROVED";
    public static final String STATUS_REJECTED = "REJECTED";
    public static final String STATUS_UNFULFILLED = "UNFULFILLED";
    public static final String STATUS_RECEIVED = "RECEIVED";
    public static final String STATUS_COMPLETED = "COMPLETED";

    public Finance_ViewPR(String prID, String supplierID, LocalDate requiredDate, 
                         String raisedBy, String status) {
        this.prID = prID;
        this.supplierID = supplierID;
        this.requiredDate = requiredDate;
        this.raisedBy = raisedBy;
        this.status = status;
    }
    
    // Getters
    public String getPrID() {
        return prID;
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
    
    // Setters
    public void setSupplierID(String supplierID) {
        this.supplierID = supplierID;
    }
    
    public void setRequiredDate(LocalDate requiredDate) {
        this.requiredDate = requiredDate;
    }
    
    public void setRaisedBy(String raisedBy) {
        this.raisedBy = raisedBy;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    // Convert object to string to save it in file
    @Override
    public String toString() {
        return prID + "," + supplierID + "," + requiredDate + "," + 
               raisedBy + "," + status;
    }
    
    // Convert the line in the file from String to object
    public static Finance_ViewPR fromString(String line) {
        String[] parts = line.split(",", 5);  // Split into exactly 5 parts
        
        String prID = parts[0];
        String supplierID = parts[1];
        LocalDate requiredDate = LocalDate.parse(parts[2]);
        String raisedBy = parts[3];
        String status = parts[4];
        
        return new Finance_ViewPR(prID, supplierID, requiredDate, raisedBy, status);
    }
    
    // Method to auto GENERATE PR ID from the last one
    public static String generateNextPRID(List<Finance_ViewPR> prList) {
        int maxNumber = 0;
        for (Finance_ViewPR pr : prList) {
            String id = pr.getPrID();
            if (id.startsWith("PR")) {
                try {
                    int num = Integer.parseInt(id.substring(2));
                    if (num > maxNumber) {
                        maxNumber = num;
                    }
                } catch (NumberFormatException ignored) {}
            }
        }
        return String.format("PR%04d", maxNumber + 1);
    }
    
    // Method to load Purchase Requisition records from a file
    public static List<Finance_ViewPR> loadPRRecords() {
        List<Finance_ViewPR> prList = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(PR_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                try {
                    Finance_ViewPR pr = Finance_ViewPR.fromString(line);
                    prList.add(pr);
                } catch (Exception e) {
                    System.err.println("Error parsing PR record: " + line + " - " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading PR records: " + e.getMessage());
        }
        return prList;
    }
    
    // Method to save Purchase Requisition records to file
    public static boolean savePRRecords(List<Finance_ViewPR> prList) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(PR_FILE))) {
            for (Finance_ViewPR pr : prList) {
                writer.write(pr.toString());
                writer.newLine();
            }
            return true;
        } catch (IOException e) {
            System.err.println("Error saving PR records: " + e.getMessage());
            return false;
        }
    }
    
    // Method to update PR table in the UI
    public static void updatePRTableInUI(List<Finance_ViewPR> prList, JTable targetTable) {
        String[] columnNames = {"PR ID", "Supplier ID", "Required Date", "Raised By", "Status"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);

        for (Finance_ViewPR pr : prList) {
            Object[] row = {
                pr.getPrID(),
                pr.getSupplierID(),
                pr.getRequiredDate().toString(),
                pr.getRaisedBy(),
                pr.getStatus()
            };
            tableModel.addRow(row);
        }

        targetTable.setModel(tableModel);
        autoResizeColumnWidths(targetTable);
        applyRowColorBasedOnStatus(targetTable);
    }
    
    // Search and display PR record in table
    public static void searchAndDisplayPRInTable(JTextField searchField, JTable table, List<Finance_ViewPR> prList) {
        String searchID = searchField.getText().trim().toUpperCase();
        boolean found = false;

        if (prList.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No PR records loaded.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0); // Clear previous table data

        for (Finance_ViewPR pr : prList) {
            if (pr.getPrID().equalsIgnoreCase(searchID)) {
                Object[] row = {
                    pr.getPrID(),
                    pr.getSupplierID(),
                    pr.getRequiredDate().toString(),
                    pr.getRaisedBy(),
                    pr.getStatus()
                };
                model.addRow(row); // Add matched PR record to the table
                found = true;
                
                searchField.setText(""); // Reset Search Field
                break;
            }
        }

        if (!found) {
            JOptionPane.showMessageDialog(null, "PR ID not found.", "Not Found", JOptionPane.INFORMATION_MESSAGE);
            
            // Reload full PR list into table
            updatePRTableInUI(prList, table);
        }
        // Reset Search Field
        searchField.setText("Enter PR ID");
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
    
    // Apply row colors based on PR status - UPDATED for new status values
    public static void applyRowColorBasedOnStatus(JTable table) {
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                        boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                String status = table.getModel().getValueAt(row, 4).toString(); // Column index 4 = "Status"

                if (status.equalsIgnoreCase(STATUS_APPROVED)) {
                    c.setBackground(new Color(204, 255, 204)); // Light green
                } else if (status.equalsIgnoreCase(STATUS_PENDING)) {
                    c.setBackground(new Color(255, 255, 204)); // Light yellow
                } else if (status.equalsIgnoreCase(STATUS_REJECTED)) {
                    c.setBackground(new Color(255, 204, 204)); // Light red
                } else if (status.equalsIgnoreCase(STATUS_UNFULFILLED)) {
                    c.setBackground(new Color(255, 230, 153)); // Light orange
                } else if (status.equalsIgnoreCase(STATUS_RECEIVED)) {
                    c.setBackground(new Color(204, 229, 255)); // Light blue
                } else if (status.equalsIgnoreCase(STATUS_COMPLETED)) {
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
    
    // Find PR record by PR ID
    public static Finance_ViewPR findByPRID(String prID) {
        List<Finance_ViewPR> allPRRecords = loadPRRecords();
        for (Finance_ViewPR pr : allPRRecords) {
            if (pr.getPrID().equalsIgnoreCase(prID)) {
                return pr;
            }
        }
        return null;
    }
    
    // Filter PR records by status
    public static void filterByStatus(String statusFilter, JTable table, List<Finance_ViewPR> prList) {
        String[] columnNames = {"PR ID", "Supplier ID", "Required Date", "Raised By", "Status"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        for (Finance_ViewPR pr : prList) {
            boolean include = false;
            String status = pr.getStatus();
            
            if (statusFilter.equals("All")) {
                include = true;
            } else if (statusFilter.equalsIgnoreCase(status)) {
                include = true;
            }

            if (include) {
                Object[] row = {
                    pr.getPrID(),
                    pr.getSupplierID(),
                    pr.getRequiredDate().toString(),
                    pr.getRaisedBy(),
                    pr.getStatus()
                };
                model.addRow(row);
            }
        }

        table.setModel(model);
        autoResizeColumnWidths(table);
        applyRowColorBasedOnStatus(table);

        if (model.getRowCount() == 0) {
            JOptionPane.showMessageDialog(null, "No PR records found for status: " + statusFilter, "No Results", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    // Filter PR records by supplier
    public static void filterBySupplier(String supplierFilter, JTable table, List<Finance_ViewPR> prList) {
        String[] columnNames = {"PR ID", "Supplier ID", "Required Date", "Raised By", "Status"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        for (Finance_ViewPR pr : prList) {
            boolean include = false;
            String supplier = pr.getSupplierID();
            
            if (supplierFilter.equals("All")) {
                include = true;
            } else if (supplierFilter.equalsIgnoreCase(supplier)) {
                include = true;
            }

            if (include) {
                Object[] row = {
                    pr.getPrID(),
                    pr.getSupplierID(),
                    pr.getRequiredDate().toString(),
                    pr.getRaisedBy(),
                    pr.getStatus()
                };
                model.addRow(row);
            }
        }

        table.setModel(model);
        autoResizeColumnWidths(table);
        applyRowColorBasedOnStatus(table);

        if (model.getRowCount() == 0) {
            JOptionPane.showMessageDialog(null, "No PR records found for supplier: " + supplierFilter, "No Results", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    // Filter PR records by raised by (user)
    public static void filterByRaisedBy(String userFilter, JTable table, List<Finance_ViewPR> prList) {
        String[] columnNames = {"PR ID", "Supplier ID", "Required Date", "Raised By", "Status"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        for (Finance_ViewPR pr : prList) {
            boolean include = false;
            String raisedBy = pr.getRaisedBy();
            
            if (userFilter.equals("All")) {
                include = true;
            } else if (userFilter.equalsIgnoreCase(raisedBy)) {
                include = true;
            }

            if (include) {
                Object[] row = {
                    pr.getPrID(),
                    pr.getSupplierID(),
                    pr.getRequiredDate().toString(),
                    pr.getRaisedBy(),
                    pr.getStatus()
                };
                model.addRow(row);
            }
        }

        table.setModel(model);
        autoResizeColumnWidths(table);
        applyRowColorBasedOnStatus(table);

        if (model.getRowCount() == 0) {
            JOptionPane.showMessageDialog(null, "No PR records found for user: " + userFilter, "No Results", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    // Get count of PRs by status
    public static int getCountByStatus(List<Finance_ViewPR> prList, String status) {
        int count = 0;
        for (Finance_ViewPR pr : prList) {
            if (pr.getStatus().equalsIgnoreCase(status)) {
                count++;
            }
        }
        return count;
    }
    
    // Get count of PRs by supplier
    public static int getCountBySupplier(List<Finance_ViewPR> prList, String supplierID) {
        int count = 0;
        for (Finance_ViewPR pr : prList) {
            if (pr.getSupplierID().equalsIgnoreCase(supplierID)) {
                count++;
            }
        }
        return count;
    }
    
    // Get PRs that are overdue (required date has passed and status is still PENDING or APPROVED)
    public static List<Finance_ViewPR> getOverduePRs(List<Finance_ViewPR> prList) {
        List<Finance_ViewPR> overduePRs = new ArrayList<>();
        LocalDate today = LocalDate.now();
        
        for (Finance_ViewPR pr : prList) {
            if ((pr.getStatus().equalsIgnoreCase(STATUS_PENDING) || 
                 pr.getStatus().equalsIgnoreCase(STATUS_APPROVED)) && 
                pr.getRequiredDate().isBefore(today)) {
                overduePRs.add(pr);
            }
        }
        return overduePRs;
    }
    
    // Update PR status
    public static boolean updatePRStatus(String prID, String newStatus) {
        List<Finance_ViewPR> prList = loadPRRecords();
        boolean updated = false;
        
        for (Finance_ViewPR pr : prList) {
            if (pr.getPrID().equalsIgnoreCase(prID)) {
                pr.setStatus(newStatus);
                updated = true;
                break;
            }
        }
        
        if (updated) {
            return savePRRecords(prList);
        }
        
        return false;
    }
    
    // Helper method to get all valid status options - NEW
    public static String[] getAllStatusOptions() {
        return new String[]{
            STATUS_PENDING,
            STATUS_APPROVED,
            STATUS_REJECTED,
            STATUS_UNFULFILLED,
            STATUS_RECEIVED,
            STATUS_COMPLETED
        };
    }
    
    // Helper method to check if a status is valid - NEW
    public static boolean isValidStatus(String status) {
        String[] validStatuses = getAllStatusOptions();
        for (String validStatus : validStatuses) {
            if (validStatus.equalsIgnoreCase(status)) {
                return true;
            }
        }
        return false;
    }
}