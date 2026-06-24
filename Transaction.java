package com.landlink.model;

/**
 * Transaction model class demonstrating Encapsulation (OOP Concept).
 * Records a land purchase transaction between a buyer and seller.
 */
public class Transaction {

    // Status constants
    public static final String STATUS_COMPLETED = "COMPLETED";
    public static final String STATUS_PENDING = "PENDING";
    public static final String STATUS_CANCELLED = "CANCELLED";

    // Private fields - Encapsulation
    private int id;
    private int landId;
    private int buyerId;
    private int sellerId;
    private double amount;
    private String status;
    private String transactionDate;

    // Related object names (for display)
    private String landTitle;
    private String buyerName;
    private String sellerName;

    // Default constructor
    public Transaction() {
        this.status = STATUS_COMPLETED;
    }

    // Parameterized constructor
    public Transaction(int landId, int buyerId, int sellerId, double amount) {
        this();
        this.landId = landId;
        this.buyerId = buyerId;
        this.sellerId = sellerId;
        this.amount = amount;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getLandId() {
        return landId;
    }

    public void setLandId(int landId) {
        this.landId = landId;
    }

    public int getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(int buyerId) {
        this.buyerId = buyerId;
    }

    public int getSellerId() {
        return sellerId;
    }

    public void setSellerId(int sellerId) {
        this.sellerId = sellerId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(String transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getLandTitle() {
        return landTitle;
    }

    public void setLandTitle(String landTitle) {
        this.landTitle = landTitle;
    }

    public String getBuyerName() {
        return buyerName;
    }

    public void setBuyerName(String buyerName) {
        this.buyerName = buyerName;
    }

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    // Get formatted amount
    public String getFormattedAmount() {
        return String.format("LKR %,.2f", amount);
    }

    // Polymorphism - toString() override
    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", landId=" + landId +
                ", amount=" + getFormattedAmount() +
                ", status='" + status + '\'' +
                ", date='" + transactionDate + '\'' +
                '}';
    }
}
