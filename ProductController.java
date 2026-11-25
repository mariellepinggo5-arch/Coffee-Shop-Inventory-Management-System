package com.inventory.controller;

import com.inventory.model.Product;
import com.inventory.service.CategoryService;
import com.inventory.service.ProductService;
import com.inventory.service.SupplierService;
import com.inventory.util.ValidationException;
import com.inventory.view.ProductPanel;
import com.inventory.view.dialog.ProductDialog;
import java.sql.SQLException;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class ProductController {

    private final ProductService productService;
    private final CategoryService categoryService;
    private final SupplierService supplierService;
    private ProductPanel productPanel;

    public ProductController() {
        this.productService = new ProductService();
        this.categoryService = new CategoryService();
        this.supplierService = new SupplierService();
    }

    public void setProductPanel(ProductPanel panel) {
        this.productPanel = panel;
    }

    public void loadProducts() {
        try {
            List<Product> products = productService.getAllProducts();
            productPanel.displayProducts(products);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                productPanel,
                "Error loading products: " + e.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    public void searchProducts(String keyword, Integer categoryId, Integer supplierId, String stockStatus) {
        try {
            List<Product> results = productService.searchProducts(keyword, categoryId, supplierId, stockStatus);
            productPanel.displayProducts(results);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                productPanel,
                "Search error: " + e.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    public void openAddProductDialog() {
        try {
            ProductDialog dialog = new ProductDialog(
                SwingUtilities.getWindowAncestor(productPanel),
                "Add New Product",
                null,
                categoryService.getAllCategories(),
                supplierService.getAllSuppliers()
            );
            dialog.setVisible(true);

            if (dialog.isSaved()) {
                Product newProduct = dialog.getProduct();
                productService.addProduct(newProduct);
                JOptionPane.showMessageDialog(
                    productPanel,
                    "Product '" + newProduct.getProductName() + "' created successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
                );
                loadProducts();
            }
        } catch (ValidationException ve) {
            JOptionPane.showMessageDialog(productPanel, ve.getMessage(), "Validation Notice", JOptionPane.WARNING_MESSAGE);
        } catch (SQLException sqle) {
            JOptionPane.showMessageDialog(productPanel, "Database error: " + sqle.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void openEditProductDialog(Product selectedProduct) {
        if (selectedProduct == null) {
            JOptionPane.showMessageDialog(productPanel, "Please select a product to edit.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            ProductDialog dialog = new ProductDialog(
                SwingUtilities.getWindowAncestor(productPanel),
                "Edit Product - " + selectedProduct.getSku(),
                selectedProduct,
                categoryService.getAllCategories(),
                supplierService.getAllSuppliers()
            );
            dialog.setVisible(true);

            if (dialog.isSaved()) {
                Product updated = dialog.getProduct();
                productService.updateProduct(updated);
                JOptionPane.showMessageDialog(
                    productPanel,
                    "Product '" + updated.getProductName() + "' updated successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
                );
                loadProducts();
            }
        } catch (ValidationException ve) {
            JOptionPane.showMessageDialog(productPanel, ve.getMessage(), "Validation Notice", JOptionPane.WARNING_MESSAGE);
        } catch (SQLException sqle) {
            JOptionPane.showMessageDialog(productPanel, "Database error: " + sqle.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void handleDeleteProduct(Product product) {
        if (product == null) {
            JOptionPane.showMessageDialog(productPanel, "Please select a product to delete.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
            productPanel,
            "Are you sure you want to delete product:\n" +
            "[" + product.getSku() + "] " + product.getProductName() + "?",
            "Confirm Product Deletion",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                productService.deleteProduct(product.getProductId());
                JOptionPane.showMessageDialog(
                    productPanel,
                    "Product deleted successfully.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
                );
                loadProducts();
            } catch (ValidationException ve) {
                JOptionPane.showMessageDialog(productPanel, ve.getMessage(), "Notice", JOptionPane.WARNING_MESSAGE);
                loadProducts();
            } catch (SQLException sqle) {
                JOptionPane.showMessageDialog(productPanel, "Database error: " + sqle.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
