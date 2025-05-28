/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.owsb.model;

import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import javax.swing.*;
import java.util.List;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 *
 * @author timi
 */


public class SalesManager extends Manager implements ManageItemInterface{
    // String representing the file path for item data
    private static final String ITEM_FILE = "data/items.txt";
    
    // String representing the file path for supplier data
    private static final String SUPPLIER_FILE = "data/suppliers.txt";
    
    // String representing the file path for supplier data
    private static final String SUPPLIER_ITEM_FILE = "data/supplier_item.txt";
    
    // String representing the file path for pr data
    private static final String PURCHASE_REQUISITION_FILE = "data/purchase_requisition.txt";
    
    // String representing the file path for pr data
    private static final String PURCHASE_REQUISITION_ITEM_FILE = "data/purchase_requisition_item.txt";
    
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
        JTextField stockField = new JTextField(20);
        JTextField costField = new JTextField(20);
        JTextField priceField = new JTextField(20);

        // Error labels
        JLabel itemNameError = new JLabel();
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
            stockError.setText("");
            costError.setText("");
            priceError.setText("");

            String itemName = itemNameField.getText().trim();
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
                
                Item newItem = new Item(nextItemID, itemName, stock, cost, price,lowStockAlert);
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
        JTextField stockField = new JTextField(String.valueOf(itemToEdit.getStock()), 20);
        JTextField costField = new JTextField(String.valueOf(itemToEdit.getCost()), 20);
        JTextField priceField = new JTextField(String.valueOf(itemToEdit.getPrice()), 20);

        // Error labels
        JLabel nameError = new JLabel();
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
             stockError.setText("");
             costError.setText("");
             priceError.setText("");

             String name = nameField.getText().trim();
             String stockStr = stockField.getText().trim();
             String costStr = costField.getText().trim();
             String priceStr = priceField.getText().trim();

             boolean isValid = true;

             if (name.isEmpty()) {
                 nameError.setForeground(errorColor);
                 nameError.setText("Item name is required.");
                 isValid = false;
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
    public void addSupplier(JFrame parent, List<Supplier> supplierList, List<SupplierItem> supplierItemList, List<Item> itemList, JTable supplierTable) {
        if (!isAllowedToPerform("add supplier")) {
            JOptionPane.showMessageDialog(null, "Not authorized to add suppliers.", "Permission Denied", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String nextSupplierID = Supplier.generateNextSupplierID(supplierList);

        JTextField supplierNameField = new JTextField(20);
        JTextField emailField = new JTextField(20);

        JLabel supplierNameError = new JLabel();
        JLabel emailError = new JLabel();
        Color errorColor = Color.RED;

        JPanel outerPanel = new JPanel(new BorderLayout());
        outerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        outerPanel.setBackground(Color.white);

        // Form panel (top)
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 0, 5));
        formPanel.setBackground(Color.white);

        formPanel.add(new JLabel("Supplier ID:"));
        formPanel.add(new JLabel(nextSupplierID));

        formPanel.add(new JLabel("Supplier Name:"));
        formPanel.add(supplierNameField);
        formPanel.add(new JLabel());
        formPanel.add(supplierNameError);

        formPanel.add(new JLabel("Email:"));
        formPanel.add(emailField);
        formPanel.add(new JLabel());
        formPanel.add(emailError);

        outerPanel.add(formPanel, BorderLayout.NORTH);

        // Checkbox panel (center)
        List<JCheckBox> itemCheckboxes = new ArrayList<>();
        JPanel itemPanel = new JPanel();
        itemPanel.setLayout(new BoxLayout(itemPanel, BoxLayout.Y_AXIS));
        itemPanel.setBackground(Color.white);

        for (Item item : itemList) {
            JCheckBox itemCheckbox = new JCheckBox(item.getItemID() + " - " + item.getItemName());
            itemCheckbox.setBackground(Color.white);
            itemCheckboxes.add(itemCheckbox);
            itemPanel.add(itemCheckbox);
        }

        JScrollPane scrollPane = new JScrollPane(itemPanel);
        scrollPane.setPreferredSize(new Dimension(300, 150));

        JPanel checkboxContainer = new JPanel(new BorderLayout());
        checkboxContainer.add(new JLabel("Linked Items:"), BorderLayout.NORTH);
        checkboxContainer.add(scrollPane, BorderLayout.CENTER);
        checkboxContainer.setBackground(Color.white);

        outerPanel.add(checkboxContainer, BorderLayout.CENTER);

        // Buttons (bottom)
        JButton submit = new JButton("Add");
        JButton cancel = new JButton("Cancel");
        submit.setBackground(Color.RED);
        submit.setForeground(Color.WHITE);
        cancel.setBackground(Color.BLACK);
        cancel.setForeground(Color.WHITE);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(submit);
        buttonPanel.add(cancel);
        buttonPanel.setBackground(Color.white);

        outerPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Dialog setup
        JDialog dialog = new JDialog(parent, "Add New Supplier", true);
        dialog.getContentPane().add(outerPanel);
        dialog.pack();
        dialog.setLocationRelativeTo(parent);

        submit.addActionListener(e -> {
            supplierNameError.setText("");
            emailError.setText("");

            String supplierName = supplierNameField.getText().trim();
            String email = emailField.getText().trim();

            boolean isValid = true;

            // Supplier name validation
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

                // Save linked supplier-item
                for (JCheckBox cb : itemCheckboxes) {
                    if (cb.isSelected()) {
                        String itemId = cb.getText().split(" - ")[0];  // Only the item ID part
                        supplierItemList.add(new SupplierItem(nextSupplierID, itemId));
                    }
                }

                FileUtil.saveListToFile(SUPPLIER_FILE, supplierList);
                FileUtil.saveListToFile(SUPPLIER_ITEM_FILE, supplierItemList);
                Supplier.updateSupplierTableInUI(supplierList, supplierItemList, supplierTable);

                JOptionPane.showMessageDialog(dialog, "Supplier added successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
            }
        });

        cancel.addActionListener(e -> dialog.dispose());

        dialog.setVisible(true);
    }

    
    public void editSupplier(Supplier supplierToEdit, List<Supplier> supplierList, List<SupplierItem> supplierItemList, List<Item> itemList, JTable supplierTable) {
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

        // Outer panel using BorderLayout
        JPanel outerPanel = new JPanel(new BorderLayout());
        outerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        outerPanel.setBackground(Color.white);

        // Form panel (top)
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 0, 5));
        formPanel.setBackground(Color.white);

        formPanel.add(new JLabel("Supplier ID:"));
        JTextField idField = new JTextField(supplierToEdit.getSupplierID());
        idField.setEditable(false);
        idField.setBackground(Color.LIGHT_GRAY);
        formPanel.add(idField);

        formPanel.add(new JLabel("Supplier Name:"));
        formPanel.add(nameField);
        formPanel.add(new JLabel());
        formPanel.add(nameError);

        formPanel.add(new JLabel("Email:"));
        formPanel.add(emailField);
        formPanel.add(new JLabel());
        formPanel.add(emailError);

        outerPanel.add(formPanel, BorderLayout.NORTH);

        // Checkbox panel (center)
        List<JCheckBox> itemCheckboxes = new ArrayList<>();
        JPanel itemPanel = new JPanel();
        itemPanel.setLayout(new BoxLayout(itemPanel, BoxLayout.Y_AXIS));
        itemPanel.setBackground(Color.white);

        for (Item item : itemList) {
            JCheckBox itemCheckbox = new JCheckBox(item.getItemID());

            // Check if this item is already linked to the supplier
            boolean isLinked = false;
            for (SupplierItem si : supplierItemList) {
                if (si.getSupplierID().equalsIgnoreCase(supplierToEdit.getSupplierID()) &&
                    si.getItemID().equalsIgnoreCase(item.getItemID())) {
                    isLinked = true;
                    break;
                }
            }

            itemCheckbox.setSelected(isLinked);
            itemCheckboxes.add(itemCheckbox);
            itemPanel.add(itemCheckbox);
        }

        JScrollPane scrollPane = new JScrollPane(itemPanel);
        scrollPane.setPreferredSize(new Dimension(300, 150));

        JPanel checkboxContainer = new JPanel(new BorderLayout());
        checkboxContainer.add(new JLabel("Linked Items:"), BorderLayout.NORTH);
        checkboxContainer.add(scrollPane, BorderLayout.CENTER);
        checkboxContainer.setBackground(Color.white);

        outerPanel.add(checkboxContainer, BorderLayout.CENTER);

        // Buttons (bottom)
        JButton saveBtn = new JButton("Save");
        JButton cancelBtn = new JButton("Cancel");
        
        saveBtn.setBackground(Color.RED);
        saveBtn.setForeground(Color.WHITE);

        cancelBtn.setBackground(Color.BLACK);
        cancelBtn.setForeground(Color.WHITE);

        JPanel btnPanel = new JPanel();
        btnPanel.add(saveBtn);
        btnPanel.add(cancelBtn);
        btnPanel.setBackground(Color.white);
        
        outerPanel.add(btnPanel, BorderLayout.SOUTH);

        // Dialog setup
        JDialog dialog = new JDialog((Frame) null, "Edit Supplier", true);
        dialog.getContentPane().add(outerPanel);
        dialog.pack();
        dialog.setLocationRelativeTo(null);

        // Save action
        saveBtn.addActionListener(e -> {
            nameError.setText("");
            emailError.setText("");

            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            boolean isValid = true;

            if (name.isEmpty()) {
                nameError.setForeground(errorColor);
                nameError.setText("*Supplier name is required.");
                isValid = false;
            }

            if (email.isEmpty()) {
                emailError.setForeground(errorColor);
                emailError.setText("*Email is required.");
                isValid = false;
            } else if (!email.matches("^[\\w.-]+@[\\w.-]+\\.\\w+$")) {
                emailError.setForeground(errorColor);
                emailError.setText("*Invalid email format.");
                isValid = false;
            }

            if (isValid) {
                supplierToEdit.setSupplierName(name);
                supplierToEdit.setEmail(email);

                // Update supplier-item links
                supplierItemList.removeIf(si -> si.getSupplierID().equalsIgnoreCase(supplierToEdit.getSupplierID()));
                for (JCheckBox cb : itemCheckboxes) {
                    if (cb.isSelected()) {
                        supplierItemList.add(new SupplierItem(supplierToEdit.getSupplierID(), cb.getText()));
                    }
                }

                FileUtil.saveListToFile(SUPPLIER_FILE, supplierList);
                FileUtil.saveListToFile(SUPPLIER_ITEM_FILE, supplierItemList);

                Supplier.updateSupplierTableInUI(supplierList, supplierItemList, supplierTable);
                JOptionPane.showMessageDialog(dialog, "Supplier updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
            }
        });

        cancelBtn.addActionListener(e -> dialog.dispose());

        dialog.setVisible(true);
    }


    // Method to DELETE supplier
    public void deleteSupplier(JFrame parent, List<Supplier> supplierList, List<SupplierItem> supplierItemList, JTable supplierTable) {
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
        for (SupplierItem supplierItem : supplierItemList) {
            if (supplierItem.getSupplierID().trim().equalsIgnoreCase(supplierId)) {
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
                Supplier.updateSupplierTableInUI(supplierList, supplierItemList, supplierTable);

                JOptionPane.showMessageDialog(parent, "Supplier deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(parent, "Supplier not found.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(parent, "Supplier deletion canceled.", "Canceled", JOptionPane.INFORMATION_MESSAGE);
        }
    }


    //PURCHASE REQUISITION SECTION
    public void addPurchaseRequisition(JFrame parent, List<Item> itemList, List<PurchaseRequisition> prList, List<PurchaseRequisitionItem> prItemList, List<Supplier> supplierList, List<SupplierItem> supplierItemList, JTable prTable) {
        if (!isAllowedToPerform("add pr")) {
            JOptionPane.showMessageDialog(parent, "Not authorized to add purchase requisition.", "Permission Denied", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String raisedBy = getLoggedInUser().getUsername();
        
        JTextField itemIDField = new JTextField(20);
        JTextField quantityField = new JTextField(20);
        JTextField requiredDateField = new JTextField(20); // Expected format: YYYY-MM-DD
        
        JPanel supplierSelectorPanel = new JPanel(new CardLayout());
        JButton selectSupplierBtn = new JButton("Select Supplier");
        JComboBox<String> supplierComboBox = new JComboBox<>();
        
        supplierSelectorPanel.add(selectSupplierBtn, "BUTTON");
        supplierSelectorPanel.add(supplierComboBox, "COMBO");

        // Error labels
        JLabel itemIDError = new JLabel();
        JLabel supplierError = new JLabel();
        JLabel quantityError = new JLabel();
        JLabel dateError = new JLabel();
        
        Color errorColor = Color.RED;

        JPanel panel = new JPanel(new GridLayout(0, 2, 0, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        panel.setBackground(Color.white);

        panel.add(new JLabel("Item ID:"));
        panel.add(itemIDField); panel.add(new JLabel()); panel.add(itemIDError);
        
        panel.add(new JLabel("Supplier:"));
        panel.add(supplierSelectorPanel); panel.add(new JLabel()); panel.add(supplierError);
        
        panel.add(new JLabel("Quantity:"));
        panel.add(quantityField); panel.add(new JLabel()); panel.add(quantityError);

        panel.add(new JLabel("Required Date (YYYY-MM-DD):"));
        panel.add(requiredDateField); panel.add(new JLabel()); panel.add(dateError);

        JDialog dialog = new JDialog(parent, "Add Purchase Requisition", true);
        dialog.getContentPane().add(panel, BorderLayout.CENTER);

        JButton addBtn = new JButton("Add");
        JButton cancelBtn = new JButton("Cancel");
        JPanel btnPanel = new JPanel();
        btnPanel.add(addBtn); btnPanel.add(cancelBtn);
        dialog.getContentPane().add(btnPanel, BorderLayout.SOUTH);

        dialog.pack();
        dialog.setLocationRelativeTo(parent);

        CardLayout cl = (CardLayout) (supplierSelectorPanel.getLayout());
        
        // Reset to BUTTON whenever itemIDField changes
        itemIDField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                resetSupplierSelector();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                resetSupplierSelector();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                resetSupplierSelector();
            }

            private void resetSupplierSelector() {
                cl.show(supplierSelectorPanel, "BUTTON");  // show button again
                supplierComboBox.removeAllItems();         // clear previous combo items
            }
        });

        selectSupplierBtn.addActionListener(ev -> {
            supplierComboBox.removeAllItems();

            String itemID = itemIDField.getText().trim().toUpperCase();
            if (itemID.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please enter Item ID first.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            boolean hasSupplier = false;
            for (SupplierItem supplierItem : supplierItemList) {
                if (supplierItem.getItemID().equalsIgnoreCase(itemID)) {
                    supplierComboBox.addItem(supplierItem.getSupplierID());
                    hasSupplier = true;
                }
            }

            if (hasSupplier) {
                cl.show(supplierSelectorPanel, "COMBO");  // switch to combo box
                dialog.pack();
            } else {
                supplierError.setForeground(errorColor);
                supplierError.setText("*No suppliers found for this Item ID.");
            }
        });




        addBtn.addActionListener(e -> {
            // Reset errors
            itemIDError.setText(""); 
            quantityError.setText("");
            dateError.setText(""); 

            String itemID = itemIDField.getText().trim().toUpperCase();
            String quantityStr = quantityField.getText().trim();
            String requiredDate = requiredDateField.getText().trim();
            
            
            boolean valid = true;
            int quantity = 0;

            // Item ID validation
            if (itemID.isEmpty()) {
                itemIDError.setForeground(errorColor);
                itemIDError.setText("*Item ID cannot be empty.");
                valid = false;
            } else {
                if (!itemID.matches("IT\\d{4}")) {
                    itemIDError.setForeground(errorColor);
                    itemIDError.setText("*Format must be (ex:IT0001).");
                    valid = false;
                } else {
                    boolean itemExists = false;
                    for (Item item : itemList) {
                        if (item.getItemID().equalsIgnoreCase(itemID)) {
                            itemExists = true;
                            break;
                        }
                    }

                    if (!itemExists) {
                        itemIDError.setForeground(errorColor);  // Set error color
                        itemIDError.setText("*Item ID does not exist.");
                        valid = false;  // Mark as invalid to prevent further submission
                    } else {
                        itemIDError.setText("");  // Clear any previous error messages if valid
                    }
                }
            }
            
            // Supplier ID validation
            String selectedSupplierId = (String) supplierComboBox.getSelectedItem();
            if (selectedSupplierId == null || selectedSupplierId.isEmpty()) {
                supplierError.setForeground(errorColor);
                supplierError.setText("*Please select a supplier.");
                valid = false;
            } else {
                supplierError.setText(""); // Clear if no error
            }
       
            
            // Validate quantity
            try {
                quantity = Integer.parseInt(quantityStr);
                if (quantity <= 0) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                quantityError.setForeground(errorColor);
                quantityError.setText("*Quantity must be positive.");
                valid = false;
            }

            
            // Validate requiredDate 
            LocalDate today = LocalDate.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            try {
                LocalDate parsedDate = LocalDate.parse(requiredDate, formatter);
                if (!parsedDate.isAfter(today)) {
                    dateError.setForeground(errorColor);
                    dateError.setText("*Date must be after today.");
                    valid = false;
                } else {
                    dateError.setText("");  // Clear error if valid
                }
            } catch (DateTimeParseException ex) {
                dateError.setForeground(errorColor);
                dateError.setText("*Date must be in YYYY-MM-DD format.");
                valid = false;
            }
            
            // Check for duplicate PR entry
            boolean alreadyRequested = false;
            for (PurchaseRequisition pr : prList) {
                if ("PENDING".equalsIgnoreCase(pr.getStatus()) && pr.getSupplierID().equalsIgnoreCase(selectedSupplierId)) {
                    for (PurchaseRequisitionItem item : pr.getItems()) {
                        if (item.getItemID().equalsIgnoreCase(itemID)) {
                            alreadyRequested = true;
                            break;
                        }
                    }
                }
                if (alreadyRequested) break;
            }

            if (alreadyRequested) {
                itemIDError.setForeground(errorColor);
                itemIDError.setText("*A pending PR for this Item + Supplier already exists.");
                valid = false;
            }

            // Get unitCost from itemID
            double unitCost = 0;
            for (Item item : itemList) {
                if (item.getItemID().equalsIgnoreCase(itemID)) {
                    unitCost = item.getCost();  // Assuming getUnitCost() returns Double or double
                    break;
                }
            }

            if (unitCost == 0) {
                JOptionPane.showMessageDialog(dialog, "Failed to find unit cost for the given Item ID.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Check if there's an existing pending PR for this supplier
            String prId = null;
            for (PurchaseRequisition pr : prList) {
                if ("PENDING".equalsIgnoreCase(pr.getStatus()) && pr.getSupplierID().equalsIgnoreCase(selectedSupplierId)) {
                    prId = pr.getPrID();
                    break;
                }
            }

            // If no existing PR, generate a new one and add to prList
            if (prId == null) {
                prId = PurchaseRequisition.generateNextPRId();
                PurchaseRequisition newPr = new PurchaseRequisition(
                    prId,
                    requiredDate,
                    selectedSupplierId,
                    raisedBy,
                    "PENDING"
                );
                prList.add(newPr);
            }

            // If all fields are valid, add the pr
            if (valid) {
                PurchaseRequisition newPr = new PurchaseRequisition(
                    prId,  // prId
                    requiredDate,
                    selectedSupplierId,
                    raisedBy,
                    "PENDING"
                );
                
                PurchaseRequisitionItem newItem = new PurchaseRequisitionItem(
                    prId,   // prId to link item to this PR
                    itemID,
                    quantity,
                    unitCost
                );
                

                prItemList.add(newItem);

                FileUtil.saveListToFile(PURCHASE_REQUISITION_FILE, prList);
                FileUtil.saveListToFile(PURCHASE_REQUISITION_ITEM_FILE, prItemList);
                PurchaseRequisition.updatePRTableInUI(prList, prItemList, prTable);
                JOptionPane.showMessageDialog(dialog, "Purchase requisition added successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
            }
        });

        cancelBtn.addActionListener(e -> dialog.dispose());
        dialog.setVisible(true);
    }
    
    public void editPurchaseRequisition(JFrame parent, List<PurchaseRequisition> prList, JTable prTable) {}

    
}


