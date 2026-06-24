package com.landlink.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Land model class demonstrating Encapsulation and Collections (OOP Concepts).
 * Represents a land property listing.
 */
public class Land {

    // Land type constants
    public static final String TYPE_RESIDENTIAL = "Residential";
    public static final String TYPE_COMMERCIAL = "Commercial";
    public static final String TYPE_AGRICULTURAL = "Agricultural";

    // Status constants
    public static final String STATUS_PENDING = "PENDING";
    public static final String STATUS_APPROVED = "APPROVED";
    public static final String STATUS_SOLD = "SOLD";
    public static final String STATUS_REJECTED = "REJECTED";
    public static final String STATUS_RESERVED = "RESERVED";

    // Private fields - Encapsulation
    private int id;
    private String title;
    private String location;
    private double landSize; // in perches
    private double price;    // in LKR
    private String description;
    private String landType;
    private String status;
    private int sellerId;
    private String sellerName;
    private String contactNumber;
    private List<String> imagePaths; // Collection of image file paths
    private String createdAt;

    // Default constructor
    public Land() {
        this.status = STATUS_PENDING;
        this.imagePaths = new ArrayList<>();
    }

    // Parameterized constructor
    public Land(String title, String location, double landSize, double price,
                String description, String landType, int sellerId, String contactNumber) {
        this();
        this.title = title;
        this.location = location;
        this.landSize = landSize;
        this.price = price;
        this.description = description;
        this.landType = landType;
        this.sellerId = sellerId;
        this.contactNumber = contactNumber;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public double getLandSize() {
        return landSize;
    }

    public void setLandSize(double landSize) {
        this.landSize = landSize;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLandType() {
        return landType;
    }

    public void setLandType(String landType) {
        this.landType = landType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getSellerId() {
        return sellerId;
    }

    public void setSellerId(int sellerId) {
        this.sellerId = sellerId;
    }

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public List<String> getImagePaths() {
        return imagePaths;
    }

    public void setImagePaths(List<String> imagePaths) {
        this.imagePaths = imagePaths;
    }

    public void addImagePath(String path) {
        if (this.imagePaths == null) {
            this.imagePaths = new ArrayList<>();
        }
        this.imagePaths.add(path);
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    // Get formatted price string
    public String getFormattedPrice() {
        return String.format("LKR %,.2f", price);
    }

    // Get formatted size string
    public String getFormattedSize() {
        return String.format("%.2f perches", landSize);
    }

    // Polymorphism - toString() override
    @Override
    public String toString() {
        return "Land{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", location='" + location + '\'' +
                ", price=" + getFormattedPrice() +
                ", status='" + status + '\'' +
                '}';
    }
}
