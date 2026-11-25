package com.inventory.controller;

import com.inventory.model.User;
import com.inventory.service.AuthService;
import com.inventory.util.ValidationException;
import com.inventory.view.LoginFrame;
import com.inventory.view.MainFrame;
import com.inventory.view.NewJFrame;
import java.awt.Window;
import java.sql.SQLException;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class AuthController {

    private final AuthService authService;
    private LoginFrame loginFrame;

    public AuthController() {
        this.authService = new AuthService();
    }

    public void setLoginFrame(LoginFrame loginFrame) {
        this.loginFrame = loginFrame;
    }

    public void handleLogin(String username, String password) {
        try {
            User user = authService.login(username, password);
            if (user != null) {
                loginFrame.dispose();
                SwingUtilities.invokeLater(() -> {
                    NewJFrame frame = new NewJFrame(user);
                    frame.setVisible(true);
                });
            }
        } catch (ValidationException ve) {
            JOptionPane.showMessageDialog(
                loginFrame,
                ve.getMessage(),
                "Authentication Notice",
                JOptionPane.WARNING_MESSAGE
            );
        } catch (SQLException sqle) {
            JOptionPane.showMessageDialog(
                loginFrame,
                "Database Connection Error:\n" + sqle.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    public void handleLogout(Window window) {
        int confirm = JOptionPane.showConfirmDialog(
            window,
            "Are you sure you want to log out of BrewTrack?",
            "Confirm Logout",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            AuthService.logout();
            window.dispose();
            SwingUtilities.invokeLater(() -> {
                LoginFrame frame = new LoginFrame();
                frame.setVisible(true);
            });
        }
    }

    public void handleLogout(MainFrame mainFrame) {
        handleLogout((Window) mainFrame);
    }

    public void handleLogout(NewJFrame newJFrame) {
        handleLogout((Window) newJFrame);
    }
}
