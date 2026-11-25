package com.inventory.view;

import com.inventory.controller.SupplierController;
import com.inventory.util.UITheme;

import java.awt.BorderLayout;

/**
 * SupplierFrame – Standalone JFrame GUI Form for Roasters & Suppliers.
 */
public class SupplierFrame extends javax.swing.JFrame {

    private final SupplierController supplierController;
    private SupplierPanel supplierPanel;

    public SupplierFrame() {
        this.supplierController = new SupplierController();

        UITheme.applyAppIcon(this);
        initComponents();
        customInit();
    }

    private void customInit() {
        setLocationRelativeTo(null);
        supplierPanel = new SupplierPanel(supplierController);
        contentPanel.add(supplierPanel, BorderLayout.CENTER);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        topPanel = new javax.swing.JPanel();
        lblTitle = new javax.swing.JLabel();
        contentPanel = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Roasters & Suppliers - BrewTrack Inventory");
        setMinimumSize(new java.awt.Dimension(950, 600));
        setPreferredSize(new java.awt.Dimension(1150, 700));

        topPanel.setBackground(new java.awt.Color(251, 249, 246));
        topPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(15, 15, 10, 15));
        topPanel.setLayout(new java.awt.BorderLayout());

        lblTitle.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        lblTitle.setForeground(new java.awt.Color(92, 58, 33));
        lblTitle.setText("🌍  Roasters & Partner Suppliers");
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

        java.awt.EventQueue.invokeLater(() -> new SupplierFrame().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel contentPanel;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JPanel topPanel;
    // End of variables declaration//GEN-END:variables
}
