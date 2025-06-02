package com.mycompany.owsb.model;

import java.io.*;
import java.util.*;
import java.text.SimpleDateFormat;

public class AuditLog {
    private static final String AUDITPO_FILE = "data/audit_log.txt";
    private final SimpleDateFormat sdf;

    public AuditLog() {
        sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }

    // Generic method to log an action
    public void logAction(String userId, String role, String action, String details) {
        AuditLogEntry entry = new AuditLogEntry(userId, role, action, details);
        entry.save();
    }

    // Log PO creation
    public void logPOCreation(String userId, String role, String poId, String prId, String supplierId, String itemDetails) {
        AuditLogEntry.logPOCreation(userId, role, poId, prId, supplierId, itemDetails);
    }

    // Log PO deletion
    public void logPODeletion(String userId, String role, String poId, String itemId) {
        AuditLogEntry.logPODeletion(userId, role, poId, itemId);
    }

    // Log PO status change
    public void logPOStatusChange(String userId, String role, String poId, String itemId, String oldStatus, String newStatus) {
        AuditLogEntry.logPOStatusChange(userId, role, poId, itemId, oldStatus, newStatus);
    }

    // Read recent logs
    public List<String[]> getRecentLogs(int maxLogs) {
        List<String[]> logs = new ArrayList<>();
        List<AuditLogEntry> entries = AuditLogEntry.loadAll();
        int startIndex = Math.max(0, entries.size() - maxLogs);
        for (int i = entries.size() - 1; i >= startIndex && i >= 0; i--) {
            AuditLogEntry entry = entries.get(i);
            logs.add(new String[]{
                entry.getTimestamp(),
                entry.getUserId(),
                entry.getRole(),
                entry.getAction(),
                entry.getDetails()
            });
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
                timestamp, userId, role, action, details));
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
        List<AuditLogEntry> entries = AuditLogEntry.loadAll();
        for (AuditLogEntry entry : entries) {
            logs.add(new String[]{
                entry.getTimestamp(),
                entry.getUserId(),
                entry.getRole(),
                entry.getAction(),
                entry.getDetails()
            });
        }
        return logs;
    }

    public List<String[]> searchLogs(String keyword) {
        List<String[]> results = new ArrayList<>();
        List<AuditLogEntry> entries = AuditLogEntry.searchLogs(keyword.toLowerCase());
        for (AuditLogEntry entry : entries) {
            results.add(new String[]{
                entry.getTimestamp(),
                entry.getUserId(),
                entry.getRole(),
                entry.getAction(),
                entry.getDetails()
            });
        }
        return results;
    }
}