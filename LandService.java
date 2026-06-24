package com.landlink.service;

import com.landlink.dao.LandDAO;
import com.landlink.model.Land;

import java.util.List;

/**
 * LandService - Business logic for land operations.
 * Validates land data before database operations.
 */
public class LandService {

    private final LandDAO landDAO;
    private final com.landlink.dao.MessageDAO messageDAO;
    private final com.landlink.dao.PurchaseRequestDAO requestDAO;

    public LandService() {
        this.landDAO = new LandDAO();
        this.messageDAO = new com.landlink.dao.MessageDAO();
        this.requestDAO = new com.landlink.dao.PurchaseRequestDAO();
    }

    /**
     * Add a new land listing.
     * @throws IllegalArgumentException if validation fails
     */
    public int addLand(Land land) throws IllegalArgumentException {
        // Validate
        if (land.getTitle() == null || land.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Land title cannot be empty!");
        }
        if (land.getLocation() == null || land.getLocation().trim().isEmpty()) {
            throw new IllegalArgumentException("Location cannot be empty!");
        }
        if (land.getLandSize() <= 0) {
            throw new IllegalArgumentException("Land size must be greater than 0!");
        }
        if (land.getPrice() <= 0) {
            throw new IllegalArgumentException("Price must be greater than 0!");
        }
        if (land.getContactNumber() == null || land.getContactNumber().trim().isEmpty()) {
            throw new IllegalArgumentException("Contact number cannot be empty!");
        }

        return landDAO.createLand(land);
    }

    // Get all approved lands for browsing
    public List<Land> getApprovedLands() {
        return landDAO.getBrowseableLands();
    }

    // Get all lands (admin view)
    public List<Land> getAllLands() {
        return landDAO.getAllLands();
    }

    // Get lands by seller
    public List<Land> getMyLands(int sellerId) {
        return landDAO.getLandsBySeller(sellerId);
    }

    // Get land by ID
    public Land getLandById(int id) {
        return landDAO.findById(id);
    }

    // Approve a land listing
    public boolean approveLand(int landId) {
        return landDAO.updateLandStatus(landId, Land.STATUS_APPROVED);
    }

    // Reject a land listing
    public boolean rejectLand(int landId) {
        boolean updated = landDAO.updateLandStatus(landId, Land.STATUS_REJECTED);
        if (updated) {
            Land land = landDAO.findById(landId);
            if (land != null) {
                // Determine Admin ID
                int adminId = 0;
                com.landlink.model.User currentUser = com.landlink.service.AuthService.getCurrentUser();
                if (currentUser != null && currentUser.isAdmin()) {
                    adminId = currentUser.getId();
                } else {
                    // Fallback to finding an admin from DB if not logged in context
                    adminId = new com.landlink.dao.UserDAO().getAllUsers().stream()
                        .filter(u -> u.isAdmin()).findFirst().map(u -> u.getId()).orElse(0);
                }
                
                String subject = "❌ Land Listing Rejected — " + land.getTitle();
                String body = "Dear " + land.getSellerName() + ",\n\n" +
                    "Your recent land listing has been rejected due to unclear data or missing document submission.\n\n" +
                    "🏡 Property: " + land.getTitle() + "\n" +
                    "📍 Location: " + land.getLocation() + "\n\n" +
                    "Please review the details and resubmit the listing with clear and accurate information.\n\n" +
                    "— Land_Link Team";
                messageDAO.sendMessage(new com.landlink.model.Message(land.getSellerId(), adminId, subject, body));

                // Also cancel any existing purchase requests for this land and notify buyers
                List<com.landlink.model.PurchaseRequest> requests = requestDAO.getByLand(landId);
                for (com.landlink.model.PurchaseRequest req : requests) {
                    if (com.landlink.model.PurchaseRequest.STATUS_PENDING.equals(req.getStatus()) || 
                        com.landlink.model.PurchaseRequest.STATUS_APPROVED.equals(req.getStatus())) {
                        
                        requestDAO.updateStatus(req.getId(), com.landlink.model.PurchaseRequest.STATUS_REJECTED, "Land listing rejected.", null);
                        
                        String buyerSubject = "⚠️ Purchase Request Cancelled — " + land.getTitle();
                        String buyerBody = "Dear " + req.getBuyerName() + ",\n\n" +
                            "We regret to inform you that the property you requested to purchase is currently unavailable as its listing has been rejected or removed by the admin.\n\n" +
                            "🏡 Property: " + land.getTitle() + "\n" +
                            "📍 Location: " + land.getLocation() + "\n\n" +
                            "If you had a scheduled meeting or agreement date, please note that it is now CANCELLED.\n\n" +
                            "You may browse other available properties on Land_Link.\n\n" +
                            "— Land_Link Team";
                        messageDAO.sendMessage(new com.landlink.model.Message(req.getBuyerId(), adminId, buyerSubject, buyerBody));
                    }
                }
            }
        }
        return updated;
    }

    // Mark land as sold
    public boolean markAsSold(int landId) {
        return landDAO.updateLandStatus(landId, Land.STATUS_SOLD);
    }

    // Delete a land listing
    public boolean deleteLand(int landId) {
        return landDAO.deleteLand(landId);
    }

    // Search lands with filters
    public List<Land> searchLands(String keyword, String landType, Double minPrice, Double maxPrice) {
        return landDAO.searchLands(keyword, landType, minPrice, maxPrice);
    }

    // Get counts
    public int countByStatus(String status) {
        return landDAO.countByStatus(status);
    }

    public int countAll() {
        return landDAO.countAll();
    }
}
