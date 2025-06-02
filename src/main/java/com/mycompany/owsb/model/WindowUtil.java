/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.owsb.model;

import com.mycompany.owsb.view.LoginWindow;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 *
 * @author leopa
 */
public class WindowUtil {
    public static void logoutAndRedirectToLogin(JFrame currentWindow) {
        int confirm = JOptionPane.showConfirmDialog(
            currentWindow,
            "Are you sure you want to log out?",
            "Confirm Logout",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            currentWindow.dispose(); // Close the current window
            LoginWindow loginWindow = new LoginWindow(); // Launch login
            loginWindow.setVisible(true);
        }
    }

    
    public static void switchWindow(JFrame currentWindow, JFrame nextWindow) {
        nextWindow.setVisible(true);
        currentWindow.setVisible(false);
    }
        //
    public static void goBack(JFrame currentWindow, JFrame parentWindow) {
        currentWindow.dispose();
        if (parentWindow != null) {
            parentWindow.setVisible(true);
        }
    }
}
