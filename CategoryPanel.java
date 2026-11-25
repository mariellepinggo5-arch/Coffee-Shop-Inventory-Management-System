package com.inventory.view;

import com.inventory.controller.CategoryController;
import com.inventory.model.Category;
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

public class CategoryPanel extends JPanel {

    private final CategoryController categoryController;
    private JTable categoryTable;
    private DefaultTableModel tableModel;
    private List<Category> categoryList = new ArrayList<>();

    public CategoryPanel(CategoryController categoryController) {
        this.categoryController = categoryController;
        categoryController.setCategoryPanel(this);

        initComponents();
        categoryController.loadCategories();
    }

    private void initComponents() {
        setLayout(new BorderLayout(0, 12));
        setBackground(UITheme.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Header and Toolbar
        JPanel topPanel = new JPanel(new BorderLayout(0, 8));
        topPanel.setBackground(UITheme.BACKGROUND);

        JLabel lblTitle = new JLabel("Cafe Product & Ingredient Categories");
        lblTitle.setFont(UITheme.FONT_HEADER);
        lblTitle.setForeground(UITheme.TEXT_MAIN);
        topPanel.add(lblTitle, BorderLayout.NORTH);

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        toolbar.setBackground(UITheme.BACKGROUND);

        JButton btnAdd = UITheme.createPrimaryButton("+ Add Category");
        btnAdd.addActionListener(e -> categoryController.openAddCategoryDialog());

        JButton btnEdit = UITheme.createSecondaryButton("Edit Category");
        btnEdit.addActionListener(e -> categoryController.openEditCategoryDialog(getSelectedCategory()));

        JButton btnDelete = UITheme.createDangerButton("Delete Category");
        btnDelete.addActionListener(e -> categoryController.handleDeleteCategory(getSelectedCategory()));

        JButton btnRefresh = UITheme.createSecondaryButton("Refresh Categories");
        btnRefresh.addActionListener(e -> categoryController.loadCategories());

        toolbar.add(btnAdd);
        toolbar.add(btnEdit);
        toolbar.add(btnDelete);
        toolbar.add(btnRefresh);

        topPanel.add(toolbar, BorderLayout.SOUTH);
        add(topPanel, BorderLayout.NORTH);

        // Table
        String[] columns = {"ID", "Category Name", "Description", "Created Date"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        categoryTable = new JTable(tableModel);
        UITheme.styleTable(categoryTable);

        JScrollPane scrollPane = new JScrollPane(categoryTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(UITheme.BORDER_COLOR));
        add(scrollPane, BorderLayout.CENTER);
    }

    public void displayCategories(List<Category> categories) {
        this.categoryList = categories;
        tableModel.setRowCount(0);
        for (Category c : categories) {
            tableModel.addRow(new Object[]{
                c.getCategoryId(),
                c.getCategoryName(),
                c.getDescription(),
                c.getCreatedAt() != null ? c.getCreatedAt().toString() : ""
            });
        }
    }

    public Category getSelectedCategory() {
        int row = categoryTable.getSelectedRow();
        if (row >= 0 && row < categoryList.size()) {
            return categoryList.get(row);
        }
        return null;
    }
}
