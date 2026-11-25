package com.inventory.view;

import com.inventory.controller.AuthController;
import com.inventory.util.UITheme;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;

/**
 * LoginFrame – GUI Form for user authentication.
 */
public class LoginFrame extends javax.swing.JFrame {

    private final AuthController authController;

    public LoginFrame() {
        this.authController = new AuthController();
        authController.setLoginFrame(this);

        UITheme.applyAppIcon(this);
        initComponents();
        customInit();
    }

    private void customInit() {
        setLocationRelativeTo(null);

        // Demo login panel
        JPanel demoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 4));
        demoPanel.setBackground(UITheme.CARD_BG);
        demoPanel.setBorder(BorderFactory.createTitledBorder("Quick Demo Logins"));

        JButton btnDemoAdmin = new JButton("Manager Demo (admin)");
        btnDemoAdmin.setFont(UITheme.FONT_SMALL);
        btnDemoAdmin.addActionListener(e -> {
            txtUsername.setText("admin");
            txtPassword.setText("admin123");
        });

        JButton btnDemoStaff = new JButton("Barista Demo (barista)");
        btnDemoStaff.setFont(UITheme.FONT_SMALL);
        btnDemoStaff.addActionListener(e -> {
            txtUsername.setText("barista");
            txtPassword.setText("barista123");
        });

        demoPanel.add(btnDemoAdmin);
        demoPanel.add(btnDemoStaff);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new java.awt.Insets(6, 6, 6, 6);
        gbc.weightx = 1.0;
        gbc.gridx = 0;
        gbc.gridy = 7;
        cardPanel.add(demoPanel, gbc);

        // Actions
        btnLogin.addActionListener(e -> onLogin());
        txtUsername.addActionListener(e -> onLogin());
        txtPassword.addActionListener(e -> onLogin());
    }

    private void onLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());
        authController.handleLogin(username, password);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        headerPanel = new javax.swing.JPanel();
        lblLogo = new javax.swing.JLabel();
        lblSub = new javax.swing.JLabel();
        cardPanel = new javax.swing.JPanel();
        lblPrompt = new javax.swing.JLabel();
        lblUser = new javax.swing.JLabel();
        txtUsername = new javax.swing.JTextField();
        lblPass = new javax.swing.JLabel();
        txtPassword = new javax.swing.JPasswordField();
        lblStatus = new javax.swing.JLabel();
        btnLogin = new javax.swing.JButton();
        footerPanel = new javax.swing.JPanel();
        lblFooter = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("BrewTrack - Coffee Shop Inventory System");
        setResizable(false);

        headerPanel.setBackground(new java.awt.Color(35, 26, 21));
        headerPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(28, 25, 22, 25));
        headerPanel.setLayout(new java.awt.BorderLayout());

        lblLogo.setFont(new java.awt.Font("Segoe UI", 1, 22)); // NOI18N
        lblLogo.setForeground(new java.awt.Color(255, 255, 255));
        lblLogo.setText("☕  BrewTrack");
        headerPanel.add(lblLogo, java.awt.BorderLayout.NORTH);

        lblSub.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        lblSub.setForeground(new java.awt.Color(214, 197, 187));
        lblSub.setText("Coffee Shop Inventory Management | ISUFST Final Project");
        headerPanel.add(lblSub, java.awt.BorderLayout.SOUTH);

        getContentPane().add(headerPanel, java.awt.BorderLayout.NORTH);

        cardPanel.setBackground(new java.awt.Color(255, 255, 255));
        cardPanel.setLayout(new java.awt.GridBagLayout());

        lblPrompt.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        lblPrompt.setForeground(new java.awt.Color(28, 19, 14));
        lblPrompt.setText("Sign in to manage cafe stocks & supplies:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 6, 6);
        cardPanel.add(lblPrompt, gridBagConstraints);

        lblUser.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        lblUser.setText("Username:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 6, 6);
        cardPanel.add(lblUser, gridBagConstraints);

        txtUsername.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        txtUsername.setText("admin");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 6, 6);
        cardPanel.add(txtUsername, gridBagConstraints);

        lblPass.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        lblPass.setText("Password:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 6, 6);
        cardPanel.add(lblPass, gridBagConstraints);

        txtPassword.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        txtPassword.setText("admin123");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 6, 6);
        cardPanel.add(txtPassword, gridBagConstraints);

        lblStatus.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        lblStatus.setForeground(new java.awt.Color(220, 38, 38));
        lblStatus.setText(" ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 6, 6);
        cardPanel.add(lblStatus, gridBagConstraints);

        btnLogin.setBackground(new java.awt.Color(92, 58, 33));
        btnLogin.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnLogin.setForeground(new java.awt.Color(255, 255, 255));
        btnLogin.setText("Sign In to Coffee System");
        btnLogin.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnLogin.setFocusable(false);
        btnLogin.setPreferredSize(new java.awt.Dimension(0, 42));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 6, 6);
        cardPanel.add(btnLogin, gridBagConstraints);

        getContentPane().add(cardPanel, java.awt.BorderLayout.CENTER);

        footerPanel.setBackground(new java.awt.Color(251, 249, 246));

        lblFooter.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        lblFooter.setForeground(new java.awt.Color(133, 107, 91));
        lblFooter.setText("ISUFST Academic Final Project - 2026");
        footerPanel.add(lblFooter);

        getContentPane().add(footerPanel, java.awt.BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ignored) {}

        java.awt.EventQueue.invokeLater(() -> new LoginFrame().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnLogin;
    private javax.swing.JPanel cardPanel;
    private javax.swing.JPanel footerPanel;
    private javax.swing.JPanel headerPanel;
    private javax.swing.JLabel lblFooter;
    private javax.swing.JLabel lblLogo;
    private javax.swing.JLabel lblPass;
    private javax.swing.JLabel lblPrompt;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JLabel lblSub;
    private javax.swing.JLabel lblUser;
    private javax.swing.JPasswordField txtPassword;
    private javax.swing.JTextField txtUsername;
    // End of variables declaration//GEN-END:variables
}
