package com.landlink.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * DatabaseHelper - Manages SQLite database connection and table creation.
 * Demonstrates try-catch exception handling throughout.
 */
public class DatabaseHelper {

    private static final String DB_URL = "jdbc:sqlite:landlink.db";
    private static DatabaseHelper instance; // Singleton pattern
    private Connection connection;

    // Private constructor - Singleton pattern
    private DatabaseHelper() {
        try {
            // Load SQLite JDBC driver
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection(DB_URL);
            System.out.println("✅ Database connected successfully!");
            createTables();
            insertDefaultAdmin();
        } catch (ClassNotFoundException e) {
            System.err.println("❌ SQLite JDBC Driver not found: " + e.getMessage());
            throw new RuntimeException("Database driver not found", e);
        } catch (SQLException e) {
            System.err.println("❌ Database connection failed: " + e.getMessage());
            throw new RuntimeException("Cannot connect to database", e);
        }
    }

    // Singleton getInstance
    public static synchronized DatabaseHelper getInstance() {
        if (instance == null) {
            instance = new DatabaseHelper();
        }
        return instance;
    }

    // Get database connection
    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(DB_URL);
            }
        } catch (SQLException e) {
            System.err.println("❌ Error reconnecting to database: " + e.getMessage());
        }
        return connection;
    }

    // Create all tables
    private void createTables() {
        try (Statement stmt = connection.createStatement()) {

            // Users table
            stmt.execute(
                "CREATE TABLE IF NOT EXISTS users (" +
                "   id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "   username TEXT UNIQUE NOT NULL," +
                "   password TEXT NOT NULL," +
                "   full_name TEXT NOT NULL," +
                "   email TEXT," +
                "   phone TEXT," +
                "   role TEXT DEFAULT 'USER'," +
                "   active INTEGER DEFAULT 1," +
                "   created_at DATETIME DEFAULT CURRENT_TIMESTAMP" +
                ")"
            );

            // Lands table
            stmt.execute(
                "CREATE TABLE IF NOT EXISTS lands (" +
                "   id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "   title TEXT NOT NULL," +
                "   location TEXT NOT NULL," +
                "   land_size REAL NOT NULL," +
                "   price REAL NOT NULL," +
                "   description TEXT," +
                "   land_type TEXT DEFAULT 'Residential'," +
                "   status TEXT DEFAULT 'PENDING'," +
                "   seller_id INTEGER NOT NULL," +
                "   contact_number TEXT," +
                "   created_at DATETIME DEFAULT CURRENT_TIMESTAMP," +
                "   FOREIGN KEY (seller_id) REFERENCES users(id)" +
                ")"
            );

            // Land images table
            stmt.execute(
                "CREATE TABLE IF NOT EXISTS land_images (" +
                "   id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "   land_id INTEGER NOT NULL," +
                "   image_path TEXT NOT NULL," +
                "   FOREIGN KEY (land_id) REFERENCES lands(id) ON DELETE CASCADE" +
                ")"
            );

            // Transactions table
            stmt.execute(
                "CREATE TABLE IF NOT EXISTS transactions (" +
                "   id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "   land_id INTEGER NOT NULL," +
                "   buyer_id INTEGER NOT NULL," +
                "   seller_id INTEGER NOT NULL," +
                "   amount REAL NOT NULL," +
                "   status TEXT DEFAULT 'COMPLETED'," +
                "   transaction_date DATETIME DEFAULT CURRENT_TIMESTAMP," +
                "   FOREIGN KEY (land_id) REFERENCES lands(id)," +
                "   FOREIGN KEY (buyer_id) REFERENCES users(id)," +
                "   FOREIGN KEY (seller_id) REFERENCES users(id)" +
                ")"
            );

            // Purchase Requests table (pending admin approval)
            stmt.execute(
                "CREATE TABLE IF NOT EXISTS purchase_requests (" +
                "   id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "   land_id INTEGER NOT NULL," +
                "   buyer_id INTEGER NOT NULL," +
                "   seller_id INTEGER NOT NULL," +
                "   amount REAL NOT NULL," +
                "   status TEXT DEFAULT 'PENDING'," +
                "   rejection_reason TEXT," +
                "   agreement_date TEXT," +
                "   created_at DATETIME DEFAULT CURRENT_TIMESTAMP," +
                "   FOREIGN KEY (land_id) REFERENCES lands(id)," +
                "   FOREIGN KEY (buyer_id) REFERENCES users(id)," +
                "   FOREIGN KEY (seller_id) REFERENCES users(id)" +
                ")"
            );

            // Messages (Inbox) table
            stmt.execute(
                "CREATE TABLE IF NOT EXISTS messages (" +
                "   id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "   recipient_id INTEGER NOT NULL," +
                "   sender_id INTEGER DEFAULT 0," +
                "   subject TEXT NOT NULL," +
                "   body TEXT NOT NULL," +
                "   is_read INTEGER DEFAULT 0," +
                "   sent_at DATETIME DEFAULT CURRENT_TIMESTAMP," +
                "   FOREIGN KEY (recipient_id) REFERENCES users(id)" +
                ")"
            );

            // Migration: Add sender_id to existing messages table
            try {
                stmt.execute("ALTER TABLE messages ADD COLUMN sender_id INTEGER DEFAULT 0");
                System.out.println("✅ Migration: added sender_id to messages table.");
            } catch (SQLException e) {
                // Ignore if column already exists
            }

            System.out.println("✅ All tables created successfully!");

        } catch (SQLException e) {
            System.err.println("❌ Error creating tables: " + e.getMessage());
        }
    }

    // Insert default admin account
    private void insertDefaultAdmin() {
        String sql = "INSERT OR IGNORE INTO users (username, password, full_name, email, phone, role) " +
                     "VALUES ('admin', 'admin123', 'System Administrator', 'admin@landlink.com', '0771234567', 'ADMIN')";
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
            System.out.println("✅ Default admin account ready (admin / admin123)");
        } catch (SQLException e) {
            System.err.println("❌ Error inserting default admin: " + e.getMessage());
        }
    }

    // Close connection
    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("✅ Database connection closed.");
            }
        } catch (SQLException e) {
            System.err.println("❌ Error closing database: " + e.getMessage());
        }
    }
}
