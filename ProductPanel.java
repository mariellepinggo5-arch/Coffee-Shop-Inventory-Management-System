package com.inventory.view;

import com.inventory.controller.ProductController;
import com.inventory.controller.StockController;
import com.inventory.model.Category;
import com.inventory.model.Product;
import com.inventory.model.Supplier;
import com.inventory.service.CategoryService;
import com.inventory.service.SupplierService;
import com.inventory.util.UITheme;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class ProductPanel extends JPanel {

    private final ProductController productController;
    private final StockController stockController;
    private final CategoryService categoryService;
    private final SupplierService supplierService;

    private JTable productTable;
    private DefaultTableModel tableModel;
    private List<Product> currentProductList = new ArrayList<>();

    private JTextField txtSearch;
    private JComboBox<Object> cmbCategoryFilter;
    private JComboBox<Object> cmbSupplierFilter;
    private JComboBox<String> cmbStockFilter;

    public ProductPanel(ProductController productController, StockController stockController) {
        this.productController = productController;
        this.stockController = stockController;
        this.categoryService = new CategoryService();
        this.supplierService = new SupplierService();

        productController.setProductPanel(this);

        initComponents();
        loadFilterOptions();
        productController.loadProducts();
    }

    private void initComponents() {
        setLayout(new BorderLayout(0, 10));
        setBackground(UITheme.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Top Control Area
        JPanel topPanel = new JPanel(new BorderLayout(0, 8));
        topPanel.setBackground(UITheme.BACKGROUND);

        // Header Title
        JLabel lblTitle = new JLabel("Cafe Inventory & Ingredients Catalog");
        lblTitle.setFont(UITheme.FONT_HEADER);
        lblTitle.setForeground(UITheme.TEXT_MAIN);
        topPanel.add(lblTitle, BorderLayout.NORTH);

        // Filter Bar
        JPanel filterBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        filterBar.setBackground(UITheme.CARD_BG);
        filterBar.setBorder(UITheme.BORDER_CARD);

        txtSearch = new JTextField(15);
        txtSearch.setBorder(UITheme.BORDER_INPUT);
        txtSearch.setToolTipText("Search by SKU, beans, syrups, or item name");

        cmbCategoryFilter = new JComboBox<>();
        cmbSupplierFilter = new JComboBox<>();
        cmbStockFilter = new JComboBox<>(new String[]{"All Stock Levels", "IN_STOCK", "LOW_STOCK", "OUT_OF_STOCK"});

        JButton btnSearch = UITheme.createPrimaryButton("Filter Items");
        btnSearch.addActionListener(e -> onSearch());

        JButton btnReset = UITheme.createSecondaryButton("Reset Filters");
        btnReset.addActionListener(e -> onResetFilters());

        filterBar.add(new JLabel("Keyword:"));
        filterBar.add(txtSearch);
        filterBar.add(new JLabel("Category:"));
        filterBar.add(cmbCategoryFilter);
        filterBar.add(new JLabel("Supplier / Roaster:"));
        filterBar.add(cmbSupplierFilter);
        filterBar.add(new JLabel("Stock Status:"));
        filterBar.add(cmbStockFilter);
        filterBar.add(btnSearch);
        filterBar.add(btnReset);

        topPanel.add(filterBar, BorderLayout.CENTER);

        // Action Toolbar
        JPanel actionToolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        actionToolbar.setBackground(UITheme.BACKGROUND);

        JButton btnAdd = UITheme.createPrimaryButton("+ Add Cafe Item");
        btnAdd.addActionListener(e -> productController.openAddProductDialog());

        JButton btnEdit = UITheme.createSecondaryButton("Edit Item");
        btnEdit.addActionListener(e -> productController.openEditProductDialog(getSelectedProduct()));

        JButton btnDelete = UITheme.createDangerButton("Delete Item");
        btnDelete.addActionListener(e -> productController.handleDeleteProduct(getSelectedProduct()));

        JButton btnRestock = UITheme.createSuccessButton("Restock Delivery (Inbound)");
        btnRestock.addActionListener(e -> stockController.openStockDialog(
            this, getSelectedProduct(), "INBOUND", () -> productController.loadProducts()
        ));

        JButton btnDispatch = UITheme.createAccentButton("Bar Usage / Dispense (Outbound)");
        btnDispatch.addActionListener(e -> stockController.openStockDialog(
            this, getSelectedProduct(), "OUTBOUND", () -> productController.loadProducts()
        ));

        JButton btnRefresh = UITheme.createSecondaryButton("Refresh Catalog");
        btnRefresh.addActionListener(e -> productController.loadProducts());

        actionToolbar.add(btnAdd);
        actionToolbar.add(btnEdit);
        actionToolbar.add(btnDelete);
        actionToolbar.add(Box.createHorizontalStrut(10));
        actionToolbar.add(btnRestock);
        actionToolbar.add(btnDispatch);
        actionToolbar.add(Box.createHorizontalStrut(10));
        actionToolbar.add(btnRefresh);

        topPanel.add(actionToolbar, BorderLayout.SOUTH);
        add(topPanel, BorderLayout.NORTH);

        // Table
        String[] columns = {
            "ID", "SKU / Code", "Ingredient / Item Name", "Category", "Supplier / Roaster",
            "Cost (PHP)", "Retail / Value (PHP)", "Stock On Hand", "Reorder Level", "Unit", "Stock Status"
        };

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };

        productTable = new JTable(tableModel);
        UITheme.styleTable(productTable);

        // Custom Cell Renderer for Stock Status
        productTable.getColumnModel().getColumn(10).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val, boolean isSel, boolean hasFocus, int r, int c) {
                Component comp = super.getTableCellRendererComponent(t, val, isSel, hasFocus, r, c);
                setHorizontalAlignment(JLabel.CENTER);
                String text = (val != null) ? val.toString() : "";
                if ("OUT OF STOCK".equalsIgnoreCase(text)) {
                    setForeground(UITheme.DANGER);
                    setFont(UITheme.FONT_BOLD);
                } else if ("LOW STOCK".equalsIgnoreCase(text)) {
                    setForeground(UITheme.WARNING);
                    setFont(UITheme.FONT_BOLD);
                } else {
                    setForeground(UITheme.SUCCESS);
                    setFont(UITheme.FONT_REGULAR);
                }
                return comp;
            }
        });

        JScrollPane scrollPane = new JScrollPane(productTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(UITheme.BORDER_COLOR));
        add(scrollPane, BorderLayout.CENTER);
    }

    public void displayProducts(List<Product> products) {
        this.currentProductList = products;
        tableModel.setRowCount(0);

        for (Product p : products) {
            String stockStatus;
            if (p.getQuantityInStock() == 0) {
                stockStatus = "OUT OF STOCK";
            } else if (p.isLowStock()) {
                stockStatus = "LOW STOCK";
            } else {
                stockStatus = "IN STOCK";
            }

            tableModel.addRow(new Object[]{
                p.getProductId(),
                p.getSku(),
                p.getProductName(),
                p.getCategoryName(),
                p.getSupplierName(),
                String.format("%.2f", p.getCostPrice()),
                String.format("%.2f", p.getUnitPrice()),
                p.getQuantityInStock(),
                p.getReorderLevel(),
                p.getUnitOfMeasure(),
                stockStatus
            });
        }
    }

    public Product getSelectedProduct() {
        int selectedRow = productTable.getSelectedRow();
        if (selectedRow >= 0 && selectedRow < currentProductList.size()) {
            return currentProductList.get(selectedRow);
        }
        return null;
    }

    private void loadFilterOptions() {
        cmbCategoryFilter.removeAllItems();
        cmbCategoryFilter.addItem("All Categories");
        try {
            for (Category c : categoryService.getAllCategories()) {
                cmbCategoryFilter.addItem(c);
            }
        } catch (SQLException ignored) {}

        cmbSupplierFilter.removeAllItems();
        cmbSupplierFilter.addItem("All Suppliers / Roasters");
        try {
            for (Supplier s : supplierService.getAllSuppliers()) {
                cmbSupplierFilter.addItem(s);
            }
        } catch (SQLException ignored) {}
    }

    private void onSearch() {
        String keyword = txtSearch.getText().trim();

        Integer catId = null;
        Object catObj = cmbCategoryFilter.getSelectedItem();
        if (catObj instanceof Category) {
            catId = ((Category) catObj).getCategoryId();
        }

        Integer supId = null;
        Object supObj = cmbSupplierFilter.getSelectedItem();
        if (supObj instanceof Supplier) {
            supId = ((Supplier) supObj).getSupplierId();
        }

        String stockFilter = (String) cmbStockFilter.getSelectedItem();
        if ("All Stock Levels".equals(stockFilter)) stockFilter = null;

        productController.searchProducts(keyword, catId, supId, stockFilter);
    }

    private void onResetFilters() {
        txtSearch.setText("");
        cmbCategoryFilter.setSelectedIndex(0);
        cmbSupplierFilter.setSelectedIndex(0);
        cmbStockFilter.setSelectedIndex(0);
        productController.loadProducts();
    }
}
