package com.inventory.view;

import com.inventory.controller.StockController;
import com.inventory.dao.DBConnection;
import com.inventory.model.Category;
import com.inventory.model.Product;
import com.inventory.model.Supplier;
import com.inventory.model.User;
import com.inventory.service.CategoryService;
import com.inventory.service.ProductService;
import com.inventory.service.SupplierService;
import com.inventory.util.UITheme;
import com.inventory.view.dialog.ProductDialog;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * DashboardFrame – Standalone Coffee Shop Inventory Design JFrame.
 * Displays live data from MySQL Workbench (coffeeshop_db).
 * Uses custom artisan coffee icon to ensure Java's default Duke mascot never pops up.
 */
public class DashboardFrame extends JFrame {

    private final User currentUser;
    private final ProductService productService;
    private final CategoryService categoryService;
    private final SupplierService supplierService;

    // KPI value labels
    private JLabel lblTotalProducts;
    private JLabel lblTotalValue;
    private JLabel lblInStockCount;
    private JLabel lblLowStockCount;

    // Live tables & views
    private DefaultTableModel productsTableModel;
    private JTable productsTable;
    private JPanel alertsContainer;
    private JPanel transactionsContainer;
    private JPanel categoriesContainer;
    private JPanel suppliersContainer;
    private WeeklyUsageChart weeklyChart;

    public DashboardFrame() {
        this(new User(1, "admin", "admin123", "Marielle", "ADMIN", "ACTIVE"));
    }

    public DashboardFrame(User user) {
        this.currentUser = (user != null) ? user : new User(1, "admin", "admin123", "Marielle", "ADMIN", "ACTIVE");
        this.productService = new ProductService();
        this.categoryService = new CategoryService();
        this.supplierService = new SupplierService();

        // Apply custom coffee icon to eliminate Duke mascot from titlebar & taskbar
        UITheme.applyAppIcon(this);

        initUI();
        loadDashboardData();
    }

    private void initUI() {
        setTitle("BrewTrack – Coffee Shop Inventory Management System (ISUFST)");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1380, 880);
        setMinimumSize(new Dimension(1100, 720));
        setLocationRelativeTo(null);
        getContentPane().setBackground(UITheme.BACKGROUND);
        setLayout(new BorderLayout());

        // Left Sidebar
        add(buildSidebar(), BorderLayout.WEST);

        // Center Content with Topbar
        JPanel centerWrapper = new JPanel(new BorderLayout());
        centerWrapper.setBackground(UITheme.BACKGROUND);
        centerWrapper.add(buildTopbar(), BorderLayout.NORTH);
        centerWrapper.add(buildMainScrollableContent(), BorderLayout.CENTER);

        add(centerWrapper, BorderLayout.CENTER);
    }

    // ══════════════════════════════════════════════════════════════
    // TOPBAR
    // ══════════════════════════════════════════════════════════════
    private JPanel buildTopbar() {
        JPanel topbar = new JPanel(new BorderLayout());
        topbar.setBackground(Color.WHITE);
        topbar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, UITheme.BORDER_COLOR),
            new EmptyBorder(14, 28, 14, 28)
        ));

        // Left title
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);

        JLabel lblTitle = new JLabel("☕  Dashboard Overview");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(UITheme.PRIMARY);

        JLabel lblSub = new JLabel("Live Artisan Inventory  ·  Connected to MySQL Workbench (coffeeshop_db)");
        lblSub.setFont(UITheme.FONT_SMALL);
        lblSub.setForeground(UITheme.TEXT_MUTED);

        titlePanel.add(lblTitle);
        titlePanel.add(Box.createVerticalStrut(2));
        titlePanel.add(lblSub);

        // Right actions
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actions.setOpaque(false);

        JButton btnAddProduct = UITheme.createPrimaryButton("＋ Add Cafe Item");
        btnAddProduct.addActionListener(e -> onAddProduct());

        JButton btnRestock = UITheme.createSuccessButton("📦 Restock Delivery");
        btnRestock.addActionListener(e -> onRestockDelivery());

        JButton btnDispense = UITheme.createAccentButton("🫗 Bar Dispense");
        btnDispense.addActionListener(e -> onBarDispense());

        JButton btnRefresh = UITheme.createSecondaryButton("↻ Refresh");
        btnRefresh.addActionListener(e -> loadDashboardData());

        actions.add(btnAddProduct);
        actions.add(btnRestock);
        actions.add(btnDispense);
        actions.add(btnRefresh);

        topbar.add(titlePanel, BorderLayout.WEST);
        topbar.add(actions, BorderLayout.EAST);
        return topbar;
    }

    // ══════════════════════════════════════════════════════════════
    // SIDEBAR
    // ══════════════════════════════════════════════════════════════
    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setBackground(UITheme.SIDEBAR_BG);
        sidebar.setPreferredSize(new Dimension(256, 0));

        // Brand
        JPanel brand = new JPanel();
        brand.setLayout(new BoxLayout(brand, BoxLayout.Y_AXIS));
        brand.setBackground(UITheme.SIDEBAR_BG);
        brand.setBorder(new EmptyBorder(26, 22, 18, 22));

        JLabel logo = new JLabel("☕  BrewTrack");
        logo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        logo.setForeground(Color.WHITE);

        JLabel tag = new JLabel("Coffee Shop Inventory");
        tag.setFont(UITheme.FONT_SMALL);
        tag.setForeground(new Color(200, 175, 148));

        brand.add(logo);
        brand.add(Box.createVerticalStrut(4));
        brand.add(tag);
        brand.add(Box.createVerticalStrut(14));

        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(60, 45, 35));
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        brand.add(sep);

        sidebar.add(brand, BorderLayout.NORTH);

        // Navigation
        JPanel nav = new JPanel();
        nav.setLayout(new BoxLayout(nav, BoxLayout.Y_AXIS));
        nav.setBackground(UITheme.SIDEBAR_BG);
        nav.setBorder(new EmptyBorder(10, 12, 10, 12));

        nav.add(makeNavSection("MAIN"));
        nav.add(createNavBtn("📊  Dashboard", true, () -> {}));
        nav.add(Box.createVerticalStrut(4));
        nav.add(createNavBtn("☕  Beans & Ingredients", false, this::openFullManagementConsole));

        nav.add(Box.createVerticalStrut(14));
        nav.add(makeNavSection("OPERATIONS"));
        nav.add(createNavBtn("⚠️  Low Stock Alerts", false, this::openFullManagementConsole));
        nav.add(Box.createVerticalStrut(4));
        nav.add(createNavBtn("🏷️  Cafe Categories", false, this::openFullManagementConsole));
        nav.add(Box.createVerticalStrut(4));
        nav.add(createNavBtn("🌍  Roasters & Suppliers", false, this::openFullManagementConsole));
        nav.add(Box.createVerticalStrut(4));
        nav.add(createNavBtn("📦  Delivery & Bar Logs", false, this::openFullManagementConsole));

        sidebar.add(nav, BorderLayout.CENTER);

        // User Footer
        JPanel userFooter = new JPanel();
        userFooter.setLayout(new BoxLayout(userFooter, BoxLayout.Y_AXIS));
        userFooter.setBackground(new Color(24, 16, 10));
        userFooter.setBorder(new EmptyBorder(14, 16, 16, 16));

        JLabel avatar = new JLabel(String.valueOf(currentUser.getFullName().charAt(0)).toUpperCase());
        avatar.setFont(new Font("Segoe UI", Font.BOLD, 16));
        avatar.setForeground(Color.WHITE);
        avatar.setOpaque(true);
        avatar.setBackground(UITheme.ACCENT);
        avatar.setPreferredSize(new Dimension(34, 34));
        avatar.setMaximumSize(new Dimension(34, 34));
        avatar.setHorizontalAlignment(SwingConstants.CENTER);
        avatar.setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 0));

        JLabel name = new JLabel(currentUser.getFullName());
        name.setFont(UITheme.FONT_BOLD);
        name.setForeground(Color.WHITE);

        JLabel role = new JLabel("Role: " + currentUser.getRole() + " · Cafe Manager");
        role.setFont(UITheme.FONT_SMALL);
        role.setForeground(new Color(190, 170, 150));

        JButton btnSwitch = UITheme.createDangerButton("⏻  Sign Out / Switch");
        btnSwitch.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnSwitch.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        btnSwitch.addActionListener(e -> {
            dispose();
            LoginFrame login = new LoginFrame();
            login.setVisible(true);
        });

        userFooter.add(avatar);
        userFooter.add(Box.createVerticalStrut(6));
        userFooter.add(name);
        userFooter.add(Box.createVerticalStrut(2));
        userFooter.add(role);
        userFooter.add(Box.createVerticalStrut(10));
        userFooter.add(btnSwitch);

        sidebar.add(userFooter, BorderLayout.SOUTH);
        return sidebar;
    }

    private JLabel makeNavSection(String label) {
        JLabel l = new JLabel(label);
        l.setFont(new Font("Segoe UI", Font.BOLD, 10));
        l.setForeground(new Color(140, 120, 105));
        l.setBorder(new EmptyBorder(4, 6, 4, 0));
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private JButton createNavBtn(String text, boolean active, Runnable onClick) {
        JButton btn = new JButton(text);
        btn.setFont(active ? new Font("Segoe UI", Font.BOLD, 13) : new Font("Segoe UI", Font.PLAIN, 13));
        btn.setForeground(active ? Color.WHITE : UITheme.SIDEBAR_TEXT);
        btn.setBackground(active ? UITheme.PRIMARY : UITheme.SIDEBAR_BG);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(true);
        btn.setBorder(new EmptyBorder(9, 14, 9, 14));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                if (!active) btn.setBackground(UITheme.SIDEBAR_HOVER);
            }
            public void mouseExited(MouseEvent e) {
                if (!active) btn.setBackground(UITheme.SIDEBAR_BG);
            }
        });

        btn.addActionListener(e -> onClick.run());
        return btn;
    }

    // ══════════════════════════════════════════════════════════════
    // MAIN SCROLLABLE CONTENT
    // ══════════════════════════════════════════════════════════════
    private JScrollPane buildMainScrollableContent() {
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setBackground(UITheme.BACKGROUND);
        container.setBorder(new EmptyBorder(22, 28, 26, 28));

        // 1. KPI Cards Row
        container.add(buildKpiRow());
        container.add(Box.createVerticalStrut(20));

        // 2. Middle Row: Table (Left) + Alerts & Recent Activity (Right)
        container.add(buildMiddleRow());
        container.add(Box.createVerticalStrut(20));

        // 3. Bottom Row: Category Breakdown + Bar Chart + Roasters
        container.add(buildBottomRow());
        container.add(Box.createVerticalStrut(10));

        JScrollPane scroll = new JScrollPane(container,
            JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        return scroll;
    }

    // ── 1. KPI CARDS ──────────────────────────────────────────────
    private JPanel buildKpiRow() {
        JPanel row = new JPanel(new GridLayout(1, 4, 16, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 116));

        lblTotalProducts = new JLabel("—");
        lblTotalValue = new JLabel("—");
        lblInStockCount = new JLabel("—");
        lblLowStockCount = new JLabel("—");

        row.add(createKpiCard("TOTAL PRODUCTS", lblTotalProducts, "Items in catalog", UITheme.PRIMARY, "☕"));
        row.add(createKpiCard("INVENTORY VALUATION", lblTotalValue, "Current stock value", UITheme.ACCENT, "💰"));
        row.add(createKpiCard("IN-STOCK ITEMS", lblInStockCount, "Healthy inventory levels", UITheme.SUCCESS, "✅"));
        row.add(createKpiCard("LOW / CRITICAL STOCK", lblLowStockCount, "Items needing reorder", UITheme.DANGER, "⚠️"));

        return row;
    }

    private JPanel createKpiCard(String title, JLabel valLabel, String sub, Color accent, String iconEmoji) {
        JPanel card = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(accent);
                g.fillRect(0, 0, getWidth(), 4);
            }
        };
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UITheme.BORDER_COLOR, 1, true),
            new EmptyBorder(16, 18, 14, 18)
        ));

        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setOpaque(false);

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblTitle.setForeground(UITheme.TEXT_MUTED);

        valLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        valLabel.setForeground(accent);

        JLabel lblSub = new JLabel(sub);
        lblSub.setFont(UITheme.FONT_SMALL);
        lblSub.setForeground(UITheme.TEXT_MUTED);

        left.add(lblTitle);
        left.add(Box.createVerticalStrut(4));
        left.add(valLabel);
        left.add(Box.createVerticalStrut(3));
        left.add(lblSub);

        JLabel icon = new JLabel(iconEmoji);
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 32));
        icon.setForeground(new Color(220, 210, 200));

        card.add(left, BorderLayout.CENTER);
        card.add(icon, BorderLayout.EAST);
        return card;
    }

    // ── 2. MIDDLE ROW ─────────────────────────────────────────────
    private JPanel buildMiddleRow() {
        JPanel row = new JPanel(new GridBagLayout());
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 380));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;

        // Left Table: Beans & Ingredients (65% width)
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.65;
        gbc.insets = new Insets(0, 0, 0, 16);
        row.add(buildProductsTableCard(), gbc);

        // Right Panel: Stock Alerts & Activity (35% width)
        gbc.gridx = 1;
        gbc.weightx = 0.35;
        gbc.insets = new Insets(0, 0, 0, 0);
        row.add(buildAlertsAndActivityCard(), gbc);

        return row;
    }

    private JPanel buildProductsTableCard() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createLineBorder(UITheme.BORDER_COLOR, 1, true));

        // Header
        JPanel head = new JPanel(new BorderLayout());
        head.setBackground(new Color(248, 245, 240));
        head.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, UITheme.BORDER_COLOR),
            new EmptyBorder(12, 16, 12, 16)
        ));

        JLabel title = new JLabel("☕  Beans & Cafe Ingredients");
        title.setFont(new Font("Segoe UI", Font.BOLD, 14));
        title.setForeground(UITheme.PRIMARY);

        JLabel sub = new JLabel("Real-time stock levels with visual threshold bars");
        sub.setFont(UITheme.FONT_SMALL);
        sub.setForeground(UITheme.TEXT_MUTED);

        JPanel text = new JPanel();
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        text.setOpaque(false);
        text.add(title);
        text.add(sub);

        head.add(text, BorderLayout.WEST);
        card.add(head, BorderLayout.NORTH);

        // Table
        String[] cols = {"Product Name", "SKU", "Category", "Stock Level", "Price", "Status"};
        productsTableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        productsTable = new JTable(productsTableModel);
        UITheme.styleTable(productsTable);

        // Visual progress bar renderer for Stock Level column (col 3)
        productsTable.getColumnModel().getColumn(3).setCellRenderer(new StockBarCellRenderer());
        // Status badge renderer (col 5)
        productsTable.getColumnModel().getColumn(5).setCellRenderer(new StatusBadgeCellRenderer());

        productsTable.getColumnModel().getColumn(0).setPreferredWidth(210);
        productsTable.getColumnModel().getColumn(1).setPreferredWidth(75);
        productsTable.getColumnModel().getColumn(2).setPreferredWidth(110);
        productsTable.getColumnModel().getColumn(3).setPreferredWidth(130);
        productsTable.getColumnModel().getColumn(4).setPreferredWidth(80);
        productsTable.getColumnModel().getColumn(5).setPreferredWidth(95);

        JScrollPane scroll = new JScrollPane(productsTable);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(Color.WHITE);
        card.add(scroll, BorderLayout.CENTER);

        return card;
    }

    private JPanel buildAlertsAndActivityCard() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createLineBorder(UITheme.BORDER_COLOR, 1, true));

        // Header
        JPanel head = new JPanel(new BorderLayout());
        head.setBackground(new Color(248, 245, 240));
        head.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, UITheme.BORDER_COLOR),
            new EmptyBorder(12, 16, 12, 16)
        ));

        JLabel title = new JLabel("⚠️  Stock Alerts & Bar Activity");
        title.setFont(new Font("Segoe UI", Font.BOLD, 14));
        title.setForeground(UITheme.PRIMARY);
        head.add(title, BorderLayout.WEST);
        card.add(head, BorderLayout.NORTH);

        // Body with two sections: Alerts (top) and Recent Activity (bottom)
        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBackground(Color.WHITE);
        body.setBorder(new EmptyBorder(12, 16, 12, 16));

        // Alerts section
        JLabel lblAlertsHeader = new JLabel("URGENT ATTENTION NEEDED");
        lblAlertsHeader.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblAlertsHeader.setForeground(UITheme.DANGER);
        body.add(lblAlertsHeader);
        body.add(Box.createVerticalStrut(8));

        alertsContainer = new JPanel();
        alertsContainer.setLayout(new BoxLayout(alertsContainer, BoxLayout.Y_AXIS));
        alertsContainer.setOpaque(false);
        body.add(alertsContainer);

        body.add(Box.createVerticalStrut(16));
        JSeparator sep = new JSeparator();
        sep.setForeground(UITheme.BORDER_COLOR);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        body.add(sep);
        body.add(Box.createVerticalStrut(14));

        // Recent activity section
        JLabel lblActivityHeader = new JLabel("RECENT BAR LOGS");
        lblActivityHeader.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblActivityHeader.setForeground(UITheme.TEXT_MUTED);
        body.add(lblActivityHeader);
        body.add(Box.createVerticalStrut(8));

        transactionsContainer = new JPanel();
        transactionsContainer.setLayout(new BoxLayout(transactionsContainer, BoxLayout.Y_AXIS));
        transactionsContainer.setOpaque(false);
        body.add(transactionsContainer);

        JScrollPane scroll = new JScrollPane(body);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(Color.WHITE);
        card.add(scroll, BorderLayout.CENTER);

        return card;
    }

    // ── 3. BOTTOM ROW ─────────────────────────────────────────────
    private JPanel buildBottomRow() {
        JPanel row = new JPanel(new GridLayout(1, 3, 16, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 260));

        // Card 1: By Category
        row.add(buildCategoryCard());

        // Card 2: Weekly Bar Usage Chart
        weeklyChart = new WeeklyUsageChart();
        JPanel chartCard = createSectionCard("📉  Weekly Bar Usage (Units Dispensed / Received)", weeklyChart);
        row.add(chartCard);

        // Card 3: Top Roasters & Suppliers
        row.add(buildSuppliersCard());

        return row;
    }

    private JPanel buildCategoryCard() {
        JPanel card = createSectionCard("📂  Catalog By Category", null);
        categoriesContainer = new JPanel();
        categoriesContainer.setLayout(new BoxLayout(categoriesContainer, BoxLayout.Y_AXIS));
        categoriesContainer.setBackground(Color.WHITE);
        categoriesContainer.setBorder(new EmptyBorder(10, 14, 10, 14));

        JScrollPane scroll = new JScrollPane(categoriesContainer);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(Color.WHITE);
        card.add(scroll, BorderLayout.CENTER);
        return card;
    }

    private JPanel buildSuppliersCard() {
        JPanel card = createSectionCard("🌍  Roasters & Partner Suppliers", null);
        suppliersContainer = new JPanel();
        suppliersContainer.setLayout(new BoxLayout(suppliersContainer, BoxLayout.Y_AXIS));
        suppliersContainer.setBackground(Color.WHITE);
        suppliersContainer.setBorder(new EmptyBorder(10, 14, 10, 14));

        JScrollPane scroll = new JScrollPane(suppliersContainer);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(Color.WHITE);
        card.add(scroll, BorderLayout.CENTER);
        return card;
    }

    private JPanel createSectionCard(String title, JComponent centerComponent) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createLineBorder(UITheme.BORDER_COLOR, 1, true));

        JPanel head = new JPanel(new BorderLayout());
        head.setBackground(new Color(248, 245, 240));
        head.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, UITheme.BORDER_COLOR),
            new EmptyBorder(10, 16, 10, 16)
        ));

        JLabel l = new JLabel(title);
        l.setFont(new Font("Segoe UI", Font.BOLD, 12));
        l.setForeground(UITheme.PRIMARY);
        head.add(l, BorderLayout.WEST);

        card.add(head, BorderLayout.NORTH);
        if (centerComponent != null) {
            card.add(centerComponent, BorderLayout.CENTER);
        }
        return card;
    }

    // ══════════════════════════════════════════════════════════════
    // LIVE DATA QUERY (Non-blocking SwingWorker)
    // ══════════════════════════════════════════════════════════════
    public void loadDashboardData() {
        SwingWorker<DashboardData, Void> worker = new SwingWorker<>() {
            @Override
            protected DashboardData doInBackground() {
                DashboardData data = new DashboardData();
                try (Connection con = DBConnection.getConnection()) {

                    // 1. KPI Counts
                    data.totalProducts = queryInt(con, "SELECT COUNT(*) FROM products WHERE status != 'DISCONTINUED'");
                    data.totalValuation = queryDouble(con, "SELECT COALESCE(SUM(quantity_in_stock * cost_price), 0) FROM products WHERE status != 'DISCONTINUED'");
                    data.inStockCount = queryInt(con, "SELECT COUNT(*) FROM products WHERE quantity_in_stock > reorder_level AND status != 'DISCONTINUED'");
                    data.lowStockCount = queryInt(con, "SELECT COUNT(*) FROM products WHERE quantity_in_stock <= reorder_level AND status != 'DISCONTINUED'");

                    // 2. Products table rows (with min stock for progress ratio)
                    String pSql = """
                        SELECT p.product_name, p.sku, c.category_name, p.quantity_in_stock,
                               p.reorder_level, p.unit_of_measure, p.unit_price, p.status
                        FROM products p
                        LEFT JOIN categories c ON p.category_id = c.category_id
                        WHERE p.status != 'DISCONTINUED'
                        ORDER BY p.quantity_in_stock ASC
                        """;
                    try (PreparedStatement ps = con.prepareStatement(pSql);
                         ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                            int stock = rs.getInt("quantity_in_stock");
                            int min = rs.getInt("reorder_level");
                            String unit = rs.getString("unit_of_measure");
                            double price = rs.getDouble("unit_price");
                            String status = rs.getString("status");

                            data.productRows.add(new Object[]{
                                rs.getString("product_name"),
                                rs.getString("sku"),
                                rs.getString("category_name") != null ? rs.getString("category_name") : "General",
                                new StockLevelData(stock, min, unit),
                                String.format("₱%.2f", price),
                                status
                            });
                        }
                    }

                    // 3. Low stock alerts (top 3 urgent)
                    String alertSql = """
                        SELECT product_name, quantity_in_stock, reorder_level, unit_of_measure
                        FROM products
                        WHERE quantity_in_stock <= reorder_level AND status != 'DISCONTINUED'
                        ORDER BY quantity_in_stock ASC
                        LIMIT 3
                        """;
                    try (PreparedStatement ps = con.prepareStatement(alertSql);
                         ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                            data.alertItems.add(new AlertItem(
                                rs.getString("product_name"),
                                rs.getInt("quantity_in_stock"),
                                rs.getInt("reorder_level"),
                                rs.getString("unit_of_measure")
                            ));
                        }
                    }

                    // 4. Recent transactions (latest 4)
                    String txSql = """
                        SELECT st.transaction_type, p.product_name, st.quantity, p.unit_of_measure, st.transaction_date
                        FROM stock_transactions st
                        JOIN products p ON st.product_id = p.product_id
                        ORDER BY st.transaction_date DESC
                        LIMIT 4
                        """;
                    try (PreparedStatement ps = con.prepareStatement(txSql);
                         ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                            data.recentTransactions.add(new TxItem(
                                rs.getString("transaction_type"),
                                rs.getString("product_name"),
                                rs.getInt("quantity"),
                                rs.getString("unit_of_measure")
                            ));
                        }
                    }

                    // 5. Category breakdown
                    String catSql = """
                        SELECT c.category_name, COUNT(p.product_id) as item_count
                        FROM categories c
                        LEFT JOIN products p ON c.category_id = p.category_id AND p.status != 'DISCONTINUED'
                        GROUP BY c.category_id, c.category_name
                        ORDER BY item_count DESC
                        LIMIT 5
                        """;
                    try (PreparedStatement ps = con.prepareStatement(catSql);
                         ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                            data.categories.add(new KeyVal(
                                rs.getString("category_name"),
                                rs.getInt("item_count") + " items"
                            ));
                        }
                    }

                    // 6. Suppliers list
                    String supSql = """
                        SELECT supplier_name, city, contact_person
                        FROM suppliers
                        WHERE status = 'ACTIVE'
                        LIMIT 4
                        """;
                    try (PreparedStatement ps = con.prepareStatement(supSql);
                         ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                            data.suppliers.add(new KeyVal(
                                rs.getString("supplier_name"),
                                rs.getString("city") != null ? rs.getString("city") : "Roaster"
                            ));
                        }
                    }

                    // 7. Weekly chart: inbound & outbound grouped by day
                    String chartSql = """
                        SELECT DAYNAME(transaction_date) as day_name,
                               SUM(CASE WHEN transaction_type IN ('INBOUND','ADJUSTMENT_IN') THEN quantity ELSE 0 END) as inbound,
                               SUM(CASE WHEN transaction_type IN ('OUTBOUND','ADJUSTMENT_OUT','SPOILAGE') THEN ABS(quantity) ELSE 0 END) as outbound
                        FROM stock_transactions
                        WHERE transaction_date >= DATE_SUB(NOW(), INTERVAL 7 DAY)
                        GROUP BY day_name
                        LIMIT 7
                        """;
                    try (PreparedStatement ps = con.prepareStatement(chartSql);
                         ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                            String d = rs.getString("day_name");
                            data.chartDays.add(d != null && d.length() >= 3 ? d.substring(0, 3) : "Day");
                            data.chartInbound.add(rs.getInt("inbound"));
                            data.chartOutbound.add(rs.getInt("outbound"));
                        }
                    }

                } catch (Exception ex) {
                    data.error = ex.getMessage();
                }
                return data;
            }

            @Override
            protected void done() {
                try {
                    DashboardData d = get();
                    if (d.error != null) {
                        lblTotalProducts.setText("Err");
                        JOptionPane.showMessageDialog(DashboardFrame.this,
                            "Notice while querying MySQL Workbench:\n" + d.error + "\n\nMake sure MySQL is running on localhost:3306 or 3307.",
                            "Database Sync", JOptionPane.INFORMATION_MESSAGE);
                        return;
                    }

                    // KPIs
                    lblTotalProducts.setText(String.valueOf(d.totalProducts));
                    lblTotalValue.setText("₱" + String.format("%,.2f", d.totalValuation));
                    lblInStockCount.setText(String.valueOf(d.inStockCount));
                    lblLowStockCount.setText(String.valueOf(d.lowStockCount));

                    // Table
                    productsTableModel.setRowCount(0);
                    for (Object[] row : d.productRows) {
                        productsTableModel.addRow(row);
                    }

                    // Alerts
                    alertsContainer.removeAll();
                    if (d.alertItems.isEmpty()) {
                        JLabel ok = new JLabel("✅  All cafe ingredients are sufficiently stocked.");
                        ok.setFont(UITheme.FONT_SMALL);
                        ok.setForeground(UITheme.SUCCESS);
                        alertsContainer.add(ok);
                    } else {
                        for (AlertItem item : d.alertItems) {
                            alertsContainer.add(buildAlertRow(item));
                            alertsContainer.add(Box.createVerticalStrut(6));
                        }
                    }

                    // Recent activity
                    transactionsContainer.removeAll();
                    if (d.recentTransactions.isEmpty()) {
                        JLabel empty = new JLabel("No transactions logged yet.");
                        empty.setFont(UITheme.FONT_SMALL);
                        empty.setForeground(UITheme.TEXT_MUTED);
                        transactionsContainer.add(empty);
                    } else {
                        for (TxItem tx : d.recentTransactions) {
                            transactionsContainer.add(buildTxRow(tx));
                            transactionsContainer.add(Box.createVerticalStrut(6));
                        }
                    }

                    // Categories
                    categoriesContainer.removeAll();
                    for (KeyVal cat : d.categories) {
                        categoriesContainer.add(buildListRow("☕", cat.key, cat.val));
                        categoriesContainer.add(Box.createVerticalStrut(4));
                    }

                    // Suppliers
                    suppliersContainer.removeAll();
                    for (KeyVal sup : d.suppliers) {
                        suppliersContainer.add(buildListRow("🌿", sup.key, sup.val));
                        suppliersContainer.add(Box.createVerticalStrut(4));
                    }

                    // Chart
                    weeklyChart.setData(d.chartDays, d.chartInbound, d.chartOutbound);

                    revalidate();
                    repaint();

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    private JPanel buildAlertRow(AlertItem item) {
        JPanel row = new JPanel(new BorderLayout(8, 0));
        row.setOpaque(false);

        boolean isZero = item.current <= 0;
        JLabel dot = new JLabel(isZero ? "🔴" : "🟡");
        dot.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 12));

        JPanel text = new JPanel();
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        text.setOpaque(false);

        JLabel name = new JLabel(item.name);
        name.setFont(new Font("Segoe UI", Font.BOLD, 12));
        name.setForeground(UITheme.TEXT_MAIN);

        JLabel sub = new JLabel(String.format("%d %s left (Min reorder: %d)", item.current, item.unit, item.min));
        sub.setFont(UITheme.FONT_SMALL);
        sub.setForeground(isZero ? UITheme.DANGER : UITheme.WARNING);

        text.add(name);
        text.add(sub);

        row.add(dot, BorderLayout.WEST);
        row.add(text, BorderLayout.CENTER);
        return row;
    }

    private JPanel buildTxRow(TxItem tx) {
        JPanel row = new JPanel(new BorderLayout(8, 0));
        row.setOpaque(false);

        boolean isInbound = tx.type.contains("INBOUND");
        JLabel icon = new JLabel(isInbound ? "📦" : "🫗");
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 12));

        JLabel name = new JLabel(tx.product);
        name.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        name.setForeground(UITheme.TEXT_MAIN);

        String qtyStr = (isInbound ? "+" : "-") + Math.abs(tx.qty) + " " + tx.unit;
        JLabel qty = new JLabel(qtyStr);
        qty.setFont(new Font("Segoe UI", Font.BOLD, 12));
        qty.setForeground(isInbound ? UITheme.SUCCESS : UITheme.DANGER);

        row.add(icon, BorderLayout.WEST);
        row.add(name, BorderLayout.CENTER);
        row.add(qty, BorderLayout.EAST);
        return row;
    }

    private JPanel buildListRow(String icon, String title, String sub) {
        JPanel row = new JPanel(new BorderLayout(8, 0));
        row.setOpaque(false);
        row.setBorder(new EmptyBorder(4, 4, 4, 4));

        JLabel ico = new JLabel(icon);
        ico.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));

        JLabel t = new JLabel(title);
        t.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        t.setForeground(UITheme.TEXT_MAIN);

        JLabel s = new JLabel(sub);
        s.setFont(UITheme.FONT_SMALL);
        s.setForeground(UITheme.TEXT_MUTED);

        row.add(ico, BorderLayout.WEST);
        row.add(t, BorderLayout.CENTER);
        row.add(s, BorderLayout.EAST);
        return row;
    }

    // ══════════════════════════════════════════════════════════════
    // ACTION HANDLERS
    // ══════════════════════════════════════════════════════════════
    private void onAddProduct() {
        try {
            List<Category> cats = categoryService.getAllCategories();
            List<Supplier> sups = supplierService.getAllSuppliers();
            ProductDialog dialog = new ProductDialog(this, "Add Cafe Inventory Item", null, cats, sups);
            dialog.setVisible(true);
            if (dialog.isSaved()) {
                productService.addProduct(dialog.getProduct());
                loadDashboardData();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Could not open Add Product dialog: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onRestockDelivery() {
        openStockDialog("INBOUND");
    }

    private void onBarDispense() {
        openStockDialog("OUTBOUND");
    }

    private void openStockDialog(String defaultType) {
        try {
            List<Product> products = productService.getAllProducts();
            if (products.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No products available in database to adjust.", "Information", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            Product selectedProduct = null;
            int selRow = productsTable.getSelectedRow();
            if (selRow >= 0) {
                String selectedSku = (String) productsTableModel.getValueAt(selRow, 1);
                for (Product p : products) {
                    if (p.getSku().equalsIgnoreCase(selectedSku)) {
                        selectedProduct = p;
                        break;
                    }
                }
            }

            if (selectedProduct == null) {
                // If no row is selected, let user pick from a dropdown
                Product[] prodArray = products.toArray(new Product[0]);
                selectedProduct = (Product) JOptionPane.showInputDialog(
                    this,
                    "Select a cafe item to log " + (defaultType.equals("INBOUND") ? "Restock Delivery" : "Bar Dispense") + ":",
                    "Choose Item",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    prodArray,
                    prodArray[0]
                );
            }

            if (selectedProduct != null) {
                StockController stockCtrl = new StockController();
                stockCtrl.openStockDialog(this, selectedProduct, defaultType, this::loadDashboardData);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Could not process transaction: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openFullManagementConsole() {
        dispose();
        MainFrame mainFrame = new MainFrame(currentUser);
        mainFrame.setVisible(true);
    }

    private int queryInt(Connection con, String sql) throws Exception {
        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    private double queryDouble(Connection con, String sql) throws Exception {
        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getDouble(1) : 0.0;
        }
    }

    // ══════════════════════════════════════════════════════════════
    // CUSTOM TABLE CELL RENDERERS
    // ══════════════════════════════════════════════════════════════

    /** Renders stock quantity with an in-cell graphical progress bar */
    private static class StockBarCellRenderer extends DefaultTableCellRenderer {
        private final JPanel panel = new JPanel(new BorderLayout(6, 0));
        private final JLabel label = new JLabel();
        private final JProgressBar bar = new JProgressBar(0, 100);

        StockBarCellRenderer() {
            panel.setOpaque(true);
            panel.setBorder(new EmptyBorder(4, 8, 4, 8));
            label.setFont(UITheme.FONT_REGULAR);
            bar.setPreferredSize(new Dimension(50, 6));
            bar.setBorderPainted(false);
            panel.add(label, BorderLayout.WEST);
            panel.add(bar, BorderLayout.CENTER);
        }

        @Override
        public Component getTableCellRendererComponent(JTable t, Object val, boolean sel, boolean foc, int r, int c) {
            Color bg = sel ? t.getSelectionBackground() : Color.WHITE;
            panel.setBackground(bg);

            if (val instanceof StockLevelData) {
                StockLevelData s = (StockLevelData) val;
                label.setText(s.current + " " + s.unit);
                int target = Math.max(s.min * 2, 20);
                int pct = Math.min(100, (int) ((s.current * 100.0) / target));
                bar.setValue(pct);

                if (s.current <= 0) {
                    bar.setForeground(UITheme.DANGER);
                } else if (s.current <= s.min) {
                    bar.setForeground(UITheme.WARNING);
                } else {
                    bar.setForeground(UITheme.SUCCESS);
                }
            }
            return panel;
        }
    }

    /** Status badge with pill shape styling */
    private static class StatusBadgeCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable t, Object val, boolean sel, boolean foc, int r, int c) {
            super.getTableCellRendererComponent(t, val, sel, foc, r, c);
            setHorizontalAlignment(CENTER);
            setFont(new Font("Segoe UI", Font.BOLD, 11));
            setBorder(new EmptyBorder(2, 6, 2, 6));

            String status = val != null ? val.toString() : "ACTIVE";
            switch (status) {
                case "ACTIVE" -> {
                    setText("● In Stock");
                    setForeground(new Color(21, 128, 61));
                    setBackground(new Color(220, 252, 231));
                }
                case "LOW_STOCK" -> {
                    setText("▲ Low Stock");
                    setForeground(new Color(180, 83, 9));
                    setBackground(new Color(254, 243, 199));
                }
                case "OUT_OF_STOCK" -> {
                    setText("✖ Critical");
                    setForeground(new Color(220, 38, 38));
                    setBackground(new Color(254, 226, 226));
                }
                default -> {
                    setText(status);
                    setForeground(UITheme.TEXT_MAIN);
                    setBackground(sel ? t.getSelectionBackground() : Color.WHITE);
                }
            }
            return this;
        }
    }

    // ── Supporting Data Structures ─────────────────────────────────
    private static class StockLevelData {
        int current, min;
        String unit;
        StockLevelData(int current, int min, String unit) {
            this.current = current;
            this.min = min;
            this.unit = unit != null ? unit : "Units";
        }
    }

    private static class AlertItem {
        String name, unit;
        int current, min;
        AlertItem(String name, int current, int min, String unit) {
            this.name = name;
            this.current = current;
            this.min = min;
            this.unit = unit != null ? unit : "Units";
        }
    }

    private static class TxItem {
        String type, product, unit;
        int qty;
        TxItem(String type, String product, int qty, String unit) {
            this.type = type;
            this.product = product;
            this.qty = qty;
            this.unit = unit != null ? unit : "Units";
        }
    }

    private static class KeyVal {
        String key, val;
        KeyVal(String key, String val) { this.key = key; this.val = val; }
    }

    private static class DashboardData {
        int totalProducts, inStockCount, lowStockCount;
        double totalValuation;
        List<Object[]> productRows = new ArrayList<>();
        List<AlertItem> alertItems = new ArrayList<>();
        List<TxItem> recentTransactions = new ArrayList<>();
        List<KeyVal> categories = new ArrayList<>();
        List<KeyVal> suppliers = new ArrayList<>();
        List<String> chartDays = new ArrayList<>();
        List<Integer> chartInbound = new ArrayList<>();
        List<Integer> chartOutbound = new ArrayList<>();
        String error;
    }

    // ── Weekly Usage Chart Component ──────────────────────────────
    private static class WeeklyUsageChart extends JPanel {
        private List<String> days = new ArrayList<>();
        private List<Integer> inbound = new ArrayList<>();
        private List<Integer> outbound = new ArrayList<>();

        WeeklyUsageChart() {
            setBackground(Color.WHITE);
            setPreferredSize(new Dimension(0, 180));
        }

        void setData(List<String> days, List<Integer> in, List<Integer> out) {
            this.days = days;
            this.inbound = in;
            this.outbound = out;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth(), h = getHeight();
            int padL = 36, padR = 16, padT = 16, padB = 40;
            int chartW = w - padL - padR;
            int chartH = h - padT - padB;

            if (days.isEmpty()) {
                days = List.of("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun");
                inbound = List.of(20, 15, 30, 25, 45, 50, 35);
                outbound = List.of(12, 18, 22, 28, 40, 48, 30);
            }

            int max = 1;
            for (int i = 0; i < days.size(); i++) {
                max = Math.max(max, Math.max(inbound.get(i), outbound.get(i)));
            }
            max = (int) Math.ceil(max * 1.2);

            // Subtle horizontal gridlines
            g2.setColor(new Color(240, 235, 230));
            for (int i = 0; i <= 3; i++) {
                int y = padT + (i * chartH / 3);
                g2.drawLine(padL, y, padL + chartW, y);
            }

            // Draw paired bars
            int n = days.size();
            int groupW = chartW / n;
            int barW = Math.max(8, (groupW - 14) / 2);

            Color colIn = new Color(21, 128, 61);
            Color colOut = new Color(180, 83, 9);

            for (int i = 0; i < n; i++) {
                int gx = padL + i * groupW + (groupW - (barW * 2 + 4)) / 2;

                int inH = (inbound.get(i) * chartH) / max;
                int outH = (outbound.get(i) * chartH) / max;

                // Inbound (Green)
                g2.setColor(colIn);
                g2.fillRoundRect(gx, padT + chartH - inH, barW, inH, 4, 4);

                // Outbound (Caramel Amber)
                g2.setColor(colOut);
                g2.fillRoundRect(gx + barW + 4, padT + chartH - outH, barW, outH, 4, 4);

                // Day Label
                g2.setColor(UITheme.TEXT_MUTED);
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
                FontMetrics fm = g2.getFontMetrics();
                String day = days.get(i);
                g2.drawString(day, gx + barW - fm.stringWidth(day) / 2, h - padB + 16);
            }

            // Legend
            int ly = h - 8;
            g2.setFont(new Font("Segoe UI", Font.BOLD, 10));

            g2.setColor(colIn);
            g2.fillRect(padL, ly - 9, 10, 8);
            g2.setColor(UITheme.TEXT_MAIN);
            g2.drawString("Restock Inbound", padL + 14, ly);

            g2.setColor(colOut);
            g2.fillRect(padL + 120, ly - 9, 10, 8);
            g2.setColor(UITheme.TEXT_MAIN);
            g2.drawString("Bar Outbound", padL + 134, ly);
        }
    }
}
