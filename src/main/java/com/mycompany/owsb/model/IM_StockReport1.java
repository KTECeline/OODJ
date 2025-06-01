package com.mycompany.owsb.model;

import java.util.ArrayList;
import java.util.List;

public class IM_StockReport1 {

    //Load and return the full list of items
    public List<Item> getAllItems() {
        return Item.loadItems();
    }

    //Filter the list of items for those items' stock less than 10
    public List<Item> filterLowStock(List<Item> itemList) {
        List<Item> filteredList = new ArrayList<>();
        for (Item item : itemList) {
            if (item.getStock() < 10) {
                filteredList.add(item);
            }
        }
        return filteredList;
    }

    //Calculate the total number of items
    public int calculateTotalItems(List<Item> itemList) {
        return itemList.size();
    }

    //Calculate the total number of stocks for all items
    public int calculateTotalStocks(List<Item> itemList) {
        int totalStock = 0;
        for (Item item : itemList) {
            totalStock += item.getStock();
        }
        return totalStock;
    }
}