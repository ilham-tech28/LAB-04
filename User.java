package com.landlink.model;

/**
 * User model class demonstrating Encapsulation (OOP Concept).
 * All fields are private with public getters and setters.
 */
public class User {

    // Private fields - Encapsulation
    private int id;
    private String username;
    private String password;
    private String fullName;
    private String email;
    private String phone;
    private String role; // "ADMIN" or "USER"
    private boolean active;
    private String createdAt;

    // Default constructor
    public User() {
        this.active = true;
        this.role = "USER";
    }

    // Parameterized constructor
    public User(String username, String password, String fullName, String email, String phone) {
        this();
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
    }

    // Full constructor
    public User(int id, String username, String password, String fullName,
                String email, String phone, String role, boolean active, String createdAt) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.role = role;
        this.active = active;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    // Polymorphism - toString() override
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", fullName='" + fullName + '\'' +
                ", role='" + role + '\'' +
                ", active=" + active +
                '}';
    }

    // Check if user is admin
    public boolean isAdmin() {
        return "ADMIN".equalsIgnoreCase(this.role);
    }
}
