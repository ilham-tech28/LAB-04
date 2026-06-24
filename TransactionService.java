package com.landlink.service;

import com.landlink.dao.LandDAO;
import com.landlink.dao.TransactionDAO;
import com.landlink.model.Land;
import com.landlink.model.Transaction;

import java.util.List;

/**
 * TransactionService - Business logic for purchase transactions.
 * Handles the purchase flow including validation.
 */
public class TransactionService {

    private final TransactionDAO transactionDAO;
    private final LandDAO landDAO;

    public TransactionService() {
        this.transactionDAO = new TransactionDAO();
        this.landDAO = new LandDAO();
    }

    /**
     * Process a land purchase.
     * @throws IllegalArgumentException if validation fails
     * @throws IllegalStateException if land is not available
     */
    public boolean purchaseLand(int landId, int buyerId) throws IllegalArgumentException, IllegalStateException {
        // Get the land
        Land land = landDAO.findById(landId);
        if (land == null) {
            throw new IllegalArgumentException("Land not found!");
        }
        if (!Land.STATUS_APPROVED.equals(land.getStatus())) {
            throw new IllegalStateException("This land is not available for purchase!");
        }
        if (land.getSellerId() == buyerId) {
            throw new IllegalArgumentException("You cannot buy your own land!");
        }

        // Create transaction
        Transaction transaction = new Transaction(landId, buyerId, land.getSellerId(), land.getPrice());
        boolean success = transactionDAO.createTransaction(transaction);

        if (success) {
            // Mark land as sold
            landDAO.updateLandStatus(landId, Land.STATUS_SOLD);
        }

        return success;
    }

    // Get all transactions
    public List<Transaction> getAllTransactions() {
        return transactionDAO.getAllTransactions();
    }

    // Get buyer's transactions
    public List<Transaction> getMyPurchases(int buyerId) {
        return transactionDAO.getTransactionsByBuyer(buyerId);
    }

    // Get seller's transactions
    public List<Transaction> getMySales(int sellerId) {
        return transactionDAO.getTransactionsBySeller(sellerId);
    }

    // Count all transactions
    public int countAll() {
        return transactionDAO.countAll();
    }

    // Get total sales amount
    public double getTotalSalesAmount() {
        return transactionDAO.getTotalSalesAmount();
    }
}
