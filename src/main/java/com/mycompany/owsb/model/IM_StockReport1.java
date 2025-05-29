package com.mycompany.owsb.model;

import java.io.*;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class IM_StockReport1 {

    public static DefaultTableModel getSummaryTableModel() {
        String[] columns = { "Item ID", "Item Name", "Stock Quantity" };
        DefaultTableModel model = new DefaultTableModel(columns, 0);

        try (BufferedReader br = new BufferedReader(new FileReader("data/items.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 5) {
                    String itemID = parts[0];
                    String itemName = parts[1];
                    int stockQty = Integer.parseInt(parts[2]);

                    model.addRow(new Object[]{ itemID, itemName, stockQty });
                }
            }
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }

        return model;
    }

    public static void applyRowColorBasedOnStockQuantity(JTable table) {
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                try {
                    int stock = Integer.parseInt(table.getModel().getValueAt(row, 2).toString()); // Column 2 = Stock Qty

                    if (stock < 10) {
                        c.setBackground(new Color(255, 204, 204)); // Light red
                    } else {
                        c.setBackground(new Color(204, 255, 204)); // Light green
                    }

                    if (isSelected) {
                        c.setBackground(table.getSelectionBackground());
                    }
                } catch (NumberFormatException e) {
                    c.setBackground(Color.WHITE);
                }

                return c;
            }
        });
    }
}
