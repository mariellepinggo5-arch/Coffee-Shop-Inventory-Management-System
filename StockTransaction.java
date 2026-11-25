package com.inventory.model;

import java.sql.Timestamp;

public class StockTransaction {
    private int transactionId;
    private int productId;
    private String transactionType; 
    private int quantity;
    private String referenceNo;
    private String remarks;
    private int performedBy;
    private Timestamp transactionDate;

    private String productName;
    private String sku;
    private String performedByName;

    public StockTransaction() {}

    public StockTransaction(int transactionId, int productId, String transactionType, int quantity,
                            String referenceNo, String remarks, int performedBy) {
        this.transactionId = transactionId;
        this.productId = productId;
        this.transactionType = transactionType;
        this.quantity = quantity;
        this.referenceNo = referenceNo;
        this.remarks = remarks;
        this.performedBy = performedBy;
    }

    public int getTransactionId() { return transactionId; }
    public void setTransactionId(int transactionId) { this.transactionId = transactionId; }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public String getTransactionType() { return transactionType; }
    public void setTransactionType(String transactionType) { this.transactionType = transactionType; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public String getReferenceNo() { return referenceNo; }
    public void setReferenceNo(String referenceNo) { this.referenceNo = referenceNo; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }

    public int getPerformedBy() { return performedBy; }
    public void setPerformedBy(int performedBy) { this.performedBy = performedBy; }

    public Timestamp getTransactionDate() { return transactionDate; }
    public void setTransactionDate(Timestamp transactionDate) { this.transactionDate = transactionDate; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }

    public String getPerformedByName() { return performedByName; }
    public void setPerformedByName(String performedByName) { this.performedByName = performedByName; }
}
