package com.mycompany.owsb.view;

import com.mycompany.owsb.model.User;
import com.mycompany.owsb.model.FinanceManager;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class FinanceManagerWindow extends javax.swing.JFrame {
    private final User loggedInUser;
    private final FinanceManager financeManager;

    /**
     * Creates new form FinanceManagerWindow
     * @param loggedInUser
     */
    public FinanceManagerWindow(User loggedInUser) {
        this.loggedInUser = loggedInUser;
        this.financeManager = new FinanceManager(loggedInUser);
        initComponents();
        updateUserDisplay();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">  
    private void initComponents() {
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Finance Manager Dashboard");
        setSize(700, 500);
        setLocationRelativeTo(null);
        
        // Create main panel with BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(245, 245, 245));
        
        // Header panel
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Content panel
        JPanel contentPanel = createContentPanel();
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        
        // Footer panel
        JPanel footerPanel = createFooterPanel();
        mainPanel.add(footerPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    /**
     * Create the header panel with welcome message and logout
     */
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(51, 51, 51));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        // Welcome label
        welcomeLabel = new JLabel("Finance Manager Dashboard");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 20));
        welcomeLabel.setForeground(Color.WHITE);
        
        // User info panel
        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        userPanel.setBackground(new Color(51, 51, 51));
        
        loggedInUsernameLabel = new JLabel();
        loggedInUsernameLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        loggedInUsernameLabel.setForeground(Color.WHITE);
        
        logOutButton = new JButton("Log Out");
        logOutButton.setBackground(new Color(220, 20, 60));
        logOutButton.setForeground(Color.WHITE);
        logOutButton.setFont(new Font("Arial", Font.BOLD, 12));
        logOutButton.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        logOutButton.setFocusPainted(false);
        logOutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                logOutButtonActionPerformed(evt);
            }
        });
        
        userPanel.add(loggedInUsernameLabel);
        userPanel.add(Box.createHorizontalStrut(10));
        userPanel.add(logOutButton);
        
        headerPanel.add(welcomeLabel, BorderLayout.WEST);
        headerPanel.add(userPanel, BorderLayout.EAST);
        
        return headerPanel;
    }
    
    /**
     * Create the main content panel with function buttons
     */
    private JPanel createContentPanel() {
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(new Color(245, 245, 245));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        
        // Create function buttons
        approvePOButton = createStyledButton("Approve Purchase Orders", 
            "Review and approve pending purchase orders", new Color(34, 139, 34));
        approvePOButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                approvePOButtonActionPerformed(evt);
            }
        });
        
        viewPRButton = createStyledButton("Verify Inventory Updates", 
            "Verify received inventory updates", new Color(70, 130, 180));
        viewPRButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                verifyInventoryButtonActionPerformed(evt);
            }
        });
        
        generateReportButton = createStyledButton("Generate Financial Report", 
            "Generate comprehensive financial reports", new Color(255, 140, 0));
        generateReportButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                generateReportButtonActionPerformed(evt);
            }
        });
        
        processPaymentButton = createStyledButton("Process Payments", 
            "Process payments for approved orders", new Color(147, 112, 219));
        processPaymentButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                processPaymentButtonActionPerformed(evt);
            }
        });
        
        // Add buttons to panel in 2x2 grid
        gbc.gridx = 0; gbc.gridy = 0;
        contentPanel.add(approvePOButton, gbc);
        
        gbc.gridx = 1; gbc.gridy = 0;
        contentPanel.add(viewPRButton, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        contentPanel.add(generateReportButton, gbc);
        
        gbc.gridx = 1; gbc.gridy = 1;
        contentPanel.add(processPaymentButton, gbc);
        
        return contentPanel;
    }
    
    /**
     * Create a styled button with description
     */
    private JButton createStyledButton(String title, String description, Color bgColor) {
        JButton button = new JButton();
        button.setLayout(new BorderLayout());
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(250, 120));
        
        JLabel titleLabel = new JLabel(title, JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        titleLabel.setForeground(Color.WHITE);
        
        JLabel descLabel = new JLabel("<html><center>" + description + "</center></html>", JLabel.CENTER);
        descLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        descLabel.setForeground(new Color(230, 230, 230));
        
        button.add(titleLabel, BorderLayout.CENTER);
        button.add(descLabel, BorderLayout.SOUTH);
        
        // Add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor.brighter());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });
        
        return button;
    }
    
    /**
     * Create footer panel with system info
     */
    private JPanel createFooterPanel() {
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footerPanel.setBackground(new Color(245, 245, 245));
        footerPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 15, 0));
        
        JLabel footerLabel = new JLabel("Office Workflow System - Finance Management Module");
        footerLabel.setFont(new Font("Arial", Font.ITALIC, 10));
        footerLabel.setForeground(Color.GRAY);
        
        footerPanel.add(footerLabel);
        return footerPanel;
    }
    
    /**
     * Update the user display with logged in user information
     */
    private void updateUserDisplay() {
        if (loggedInUser != null) {
            loggedInUsernameLabel.setText("Welcome, " + loggedInUser.getUsername());
        } else {
            loggedInUsernameLabel.setText("Welcome, Guest User");
        }
    }

    /**
     * Handle logout button click
     */
    private void logOutButtonActionPerformed(ActionEvent evt) {
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to log out?", 
            "Confirm Logout", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            this.dispose(); // Closes the current FinanceManagerWindow
            LoginWindow loginWindow = new LoginWindow();
            loginWindow.setVisible(true);
        }
    }

    /**
     * Handle approve PO button click
     */
    private void approvePOButtonActionPerformed(ActionEvent evt) {
        FM_ViewPO managePOWindow = new FM_ViewPO(this, financeManager);
        managePOWindow.setVisible(true);
        this.setVisible(false); // Hide current window
    }

    /**
     * Handle verify inventory button click
     */
    private void verifyInventoryButtonActionPerformed(ActionEvent evt) {
        FM_VerifyInventory verifyInventoryWindow = new FM_VerifyInventory(this, financeManager);
        verifyInventoryWindow.setVisible(true);
        this.setVisible(false); // Hide current window
    }

    /**
     * Handle generate report button click
     */
    private void generateReportButtonActionPerformed(ActionEvent evt) {
        try {
            FM_Report reportWindow = new FM_Report(this, financeManager);
            reportWindow.setVisible(true);
            this.setVisible(false); // Hide current window
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error opening Financial Reports: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Handle process payment button click
     */
    private void processPaymentButtonActionPerformed(ActionEvent evt) {
        FM_Payment paymentWindow = new FM_Payment(this, financeManager);
        paymentWindow.setVisible(true);
        this.setVisible(false); // Hide current window
    }// </editor-fold> 

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(FinanceManagerWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            new FinanceManagerWindow(null).setVisible(true);
        });
    }
    
    // Variables declaration - do not modify                     
    private javax.swing.JButton logOutButton;
    private javax.swing.JLabel loggedInUsernameLabel;
    private javax.swing.JLabel welcomeLabel;
    private javax.swing.JButton approvePOButton;
    private javax.swing.JButton viewPRButton;
    private javax.swing.JButton generateReportButton;
    private javax.swing.JButton processPaymentButton;
    // End of variables declaration  
}