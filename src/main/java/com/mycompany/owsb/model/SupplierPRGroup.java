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
public class SupplierPRGroup {
    private String supplierId;
    private List<PurchaseRequestItemGroup> prGroups;

    public SupplierPRGroup(String supplierId) {
        this.supplierId = supplierId;
        this.prGroups = new ArrayList<>();
    }

    public String getSupplierId() { return supplierId; }
    public List<PurchaseRequestItemGroup> getPrGroups() { return prGroups; }

    public PurchaseRequestItemGroup getOrCreatePRGroup(String prId) {
        for (PurchaseRequestItemGroup group : prGroups) {
            if (group.getPrId().equals(prId)) {
                return group;
            }
        }
        PurchaseRequestItemGroup newGroup = new PurchaseRequestItemGroup(prId);
        prGroups.add(newGroup);
        return newGroup;
    }
}
