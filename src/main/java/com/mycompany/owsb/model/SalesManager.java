/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.owsb.model;

import java.awt.*;
import javax.swing.*;
import java.util.*;

/**
 *
 * @author timi
 */


public class SalesManager {

    private static final String ITEM_FILE = "data/items.txt";
    private static final String PO_FILE = "data/purchase_order.txt";
    
    private final java.util.List<Item> itemDataList = new ArrayList<>();

    public SalesManager() {

    }



   // Edit item details (UI + logic)
    public void editItem(Item itemToEdit) {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(6, 2));

        JTextField idField = new JTextField(itemToEdit.getItemID());
        idField.setEditable(false);
        idField.setBackground(Color.LIGHT_GRAY);

        JTextField nameField = new JTextField(itemToEdit.getItemName());
        JTextField supplierField = new JTextField(itemToEdit.getSupplierId());
        JTextField stockField = new JTextField(String.valueOf(itemToEdit.getStock()));
        JTextField costField = new JTextField(String.valueOf(itemToEdit.getCost()));
        JTextField priceField = new JTextField(String.valueOf(itemToEdit.getPrice()));

        panel.add(new JLabel("Item ID:"));
        panel.add(idField);
        panel.add(new JLabel("Item Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Supplier ID:"));
        panel.add(supplierField);
        panel.add(new JLabel("Stock:"));
        panel.add(stockField);
        panel.add(new JLabel("Cost (RM):"));
        panel.add(costField);
        panel.add(new JLabel("Price:"));
        panel.add(priceField);

        int option = JOptionPane.showConfirmDialog(null, panel, "Edit Item", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (option == JOptionPane.OK_OPTION) {
            try {
                String newName = nameField.getText().trim();
                String newSupplier = supplierField.getText().trim();
                int newStock = Integer.parseInt(stockField.getText().trim());
                double newCost = Double.parseDouble(costField.getText().trim());
                double newPrice = Double.parseDouble(priceField.getText().trim());

                // Update item object
                itemToEdit.setItemName(newName);
                itemToEdit.setSupplierId(newSupplier);
                itemToEdit.setStock(newStock);
                itemToEdit.setCost(newCost);
                itemToEdit.setPrice(newPrice);

                // Save the updated item
                saveItemChanges(itemToEdit);

            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Invalid input. Please enter correct numeric values.", "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Separate method to save changes to file
    public void saveItemChanges(Item updatedItem) {
        java.util.List<Item> currentItemList = Item.loadFromFile(ITEM_FILE);

        for (int i = 0; i < currentItemList.size(); i++) {
            if (currentItemList.get(i).getItemID().equalsIgnoreCase(updatedItem.getItemID())) {
                currentItemList.set(i, updatedItem);
                break;
            }
        }

        Item.saveToFile(currentItemList, ITEM_FILE);
    }
}


