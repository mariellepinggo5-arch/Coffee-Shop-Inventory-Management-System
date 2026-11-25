package com.inventory.service;

import com.inventory.dao.ProductDAO;
import com.inventory.dao.StockTransactionDAO;
import com.inventory.model.Product;
import com.inventory.model.StockTransaction;
import com.inventory.util.ValidationException;
import java.sql.SQLException;
import java.util.List;

public class StockService {

    private final ProductDAO productDAO;
    private final StockTransactionDAO transactionDAO;

    public StockService() {
        this.productDAO = new ProductDAO();
        this.transactionDAO = new StockTransactionDAO();
    }

    public List<StockTransaction> getAllTransactions() throws SQLException {
        return transactionDAO.findAll();
    }

    public List<StockTransaction> getProductTransactions(int productId) throws SQLException {
        return transactionDAO.findByProductId(productId);
    }

    public List<StockTransaction> getRecentTransactions(int limit) throws SQLException {
        return transactionDAO.getRecentTransactions(limit);
    }

    public void processInbound(int productId, int quantity, String referenceNo, String remarks, int userId)
            throws ValidationException, SQLException {
        if (quantity <= 0) {
            throw new ValidationException("Inbound restock quantity must be greater than zero.");
        }

        Product product = productDAO.findById(productId);
        if (product == null) {
            throw new ValidationException("Product not found (ID: " + productId + ").");
        }

        int updatedStock = product.getQuantityInStock() + quantity;

        StockTransaction trans = new StockTransaction();
        trans.setProductId(productId);
        trans.setTransactionType("INBOUND");
        trans.setQuantity(quantity);
        trans.setReferenceNo(referenceNo != null && !referenceNo.trim().isEmpty() ? referenceNo.trim() : "RESTOCK");
        trans.setRemarks(remarks != null ? remarks.trim() : "Stock received into warehouse");
        trans.setPerformedBy(userId);

        transactionDAO.recordTransaction(trans, updatedStock);
    }

    public void processOutbound(int productId, int quantity, String referenceNo, String remarks, int userId)
            throws ValidationException, SQLException {
        if (quantity <= 0) {
            throw new ValidationException("Outbound dispatch quantity must be greater than zero.");
        }

        Product product = productDAO.findById(productId);
        if (product == null) {
            throw new ValidationException("Product not found (ID: " + productId + ").");
        }

        if (product.getQuantityInStock() < quantity) {
            throw new ValidationException(
                "Insufficient stock available!\n" +
                "Current stock: " + product.getQuantityInStock() + " " + product.getUnitOfMeasure() + "(s).\n" +
                "Requested outbound quantity: " + quantity + "."
            );
        }

        int updatedStock = product.getQuantityInStock() - quantity;

        StockTransaction trans = new StockTransaction();
        trans.setProductId(productId);
        trans.setTransactionType("OUTBOUND");
        trans.setQuantity(quantity);
        trans.setReferenceNo(referenceNo != null && !referenceNo.trim().isEmpty() ? referenceNo.trim() : "DISPATCH");
        trans.setRemarks(remarks != null ? remarks.trim() : "Stock dispatched / sold");
        trans.setPerformedBy(userId);

        transactionDAO.recordTransaction(trans, updatedStock);
    }

    public void processAdjustment(int productId, int newQuantity, String referenceNo, String remarks, int userId)
            throws ValidationException, SQLException {
        if (newQuantity < 0) {
            throw new ValidationException("Adjusted stock count cannot be negative.");
        }

        Product product = productDAO.findById(productId);
        if (product == null) {
            throw new ValidationException("Product not found (ID: " + productId + ").");
        }

        int difference = Math.abs(newQuantity - product.getQuantityInStock());

        StockTransaction trans = new StockTransaction();
        trans.setProductId(productId);
        trans.setTransactionType("ADJUSTMENT");
        trans.setQuantity(difference);
        trans.setReferenceNo(referenceNo != null && !referenceNo.trim().isEmpty() ? referenceNo.trim() : "AUDIT-ADJ");
        trans.setRemarks(
            (remarks != null && !remarks.trim().isEmpty() ? remarks.trim() + " " : "") +
            "(Stock adjusted from " + product.getQuantityInStock() + " to " + newQuantity + ")"
        );
        trans.setPerformedBy(userId);

        transactionDAO.recordTransaction(trans, newQuantity);
    }
}
