package com.inventory.service;

import com.inventory.dao.SupplierDAO;
import com.inventory.model.Supplier;
import com.inventory.util.ValidationException;
import java.sql.SQLException;
import java.util.List;

public class SupplierService {

    private final SupplierDAO supplierDAO;

    public SupplierService() {
        this.supplierDAO = new SupplierDAO();
    }

    public List<Supplier> getAllSuppliers() throws SQLException {
        return supplierDAO.findAll();
    }

    public Supplier getSupplierById(int id) throws SQLException {
        return supplierDAO.findById(id);
    }

    public void addSupplier(Supplier supplier) throws ValidationException, SQLException {
        validateSupplier(supplier);
        boolean success = supplierDAO.create(supplier);
        if (!success) {
            throw new SQLException("Failed to save new supplier into the database.");
        }
    }

    public void updateSupplier(Supplier supplier) throws ValidationException, SQLException {
        if (supplier.getSupplierId() <= 0) {
            throw new ValidationException("Invalid supplier ID for update.");
        }
        validateSupplier(supplier);
        boolean success = supplierDAO.update(supplier);
        if (!success) {
            throw new SQLException("Failed to update supplier: record not found.");
        }
    }

    public void deleteSupplier(int supplierId) throws ValidationException, SQLException {
        if (supplierId <= 0) {
            throw new ValidationException("Invalid supplier ID.");
        }

        int count = supplierDAO.getProductCount(supplierId);
        if (count > 0) {
            throw new ValidationException(
                "Cannot delete supplier: " + count + " product(s) are supplied by this vendor.\n" +
                "Please reassign or remove those products first."
            );
        }

        boolean success = supplierDAO.delete(supplierId);
        if (!success) {
            throw new SQLException("Failed to delete supplier: record not found.");
        }
    }

    private void validateSupplier(Supplier supplier) throws ValidationException {
        if (supplier == null) {
            throw new ValidationException("Supplier details cannot be null.");
        }
        if (supplier.getSupplierName() == null || supplier.getSupplierName().trim().isEmpty()) {
            throw new ValidationException("Supplier Name is required.");
        }
        if (supplier.getEmail() != null && !supplier.getEmail().trim().isEmpty()) {
            if (!supplier.getEmail().contains("@") || !supplier.getEmail().contains(".")) {
                throw new ValidationException("Invalid email format (e.g., vendor@example.com).");
            }
        }
    }
}
