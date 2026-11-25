package com.inventory.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class Product {
    private int productId;
    private String sku;
    private String productName;
    private int categoryId;
    private int supplierId;
    private BigDecimal costPrice;
    private BigDecimal unitPrice;
    private int quantityInStock;
    private int reorderLevel;
    private String unitOfMeasure;
    private String status; // ACTIVE, DISCONTINUED
    private Timestamp createdAt;
    private Timestamp updatedAt;

    // Associated lookup display names
    private String categoryName;
    private String supplierName;

    public Product() {
        this.costPrice = BigDecimal.ZERO;
        this.unitPrice = BigDecimal.ZERO;
        this.quantityInStock = 0;
        this.reorderLevel = 10;
        this.unitOfMeasure = "Piece";
        this.status = "ACTIVE";
    }

    public Product(int productId, String sku, String productName, int categoryId, int supplierId,
                   BigDecimal costPrice, BigDecimal unitPrice, int quantityInStock, int reorderLevel,
                   String unitOfMeasure, String status) {
        this.productId = productId;
        this.sku = sku;
        this.productName = productName;
        this.categoryId = categoryId;
        this.supplierId = supplierId;
        this.costPrice = costPrice;
        this.unitPrice = unitPrice;
        this.quantityInStock = quantityInStock;
        this.reorderLevel = reorderLevel;
        this.unitOfMeasure = unitOfMeasure;
        this.status = status;
    }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }

    public int getSupplierId() { return supplierId; }
    public void setSupplierId(int supplierId) { this.supplierId = supplierId; }

    public BigDecimal getCostPrice() { return costPrice; }
    public void setCostPrice(BigDecimal costPrice) { this.costPrice = costPrice; }

    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }

    public int getQuantityInStock() { return quantityInStock; }
    public void setQuantityInStock(int quantityInStock) { this.quantityInStock = quantityInStock; }

    public int getReorderLevel() { return reorderLevel; }
    public void setReorderLevel(int reorderLevel) { this.reorderLevel = reorderLevel; }

    public String getUnitOfMeasure() { return unitOfMeasure; }
    public void setUnitOfMeasure(String unitOfMeasure) { this.unitOfMeasure = unitOfMeasure; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public String getSupplierName() { return supplierName; }
    public void setSupplierName(String supplierName) { this.supplierName = supplierName; }

    public boolean isLowStock() {
        return quantityInStock <= reorderLevel;
    }

    public BigDecimal getTotalInventoryValue() {
        if (costPrice == null) return BigDecimal.ZERO;
        return costPrice.multiply(BigDecimal.valueOf(quantityInStock));
    }

    @Override
    public String toString() {
        return productName + " (" + sku + ")";
    }
}
