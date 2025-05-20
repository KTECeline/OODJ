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


public class SalesManager extends Manager implements ManageItemInterface{
    // String representing the file path for item data
    private static final String ITEM_FILE = "data/items.txt";
    
    // String representing the file path for supplier data
    private static final String SUPPLIER_FILE = "data/suppliers.txt";
    
    public SalesManager(User loggedInUser) {
        super(loggedInUser);
    }
    
    @Override
    public boolean isAllowedToPerform(String action) {
        // Check if user is logged in and role matches "Sales"
        if (getLoggedInUser() == null || getLoggedInUser().getRole() == null || 
            !getLoggedInUser().getRole().equalsIgnoreCase("Sales Manager")) {
            return false;
        }

        // Create dialog for password input
        JDialog dialog = new JDialog((Frame) null, "Password Verification", true);
        dialog.setLayout(new BorderLayout());
        dialog.setBackground(Color.white);

        JPanel panel = new JPanel(new GridLayout(2, 2, 5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        panel.setBackground(Color.white);

        JLabel passwordLabel = new JLabel("Enter Password:");
        JPasswordField passwordField = new JPasswordField(20);
        JLabel errorLabel = new JLabel();
        errorLabel.setForeground(Color.RED);

        panel.add(passwordLabel);
        panel.add(passwordField);
        panel.add(new JLabel());
        panel.add(errorLabel);

        JButton submit = new JButton("Submit");
        JButton cancel = new JButton("Cancel");
        submit.setBackground(Color.red);
        submit.setForeground(Color.white);
        cancel.setBackground(Color.black);
        cancel.setForeground(Color.white);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.white);
        buttonPanel.add(submit);
        buttonPanel.add(cancel);

        dialog.add(panel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.pack();
        dialog.setLocationRelativeTo(null);

        final boolean[] isAuthenticated = {false};

        submit.addActionListener(e -> {
            errorLabel.setText("");
            String enteredPassword = new String(passwordField.getPassword()).trim();
            if (enteredPassword.isEmpty()) {
                errorLabel.setText("*Password is required.");
            } else if (getLoggedInUser().getPassword().equals(enteredPassword)) {
                isAuthenticated[0] = true;
                dialog.dispose();
            } else {
                errorLabel.setText("*Incorrect password.");
            }
        });

        cancel.addActionListener(e -> dialog.dispose());

        dialog.setVisible(true);

        return isAuthenticated[0];
    }
    
    //ITEM SECTION

    
    // Method to ADD new item
    @Override
    public void addItem(JFrame parent, List<Item> itemList, List<Supplier> supplierList, JTable itemTable) {
        if (!isAllowedToPerform("add item")) {
            JOptionPane.showMessageDialog(parent, "Not authorized to add items.", "Permission Denied", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String nextItemID = Item.generateNextItemID(itemList);

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
                
                // Show warning if price is lower than cost
                if (price < cost) {
                    int response = JOptionPane.showConfirmDialog(
                        dialog,
                        "Warning: Price is lower than cost.\nDo you want to continue?",
                        "Low Price Warning",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                    );

                    if (response != JOptionPane.YES_OPTION) {
                        return; // Stop further execution if user selects "No"
                    }
                }
                
            } catch (NumberFormatException ex) {
                priceError.setForeground(errorColor);
                priceError.setText("*Invalid price.");
                isValid = false;
            }
            
            
            // If all fields are valid, add the item
            if (isValid) {
                boolean lowStockAlert = Item.isLowStock(stock);
                
                Item newItem = new Item(nextItemID, itemName, supplierId, stock, cost, price,lowStockAlert);
                itemList.add(newItem);
                FileUtil.saveListToFile(ITEM_FILE, itemList);
                Item.updateItemTableInUI(itemList, itemTable);
                JOptionPane.showMessageDialog(dialog, "Item added successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
            }
        });

        cancel.addActionListener(e -> dialog.dispose());

        dialog.setVisible(true);
    }

    
    // Method to EDIT item details
    @Override
    public void editItem(Item itemToEdit, List<Item> itemList, List<Supplier> supplierList, JTable itemTable) {
        if (!isAllowedToPerform("edit item")) {
            JOptionPane.showMessageDialog(null, "Not authorized to edit items.", "Permission Denied", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
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

                FileUtil.saveListToFile(ITEM_FILE, itemList);
                Item.updateItemTableInUI(itemList, itemTable);
                JOptionPane.showMessageDialog(dialog, "Item updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
            }
        });

        cancelBtn.addActionListener(e -> dialog.dispose());

        dialog.setVisible(true);
    }

    
    // Method to DELETE item
    @Override
    public void deleteItem(JFrame parent, List<Item> itemList, JTable itemTable) {
        if (!isAllowedToPerform("delete item")) {
            JOptionPane.showMessageDialog(parent, "Not authorized to delete items.", "Permission Denied", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
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
                FileUtil.saveListToFile(ITEM_FILE, itemList); // Save the updated list to the file
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

    
    
    
    //SUPPLIER SECTION
    
    // Method to ADD new supplier
    public void addSupplier(JFrame parent, List<Supplier> supplierList, JTable supplierTable) {
        if (!isAllowedToPerform("add supplier")) {
            JOptionPane.showMessageDialog(null, "Not authorized to add suppliers.", "Permission Denied", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String nextSupplierID = Supplier.generateNextSupplierID(supplierList);
        
        // Input fields
        JTextField supplierNameField = new JTextField(20);
        JTextField emailField = new JTextField(20);

        // Error labels
        JLabel supplierNameError = new JLabel();
        JLabel emailError = new JLabel();

        Color errorColor = Color.RED;

        JPanel panel = new JPanel(new GridLayout(0, 2, 0, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        panel.setBackground(Color.white);

        panel.add(new JLabel("Supplier ID:"));
        panel.add(new JLabel(nextSupplierID));

        panel.add(new JLabel("Supplier Name:"));
        panel.add(supplierNameField);
        panel.add(new JLabel());
        panel.add(supplierNameError);

        panel.add(new JLabel("Email:"));
        panel.add(emailField);
        panel.add(new JLabel());
        panel.add(emailError);

        JDialog dialog = new JDialog(parent, "Add New Supplier", true);
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
            supplierNameError.setText("");
            emailError.setText("");

            String supplierName = supplierNameField.getText().trim();
            String email = emailField.getText().trim();

            boolean isValid = true;


            // Supplier Name validation
            if (supplierName.isEmpty()) {
                supplierNameError.setForeground(errorColor);
                supplierNameError.setText("*Supplier name is required.");
                isValid = false;
            }


            // Email validation
            if (email.isEmpty() || !email.matches("^[\\w.-]+@[\\w.-]+\\.\\w+$")) {
                emailError.setForeground(errorColor);
                emailError.setText("*Invalid email format.");
                isValid = false;
            }

            if (isValid) {
                Supplier newSupplier = new Supplier(nextSupplierID, supplierName, email);
                supplierList.add(newSupplier);
                FileUtil.saveListToFile(SUPPLIER_FILE, supplierList);
                Supplier.updateSupplierTableInUI(supplierList, supplierTable);
                JOptionPane.showMessageDialog(dialog, "Supplier added successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
            }
        });

        cancel.addActionListener(e -> dialog.dispose());

        dialog.setVisible(true);
    }

    // Method to EDIT supplier
    public void editSupplier(Supplier supplierToEdit, List<Supplier> supplierList, JTable supplierTable) {
        if (!isAllowedToPerform("edit supplier")) {
            JOptionPane.showMessageDialog(null, "Not authorized to edit suppliers.", "Permission Denied", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Input fields
        JTextField nameField = new JTextField(supplierToEdit.getSupplierName(), 20);
        JTextField emailField = new JTextField(supplierToEdit.getEmail(), 20);

        // Error labels
        JLabel nameError = new JLabel();
        JLabel emailError = new JLabel();
        Color errorColor = Color.RED;

        JPanel panel = new JPanel(new GridLayout(0, 2, 0, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        panel.setBackground(Color.white);

        // Supplier ID (not editable)
        panel.add(new JLabel("Supplier ID:"));
        JTextField idField = new JTextField(supplierToEdit.getSupplierID());
        idField.setEditable(false);
        idField.setBackground(Color.LIGHT_GRAY);
        panel.add(idField);

        // Supplier Name
        panel.add(new JLabel("Supplier Name:"));
        panel.add(nameField);
        panel.add(new JLabel());
        panel.add(nameError);
        
        // Email
        panel.add(new JLabel("Email:"));
        panel.add(emailField);
        panel.add(new JLabel());
        panel.add(emailError);

        // Buttons
        JButton saveBtn = new JButton("Save");
        JButton cancelBtn = new JButton("Cancel");
        JPanel btnPanel = new JPanel();
        btnPanel.add(saveBtn);
        btnPanel.add(cancelBtn);

        // Dialog
        JDialog dialog = new JDialog((Frame) null, "Edit Supplier", true);
        dialog.getContentPane().add(panel, BorderLayout.CENTER);
        dialog.getContentPane().add(btnPanel, BorderLayout.SOUTH);
        dialog.pack();
        dialog.setLocationRelativeTo(null);

        // Save action
        saveBtn.addActionListener(e -> {
            // Clear previous error messages
            nameError.setText("");
            emailError.setText("");

            String name = nameField.getText().trim();
            String email = emailField.getText().trim();

            boolean isValid = true;

            // Validation: name
            if (name.isEmpty()) {
                nameError.setForeground(errorColor);
                nameError.setText("*Supplier name is required.");
                isValid = false;
            }

            // Validation: email format
            if (email.isEmpty()) {
                emailError.setForeground(errorColor);
                emailError.setText("*Email is required.");
                isValid = false;
            } else if (!email.matches("^[\\w.-]+@[\\w.-]+\\.\\w+$")) {
                emailError.setForeground(errorColor);
                emailError.setText("*Invalid email format.");
                isValid = false;
            }

            // Save if all valid
            if (isValid) {
                supplierToEdit.setSupplierName(name);
                supplierToEdit.setEmail(email);

                FileUtil.saveListToFile(SUPPLIER_FILE, supplierList);
                Supplier.updateSupplierTableInUI(supplierList, supplierTable);
                JOptionPane.showMessageDialog(dialog, "Supplier updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
            }
        });

        cancelBtn.addActionListener(e -> dialog.dispose());

        dialog.setVisible(true);
    }

    // Method to DELETE supplier
    public void deleteSupplier(JFrame parent, List<Supplier> supplierList, List<Item> itemList, JTable supplierTable) {
        if (!isAllowedToPerform("delete supplier")) {
            JOptionPane.showMessageDialog(null, "Not authorized to delete suppliers.", "Permission Denied", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int selectedRow = supplierTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(parent, "Please select a supplier to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String supplierId = supplierTable.getValueAt(selectedRow, 0).toString();

        // Check if this supplier is linked to any item
        boolean isLinked = false;
        for (Item item : itemList) {
            if (item.getSupplierId().trim().equalsIgnoreCase(supplierId)) {
                isLinked = true;
                break;
            }
        }

        if (isLinked) {
            JOptionPane.showMessageDialog(parent,
                "This supplier is linked to one or more items and cannot be deleted.\nPlease update or remove related items first.",
                "Deletion Not Allowed", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Confirm deletion
        int response = JOptionPane.showConfirmDialog(
            parent,
            "Are you sure you want to delete this supplier?",
            "Confirm Deletion",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );

        if (response == JOptionPane.YES_OPTION) {
            Supplier supplierToDelete = null;
            for (Supplier supplier : supplierList) {
                if (supplier.getSupplierID().equals(supplierId)) {
                    supplierToDelete = supplier;
                    break;
                }
            }

            if (supplierToDelete != null) {
                supplierList.remove(supplierToDelete);
                FileUtil.saveListToFile(SUPPLIER_FILE, supplierList);
                Supplier.updateSupplierTableInUI(supplierList, supplierTable); // Assumes this method exists

                JOptionPane.showMessageDialog(parent, "Supplier deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(parent, "Supplier not found.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(parent, "Supplier deletion canceled.", "Canceled", JOptionPane.INFORMATION_MESSAGE);
        }
    }


    //DAILY SALES SECTION
    
}


