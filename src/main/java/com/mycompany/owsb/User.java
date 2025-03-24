package com.mycompany.owsb;


/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author timi
 */
public class User {
    private final String username;
    private String password;
    private String role;
    private int failedAttempts;
    private boolean isLocked;


    public User(String username, String password, String role, int failedAttempts, boolean isLocked) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.failedAttempts = failedAttempts;
        this.isLocked = isLocked;
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

    public int getFailedAttempts() {
        return failedAttempts;
    }

    public void incrementFailedAttempts() {
        this.failedAttempts++;
    }
    
    public void resetFailedAttempts() {
        this.failedAttempts = 0;
    }

    public boolean isLocked() {
        return isLocked;
    }

    public void setLocked(boolean isLocked) {
        this.isLocked = isLocked;
    }

    @Override
    public String toString() {
        return username + "," + password + "," + role + "," + failedAttempts + "," + (isLocked ? "locked" : "unlocked");
    }

    public static User fromString(String userData) {
        // split data by comma
        String[] data = userData.split(",");
        
        //check data length
        if (data.length != 5) {
            System.out.println("Error: Invalid user data format: " + userData);
            return null;
        }
        
        String username = data[0];
        String password = data[1];
        String role = data[2];
        //convert string to int
        int failedAttempts = Integer.parseInt(data[3]);
        boolean isLocked = data[4].equals("locked");

        return new User(username, password, role, failedAttempts, isLocked);
    }
    
    


}
