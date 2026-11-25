package com.inventory.service;

import com.inventory.dao.UserDAO;
import com.inventory.model.User;
import com.inventory.util.ValidationException;
import java.sql.SQLException;

public class AuthService {

    private final UserDAO userDAO;
    private static User currentUser;

    public AuthService() {
        this.userDAO = new UserDAO();
    }

    public User login(String username, String password) throws ValidationException, SQLException {
        if (username == null || username.trim().isEmpty()) {
            throw new ValidationException("Username cannot be empty.");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new ValidationException("Password cannot be empty.");
        }

        User user = userDAO.authenticate(username.trim(), password);
        if (user == null) {
            throw new ValidationException("Invalid username or password, or account is inactive.");
        }

        currentUser = user;
        return user;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    public static void logout() {
        currentUser = null;
    }

    public static boolean isLoggedIn() {
        return currentUser != null;
    }

    public static boolean isAdmin() {
        return currentUser != null && currentUser.isAdmin();
    }
}
