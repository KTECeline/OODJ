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


    public User(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
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

    @Override
    public String toString() {
        return username + "," + password + "," + role;
    }

    public static User fromString(String userData) {
        // split data by comma
        String[] data = userData.split(",");
        
        //check data length
        if (data.length != 3) {
            System.out.println("Error: Invalid user data format: " + userData);
            return null;
        }
        
        String username = data[0];
        String password = data[1];
        String role = data[2];

        return new User(username, password, role);
    }
    
    


}
