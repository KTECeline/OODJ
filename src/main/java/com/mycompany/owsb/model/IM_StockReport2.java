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
        // Collect unique itemIDs by scanning purchaseOrders list
        Set<String> itemIDs = new HashSet<>();
        for (String[] po : purchaseOrders) {
            itemIDs.add(po[1]);  // itemID is at index 1
        }
        return itemIDs.toArray(new String[0]);
    }

    public DefaultTableModel getStockMovementsForItem(String itemID) {
        String[] columns = { "Stock Received ID", "Purchase Order ID", "Item ID", "Item Name", "Amount Received", "Date Received" };
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
                if (parts.length >= 4) {
                    String poID = parts[1];

                    // Check if poID exists in filteredPOs
                    String[] matchingPO = null;
                    for (String[] po : filteredPOs) {
                        if (po[0].equals(poID)) {
                            matchingPO = po;
                            break;
                        }
                    }

                    if (matchingPO != null) {
                        String srID = parts[0];
                        int amountReceived = Integer.parseInt(parts[2]);
                        String dateReceived = parts[3];
                        String itemName = matchingPO[1];  // po[1] is itemID, better to get actual name? But data only has itemID here

                        // If you want itemName, you may need to load from items.txt or adjust this part accordingly.
                        // For now, just use itemID as itemName placeholder.

                        model.addRow(new Object[] { srID, poID, itemID, itemName, amountReceived, dateReceived });
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return model;
    }
}
