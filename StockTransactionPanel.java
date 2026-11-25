package com.inventory.view;

import com.inventory.controller.StockController;
import com.inventory.model.StockTransaction;
import com.inventory.util.UITheme;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class StockTransactionPanel extends JPanel {

    private final StockController stockController;
    private JTable transTable;
    private DefaultTableModel tableModel;
    private List<StockTransaction> allTransactions = new ArrayList<>();
    private JComboBox<String> cmbTypeFilter;

    public StockTransactionPanel(StockController stockController) {
        this.stockController = stockController;
        stockController.setTransactionPanel(this);

        initComponents();
        stockController.loadTransactions();
    }

    private void initComponents() {
        setLayout(new BorderLayout(0, 12));
        setBackground(UITheme.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Header & Toolbar
        JPanel topPanel = new JPanel(new BorderLayout(0, 8));
        topPanel.setBackground(UITheme.BACKGROUND);

        JLabel lblTitle = new JLabel("Delivery Receipts & Bar Consumption Audit Trail");
        lblTitle.setFont(UITheme.FONT_HEADER);
        lblTitle.setForeground(UITheme.TEXT_MAIN);
        topPanel.add(lblTitle, BorderLayout.NORTH);

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        toolbar.setBackground(UITheme.BACKGROUND);

        cmbTypeFilter = new JComboBox<>(new String[]{"All Transaction Types", "INBOUND", "OUTBOUND", "ADJUSTMENT"});
        cmbTypeFilter.addActionListener(e -> applyFilter());

        JButton btnRefresh = UITheme.createSecondaryButton("Refresh Log Records");
        btnRefresh.addActionListener(e -> stockController.loadTransactions());

        toolbar.add(new JLabel("Filter Log Type:"));
        toolbar.add(cmbTypeFilter);
        toolbar.add(btnRefresh);

        topPanel.add(toolbar, BorderLayout.SOUTH);
        add(topPanel, BorderLayout.NORTH);

        // Table
        String[] columns = {
            "Trans ID", "Timestamp", "SKU / Code", "Ingredient / Supply", "Movement Type",
            "Quantity", "Ref / Invoice No.", "Logged By Barista", "Audit Notes"
        };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        transTable = new JTable(tableModel);
        UITheme.styleTable(transTable);

        // Custom renderer for Movement Type
        transTable.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val, boolean isSel, boolean hasFocus, int r, int c) {
                Component comp = super.getTableCellRendererComponent(t, val, isSel, hasFocus, r, c);
                setHorizontalAlignment(JLabel.CENTER);
                String text = (val != null) ? val.toString() : "";
                if ("INBOUND".equalsIgnoreCase(text)) {
                    setForeground(UITheme.SUCCESS); // Delivery
                    setFont(UITheme.FONT_BOLD);
                } else if ("OUTBOUND".equalsIgnoreCase(text)) {
                    setForeground(UITheme.DANGER); // Bar use
                    setFont(UITheme.FONT_BOLD);
                } else {
                    setForeground(UITheme.WARNING); // Spoilage / Adj
                    setFont(UITheme.FONT_BOLD);
                }
                return comp;
            }
        });

        JScrollPane scrollPane = new JScrollPane(transTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(UITheme.BORDER_COLOR));
        add(scrollPane, BorderLayout.CENTER);
    }

    public void displayTransactions(List<StockTransaction> transactions) {
        this.allTransactions = transactions;
        applyFilter();
    }

    private void applyFilter() {
        String filter = (String) cmbTypeFilter.getSelectedItem();
        tableModel.setRowCount(0);

        for (StockTransaction t : allTransactions) {
            if ("All Transaction Types".equals(filter) || t.getTransactionType().equalsIgnoreCase(filter)) {
                tableModel.addRow(new Object[]{
                    t.getTransactionId(),
                    t.getTransactionDate() != null ? t.getTransactionDate().toString() : "",
                    t.getSku(),
                    t.getProductName(),
                    t.getTransactionType(),
                    t.getQuantity(),
                    t.getReferenceNo(),
                    t.getPerformedByName(),
                    t.getRemarks()
                });
            }
        }
    }
}
