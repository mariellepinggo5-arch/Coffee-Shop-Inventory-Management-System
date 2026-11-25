package com.inventory.dao;

import com.inventory.model.StockTransaction;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class StockTransactionDAO {

    private static final String BASE_SELECT =
        "SELECT t.transaction_id, t.product_id, t.transaction_type, t.quantity, " +
        "t.reference_no, t.remarks, t.performed_by, t.transaction_date, " +
        "p.product_name, p.sku, u.full_name AS performed_by_name " +
        "FROM stock_transactions t " +
        "JOIN products p ON t.product_id = p.product_id " +
        "JOIN users u ON t.performed_by = u.user_id ";

        public boolean recordTransaction(StockTransaction trans, int newStockQuantity) throws SQLException {
        String insertSql = "INSERT INTO stock_transactions (product_id, transaction_type, quantity, " +
                           "reference_no, remarks, performed_by) VALUES (?, ?, ?, ?, ?, ?)";
        String updateSql = "UPDATE products SET quantity_in_stock = ? WHERE product_id = ?";

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); 
            try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                updateStmt.setInt(1, newStockQuantity);
                updateStmt.setInt(2, trans.getProductId());
                updateStmt.executeUpdate();
            }

            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
                insertStmt.setInt(1, trans.getProductId());
                insertStmt.setString(2, trans.getTransactionType());
                insertStmt.setInt(3, trans.getQuantity());
                insertStmt.setString(4, trans.getReferenceNo());
                insertStmt.setString(5, trans.getRemarks());
                insertStmt.setInt(6, trans.getPerformedBy());

                insertStmt.executeUpdate();
                try (ResultSet keys = insertStmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        trans.setTransactionId(keys.getInt(1));
                    }
                }
            }

            conn.commit(); 
            return true;
        } catch (SQLException ex) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    System.err.println("Rollback failed: " + rollbackEx.getMessage());
                }
            }
            throw ex;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException closeEx) {
                    System.err.println("Closing connection failed: " + closeEx.getMessage());
                }
            }
        }
    }

    public List<StockTransaction> findAll() throws SQLException {
        List<StockTransaction> list = new ArrayList<>();
        String sql = BASE_SELECT + "ORDER BY t.transaction_date DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        return list;
    }

    public List<StockTransaction> findByProductId(int productId) throws SQLException {
        List<StockTransaction> list = new ArrayList<>();
        String sql = BASE_SELECT + "WHERE t.product_id = ? ORDER BY t.transaction_date DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, productId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        }
        return list;
    }

    public List<StockTransaction> getRecentTransactions(int limit) throws SQLException {
        List<StockTransaction> list = new ArrayList<>();
        String sql = BASE_SELECT + "ORDER BY t.transaction_date DESC LIMIT ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, limit);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        }
        return list;
    }

    private StockTransaction mapRow(ResultSet rs) throws SQLException {
        StockTransaction t = new StockTransaction();
        t.setTransactionId(rs.getInt("transaction_id"));
        t.setProductId(rs.getInt("product_id"));
        t.setTransactionType(rs.getString("transaction_type"));
        t.setQuantity(rs.getInt("quantity"));
        t.setReferenceNo(rs.getString("reference_no"));
        t.setRemarks(rs.getString("remarks"));
        t.setPerformedBy(rs.getInt("performed_by"));
        t.setTransactionDate(rs.getTimestamp("transaction_date"));

        t.setProductName(rs.getString("product_name"));
        t.setSku(rs.getString("sku"));
        t.setPerformedByName(rs.getString("performed_by_name"));
        return t;
    }
}
