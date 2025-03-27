/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.mycompany.owsb;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 *
 * @author timi
 */
public class AdminWindow extends javax.swing.JFrame {
    //get logged in user 
    private final User loggedInUser;

    //set text file
    private final String USERS_FILE = "user.txt";
    
    //create bg purpose
    private JPanel contentPanel;
    
    //initialize users list
    private List<User> users;
    
    // Class-level variable to store users
    private List<User> userList = new ArrayList<>(); 
    
    /**
     * Creates new form AdminWindow
     */
    public AdminWindow(User loggedInUser) {
        this.loggedInUser = loggedInUser; // Use the provided user
        this.users = loadUsers();
        initComponents();
        setBackgroundImage();
        updateUserList();
        setVisible(true);
    }
    
    // Method to show the UserWindow and ensure the user list is updated
    public void showAdminWindow() {
        updateUserList();  // Ensure the user list is up to date
        setVisible(true);  // Show the window
    }
    
    // Getter for users list
    public List<User> getUserList() {
        return users; // Return the list directly
    }
    
    // Setter for users list
    public void setUsers(List<User> users) {
        this.users = users;
        updateUserList(); // Refresh the list whenever it is set
    }
    
    //load users from user.txt
    public List<User> loadUsers() {
        List<User> userList = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(USERS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                User user = User.fromString(line);
                userList.add(user);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "An error occurred while reading the file: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        return userList;
    }
    
    //update user list 
    public void updateUserList() {
        this.users = loadUsers();
        
        // Convert user list to an array of usernames for the JList
        DefaultListModel<String> listModel = new DefaultListModel<>();
        for (User user : users) {
            listModel.addElement(user.getUsername()); // show username in list
        }
        listUser.setModel(listModel);
    }

    
    // Retrieve a user by username
    public User getUser(String username) {
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }
    
    // Save the users to the file
    public void saveUsers() {
        System.out.println("Saving users to: " + USERS_FILE);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(USERS_FILE))) {
            for (User user : users) {
                writer.write(user.toString());
                writer.newLine();
            }
            System.out.println("Users successfully saved.");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "An error occurred while writing the file: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void setBackgroundImage() {
        // Load the image
        ImageIcon icon = new ImageIcon(getClass().getResource("/background.png"));
        Image image = icon.getImage();

        // Create a custom panel to hold the background image
        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(image, 0, 0, getWidth(), getHeight(), this);
            }
        };

        // Set the layout to null for absolute positioning
        backgroundPanel.setLayout(null);

        // Set the size of the background panel
        backgroundPanel.setPreferredSize(new Dimension(500, 300));


        // Set the custom panel as the content pane
        setContentPane(backgroundPanel);

        // Re-add existing components to the background panel
        backgroundPanel.add(searchField);
        backgroundPanel.add(searchButton);
        backgroundPanel.add(createUser);
        backgroundPanel.add(updateUser);
        backgroundPanel.add(deleteUser);
        backgroundPanel.add(jScrollPane1); 
        backgroundPanel.add(userDetails);
        
        // Set bounds for the components position (x, y, width, height)
        searchField.setBounds(40, 45, 312, 27);
        searchButton.setBounds(357, 45, 80, 25);
        createUser.setBounds(40, 100, 125, 30);
        updateUser.setBounds(40, 140, 125, 30);
        deleteUser.setBounds(40, 180, 125, 30);
        jScrollPane1.setBounds(200, 100, 235, 105);
        userDetails.setBounds(200,227,235,105);

        pack(); // Adjusts frame size based on the preferred size of the content pane
        revalidate();
        repaint();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        createUser = new javax.swing.JButton();
        updateUser = new javax.swing.JButton();
        deleteUser = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        listUser = new javax.swing.JList<>();
        jScrollPane2 = new javax.swing.JScrollPane();
        userDetails = new javax.swing.JTextArea();
        searchField = new javax.swing.JTextField();
        searchButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        createUser.setBackground(new java.awt.Color(204, 204, 255));
        createUser.setFont(new java.awt.Font("Heiti TC", 0, 11)); // NOI18N
        createUser.setText("Create User");
        createUser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createUserActionPerformed(evt);
            }
        });

        updateUser.setBackground(new java.awt.Color(186, 186, 255));
        updateUser.setFont(new java.awt.Font("Heiti TC", 0, 11)); // NOI18N
        updateUser.setText("Update User");
        updateUser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                updateUserActionPerformed(evt);
            }
        });

        deleteUser.setBackground(new java.awt.Color(165, 165, 250));
        deleteUser.setFont(new java.awt.Font("Heiti TC", 0, 11)); // NOI18N
        deleteUser.setText("Delete User");
        deleteUser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteUserActionPerformed(evt);
            }
        });

        listUser.setFont(new java.awt.Font("Heiti TC", 0, 11)); // NOI18N
        listUser.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        listUser.setSelectionBackground(new java.awt.Color(153, 153, 255));
        listUser.addAncestorListener(new javax.swing.event.AncestorListener() {
            public void ancestorAdded(javax.swing.event.AncestorEvent evt) {
                listUserAncestorAdded(evt);
            }
            public void ancestorMoved(javax.swing.event.AncestorEvent evt) {
            }
            public void ancestorRemoved(javax.swing.event.AncestorEvent evt) {
            }
        });
        jScrollPane1.setViewportView(listUser);

        userDetails.setEditable(false);
        userDetails.setColumns(20);
        userDetails.setFont(new java.awt.Font("Heiti TC", 0, 11)); // NOI18N
        userDetails.setLineWrap(true);
        userDetails.setRows(5);
        userDetails.setText("User Details");
        userDetails.setWrapStyleWord(true);
        userDetails.setMargin(new java.awt.Insets(4, 4, 2, 6));
        jScrollPane2.setViewportView(userDetails);

        searchField.setFont(new java.awt.Font("Heiti TC", 0, 11)); // NOI18N
        searchField.setText("Enter username");
        searchField.setToolTipText("");

        searchButton.setBackground(new java.awt.Color(174, 201, 255));
        searchButton.setFont(new java.awt.Font("Heiti TC", 0, 11)); // NOI18N
        searchButton.setText("Search");
        searchButton.setMargin(new java.awt.Insets(2, 2, 2, 2));
        searchButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(77, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(searchField, javax.swing.GroupLayout.PREFERRED_SIZE, 323, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(searchButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(updateUser, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(deleteUser, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(createUser, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(34, 34, 34)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jScrollPane1)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 235, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(73, 73, 73))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(43, 43, 43)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(searchField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(searchButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(35, 35, 35)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(createUser, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(updateUser, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(deleteUser, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(44, 44, 44))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void createUserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_createUserActionPerformed
        CreateUserWindow createUserWindow = new CreateUserWindow(this);
        createUserWindow.setVisible(true);
        this.setVisible(false); // Hide current window
    }//GEN-LAST:event_createUserActionPerformed

    private void updateUserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updateUserActionPerformed
        // Get the selected user from the list
        String selectedUser = listUser.getSelectedValue();

        //if user not selected from list, prompt to enter username
        if (selectedUser == null) {
            selectedUser = JOptionPane.showInputDialog(this, "Enter Username:");
        }

        if (selectedUser != null && !selectedUser.isEmpty()) {
            User user = getUser(selectedUser);
            if (user != null) {
                // Open the UpdateUserWindow with the selected user
                UpdateUserWindow updateUserUI = new UpdateUserWindow(this, user);
                updateUserUI.setVisible(true);
                this.setVisible(false); // Hide the current window
            } else {
                JOptionPane.showMessageDialog(this, "User not found!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "No user selected or entered!", "Error", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_updateUserActionPerformed

    private void deleteUserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteUserActionPerformed
        // Get the selected user from the list
        String selectedUser = listUser.getSelectedValue();

        //if user not selected from list, prompt to enter username
        if (selectedUser == null) {
            selectedUser = JOptionPane.showInputDialog(this, "Enter Username:");
        }

        if (selectedUser != null && !selectedUser.isEmpty()) {
            User user = getUser(selectedUser);

            if (user != null) {
                int confirmation = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete/disable this user?", "Confirm", JOptionPane.YES_NO_OPTION);
                if (confirmation == JOptionPane.YES_OPTION) {
                    users.remove(user);
                    saveUsers();
                    updateUserList(); // Refresh the list
                    JOptionPane.showMessageDialog(this, "User deleted/disabled successfully!");
                }

            } else {
                JOptionPane.showMessageDialog(this, "User not found!", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } else {
            JOptionPane.showMessageDialog(this, "No user selected or entered!", "Error", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_deleteUserActionPerformed

    private void listUserAncestorAdded(javax.swing.event.AncestorEvent evt) {//GEN-FIRST:event_listUserAncestorAdded
        // Load user data and update the list
        updateUserList();  // Ensure list is updated with latest users
        loadUsers();

        // Add or ensure the ListSelectionListener is added only once
        if (listUser.getListSelectionListeners().length == 0) {
            listUser.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
                @Override
                public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                    // Get the selected username from the list
                    String selectedUsername = listUser.getSelectedValue();

                    // Check if the selection is not empty
                    if (selectedUsername != null) {
                        // Find the user with the given username
                        User selectedUser = getUser(selectedUsername);

                        if (selectedUser != null) {
                            // Display user details in the text area
                            userDetails.setText(
                                "Username: " + selectedUser.getUsername() + "\n\n" +
                                "Password: " + selectedUser.getPassword() + "\n\n" +
                                "Role: " + selectedUser.getRole() + "\n\n"
                            );
                        } else {
                            // If no user is found
                            userDetails.setText("User not found");
                        }
                    }
                }
            });
        }
    }//GEN-LAST:event_listUserAncestorAdded

    private void searchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchButtonActionPerformed
        String searchText = searchField.getText().trim();

        // Check if the search field is not empty
        if (!searchText.isEmpty()) {
            // Find the user by username
            User foundUser = getUser(searchText);

            // Check if the user was found
            if (foundUser != null) {
                // Display user details in the userDetails JTextArea
                userDetails.setText(
                    "Username: " + foundUser.getUsername() + "\n\n" +
                    "Password: " + foundUser.getPassword() + "\n\n" +
                    "Role: " + foundUser.getRole() + "\n\n"
                );
            } else {
                // Display a message in the userDetails JTextArea if the user was not found
                userDetails.setText("User not found");
            }
        } else {
            // Prompt user to enter a search term if the field is empty
            userDetails.setText("Please enter a username to search");
        }
    }//GEN-LAST:event_searchButtonActionPerformed
    
     
        
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
            java.util.logging.Logger.getLogger(AdminWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(AdminWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(AdminWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(AdminWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new AdminWindow(null).setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton createUser;
    private javax.swing.JButton deleteUser;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JList<String> listUser;
    private javax.swing.JButton searchButton;
    private javax.swing.JTextField searchField;
    private javax.swing.JButton updateUser;
    private javax.swing.JTextArea userDetails;
    // End of variables declaration//GEN-END:variables
}
