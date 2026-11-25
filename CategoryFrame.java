package com.inventory.view;

import com.inventory.controller.CategoryController;
import com.inventory.util.UITheme;

import java.awt.BorderLayout;


public class CategoryFrame extends javax.swing.JFrame {

    private final CategoryController categoryController;
    private CategoryPanel categoryPanel;

    public CategoryFrame() {
        this.categoryController = new CategoryController();

        UITheme.applyAppIcon(this);
        initComponents();
        customInit();
    }

    private void customInit() {
        setLocationRelativeTo(null);
        categoryPanel = new CategoryPanel(categoryController);
        contentPanel.add(categoryPanel, BorderLayout.CENTER);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        topPanel = new javax.swing.JPanel();
        lblTitle = new javax.swing.JLabel();
        contentPanel = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Cafe Categories - BrewTrack Inventory");
        setMinimumSize(new java.awt.Dimension(900, 600));
        setPreferredSize(new java.awt.Dimension(1100, 700));

        topPanel.setBackground(new java.awt.Color(251, 249, 246));
        topPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(15, 15, 10, 15));
        topPanel.setLayout(new java.awt.BorderLayout());

        lblTitle.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        lblTitle.setForeground(new java.awt.Color(92, 58, 33));
        lblTitle.setText("🏷️  Cafe Categories Management");
        topPanel.add(lblTitle, java.awt.BorderLayout.NORTH);

        getContentPane().add(topPanel, java.awt.BorderLayout.NORTH);

        contentPanel.setBackground(new java.awt.Color(251, 249, 246));
        contentPanel.setLayout(new java.awt.BorderLayout());
        getContentPane().add(contentPanel, java.awt.BorderLayout.CENTER);

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

        java.awt.EventQueue.invokeLater(() -> new CategoryFrame().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel contentPanel;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JPanel topPanel;
    // End of variables declaration//GEN-END:variables
}
