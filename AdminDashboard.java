package com.landlink.ui.admin;

import com.landlink.service.AuthService;
import com.landlink.service.LandService;
import com.landlink.dao.UserDAO;
import com.landlink.service.TransactionService;
import com.landlink.ui.LoginFrame;
import com.landlink.ui.theme.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * AdminDashboard - Main frame for Admin users.
 * Features a sidebar navigation and swappable admin panels.
 */
public class AdminDashboard extends JFrame {

    private JPanel contentPanel;
    private JPanel sidebarPanel;
    private String activeMenuItem = "overview";
    private int seenPendingBuyRequests = 0;
    // Services for stats
    private UserDAO userDAO;
    private LandService landService;
    private TransactionService transactionService;
    private com.landlink.service.PurchaseRequestService purchaseRequestService;
    private com.landlink.dao.MessageDAO messageDAO;

    // Notifications
    private java.util.Map<String, Integer> notificationCounts = new java.util.HashMap<>();
    private Timer notificationTimer;

    // Menu items
    private final String[][] menuItems = {
        {"overview", "📊  Overview"},
        {"users", "👥  Manage Users"},
        {"lands", "🏢  Manage Lands"},
        {"buy_requests", "🛒  Buy Requests"},
        {"inbox", "📥  Inbox"}
    };

    public AdminDashboard() {
        this.userDAO = new UserDAO();
        this.landService = new LandService();
        this.transactionService = new TransactionService();
        this.purchaseRequestService = new com.landlink.service.PurchaseRequestService();
        this.messageDAO = new com.landlink.dao.MessageDAO();
        initComponents();
        showPanel("overview");

        startNotificationTimer();
    }

    private void startNotificationTimer() {
        notificationTimer = new Timer(5000, e -> updateNotifications());
        notificationTimer.start();
        updateNotifications(); // Initial fetch
    }

    private void updateNotifications() {
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                try {
                    // Fetch pending buy requests
                    int pendingReqs = purchaseRequestService.getPendingRequests().size();
                    if ("buy_requests".equals(activeMenuItem)) {
                        seenPendingBuyRequests = pendingReqs;
                    }
                    int displayPending = Math.max(0, pendingReqs - seenPendingBuyRequests);
                    notificationCounts.put("buy_requests", displayPending);

                    // Fetch unread inbox messages for admin
                    com.landlink.model.User admin = AuthService.getCurrentUser();
                    if (admin != null) {
                        int unreadInbox = 0;
                        for (com.landlink.dao.MessageDAO.ConversationSummary cs : messageDAO.getConversations(admin)) {
                            unreadInbox += cs.unreadCount;
                        }
                        notificationCounts.put("inbox", unreadInbox);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                return null;
            }

            @Override
            protected void done() {
                if (sidebarPanel != null) {
                    sidebarPanel.repaint();
                }
            }
        };
        worker.execute();
    }

    private void initComponents() {
        setTitle("Land_Link — Admin Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 750);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(900, 600));

        // Main container
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(ThemeColors.BG_MAIN);

        // === SIDEBAR ===
        sidebarPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(ThemeColors.BG_SIDEBAR);
                g2.fillRect(0, 0, getWidth(), getHeight());
                // Right border
                g2.setColor(ThemeColors.BORDER);
                g2.fillRect(getWidth() - 1, 0, 1, getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        sidebarPanel.setOpaque(false);
        sidebarPanel.setPreferredSize(new Dimension(ThemeColors.SIDEBAR_WIDTH, 0));
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setBorder(new EmptyBorder(0, 0, 0, 0));

        // Logo
        JPanel logoPanel = new JPanel();
        logoPanel.setOpaque(false);
        logoPanel.setLayout(new BoxLayout(logoPanel, BoxLayout.Y_AXIS));
        logoPanel.setBorder(new EmptyBorder(25, 20, 20, 20));
        logoPanel.setMaximumSize(new Dimension(ThemeColors.SIDEBAR_WIDTH, 80));

        JLabel logoLabel = new JLabel("Land_Link");
        logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        logoLabel.setForeground(ThemeColors.PRIMARY);
        logoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        try {
            ImageIcon icon = new ImageIcon("logo.png");
            if (icon.getIconWidth() > 0) {
                Image img = icon.getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH);
                logoLabel.setIcon(new ImageIcon(img));
                logoLabel.setText(""); 
            }
        } catch (Exception ex) { }

        JLabel roleLabel = new JLabel("Admin Dashboard");
        roleLabel.setFont(ThemeColors.FONT_SMALL);
        roleLabel.setForeground(ThemeColors.DANGER); // Red for admin
        roleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        logoPanel.add(logoLabel);
        logoPanel.add(Box.createVerticalStrut(3));
        logoPanel.add(roleLabel);

        sidebarPanel.add(logoPanel);
        sidebarPanel.add(Box.createVerticalStrut(10));

        // Menu items
        for (String[] item : menuItems) {
            sidebarPanel.add(createMenuItem(item[0], item[1]));
        }

        sidebarPanel.add(Box.createVerticalGlue());

        // User info & Logout
        JPanel userInfoPanel = new JPanel();
        userInfoPanel.setOpaque(false);
        userInfoPanel.setLayout(new BoxLayout(userInfoPanel, BoxLayout.Y_AXIS));
        userInfoPanel.setBorder(new EmptyBorder(15, 20, 25, 20));
        userInfoPanel.setMaximumSize(new Dimension(ThemeColors.SIDEBAR_WIDTH, 100));

        JSeparator sep = new JSeparator();
        sep.setForeground(ThemeColors.BORDER);
        sep.setMaximumSize(new Dimension(200, 1));
        sep.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel userName = new JLabel("👑 System Admin");
        userName.setFont(ThemeColors.FONT_SMALL);
        userName.setForeground(ThemeColors.TEXT_SECONDARY);
        userName.setAlignmentX(Component.LEFT_ALIGNMENT);
        // Padding removed so it aligns perfectly with the button

        StyledButton logoutBtn = StyledButton.danger("Logout");
        logoutBtn.setMaximumSize(new Dimension(220, 36));
        logoutBtn.setPreferredSize(new Dimension(220, 36));
        logoutBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        logoutBtn.addActionListener(e -> {
            AuthService.logout();
            dispose();
            new LoginFrame().setVisible(true);
        });

        userInfoPanel.add(sep);
        userInfoPanel.add(Box.createVerticalStrut(15));
        userInfoPanel.add(userName);
        userInfoPanel.add(Box.createVerticalStrut(15));
        userInfoPanel.add(logoutBtn);

        sidebarPanel.add(userInfoPanel);

        // === CONTENT AREA ===
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(ThemeColors.BG_MAIN);

        mainPanel.add(sidebarPanel, BorderLayout.WEST);
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        setContentPane(mainPanel);
    }

    private JPanel createMenuItem(String key, String label) {
        JPanel item = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (activeMenuItem.equals(key)) {
                    g2.setColor(new Color(79, 172, 254, 20));
                    g2.fillRoundRect(8, 2, getWidth() - 16, getHeight() - 4, 10, 10);
                    g2.setColor(ThemeColors.PRIMARY);
                    g2.fillRoundRect(0, 6, 4, getHeight() - 12, 4, 4);
                }

                // Draw Notification Badge
                int count = notificationCounts.getOrDefault(key, 0);
                if (count > 0) {
                    String text = count > 99 ? "99+" : String.valueOf(count);
                    g2.setFont(new Font("Segoe UI", Font.BOLD, 10));
                    FontMetrics fm = g2.getFontMetrics();
                    int tw = fm.stringWidth(text);
                    int bw = Math.max(18, tw + 8);
                    int bh = 18;
                    int bx = getWidth() - bw - 20;
                    int by = (getHeight() - bh) / 2;

                    g2.setColor(ThemeColors.DANGER); // Red badge
                    g2.fillRoundRect(bx, by, bw, bh, 10, 10);
                    g2.setColor(Color.WHITE);
                    g2.drawString(text, bx + (bw - tw) / 2, by + fm.getAscent() + (bh - fm.getHeight()) / 2);
                }

                g2.dispose();
                super.paintComponent(g);
            }
        };
        item.setOpaque(false);
        item.setLayout(new BorderLayout());
        item.setBorder(new EmptyBorder(12, 20, 12, 20));
        item.setMaximumSize(new Dimension(ThemeColors.SIDEBAR_WIDTH, 48));
        item.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel itemLabel = new JLabel(label);
        itemLabel.setFont(ThemeColors.FONT_BODY);
        itemLabel.setForeground(activeMenuItem.equals(key) ? ThemeColors.PRIMARY : ThemeColors.TEXT_SECONDARY);
        item.add(itemLabel);

        item.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showPanel(key);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                if (!activeMenuItem.equals(key)) {
                    item.setBackground(ThemeColors.BG_CARD);
                    itemLabel.setForeground(ThemeColors.TEXT_PRIMARY);
                }
                item.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (!activeMenuItem.equals(key)) {
                    itemLabel.setForeground(ThemeColors.TEXT_SECONDARY);
                }
                item.repaint();
            }
        });

        return item;
    }

    private void showPanel(String key) {
        activeMenuItem = key;
        
        // Immediately clear badge if we switch to a tab that tracks seen items
        if ("buy_requests".equals(key)) {
            int pendingReqs = purchaseRequestService.getPendingRequests().size();
            seenPendingBuyRequests = pendingReqs;
            notificationCounts.put("buy_requests", 0);
            sidebarPanel.repaint();
        }

        contentPanel.removeAll();

        JPanel panel;
        switch (key) {
            case "overview":
                panel = new AdminOverviewPanel();
                break;
            case "users":
                panel = new ManageUsersPanel();
                break;
            case "lands":
                panel = new ManageLandsPanel();
                break;
            case "buy_requests":
                panel = new AdminBuyRequestsPanel();
                break;
            case "inbox":
                panel = new com.landlink.ui.user.UserInboxPanel();
                break;
            default:
                panel = new AdminOverviewPanel();
        }

        contentPanel.add(panel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();

        sidebarPanel.revalidate();
        sidebarPanel.repaint();
    }
}

