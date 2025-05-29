/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.owsb.model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author timi
 */
public class SupplierItem {
    private String supplierID;
    private String itemID;

    private static final String SUPPLIER_ITEM_FILE = "data/supplier_item.txt";

    public SupplierItem(String supplierID, String itemID) {
        this.supplierID = supplierID;
        this.itemID = itemID;
    }

    public String getSupplierID() {
        return supplierID;
    }

    public String getItemID() {
        return itemID;
    }

    @Override
    public String toString() {
        return supplierID + "," + itemID;
    }

    public static SupplierItem fromString(String line) {
        String[] parts = line.split(",");
        return new SupplierItem(parts[0], parts[1]);
    }

    public static List<SupplierItem> loadSupplierItems() {
        List<SupplierItem> list = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(SUPPLIER_ITEM_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                list.add(SupplierItem.fromString(line));
            }
        } catch (IOException e) {
            System.err.println("Error loading supplier items: " + e.getMessage());
        }
        return list;
    }

    public static void saveAll(List<SupplierItem> list) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(SUPPLIER_ITEM_FILE))) {
            for (SupplierItem si : list) {
                writer.write(si.toString());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error saving supplier items: " + e.getMessage());
        }
    }
}

