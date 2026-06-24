package com.landlink.service;

import com.landlink.dao.LandDAO;
import com.landlink.dao.MessageDAO;
import com.landlink.dao.PurchaseRequestDAO;
import com.landlink.dao.TransactionDAO;
import com.landlink.model.Land;
import com.landlink.model.Message;
import com.landlink.model.PurchaseRequest;
import com.landlink.model.Transaction;

import java.util.List;

/**
 * PurchaseRequestService - Business logic for the buy request workflow.
 * Handles submit → admin approve/reject → send messages → create transaction.
 */
public class PurchaseRequestService {

    private final PurchaseRequestDAO requestDAO;
    private final LandDAO landDAO;
    private final TransactionDAO transactionDAO;
    private final MessageDAO messageDAO;
    private final com.landlink.dao.UserDAO userDAO;

    public PurchaseRequestService() {
        this.requestDAO = new PurchaseRequestDAO();
        this.landDAO = new LandDAO();
        this.transactionDAO = new TransactionDAO();
        this.messageDAO = new MessageDAO();
        this.userDAO = new com.landlink.dao.UserDAO();
    }

    private int getAdminId() {
        com.landlink.model.User admin = userDAO.getAdminUser();
        return admin != null ? admin.getId() : 0;
    }

    /**
     * Submit a purchase request from a buyer.
     */
    public boolean submitRequest(int landId, int buyerId) {
        Land land = landDAO.findById(landId);
        if (land == null) throw new IllegalArgumentException("Land not found!");
        if (!Land.STATUS_APPROVED.equals(land.getStatus()) && !Land.STATUS_RESERVED.equals(land.getStatus()))
            throw new IllegalStateException("This land is not available for purchase!");
        if (land.getSellerId() == buyerId)
            throw new IllegalArgumentException("You cannot buy your own land!");
        if (requestDAO.existsPendingRequest(landId, buyerId))
            throw new IllegalStateException("You already have a pending request for this land!");

        PurchaseRequest req = new PurchaseRequest(landId, buyerId, land.getSellerId(), land.getPrice());
        return requestDAO.createRequest(req);
    }

    /**
     * Admin approves a request: create transaction, mark land SOLD, send messages.
     */
    public boolean approveRequest(int requestId, String agreementDate) {
        List<PurchaseRequest> all = requestDAO.getAll();
        PurchaseRequest req = all.stream().filter(r -> r.getId() == requestId).findFirst().orElse(null);
        if (req == null) throw new IllegalArgumentException("Request not found!");

        // Update status
        boolean updated = requestDAO.updateStatus(requestId, PurchaseRequest.STATUS_APPROVED, null, agreementDate);
        if (!updated) return false;

        // Mark land as RESERVED
        landDAO.updateLandStatus(req.getLandId(), Land.STATUS_RESERVED);

        // Send message to BUYER
        String buyerSubject = "✅ Purchase Request Approved — " + req.getLandTitle();
        String buyerBody = "Dear " + req.getBuyerName() + ",\n\n" +
            "Great news! Your purchase request for the following property has been APPROVED.\n\n" +
            "🏡 Property: " + req.getLandTitle() + "\n" +
            "📍 Location: " + req.getLandLocation() + "\n" +
            "💰 Amount: " + req.getFormattedAmount() + "\n\n" +
            "📅 Agreement Date: " + agreementDate + "\n\n" +
            "Please visit our office on the agreement date to complete the legal paperwork.\n\n" +
            "Thank you for choosing Land_Link!\n— Land_Link Team";
        messageDAO.sendMessage(new Message(req.getBuyerId(), getAdminId(), buyerSubject, buyerBody));

        // Send message to SELLER
        String sellerSubject = "🤝 Your Land Has Been Committed for Sale — " + req.getLandTitle();
        String sellerBody = "Dear " + req.getSellerName() + ",\n\n" +
            "Your land listing has been committed for sale to a buyer.\n\n" +
            "🏡 Property: " + req.getLandTitle() + "\n" +
            "📍 Location: " + req.getLandLocation() + "\n" +
            "💰 Sale Amount: " + req.getFormattedAmount() + "\n" +
            "👤 Buyer: " + req.getBuyerName() + "\n\n" +
            "📅 Agreement Date: " + agreementDate + "\n\n" +
            "Please visit our office on the agreement date to complete the legal transfer.\n\n" +
            "Thank you for listing your property on Land_Link!\n— Land_Link Team";
        messageDAO.sendMessage(new Message(req.getSellerId(), getAdminId(), sellerSubject, sellerBody));

        return true;
    }

    /**
     * Admin cancels an ALREADY APPROVED request.
     */
    public boolean cancelApprovedRequest(int requestId) {
        List<PurchaseRequest> all = requestDAO.getAll();
        PurchaseRequest req = all.stream().filter(r -> r.getId() == requestId).findFirst().orElse(null);
        if (req == null) throw new IllegalArgumentException("Request not found!");

        // Update request status to CANCELLED/REJECTED
        boolean updated = requestDAO.updateStatus(requestId, PurchaseRequest.STATUS_REJECTED, "Approval cancelled due to issues.", null);
        if (!updated) return false;

        // Mark land back as APPROVED
        landDAO.updateLandStatus(req.getLandId(), com.landlink.model.Land.STATUS_APPROVED);

        // Send cancellation message to BUYER
        String subject = "⚠️ Purchase Approval Cancelled — " + req.getLandTitle();
        String body = "Dear " + req.getBuyerName() + ",\n\n" +
            "Your previous purchase approval for the following property has been CANCELLED due to some issues.\n\n" +
            "🏡 Property: " + req.getLandTitle() + "\n" +
            "📍 Location: " + req.getLandLocation() + "\n\n" +
            "Please contact the admin for further details.\n\n" +
            "— Land_Link Team";
        messageDAO.sendMessage(new com.landlink.model.Message(req.getBuyerId(), getAdminId(), subject, body));

        return true;
    }

    /**
     * Admin rejects a request: update status, send rejection message to buyer.
     */
    public boolean rejectRequest(int requestId, String reason) {
        List<PurchaseRequest> all = requestDAO.getAll();
        PurchaseRequest req = all.stream().filter(r -> r.getId() == requestId).findFirst().orElse(null);
        if (req == null) throw new IllegalArgumentException("Request not found!");

        boolean updated = requestDAO.updateStatus(requestId, PurchaseRequest.STATUS_REJECTED, reason, null);
        if (!updated) return false;

        // Send message to BUYER
        String subject = "❌ Purchase Request Rejected — " + req.getLandTitle();
        String body = "Dear " + req.getBuyerName() + ",\n\n" +
            "We regret to inform you that the request is rejected for your requested land. We now have some clarifications required.\n\n" +
            "🏡 Property: " + req.getLandTitle() + "\n" +
            "📍 Location: " + req.getLandLocation() + "\n" +
            "💰 Amount: " + req.getFormattedAmount() + "\n\n" +
            "📝 Clarification/Reason: " + reason + "\n\n" +
            "Please contact the admin via the Inbox for further details, or you may browse other available properties on Land_Link.\n\n" +
            "We apologize for the inconvenience.\n— Land_Link Team";
        messageDAO.sendMessage(new Message(req.getBuyerId(), getAdminId(), subject, body));

        return true;
    }

    // Get all requests (for admin panel)
    public List<PurchaseRequest> getAllRequests() {
        return requestDAO.getAll();
    }

    // Get pending requests
    public List<PurchaseRequest> getPendingRequests() {
        return requestDAO.getAllPending();
    }

    // Get buyer's requests
    public List<PurchaseRequest> getMyRequests(int buyerId) {
        return requestDAO.getByBuyer(buyerId);
    }

    // Get requests by land
    public List<PurchaseRequest> getRequestsByLand(int landId) {
        return requestDAO.getByLand(landId);
    }

    // Count pending requests
    public int countPending() {
        return requestDAO.countPending();
    }
}
