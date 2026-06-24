package com.landlink.model;

/**
 * Message model - represents an inbox message for a user.
 */
public class Message {

    private int id;
    private int recipientId;
    private int senderId; // 0 for System
    private String subject;
    private String body;
    private boolean isRead;
    private String sentAt;

    private String senderName; // For display

    public Message() {}

    public Message(int recipientId, int senderId, String subject, String body) {
        this.recipientId = recipientId;
        this.senderId = senderId;
        this.subject = subject;
        this.body = body;
        this.isRead = false;
    }

    public Message(int recipientId, String subject, String body) {
        this(recipientId, 0, subject, body); // Default to System sender
    }

    // Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getRecipientId() { return recipientId; }
    public void setRecipientId(int recipientId) { this.recipientId = recipientId; }

    public int getSenderId() { return senderId; }
    public void setSenderId(int senderId) { this.senderId = senderId; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }

    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }

    public String getSentAt() { return sentAt; }
    public void setSentAt(String sentAt) { this.sentAt = sentAt; }

    public String getSenderName() { return senderName; }
    public void setSenderName(String senderName) { this.senderName = senderName; }
}
