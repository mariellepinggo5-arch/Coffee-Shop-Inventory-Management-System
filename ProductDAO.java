package com.inventory.dao;

import com.inventory.model.Product;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {

    private static final String BASE_SELECT =
        "SELECT p.product_id, p.sku, p.product_name, p.category_id, p.supplier_id, " +
        "p.cost_price, p.unit_price, p.quantity_in_stock, p.reorder_level, p.unit_of_measure, " +
        "p.status, p.created_at, p.updated_at, " +
        "c.category_name, s.supplier_name " +
        "FROM products p " +
        "LEFT JOIN categories c ON p.category_id = c.category_id " +
        "LEFT JOIN suppliers s ON p.supplier_id = s.supplier_id ";

    public boolean create(Product product) throws SQLException {
        String sql = "INSERT INTO products (sku, product_name, category_id, supplier_id, " +
                     "cost_price, unit_price, quantity_in_stock, reorder_level, unit_of_measure, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, product.getSku());
            stmt.setString(2, product.getProductName());
            stmt.setInt(3, product.getCategoryId());
            stmt.setInt(4, product.getSupplierId());
            stmt.setBigDecimal(5, product.getCostPrice());
            stmt.setBigDecimal(6, product.getUnitPrice());
            stmt.setInt(7, product.getQuantityInStock());
            stmt.setInt(8, product.getReorderLevel());
            stmt.setString(9, product.getUnitOfMeasure());
            stmt.setString(10, product.getStatus());

            int affected = stmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet keys = stmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        product.setProductId(keys.getInt(1));
                    }
                }
                return true;
            }
        }
        return false;
    }

    public boolean update(Product product) throws SQLException {
        String sql = "UPDATE products SET sku = ?, product_name = ?, category_id = ?, supplier_id = ?, " +
                     "cost_price = ?, unit_price = ?, quantity_in_stock = ?, reorder_level = ?, " +
                     "unit_of_measure = ?, status = ? WHERE product_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, product.getSku());
            stmt.setString(2, product.getProductName());
            stmt.setInt(3, product.getCategoryId());
            stmt.setInt(4, product.getSupplierId());
            stmt.setBigDecimal(5, product.getCostPrice());
            stmt.setBigDecimal(6, product.getUnitPrice());
            stmt.setInt(7, product.getQuantityInStock());
            stmt.setInt(8, product.getReorderLevel());
            stmt.setString(9, product.getUnitOfMeasure());
            stmt.setString(10, product.getStatus());
            stmt.setInt(11, product.getProductId());

            return stmt.executeUpdate() > 0;
        }
    }

    public boolean delete(int productId) throws SQLException {
        String sql = "DELETE FROM products WHERE product_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, productId);
            return stmt.executeUpdate() > 0;
        }
    }

    public Product findById(int productId) throws SQLException {
        String sql = BASE_SELECT + "WHERE p.product_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, productId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    public Product findBySku(String sku) throws SQLException {
        String sql = BASE_SELECT + "WHERE LOWER(p.sku) = LOWER(?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, sku.trim());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    public List<Product> findAll() throws SQLException {
        List<Product> list = new ArrayList<>();
        String sql = BASE_SELECT + "ORDER BY p.product_name ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        return list;
    }

    public List<Product> findLowStock() throws SQLException {
        List<Product> list = new ArrayList<>();
        String sql = BASE_SELECT + "WHERE p.quantity_in_stock <= p.reorder_level AND p.status = 'ACTIVE' " +
                     "ORDER BY p.quantity_in_stock ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        return list;
    }

    public List<Product> search(String keyword, Integer categoryId, Integer supplierId, String stockFilter) throws SQLException {
        List<Product> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(BASE_SELECT);
        sql.append("WHERE 1=1 ");

        List<Object> params = new ArrayList<>();

        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append("AND (LOWER(p.product_name) LIKE ? OR LOWER(p.sku) LIKE ?) ");
            String term = "%" + keyword.trim().toLowerCase() + "%";
            params.add(term);
            params.add(term);
        }

        if (categoryId != null && categoryId > 0) {
            sql.append("AND p.category_id = ? ");
            params.add(categoryId);
        }

        if (supplierId != null && supplierId > 0) {
            sql.append("AND p.supplier_id = ? ");
            params.add(supplierId);
        }

        if ("LOW_STOCK".equalsIgnoreCase(stockFilter)) {
            sql.append("AND p.quantity_in_stock <= p.reorder_level ");
        } else if ("OUT_OF_STOCK".equalsIgnoreCase(stockFilter)) {
            sql.append("AND p.quantity_in_stock = 0 ");
        } else if ("IN_STOCK".equalsIgnoreCase(stockFilter)) {
            sql.append("AND p.quantity_in_stock > p.reorder_level ");
        }

        sql.append("ORDER BY p.product_name ASC");

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        }
        return list;
    }

    public boolean isSkuExists(String sku, int excludeProductId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM products WHERE LOWER(sku) = LOWER(?) AND product_id != ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, sku.trim());
            stmt.setInt(2, excludeProductId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    public boolean updateQuantity(int productId, int newQuantity) throws SQLException {
        String sql = "UPDATE products SET quantity_in_stock = ? WHERE product_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, newQuantity);
            stmt.setInt(2, productId);
            return stmt.executeUpdate() > 0;
        }
    }

    public int getTransactionCount(int productId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM stock_transactions WHERE product_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, productId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    private Product mapRow(ResultSet rs) throws SQLException {
        Product p = new Product();
        p.setProductId(rs.getInt("product_id"));
        p.setSku(rs.getString("sku"));
        p.setProductName(rs.getString("product_name"));
        p.setCategoryId(rs.getInt("category_id"));
        p.setSupplierId(rs.getInt("supplier_id"));
        p.setCostPrice(rs.getBigDecimal("cost_price"));
        p.setUnitPrice(rs.getBigDecimal("unit_price"));
        p.setQuantityInStock(rs.getInt("quantity_in_stock"));
        p.setReorderLevel(rs.getInt("reorder_level"));
        p.setUnitOfMeasure(rs.getString("unit_of_measure"));
        p.setStatus(rs.getString("status"));
        p.setCreatedAt(rs.getTimestamp("created_at"));
        p.setUpdatedAt(rs.getTimestamp("updated_at"));

        p.setCategoryName(rs.getString("category_name"));
        p.setSupplierName(rs.getString("supplier_name"));
        return p;
    }
}
