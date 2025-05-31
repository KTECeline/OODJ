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

    //Retrieve the total stock received before for the specific purchase order 
    public int getTotalReceivedAmount(String poID, String itemId) {
        int total = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(stockReceivedFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 3 && parts[1].equals(poID) && parts[2].equals(itemId)) {
                    total += Integer.parseInt(parts[3]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return total;
    }

    public boolean updateStock(String poID, String itemID, int newReceived, int totalOrdered, String receivedDate, String userId) {
        int currentTotal = getTotalReceivedAmount(poID,itemID);
        int newTotal = currentTotal + newReceived;
        boolean isComplete = newTotal >= totalOrdered;

        // 1. Update items.txt
        updateItemQuantity(itemID, newReceived);

        // 2. Update purchase_order.txt status
        updatePOStatus(poID, itemID, isComplete ? "RECEIVED" : "UNFULFILLED");

        // 3. Add record to stock_received.txt
        return logStockReceived(poID, itemID, newReceived, receivedDate, userId);
    }

    private void updateItemQuantity(String itemID, int addAmount) {
        File file = new File(itemFile);
        List<String> updatedLines = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts[0].equals(itemID)) {
                    int qty = Integer.parseInt(parts[2]) + addAmount;
                    parts[2] = String.valueOf(qty);
                    updatedLines.add(String.join(",", parts));
                } else {
                    updatedLines.add(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (PrintWriter pw = new PrintWriter(new FileWriter(file))) {
            for (String updated : updatedLines) {
                pw.println(updated);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updatePOStatus(String poID, String itemID, String newStatus) {
        File file = new File(poFile);
        List<String> updatedLines = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts[0].equals(poID) && parts[1].equals(itemID)) {
                    parts[6] = newStatus;
                    updatedLines.add(String.join(",", parts));
                } else {
                    updatedLines.add(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (PrintWriter pw = new PrintWriter(new FileWriter(file))) {
            for (String updated : updatedLines) {
                pw.println(updated);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean logStockReceived(String poID, String itemID, int receivedAmount, String receivedDate, String userId) {
        String srID = generateSRID();
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
