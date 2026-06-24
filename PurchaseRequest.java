package com.landlink.model;

/**
 * PurchaseRequest model - represents a buyer's request to purchase a land.
 * Status flows: PENDING → APPROVED or REJECTED
 */
public class PurchaseRequest {

    public static final String STATUS_PENDING = "PENDING";
    public static final String STATUS_APPROVED = "APPROVED";
    public static final String STATUS_REJECTED = "REJECTED";

    private int id;
    private int landId;
    private int buyerId;
    private int sellerId;
    private double amount;
    private String status;
    private String rejectionReason;
    private String agreementDate;
    private String createdAt;

    // Joined display fields
    private String landTitle;
    private String buyerName;
    private String sellerName;
    private String landLocation;

    public PurchaseRequest() {
        this.status = STATUS_PENDING;
    }

    public PurchaseRequest(int landId, int buyerId, int sellerId, double amount) {
        this();
        this.landId = landId;
        this.buyerId = buyerId;
        this.sellerId = sellerId;
        this.amount = amount;
    }

    // Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getLandId() { return landId; }
    public void setLandId(int landId) { this.landId = landId; }

    public int getBuyerId() { return buyerId; }
    public void setBuyerId(int buyerId) { this.buyerId = buyerId; }

    public int getSellerId() { return sellerId; }
    public void setSellerId(int sellerId) { this.sellerId = sellerId; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getRejectionReason() { return rejectionReason; }
    public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }

    public String getAgreementDate() { return agreementDate; }
    public void setAgreementDate(String agreementDate) { this.agreementDate = agreementDate; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getLandTitle() { return landTitle; }
    public void setLandTitle(String landTitle) { this.landTitle = landTitle; }

    public String getBuyerName() { return buyerName; }
    public void setBuyerName(String buyerName) { this.buyerName = buyerName; }

    public String getSellerName() { return sellerName; }
    public void setSellerName(String sellerName) { this.sellerName = sellerName; }

    public String getLandLocation() { return landLocation; }
    public void setLandLocation(String landLocation) { this.landLocation = landLocation; }

    public String getFormattedAmount() {
        return String.format("LKR %,.2f", amount);
    }
}
