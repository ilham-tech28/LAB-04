package com.landlink.service;

import com.landlink.dao.UserDAO;
import com.landlink.model.User;

/**
 * AuthService - Handles authentication logic.
 * Validates credentials and manages login state.
 */
public class AuthService {

    private final UserDAO userDAO;
    private static User currentUser; // Currently logged-in user

    public AuthService() {
        this.userDAO = new UserDAO();
    }

    /**
     * Attempt login with username and password.
     * @return User object if login successful, null otherwise
     * @throws IllegalArgumentException if fields are empty
     * @throws SecurityException if account is deactivated
     */
    public User login(String username, String password) throws IllegalArgumentException, SecurityException {
        // Validate input
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty!");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty!");
        }

        // Find user
        User user = userDAO.findByUsernameAndPassword(username.trim(), password);
        if (user == null) {
            return null; // Invalid credentials
        }

        // Check if active
        if (!user.isActive()) {
            throw new SecurityException("Your account has been deactivated. Please contact admin.");
        }

        // Set current user
        currentUser = user;
        return user;
    }

    /**
     * Register a new user account.
     * @throws IllegalArgumentException if validation fails
     */
    public boolean register(User user) throws IllegalArgumentException {
        // Validate
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty!");
        }
        if (user.getPassword() == null || user.getPassword().length() < 4) {
            throw new IllegalArgumentException("Password must be at least 4 characters!");
        }
        if (user.getFullName() == null || user.getFullName().trim().isEmpty()) {
            throw new IllegalArgumentException("Full name cannot be empty!");
        }
        if (userDAO.usernameExists(user.getUsername().trim())) {
            throw new IllegalArgumentException("Username '" + user.getUsername() + "' already exists!");
        }

        return userDAO.createUser(user);
    }

    // Get currently logged-in user
    public static User getCurrentUser() {
        return currentUser;
    }

    // Logout
    public static void logout() {
        currentUser = null;
    }

    // Check if a user is logged in
    public static boolean isLoggedIn() {
        return currentUser != null;
    }
}
