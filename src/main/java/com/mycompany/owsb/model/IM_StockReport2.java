package com.mycompany.owsb.model;

import java.io.*;
import java.util.*;
import javax.swing.table.DefaultTableModel;

public class IM_StockReport2 {

    // Store all purchase orders as List of String arrays
    private List<String[]> purchaseOrders = new ArrayList<>();

    public IM_StockReport2() {
        loadPurchaseOrders();
    }

    private void loadPurchaseOrders() {
        try (BufferedReader br = new BufferedReader(new FileReader("data/purchase_order.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 9) {
                    purchaseOrders.add(parts);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String[] getItemIDs() {
        List<String> allItemIDs = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader("data/items.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length > 0) {
                    allItemIDs.add(parts[0].trim());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return allItemIDs.toArray(new String[0]);
    }

    public DefaultTableModel getStockMovementsForItem(String itemID) {
        String[] columns = { "Stock Received ID", "Purchase Order ID", "Item ID", "Amount Received", "Date Received", "User ID" };
        DefaultTableModel model = new DefaultTableModel(columns, 0);

        // Find all purchase order IDs for the given itemID
        List<String[]> filteredPOs = new ArrayList<>();
        for (String[] po : purchaseOrders) {
            if (po[1].equals(itemID)) {
                filteredPOs.add(po);
            }
        }

        try (BufferedReader br = new BufferedReader(new FileReader("data/stock_received.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 6) {
                    String srID = parts[0];
                    String poID = parts[1];
                    String itemIdFromFile = parts[2];
                    String amountReceived = parts[3];
                    String dateReceived = parts[4];
                    String userID = parts[5];

                    if (itemID.equals(itemIdFromFile)) {
                        model.addRow(new Object[] { srID, poID, itemIdFromFile, amountReceived, dateReceived, userID });
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return model;
    }
}
