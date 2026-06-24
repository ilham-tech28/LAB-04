package com.landlink.ui.user;

import com.landlink.dao.MessageDAO;
import com.landlink.dao.MessageDAO.ConversationSummary;
import com.landlink.model.Message;
import com.landlink.service.AuthService;
import com.landlink.ui.theme.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.List;

/**
 * UserInboxPanel - Messenger-style chat platform with conversation list and chat view.
 */
public class UserInboxPanel extends JPanel {

    private MessageDAO messageDAO;
    private JPanel conversationListPanel;
    private JPanel chatAreaPanel;
    private JPanel chatMessagesPanel;
    private JScrollPane chatScrollPane;
    private JTextArea messageInput;
    private StyledButton sendButton;
    private JLabel chatHeaderLabel;
    
    private int selectedPartnerId = -1;
    private String selectedPartnerName = "";
    private int currentLoadedPartnerId = -1; // -1 = none, 0 = system, >0 = user
    private int currentUserId;
    private javax.swing.Timer refreshTimer;

    public UserInboxPanel() {
        this.messageDAO = new MessageDAO();
        this.currentUserId = AuthService.getCurrentUser().getId();
        initComponents();
        loadConversations();
        
        // Auto-refresh timer
        refreshTimer = new javax.swing.Timer(5000, e -> {
            loadConversations();
            if (selectedPartnerId > 0) {
                // Store scroll position
                JScrollBar sb = chatScrollPane != null ? chatScrollPane.getVerticalScrollBar() : null;
                int val = sb != null ? sb.getValue() : 0;
                int max = sb != null ? sb.getMaximum() : 0;
                boolean isAtBottom = sb != null && (max - val <= sb.getVisibleAmount() + 20);

                loadChat(selectedPartnerId, selectedPartnerName, false);

                // Restore scroll position or scroll to bottom if we were at bottom
                SwingUtilities.invokeLater(() -> {
                    if (sb != null) {
                        if (isAtBottom) sb.setValue(sb.getMaximum());
                        else sb.setValue(val);
                    }
                });
            }
        });
        refreshTimer.start();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(ThemeColors.BG_MAIN);

        // === HEADER ===
        GradientPanel headerPanel = new GradientPanel(
            new Color(20, 40, 70), new Color(30, 80, 140), new Color(40, 120, 200));
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setBorder(new EmptyBorder(16, 25, 16, 25));
        headerPanel.setPreferredSize(new Dimension(0, 60));

        JLabel titleLabel = new JLabel("💬 Messages");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);

        StyledButton refreshBtn = StyledButton.secondary("🔄 Refresh");
        refreshBtn.setPreferredSize(new Dimension(100, 32));
        refreshBtn.addActionListener(e -> {
            loadConversations();
            if (selectedPartnerId > 0) loadChat(selectedPartnerId, selectedPartnerName, true);
        });

        JPanel rightHeader = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        rightHeader.setOpaque(false);
        rightHeader.add(refreshBtn);

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(rightHeader, BorderLayout.EAST);

        // === MAIN SPLIT ===
        JPanel mainBody = new JPanel(new BorderLayout());
        mainBody.setBackground(ThemeColors.BG_MAIN);

        // --- LEFT: Conversation List ---
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(ThemeColors.BG_SIDEBAR);
        leftPanel.setPreferredSize(new Dimension(280, 0));
        leftPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, ThemeColors.BORDER));

        JLabel convoTitle = new JLabel("  Conversations");
        convoTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        convoTitle.setForeground(ThemeColors.TEXT_SECONDARY);
        convoTitle.setBorder(new EmptyBorder(12, 10, 12, 10));
        convoTitle.setOpaque(true);
        convoTitle.setBackground(ThemeColors.BG_SIDEBAR);

        class ScrollablePanel extends JPanel implements Scrollable {
            @Override public Dimension getPreferredScrollableViewportSize() { return getPreferredSize(); }
            @Override public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) { return 16; }
            @Override public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) { return visibleRect.height; }
            @Override public boolean getScrollableTracksViewportWidth() { return true; }
            @Override public boolean getScrollableTracksViewportHeight() { return false; }
        }

        conversationListPanel = new ScrollablePanel();
        conversationListPanel.setLayout(new BoxLayout(conversationListPanel, BoxLayout.Y_AXIS));
        conversationListPanel.setBackground(ThemeColors.BG_SIDEBAR);

        JScrollPane leftScroll = new JScrollPane(conversationListPanel);
        leftScroll.setBorder(BorderFactory.createEmptyBorder());
        leftScroll.getViewport().setBackground(ThemeColors.BG_SIDEBAR);
        leftScroll.getVerticalScrollBar().setUnitIncrement(12);
        leftScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        leftPanel.add(convoTitle, BorderLayout.NORTH);
        leftPanel.add(leftScroll, BorderLayout.CENTER);

        // --- RIGHT: Chat Area ---
        chatAreaPanel = new JPanel(new BorderLayout());
        chatAreaPanel.setBackground(ThemeColors.BG_MAIN);
        showEmptyChat();

        mainBody.add(leftPanel, BorderLayout.WEST);
        mainBody.add(chatAreaPanel, BorderLayout.CENTER);

        add(headerPanel, BorderLayout.NORTH);
        add(mainBody, BorderLayout.CENTER);
    }

    private void showEmptyChat() {
        chatAreaPanel.removeAll();
        JPanel emptyPanel = new JPanel(new GridBagLayout());
        emptyPanel.setBackground(ThemeColors.BG_MAIN);

        JPanel centerBox = new JPanel();
        centerBox.setOpaque(false);
        centerBox.setLayout(new BoxLayout(centerBox, BoxLayout.Y_AXIS));

        JLabel emptyIcon = new JLabel("💬");
        emptyIcon.setFont(new Font("Segoe UI", Font.PLAIN, 60));
        emptyIcon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel emptyText = new JLabel("Select a conversation");
        emptyText.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        emptyText.setForeground(ThemeColors.TEXT_MUTED);
        emptyText.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel emptySubText = new JLabel("Choose a chat from the left to start messaging");
        emptySubText.setFont(ThemeColors.FONT_SMALL);
        emptySubText.setForeground(ThemeColors.TEXT_MUTED);
        emptySubText.setAlignmentX(Component.CENTER_ALIGNMENT);

        centerBox.add(emptyIcon);
        centerBox.add(Box.createVerticalStrut(10));
        centerBox.add(emptyText);
        centerBox.add(Box.createVerticalStrut(5));
        centerBox.add(emptySubText);

        emptyPanel.add(centerBox);
        chatAreaPanel.add(emptyPanel, BorderLayout.CENTER);
        chatAreaPanel.revalidate();
        chatAreaPanel.repaint();
    }

    // ==================== CONVERSATION LIST ====================

    public void loadConversations() {
        com.landlink.model.User currentUser = com.landlink.service.AuthService.getCurrentUser();
        List<ConversationSummary> conversations = messageDAO.getConversations(currentUser);

        conversationListPanel.removeAll();

        if (conversations.isEmpty()) {
            JLabel noConvo = new JLabel("No conversations yet");
            noConvo.setFont(ThemeColors.FONT_BODY);
            noConvo.setForeground(ThemeColors.TEXT_MUTED);
            noConvo.setBorder(new EmptyBorder(30, 20, 30, 20));
            noConvo.setAlignmentX(Component.CENTER_ALIGNMENT);
            conversationListPanel.add(noConvo);
        } else {
            for (ConversationSummary cs : conversations) {
                String preview = cs.lastMessage != null ? cs.lastMessage.replace("\n", " ") : "";
                if (preview.length() > 40) preview = preview.substring(0, 37) + "...";
                conversationListPanel.add(createConversationCard(
                    cs.partnerId, cs.partnerName, preview,
                    cs.lastTime, cs.unreadCount));
            }

            // Auto-select first conversation if nothing is selected
            if (selectedPartnerId == -1 && !conversations.isEmpty()) {
                ConversationSummary first = conversations.get(0);
                selectedPartnerId = first.partnerId;
                selectedPartnerName = first.partnerName;
                loadChat(selectedPartnerId, selectedPartnerName, false);
                // Need to re-render the card to show selection state, wait for next repaint
            }
        }

        conversationListPanel.revalidate();
        conversationListPanel.repaint();
    }

    private JPanel createConversationCard(int partnerId, String name, String preview,
                                           String time, int unreadCount) {
        boolean isSelected = partnerId == selectedPartnerId;
        boolean hasUnread = unreadCount > 0;

        JPanel card = new JPanel() {
            boolean hovered = false;
            {
                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) { hovered = true; repaint(); }
                    @Override
                    public void mouseExited(MouseEvent e) { hovered = false; repaint(); }
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        selectedPartnerId = partnerId;
                        selectedPartnerName = name;
                        loadChat(partnerId, name, true);
                        loadConversations(); // Refresh list to update selection & unread
                    }
                });
            }
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (isSelected) {
                    g2.setColor(new Color(79, 172, 254, 30));
                } else if (hovered) {
                    g2.setColor(new Color(100, 100, 100, 20));
                } else {
                    g2.setColor(ThemeColors.BG_SIDEBAR);
                }
                g2.fillRect(0, 0, getWidth(), getHeight());

                // Selected accent bar
                if (isSelected) {
                    g2.setColor(ThemeColors.PRIMARY);
                    g2.fillRoundRect(0, 4, 4, getHeight() - 8, 4, 4);
                }

                // Bottom border
                g2.setColor(new Color(ThemeColors.BORDER.getRed(), ThemeColors.BORDER.getGreen(),
                    ThemeColors.BORDER.getBlue(), 80));
                g2.fillRect(15, getHeight() - 1, getWidth() - 30, 1);

                g2.dispose();
                super.paintComponent(g);
            }
        };
        card.setOpaque(false);
        card.setLayout(new BorderLayout(10, 0));
        card.setBorder(new EmptyBorder(12, 15, 12, 15));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 72));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Avatar circle
        JLabel avatar = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color avatarColor = partnerId == 0 ? new Color(255, 152, 0) :
                    new Color(79, 172, 254);
                g2.setColor(avatarColor);
                g2.fillOval(0, 0, 40, 40);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 16));
                String initial = name.length() > 0 ? name.substring(0, 1).toUpperCase() : "?";
                if (partnerId == 0) initial = "🔔";
                FontMetrics fm = g2.getFontMetrics();
                int tx = (40 - fm.stringWidth(initial)) / 2;
                int ty = (40 + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(initial, tx, ty);
                g2.dispose();
            }
        };
        avatar.setPreferredSize(new Dimension(40, 40));

        // Text
        JPanel textPanel = new JPanel();
        textPanel.setOpaque(false);
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));

        JLabel nameLabel = new JLabel(partnerId == 0 ? "System" : name);
        nameLabel.setFont(hasUnread
            ? new Font("Segoe UI", Font.BOLD, 14)
            : new Font("Segoe UI", Font.PLAIN, 14));
        nameLabel.setForeground(ThemeColors.TEXT_PRIMARY);

        JLabel previewLabel = new JLabel(preview);
        previewLabel.setFont(ThemeColors.FONT_SMALL);
        previewLabel.setForeground(hasUnread ? ThemeColors.TEXT_SECONDARY : ThemeColors.TEXT_MUTED);

        textPanel.add(nameLabel);
        textPanel.add(Box.createVerticalStrut(3));
        textPanel.add(previewLabel);

        // Right: time + unread badge
        JPanel rightPanel = new JPanel();
        rightPanel.setOpaque(false);
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setAlignmentY(Component.TOP_ALIGNMENT);

        String displayTime = "";
        if (time != null && time.length() >= 16) {
            displayTime = time.substring(11, 16); // HH:mm
        } else if (time != null && time.length() >= 10) {
            displayTime = time.substring(5, 10); // MM-DD
        }

        JLabel timeLabel = new JLabel(displayTime);
        timeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        timeLabel.setForeground(hasUnread ? ThemeColors.PRIMARY : ThemeColors.TEXT_MUTED);
        timeLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);
        rightPanel.add(timeLabel);

        if (hasUnread) {
            rightPanel.add(Box.createVerticalStrut(5));
            JLabel badge = new JLabel(String.valueOf(unreadCount)) {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(new Color(220, 50, 50));
                    g2.fillOval(0, 0, getWidth(), getHeight());
                    g2.dispose();
                    super.paintComponent(g);
                }
            };
            badge.setOpaque(false);
            badge.setFont(new Font("Segoe UI", Font.BOLD, 10));
            badge.setForeground(Color.WHITE);
            badge.setHorizontalAlignment(SwingConstants.CENTER);
            badge.setPreferredSize(new Dimension(22, 22));
            badge.setMaximumSize(new Dimension(22, 22));
            badge.setAlignmentX(Component.RIGHT_ALIGNMENT);
            rightPanel.add(badge);
        }

        card.add(avatar, BorderLayout.WEST);
        card.add(textPanel, BorderLayout.CENTER);
        card.add(rightPanel, BorderLayout.EAST);

        return card;
    }

    // ==================== CHAT VIEW ====================

    private void loadChat(int partnerId, String partnerName, boolean forceScrollToBottom) {
        if (currentLoadedPartnerId != partnerId || chatMessagesPanel == null) {
            buildChatLayout(partnerId, partnerName);
            currentLoadedPartnerId = partnerId;
        }
        refreshChatMessages(partnerId, forceScrollToBottom);
    }

    private void buildChatLayout(int partnerId, String partnerName) {
        chatAreaPanel.removeAll();
        chatAreaPanel.setLayout(new BorderLayout());

        // Chat header
        JPanel chatHeader = new JPanel(new BorderLayout());
        chatHeader.setBackground(ThemeColors.BG_CARD);
        chatHeader.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, ThemeColors.BORDER),
            new EmptyBorder(12, 20, 12, 20)));

        chatHeaderLabel = new JLabel("💬 " + partnerName);
        chatHeaderLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        chatHeaderLabel.setForeground(ThemeColors.TEXT_PRIMARY);
        chatHeader.add(chatHeaderLabel, BorderLayout.WEST);

        // Chat messages area
        chatMessagesPanel = new JPanel();
        chatMessagesPanel.setLayout(new BoxLayout(chatMessagesPanel, BoxLayout.Y_AXIS));
        chatMessagesPanel.setBackground(new Color(240, 242, 245));
        chatMessagesPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        chatScrollPane = new JScrollPane(chatMessagesPanel);
        chatScrollPane.setBorder(BorderFactory.createEmptyBorder());
        chatScrollPane.getViewport().setBackground(new Color(240, 242, 245));
        chatScrollPane.getVerticalScrollBar().setUnitIncrement(15);
        chatScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        // Input area
        JPanel inputPanel = new JPanel(new BorderLayout(10, 0));
        inputPanel.setBackground(ThemeColors.BG_CARD);
        inputPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, ThemeColors.BORDER),
            new EmptyBorder(12, 15, 12, 15)));

        messageInput = new JTextArea(2, 1);
        messageInput.setFont(ThemeColors.FONT_BODY);
        messageInput.setLineWrap(true);
        messageInput.setWrapStyleWord(true);
        messageInput.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ThemeColors.BORDER, 1, true),
            new EmptyBorder(8, 12, 8, 12)));

        // Enter key sends message (Shift+Enter for new line)
        messageInput.getInputMap(JComponent.WHEN_FOCUSED).put(javax.swing.KeyStroke.getKeyStroke("ENTER"), "send");
        messageInput.getActionMap().put("send", new javax.swing.AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                sendMessage(partnerId);
            }
        });
        messageInput.getInputMap(JComponent.WHEN_FOCUSED).put(javax.swing.KeyStroke.getKeyStroke("shift ENTER"), "newline");
        messageInput.getActionMap().put("newline", new javax.swing.AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                messageInput.append("\n");
            }
        });

        sendButton = StyledButton.success("Send ▶");
        sendButton.setPreferredSize(new Dimension(90, 44));
        sendButton.addActionListener(e -> sendMessage(partnerId));

        JScrollPane inputScroll = new JScrollPane(messageInput);
        inputScroll.setBorder(BorderFactory.createEmptyBorder());
        inputScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        inputScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        inputPanel.add(inputScroll, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        chatAreaPanel.add(chatHeader, BorderLayout.NORTH);
        chatAreaPanel.add(chatScrollPane, BorderLayout.CENTER);
        chatAreaPanel.add(inputPanel, BorderLayout.SOUTH);

        chatAreaPanel.revalidate();
        chatAreaPanel.repaint();
    }

    private void refreshChatMessages(int partnerId, boolean forceScrollToBottom) {
        // Mark conversation as read
        messageDAO.markConversationAsRead(currentUserId, partnerId);

        chatMessagesPanel.removeAll();

        // Load messages
        List<Message> messages = messageDAO.getConversation(currentUserId, partnerId);
        if (messages.isEmpty()) {
            JLabel noMsg = new JLabel("No messages yet. Start the conversation!");
            noMsg.setFont(ThemeColors.FONT_BODY);
            noMsg.setForeground(ThemeColors.TEXT_MUTED);
            noMsg.setAlignmentX(Component.CENTER_ALIGNMENT);
            chatMessagesPanel.add(Box.createVerticalGlue());
            chatMessagesPanel.add(noMsg);
            chatMessagesPanel.add(Box.createVerticalGlue());
        } else {
            for (Message msg : messages) {
                boolean isSent = msg.getSenderId() == currentUserId;
                chatMessagesPanel.add(createChatBubble(msg, isSent));
                chatMessagesPanel.add(Box.createVerticalStrut(8));
            }
        }

        chatMessagesPanel.revalidate();
        chatMessagesPanel.repaint();

        // Scroll to bottom
        if (forceScrollToBottom) {
            SwingUtilities.invokeLater(() -> {
                if (chatScrollPane != null) {
                    JScrollBar sb = chatScrollPane.getVerticalScrollBar();
                    sb.setValue(sb.getMaximum());
                }
            });
        }
    }

    private JPanel createChatBubble(Message msg, boolean isSent) {
        // Wrapper to align left or right
        JPanel wrapper = new JPanel(new BorderLayout()) {
            @Override
            public Dimension getMaximumSize() {
                return new Dimension(Integer.MAX_VALUE, getPreferredSize().height);
            }
        };
        wrapper.setOpaque(false);

        JPanel bubble = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (isSent) {
                    g2.setColor(new Color(0, 132, 255)); // Blue for sent
                } else {
                    g2.setColor(Color.WHITE); // White for received
                }
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 18, 18));
                if (!isSent) {
                    g2.setColor(new Color(220, 220, 220));
                    g2.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 18, 18));
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        bubble.setOpaque(false);
        bubble.setLayout(new BoxLayout(bubble, BoxLayout.Y_AXIS));
        bubble.setBorder(new EmptyBorder(10, 14, 8, 14));

        // Subject line (if present and not a reply)
        String subject = msg.getSubject();
        if (subject != null && !subject.isEmpty() && !subject.startsWith("Re:") && !subject.equals("Chat")) {
            JLabel subLabel = new JLabel(subject);
            subLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
            subLabel.setForeground(isSent ? new Color(220, 240, 255) : ThemeColors.TEXT_SECONDARY);
            subLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            bubble.add(subLabel);
            bubble.add(Box.createVerticalStrut(4));
        }

        // Message body
        JTextArea bodyArea = new JTextArea(msg.getBody());
        bodyArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        bodyArea.setForeground(isSent ? Color.WHITE : ThemeColors.TEXT_PRIMARY);
        bodyArea.setOpaque(false);
        bodyArea.setEditable(false);
        bodyArea.setLineWrap(true);
        bodyArea.setWrapStyleWord(true);
        bodyArea.setBorder(null);
        bodyArea.setAlignmentX(Component.LEFT_ALIGNMENT);
        bubble.add(bodyArea);

        // Time
        String time = "";
        if (msg.getSentAt() != null && msg.getSentAt().length() >= 16) {
            time = msg.getSentAt().substring(11, 16);
        }
        JLabel timeLabel = new JLabel(time);
        timeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        timeLabel.setForeground(isSent ? new Color(200, 220, 255) : ThemeColors.TEXT_MUTED);
        timeLabel.setAlignmentX(isSent ? Component.RIGHT_ALIGNMENT : Component.LEFT_ALIGNMENT);
        bubble.add(Box.createVerticalStrut(4));
        bubble.add(timeLabel);

        if (isSent) {
            wrapper.add(Box.createHorizontalGlue(), BorderLayout.CENTER);
            wrapper.add(bubble, BorderLayout.EAST);
        } else {
            wrapper.add(bubble, BorderLayout.WEST);
            wrapper.add(Box.createHorizontalGlue(), BorderLayout.CENTER);
        }

        return wrapper;
    }

    private void sendMessage(int partnerId) {
        String text = messageInput.getText().trim();
        if (text.isEmpty()) return;

        Message msg = new Message(partnerId, currentUserId, "Chat", text);
        if (messageDAO.sendMessage(msg)) {
            messageInput.setText("");
            refreshChatMessages(partnerId, true);
            loadConversations();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to send message.",
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

}
