package com.inventory.view;

import com.inventory.controller.SupplierController;
import com.inventory.model.Supplier;
import com.inventory.util.UITheme;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class SupplierPanel extends JPanel {

    private final SupplierController supplierController;
    private JTable supplierTable;
    private DefaultTableModel tableModel;
    private List<Supplier> supplierList = new ArrayList<>();

    public SupplierPanel(SupplierController supplierController) {
        this.supplierController = supplierController;
        supplierController.setSupplierPanel(this);

        initComponents();
        supplierController.loadSuppliers();
    }

    private void initComponents() {
        setLayout(new BorderLayout(0, 12));
        setBackground(UITheme.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Header & Toolbar
        JPanel topPanel = new JPanel(new BorderLayout(0, 8));
        topPanel.setBackground(UITheme.BACKGROUND);

        JLabel lblTitle = new JLabel("Coffee Roasters & Cafe Suppliers Directory");
        lblTitle.setFont(UITheme.FONT_HEADER);
        lblTitle.setForeground(UITheme.TEXT_MAIN);
        topPanel.add(lblTitle, BorderLayout.NORTH);

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        toolbar.setBackground(UITheme.BACKGROUND);

        JButton btnAdd = UITheme.createPrimaryButton("+ Add Roaster / Vendor");
        btnAdd.addActionListener(e -> supplierController.openAddSupplierDialog());

        JButton btnEdit = UITheme.createSecondaryButton("Edit Roaster");
        btnEdit.addActionListener(e -> supplierController.openEditSupplierDialog(getSelectedSupplier()));

        JButton btnDelete = UITheme.createDangerButton("Delete Vendor");
        btnDelete.addActionListener(e -> supplierController.handleDeleteSupplier(getSelectedSupplier()));

        JButton btnRefresh = UITheme.createSecondaryButton("Refresh Directory");
        btnRefresh.addActionListener(e -> supplierController.loadSuppliers());

        toolbar.add(btnAdd);
        toolbar.add(btnEdit);
        toolbar.add(btnDelete);
        toolbar.add(btnRefresh);

        topPanel.add(toolbar, BorderLayout.SOUTH);
        add(topPanel, BorderLayout.NORTH);

        // Table
        String[] columns = {"ID", "Company / Roastery Name", "Representative", "Email", "Phone", "Location", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        supplierTable = new JTable(tableModel);
        UITheme.styleTable(supplierTable);

        JScrollPane scrollPane = new JScrollPane(supplierTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(UITheme.BORDER_COLOR));
        add(scrollPane, BorderLayout.CENTER);
    }

    public void displaySuppliers(List<Supplier> suppliers) {
        this.supplierList = suppliers;
        tableModel.setRowCount(0);
        for (Supplier s : suppliers) {
            tableModel.addRow(new Object[]{
                s.getSupplierId(),
                s.getSupplierName(),
                s.getContactPerson(),
                s.getEmail(),
                s.getPhone(),
                s.getAddress(),
                s.getStatus()
            });
        }
    }

    public Supplier getSelectedSupplier() {
        int row = supplierTable.getSelectedRow();
        if (row >= 0 && row < supplierList.size()) {
            return supplierList.get(row);
        }
        return null;
    }
}
