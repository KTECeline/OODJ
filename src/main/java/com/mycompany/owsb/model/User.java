package com.mycompany.owsb.model;


/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author timi
 */
public class User {
    private final String userId;     // New field with pattern U001, U002, etc.
    private final String username;
    private String password;
    private String role;
    
    private static int lastUserId = 0;  // To track the last assigned ID

    public User(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.userId = generateUserId();
    }
    
    // Constructor with userId (for loading existing users)
    public User(String userId, String username, String password, String role) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.role = role;
    }
    
    // Generate a new userId with pattern U001, U002, etc.
    private String generateUserId() {
        // Initialize lastUserId by checking existing users if needed
        if (lastUserId == 0) {
            initializeLastUserId();
        }
        
        // Increment and format the new ID
        lastUserId++;
        return String.format("US%04d", lastUserId);
    }
    
    // Initialize lastUserId by finding the highest existing ID from file data
    private void initializeLastUserId() {
        java.util.List<String> lines = FileUtil.readLines("user.txt");
        
        for (String line : lines) {
            String[] parts = line.split(",");
            if (parts.length >= 4 && parts[0].startsWith("US")) {
                try {
                    int idNum = Integer.parseInt(parts[0].substring(2));
                    if (idNum > lastUserId) {
                        lastUserId = idNum;
                    }
                } catch (NumberFormatException e) {
                    // Skip if format is invalid
                }
            }
        }
    }

    public String getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRole(String role) {
        this.role = role;
    }
    
    // Convert object to string to save it in file
    @Override
    public String toString() {
        return userId + "," + username + "," + password + "," + role;
    }
    
    // Convert the line in the file from String to object
    public static User fromString(String userData) {
        // split data by comma
        String[] data = userData.split(",");
        
        // Check if data has userId (new format) or not (old format)
        if (data.length == 4) {
            // New format with userId
            String userId = data[0];
            String username = data[1];
            String password = data[2];
            String role = data[3];
            
            return new User(userId, username, password, role);
        } else if (data.length == 3) {
            // Old format without userId - create with auto-generated ID
            String username = data[0];
            String password = data[1];
            String role = data[2];
            
            return new User(username, password, role);
        } else {
            System.out.println("Error: Invalid user data format: " + userData);
            return null;
        }
    }
}
