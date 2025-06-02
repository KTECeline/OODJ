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


public class SalesManager extends Manager implements ManageItemInterface, ManageSupplierInterface, 
                                                    ManagePRInterface, ManageSalesInterface{
    
    // String representing the file path for item data
    private static final String ITEM_FILE = "data/items.txt";
    
    // String representing the file path for supplier data
    private static final String SUPPLIER_FILE = "data/suppliers.txt";
    
    // String representing the file path for supplier item data
    private static final String SUPPLIER_ITEM_FILE = "data/supplier_item.txt";
    
    // String representing the file path for pr data
    private static final String PURCHASE_REQUISITION_FILE = "data/purchase_requisition.txt";
    
    // String representing the file path for pr item data
    private static final String PURCHASE_REQUISITION_ITEM_FILE = "data/purchase_requisition_item.txt";
    
    // String representing the file path for supplier data
    private static final String SALES_FILE = "data/sales.txt";
    
    // String representing the file path for supplier item data
    private static final String SALES_ITEM_FILE = "data/sales_item.txt";
    
    
    public SalesManager(User loggedInUser) {
        super(loggedInUser);
    }
    
    @Override
    public boolean isAllowedToPerform(String action) {
        // Check if user is logged in and role matches "Sales" or "Administartor" or "Root Administrator"
        
        
        if (getLoggedInUser() == null || getLoggedInUser().getRole() == null || 
            !(getLoggedInUser().getRole().equalsIgnoreCase("Sales Manager") ||
            getLoggedInUser().getRole().equalsIgnoreCase("Administrator") ||
            getLoggedInUser().getRole().equalsIgnoreCase("Root Administrator"))) {
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
    public void addItem(JFrame parent, List<Item> itemList, JTable itemTable) {
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
    public void editItem(Item itemToEdit, List<Item> itemList, JTable itemTable) {
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
    public void deleteItem(JFrame parent, List<Item> itemList, List<SupplierItem> supplierItemList, 
                        List<PurchaseRequisition> prList, List<PurchaseRequisitionItem> prItemList, JTable itemTable) {

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

        // Count linked supplier entries
        int linkedSupplierCount = 0;
        for (SupplierItem supplierItem : supplierItemList) {
            if (supplierItem.getItemID().equalsIgnoreCase(itemId)) {
                linkedSupplierCount++;
            }
        }

        // Count linked PR entries
        int linkedPRCount = 0;
        for (PurchaseRequisitionItem prItem : prItemList) {
            if (prItem.getItemID().equalsIgnoreCase(itemId)) {
                linkedPRCount++;
            }
        }

        // Confirm deletion with full warning
        StringBuilder message = new StringBuilder("Are you sure you want to delete item " + itemId + "?");
        if (linkedSupplierCount > 0) {
            message.append("\nThis will also remove ").append(linkedSupplierCount).append(" linked supplier(s).");
        }
        if (linkedPRCount > 0) {
            message.append("\nThis will also remove ").append(linkedPRCount).append(" linked PR record(s).");
        }

        int response = JOptionPane.showConfirmDialog(
            parent,
            message.toString(),
            "Confirm Deletion",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );

        if (response == JOptionPane.YES_OPTION) {
            // Remove from itemList
            Item itemToDelete = null;
            for (Item item : itemList) {
                if (item.getItemID().equalsIgnoreCase(itemId)) {
                    itemToDelete = item;
                    break;
                }
            }

            if (itemToDelete != null) {
                itemList.remove(itemToDelete);

                // Remove related SupplierItem links
                supplierItemList.removeIf(si -> si.getItemID().equalsIgnoreCase(itemId));

                // Remove related PurchaseRequisitionItem links
                List<String> affectedPRIds = new ArrayList<>();
                prItemList.removeIf(prItem -> {
                    boolean match = prItem.getItemID().equalsIgnoreCase(itemId);
                    if (match) {
                        affectedPRIds.add(prItem.getPrID());
                    }
                    return match;
                });

                // Optionally, remove PRs with no items left
                prList.removeIf(pr -> affectedPRIds.contains(pr.getPrID()) &&
                    prItemList.stream().noneMatch(pi -> pi.getPrID().equalsIgnoreCase(pr.getPrID())));

                // Save files
                FileUtil.saveListToFile(ITEM_FILE, itemList);
                FileUtil.saveListToFile(SUPPLIER_ITEM_FILE, supplierItemList);
                FileUtil.saveListToFile(PURCHASE_REQUISITION_ITEM_FILE, prItemList);
                FileUtil.saveListToFile(PURCHASE_REQUISITION_FILE, prList);

                // Update UI tables
                Item.updateItemTableInUI(itemList, itemTable);

                JOptionPane.showMessageDialog(parent, "Item and related supplier/PR links deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                System.out.println("Deleted item " + itemId + ", removed " + linkedSupplierCount + " supplier link(s), and " + linkedPRCount + " PR record(s).");

            } else {
                JOptionPane.showMessageDialog(parent, "Item not found.", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } else {
            JOptionPane.showMessageDialog(parent, "Item deletion canceled.", "Canceled", JOptionPane.INFORMATION_MESSAGE);
        }

    }

    
    //SUPPLIER SECTION
    
    // Method to ADD new supplier
    @Override
    public void addSupplier(JFrame parent, List<Supplier> supplierList, List<SupplierItem> supplierItemList, 
                        List<Item> itemList, JTable supplierTable) {
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

    // Method to EDIT supplier
    @Override
    public void editSupplier(Supplier supplierToEdit, List<Supplier> supplierList, 
                        List<SupplierItem> supplierItemList, List<Item> itemList, JTable supplierTable) {
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
            // Display both ID and name, e.g., IT0001 - Laptop
            JCheckBox itemCheckbox = new JCheckBox(item.getItemID() + " - " + item.getItemName());


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
                        String itemId = cb.getText().split(" - ")[0];  // Only take the ID part before the dash
                        supplierItemList.add(new SupplierItem(supplierToEdit.getSupplierID(), itemId));
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
    @Override
    public void deleteSupplier(JFrame parent, List<Supplier> supplierList, 
                            List<SupplierItem> supplierItemList, JTable supplierTable) {
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
    
    // Method to add new purchase requisition
    @Override
    public void addPurchaseRequisition(JFrame parent, List<Item> itemList, List<PurchaseRequisition> prList, 
                                    List<PurchaseRequisitionItem> prItemList, List<Supplier> supplierList, 
                                    List<SupplierItem> supplierItemList, JTable prTable) {
        if (!isAllowedToPerform("add pr")) {
            JOptionPane.showMessageDialog(parent, "Not authorized to add purchase requisition.", "Permission Denied", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String raisedBy = getLoggedInUser().getUsername();
        
        JTextField itemIDField = new JTextField(20);
        JTextField quantityField = new JTextField(20);
        
        JPanel supplierSelectorPanel = new JPanel(new CardLayout());
        JButton selectSupplierBtn = new JButton("Select Supplier");
        JComboBox<String> supplierComboBox = new JComboBox<>();
        
        supplierSelectorPanel.add(selectSupplierBtn, "BUTTON");
        supplierSelectorPanel.add(supplierComboBox, "COMBO");
        supplierSelectorPanel.setBackground(Color.white);

        // Error labels
        JLabel itemIDError = new JLabel();
        JLabel supplierError = new JLabel();
        JLabel quantityError = new JLabel();
        
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

        JDialog dialog = new JDialog(parent, "Add Purchase Requisition", true);
        dialog.getContentPane().add(panel, BorderLayout.CENTER);

        JButton addBtn = new JButton("Add");
        JButton cancelBtn = new JButton("Cancel");
        
        addBtn.setBackground(Color.RED);
        addBtn.setForeground(Color.WHITE);

        cancelBtn.setBackground(Color.BLACK);
        cancelBtn.setForeground(Color.WHITE);
        
        JPanel btnPanel = new JPanel();
        btnPanel.add(addBtn); 
        btnPanel.add(cancelBtn);
        btnPanel.setBackground(Color.white);
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

            String itemID = itemIDField.getText().trim().toUpperCase();
            String quantityStr = quantityField.getText().trim();
            
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
            
            if (valid) {
                // Check if there's an existing pending PR for this supplier
                String prId = null;
                PurchaseRequisition existingPr = null;

                for (PurchaseRequisition pr : prList) {
                    if ("PENDING".equalsIgnoreCase(pr.getStatus()) &&
                        pr.getSupplierID().equalsIgnoreCase(selectedSupplierId)) {
                        prId = pr.getPrID();
                        existingPr = pr;
                        break;
                    }
                }

                // If no existing PR, generate a new one and prompt for required date
                if (prId == null) {
                    prId = PurchaseRequisition.generateNextPRId();

                    String dateInput = JOptionPane.showInputDialog(
                        dialog,
                        "Enter required date (YYYY-MM-DD):",
                        "Required Date",
                        JOptionPane.PLAIN_MESSAGE
                    );

                    if (dateInput == null || dateInput.trim().isEmpty()) {
                        JOptionPane.showMessageDialog(dialog, "Required date is mandatory for a new PR.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    String requiredDateStr = dateInput.trim();

                    // Validate format: yyyy-MM-dd
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    LocalDate requiredDate;
                    try {
                        requiredDate = LocalDate.parse(requiredDateStr, formatter);
                    } catch (DateTimeParseException ex) {
                        JOptionPane.showMessageDialog(dialog, "Invalid date format. Please use YYYY-MM-DD.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    // Validate if date is in the past
                    if (requiredDate.isBefore(LocalDate.now())) {
                        JOptionPane.showMessageDialog(dialog, "Required date cannot be in the past.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    // Create and add new PR
                    PurchaseRequisition newPr = new PurchaseRequisition(
                        prId,
                        selectedSupplierId,
                        requiredDate,
                        raisedBy,
                        "PENDING"
                    );
                    prList.add(newPr);
                }

                // Finally, add the item
                PurchaseRequisitionItem newItem = new PurchaseRequisitionItem(
                    prId,
                    itemID,
                    quantity,
                    unitCost
                );

                prItemList.add(newItem);

                FileUtil.saveListToFile(PURCHASE_REQUISITION_FILE, prList);
                FileUtil.saveListToFile(PURCHASE_REQUISITION_ITEM_FILE, prItemList);
                PurchaseRequisition.updatePRTableInUI(prList, prItemList, itemList, prTable);
                JOptionPane.showMessageDialog(dialog, "Purchase requisition added successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
            }

        });

        cancelBtn.addActionListener(e -> dialog.dispose());
        dialog.setVisible(true);
    }
    
    // Method to edit purchase requisition
    @Override
    public void editPurchaseRequisition(JFrame parent, PurchaseRequisition prToEdit, PurchaseRequisitionItem itemToEdit,
                                    List<PurchaseRequisition> prList, List<PurchaseRequisitionItem> prItemList,
                                    List<SupplierItem> supplierItemList, List<Item> itemList, List<Supplier> supplierList,
                                    JTable prTable) {
        
        // Only allow edit if status is PENDING
        if (!"PENDING".equalsIgnoreCase(prToEdit.getStatus())) {
            JOptionPane.showMessageDialog(parent,
                "Only purchase requisitions with status PENDING can be edited.",
                "Edit Not Allowed",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Check User Permission for edit PR
        if (!isAllowedToPerform("edit PR")) {
            JOptionPane.showMessageDialog(null, "Not authorized to edit purchase requisition.", "Permission Denied", JOptionPane.ERROR_MESSAGE);
            return;
        }

        final String prID = prToEdit.getPrID();
        final String itemID = itemToEdit.getItemID();

        String itemName = "";
        for (Item item : itemList) {
            if (item.getItemID().equalsIgnoreCase(itemID)) {
                itemName = item.getItemName();
                break;
            }
        }

        JLabel supplierError = new JLabel();
        Color errorColor = Color.RED;

        JComboBox<String> supplierComboBox = new JComboBox<>();
        for (SupplierItem supplierItem : supplierItemList) {
            if (supplierItem.getItemID().equalsIgnoreCase(itemID)) {
                String supplierName = "";
                for (Supplier supplier : supplierList) {
                    if (supplier.getSupplierID().equalsIgnoreCase(supplierItem.getSupplierID())) {
                        supplierName = supplier.getSupplierName();
                        break;
                    }
                }
                supplierComboBox.addItem(supplierItem.getSupplierID() + " - " + supplierName);
            }
        }
        
        JTextField quantityField = new JTextField(String.valueOf(itemToEdit.getQuantity()), 10);
        JLabel quantityError = new JLabel();
        quantityError.setForeground(Color.RED);
        
        JTextField requiredDateField = new JTextField(prToEdit.getRequiredDate().toString(), 10); // LocalDate to string
        JLabel dateError = new JLabel();
        dateError.setForeground(Color.RED);

        JPanel panel = new JPanel(new GridLayout(0, 2, 0, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        panel.setBackground(Color.white);
        panel.setPreferredSize(new Dimension(600, 400));

        panel.add(new JLabel("PR ID:"));
        panel.add(new JLabel(prID));
        panel.add(new JLabel("Item ID:"));
        panel.add(new JLabel(itemID + " - " + itemName));
        panel.add(new JLabel());
        panel.add(new JLabel());
        panel.add(new JLabel("Supplier:"));
        panel.add(supplierComboBox);
        panel.add(new JLabel());
        panel.add(supplierError);
        panel.add(new JLabel("Quantity:"));
        panel.add(quantityField);
        panel.add(new JLabel());
        panel.add(quantityError);  // add the error label below quantity
        panel.add(new JLabel("Required Date (YYYY-MM-DD):"));
        panel.add(requiredDateField);
        panel.add(new JLabel());
        panel.add(dateError);  // add the error label under the date field

        JDialog dialog = new JDialog(parent, "Edit Purchase Requisition", true);
        dialog.getContentPane().add(panel, BorderLayout.CENTER);

        JButton saveBtn = new JButton("Save");
        JButton cancelBtn = new JButton("Cancel");
        JPanel btnPanel = new JPanel();
        btnPanel.add(saveBtn);
        btnPanel.add(cancelBtn);

        saveBtn.setBackground(Color.RED);
        saveBtn.setForeground(Color.WHITE);

        cancelBtn.setBackground(Color.BLACK);
        cancelBtn.setForeground(Color.WHITE);

        btnPanel.setBackground(Color.white);
        dialog.getContentPane().add(btnPanel, BorderLayout.SOUTH);

        dialog.pack();
        dialog.setLocationRelativeTo(parent);

        if (supplierComboBox.getItemCount() > 0) {
            for (int i = 0; i < supplierComboBox.getItemCount(); i++) {
                String comboEntry = supplierComboBox.getItemAt(i);
                if (comboEntry.startsWith(prToEdit.getSupplierID() + " -")) {
                    supplierComboBox.setSelectedIndex(i);
                    break;
                }
            }
            dialog.pack();
        } else {
            supplierError.setForeground(errorColor);
            supplierError.setText("*No suppliers found for this Item ID.");
        }

        saveBtn.addActionListener(e -> {
            String selectedSupplier = (String) supplierComboBox.getSelectedItem();
            if (selectedSupplier == null || selectedSupplier.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please select a supplier.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String selectedSupplierID = selectedSupplier.split(" - ")[0];

            boolean supplierItemMatch = false;
            for (SupplierItem si : supplierItemList) {
                if (si.getSupplierID().equalsIgnoreCase(selectedSupplierID) &&
                    si.getItemID().equalsIgnoreCase(itemID)) {
                    supplierItemMatch = true;
                    break;
                }
            }
            if (!supplierItemMatch) {
                JOptionPane.showMessageDialog(dialog, "Selected supplier does not supply this item.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Validate quantity
            quantityError.setText("");  // reset previous error
            int newQuantity = 0;
            try {
                newQuantity = Integer.parseInt(quantityField.getText().trim());
                if (newQuantity <= 0) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                quantityError.setText("* Quantity must be a positive integer.");
                return;
            }
            
            // Validate required date
            dateError.setText("");  // reset previous error
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate requiredDate;

            try {
                requiredDate = LocalDate.parse(requiredDateField.getText().trim(), formatter);
                if (requiredDate.isBefore(LocalDate.now())) {
                    dateError.setText("* Date cannot be in the past.");
                    return;
                }
            } catch (DateTimeParseException ex) {
                dateError.setText("* Invalid date format. Use YYYY-MM-DD.");
                return;
            }
            
            // Warn if date is changed
            if (!prToEdit.getRequiredDate().equals(requiredDate)) {
                int confirm = JOptionPane.showConfirmDialog(dialog,
                    "Changing the required date will apply to the entire PR, not just this item.\nDo you want to proceed?",
                    "Confirm Date Change",
                    JOptionPane.YES_NO_OPTION);
                if (confirm != JOptionPane.YES_OPTION) return;
            }

            // Update quantity
            itemToEdit.setQuantity(newQuantity);
            itemToEdit.setTotalCost(newQuantity * itemToEdit.getUnitCost());

            // Update PR date
            prToEdit.setRequiredDate(requiredDate);
            

            // Look for existing PR for this supplier
            String targetPrID = null;
            PurchaseRequisition existingPr = null;

            for (PurchaseRequisition pr : prList) {
                if (pr.getSupplierID().equalsIgnoreCase(selectedSupplierID) &&
                    pr.getStatus().equalsIgnoreCase("PENDING")) {
                    targetPrID = pr.getPrID();
                    existingPr = pr;
                    break;
                }
            }

            // If no existing PR, generate new PR ID
            if (targetPrID == null) {
                targetPrID = PurchaseRequisition.generateNextPRId();

                PurchaseRequisition newPr = new PurchaseRequisition(
                    targetPrID,
                    selectedSupplierID,
                    requiredDate,
                    prToEdit.getRaisedBy(),
                    "PENDING"
                );
                prList.add(newPr);
            }

            // Check for duplicate (targetPrID + itemID)
            for (PurchaseRequisitionItem otherItem : prItemList) {
                if (!otherItem.equals(itemToEdit) &&
                    otherItem.getPrID().equalsIgnoreCase(targetPrID) &&
                    otherItem.getItemID().equalsIgnoreCase(itemID)) {
                    JOptionPane.showMessageDialog(dialog,
                        "A purchase requisition with this PR ID and Item ID already exists.",
                        "Duplicate Entry",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            // Update the item’s PR ID
            itemToEdit.setPrID(targetPrID);
            
            if (!selectedSupplierID.equalsIgnoreCase(prToEdit.getSupplierID())) {
                // Only reassign PR ID if supplier is changed
                itemToEdit.setPrID(targetPrID);

                // After moving the item, check if old PR has no more items
                boolean hasOtherItems = false;
                for (PurchaseRequisitionItem item : prItemList) {
                    if (!item.equals(itemToEdit) && item.getPrID().equalsIgnoreCase(prID)) {
                        hasOtherItems = true;
                        break;
                    }
                }
                if (!hasOtherItems) {
                    prList.remove(prToEdit);
                }
            }


            FileUtil.saveListToFile(PURCHASE_REQUISITION_FILE, prList);
            FileUtil.saveListToFile(PURCHASE_REQUISITION_ITEM_FILE, prItemList);

            PurchaseRequisition.updatePRTableInUI(prList, prItemList, itemList, prTable);
            JOptionPane.showMessageDialog(dialog, "Purchase requisition updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            dialog.dispose();
        });

        cancelBtn.addActionListener(e -> dialog.dispose());
        dialog.setVisible(true);
    }


    // Method to delete purchase requisition
    @Override
    public void deletePurchaseRequisition(JFrame parent, List<PurchaseRequisition> prList, 
                                    List<PurchaseRequisitionItem> prItemList, List<Item> itemList, JTable prTable) {
        
        int selectedRow = prTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(parent, "Please select a purchase requisition to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        

        String prId = prTable.getValueAt(selectedRow, 0).toString();
        String itemIdWithName = prTable.getValueAt(selectedRow, 1).toString();
        String itemId = itemIdWithName.split(" - ")[0];  // get only the item ID part
        String supplierId = prTable.getValueAt(selectedRow, 2).toString();
        
        if (!isAllowedToPerform("delete pr")) {
            JOptionPane.showMessageDialog(null, "Not authorized to delete purchase requisitions.", "Permission Denied", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Find the matching PR object
        PurchaseRequisition prToDelete = null;
        for (PurchaseRequisition pr : prList) {
            if (pr.getPrID().equalsIgnoreCase(prId)) {
                prToDelete = pr;
                break;
            }
        }

        if (prToDelete == null) {
            JOptionPane.showMessageDialog(parent, "Purchase Requisition not found.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Only allow delete if status is PENDING
        if (!"PENDING".equalsIgnoreCase(prToDelete.getStatus())) {
            JOptionPane.showMessageDialog(parent,
                "Only purchase requisitions with status PENDING can be deleted.",
                "Delete Not Allowed",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Confirm deletion
        int response = JOptionPane.showConfirmDialog(
            parent,
            "Are you sure you want to delete this PR? \n\nItem: " + itemIdWithName + "\nSupplier: " + supplierId,
            "Confirm Deletion",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );

        if (response == JOptionPane.YES_OPTION) {
            // Step 1: Remove the PurchaseRequisitionItem (the line entry)
            PurchaseRequisitionItem itemToDelete = null;
            for (PurchaseRequisitionItem item : prItemList) {
                if (item.getPrID().equalsIgnoreCase(prId) && item.getItemID().equalsIgnoreCase(itemId)) {
                    itemToDelete = item;
                    break;
                }
            }

            if (itemToDelete != null) {
                prItemList.remove(itemToDelete);

                // Step 2: If no more items under this PR, remove the PR itself
                boolean hasOtherItems = false;
                for (PurchaseRequisitionItem item : prItemList) {
                    if (item.getPrID().equalsIgnoreCase(prId)) {
                        hasOtherItems = true;
                        break;
                    }
                }

            if (!hasOtherItems) {
                prList.remove(prToDelete);
            }

                // Step 3: Save updates
                FileUtil.saveListToFile(PURCHASE_REQUISITION_FILE, prList);
                FileUtil.saveListToFile(PURCHASE_REQUISITION_ITEM_FILE, prItemList);

                // Step 4: Update UI table
                PurchaseRequisition.updatePRTableInUI(prList, prItemList, itemList, prTable);

                JOptionPane.showMessageDialog(parent, "Purchase requisition record deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(parent, "Purchase requisition record not found.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(parent, "Purchase requisition deletion canceled.", "Canceled", JOptionPane.INFORMATION_MESSAGE);
        }
    }


    
    // SALES SECTION
    
    // Method to add sales
    @Override
    public Sales addSales(JFrame parent, String loggedInUsername, List<Sales> salesList) {
        
        if (!isAllowedToPerform("add sale")) {
            JOptionPane.showMessageDialog(parent, "Not authorized to add sales.", "Permission Denied", JOptionPane.ERROR_MESSAGE);
            return null;
        }
        
        String salesID = Sales.generateNextSalesID(salesList);

        // Create fields and error label
        JTextField dateField = new JTextField(10);
        JTextField remarksField = new JTextField(20);
        JLabel dateError = new JLabel();
        dateError.setForeground(Color.RED);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // Panel layout
        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        panel.setBackground(Color.WHITE);
                
        panel.add(new JLabel("Sales ID:"));
        panel.add(new JLabel(salesID));
        panel.add(new JLabel("Date (YYYY-MM-DD):"));
        panel.add(dateField);
        panel.add(new JLabel());
        panel.add(dateError);
        panel.add(new JLabel("Remarks:"));
        panel.add(remarksField);

        // Create dialog
        JDialog dialog = new JDialog(parent, "Add New Sale", true);
        dialog.getContentPane().add(panel, BorderLayout.CENTER);

        JButton continueBtn = new JButton("Continue");
        JButton cancelBtn = new JButton("Cancel");
        
        
        continueBtn.setBackground(Color.RED);
        continueBtn.setForeground(Color.WHITE);

        cancelBtn.setBackground(Color.BLACK);
        cancelBtn.setForeground(Color.WHITE);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(continueBtn);
        buttonPanel.add(cancelBtn);

        dialog.getContentPane().add(buttonPanel, BorderLayout.SOUTH);
        dialog.pack();
        dialog.setLocationRelativeTo(parent);

        final Sales[] result = {null};

        continueBtn.addActionListener(e -> {
            String dateInput = dateField.getText().trim();
            String remarks = remarksField.getText().trim();

            if (dateInput.isEmpty()) {
                dateError.setText("* Date cannot be empty.");
                return;
            }

            try {
                LocalDate date = LocalDate.parse(dateInput, formatter);
                if (date.isBefore(LocalDate.now())) {
                    dateError.setText("* Date cannot be in the past.");
                    return;
                }
                
                if (remarks == null || remarks.trim().isEmpty()) {
                    remarks = "None";
                }

                // All valid, create the Sales object
                result[0] = new Sales(salesID, date, loggedInUsername, remarks);
                dialog.dispose();

            } catch (DateTimeParseException ex) {
                dateError.setText("* Invalid date format. Use YYYY-MM-DD.");
            }

        });

        cancelBtn.addActionListener(e -> {
            result[0] = null;  // Explicitly set null
            dialog.dispose();
        });

        dialog.setVisible(true);
        return result[0];  // Will return after dialog is closed
    }

    // Method to edit sales record
    @Override
    public void editSalesRecord(JFrame parent, Sales sale, List<Sales> salesList) {
        // Check permission first
        if (!isAllowedToPerform("edit sale")) {
            JOptionPane.showMessageDialog(parent, "Not authorized to edit sales.", "Permission Denied", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Prepare fields prefilled with current data
        JTextField dateField = new JTextField(sale.getDate().toString(), 10);
        JTextField remarksField = new JTextField(sale.getRemarks(), 20);
        JLabel dateError = new JLabel();
        dateError.setForeground(Color.RED);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // Build panel
        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        panel.setBackground(Color.WHITE);

        panel.add(new JLabel("Sales ID:"));
        panel.add(new JLabel(sale.getSalesID()));
        panel.add(new JLabel("Date (YYYY-MM-DD):"));
        panel.add(dateField);
        panel.add(new JLabel());
        panel.add(dateError);
        panel.add(new JLabel("Remarks:"));
        panel.add(remarksField);

        // Build dialog
        JDialog dialog = new JDialog(parent, "Edit Sales Record", true);
        dialog.getContentPane().add(panel, BorderLayout.CENTER);

        JButton saveBtn = new JButton("Save");
        JButton cancelBtn = new JButton("Cancel");

        saveBtn.setBackground(Color.RED);
        saveBtn.setForeground(Color.WHITE);

        cancelBtn.setBackground(Color.BLACK);
        cancelBtn.setForeground(Color.WHITE);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(saveBtn);
        buttonPanel.add(cancelBtn);

        dialog.getContentPane().add(buttonPanel, BorderLayout.SOUTH);
        dialog.pack();
        dialog.setLocationRelativeTo(parent);

        // Button actions
        saveBtn.addActionListener(e -> {
            String dateInput = dateField.getText().trim();
            String remarks = remarksField.getText().trim();

            if (dateInput.isEmpty()) {
                dateError.setText("* Date cannot be empty.");
                return;
            }

            try {
                LocalDate newDate = LocalDate.parse(dateInput, formatter);
                if (newDate.isBefore(LocalDate.now())) {
                    dateError.setText("* Date cannot be in the past.");
                    return;
                }

                if (remarks.isEmpty()) {
                    remarks = "None";
                }

                // Apply updates
                sale.setDate(newDate);
                sale.setRemarks(remarks);
                
                // Update the in-memory sales list (find and replace the edited sale)
                for (int i = 0; i < salesList.size(); i++) {
                    if (salesList.get(i).getSalesID().equals(sale.getSalesID())) {
                        salesList.set(i, sale);  // replace the old with the updated one
                        break;
                    }
                }

                // Save the updated list back to the file
                FileUtil.saveListToFile(SALES_FILE, salesList);

                JOptionPane.showMessageDialog(parent, "Sales record updated successfully.");
                dialog.dispose();

            } catch (DateTimeParseException ex) {
                dateError.setText("* Invalid date format. Use YYYY-MM-DD.");
            }
        });

        cancelBtn.addActionListener(e -> dialog.dispose());

        dialog.setVisible(true);
    }
    
    // Method to delete sales record
    @Override
    public void deleteSalesRecord(JFrame parent, String salesID, List<Sales> salesList, 
                              List<SalesItem> salesItemList, List<Item> itemList, 
                              JList<String> salesIDList, JTextArea salesDetailsArea, JTable salesItemTable) {

        if (!isAllowedToPerform("delete sale")) {
            JOptionPane.showMessageDialog(parent, "Not authorized to delete sales record.", "Permission Denied", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(parent, 
            "Are you sure you want to delete Sale ID: " + salesID + "?", 
            "Confirm Delete", 
            JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) {
            return;  // User cancelled
        }

        // Find and remove the sale
        Sales saleToRemove = null;
        for (Sales sale : salesList) {
            if (sale.getSalesID().equalsIgnoreCase(salesID)) {
                saleToRemove = sale;
                break;
            }
        }

        if (saleToRemove != null) {
            // Restore stock
            for (SalesItem si : salesItemList) {
                if (si.getSalesID().equalsIgnoreCase(salesID)) {
                    for (Item item : itemList) {
                        if (item.getItemID().equalsIgnoreCase(si.getItemID())) {
                            item.setStock(item.getStock() + si.getQuantitySold());
                            break;
                        }
                    }
                }
            }

            // Remove the sale
            salesList.remove(saleToRemove);

            // Remove its sales items
            salesItemList.removeIf(si -> si.getSalesID().equalsIgnoreCase(salesID));

            // Save updates
            FileUtil.saveListToFile(SALES_FILE, salesList);
            FileUtil.saveListToFile(SALES_ITEM_FILE, salesItemList);
            FileUtil.saveListToFile(ITEM_FILE, itemList);

            // Update the UI
            Sales.updateSalesUI(salesList, salesItemList, itemList, salesIDList, salesDetailsArea, salesItemTable);

            JOptionPane.showMessageDialog(parent, "Sale ID " + salesID + " deleted successfully.");
        } else {
            JOptionPane.showMessageDialog(parent, "Sale ID " + salesID + " not found.");
        }
    }
    
    
    // Method to add sales items
    @Override
    public void addSalesItems(JFrame parent, Sales sale, List<Item> itemList, List<SalesItem> salesItemList, 
                        List<Sales> salesList, boolean allowCancelSale) {
        boolean addingItems = true;
        List<String> addedItemIDs = new ArrayList<>();
        final boolean[] stopAdding = {false};

        while (addingItems) {
            JTextField itemIDField = new JTextField(10);
            JTextField quantityField = new JTextField(10);
            JLabel errorLabel = new JLabel();
            errorLabel.setForeground(Color.RED);

            JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
            panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
            panel.setBackground(Color.WHITE);
            panel.setPreferredSize(new Dimension(500, 150));

            panel.add(new JLabel("Item ID:"));
            panel.add(itemIDField);
            panel.add(new JLabel("Quantity:"));
            panel.add(quantityField);
            panel.add(new JLabel());
            panel.add(errorLabel);

            JDialog dialog = new JDialog(parent, "Add Sales Item", true);
            dialog.getContentPane().add(panel, BorderLayout.CENTER);
            
            JButton backBtn = new JButton("Back");
            JButton addBtn = new JButton("Add Item");
            JButton cancelBtn = new JButton("Cancel");

            addBtn.setBackground(Color.RED);
            addBtn.setForeground(Color.WHITE);

            backBtn.setBackground(Color.GRAY);
            backBtn.setForeground(Color.WHITE);

            cancelBtn.setBackground(Color.BLACK);
            cancelBtn.setForeground(Color.WHITE);

            JPanel btnPanel = new JPanel();
            btnPanel.setBackground(Color.WHITE);
            
            btnPanel.add(backBtn);
            btnPanel.add(addBtn);
            if (allowCancelSale) {
                btnPanel.add(cancelBtn);  // Only add if allowed
            }

            dialog.getContentPane().add(btnPanel, BorderLayout.SOUTH);
            dialog.pack();
            dialog.setLocationRelativeTo(parent);

            final boolean[] itemAdded = {false};
            final boolean[] canceledSale = {false};
            final boolean[] goBack = {false};
            

            addBtn.addActionListener(e -> {
                String itemID = itemIDField.getText().trim().toUpperCase();
                String qtyInput = quantityField.getText().trim();

                if (itemID.isEmpty() || qtyInput.isEmpty()) {
                    errorLabel.setText("* All fields are required.");
                    return;
                }

                boolean alreadyExistsInSalesItemList = false;
                for (SalesItem existing : salesItemList) {
                    if (existing.getSalesID().equalsIgnoreCase(sale.getSalesID()) &&
                        existing.getItemID().equalsIgnoreCase(itemID)) {
                        alreadyExistsInSalesItemList = true;
                        break;
                    }
                }

                if (alreadyExistsInSalesItemList) {
                    errorLabel.setText("* Item already exists in this sale.");
                    return;
                }



                Item matchedItem = null;
                for (Item item : itemList) {
                    if (item.getItemID().equalsIgnoreCase(itemID)) {
                        matchedItem = item;
                        break;
                    }
                }
                if (matchedItem == null) {
                    errorLabel.setText("* Item ID not found.");
                    return;
                }

                int quantity;
                try {
                    quantity = Integer.parseInt(qtyInput);
                    if (quantity <= 0) throw new NumberFormatException();
                } catch (NumberFormatException ex) {
                    errorLabel.setText("* Invalid quantity.");
                    return;
                }

                if (quantity > matchedItem.getStock()) {
                    errorLabel.setText("* Not enough stock. Available: " + matchedItem.getStock());
                    return;
                }

                SalesItem salesItem = new SalesItem(sale.getSalesID(), itemID, quantity, matchedItem.getCost());
                sale.getItems().add(salesItem);
                salesItemList.add(salesItem);
                itemAdded[0] = true;
                
                // Deduct stock
                matchedItem.setStock(matchedItem.getStock() - quantity);


                dialog.dispose();
            });

            backBtn.addActionListener(e -> {
                dialog.dispose();
            });


            if (allowCancelSale) {
                cancelBtn.addActionListener(e -> {
                    int confirm = JOptionPane.showConfirmDialog(parent, "Cancel the entire sale?", "Confirm Cancel", JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        sale.getItems().clear();
                        salesList.removeIf(s -> s.getSalesID().equals(sale.getSalesID()));
                        dialog.dispose();
                        JOptionPane.showMessageDialog(parent, "Sale canceled.");
                        canceledSale[0] = true;
                        stopAdding[0] = true;
                    }
                });
            }


            dialog.setVisible(true);
            
            if (canceledSale[0]) {
                return;  // exit entirely
            }
            
            if (stopAdding[0]) {
                break;
            }

            int option = JOptionPane.showConfirmDialog(parent, "Add another item?", "Continue", JOptionPane.YES_NO_OPTION);
            if (option != JOptionPane.YES_OPTION) {
                addingItems = false;
            }
        }

        if (!sale.getItems().isEmpty()) {
            double totalAmount = 0;
            for (SalesItem item : salesItemList) {
                if (item.getSalesID().equalsIgnoreCase(sale.getSalesID())) {
                    totalAmount += item.getSubtotal();
                }
            }
            sale.setTotalAmount(totalAmount);

            // Update the sale inside salesList (not add duplicate)
            for (int i = 0; i < salesList.size(); i++) {
                if (salesList.get(i).getSalesID().equalsIgnoreCase(sale.getSalesID())) {
                    salesList.set(i, sale);
                    break;
                }
            }
            
            // Check if the sale exists in the salesList
            boolean saleExists = false;
            for (Sales s : salesList) {
                if (s.getSalesID().equalsIgnoreCase(sale.getSalesID())) {
                    saleExists = true;
                    break;
                }
            }

            if (!saleExists) {
                // This is a brand new sale → add it
                salesList.add(sale);
            }

            // Rewrite the full sales list back to file
            FileUtil.saveListToFile(SALES_FILE, salesList);

            // Rewrite the full sales item list back to file
            FileUtil.saveListToFile(SALES_ITEM_FILE, salesItemList);

            // Rewrite updated item stock list
            FileUtil.saveListToFile(ITEM_FILE, itemList);

            JOptionPane.showMessageDialog(parent, "Sale saved successfully!");
        }



    }

    // Method to edit sales item
    @Override
    public void editSalesItemQuantity(JFrame parent, Sales selectedSale, SalesItem selectedSalesItem, 
                                List<SalesItem> salesItemList, List<Sales> salesList, List<Item> itemList) {
        if (selectedSalesItem == null) {
            JOptionPane.showMessageDialog(parent, "No sales item selected.");
            return;
        }

        // Find related item (for stock + price)
        final Item[] matchedItemHolder = {null};
        for (Item item : itemList) {
            if (item.getItemID().equalsIgnoreCase(selectedSalesItem.getItemID())) {
                matchedItemHolder[0] = item;
                break;
            }
        }


        if (matchedItemHolder[0] == null) {
            JOptionPane.showMessageDialog(parent, "Related item not found.");
            return;
        }

        JTextField quantityField = new JTextField(String.valueOf(selectedSalesItem.getQuantitySold()), 10);
        JLabel errorLabel = new JLabel();
        errorLabel.setForeground(Color.RED);

        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        panel.setBackground(Color.WHITE);
        panel.setPreferredSize(new Dimension(400, 180));

        panel.add(new JLabel("Item ID:"));
        panel.add(new JLabel(selectedSalesItem.getItemID()));
        panel.add(new JLabel("Current Quantity:"));
        panel.add(new JLabel(String.valueOf(selectedSalesItem.getQuantitySold())));
        panel.add(new JLabel("New Quantity:"));
        panel.add(quantityField);
        panel.add(new JLabel());  // blank
        panel.add(errorLabel);

        JDialog dialog = new JDialog(parent, "Edit Sales Item Quantity", true);
        dialog.getContentPane().setBackground(Color.WHITE);
        dialog.getContentPane().add(panel, BorderLayout.CENTER);

        JButton saveBtn = new JButton("Save");
        JButton cancelBtn = new JButton("Cancel");

        saveBtn.setBackground(Color.RED);
        saveBtn.setForeground(Color.WHITE);

        cancelBtn.setBackground(Color.BLACK);
        cancelBtn.setForeground(Color.WHITE);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(saveBtn);
        buttonPanel.add(cancelBtn);

        dialog.getContentPane().add(buttonPanel, BorderLayout.SOUTH);
        dialog.pack();
        dialog.setLocationRelativeTo(parent);

        saveBtn.addActionListener(e -> {
            Item matchedItem = matchedItemHolder[0];
            if (matchedItem == null) {
                errorLabel.setText("* Matched item not found.");
                return;
            }

            String qtyInput = quantityField.getText().trim();
            int currentQuantity = selectedSalesItem.getQuantitySold();
            int newQuantity;

            if (qtyInput.isEmpty()) {
                errorLabel.setText("* Quantity cannot be empty.");
                return;
            }

            try {
                newQuantity = Integer.parseInt(qtyInput);
                if (newQuantity <= 0) {
                    errorLabel.setText("* Quantity must be positive.");
                    return;
                }
            } catch (NumberFormatException ex) {
                errorLabel.setText("* Invalid quantity input.");
                return;
            }

            if (newQuantity > currentQuantity) {
                int extraNeeded = newQuantity - currentQuantity;
                if (extraNeeded > matchedItem.getStock()) {
                    errorLabel.setText("* Not enough stock. Available: " + matchedItem.getStock());
                    return;
                }
                matchedItem.setStock(matchedItem.getStock() - extraNeeded);
            } else if (newQuantity < currentQuantity) {
                int toReturn = currentQuantity - newQuantity;
                matchedItem.setStock(matchedItem.getStock() + toReturn);
            }

            // Update the sales item
            selectedSalesItem.setQuantitySold(newQuantity);
            selectedSalesItem.setSubtotal(newQuantity * matchedItem.getCost());

            // Recalculate total for the sale
            double newTotal = 0;
            for (SalesItem s : salesItemList) {
                if (s.getSalesID().equalsIgnoreCase(selectedSale.getSalesID())) {
                    newTotal += s.getSubtotal();
                }
            }
            selectedSale.setTotalAmount(newTotal);

            // Save updates
            FileUtil.saveListToFile(SALES_FILE, salesList);
            FileUtil.saveListToFile(SALES_ITEM_FILE, salesItemList);
            FileUtil.saveListToFile(ITEM_FILE, itemList);

            dialog.dispose();
        });

        cancelBtn.addActionListener(e -> dialog.dispose());

        dialog.setVisible(true);
    }

    // Method to delete sales item
    @Override
    public void deleteSalesItem(JFrame parent, Sales selectedSale, SalesItem selectedSalesItem, 
                            List<SalesItem> salesItemList, List<Sales> salesList, List<Item> itemList) {
        if (selectedSalesItem == null) {
            JOptionPane.showMessageDialog(parent, "No sales item selected.");
            return;
        }
        
        if (!isAllowedToPerform("delete sales item")) {
            JOptionPane.showMessageDialog(parent, "Not authorized to delete sales item record.", "Permission Denied", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(parent, 
            "Are you sure you want to delete sales item " + selectedSalesItem.getItemID() + " from Sale " + selectedSale.getSalesID() + "?", 
            "Confirm Delete Item", 
            JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        
        // Remove from sale’s internal list
        selectedSale.getItems().removeIf(item -> 
            item.getItemID().equalsIgnoreCase(selectedSalesItem.getItemID())
        );

        // Remove from global salesItemList
        salesItemList.removeIf(item -> 
            item.getSalesID().equalsIgnoreCase(selectedSale.getSalesID()) &&
            item.getItemID().equalsIgnoreCase(selectedSalesItem.getItemID())
        );

        // Restore stock
        for (Item item : itemList) {
            if (item.getItemID().equalsIgnoreCase(selectedSalesItem.getItemID())) {
                item.setStock(item.getStock() + selectedSalesItem.getQuantitySold());
                break;
            }
        }

        // Recalculate total amount
        double newTotal = 0;
        for (SalesItem s : salesItemList) {
            if (s.getSalesID().equalsIgnoreCase(selectedSale.getSalesID())) {
                newTotal += s.getSubtotal();
            }
        }
        selectedSale.setTotalAmount(newTotal);

        // If no more items, remove the sale entirely
        boolean hasOtherItems = false;
        for (SalesItem s : salesItemList) {
            if (s.getSalesID().equalsIgnoreCase(selectedSale.getSalesID())) {
                hasOtherItems = true;
                break;
            }
        }

        if (!hasOtherItems) {
            salesList.removeIf(s -> s.getSalesID().equalsIgnoreCase(selectedSale.getSalesID()));
            JOptionPane.showMessageDialog(parent, 
                "No more items left in this sale. Sale record deleted.");
        }


        // Save updates to files
        FileUtil.saveListToFile(SALES_FILE, salesList);
        FileUtil.saveListToFile(SALES_ITEM_FILE, salesItemList);
        FileUtil.saveListToFile(ITEM_FILE, itemList);

        JOptionPane.showMessageDialog(parent, "Sales item deleted successfully.");
    }
    
}    
