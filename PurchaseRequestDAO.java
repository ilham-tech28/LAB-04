package com.landlink.dao;

import com.landlink.model.PurchaseRequest;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * PurchaseRequestDAO - Data Access Object for buy request operations.
 */
public class PurchaseRequestDAO {

    private final Connection connection;

    public PurchaseRequestDAO() {
        this.connection = DatabaseHelper.getInstance().getConnection();
    }

    // Submit a new purchase request
    public boolean createRequest(PurchaseRequest req) {
        String sql = "INSERT INTO purchase_requests (land_id, buyer_id, seller_id, amount, status) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, req.getLandId());
            pstmt.setInt(2, req.getBuyerId());
            pstmt.setInt(3, req.getSellerId());
            pstmt.setDouble(4, req.getAmount());
            pstmt.setString(5, req.getStatus());
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("❌ Error creating purchase request: " + e.getMessage());
            return false;
        }
    }

    // Check if a pending request already exists for this land+buyer
    public boolean existsPendingRequest(int landId, int buyerId) {
        String sql = "SELECT COUNT(*) FROM purchase_requests WHERE land_id=? AND buyer_id=? AND status='PENDING'";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, landId);
            pstmt.setInt(2, buyerId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        } catch (SQLException e) {
            System.err.println("❌ Error checking existing request: " + e.getMessage());
        }
        return false;
    }

    // Get all pending requests (for admin)
    public List<PurchaseRequest> getAllPending() {
        return getByStatus("PENDING");
    }

    // Get all requests (for admin)
    public List<PurchaseRequest> getAll() {
        String sql = "SELECT pr.*, l.title as land_title, l.location as land_location, " +
                     "b.full_name as buyer_name, s.full_name as seller_name " +
                     "FROM purchase_requests pr " +
                     "JOIN lands l ON pr.land_id = l.id " +
                     "JOIN users b ON pr.buyer_id = b.id " +
                     "JOIN users s ON pr.seller_id = s.id " +
                     "ORDER BY pr.created_at DESC";
        return query(sql);
    }

    // Get requests by status
    public List<PurchaseRequest> getByStatus(String status) {
        String sql = "SELECT pr.*, l.title as land_title, l.location as land_location, " +
                     "b.full_name as buyer_name, s.full_name as seller_name " +
                     "FROM purchase_requests pr " +
                     "JOIN lands l ON pr.land_id = l.id " +
                     "JOIN users b ON pr.buyer_id = b.id " +
                     "JOIN users s ON pr.seller_id = s.id " +
                     "WHERE pr.status = ? ORDER BY pr.created_at DESC";
        List<PurchaseRequest> list = new ArrayList<>();
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, status);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) list.add(mapRequest(rs));
        } catch (SQLException e) {
            System.err.println("❌ Error getting requests by status: " + e.getMessage());
        }
        return list;
    }

    // Get requests by buyer
    public List<PurchaseRequest> getByBuyer(int buyerId) {
        String sql = "SELECT pr.*, l.title as land_title, l.location as land_location, " +
                     "b.full_name as buyer_name, s.full_name as seller_name " +
                     "FROM purchase_requests pr " +
                     "JOIN lands l ON pr.land_id = l.id " +
                     "JOIN users b ON pr.buyer_id = b.id " +
                     "JOIN users s ON pr.seller_id = s.id " +
                     "WHERE pr.buyer_id = ? ORDER BY pr.created_at DESC";
        List<PurchaseRequest> list = new ArrayList<>();
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, buyerId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) list.add(mapRequest(rs));
        } catch (SQLException e) {
            System.err.println("❌ Error getting buyer requests: " + e.getMessage());
        }
        return list;
    }

    // Get requests by land
    public List<PurchaseRequest> getByLand(int landId) {
        String sql = "SELECT pr.*, l.title as land_title, l.location as land_location, " +
                     "b.full_name as buyer_name, s.full_name as seller_name " +
                     "FROM purchase_requests pr " +
                     "JOIN lands l ON pr.land_id = l.id " +
                     "JOIN users b ON pr.buyer_id = b.id " +
                     "JOIN users s ON pr.seller_id = s.id " +
                     "WHERE pr.land_id = ? ORDER BY pr.created_at DESC";
        List<PurchaseRequest> list = new ArrayList<>();
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, landId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) list.add(mapRequest(rs));
        } catch (SQLException e) {
            System.err.println("❌ Error getting land requests: " + e.getMessage());
        }
        return list;
    }

    // Update request status (approve or reject)
    public boolean updateStatus(int id, String status, String rejectionReason, String agreementDate) {
        String sql = "UPDATE purchase_requests SET status=?, rejection_reason=?, agreement_date=? WHERE id=?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, status);
            pstmt.setString(2, rejectionReason);
            pstmt.setString(3, agreementDate);
            pstmt.setInt(4, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Error updating request status: " + e.getMessage());
            return false;
        }
    }

    // Count all pending requests
    public int countPending() {
        String sql = "SELECT COUNT(*) FROM purchase_requests WHERE status='PENDING'";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("❌ Error counting pending: " + e.getMessage());
        }
        return 0;
    }

    private List<PurchaseRequest> query(String sql) {
        List<PurchaseRequest> list = new ArrayList<>();
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) list.add(mapRequest(rs));
        } catch (SQLException e) {
            System.err.println("❌ Error querying requests: " + e.getMessage());
        }
        return list;
    }

    private PurchaseRequest mapRequest(ResultSet rs) throws SQLException {
        PurchaseRequest r = new PurchaseRequest();
        r.setId(rs.getInt("id"));
        r.setLandId(rs.getInt("land_id"));
        r.setBuyerId(rs.getInt("buyer_id"));
        r.setSellerId(rs.getInt("seller_id"));
        r.setAmount(rs.getDouble("amount"));
        r.setStatus(rs.getString("status"));
        r.setRejectionReason(rs.getString("rejection_reason"));
        r.setAgreementDate(rs.getString("agreement_date"));
        r.setCreatedAt(rs.getString("created_at"));
        r.setLandTitle(rs.getString("land_title"));
        r.setLandLocation(rs.getString("land_location"));
        r.setBuyerName(rs.getString("buyer_name"));
        r.setSellerName(rs.getString("seller_name"));
        return r;
    }
}
