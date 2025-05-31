/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.owsb.model;

import java.util.List;
import javax.swing.JFrame;
import javax.swing.JTable;

/**
 *
 * @author timi
 */
public interface ManageItemInterface {  
    
    void addItem(JFrame parent, List<Item> itemList, JTable itemTable);
    
    void editItem(Item itemToEdit, List<Item> itemList, JTable itemTable);
    
    public void deleteItem(JFrame parent, List<Item> itemList, List<SupplierItem> supplierItemList, 
                        List<PurchaseRequisition> prList, List<PurchaseRequisitionItem> prItemList, JTable itemTable);
}
