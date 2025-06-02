/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.owsb.model;

import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.JTextField;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author leopa
 */
public class Purchase_GuiUpdater {
      /**
     * Updates the PR table with filtered requisitions.
     * @param statusFilter The status to filter by (e.g., "PENDING", "ALL").
     * @param targetTable The JTable to update.
     */
    public void loadViewPR(String statusFilter, JTable targetTable) {
        List<PurchaseRequisition> filteredPRs = getFilteredRequisitions(statusFilter);
        List<PurchaseRequisitionItem> prItems = PurchaseRequisitionItem.loadPurchaseRequisitionItems();
        List<Item> items = Item.loadItems();
        PurchaseRequisition.updatePRTableInUI(filteredPRs, prItems, items, targetTable);
    }

    /**
     * Performs a search or filter operation on PRs and updates the table.
     * @param searchField The text field containing the search query.
     * @param filter The combo box for status filtering.
     * @param prTable The JTable to update.
     */
    public void performSearchOrFilter(JTextField searchField, JComboBox<String> filter, JTable prTable) {
        String searchQuery = searchField.getText().trim();
        if (searchQuery.isEmpty() || "Enter PR ID".equalsIgnoreCase(searchQuery)) {
            String selectedStatus = filter.getSelectedItem().toString();
            loadViewPR(selectedStatus, prTable);
        } else {
            List<PurchaseRequisition> allPRs = PurchaseRequisition.loadPurchaseRequisition();
            List<PurchaseRequisitionItem> prItems = PurchaseRequisitionItem.loadPurchaseRequisitionItems();
            List<Item> items = Item.loadItems();

            for (PurchaseRequisition pr : allPRs) {
                List<PurchaseRequisitionItem> itemsForPR = new ArrayList<>();
                for (PurchaseRequisitionItem item : prItems) {
                    if (item.getPrID().equalsIgnoreCase(pr.getPrID())) {
                        itemsForPR.add(item);
                    }
                }
                pr.setPRItems(itemsForPR);
            }

            PurchaseRequisition.searchAndDisplayPRInTable(searchField, prTable, allPRs, items, prItems);
        }
    }

    /**
     * Retrieves PRs filtered by status.
     * @param statusFilter The status to filter by (e.g., "PENDING", "ALL").
     * @return List of filtered PurchaseRequisition objects.
     */
    private List<PurchaseRequisition> getFilteredRequisitions(String statusFilter) {
        List<PurchaseRequisition> allPRs = PurchaseRequisition.loadPurchaseRequisition();
        List<PurchaseRequisitionItem> allItems = PurchaseRequisitionItem.loadPurchaseRequisitionItems();

        for (PurchaseRequisition pr : allPRs) {
            List<PurchaseRequisitionItem> itemsForPR = new ArrayList<>();
            for (PurchaseRequisitionItem item : allItems) {
                if (item.getPrID().equalsIgnoreCase(pr.getPrID())) {
                    itemsForPR.add(item);
                }
            }
            pr.setPRItems(itemsForPR);
        }

        if ("ALL".equalsIgnoreCase(statusFilter)) {
            return allPRs;
        }

        List<PurchaseRequisition> filtered = new ArrayList<>();
        for (PurchaseRequisition pr : allPRs) {
            if (statusFilter.equalsIgnoreCase(pr.getStatus())) {
                filtered.add(pr);
            }
        }
        return filtered;
    }
    
}
