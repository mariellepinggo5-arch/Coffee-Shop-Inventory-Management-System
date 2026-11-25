package com.inventory.controller;

import com.inventory.model.Supplier;
import com.inventory.service.SupplierService;
import com.inventory.util.ValidationException;
import com.inventory.view.SupplierPanel;
import com.inventory.view.dialog.SupplierDialog;
import java.sql.SQLException;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class SupplierController {

    private final SupplierService supplierService;
    private SupplierPanel supplierPanel;

    public SupplierController() {
        this.supplierService = new SupplierService();
    }

    public void setSupplierPanel(SupplierPanel panel) {
        this.supplierPanel = panel;
    }

    public void loadSuppliers() {
        try {
            List<Supplier> suppliers = supplierService.getAllSuppliers();
            supplierPanel.displaySuppliers(suppliers);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                supplierPanel,
                "Error loading suppliers: " + e.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    public void openAddSupplierDialog() {
        SupplierDialog dialog = new SupplierDialog(
            SwingUtilities.getWindowAncestor(supplierPanel),
            "Add New Supplier",
            null
        );
        dialog.setVisible(true);

        if (dialog.isSaved()) {
            Supplier newSupplier = dialog.getSupplier();
            try {
                supplierService.addSupplier(newSupplier);
                JOptionPane.showMessageDialog(
                    supplierPanel,
                    "Supplier '" + newSupplier.getSupplierName() + "' added successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
                );
                loadSuppliers();
            } catch (ValidationException ve) {
                JOptionPane.showMessageDialog(supplierPanel, ve.getMessage(), "Validation Notice", JOptionPane.WARNING_MESSAGE);
            } catch (SQLException sqle) {
                JOptionPane.showMessageDialog(supplierPanel, "Database error: " + sqle.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void openEditSupplierDialog(Supplier supplier) {
        if (supplier == null) {
            JOptionPane.showMessageDialog(supplierPanel, "Please select a supplier to edit.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        SupplierDialog dialog = new SupplierDialog(
            SwingUtilities.getWindowAncestor(supplierPanel),
            "Edit Supplier",
            supplier
        );
        dialog.setVisible(true);

        if (dialog.isSaved()) {
            Supplier updated = dialog.getSupplier();
            try {
                supplierService.updateSupplier(updated);
                JOptionPane.showMessageDialog(
                    supplierPanel,
                    "Supplier updated successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
                );
                loadSuppliers();
            } catch (ValidationException ve) {
                JOptionPane.showMessageDialog(supplierPanel, ve.getMessage(), "Validation Notice", JOptionPane.WARNING_MESSAGE);
            } catch (SQLException sqle) {
                JOptionPane.showMessageDialog(supplierPanel, "Database error: " + sqle.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void handleDeleteSupplier(Supplier supplier) {
        if (supplier == null) {
            JOptionPane.showMessageDialog(supplierPanel, "Please select a supplier to delete.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
            supplierPanel,
            "Are you sure you want to delete supplier: '" + supplier.getSupplierName() + "'?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                supplierService.deleteSupplier(supplier.getSupplierId());
                JOptionPane.showMessageDialog(
                    supplierPanel,
                    "Supplier deleted successfully.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
                );
                loadSuppliers();
            } catch (ValidationException ve) {
                JOptionPane.showMessageDialog(supplierPanel, ve.getMessage(), "Constraint Notice", JOptionPane.WARNING_MESSAGE);
            } catch (SQLException sqle) {
                JOptionPane.showMessageDialog(supplierPanel, "Database error: " + sqle.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
