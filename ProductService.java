package com.inventory.service;

import com.inventory.dao.ProductDAO;
import com.inventory.model.Product;
import com.inventory.util.ValidationException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public class ProductService {

    private final ProductDAO productDAO;

    public ProductService() {
        this.productDAO = new ProductDAO();
    }

    public List<Product> getAllProducts() throws SQLException {
        return productDAO.findAll();
    }

    public List<Product> getLowStockProducts() throws SQLException {
        return productDAO.findLowStock();
    }

    public Product getProductById(int productId) throws SQLException {
        return productDAO.findById(productId);
    }

    public List<Product> searchProducts(String keyword, Integer categoryId, Integer supplierId, String stockStatus) throws SQLException {
        return productDAO.search(keyword, categoryId, supplierId, stockStatus);
    }

    public void addProduct(Product product) throws ValidationException, SQLException {
        validateProduct(product, 0);
        boolean success = productDAO.create(product);
        if (!success) {
            throw new SQLException("Failed to create product in the database.");
        }
    }

    public void updateProduct(Product product) throws ValidationException, SQLException {
        if (product.getProductId() <= 0) {
            throw new ValidationException("Invalid product ID for update.");
        }
        validateProduct(product, product.getProductId());
        boolean success = productDAO.update(product);
        if (!success) {
            throw new SQLException("Failed to update product: record not found.");
        }
    }

    public void deleteProduct(int productId) throws ValidationException, SQLException {
        if (productId <= 0) {
            throw new ValidationException("Invalid product ID.");
        }

        int transCount = productDAO.getTransactionCount(productId);
        if (transCount > 0) {
            Product p = productDAO.findById(productId);
            if (p != null) {
                p.setStatus("DISCONTINUED");
                productDAO.update(p);
                throw new ValidationException(
                    "Product has " + transCount + " past stock transaction logs.\n" +
                    "To maintain complete audit history, this product has been marked as DISCONTINUED instead of removed."
                );
            }
        }

        boolean success = productDAO.delete(productId);
        if (!success) {
            throw new SQLException("Failed to delete product: record not found.");
        }
    }

    private void validateProduct(Product product, int excludeProductId) throws ValidationException, SQLException {
        if (product == null) {
            throw new ValidationException("Product data cannot be empty.");
        }

        if (product.getSku() == null || product.getSku().trim().isEmpty()) {
            throw new ValidationException("Product SKU / Barcode is required.");
        }
        if (product.getSku().trim().length() > 50) {
            throw new ValidationException("SKU cannot exceed 50 characters.");
        }
        if (productDAO.isSkuExists(product.getSku(), excludeProductId)) {
            throw new ValidationException("A product with SKU '" + product.getSku().trim() + "' already exists.");
        }

        if (product.getProductName() == null || product.getProductName().trim().isEmpty()) {
            throw new ValidationException("Product Name is required.");
        }
        if (product.getProductName().trim().length() > 150) {
            throw new ValidationException("Product Name cannot exceed 150 characters.");
        }

        if (product.getCategoryId() <= 0) {
            throw new ValidationException("Please select a valid Category.");
        }
        if (product.getSupplierId() <= 0) {
            throw new ValidationException("Please select a valid Supplier.");
        }

        if (product.getCostPrice() == null || product.getCostPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidationException("Cost Price must be a non-negative number.");
        }
        if (product.getUnitPrice() == null || product.getUnitPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidationException("Selling Unit Price must be a non-negative number.");
        }

        if (product.getQuantityInStock() < 0) {
            throw new ValidationException("Quantity In Stock cannot be negative.");
        }
        if (product.getReorderLevel() < 0) {
            throw new ValidationException("Reorder Level cannot be negative.");
        }

        if (product.getUnitOfMeasure() == null || product.getUnitOfMeasure().trim().isEmpty()) {
            product.setUnitOfMeasure("Piece");
        }
        if (product.getStatus() == null || product.getStatus().trim().isEmpty()) {
            product.setStatus("ACTIVE");
        }
    }
}
