package com.mycompany.owsb.view;

import com.mycompany.owsb.model.AuditLog;
import com.mycompany.owsb.model.Item;
import com.mycompany.owsb.model.PurchaseManager;
import com.mycompany.owsb.model.PurchaseOrder;
import com.mycompany.owsb.model.PurchaseRequestItemGroup;
import com.mycompany.owsb.model.PurchaseRequisition;
import com.mycompany.owsb.model.PurchaseRequisitionItem;
import com.mycompany.owsb.model.Stats;
import com.mycompany.owsb.model.SupplierPRGroup;
import com.mycompany.owsb.model.User;
import com.mycompany.owsb.model.WindowUtil;
import java.util.ArrayList;

import java.util.List;

import java.util.stream.Collectors;
import javax.swing.table.DefaultTableModel;
import javax.swing.JOptionPane;
/**


/**
 *
 * @author timi
 */
public class PurchaseManagerWindow extends javax.swing.JFrame {
    private final User loggedInUser;

    private final PurchaseManager purchaseManager;
    private List<PurchaseOrder> purchaseOrderList;
    private List<PurchaseRequisition> prList;
    private List<Item> itemList;
    private List<PurchaseRequisitionItem> prItemList;

    /**
     * Creates new form PurchaseManagerWindow
     */
    public PurchaseManagerWindow(User loggedInUser, PurchaseManager purchaseManager) {
        this.loggedInUser = loggedInUser;
        this.purchaseManager= purchaseManager;
        initComponents();
        jTextField1.setText("Enter PR ID");
        loadPRTable("All");
        loadSummaryLabels();
        showLog();
        

    }
    
    public void showLog(){
        AuditLog auditLog = new AuditLog();
        String latestLog = auditLog.getLatestLog();  // get only the latest log

        logArea.setText(latestLog);

    }
    public void showPmWindow() {
        setVisible(true);  // Show the window
    }
    
    public void initializeFilterComboBox() {
        Filter.removeAllItems();
        Filter.addItem("All");
        Filter.addItem("PENDING");
        Filter.addItem("APPROVED");
        Filter.addItem("REJECTED");
        Filter.addActionListener(e -> {
            String statusFilter = Filter.getSelectedItem().toString();
            loadPRTable(statusFilter);
        });
    }
    
    private void loadPRTable(String statusFilter) {
    // Always load fresh data
    List<PurchaseRequisition> allPRs = purchaseManager.getAllRequisitions();
        System.out.println("Total PRs" + allPRs.size());// Make sure this returns fresh full data0
    List<PurchaseRequisitionItem> prItems = PurchaseRequisitionItem.loadPurchaseRequisitionItems();
    List<Item> items = purchaseManager.getAllItems();

    // Table model with editable checkbox column
    DefaultTableModel model = new DefaultTableModel(
        new String[]{"PR ID", "Item ID", "Supplier ID", "Quantity", "Required Date", "Raised By", "Unit Cost", "Total Cost", "Status", "Select"},
        0
    ) {
        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return columnIndex == 9 ? Boolean.class : super.getColumnClass(columnIndex);
        }

        @Override
        public boolean isCellEditable(int row, int column) {
            return column == 9; // Only checkbox editable
        }
    };

    // Filter and populate
    for (PurchaseRequisition pr : allPRs) {
        if (statusFilter.equalsIgnoreCase("All") || pr.getStatus().equalsIgnoreCase(statusFilter)) {
            for (PurchaseRequisitionItem prItem : prItems) {
                if (prItem.getPrID().equalsIgnoreCase(pr.getPrID())) {
                    String itemName = items.stream()
                        .filter(item -> item.getItemID().equalsIgnoreCase(prItem.getItemID()))
                        .findFirst()
                        .map(Item::getItemName)
                        .orElse("");
                    
                    Object[] row = {
                        pr.getPrID(),
                        prItem.getItemID() + " - " + itemName,
                        pr.getSupplierID(),
                        prItem.getQuantity(),
                        pr.getRequiredDate(),
                        pr.getRaisedBy(),
                        prItem.getUnitCost(),
                        prItem.getTotalCost(),
                        pr.getStatus(),
                        false // Default unchecked
                    };
                    model.addRow(row);
                }
            }
        }
    }

    // Set and refresh table
    prTable.setModel(model);
    PurchaseRequisition.autoResizeColumnWidths(prTable);
    PurchaseRequisition.applyColorBasedOnPrID(prTable); // Optional styling
}

    
   private void loadSummaryLabels() {
    Stats stats = purchaseManager.getSummaryStats();

    lblTotalItems.setText(String.valueOf(stats.getTotalItems()));
    lblTotalSuppliers.setText(String.valueOf(stats.getTotalSuppliers()));
    lblPendingPRs.setText(String.valueOf(stats.getPendingPRs()));
    lblPendingPOs.setText(String.valueOf(stats.getPendingPOs()));
    Usernamelbl.setText(stats.getUsername());
}

    
    private List<String> getApprovedItemIDsFromPR(String prId) {
    List<PurchaseRequisitionItem> prItems = PurchaseRequisitionItem.loadPurchaseRequisitionItems();
    List<String> itemIds = new ArrayList<>();

    for (PurchaseRequisitionItem item : prItems) {
        if (item.getPrID().equalsIgnoreCase(prId)) {
            itemIds.add(item.getItemID());
        }
    }

    return itemIds;
}

   /* private void generatePOsFromSelectedRows() {
    DefaultTableModel model = (DefaultTableModel) prTable.getModel();
    if (model.getRowCount() == 0) {
        JOptionPane.showMessageDialog(this, "Table is empty. No PRs to process.", "Warning", JOptionPane.WARNING_MESSAGE);
        return;
    }

    // supplierId → (prId → itemIds)
    Map<String, Map<String, List<String>>> supplierToPRsMap = new HashMap<>();
    boolean hasSelections = false;

    for (int row = 0; row < model.getRowCount(); row++) {
        Boolean isSelected = (Boolean) model.getValueAt(row, 9);
        if (isSelected != null && isSelected) {
            hasSelections = true;
            String prId = model.getValueAt(row, 0).toString();
            String itemId = model.getValueAt(row, 1).toString().split(" - ")[0];
            String supplierId = model.getValueAt(row, 2).toString();
            String status = model.getValueAt(row, 8).toString();

            if (!status.equalsIgnoreCase("PENDING")) {
                JOptionPane.showMessageDialog(this, "Only PENDING PRs can be selected for PO generation. Invalid PR: " + prId, "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            supplierToPRsMap
                .computeIfAbsent(supplierId, k -> new HashMap<>()) // supplierId group
                .computeIfAbsent(prId, k -> new ArrayList<>())     // PR group under supplier
                .add(itemId);
        }
    }

    if (!hasSelections) {
        JOptionPane.showMessageDialog(this, "No rows selected for PO generation.", "Warning", JOptionPane.WARNING_MESSAGE);
        return;
    }

    try {
        int poCount = 0;
        String createdBy = purchaseManager.getLoggedInUser().getUserId();

        for (String supplierId : supplierToPRsMap.keySet()) {
            Map<String, List<String>> approvedItemsByPR = supplierToPRsMap.get(supplierId);
            purchaseManager.generatePurchaseOrdersFromMultiplePRs(supplierId, createdBy, approvedItemsByPR);
            poCount++;
        }

        String statusFilter = Filter.getSelectedItem().toString();
        loadPRTable(statusFilter);

        JOptionPane.showMessageDialog(this, "Successfully generated " + poCount + " purchase order(s).", "Success", JOptionPane.INFORMATION_MESSAGE);

        for (int row = 0; row < model.getRowCount(); row++) {
            model.setValueAt(false, row, 9);
        }
    } catch (IllegalArgumentException | IllegalStateException e) {
        String statusFilter = Filter.getSelectedItem().toString();
        loadPRTable(statusFilter);
        JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Unexpected error generating POs: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}*/
    
    private SupplierPRGroup findOrCreateSupplierGroup(List<SupplierPRGroup> groups, String supplierId) {
    for (SupplierPRGroup group : groups) {
        if (group.getSupplierId().equals(supplierId)) {
            return group;
        }
    }
    SupplierPRGroup newGroup = new SupplierPRGroup(supplierId);
    groups.add(newGroup);
    return newGroup;
}


    private void generatePOsFromSelectedRows() {
    DefaultTableModel model = (DefaultTableModel) prTable.getModel();
    if (model.getRowCount() == 0) {
        JOptionPane.showMessageDialog(this, "Table is empty. No PRs to process.", "Warning", JOptionPane.WARNING_MESSAGE);
        return;
    }

<<<<<<< HEAD
    // Verify authentication once
    if (!purchaseManager.isAllowedToPerform("generatePurchaseOrdersFromMultiplePRs")) {
        JOptionPane.showMessageDialog(this, "Authentication failed.", "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }

    List<SupplierPRGroup> supplierGroups = new ArrayList<>();
    boolean hasSelections = false;

    for (int row = 0; row < model.getRowCount(); row++) {
        Boolean isSelected = (Boolean) model.getValueAt(row, 9);
        if (isSelected != null && isSelected) {
            hasSelections = true;

            String prId = model.getValueAt(row, 0).toString();
            String itemId = model.getValueAt(row, 1).toString().split(" - ")[0];
            String supplierId = model.getValueAt(row, 2).toString();
            String status = model.getValueAt(row, 8).toString();

            if (!status.equalsIgnoreCase("PENDING")) {
                JOptionPane.showMessageDialog(this, "Only PENDING PRs can be selected for PO generation. Invalid PR: " + prId, "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            SupplierPRGroup supplierGroup = findOrCreateSupplierGroup(supplierGroups, supplierId);
            PurchaseRequestItemGroup prGroup = supplierGroup.getOrCreatePRGroup(prId);
            prGroup.addItem(itemId);
        }
    }

    if (!hasSelections) {
        JOptionPane.showMessageDialog(this, "No rows selected for PO generation.", "Warning", JOptionPane.WARNING_MESSAGE);
        return;
    }

    try {
        int totalPoCount = 0;
        String createdBy = purchaseManager.getLoggedInUser().getUserId();

        for (SupplierPRGroup supplierGroup : supplierGroups) {
            List<PurchaseOrder> generatedPOs = purchaseManager.generatePurchaseOrdersFromMultiplePRs(
                supplierGroup.getSupplierId(),
                createdBy,
                supplierGroup.getPrGroups()
            );
            totalPoCount += generatedPOs.size();
        }

        String statusFilter = Filter.getSelectedItem().toString();
        loadPRTable(statusFilter);

        JOptionPane.showMessageDialog(this, "Successfully generated " + totalPoCount + " purchase order(s).", "Success", JOptionPane.INFORMATION_MESSAGE);
=======
    List<SupplierPRGroup> supplierGroups = new ArrayList<>();
    boolean hasSelections = false;

    for (int row = 0; row < model.getRowCount(); row++) {
        Boolean isSelected = (Boolean) model.getValueAt(row, 9);
        if (isSelected != null && isSelected) {
            hasSelections = true;

            String prId = model.getValueAt(row, 0).toString();
            String itemId = model.getValueAt(row, 1).toString().split(" - ")[0];
            String supplierId = model.getValueAt(row, 2).toString();
            String status = model.getValueAt(row, 8).toString();

            if (!status.equalsIgnoreCase("PENDING")) {
                JOptionPane.showMessageDialog(this, "Only PENDING PRs can be selected for PO generation. Invalid PR: " + prId, "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            SupplierPRGroup supplierGroup = findOrCreateSupplierGroup(supplierGroups, supplierId);
            PurchaseRequestItemGroup prGroup = supplierGroup.getOrCreatePRGroup(prId);
            prGroup.addItem(itemId);
        }
    }

    if (!hasSelections) {
        JOptionPane.showMessageDialog(this, "No rows selected for PO generation.", "Warning", JOptionPane.WARNING_MESSAGE);
        return;
    }

    try {
        int poCount = 0;
        String createdBy = purchaseManager.getLoggedInUser().getUserId();

        for (SupplierPRGroup supplierGroup : supplierGroups) {
            purchaseManager.generatePurchaseOrdersFromMultiplePRs(
                supplierGroup.getSupplierId(),
                createdBy,
                supplierGroup.getPrGroups()
            );
            poCount++;
        }

        String statusFilter = Filter.getSelectedItem().toString();
        loadPRTable(statusFilter);

        JOptionPane.showMessageDialog(this, "Successfully generated " + poCount + " purchase order(s).", "Success", JOptionPane.INFORMATION_MESSAGE);
>>>>>>> origin/main

        for (int row = 0; row < model.getRowCount(); row++) {
            model.setValueAt(false, row, 9);
        }

    } catch (IllegalArgumentException | IllegalStateException e) {
        String statusFilter = Filter.getSelectedItem().toString();
        loadPRTable(statusFilter);
        JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Unexpected error generating POs: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}

    

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextArea2 = new javax.swing.JTextArea();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTextArea3 = new javax.swing.JTextArea();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTextArea4 = new javax.swing.JTextArea();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        Usernamelbl = new javax.swing.JLabel();
        jToggleButton1 = new javax.swing.JToggleButton();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        lblTotalSuppliers = new javax.swing.JLabel();
        lblTotalItems = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        lblPendingPRs = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        lblPendingPOs = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jButton6 = new javax.swing.JButton();
        Filter = new javax.swing.JComboBox<>();
        jButton7 = new javax.swing.JButton();
        jScrollPane7 = new javax.swing.JScrollPane();
        jScrollPane6 = new javax.swing.JScrollPane();
        prTable = new javax.swing.JTable();
        jButton8 = new javax.swing.JButton();
        Itembtn = new javax.swing.JButton();
        Supplierbtn = new javax.swing.JButton();
        PRbtn = new javax.swing.JButton();
        pendingbtn = new javax.swing.JButton();
        jScrollPane5 = new javax.swing.JScrollPane();
        jTextArea5 = new javax.swing.JTextArea();
        jScrollPane8 = new javax.swing.JScrollPane();
        logArea = new javax.swing.JTextArea();
<<<<<<< HEAD
        jButton1 = new javax.swing.JButton();
=======
>>>>>>> origin/main

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jScrollPane1.setViewportView(jTextArea1);

        jTextArea2.setColumns(20);
        jTextArea2.setRows(5);
        jScrollPane2.setViewportView(jTextArea2);

        jTextArea3.setColumns(20);
        jTextArea3.setRows(5);
        jScrollPane3.setViewportView(jTextArea3);

        jTextArea4.setColumns(20);
        jTextArea4.setRows(5);
        jScrollPane4.setViewportView(jTextArea4);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setText("Purchase Order Management System");

        jLabel2.setText("Logged in as: ");

        Usernamelbl.setText("jLabel3");

        jToggleButton1.setText("Log Out");
        jToggleButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButton1ActionPerformed(evt);
            }
        });

        jLabel3.setText("Total Items: ");

        jLabel4.setText("Total Suppliers: ");

        lblTotalSuppliers.setText("jLabel5");

        lblTotalItems.setText("jLabel6");

        jLabel7.setText("Pending PRs:");

        lblPendingPRs.setForeground(new java.awt.Color(255, 0, 0));
        lblPendingPRs.setText("jLabel8");

        jLabel9.setText("Pending POs:");

        lblPendingPOs.setText("jLabel10");

        jTextField1.setText("Search PR");
        jTextField1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField1ActionPerformed(evt);
            }
        });

        jButton6.setText("Search");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        Filter.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "ALL", "PENDING", "APPROVED", "REJECTED" }));
        Filter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                FilterActionPerformed(evt);
            }
        });

        jButton7.setText("Generate PO");
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        prTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "PR ID", "Item ID", "Quantity", "Unit Cost", "Total Cost", "Select"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.Double.class, java.lang.Double.class, java.lang.Boolean.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jScrollPane6.setViewportView(prTable);

        jScrollPane7.setViewportView(jScrollPane6);

        jButton8.setText("Home");

        Itembtn.setText("View Item");
        Itembtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ItembtnActionPerformed(evt);
            }
        });

        Supplierbtn.setText("View Supplier");
        Supplierbtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SupplierbtnActionPerformed(evt);
            }
        });

        PRbtn.setText("View PR");
        PRbtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                PRbtnActionPerformed(evt);
            }
        });

        pendingbtn.setText("View PO");
        pendingbtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pendingbtnActionPerformed(evt);
            }
        });

        jTextArea5.setColumns(20);
        jTextArea5.setRows(5);
        jScrollPane5.setViewportView(jTextArea5);

        logArea.setEditable(false);
        logArea.setColumns(20);
        logArea.setRows(5);
        jScrollPane8.setViewportView(logArea);

<<<<<<< HEAD
        jButton1.setText("View Logs");

=======
>>>>>>> origin/main
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(40, 40, 40)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jToggleButton1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Usernamelbl, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(94, 94, 94)
                .addComponent(jLabel1)
                .addGap(29, 29, 29)
                .addComponent(jScrollPane8, javax.swing.GroupLayout.DEFAULT_SIZE, 258, Short.MAX_VALUE)
                .addGap(19, 19, 19))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton7)
                .addGap(306, 306, 306))
            .addGroup(layout.createSequentialGroup()
<<<<<<< HEAD
                .addGap(49, 49, 49)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
=======
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(49, 49, 49)
>>>>>>> origin/main
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
<<<<<<< HEAD
                                .addGap(34, 34, 34)
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lblTotalItems, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(29, 29, 29)
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lblTotalSuppliers, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(27, 27, 27)
                                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jButton8)
                                .addGap(43, 43, 43)
                                .addComponent(Itembtn)
=======
                                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 397, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(82, 82, 82)
                                .addComponent(Filter, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
>>>>>>> origin/main
                                .addGap(18, 18, 18)
<<<<<<< HEAD
                                .addComponent(Supplierbtn)
                                .addGap(27, 27, 27)
                                .addComponent(PRbtn)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(25, 25, 25)
                                .addComponent(pendingbtn)
                                .addGap(36, 36, 36)
                                .addComponent(jButton1))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(lblPendingPRs, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(22, 22, 22)
                                .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lblPendingPOs, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE))))
=======
                                .addComponent(jButton6))
                            .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 656, javax.swing.GroupLayout.PREFERRED_SIZE)))
>>>>>>> origin/main
                    .addGroup(layout.createSequentialGroup()
<<<<<<< HEAD
                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 397, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(82, 82, 82)
                        .addComponent(Filter, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButton6))
                    .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 656, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(46, Short.MAX_VALUE))
=======
                        .addGap(83, 83, 83)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lblTotalItems, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(29, 29, 29)
                                .addComponent(jLabel4))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jButton8)
                                .addGap(36, 36, 36)
                                .addComponent(Itembtn)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(Supplierbtn)
                                .addGap(39, 39, 39)
                                .addComponent(PRbtn)
                                .addGap(36, 36, 36)
                                .addComponent(pendingbtn))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(lblTotalSuppliers, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(27, 27, 27)
                                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lblPendingPRs, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(22, 22, 22)
                                .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lblPendingPOs, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
>>>>>>> origin/main
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(33, 33, 33)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel2)
                                    .addComponent(Usernamelbl))
                                .addGap(18, 18, 18)
                                .addComponent(jToggleButton1)
                                .addGap(29, 29, 29))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(jScrollPane8, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)))))
<<<<<<< HEAD
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButton8)
                        .addComponent(Itembtn)
                        .addComponent(Supplierbtn)
                        .addComponent(PRbtn)
                        .addComponent(pendingbtn))
                    .addComponent(jButton1))
=======
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton8)
                    .addComponent(Itembtn)
                    .addComponent(Supplierbtn)
                    .addComponent(PRbtn)
                    .addComponent(pendingbtn))
>>>>>>> origin/main
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel4)
                        .addComponent(lblTotalSuppliers)
                        .addComponent(lblTotalItems)
                        .addComponent(jLabel7)
                        .addComponent(lblPendingPRs)
                        .addComponent(jLabel9)
                        .addComponent(lblPendingPOs)))
                .addGap(44, 44, 44)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton6)
                    .addComponent(Filter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(23, 23, 23)
                .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 303, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton7)
                .addGap(21, 21, 21))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jToggleButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButton1ActionPerformed
        // TODO add your handling code here:
        WindowUtil.logoutAndRedirectToLogin(this);

    }//GEN-LAST:event_jToggleButton1ActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
            generatePOsFromSelectedRows();   
            loadSummaryLabels();
    }//GEN-LAST:event_jButton7ActionPerformed

    private void ItembtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ItembtnActionPerformed
        // TODO add your handling code here:
        WindowUtil.switchWindow(this, new PmViewItem(this, purchaseManager));

    }//GEN-LAST:event_ItembtnActionPerformed

    private void SupplierbtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SupplierbtnActionPerformed
        // TODO add your handling code here:
         
        WindowUtil.switchWindow(this, new PmViewSupplier(this, purchaseManager));

    }//GEN-LAST:event_SupplierbtnActionPerformed

    private void PRbtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_PRbtnActionPerformed
        // TODO add your handling code here:
        
        WindowUtil.switchWindow(this, new PmViewPR(this, purchaseManager));
    }//GEN-LAST:event_PRbtnActionPerformed

    private void pendingbtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pendingbtnActionPerformed
        // TODO add your handling code here:
         
        WindowUtil.switchWindow(this, new PmViewPO(this, purchaseManager));

    }//GEN-LAST:event_pendingbtnActionPerformed

    private void FilterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_FilterActionPerformed
        // TODO add your handling code here:
        String searchQuery = jTextField1.getText().trim();
            String statusFilter = Filter.getSelectedItem().toString();
            loadPRTable(statusFilter);
    }//GEN-LAST:event_FilterActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        // TODO add your handling code here:
        String searchQuery = jTextField1.getText().trim();
if (searchQuery.isEmpty() || searchQuery.equalsIgnoreCase("Enter PR ID")) {
    String statusFilter = Filter.getSelectedItem().toString();
    loadPRTable(statusFilter);
} else {
    List<PurchaseRequisition> allPRs = purchaseManager.getAllRequisitions();
    List<PurchaseRequisitionItem> prItems = PurchaseRequisitionItem.loadPurchaseRequisitionItems();
    List<Item> items = purchaseManager.getAllItems();

    // Link items to each PR without using streams
    for (PurchaseRequisition pr : allPRs) {
        List<PurchaseRequisitionItem> itemsForPR = new ArrayList<>();
        for (PurchaseRequisitionItem item : prItems) {
            if (item.getPrID().equalsIgnoreCase(pr.getPrID())) {
                itemsForPR.add(item);
            }
        }
        pr.setPRItems(itemsForPR); // Ensure this setter exists in your PurchaseRequisition class
    }

    // Call your existing method
    PurchaseRequisition.searchAndDisplayPRInTable(jTextField1, prTable, allPRs, items, prItems);
}


    }//GEN-LAST:event_jButton6ActionPerformed

    private void jTextField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField1ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(PurchaseManagerWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(PurchaseManagerWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(PurchaseManagerWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(PurchaseManagerWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
       
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> Filter;
    private javax.swing.JButton Itembtn;
    private javax.swing.JButton PRbtn;
    private javax.swing.JButton Supplierbtn;
    private javax.swing.JLabel Usernamelbl;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextArea jTextArea2;
    private javax.swing.JTextArea jTextArea3;
    private javax.swing.JTextArea jTextArea4;
    private javax.swing.JTextArea jTextArea5;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JToggleButton jToggleButton1;
    private javax.swing.JLabel lblPendingPOs;
    private javax.swing.JLabel lblPendingPRs;
    private javax.swing.JLabel lblTotalItems;
    private javax.swing.JLabel lblTotalSuppliers;
    private javax.swing.JTextArea logArea;
    private javax.swing.JButton pendingbtn;
    private javax.swing.JTable prTable;
    // End of variables declaration//GEN-END:variables
}
