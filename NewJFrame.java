package com.inventory.view;

import com.inventory.controller.AuthController;
import com.inventory.controller.CategoryController;
import com.inventory.controller.ProductController;
import com.inventory.controller.StockController;
import com.inventory.controller.SupplierController;
import com.inventory.model.User;
import com.inventory.util.UITheme;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;

/**
 * NewJFrame – The NetBeans Visual Form JFrame for BrewTrack Coffee Shop Inventory.
 * Integrates visual form designer layout with all controllers and views.
 *
 * @author marie
 */
public class NewJFrame extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(NewJFrame.class.getName());

    private final User currentUser;
    private final AuthController authController;
    private final ProductController productController;
    private final CategoryController categoryController;
    private final SupplierController supplierController;
    private final StockController stockController;

    private CardLayout cardLayout;
    private DashboardPanel dashboardPanel;
    private ProductPanel productPanel;
    private CategoryPanel categoryPanel;
    private SupplierPanel supplierPanel;
    private StockTransactionPanel transactionPanel;
    private LowStockPanel lowStockPanel;

    private final List<JButton> navButtons = new ArrayList<>();

    /**
     * Default constructor for NetBeans Form Editor and standalone launch
     */
    public NewJFrame() {
        this(new User(1, "admin", "admin123", "Marielle", "ADMIN", "ACTIVE"));
    }

    /**
     * Creates new form NewJFrame for an authenticated User session
     */
    public NewJFrame(User user) {
        this.currentUser = (user != null) ? user : new User(1, "admin", "admin123", "Marielle", "ADMIN", "ACTIVE");
        this.authController = new AuthController();
        this.productController = new ProductController();
        this.categoryController = new CategoryController();
        this.supplierController = new SupplierController();
        this.stockController = new StockController();

        // 1. Replace default Java Duke icon on window & taskbar
        UITheme.applyAppIcon(this);

        // 2. NetBeans GUI Form generated component tree
        initComponents();

        // 3. Connect controllers, embed view panels into CardLayout, and wire actions
        customInit();
    }

    /**
     * Sets up the views, card layout, and button listeners.
     */
    private void customInit() {
        setLocationRelativeTo(null);

        // Update user display
        lblUser.setText(currentUser.getFullName());
        lblRole.setText("Role: " + currentUser.getRole() + " · Cafe Manager");

        // Set up CardLayout and panels
        cardLayout = (CardLayout) contentContainer.getLayout();

        dashboardPanel = new DashboardPanel();
        productPanel = new ProductPanel(productController, stockController);
        categoryPanel = new CategoryPanel(categoryController);
        supplierPanel = new SupplierPanel(supplierController);
        transactionPanel = new StockTransactionPanel(stockController);
        lowStockPanel = new LowStockPanel(stockController, () -> productController.loadProducts());

        contentContainer.add(dashboardPanel, "DASHBOARD");
        contentContainer.add(productPanel, "PRODUCTS");
        contentContainer.add(lowStockPanel, "LOW_STOCK");
        contentContainer.add(categoryPanel, "CATEGORIES");
        contentContainer.add(supplierPanel, "SUPPLIERS");
        contentContainer.add(transactionPanel, "TRANSACTIONS");

        // Track navigation buttons
        navButtons.clear();
        navButtons.add(btnDashboard);
        navButtons.add(btnProducts);
        navButtons.add(btnLowStock);
        navButtons.add(btnCategories);
        navButtons.add(btnSuppliers);
        navButtons.add(btnTransactions);

        // Attach listeners
        btnDashboard.addActionListener(e -> selectNav(btnDashboard, "DASHBOARD"));
        btnProducts.addActionListener(e -> selectNav(btnProducts, "PRODUCTS"));
        btnLowStock.addActionListener(e -> selectNav(btnLowStock, "LOW_STOCK"));
        btnCategories.addActionListener(e -> selectNav(btnCategories, "CATEGORIES"));
        btnSuppliers.addActionListener(e -> selectNav(btnSuppliers, "SUPPLIERS"));
        btnTransactions.addActionListener(e -> selectNav(btnTransactions, "TRANSACTIONS"));

        btnLogout.addActionListener(e -> {
            dispose();
            LoginFrame login = new LoginFrame();
            login.setVisible(true);
        });

        // Default view: Dashboard
        selectNav(btnDashboard, "DASHBOARD");
    }

    private void selectNav(JButton selectedBtn, String cardKey) {
        for (JButton b : navButtons) {
            b.setBackground(UITheme.SIDEBAR_BG);
            b.setForeground(UITheme.SIDEBAR_TEXT);
            b.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        }
        selectedBtn.setBackground(UITheme.PRIMARY);
        selectedBtn.setForeground(Color.WHITE);
        selectedBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));

        cardLayout.show(contentContainer, cardKey);

        switch (cardKey) {
            case "DASHBOARD" -> dashboardPanel.loadData();
            case "PRODUCTS" -> productController.loadProducts();
            case "LOW_STOCK" -> lowStockPanel.loadLowStock();
            case "CATEGORIES" -> categoryController.loadCategories();
            case "SUPPLIERS" -> supplierController.loadSuppliers();
            case "TRANSACTIONS" -> stockController.loadTransactions();
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        sidebarPanel = new javax.swing.JPanel();
        brandPanel = new javax.swing.JPanel();
        lblBrand = new javax.swing.JLabel();
        lblTag = new javax.swing.JLabel();
        navPanel = new javax.swing.JPanel();
        btnDashboard = new javax.swing.JButton();
        btnProducts = new javax.swing.JButton();
        btnLowStock = new javax.swing.JButton();
        btnCategories = new javax.swing.JButton();
        btnSuppliers = new javax.swing.JButton();
        btnTransactions = new javax.swing.JButton();
        userPanel = new javax.swing.JPanel();
        lblUser = new javax.swing.JLabel();
        lblRole = new javax.swing.JLabel();
        btnLogout = new javax.swing.JButton();
        contentContainer = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("BrewTrack - Coffee Shop Inventory Management System (ISUFST)");
        setMinimumSize(new java.awt.Dimension(1100, 720));
        setPreferredSize(new java.awt.Dimension(1340, 840));

        sidebarPanel.setBackground(new java.awt.Color(35, 26, 21));
        sidebarPanel.setPreferredSize(new java.awt.Dimension(256, 800));
        sidebarPanel.setLayout(new java.awt.BorderLayout());

        brandPanel.setBackground(new java.awt.Color(35, 26, 21));
        brandPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(26, 22, 18, 22));
        brandPanel.setLayout(new javax.swing.BoxLayout(brandPanel, javax.swing.BoxLayout.Y_AXIS));

        lblBrand.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        lblBrand.setForeground(new java.awt.Color(255, 255, 255));
        lblBrand.setText("☕  BrewTrack");
        brandPanel.add(lblBrand);

        lblTag.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        lblTag.setForeground(new java.awt.Color(180, 160, 140));
        lblTag.setText("Coffee Shop Inventory");
        brandPanel.add(lblTag);

        sidebarPanel.add(brandPanel, java.awt.BorderLayout.NORTH);

        navPanel.setBackground(new java.awt.Color(35, 26, 21));
        navPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 12, 10, 12));
        navPanel.setLayout(new javax.swing.BoxLayout(navPanel, javax.swing.BoxLayout.Y_AXIS));

        btnDashboard.setBackground(new java.awt.Color(92, 58, 33));
        btnDashboard.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnDashboard.setForeground(new java.awt.Color(255, 255, 255));
        btnDashboard.setText("📊  Dashboard");
        btnDashboard.setBorder(javax.swing.BorderFactory.createEmptyBorder(9, 14, 9, 14));
        btnDashboard.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnDashboard.setFocusable(false);
        btnDashboard.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnDashboard.setMaximumSize(new java.awt.Dimension(32767, 42));
        navPanel.add(btnDashboard);

        btnProducts.setBackground(new java.awt.Color(35, 26, 21));
        btnProducts.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        btnProducts.setForeground(new java.awt.Color(245, 240, 235));
        btnProducts.setText("☕  Beans & Ingredients");
        btnProducts.setBorder(javax.swing.BorderFactory.createEmptyBorder(9, 14, 9, 14));
        btnProducts.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnProducts.setFocusable(false);
        btnProducts.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnProducts.setMaximumSize(new java.awt.Dimension(32767, 42));
        navPanel.add(btnProducts);

        btnLowStock.setBackground(new java.awt.Color(35, 26, 21));
        btnLowStock.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        btnLowStock.setForeground(new java.awt.Color(245, 240, 235));
        btnLowStock.setText("⚠️  Low Stock Alerts");
        btnLowStock.setBorder(javax.swing.BorderFactory.createEmptyBorder(9, 14, 9, 14));
        btnLowStock.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnLowStock.setFocusable(false);
        btnLowStock.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnLowStock.setMaximumSize(new java.awt.Dimension(32767, 42));
        navPanel.add(btnLowStock);

        btnCategories.setBackground(new java.awt.Color(35, 26, 21));
        btnCategories.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        btnCategories.setForeground(new java.awt.Color(245, 240, 235));
        btnCategories.setText("🏷️  Cafe Categories");
        btnCategories.setBorder(javax.swing.BorderFactory.createEmptyBorder(9, 14, 9, 14));
        btnCategories.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnCategories.setFocusable(false);
        btnCategories.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnCategories.setMaximumSize(new java.awt.Dimension(32767, 42));
        navPanel.add(btnCategories);

        btnSuppliers.setBackground(new java.awt.Color(35, 26, 21));
        btnSuppliers.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        btnSuppliers.setForeground(new java.awt.Color(245, 240, 235));
        btnSuppliers.setText("🌍  Roasters & Suppliers");
        btnSuppliers.setBorder(javax.swing.BorderFactory.createEmptyBorder(9, 14, 9, 14));
        btnSuppliers.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnSuppliers.setFocusable(false);
        btnSuppliers.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnSuppliers.setMaximumSize(new java.awt.Dimension(32767, 42));
        navPanel.add(btnSuppliers);

        btnTransactions.setBackground(new java.awt.Color(35, 26, 21));
        btnTransactions.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        btnTransactions.setForeground(new java.awt.Color(245, 240, 235));
        btnTransactions.setText("📦  Delivery & Bar Logs");
        btnTransactions.setBorder(javax.swing.BorderFactory.createEmptyBorder(9, 14, 9, 14));
        btnTransactions.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnTransactions.setFocusable(false);
        btnTransactions.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnTransactions.setMaximumSize(new java.awt.Dimension(32767, 42));
        navPanel.add(btnTransactions);

        sidebarPanel.add(navPanel, java.awt.BorderLayout.CENTER);

        userPanel.setBackground(new java.awt.Color(24, 16, 10));
        userPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(14, 16, 16, 16));
        userPanel.setLayout(new javax.swing.BoxLayout(userPanel, javax.swing.BoxLayout.Y_AXIS));

        lblUser.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        lblUser.setForeground(new java.awt.Color(255, 255, 255));
        lblUser.setText("Marielle");
        userPanel.add(lblUser);

        lblRole.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        lblRole.setForeground(new java.awt.Color(180, 160, 140));
        lblRole.setText("Role: ADMIN · Cafe Manager");
        userPanel.add(lblRole);

        btnLogout.setBackground(new java.awt.Color(220, 38, 38));
        btnLogout.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnLogout.setForeground(new java.awt.Color(255, 255, 255));
        btnLogout.setText("⏻  Log Out");
        btnLogout.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnLogout.setFocusable(false);
        btnLogout.setMaximumSize(new java.awt.Dimension(32767, 36));
        userPanel.add(btnLogout);

        sidebarPanel.add(userPanel, java.awt.BorderLayout.SOUTH);

        getContentPane().add(sidebarPanel, java.awt.BorderLayout.WEST);

        contentContainer.setBackground(new java.awt.Color(251, 249, 246));
        contentContainer.setLayout(new java.awt.CardLayout());
        getContentPane().add(contentContainer, java.awt.BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new NewJFrame().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel brandPanel;
    private javax.swing.JButton btnCategories;
    private javax.swing.JButton btnDashboard;
    private javax.swing.JButton btnLogout;
    private javax.swing.JButton btnLowStock;
    private javax.swing.JButton btnProducts;
    private javax.swing.JButton btnSuppliers;
    private javax.swing.JButton btnTransactions;
    private javax.swing.JPanel contentContainer;
    private javax.swing.JLabel lblBrand;
    private javax.swing.JLabel lblRole;
    private javax.swing.JLabel lblTag;
    private javax.swing.JLabel lblUser;
    private javax.swing.JPanel navPanel;
    private javax.swing.JPanel sidebarPanel;
    private javax.swing.JPanel userPanel;
    // End of variables declaration//GEN-END:variables
}
