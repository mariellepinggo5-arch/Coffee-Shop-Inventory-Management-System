package com.inventory.controller;

import com.inventory.model.Product;
import com.inventory.model.StockTransaction;
import com.inventory.model.User;
import com.inventory.service.AuthService;
import com.inventory.service.ProductService;
import com.inventory.service.StockService;
import com.inventory.util.ValidationException;
import com.inventory.view.StockTransactionPanel;
import com.inventory.view.dialog.StockAdjustmentDialog;
import java.awt.Component;
import java.sql.SQLException;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class StockController {

    private final StockService stockService;
    private final ProductService productService;
    private StockTransactionPanel transactionPanel;

    public StockController() {
        this.stockService = new StockService();
        this.productService = new ProductService();
    }

    public void setTransactionPanel(StockTransactionPanel panel) {
        this.transactionPanel = panel;
    }

    public void loadTransactions() {
        try {
            List<StockTransaction> transactions = stockService.getAllTransactions();
            if (transactionPanel != null) {
                transactionPanel.displayTransactions(transactions);
            }
        } catch (SQLException e) {
            if (transactionPanel != null) {
                JOptionPane.showMessageDialog(
                    transactionPanel,
                    "Error loading stock transaction logs: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    public void openStockDialog(Component parentComponent, Product product, String defaultType, Runnable onSuccessCallback) {
        if (product == null) {
            JOptionPane.showMessageDialog(
                parentComponent,
                "Please select a product first.",
                "Selection Required",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        User user = AuthService.getCurrentUser();
        int userId = (user != null) ? user.getUserId() : 1;

        StockAdjustmentDialog dialog = new StockAdjustmentDialog(
            SwingUtilities.getWindowAncestor(parentComponent),
            product,
            defaultType
        );
        dialog.setVisible(true);

        if (dialog.isConfirmed()) {
            String type = dialog.getTransactionType();
            int qty = dialog.getQuantity();
            String ref = dialog.getReferenceNumber();
            String remarks = dialog.getRemarks();

            try {
                if ("INBOUND".equalsIgnoreCase(type)) {
                    stockService.processInbound(product.getProductId(), qty, ref, remarks, userId);
                    JOptionPane.showMessageDialog(
                        parentComponent,
                        "Successfully received " + qty + " " + product.getUnitOfMeasure() + "(s) for " + product.getProductName() + ".",
                        "Restock Successful",
                        JOptionPane.INFORMATION_MESSAGE
                    );
                } else if ("OUTBOUND".equalsIgnoreCase(type)) {
                    stockService.processOutbound(product.getProductId(), qty, ref, remarks, userId);
                    JOptionPane.showMessageDialog(
                        parentComponent,
                        "Successfully dispatched " + qty + " " + product.getUnitOfMeasure() + "(s) from inventory.",
                        "Dispatch Successful",
                        JOptionPane.INFORMATION_MESSAGE
                    );
                } else if ("ADJUSTMENT".equalsIgnoreCase(type)) {
                    stockService.processAdjustment(product.getProductId(), qty, ref, remarks, userId);
                    JOptionPane.showMessageDialog(
                        parentComponent,
                        "Stock quantity updated to " + qty + " " + product.getUnitOfMeasure() + "(s).",
                        "Inventory Adjusted",
                        JOptionPane.INFORMATION_MESSAGE
                    );
                }

                if (onSuccessCallback != null) {
                    onSuccessCallback.run();
                }
                loadTransactions();

            } catch (ValidationException ve) {
                JOptionPane.showMessageDialog(parentComponent, ve.getMessage(), "Validation Notice", JOptionPane.WARNING_MESSAGE);
            } catch (SQLException sqle) {
                JOptionPane.showMessageDialog(parentComponent, "Database error: " + sqle.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
