package com.inventory.controller;

import com.inventory.model.Category;
import com.inventory.service.CategoryService;
import com.inventory.util.ValidationException;
import com.inventory.view.CategoryPanel;
import com.inventory.view.dialog.CategoryDialog;
import java.sql.SQLException;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class CategoryController {

    private final CategoryService categoryService;
    private CategoryPanel categoryPanel;

    public CategoryController() {
        this.categoryService = new CategoryService();
    }

    public void setCategoryPanel(CategoryPanel panel) {
        this.categoryPanel = panel;
    }

    public void loadCategories() {
        try {
            List<Category> categories = categoryService.getAllCategories();
            categoryPanel.displayCategories(categories);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                categoryPanel,
                "Error loading categories: " + e.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    public void openAddCategoryDialog() {
        CategoryDialog dialog = new CategoryDialog(
            SwingUtilities.getWindowAncestor(categoryPanel),
            "Add New Category",
            null
        );
        dialog.setVisible(true);

        if (dialog.isSaved()) {
            Category newCategory = dialog.getCategory();
            try {
                categoryService.addCategory(newCategory);
                JOptionPane.showMessageDialog(
                    categoryPanel,
                    "Category '" + newCategory.getCategoryName() + "' added successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
                );
                loadCategories();
            } catch (ValidationException ve) {
                JOptionPane.showMessageDialog(categoryPanel, ve.getMessage(), "Validation Notice", JOptionPane.WARNING_MESSAGE);
            } catch (SQLException sqle) {
                JOptionPane.showMessageDialog(categoryPanel, "Database error: " + sqle.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void openEditCategoryDialog(Category category) {
        if (category == null) {
            JOptionPane.showMessageDialog(categoryPanel, "Please select a category to edit.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        CategoryDialog dialog = new CategoryDialog(
            SwingUtilities.getWindowAncestor(categoryPanel),
            "Edit Category",
            category
        );
        dialog.setVisible(true);

        if (dialog.isSaved()) {
            Category updated = dialog.getCategory();
            try {
                categoryService.updateCategory(updated);
                JOptionPane.showMessageDialog(
                    categoryPanel,
                    "Category updated successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
                );
                loadCategories();
            } catch (ValidationException ve) {
                JOptionPane.showMessageDialog(categoryPanel, ve.getMessage(), "Validation Notice", JOptionPane.WARNING_MESSAGE);
            } catch (SQLException sqle) {
                JOptionPane.showMessageDialog(categoryPanel, "Database error: " + sqle.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void handleDeleteCategory(Category category) {
        if (category == null) {
            JOptionPane.showMessageDialog(categoryPanel, "Please select a category to delete.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
            categoryPanel,
            "Are you sure you want to delete category: '" + category.getCategoryName() + "'?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                categoryService.deleteCategory(category.getCategoryId());
                JOptionPane.showMessageDialog(
                    categoryPanel,
                    "Category deleted successfully.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
                );
                loadCategories();
            } catch (ValidationException ve) {
                JOptionPane.showMessageDialog(categoryPanel, ve.getMessage(), "Constraint Notice", JOptionPane.WARNING_MESSAGE);
            } catch (SQLException sqle) {
                JOptionPane.showMessageDialog(categoryPanel, "Database error: " + sqle.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
