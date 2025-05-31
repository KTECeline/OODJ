package com.mycompany.owsb.model;

public class InventoryManager extends Manager {
    /**
     * @param loggedInUser The user currently logged in.
     */
    public InventoryManager(User loggedInUser) {
        //Call the superclass (Manager) constructor to set up the user context.
        super(loggedInUser);
    }

    /**
     *  Checks if the user's department is "Inventory Manager" and if the action
     *  is one of the permitted actions ("ViewItemList", "UpdateStock", "GenerateStockReport").
     * 
     * @param action The action to check permission for.
     * @return true if allowed, false otherwise.
     */
    @Override
    public boolean isAllowedToPerform(String action) {
        if (getDepartment().equalsIgnoreCase("Inventory Manager") || getDepartment().equalsIgnoreCase("Administrator") ||getDepartment().equalsIgnoreCase("Root Administrator")) {
            // Inventory Manager allowed actions
            if (action.equalsIgnoreCase("ViewItemList") ||
                action.equalsIgnoreCase("UpdateStock") ||
                action.equalsIgnoreCase("StockReport")) {
                return true;
            }
        }
        return false;
    }
}
