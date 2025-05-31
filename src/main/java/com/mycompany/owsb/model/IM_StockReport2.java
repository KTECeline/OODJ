package com.mycompany.owsb.model;

import java.io.*;
import java.util.*;
import javax.swing.table.DefaultTableModel;

public class IM_StockReport2 {

    /**
     * Retrieves all item IDs from the items.txt file to display the item selection list
     * @return Array of item IDs
     */
    public String[] getItemIDs() {
        List<String> allItemIDs = new ArrayList<>();
        //Get all Item ID from txt file for the filter
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
        //Convert the list to array when return
        return allItemIDs.toArray(new String[0]);
    }

    /**
     * Generates a table model containing stock movement records for a specific item
     * @param itemID The ID of the item to filter stock movements
     * @return DefaultTableModel with stock movement data for JTable display
     */
    public DefaultTableModel getStockMovementsForItem(String itemID) {

        // Set up column headers for the JTable
        String[] columns = {"Stock Received ID", "Purchase Order ID", "Item ID", "Amount Received", "Date Received", "User ID"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);

        //Read stock_received.txt file to match the correct row of data with the item ID
        try (BufferedReader br = new BufferedReader(new FileReader("data/stock_received.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                //Separate the column data
                String[] parts = line.split(",");
                if (parts.length >= 6) {
                    String srID = parts[0];
                    String poID = parts[1];
                    String itemIdFromFile = parts[2];
                    String amountReceived = parts[3];
                    String dateReceived = parts[4];
                    String userID = parts[5];

                    //Return the row if the item ID matched
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
