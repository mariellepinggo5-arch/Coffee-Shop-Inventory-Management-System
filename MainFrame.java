package com.inventory.view;

import com.inventory.controller.AuthController;
import com.inventory.controller.CategoryController;
import com.inventory.controller.ProductController;
import com.inventory.controller.StockController;
import com.inventory.controller.SupplierController;
import com.inventory.model.User;
import com.inventory.util.UITheme;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

public class MainFrame extends JFrame {

    private final User currentUser;
    private final AuthController authController;
    private final ProductController productController;
    private final CategoryController categoryController;
    private final SupplierController supplierController;
    private final StockController stockController;

    private CardLayout cardLayout;
    private JPanel contentContainer;

    private DashboardPanel dashboardPanel;
    private ProductPanel productPanel;
    private CategoryPanel categoryPanel;
    private SupplierPanel supplierPanel;
    private StockTransactionPanel transactionPanel;
    private LowStockPanel lowStockPanel;

    private final List<JButton> navButtons = new ArrayList<>();

    public MainFrame(User user) {
        this.currentUser = user;
        this.authController   = new AuthController();
        this.productController  = new ProductController();
        this.categoryController = new CategoryController();
        this.supplierController = new SupplierController();
        this.stockController    = new StockController();
        initUI();
    }

    private void initUI() {
        UITheme.applyAppIcon(this);
        setTitle("BrewTrack – Coffee Shop Inventory  |  ISUFST Final Project");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1340, 840);
        setMinimumSize(new Dimension(1080, 700));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        add(createSidebar(), BorderLayout.WEST);

        // CardLayout content area
        cardLayout = new CardLayout();
        contentContainer = new JPanel(cardLayout);
        contentContainer.setBackground(UITheme.BACKGROUND);

        dashboardPanel   = new DashboardPanel();
        productPanel     = new ProductPanel(productController, stockController);
        categoryPanel    = new CategoryPanel(categoryController);
        supplierPanel    = new SupplierPanel(supplierController);
        transactionPanel = new StockTransactionPanel(stockController);
        lowStockPanel    = new LowStockPanel(stockController, () -> productController.loadProducts());

        contentContainer.add(dashboardPanel,   "DASHBOARD");
        contentContainer.add(productPanel,     "PRODUCTS");
        contentContainer.add(lowStockPanel,    "LOW_STOCK");
        contentContainer.add(categoryPanel,    "CATEGORIES");
        contentContainer.add(supplierPanel,    "SUPPLIERS");
        contentContainer.add(transactionPanel, "TRANSACTIONS");

        add(contentContainer, BorderLayout.CENTER);

        // Start on Dashboard
        selectNav(navButtons.get(0), "DASHBOARD");
    }

    // ══════════════════════════════════════════════════════════════════
    // SIDEBAR
    // ══════════════════════════════════════════════════════════════════
    private JPanel createSidebar() {
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setBackground(UITheme.SIDEBAR_BG);
        sidebar.setPreferredSize(new Dimension(256, 0));

        // Brand header
        JPanel brand = new JPanel();
        brand.setLayout(new BoxLayout(brand, BoxLayout.Y_AXIS));
        brand.setBackground(UITheme.SIDEBAR_BG);
        brand.setBorder(BorderFactory.createEmptyBorder(26, 22, 18, 22));

        JLabel logoLine = new JLabel("☕  BrewTrack");
        logoLine.setFont(new Font("Segoe UI", Font.BOLD, 20));
        logoLine.setForeground(Color.WHITE);

        JLabel tagLine = new JLabel("Coffee Shop Inventory");
        tagLine.setFont(UITheme.FONT_SMALL);
        tagLine.setForeground(new Color(180, 160, 140));

        brand.add(logoLine);
        brand.add(Box.createVerticalStrut(3));
        brand.add(tagLine);
        brand.add(Box.createVerticalStrut(14));
        brand.add(makeDivider());

        sidebar.add(brand, BorderLayout.NORTH);

        // Nav menu
        JPanel nav = new JPanel();
        nav.setLayout(new BoxLayout(nav, BoxLayout.Y_AXIS));
        nav.setBackground(UITheme.SIDEBAR_BG);
        nav.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));

        nav.add(sectionLabel("MAIN"));
        nav.add(Box.createVerticalStrut(2));
        nav.add(createNavButton("📊  Dashboard",              "DASHBOARD"));
        nav.add(Box.createVerticalStrut(3));
        nav.add(createNavButton("☕  Beans & Ingredients",    "PRODUCTS"));

        nav.add(Box.createVerticalStrut(14));
        nav.add(sectionLabel("OPERATIONS"));
        nav.add(Box.createVerticalStrut(2));
        nav.add(createNavButton("⚠️  Low Stock Alerts",       "LOW_STOCK"));
        nav.add(Box.createVerticalStrut(3));
        nav.add(createNavButton("🏷️  Cafe Categories",        "CATEGORIES"));
        nav.add(Box.createVerticalStrut(3));
        nav.add(createNavButton("🌍  Roasters & Suppliers",   "SUPPLIERS"));
        nav.add(Box.createVerticalStrut(3));
        nav.add(createNavButton("📦  Delivery & Bar Usage",   "TRANSACTIONS"));

        sidebar.add(nav, BorderLayout.CENTER);

        // User footer
        JPanel footer = new JPanel();
        footer.setLayout(new BoxLayout(footer, BoxLayout.Y_AXIS));
        footer.setBackground(new Color(24, 16, 10));
        footer.setBorder(BorderFactory.createEmptyBorder(14, 16, 16, 16));

        JLabel avatar = new JLabel(String.valueOf(currentUser.getFullName().charAt(0)).toUpperCase());
        avatar.setFont(new Font("Segoe UI", Font.BOLD, 18));
        avatar.setForeground(Color.WHITE);
        avatar.setOpaque(true);
        avatar.setBackground(UITheme.ACCENT);
        avatar.setPreferredSize(new Dimension(36, 36));
        avatar.setMaximumSize(new Dimension(36, 36));
        avatar.setHorizontalAlignment(SwingConstants.CENTER);
        avatar.setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 0));

        JLabel nameLabel = new JLabel(currentUser.getFullName());
        nameLabel.setFont(UITheme.FONT_BOLD);
        nameLabel.setForeground(Color.WHITE);

        JLabel roleLabel = new JLabel("Role: " + currentUser.getRole());
        roleLabel.setFont(UITheme.FONT_SMALL);
        roleLabel.setForeground(new Color(180, 160, 140));

        JButton btnLogout = UITheme.createDangerButton("⏻  Log Out");
        btnLogout.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnLogout.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        btnLogout.addActionListener(e -> authController.handleLogout(this));

        footer.add(avatar);
        footer.add(Box.createVerticalStrut(6));
        footer.add(nameLabel);
        footer.add(Box.createVerticalStrut(2));
        footer.add(roleLabel);
        footer.add(Box.createVerticalStrut(10));
        footer.add(btnLogout);

        sidebar.add(footer, BorderLayout.SOUTH);
        return sidebar;
    }

    private JButton createNavButton(String text, String cardKey) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btn.setForeground(UITheme.SIDEBAR_TEXT);
        btn.setBackground(UITheme.SIDEBAR_BG);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(true);
        btn.setBorder(BorderFactory.createEmptyBorder(9, 14, 9, 14));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (!btn.getBackground().equals(UITheme.PRIMARY))
                    btn.setBackground(UITheme.SIDEBAR_HOVER);
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                if (!btn.getBackground().equals(UITheme.PRIMARY))
                    btn.setBackground(UITheme.SIDEBAR_BG);
            }
        });
        btn.addActionListener(e -> selectNav(btn, cardKey));
        navButtons.add(btn);
        return btn;
    }

    private void selectNav(JButton selected, String cardKey) {
        for (JButton b : navButtons) {
            b.setBackground(UITheme.SIDEBAR_BG);
            b.setForeground(UITheme.SIDEBAR_TEXT);
            b.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        }
        selected.setBackground(UITheme.PRIMARY);
        selected.setForeground(Color.WHITE);
        selected.setFont(new Font("Segoe UI", Font.BOLD, 13));
        cardLayout.show(contentContainer, cardKey);

        switch (cardKey) {
            case "DASHBOARD"    -> dashboardPanel.loadData();
            case "PRODUCTS"     -> productController.loadProducts();
            case "LOW_STOCK"    -> lowStockPanel.loadLowStock();
            case "CATEGORIES"   -> categoryController.loadCategories();
            case "SUPPLIERS"    -> supplierController.loadSuppliers();
            case "TRANSACTIONS" -> stockController.loadTransactions();
        }
    }

    private JSeparator makeDivider() {
        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(60, 45, 35));
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        return sep;
    }

    private JLabel sectionLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lbl.setForeground(new Color(130, 110, 95));
        lbl.setBorder(BorderFactory.createEmptyBorder(4, 6, 2, 0));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }
}
