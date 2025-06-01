package com.mycompany.owsb.model;

import java.io.*;
import java.util.*;

public class IM_UpdateStock {

    //File path for required data
    private final String poFile = "data/purchase_order.txt";
    private final String itemFile = "data/items.txt";
    private final String stockReceivedFile = "data/stock_received.txt";

    /**
     * Retrieves all purchase orders that are either Approved or Unfulfilled.
     * @return List of matching PO records, each record as a String array).
     */
    public List<String[]> getUnfulfilledApprovedPOs() {
        List<String[]> list = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(poFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 9 && (parts[6].equalsIgnoreCase("Approved") || parts[6].equalsIgnoreCase("Unfulfilled"))) {
                    list.add(parts);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    //Retrieve the total stock received before for the specific purchase order item
    public int getTotalReceivedAmount(String poID, String itemId) {
        int total = 0;
        //Get the data record from stock_received.txt
        try (BufferedReader br = new BufferedReader(new FileReader(stockReceivedFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                //Only get the specific purchase order item
                if (parts.length >= 3 && parts[1].equals(poID) && parts[2].equals(itemId)) {
                    total += Integer.parseInt(parts[3]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return total;
    }

    /**
     * Updates stock records based on a new stock receipt.
     *
     * @param poID Purchase Order ID
     * @param itemID Item ID
     * @param newReceived Quantity just received
     * @param totalOrdered Total ordered quantity for this PO
     * @param receivedDate Date the stock was received
     * @param userId User who received the stock
     * @return true if all updates were successful
     */
    public boolean updateStock(String poID, String itemID, int newReceived, int totalOrdered, String receivedDate, String userId) {
        //Get the amount received before
        int currentTotal = getTotalReceivedAmount(poID,itemID);
        //Total up the amount received
        int newTotal = currentTotal + newReceived;
        //Check if the amount received achieved the quantity ordered
        boolean isComplete = newTotal >= totalOrdered;

        //Update items.txt
        updateItemQuantity(itemID, newReceived);

        //Update purchase_order.txt status
        updatePOStatus(poID, itemID, isComplete ? "RECEIVED" : "UNFULFILLED", userId);

        //Add record to stock_received.txt
        return logStockReceived(poID, itemID, newReceived, receivedDate, userId);
    }

    /**
     * Updates item quantity in items.txt by adding received stock.
     * @param itemID Item ID
     * @param addAmount Quantity to add
     */
    private void updateItemQuantity(String itemID, int addAmount) {
        File file = new File(itemFile);
        List<String> updatedLines = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                //Find the specific item by comparing itemID
                //If the row's item id matches, it will add the amount on the stock quantity column
                if (parts[0].equals(itemID)) {
                    int qty = Integer.parseInt(parts[2]) + addAmount;
                    parts[2] = String.valueOf(qty);
                    updatedLines.add(String.join(",", parts));
                //Else it will just add the row into the list
                } else {
                    updatedLines.add(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Write the new data into txt file
        try (PrintWriter pw = new PrintWriter(new FileWriter(file))) {
            for (String updated : updatedLines) {
                pw.println(updated);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Updates the status of a purchase order in purchase_order.txt.
     * @param poID Purchase Order ID
     * @param newStatus New status to set ("RECEIVED" or "UNFULFILLED")
     */
    private void updatePOStatus(String poID, String itemID, String newStatus, String userId) {
        File file = new File(poFile);
        List<String> updatedLines = new ArrayList<>();
        boolean updated = false;
        
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                //Change the status if the purchase ID and item ID matched
                if (parts[0].equals(poID) && parts[1].equals(itemID)) {
                    parts[6] = newStatus;
                    updatedLines.add(String.join(",", parts));
                    updated = true;
                    
                    AuditLog auditLog = new AuditLog();
                    String action = "Purchase Order Status Updated by Inventory Manager";
                    String details = String.format("PO ID: %s, New Status: %s", poID, newStatus);
                    auditLog.logAction(userId, "Inventory Manager", action, details);
                } else {
                    updatedLines.add(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Write the new data into txt file
        try (PrintWriter pw = new PrintWriter(new FileWriter(file))) {
            for (String updatedLine : updatedLines) {
                pw.println(updatedLine);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Logs a new stock receipt entry in stock_received.txt.
     * @param poID Purchase Order ID
     * @param receivedAmount Amount received
     * @param receivedDate Date received
     * @param userId ID of the user who received the stock
     * @return true if successful
     */
    private boolean logStockReceived(String poID, String itemID, int receivedAmount, String receivedDate, String userId) {
        //Generate new unique stock_received ID
        String srID = generateSRID();
        //Construct the line to write into the file
        String line = srID + "," + poID + "," + itemID + "," + receivedAmount + "," + receivedDate + "," + userId;
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(stockReceivedFile, true))) {
            bw.write(line);
            bw.newLine();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    //New unique stock_received ID Generator
    private String generateSRID() {
        int count = 1;
        try (BufferedReader br = new BufferedReader(new FileReader(stockReceivedFile))) {
            while (br.readLine() != null) count++;
        } catch (IOException e) {
            // ignore
        }
        return String.format("SR%04d", count);
    }
}
