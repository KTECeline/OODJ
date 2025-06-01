/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.mycompany.owsb.model;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class AuditLog {
    private static final String AUDITPO_FILE = "data/audit_log.txt";
    private final SimpleDateFormat sdf;

    public AuditLog() {
        sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }

    // Generic method to log an action
    public void logAction(String userId, String role, String action, String details) {
        String timestamp = sdf.format(new Date());
        String logEntry = String.format("%s,%s,%s,%s,%s", timestamp, userId, role, action, details);
        System.out.println("AuditLog writing: " + logEntry);
        try {
            File file = new File(AUDITPO_FILE);
            if (!file.exists()) {
                
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(AUDITPO_FILE, true))) {
                writer.write(logEntry);
                writer.newLine();
                writer.close();
            }
        } catch (IOException e) {
            System.err.println("Audit log write error: " + e.getMessage());
            javax.swing.JOptionPane.showMessageDialog(null, "Failed to write audit log: " + e.getMessage(),
                    "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }

    // Log PO creation
    public void logPOCreation(String userId, String role, String poId, String prId, String supplierId, String itemDetails) {
        String action = "PO Created by Purchase Manager";
        String details = String.format("PO:%s,PR:%s,Supplier:%s,Items:%s", poId, prId, supplierId, itemDetails);
        logAction(userId, role, action, details);
    }


    // Log PO deletion
    public void logPODeletion(String userId, String role, String poId, String itemId) {
        String action = "PO Item Deleted by Purchase Manager";
        String details = String.format("PO:%s,Item:%s", poId, itemId);
        logAction(userId, role, action, details);
    }

    // Log PO status change
    public void logPOStatusChange(String userId, String role, String poId, String itemId, String oldStatus, String newStatus) {
        String action = "PO Status Changed by Purchase Manager";
        String details = String.format("PO:%s,Item:%s,Status:%s->%s", poId, itemId, oldStatus, newStatus);
        logAction(userId, role, action, details);
    }

    // Read recent logs
    public List<String[]> getRecentLogs(int maxLogs) {
        List<String[]> logs = new ArrayList<>();
        List<String> allLines = new ArrayList<>();
        try {
            File file = new File(AUDITPO_FILE);
            if (!file.exists()) {
                return logs;
            }
            BufferedReader reader = new BufferedReader(new FileReader(AUDITPO_FILE));
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    allLines.add(line);
                }
            }
            reader.close();
        } catch (IOException e) {
            javax.swing.JOptionPane.showMessageDialog(null, "Failed to read audit log: " + e.getMessage(),
                    "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
            return logs;
        }
        int startIndex = Math.max(0, allLines.size() - maxLogs);
        for (int i = allLines.size() - 1; i >= startIndex && i >= 0; i--) {
            String[] parts = allLines.get(i).split(",", 5);
            if (parts.length == 5) {
                logs.add(parts);
            }
        }
        return logs;
    }
    
    public String formatLogsForDisplay(int maxLogs) {
    List<String[]> logs = getRecentLogs(maxLogs);
    if (logs.isEmpty()) {
        return "No recent audit logs available.";
    }

    StringBuilder displayText = new StringBuilder();
    for (String[] log : logs) {
        String timestamp = log[0];
        String userId = log[1];
        String role = log[2];
        String action = log[3];
        String details = log[4].replace(",", "; "); // Replace commas for readability
        displayText.append(String.format("[%s] %s (%s): %s - %s%n",
                timestamp, userId, role, action, details)); // %n is newline
    }
    return displayText.toString();
}
    
    public String getLatestLog() {
    List<String[]> logs = getRecentLogs(1);
    if (logs.isEmpty()) {
        return "No recent audit logs available.";
    }

    String[] log = logs.get(0);
    String timestamp = log[0];
    String userId = log[1];
    String role = log[2];
    String action = log[3];
    
    return String.format(
        "Time:     %s\n User:     %s\n Role:     %s\n Action:   %s",
        timestamp, userId, role, action
    );
}

public List<String[]> getAllLogs() {
    List<String[]> logs = new ArrayList<>();
    try {
        File file = new File(AUDITPO_FILE);
        if (!file.exists()) {
            return logs;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(AUDITPO_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    String[] parts = line.split(",", 5);
                    if (parts.length == 5) {
                        logs.add(parts);
                    }
                }
            }
        }
    } catch (IOException e) {
        javax.swing.JOptionPane.showMessageDialog(null, "Failed to read audit log: " + e.getMessage(),
                "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
    }
    return logs;
}

public List<String[]> searchLogs(String keyword) {
    List<String[]> allLogs = getAllLogs();
    List<String[]> results = new ArrayList<>();

    for (String[] log : allLogs) {
        // Check if any field contains the keyword (case insensitive)
        for (String field : log) {
            if (field.toLowerCase().contains(keyword)) {
                results.add(log);
                break;
            }
        }
    }
    return results;
}
}