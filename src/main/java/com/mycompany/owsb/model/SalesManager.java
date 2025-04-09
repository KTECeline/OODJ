/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.owsb.model;

import java.awt.*;
import javax.swing.*;
import java.util.List;

/**
 *
 * @author timi
 */


public class SalesManager {
    public SalesManager() {

    }
    
    //ITEM SECTION
    
    // Method to auto generate ITEM ID from the last one
    public static String generateNextItemID(List<Item> itemList) {
        int maxNumber = 0;
        for (Item item : itemList) {
            String id = item.getItemID();
            if (id.startsWith("IT")) {
                try {
                    int num = Integer.parseInt(id.substring(2));
                    if (num > maxNumber) {
                        maxNumber = num;
                    }
                } catch (NumberFormatException ignored) {}
            }
        }
        return String.format("IT%04d", maxNumber + 1);
    }

    
    public void AddItem(JFrame parent, List<Item> itemList, List<Supplier> supplierList, JTable itemTable) {
        String nextItemID = generateNextItemID(itemList);

        // Input fields
        JTextField itemNameField = new JTextField(20);
        JTextField supplierIdField = new JTextField(20);
        JTextField stockField = new JTextField(20);
        JTextField costField = new JTextField(20);
        JTextField priceField = new JTextField(20);

        // Error labels
        JLabel itemNameError = new JLabel();
        JLabel supplierIdError = new JLabel();
        JLabel stockError = new JLabel();
        JLabel costError = new JLabel();
        JLabel priceError = new JLabel();

        // Red error style
        Color errorColor = Color.RED;
        
        JPanel panel = new JPanel(new GridLayout(0, 2, 0, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        panel.setBackground(Color.white);

        panel.add(new JLabel("Item ID:"));
        panel.add(new JLabel(nextItemID));

        panel.add(new JLabel("Item Name:"));
        panel.add(itemNameField);
        panel.add(new JLabel());
        panel.add(itemNameError);

        panel.add(new JLabel("Supplier ID:"));
        panel.add(supplierIdField);
        panel.add(new JLabel());
        panel.add(supplierIdError);

        panel.add(new JLabel("Stock:"));
        panel.add(stockField);
        panel.add(new JLabel());
        panel.add(stockError);

        panel.add(new JLabel("Cost (RM):"));
        panel.add(costField);
        panel.add(new JLabel());
        panel.add(costError);

        panel.add(new JLabel("Price (RM):"));
        panel.add(priceField);
        panel.add(new JLabel());
        panel.add(priceError);

        // Create dialog
        JDialog dialog = new JDialog(parent, "Add New Item", true);
        dialog.getContentPane().add(panel, BorderLayout.CENTER);

        JButton submit = new JButton("Add");
        JButton cancel = new JButton("Cancel");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(submit);
        buttonPanel.add(cancel);
        
        buttonPanel.setBackground(Color.white);
        
        submit.setBackground(Color.red);
        submit.setForeground(Color.white);
        cancel.setBackground(Color.black);
        cancel.setForeground(Color.white);
        
        dialog.getContentPane().add(buttonPanel, BorderLayout.SOUTH);

        dialog.pack();
        dialog.setLocationRelativeTo(parent);

        submit.addActionListener(e -> {
            // Reset error messages
            itemNameError.setText("");
            supplierIdError.setText("");
            stockError.setText("");
            costError.setText("");
            priceError.setText("");

            String itemName = itemNameField.getText().trim();
            String supplierId = supplierIdField.getText().trim().toUpperCase();
            String stockStr = stockField.getText().trim();
            String costStr = costField.getText().trim();
            String priceStr = priceField.getText().trim();

            boolean isValid = true;

            // Item name validation
            if (itemName.isEmpty()) {
                itemNameError.setForeground(errorColor);
                itemNameError.setText("*Item name is required.");
                isValid = false;
            }

            // Supplier ID validation
            if (supplierId.isEmpty()) {
                supplierIdError.setForeground(errorColor);
                supplierIdError.setText("*Supplier ID cannot be empty.");
                isValid = false;
            } else {
                if (!supplierId.matches("SP\\d{4}")) {
                    supplierIdError.setForeground(errorColor);
                    supplierIdError.setText("*Format must be (ex:SP0001).");
                    isValid = false;
                } else {
                    boolean supplierExists = false;
                    for (Supplier supplier : supplierList) {
                        if (supplier.getSupplierID().equalsIgnoreCase(supplierId)) {
                            supplierExists = true;
                            break;
                        }
                    }

                    if (!supplierExists) {
                        supplierIdError.setForeground(errorColor);  // Set error color
                        supplierIdError.setText("*Supplier ID does not exist.");
                        isValid = false;  // Mark as invalid to prevent further submission
                    } else {
                        supplierIdError.setText("");  // Clear any previous error messages if valid
                    }
                }
            }

            // Stock validation
            int stock = 0;
            try {
                stock = Integer.parseInt(stockStr);
                if (stock < 0) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                stockError.setForeground(errorColor);
                stockError.setText("*Stock must be a positive integer.");
                isValid = false;
            }

            // Cost validation
            double cost = 0.0;
            try {
                cost = Double.parseDouble(costStr);
                if (cost < 0) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                costError.setForeground(errorColor);
                costError.setText("*Invalid cost.");
                isValid = false;
            }

            // Price validation
            double price = 0.0;
            try {
                price = Double.parseDouble(priceStr);
                if (price < 0) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                priceError.setForeground(errorColor);
                priceError.setText("*Invalid price.");
                isValid = false;
            }

            // If all fields are valid, add the item
            if (isValid) {
                Item newItem = new Item(nextItemID, itemName, supplierId, stock, cost, price);
                itemList.add(newItem);
                Item.saveToFile(itemList);
                Item.updateItemTableInUI(itemList, itemTable);
                JOptionPane.showMessageDialog(dialog, "Item added successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
            }
        });

        cancel.addActionListener(e -> dialog.dispose());

        dialog.setVisible(true);
    }




    public void editItem(Item itemToEdit, List<Item> itemList, List<Supplier> supplierList, JTable itemTable) {
       // Input fields
       JTextField nameField = new JTextField(itemToEdit.getItemName(), 20);
       JTextField supplierField = new JTextField(itemToEdit.getSupplierId(), 20);
       JTextField stockField = new JTextField(String.valueOf(itemToEdit.getStock()), 20);
       JTextField costField = new JTextField(String.valueOf(itemToEdit.getCost()), 20);
       JTextField priceField = new JTextField(String.valueOf(itemToEdit.getPrice()), 20);

       // Error labels
       JLabel nameError = new JLabel();
       JLabel supplierIdError = new JLabel();
       JLabel stockError = new JLabel();
       JLabel costError = new JLabel();
       JLabel priceError = new JLabel();
       Color errorColor = Color.RED;

       JPanel panel = new JPanel(new GridLayout(0, 2, 0, 5));
       panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
       panel.setBackground(Color.white);

       panel.add(new JLabel("Item ID:"));
       JTextField idField = new JTextField(itemToEdit.getItemID());
       idField.setEditable(false);
       idField.setBackground(Color.LIGHT_GRAY);
       panel.add(idField);

       panel.add(new JLabel("Item Name:"));
       panel.add(nameField);
       panel.add(new JLabel());
       panel.add(nameError);

       panel.add(new JLabel("Supplier ID:"));
       panel.add(supplierField);
       panel.add(new JLabel());
       panel.add(supplierIdError);

       panel.add(new JLabel("Stock:"));
       panel.add(stockField);
       panel.add(new JLabel());
       panel.add(stockError);

       panel.add(new JLabel("Cost (RM):"));
       panel.add(costField);
       panel.add(new JLabel());
       panel.add(costError);

       panel.add(new JLabel("Price (RM):"));
       panel.add(priceField);
       panel.add(new JLabel());
       panel.add(priceError);

       // Dialog with buttons
       JDialog dialog = new JDialog((Frame) null, "Edit Item", true);
       dialog.getContentPane().add(panel, BorderLayout.CENTER);

       JButton saveBtn = new JButton("Save");
       JButton cancelBtn = new JButton("Cancel");

       JPanel btnPanel = new JPanel();
       btnPanel.add(saveBtn);
       btnPanel.add(cancelBtn);
       dialog.getContentPane().add(btnPanel, BorderLayout.SOUTH);

       dialog.pack();
       dialog.setLocationRelativeTo(null);

       saveBtn.addActionListener(e -> {
            // Clear previous error messages
            nameError.setText("");
            supplierIdError.setText("");
            stockError.setText("");
            costError.setText("");
            priceError.setText("");

            String name = nameField.getText().trim();
            String supplierId = supplierField.getText().trim();
            String stockStr = stockField.getText().trim();
            String costStr = costField.getText().trim();
            String priceStr = priceField.getText().trim();

            boolean isValid = true;

            if (name.isEmpty()) {
                nameError.setForeground(errorColor);
                nameError.setText("Item name is required.");
                isValid = false;
            }

            // Supplier ID validation
            if (supplierId.isEmpty()) {
                supplierIdError.setForeground(errorColor);
                supplierIdError.setText("*Supplier ID cannot be empty.");
                isValid = false;
            } else {
                if (!supplierId.matches("SP\\d{4}")) {
                    supplierIdError.setForeground(errorColor);
                    supplierIdError.setText("*Format must be (ex:SP0001).");
                    isValid = false;
                } else {
                    boolean supplierExists = false;
                    for (Supplier supplier : supplierList) {
                        if (supplier.getSupplierID().equalsIgnoreCase(supplierId)) {
                            supplierExists = true;
                            break;
                        }
                    }

                    if (!supplierExists) {
                        supplierIdError.setForeground(errorColor);  // Set error color
                        supplierIdError.setText("*Supplier ID does not exist.");
                        isValid = false;  // Mark as invalid to prevent further submission
                    } else {
                        supplierIdError.setText("");  // Clear any previous error messages if valid
                    }
                }
            }

           int stock = 0;
           try {
               stock = Integer.parseInt(stockStr);
               if (stock < 0) throw new NumberFormatException();
           } catch (NumberFormatException ex) {
               stockError.setForeground(errorColor);
               stockError.setText("Invalid stock value.");
               isValid = false;
           }

           double cost = 0;
           try {
               cost = Double.parseDouble(costStr);
               if (cost < 0) throw new NumberFormatException();
           } catch (NumberFormatException ex) {
               costError.setForeground(errorColor);
               costError.setText("Invalid cost.");
               isValid = false;
           }

           double price = 0;
           try {
               price = Double.parseDouble(priceStr);
               if (price < 0) throw new NumberFormatException();
           } catch (NumberFormatException ex) {
               priceError.setForeground(errorColor);
               priceError.setText("Invalid price.");
               isValid = false;
           }

           if (isValid) {
               itemToEdit.setItemName(name);
               itemToEdit.setSupplierId(supplierId);
               itemToEdit.setStock(stock);
               itemToEdit.setCost(cost);
               itemToEdit.setPrice(price);

               Item.saveToFile(itemList);
               Item.updateItemTableInUI(itemList, itemTable);
               JOptionPane.showMessageDialog(dialog, "Item updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
               dialog.dispose();
           }
       });

       cancelBtn.addActionListener(e -> dialog.dispose());

       dialog.setVisible(true);
   }

    
    
    public void deleteItem(JFrame parent, List<Item> itemList, JTable itemTable) {
        int selectedRow = itemTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(parent, "Please select an item to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String itemId = itemTable.getValueAt(selectedRow, 0).toString();

        // Ask for confirmation before deleting the item
        int response = JOptionPane.showConfirmDialog(
            parent, // Parent frame
            "Are you sure you want to delete this item?", // Confirmation message
            "Confirm Deletion", // Dialog title
            JOptionPane.YES_NO_OPTION, // Options: Yes or No
            JOptionPane.QUESTION_MESSAGE // Message type
        );

        // If the user clicks Yes, proceed with the deletion
        if (response == JOptionPane.YES_OPTION) {
            // Remove item from list
            Item itemToDelete = null;
            for (Item item : itemList) {
                if (item.getItemID().equals(itemId)) {
                    itemToDelete = item;
                    break;
                }
            }

            if (itemToDelete != null) {
                itemList.remove(itemToDelete); // Remove the item from the list
                Item.saveToFile(itemList); // Save the updated list to the file
                Item.updateItemTableInUI(itemList, itemTable); // Update the table after deletion

                JOptionPane.showMessageDialog(parent, "Item deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(parent, "Item not found.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            // If the user clicks No, show cancellation message
            JOptionPane.showMessageDialog(parent, "Item deletion canceled.", "Canceled", JOptionPane.INFORMATION_MESSAGE);
        }
    }


    

    // Separate method to save changes to file
    public void saveItemChanges(Item updatedItem) {
        java.util.List<Item> currentItemList = Item.loadItems();

        for (int i = 0; i < currentItemList.size(); i++) {
            if (currentItemList.get(i).getItemID().equalsIgnoreCase(updatedItem.getItemID())) {
                currentItemList.set(i, updatedItem);
                break;
            }
        }

        Item.saveToFile(currentItemList);
    }
}


