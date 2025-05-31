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
public interface ManageSupplierInterface {
    
    void addSupplier(JFrame parent, List<Supplier> supplierList, List<SupplierItem> supplierItemList, List<Item> itemList, JTable supplierTable);
    
    void editSupplier(Supplier supplierToEdit, List<Supplier> supplierList, List<SupplierItem> supplierItemList, List<Item> itemList, JTable supplierTable);
    
    void deleteSupplier(JFrame parent, List<Supplier> supplierList, List<SupplierItem> supplierItemList, JTable supplierTable);
    
}
