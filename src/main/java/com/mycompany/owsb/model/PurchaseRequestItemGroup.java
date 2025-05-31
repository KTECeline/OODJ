/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.owsb.model;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author leopa
 */
public class PurchaseRequestItemGroup {
    private String prId;
    private List<String> itemIds;

    public PurchaseRequestItemGroup(String prId) {
        this.prId = prId;
        this.itemIds = new ArrayList<>();
    }

    public String getPrId() { return prId; }
    public List<String> getItemIds() { return itemIds; }

    public void addItem(String itemId) {
        itemIds.add(itemId);
    }
}
