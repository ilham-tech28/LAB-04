package com.landlink.dao;

import com.landlink.model.Transaction;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * TransactionDAO - Data Access Object for Transaction operations.
 * Demonstrates try-catch exception handling for all database operations.
 */
public class TransactionDAO {

    private final Connection connection;

    public TransactionDAO() {
        this.connection = DatabaseHelper.getInstance().getConnection();
    }

    // Create a new transaction
    public boolean createTransaction(Transaction transaction) {
        String sql = "INSERT INTO transactions (land_id, buyer_id, seller_id, amount, status) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, transaction.getLandId());
            pstmt.setInt(2, transaction.getBuyerId());
            pstmt.setInt(3, transaction.getSellerId());
            pstmt.setDouble(4, transaction.getAmount());
            pstmt.setString(5, transaction.getStatus());
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("❌ Error creating transaction: " + e.getMessage());
            return false;
        }
    }

    // Delete a transaction by land ID
    public boolean deleteTransactionByLandId(int landId) {
        String sql = "DELETE FROM transactions WHERE land_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, landId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Error deleting transaction: " + e.getMessage());
            return false;
        }
    }

    // Get all transactions
    public List<Transaction> getAllTransactions() {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT t.*, l.title as land_title, " +
                     "b.full_name as buyer_name, s.full_name as seller_name " +
                     "FROM transactions t " +
                     "JOIN lands l ON t.land_id = l.id " +
                     "JOIN users b ON t.buyer_id = b.id " +
                     "JOIN users s ON t.seller_id = s.id " +
                     "ORDER BY t.transaction_date DESC";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                transactions.add(mapTransaction(rs));
            }
        } catch (SQLException e) {
            System.err.println("❌ Error getting all transactions: " + e.getMessage());
        }
        return transactions;
    }

    // Get transactions by buyer
    public List<Transaction> getTransactionsByBuyer(int buyerId) {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT t.*, l.title as land_title, " +
                     "b.full_name as buyer_name, s.full_name as seller_name " +
                     "FROM transactions t " +
                     "JOIN lands l ON t.land_id = l.id " +
                     "JOIN users b ON t.buyer_id = b.id " +
                     "JOIN users s ON t.seller_id = s.id " +
                     "WHERE t.buyer_id = ? ORDER BY t.transaction_date DESC";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, buyerId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                transactions.add(mapTransaction(rs));
            }
        } catch (SQLException e) {
            System.err.println("❌ Error getting buyer's transactions: " + e.getMessage());
        }
        return transactions;
    }

    // Get transactions by seller
    public List<Transaction> getTransactionsBySeller(int sellerId) {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT t.*, l.title as land_title, " +
                     "b.full_name as buyer_name, s.full_name as seller_name " +
                     "FROM transactions t " +
                     "JOIN lands l ON t.land_id = l.id " +
                     "JOIN users b ON t.buyer_id = b.id " +
                     "JOIN users s ON t.seller_id = s.id " +
                     "WHERE t.seller_id = ? ORDER BY t.transaction_date DESC";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, sellerId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                transactions.add(mapTransaction(rs));
            }
        } catch (SQLException e) {
            System.err.println("❌ Error getting seller's transactions: " + e.getMessage());
        }
        return transactions;
    }

    // Count all transactions
    public int countAll() {
        String sql = "SELECT COUNT(*) FROM transactions";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("❌ Error counting transactions: " + e.getMessage());
        }
        return 0;
    }

    // Get total sales amount
    public double getTotalSalesAmount() {
        String sql = "SELECT COALESCE(SUM(amount), 0) FROM transactions WHERE status = 'COMPLETED'";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (SQLException e) {
            System.err.println("❌ Error getting total sales: " + e.getMessage());
        }
        return 0;
    }

    // Map ResultSet to Transaction object
    private Transaction mapTransaction(ResultSet rs) throws SQLException {
        Transaction t = new Transaction();
        t.setId(rs.getInt("id"));
        t.setLandId(rs.getInt("land_id"));
        t.setBuyerId(rs.getInt("buyer_id"));
        t.setSellerId(rs.getInt("seller_id"));
        t.setAmount(rs.getDouble("amount"));
        t.setStatus(rs.getString("status"));
        t.setTransactionDate(rs.getString("transaction_date"));
        t.setLandTitle(rs.getString("land_title"));
        t.setBuyerName(rs.getString("buyer_name"));
        t.setSellerName(rs.getString("seller_name"));
        return t;
    }
}
