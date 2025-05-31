package com.mycompany.owsb.model;

import java.util.ArrayList;
import java.util.List;

public class IM_StockReport1 {

    public List<Item> getAllItems() {
        return Item.loadItems();
    }

    public List<Item> filterLowStock(List<Item> itemList) {
        List<Item> filteredList = new ArrayList<>();
        for (Item item : itemList) {
            if (item.getStock() < 10) {
                filteredList.add(item);
            }
        }
        return filteredList;
    }

    public int calculateTotalItems(List<Item> itemList) {
        return itemList.size();
    }

    public int calculateTotalStocks(List<Item> itemList) {
        int totalStock = 0;
        for (Item item : itemList) {
            totalStock += item.getStock();
        }
        return totalStock;
    }
}