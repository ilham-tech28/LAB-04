package com.landlink.dao;

import com.landlink.model.Land;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * LandDAO - Data Access Object for Land operations.
 * Demonstrates try-catch exception handling for all database operations.
 */
public class LandDAO {

    private final Connection connection;

    public LandDAO() {
        this.connection = DatabaseHelper.getInstance().getConnection();
    }

    // Create a new land listing
    public int createLand(Land land) {
        String sql = "INSERT INTO lands (title, location, land_size, price, description, land_type, seller_id, contact_number) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, land.getTitle());
            pstmt.setString(2, land.getLocation());
            pstmt.setDouble(3, land.getLandSize());
            pstmt.setDouble(4, land.getPrice());
            pstmt.setString(5, land.getDescription());
            pstmt.setString(6, land.getLandType());
            pstmt.setInt(7, land.getSellerId());
            pstmt.setString(8, land.getContactNumber());
            pstmt.executeUpdate();

            ResultSet keys = pstmt.getGeneratedKeys();
            if (keys.next()) {
                int landId = keys.getInt(1);
                // Save images
                saveImages(landId, land.getImagePaths());
                return landId;
            }
        } catch (SQLException e) {
            System.err.println("❌ Error creating land: " + e.getMessage());
        }
        return -1;
    }

    // Save land images
    private void saveImages(int landId, List<String> imagePaths) {
        if (imagePaths == null || imagePaths.isEmpty()) return;

        String sql = "INSERT INTO land_images (land_id, image_path) VALUES (?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            for (String path : imagePaths) {
                pstmt.setInt(1, landId);
                pstmt.setString(2, path);
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        } catch (SQLException e) {
            System.err.println("❌ Error saving land images: " + e.getMessage());
        }
    }

    // Get images for a land
    public List<String> getImagesForLand(int landId) {
        List<String> images = new ArrayList<>();
        String sql = "SELECT image_path FROM land_images WHERE land_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, landId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                images.add(rs.getString("image_path"));
            }
        } catch (SQLException e) {
            System.err.println("❌ Error getting land images: " + e.getMessage());
        }
        return images;
    }

    // Get all browseable lands (for browsing)
    public List<Land> getBrowseableLands() {
        List<Land> lands = new ArrayList<>();
        String sql = "SELECT l.*, u.full_name as seller_name FROM lands l " +
                     "JOIN users u ON l.seller_id = u.id " +
                     "WHERE l.status IN ('APPROVED', 'RESERVED') ORDER BY l.created_at DESC";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Land land = mapLand(rs);
                land.setSellerName(rs.getString("seller_name"));
                land.setImagePaths(getImagesForLand(land.getId()));
                lands.add(land);
            }
        } catch (SQLException e) {
            System.err.println("❌ Error getting browseable lands: " + e.getMessage());
        }
        return lands;
    }

    // Get lands by status
    public List<Land> getLandsByStatus(String status) {
        List<Land> lands = new ArrayList<>();
        String sql = "SELECT l.*, u.full_name as seller_name FROM lands l " +
                     "JOIN users u ON l.seller_id = u.id " +
                     "WHERE l.status = ? ORDER BY l.created_at DESC";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, status);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Land land = mapLand(rs);
                land.setSellerName(rs.getString("seller_name"));
                land.setImagePaths(getImagesForLand(land.getId()));
                lands.add(land);
            }
        } catch (SQLException e) {
            System.err.println("❌ Error getting lands by status: " + e.getMessage());
        }
        return lands;
    }

    // Get all lands (for admin)
    public List<Land> getAllLands() {
        List<Land> lands = new ArrayList<>();
        String sql = "SELECT l.*, u.full_name as seller_name FROM lands l " +
                     "JOIN users u ON l.seller_id = u.id ORDER BY l.created_at DESC";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Land land = mapLand(rs);
                land.setSellerName(rs.getString("seller_name"));
                land.setImagePaths(getImagesForLand(land.getId()));
                lands.add(land);
            }
        } catch (SQLException e) {
            System.err.println("❌ Error getting all lands: " + e.getMessage());
        }
        return lands;
    }

    // Get lands by seller
    public List<Land> getLandsBySeller(int sellerId) {
        List<Land> lands = new ArrayList<>();
        String sql = "SELECT l.*, u.full_name as seller_name FROM lands l " +
                     "JOIN users u ON l.seller_id = u.id " +
                     "WHERE l.seller_id = ? ORDER BY l.created_at DESC";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, sellerId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Land land = mapLand(rs);
                land.setSellerName(rs.getString("seller_name"));
                land.setImagePaths(getImagesForLand(land.getId()));
                lands.add(land);
            }
        } catch (SQLException e) {
            System.err.println("❌ Error getting seller's lands: " + e.getMessage());
        }
        return lands;
    }

    // Get land by ID
    public Land findById(int id) {
        String sql = "SELECT l.*, u.full_name as seller_name FROM lands l " +
                     "JOIN users u ON l.seller_id = u.id WHERE l.id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Land land = mapLand(rs);
                land.setSellerName(rs.getString("seller_name"));
                land.setImagePaths(getImagesForLand(land.getId()));
                return land;
            }
        } catch (SQLException e) {
            System.err.println("❌ Error finding land: " + e.getMessage());
        }
        return null;
    }

    // Update land status
    public boolean updateLandStatus(int landId, String status) {
        String sql = "UPDATE lands SET status = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, status);
            pstmt.setInt(2, landId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Error updating land status: " + e.getMessage());
            return false;
        }
    }

    // Delete a land listing
    public boolean deleteLand(int landId) {
        try {
            // Delete images first
            try (PreparedStatement pstmt = connection.prepareStatement("DELETE FROM land_images WHERE land_id = ?")) {
                pstmt.setInt(1, landId);
                pstmt.executeUpdate();
            }
            // Delete land
            try (PreparedStatement pstmt = connection.prepareStatement("DELETE FROM lands WHERE id = ?")) {
                pstmt.setInt(1, landId);
                return pstmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("❌ Error deleting land: " + e.getMessage());
            return false;
        }
    }

    // Search lands
    public List<Land> searchLands(String keyword, String landType, Double minPrice, Double maxPrice) {
        List<Land> lands = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT l.*, u.full_name as seller_name FROM lands l " +
            "JOIN users u ON l.seller_id = u.id WHERE l.status IN ('APPROVED', 'SOLD')"
        );
        List<Object> params = new ArrayList<>();

        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(" AND (l.title LIKE ? OR l.location LIKE ? OR l.description LIKE ?)");
            String like = "%" + keyword.trim() + "%";
            params.add(like);
            params.add(like);
            params.add(like);
        }
        if (landType != null && !landType.equals("All")) {
            sql.append(" AND l.land_type = ?");
            params.add(landType);
        }
        if (minPrice != null) {
            sql.append(" AND l.price >= ?");
            params.add(minPrice);
        }
        if (maxPrice != null) {
            sql.append(" AND l.price <= ?");
            params.add(maxPrice);
        }
        sql.append(" ORDER BY l.created_at DESC");

        try (PreparedStatement pstmt = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                Object param = params.get(i);
                if (param instanceof String) {
                    pstmt.setString(i + 1, (String) param);
                } else if (param instanceof Double) {
                    pstmt.setDouble(i + 1, (Double) param);
                }
            }
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Land land = mapLand(rs);
                land.setSellerName(rs.getString("seller_name"));
                land.setImagePaths(getImagesForLand(land.getId()));
                lands.add(land);
            }
        } catch (SQLException e) {
            System.err.println("❌ Error searching lands: " + e.getMessage());
        }
        return lands;
    }

    // Count lands by status
    public int countByStatus(String status) {
        String sql = "SELECT COUNT(*) FROM lands WHERE status = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, status);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("❌ Error counting lands: " + e.getMessage());
        }
        return 0;
    }

    // Count all lands
    public int countAll() {
        String sql = "SELECT COUNT(*) FROM lands";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("❌ Error counting all lands: " + e.getMessage());
        }
        return 0;
    }

    // Map ResultSet to Land object
    private Land mapLand(ResultSet rs) throws SQLException {
        Land land = new Land();
        land.setId(rs.getInt("id"));
        land.setTitle(rs.getString("title"));
        land.setLocation(rs.getString("location"));
        land.setLandSize(rs.getDouble("land_size"));
        land.setPrice(rs.getDouble("price"));
        land.setDescription(rs.getString("description"));
        land.setLandType(rs.getString("land_type"));
        land.setStatus(rs.getString("status"));
        land.setSellerId(rs.getInt("seller_id"));
        land.setContactNumber(rs.getString("contact_number"));
        land.setCreatedAt(rs.getString("created_at"));
        return land;
    }
}
