/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.mycompany.owsb.model;

import java.util.List;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.JTextArea;

/**
 *
 * @author timi
 */
public interface ManageSalesInterface {
    public Sales addSales(JFrame parent, String loggedInUsername, List<Sales> salesList);

    public void editSalesRecord(JFrame parent, Sales sale, List<Sales> salesList);
    
    public void deleteSalesRecord(JFrame parent, String salesID, List<Sales> salesList, 
                              List<SalesItem> salesItemList, List<Item> itemList, 
                              JList<String> salesIDList, JTextArea salesDetailsArea, JTable salesItemTable);
    
    public void addSalesItems(JFrame parent, Sales sale, List<Item> itemList, List<SalesItem> salesItemList, 
                            List<Sales> salesList, boolean allowCancelSale) ;
    
    public void deleteSalesItem(JFrame parent, Sales selectedSale, SalesItem selectedSalesItem, 
                            List<SalesItem> salesItemList, List<Sales> salesList, List<Item> itemList);
    
    public void editSalesItemQuantity(JFrame parent, Sales selectedSale, SalesItem selectedSalesItem, 
                                List<SalesItem> salesItemList, List<Sales> salesList, List<Item> itemList) ;
}
