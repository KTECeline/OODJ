/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.mycompany.owsb.view;

import com.mycompany.owsb.model.User;
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
import java.awt.Color;

/**
 *
 * @author timi
 */
public class AdminWindow extends javax.swing.JFrame {
    //get logged in user 
    private final User loggedInUser;

    //set text file
    private final String USERS_FILE = "data/user.txt";
    private final String PROFILE_FILE = "data/profile.txt";
    
    //create bg purpose
    private JPanel contentPanel;
    
    //initialize users list
    private List<User> users;
    
    // Class-level variable to store users
    private List<User> userList = new ArrayList<>(); 
    private Map<String, String[]> profiles;
    
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
    
    private void setBackgroundImage() {
        // Load the image
        ImageIcon icon = new ImageIcon(getClass().getResource("/background.jpg"));
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
        backgroundPanel.setPreferredSize(new Dimension(600, 500));
        
        backgroundPanel.setBackground(new Color(155,237,255));

        // Set the custom panel as the content pane
        setContentPane(backgroundPanel);

        // Re-add existing components to the background panel
        backgroundPanel.add(searchField);
        backgroundPanel.add(searchButton);
        backgroundPanel.add(createUser);
        backgroundPanel.add(updateUser);
        backgroundPanel.add(deleteUser);
        backgroundPanel.add(createUpdateProfileButton);
        backgroundPanel.add(jScrollPane1); 
        backgroundPanel.add(userDetails);
        backgroundPanel.add(jTabbedPane1);
        
        // Set bounds for the components position (x, y, width, height)
        searchField.setBounds(40, 45, 440, 27);
        searchButton.setBounds(490, 45, 87, 25);
        createUser.setBounds(40, 100, 150, 30);
        updateUser.setBounds(40, 140, 150, 30);
        deleteUser.setBounds(40, 180, 150, 30);
        createUpdateProfileButton.setBounds(40, 220, 150, 30);
        jScrollPane1.setBounds(210, 103, 370, 105);
        jTabbedPane1.setBounds(210,223,370,260);
        
        // Add JTextArea instances to JScrollPane components
        jScrollPane2.setViewportView(profileArea);
        jScrollPane3.setViewportView(userDetails);

        // Add JScrollPane components to tabs in detailsTab
        jTabbedPane1.addTab("Profile", jScrollPane2);
        jTabbedPane1.addTab("User Details", jScrollPane3);

        pack(); // Adjusts frame size based on the preferred size of the content pane
        revalidate();
        repaint();
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
    
    //Method to load profile from the profile.txt file
    private Map<String, String[]> loadProfiles() {
        Map<String, String[]> profileMap = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(PROFILE_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|"); // Use pipe as delimiter
                if (parts.length == 9) { // Ensure there are 9 fields (username + 8 profile fields)
                    profileMap.put(parts[0], Arrays.copyOfRange(parts, 1, 9)); // Copy the 8 fields excluding username
                } else {
                    System.out.println("Invalid profile format: " + line); // Debug line
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error reading profile file: " + e.getMessage());
        }
        return profileMap;
    }         
    
    //Method to display the profile of a selected user
    private void displayProfile(String username) {
        profiles = loadProfiles();
        String[] profile = profiles.get(username);
        if (profile != null) {
            profileArea.setText(String.format(
                "Username: %s\n\n" +
                "Name: %s\nDate of Birth: %s\nEmail: %s\nPhone: %s\nEmergency Contact: %s\nEmergency Contact Name: %s\nGender: %s\nAddress: %s",
                username, profile[5], profile[0], profile[1], profile[2], profile[3], profile[6], profile[4], profile[7] // Added address (profile[7])
            ));
        } else {
            profileArea.setText("No profile found for the selected user.");
        }
    }      
    
    //Validations
    private boolean isValidEmail(String email) {
        if (email.contains(" ")) {
            return false; 
        }
        int atIndex = email.indexOf("@");
        return atIndex > 0 && email.substring(atIndex).contains("mail.com");
    }
    
    private boolean isValidPhoneNumber(String phone) {
        return phone.matches("^01\\d{8,9}$") && !phone.contains(" "); // Disallow spaces
    }
    
    private boolean isValidEmergencyContact(String contact) {
        return contact.matches("^01\\d{8,9}$") && !contact.contains(" "); // Disallow spaces
    }
    
    private boolean isValidDate(String day, String month, String year) {
        try {
            int d = Integer.parseInt(day);
            int m = Integer.parseInt(month);
            int y = Integer.parseInt(year);

            // Create a calendar object for the input date
            Calendar inputDate = Calendar.getInstance();
            inputDate.setLenient(false); // Ensure the date is strictly valid
            inputDate.set(y, m - 1, d);  // month is 0-based in Calendar

            // Try to check if the date is valid (will throw exception if not)
            inputDate.getTime();

            // Create a calendar object for the current date
            Calendar today = Calendar.getInstance();
            today.set(Calendar.HOUR_OF_DAY, 0); // Set time to 00:00 for an accurate comparison
            today.set(Calendar.MINUTE, 0);
            today.set(Calendar.SECOND, 0);
            today.set(Calendar.MILLISECOND, 0);

            // Return true if the input date is before today
            return inputDate.before(today);
        } catch (NumberFormatException e) {
            // Handle invalid number format (e.g., if day, month, or year is not a number)
            return false;
        } catch (IllegalArgumentException e) {
            // Handle invalid date values (e.g., February 30th)
            return false;
        }
    }

    //Method to create profile for a selected user
    private void createProfile(String username) {
        JComboBox<String> dayComboBox = new JComboBox<>(getDayOptions());
        JComboBox<String> monthComboBox = new JComboBox<>(getMonthOptions());
        JComboBox<String> yearComboBox = new JComboBox<>(getYearOptions());
        JTextField emailField = new JTextField(10);
        JTextField phoneField = new JTextField(10);
        JTextField emergencyContactField = new JTextField(10);
        JTextField nameField = new JTextField(10);
        JTextField emergencyContactNameField = new JTextField(10);
        JTextField addressField = new JTextField(20); 
        JComboBox<String> genderComboBox = new JComboBox<>(new String[]{"Male", "Female"});

        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Date of Birth:"));
        panel.add(new JLabel("Day:"));
        panel.add(dayComboBox);
        panel.add(new JLabel("Month:"));
        panel.add(monthComboBox);
        panel.add(new JLabel("Year:"));
        panel.add(yearComboBox);
        panel.add(new JLabel("Email:"));
        panel.add(emailField);
        panel.add(new JLabel("Phone Number:"));
        panel.add(phoneField);
        panel.add(new JLabel("Emergency Contact:"));
        panel.add(emergencyContactField);
        panel.add(new JLabel("Emergency Contact Name:"));
        panel.add(emergencyContactNameField);
        panel.add(new JLabel("Gender:"));
        panel.add(genderComboBox);
        panel.add(new JLabel("Address:")); // New address label
        panel.add(addressField); // Add address field

        boolean isValid = false;

        while (!isValid) {
            int result = JOptionPane.showConfirmDialog(null, panel, "Create Profile", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.CANCEL_OPTION) {
                return; // User canceled the profile creation
            }

            StringBuilder errorMessage = new StringBuilder();

            String day = (String) dayComboBox.getSelectedItem();
            String month = (String) monthComboBox.getSelectedItem();
            String year = (String) yearComboBox.getSelectedItem();
            String dob = String.format("%s-%s-%s", day, month, year);
            String email = emailField.getText();
            String phone = phoneField.getText();
            String emergencyContact = emergencyContactField.getText();
            String emergencyContactName = emergencyContactNameField.getText();
            String name = nameField.getText();
            String address = addressField.getText();

            if (!isValidDate(day, month, year)) {
                errorMessage.append("Invalid date of birth. Please enter a valid date.\n");
            }

            if (!isValidEmail(email)) {
                errorMessage.append("Invalid email format. Please use @ and mail.com.\n");
            }

            if (!isValidPhoneNumber(phone)) {
                errorMessage.append("Invalid phone number format.\n");
            }

            if (!isValidEmergencyContact(emergencyContact)) {
                errorMessage.append("Invalid emergency contact format.\n");
            }

            if (errorMessage.length() > 0) {
                JOptionPane.showMessageDialog(null, errorMessage.toString(), "Validation Errors", JOptionPane.ERROR_MESSAGE);
            } else {
                isValid = true;
                String[] newProfile = {
                    dob,
                    email,
                    phone,
                    emergencyContact,
                    (String) genderComboBox.getSelectedItem(),
                    name,
                    emergencyContactName,
                    address
                };
                profiles.put(username, newProfile);
                saveProfiles();
                JOptionPane.showMessageDialog(null, "Profile created successfully.");
                displayProfile(username);
            }
        }
    }
      
    //Method to update profile for a selected user
    private void updateProfile(String username) {
        if (username == null || username.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a username from the list.");
            return;
        }

        String[] profile = profiles.get(username);
        String[] dobParts = profile[0].split("-");
        JComboBox<String> dayComboBox = new JComboBox<>(getDayOptions());
        JComboBox<String> monthComboBox = new JComboBox<>(getMonthOptions());
        JComboBox<String> yearComboBox = new JComboBox<>(getYearOptions());
        dayComboBox.setSelectedItem(dobParts[0]);
        monthComboBox.setSelectedItem(dobParts[1]);
        yearComboBox.setSelectedItem(dobParts[2]);

        JTextField emailField = new JTextField(profile[1]);
        JTextField phoneField = new JTextField(profile[2]);
        JTextField emergencyContactField = new JTextField(profile[3]);
        JTextField nameField = new JTextField(profile[5]);
        JTextField emergencyContactNameField = new JTextField(profile[6]);
        JTextField addressField = new JTextField(profile[7]); // New address field
        JComboBox<String> genderComboBox = new JComboBox<>(new String[]{"Male", "Female"});
        genderComboBox.setSelectedItem(profile[4]);

        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Date of Birth:"));
        panel.add(new JLabel("Day:"));
        panel.add(dayComboBox);
        panel.add(new JLabel("Month:"));
        panel.add(monthComboBox);
        panel.add(new JLabel("Year:"));
        panel.add(yearComboBox);
        panel.add(new JLabel("Email:"));
        panel.add(emailField);
        panel.add(new JLabel("Phone Number:"));
        panel.add(phoneField);
        panel.add(new JLabel("Emergency Contact:"));
        panel.add(emergencyContactField);
        panel.add(new JLabel("Emergency Contact Name:"));
        panel.add(emergencyContactNameField);
        panel.add(new JLabel("Gender:"));
        panel.add(genderComboBox);
        panel.add(new JLabel("Address:")); // New address label
        panel.add(addressField); // Add address field

        boolean isValid = false;

        while (!isValid) {
            int result = JOptionPane.showConfirmDialog(null, panel, "Update Profile", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.CANCEL_OPTION) {
                return; // User canceled the update
            }

            StringBuilder errorMessage = new StringBuilder();

            String day = (String) dayComboBox.getSelectedItem();
            String month = (String) monthComboBox.getSelectedItem();
            String year = (String) yearComboBox.getSelectedItem();
            String dob = String.format("%s-%s-%s", day, month, year);
            String email = emailField.getText();
            String phone = phoneField.getText();
            String emergencyContact = emergencyContactField.getText();
            String emergencyContactName = emergencyContactNameField.getText();
            String name = nameField.getText();
            String address = addressField.getText(); // Get the updated address

            if (!isValidDate(day, month, year)) {
                errorMessage.append("Invalid date of birth. Please enter a valid date.\n");
            }

            if (!isValidEmail(email)) {
                errorMessage.append("Invalid email format. Please use @ and mail.com.\n");
            }

            if (!isValidPhoneNumber(phone)) {
                errorMessage.append("Invalid phone number format.\n");
            }

            if (!isValidEmergencyContact(emergencyContact)) {
                errorMessage.append("Invalid emergency contact format.\n");
            }

            if (errorMessage.length() > 0) {
                JOptionPane.showMessageDialog(null, errorMessage.toString(), "Validation Errors", JOptionPane.ERROR_MESSAGE);
            } else {
                isValid = true;
                String[] updatedProfile = {
                    dob,
                    email,
                    phone,
                    emergencyContact,
                    (String) genderComboBox.getSelectedItem(),
                    name,
                    emergencyContactName,
                    address
                };
                profiles.put(username, updatedProfile);
                saveProfiles();
                JOptionPane.showMessageDialog(null, "Profile updated successfully.");
                displayProfile(username);
            }
        }
    }

    //Drop-down options for date
    private String[] getDayOptions() {
        String[] days = new String[31];
        for (int i = 0; i < 31; i++) {
            days[i] = String.format("%02d", i + 1);
        }
        return days;
    }

    private String[] getMonthOptions() {
        String[] months = new String[12];
        for (int i = 0; i < 12; i++) {
            months[i] = String.format("%02d", i + 1);
        }
        return months;
    }

    private String[] getYearOptions() {
        String[] years = new String[100];
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = 0; i < 100; i++) {
            years[i] = Integer.toString(currentYear - i);
        }
        return years;
    }
    
    //Method to save updated profile to the profile.txt file
    private void saveProfiles() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(PROFILE_FILE))) {
            for (Map.Entry<String, String[]> entry : profiles.entrySet()) {
                String[] profile = entry.getValue();
                if (profile.length == 8) { // Ensure 8 fields exist (including address) before writing
                    writer.write(entry.getKey() + "|" + String.join("|", profile) + "\n"); // Use pipe as delimiter
                } else {
                    System.out.println("Invalid profile data for user: " + entry.getKey()); // Debug line
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error saving profile file: " + e.getMessage());
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

        createUser = new javax.swing.JButton();
        updateUser = new javax.swing.JButton();
        deleteUser = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        listUser = new javax.swing.JList<>();
        searchField = new javax.swing.JTextField();
        searchButton = new javax.swing.JButton();
        createUpdateProfileButton = new javax.swing.JButton();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jScrollPane2 = new javax.swing.JScrollPane();
        userDetails = new javax.swing.JTextArea();
        jScrollPane3 = new javax.swing.JScrollPane();
        profileArea = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Administration");
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

        createUpdateProfileButton.setBackground(new java.awt.Color(153, 153, 255));
        createUpdateProfileButton.setFont(new java.awt.Font("Heiti TC", 0, 11)); // NOI18N
        createUpdateProfileButton.setText("Create/Update Profile");
        createUpdateProfileButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createUpdateProfileButtonActionPerformed(evt);
            }
        });

        jTabbedPane1.setFont(new java.awt.Font("Heiti TC", 0, 12)); // NOI18N

        userDetails.setEditable(false);
        userDetails.setColumns(20);
        userDetails.setFont(new java.awt.Font("Heiti TC", 0, 11)); // NOI18N
        userDetails.setLineWrap(true);
        userDetails.setRows(5);
        userDetails.setText("User Details");
        userDetails.setWrapStyleWord(true);
        userDetails.setMargin(new java.awt.Insets(4, 4, 2, 6));
        jScrollPane2.setViewportView(userDetails);

        jTabbedPane1.addTab("User Details", jScrollPane2);

        profileArea.setEditable(false);
        profileArea.setColumns(20);
        profileArea.setFont(new java.awt.Font("Heiti TC", 0, 11)); // NOI18N
        profileArea.setRows(5);
        profileArea.setText("User Profile");
        jScrollPane3.setViewportView(profileArea);

        jTabbedPane1.addTab("Profile", jScrollPane3);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(createUpdateProfileButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(updateUser, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(deleteUser, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(createUser, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(34, 34, 34)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 341, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 341, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(searchField, javax.swing.GroupLayout.PREFERRED_SIZE, 411, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(searchButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGap(49, 49, 49))
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
                        .addComponent(createUser, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(updateUser, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(deleteUser, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(createUpdateProfileButton, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 251, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(51, 51, 51))
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
                        // Display the full profile of the selected user
                        displayProfile(selectedUsername);
                        
                        // Find the user with the given username
                        User selectedUser = getUser(selectedUsername);

                        if (selectedUser != null) {
                            // Display user details in the text area
                            userDetails.setText(
                                "UserID: " + selectedUser.getUserId() + "\n\n" +
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

    private void createUpdateProfileButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_createUpdateProfileButtonActionPerformed
        String selectedUsername = listUser.getSelectedValue();

        if (selectedUsername != null) {
            if (profiles.containsKey(selectedUsername)) {
                updateProfile(selectedUsername);
            } else {
                createProfile(selectedUsername);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a username from the list.");
        }
    }//GEN-LAST:event_createUpdateProfileButtonActionPerformed
    
     
        
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
    private javax.swing.JButton createUpdateProfileButton;
    private javax.swing.JButton createUser;
    private javax.swing.JButton deleteUser;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JList<String> listUser;
    private javax.swing.JTextArea profileArea;
    private javax.swing.JButton searchButton;
    private javax.swing.JTextField searchField;
    private javax.swing.JButton updateUser;
    private javax.swing.JTextArea userDetails;
    // End of variables declaration//GEN-END:variables
}
