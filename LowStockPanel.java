package com.inventory.view;

import com.inventory.controller.StockController;
import com.inventory.model.Product;
import com.inventory.service.ProductService;
import com.inventory.util.UITheme;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class LowStockPanel extends JPanel {

    private final StockController stockController;
    private final ProductService productService;
    private JTable alertTable;
    private DefaultTableModel tableModel;
    private List<Product> lowStockProducts = new ArrayList<>();
    private final Runnable onDataUpdatedCallback;

    public LowStockPanel(StockController stockController, Runnable onDataUpdatedCallback) {
        this.stockController = stockController;
        this.productService = new ProductService();
        this.onDataUpdatedCallback = onDataUpdatedCallback;

        initComponents();
        loadLowStock();
    }

    private void initComponents() {
        setLayout(new BorderLayout(0, 12));
        setBackground(UITheme.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Header & Toolbar
        JPanel topPanel = new JPanel(new BorderLayout(0, 8));
        topPanel.setBackground(UITheme.BACKGROUND);

        JLabel lblTitle = new JLabel("Urgent Low Stock & Reorder Alerts (Cafe Supplies)");
        lblTitle.setFont(UITheme.FONT_HEADER);
        lblTitle.setForeground(new Color(185, 28, 28)); // Cafe Warning Red
        topPanel.add(lblTitle, BorderLayout.NORTH);

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        toolbar.setBackground(UITheme.BACKGROUND);

        JButton btnRestock = UITheme.createSuccessButton("Order Restock for Selected Item");
        btnRestock.addActionListener(e -> onRestockSelected());

        JButton btnRefresh = UITheme.createSecondaryButton("Refresh Alert Status");
        btnRefresh.addActionListener(e -> loadLowStock());

        toolbar.add(btnRestock);
        toolbar.add(btnRefresh);

        topPanel.add(toolbar, BorderLayout.SOUTH);
        add(topPanel, BorderLayout.NORTH);

        // Table
        String[] columns = {
            "SKU", "Ingredient / Supply Name", "Category", "Supplier / Roaster",
            "Current Stock", "Reorder Threshold", "Deficit", "Unit of Measure"
        };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        alertTable = new JTable(tableModel);
        UITheme.styleTable(alertTable);

        // Highlight Current Stock in bold red
        alertTable.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val, boolean isSel, boolean hasFocus, int r, int c) {
                Component comp = super.getTableCellRendererComponent(t, val, isSel, hasFocus, r, c);
                setHorizontalAlignment(JLabel.CENTER);
                setForeground(UITheme.DANGER);
                setFont(UITheme.FONT_BOLD);
                return comp;
            }
        });

        JScrollPane scrollPane = new JScrollPane(alertTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(UITheme.BORDER_COLOR));
        add(scrollPane, BorderLayout.CENTER);
    }

    public void loadLowStock() {
        try {
            lowStockProducts = productService.getLowStockProducts();
            tableModel.setRowCount(0);
            for (Product p : lowStockProducts) {
                int deficit = Math.max(0, p.getReorderLevel() - p.getQuantityInStock());
                tableModel.addRow(new Object[]{
                    p.getSku(),
                    p.getProductName(),
                    p.getCategoryName(),
                    p.getSupplierName(),
                    p.getQuantityInStock(),
                    p.getReorderLevel(),
                    deficit,
                    p.getUnitOfMeasure()
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error fetching low stock items: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onRestockSelected() {
        int row = alertTable.getSelectedRow();
        if (row < 0 || row >= lowStockProducts.size()) {
            JOptionPane.showMessageDialog(this, "Please select an ingredient or supply from the table to restock.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Product selected = lowStockProducts.get(row);
        stockController.openStockDialog(this, selected, "INBOUND", () -> {
            loadLowStock();
            if (onDataUpdatedCallback != null) {
                onDataUpdatedCallback.run();
            }
        });
    }
}
