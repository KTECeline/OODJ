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
public interface ManagePRInterface {
    public void addPurchaseRequisition(JFrame parent, List<Item> itemList, List<PurchaseRequisition> prList, 
                                    List<PurchaseRequisitionItem> prItemList, List<Supplier> supplierList, 
                                    List<SupplierItem> supplierItemList, JTable prTable);
    
    public void editPurchaseRequisition(JFrame parent, PurchaseRequisition prToEdit, PurchaseRequisitionItem itemToEdit,
                                    List<PurchaseRequisition> prList, List<PurchaseRequisitionItem> prItemList,
                                    List<SupplierItem> supplierItemList, List<Item> itemList, List<Supplier> supplierList,
                                    JTable prTable);
    
    public void deletePurchaseRequisition(JFrame parent, List<PurchaseRequisition> prList, 
                                        List<PurchaseRequisitionItem> prItemList, List<Item> itemList, JTable prTable);
    
    
}
