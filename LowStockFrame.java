package com.inventory.view;

import com.inventory.controller.ProductController;
import com.inventory.controller.StockController;
import com.inventory.util.UITheme;

import java.awt.BorderLayout;

/**
 * LowStockFrame – Standalone JFrame GUI Form for Low Stock Alerts.
 */
public class LowStockFrame extends javax.swing.JFrame {

    private final StockController stockController;
    private final ProductController productController;
    private LowStockPanel lowStockPanel;

    public LowStockFrame() {
        this.stockController = new StockController();
        this.productController = new ProductController();

        UITheme.applyAppIcon(this);
        initComponents();
        customInit();
    }

    private void customInit() {
        setLocationRelativeTo(null);
        lowStockPanel = new LowStockPanel(stockController, () -> productController.loadProducts());
        contentPanel.add(lowStockPanel, BorderLayout.CENTER);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        topPanel = new javax.swing.JPanel();
        lblTitle = new javax.swing.JLabel();
        contentPanel = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Low Stock & Expiry Alerts - BrewTrack Inventory");
        setMinimumSize(new java.awt.Dimension(950, 600));
        setPreferredSize(new java.awt.Dimension(1150, 700));

        topPanel.setBackground(new java.awt.Color(251, 249, 246));
        topPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(15, 15, 10, 15));
        topPanel.setLayout(new java.awt.BorderLayout());

        lblTitle.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        lblTitle.setForeground(new java.awt.Color(220, 38, 38));
        lblTitle.setText("⚠️  Low Stock & Critical Expiry Alerts");
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

        java.awt.EventQueue.invokeLater(() -> new LowStockFrame().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel contentPanel;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JPanel topPanel;
    // End of variables declaration//GEN-END:variables
}
