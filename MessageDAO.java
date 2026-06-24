package com.landlink.dao;

import com.landlink.model.Message;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * MessageDAO - Data Access Object for inbox message operations.
 */
public class MessageDAO {

    private final Connection connection;

    public MessageDAO() {
        this.connection = DatabaseHelper.getInstance().getConnection();
    }

    /**
     * ConversationSummary - Groups messages by conversation partner.
     */
    public static class ConversationSummary {
        public int partnerId;
        public String partnerName;
        public String lastMessage;
        public String lastTime;
        public int unreadCount;
    }

    // Send a message to a user
    public boolean sendMessage(Message msg) {
        String sql = "INSERT INTO messages (recipient_id, sender_id, subject, body) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, msg.getRecipientId());
            pstmt.setInt(2, msg.getSenderId());
            pstmt.setString(3, msg.getSubject());
            pstmt.setString(4, msg.getBody());
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("❌ Error sending message: " + e.getMessage());
            return false;
        }
    }

    // Get inbox for a user (newest first)
    public List<Message> getInbox(int recipientId) {
        List<Message> messages = new ArrayList<>();
        String sql = "SELECT m.*, u.full_name as sender_name " +
                     "FROM messages m " +
                     "LEFT JOIN users u ON m.sender_id = u.id " +
                     "WHERE m.recipient_id=? ORDER BY m.sent_at DESC";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, recipientId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) messages.add(mapMessage(rs));
        } catch (SQLException e) {
            System.err.println("❌ Error getting inbox: " + e.getMessage());
        }
        return messages;
    }

    // Count unread messages for a user
    public int countUnread(int recipientId) {
        String sql = "SELECT COUNT(*) FROM messages WHERE recipient_id=? AND is_read=0";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, recipientId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("❌ Error counting unread: " + e.getMessage());
        }
        return 0;
    }

    // Mark a message as read
    public boolean markAsRead(int messageId) {
        String sql = "UPDATE messages SET is_read=1 WHERE id=?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, messageId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Error marking as read: " + e.getMessage());
            return false;
        }
    }

    /**
     * Get all conversation partners for a user.
     * Admins see all their active chats. Users only see the Admin.
     */
    public List<ConversationSummary> getConversations(com.landlink.model.User currentUser) {
        List<ConversationSummary> result = new ArrayList<>();
        int userId = currentUser.getId();
        boolean isAdmin = currentUser.isAdmin();

        List<Integer> partnerIds = new ArrayList<>();

        if (isAdmin) {
            // Step 1: Get distinct partner IDs for admin
            String partnerSql = "SELECT DISTINCT " +
                "CASE WHEN sender_id = ? THEN recipient_id ELSE sender_id END as partner_id " +
                "FROM messages WHERE sender_id = ? OR recipient_id = ?";
            
            try (PreparedStatement pstmt = connection.prepareStatement(partnerSql)) {
                pstmt.setInt(1, userId);
                pstmt.setInt(2, userId);
                pstmt.setInt(3, userId);
                ResultSet rs = pstmt.executeQuery();
                while (rs.next()) {
                    int pid = rs.getInt("partner_id");
                    if (pid > 0) partnerIds.add(pid);
                }
            } catch (SQLException e) {
                System.err.println("❌ Error getting conversations: " + e.getMessage());
            }
        } else {
            // Normal users only chat with Admin
            com.landlink.model.User admin = new UserDAO().getAdminUser();
            if (admin != null) {
                partnerIds.add(admin.getId());
            }
        }


            // Step 2: For each partner, build summary
            try {
                for (int partnerId : partnerIds) {
                    ConversationSummary cs = new ConversationSummary();
                    cs.partnerId = partnerId;

                    // Get partner name
                    try (PreparedStatement p2 = connection.prepareStatement(
                            "SELECT full_name FROM users WHERE id=?")) {
                        p2.setInt(1, partnerId);
                        ResultSet r2 = p2.executeQuery();
                        cs.partnerName = r2.next() ? r2.getString("full_name") : "User #" + partnerId;
                    }

                    // Get last message between the two
                    String lastSql = "SELECT body, sent_at FROM messages WHERE " +
                        "((sender_id=? AND recipient_id=?) OR (sender_id=? AND recipient_id=?)) " +
                        "ORDER BY sent_at DESC LIMIT 1";
                    try (PreparedStatement p3 = connection.prepareStatement(lastSql)) {
                        p3.setInt(1, userId);
                        p3.setInt(2, partnerId);
                        p3.setInt(3, partnerId);
                        p3.setInt(4, userId);
                        ResultSet r3 = p3.executeQuery();
                        if (r3.next()) {
                            cs.lastMessage = r3.getString("body");
                            cs.lastTime = r3.getString("sent_at");
                        }
                    }

                    // Unread count (messages FROM partner TO user that are unread)
                    String unreadSql = "SELECT COUNT(*) FROM messages WHERE sender_id=? AND recipient_id=? AND is_read=0";
                    try (PreparedStatement p4 = connection.prepareStatement(unreadSql)) {
                        p4.setInt(1, partnerId);
                        p4.setInt(2, userId);
                        ResultSet r4 = p4.executeQuery();
                        if (r4.next()) cs.unreadCount = r4.getInt(1);
                    }

                    result.add(cs);
                }
            } catch (SQLException e) {
                System.err.println("❌ Error building summaries: " + e.getMessage());
            }

        // Sort by last message time (newest first)
        result.sort((a, b) -> {
            if (a.lastTime == null) return 1;
            if (b.lastTime == null) return -1;
            return b.lastTime.compareTo(a.lastTime);
        });

        return result;
    }

    /**
     * Get full chat history between two users (oldest first for chat display).
     */
    public List<Message> getConversation(int userId, int partnerId) {
        List<Message> messages = new ArrayList<>();
        String sql = "SELECT m.*, u.full_name as sender_name FROM messages m " +
            "LEFT JOIN users u ON m.sender_id = u.id " +
            "WHERE (m.sender_id=? AND m.recipient_id=?) OR (m.sender_id=? AND m.recipient_id=?) " +
            "ORDER BY m.sent_at ASC";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, partnerId);
            pstmt.setInt(3, partnerId);
            pstmt.setInt(4, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) messages.add(mapMessage(rs));
        } catch (SQLException e) {
            System.err.println("❌ Error getting conversation: " + e.getMessage());
        }
        return messages;
    }

    /**
     * Mark all messages from a partner to the user as read.
     */
    public boolean markConversationAsRead(int userId, int partnerId) {
        String sql = "UPDATE messages SET is_read=1 WHERE sender_id=? AND recipient_id=? AND is_read=0";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, partnerId);
            pstmt.setInt(2, userId);
            return pstmt.executeUpdate() >= 0;
        } catch (SQLException e) {
            System.err.println("❌ Error marking conversation as read: " + e.getMessage());
            return false;
        }
    }

    private Message mapMessage(ResultSet rs) throws SQLException {
        Message m = new Message();
        m.setId(rs.getInt("id"));
        m.setRecipientId(rs.getInt("recipient_id"));
        m.setSenderId(rs.getInt("sender_id"));
        m.setSubject(rs.getString("subject"));
        m.setBody(rs.getString("body"));
        m.setRead(rs.getInt("is_read") == 1);
        m.setSentAt(rs.getString("sent_at"));

        // Fetch sender_name if we joined, otherwise default
        try {
            String sName = rs.getString("sender_name");
            m.setSenderName(sName != null ? sName : "System");
        } catch (SQLException e) {
            m.setSenderName("System");
        }

        return m;
    }
}
