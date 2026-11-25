package com.inventory.service;

import com.inventory.dao.CategoryDAO;
import com.inventory.model.Category;
import com.inventory.util.ValidationException;
import java.sql.SQLException;
import java.util.List;

public class CategoryService {

    private final CategoryDAO categoryDAO;

    public CategoryService() {
        this.categoryDAO = new CategoryDAO();
    }

    public List<Category> getAllCategories() throws SQLException {
        return categoryDAO.findAll();
    }

    public Category getCategoryById(int id) throws SQLException {
        return categoryDAO.findById(id);
    }

    public void addCategory(Category category) throws ValidationException, SQLException {
        validateCategory(category, 0);
        boolean success = categoryDAO.create(category);
        if (!success) {
            throw new SQLException("Failed to save new category into the database.");
        }
    }

    public void updateCategory(Category category) throws ValidationException, SQLException {
        if (category.getCategoryId() <= 0) {
            throw new ValidationException("Invalid category ID for update.");
        }
        validateCategory(category, category.getCategoryId());
        boolean success = categoryDAO.update(category);
        if (!success) {
            throw new SQLException("Failed to update category: Category record not found.");
        }
    }

    public void deleteCategory(int categoryId) throws ValidationException, SQLException {
        if (categoryId <= 0) {
            throw new ValidationException("Invalid category ID.");
        }

        int count = categoryDAO.getProductCount(categoryId);
        if (count > 0) {
            throw new ValidationException(
                "Cannot delete category: " + count + " product(s) are currently assigned to it.\n" +
                "Please reassign or remove those products first."
            );
        }

        boolean success = categoryDAO.delete(categoryId);
        if (!success) {
            throw new SQLException("Failed to delete category: record not found.");
        }
    }

    private void validateCategory(Category category, int excludeId) throws ValidationException, SQLException {
        if (category == null) {
            throw new ValidationException("Category information cannot be null.");
        }
        if (category.getCategoryName() == null || category.getCategoryName().trim().isEmpty()) {
            throw new ValidationException("Category Name is required.");
        }
        if (category.getCategoryName().trim().length() > 100) {
            throw new ValidationException("Category Name cannot exceed 100 characters.");
        }
        if (categoryDAO.isNameExists(category.getCategoryName(), excludeId)) {
            throw new ValidationException("A category with the name '" + category.getCategoryName().trim() + "' already exists.");
        }
    }
}
