package com.mycompany.owsb.model;

public class InventoryManager extends Manager {

    public InventoryManager(User loggedInUser) {
        super(loggedInUser);
    }

    @Override
    public boolean isAllowedToPerform(String action) {
        if (getDepartment().equalsIgnoreCase("Inventory Manager")) {
            // Inventory Manager allowed actions
            if (action.equalsIgnoreCase("ViewItemList") ||
                action.equalsIgnoreCase("UpdateStock") ||
                action.equalsIgnoreCase("GenerateStockReport")) {
                return true;
            }
        }
        return false;
    }
}
