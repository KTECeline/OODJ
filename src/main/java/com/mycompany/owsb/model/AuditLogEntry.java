/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.owsb.model;

import java.io.*;
import java.util.*;
import java.text.SimpleDateFormat;

public class AuditLogEntry {
    private String timestamp;
    private String userId;
    private String role;
    private String action;
    private String details;

    private static final String AUDIT_LOG_FILE = "data/audit_log.txt";

    // Constructor for creating a new log entry
    public AuditLogEntry(String userId, String role, String action, String details) {
        this.timestamp = getCurrentTimestamp();
        this.userId = userId;
        this.role = role;
        this.action = action;
        this.details = details;
    }
    
    public AuditLogEntry(String timestamp, String userId, String role, String action, String details) {
        this.timestamp = timestamp;
        this.userId = userId;
        this.role = role;
        this.action = action;
        this.details = details;
    }

    // Save this log entry to file
    public void save() {
        File file = new File(AUDIT_LOG_FILE);
        try {
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
                writer.write(this.toLogString());
                writer.newLine();
            }

        } catch (IOException e) {
            System.err.println("Audit log write error: " + e.getMessage());
            javax.swing.JOptionPane.showMessageDialog(null,
                "Failed to write audit log: " + e.getMessage(),
                "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }

    // Converts the object to a CSV string
    public String toLogString() {
        return String.join(",", timestamp, userId, role, action, details);
    }

    // Static: Load all logs from file
    public static List<AuditLogEntry> loadAll() {
        List<AuditLogEntry> logs = new ArrayList<>();
        File file = new File(AUDIT_LOG_FILE);
        if (!file.exists()) {
            return logs;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    String[] parts = line.split(",", 5);
                    if (parts.length == 5) {
                        logs.add(new AuditLogEntry(parts[0], parts[1], parts[2], parts[3], parts[4]));
                    }
                }
            }
        } catch (IOException e) {
            javax.swing.JOptionPane.showMessageDialog(null,
                "Failed to read audit log: " + e.getMessage(),
                "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
        }

        return logs;
    }

    // Static: Search logs for keyword (case-insensitive)
    public static List<AuditLogEntry> searchLogs(String keyword) {
        List<AuditLogEntry> results = new ArrayList<>();
        for (AuditLogEntry log : loadAll()) {
            if (log.timestamp.toLowerCase().contains(keyword)
                || log.userId.toLowerCase().contains(keyword)
                || log.role.toLowerCase().contains(keyword)
                || log.action.toLowerCase().contains(keyword)
                || log.details.toLowerCase().contains(keyword)) {
                results.add(log);
            }
        }
        return results;
    }

    private String getCurrentTimestamp() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }

    // Getters
    public String getTimestamp() { return timestamp; }
    public String getUserId() { return userId; }
    public String getRole() { return role; }
    public String getAction() { return action; }
    public String getDetails() { return details; }

    // Helper methods to create and save common logs
    public static void logPOCreation(String userId, String role, String poId, String prId, String supplierId, String itemDetails) {
        String action = "PO Created by Purchase Manager";
        String details = String.format("PO:%s,PR:%s,Supplier:%s,Items:%s", poId, prId, supplierId, itemDetails);
        new AuditLogEntry(userId, role, action, details).save();
    }

    public static void logPODeletion(String userId, String role, String poId, String itemId) {
        String action = "PO Item Deleted by Purchase Manager";
        String details = String.format("PO:%s,Item:%s", poId, itemId);
        new AuditLogEntry(userId, role, action, details).save();
    }

    public static void logPOStatusChange(String userId, String role, String poId, String itemId, String oldStatus, String newStatus) {
        String action = "PO Status Changed by Purchase Manager";
        String details = String.format("PO:%s,Item:%s,Status:%s->%s", poId, itemId, oldStatus, newStatus);
        new AuditLogEntry(userId, role, action, details).save();
    }
}