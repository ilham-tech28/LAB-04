package com.landlink.ui.user;

import com.landlink.dao.MessageDAO;
import com.landlink.service.AuthService;
import com.landlink.model.User;
import com.landlink.ui.LoginFrame;
import com.landlink.ui.theme.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

/**
 * UserDashboard - Main frame for logged-in users.
 * Features a sidebar navigation and swappable content panels.
 */
public class UserDashboard extends JFrame {

    private JPanel contentPanel;
    private JPanel sidebarPanel;
    private String activeMenuItem = "browse";
    private JLabel inboxBadge;
    private javax.swing.Timer badgeTimer;

    // Menu items
    private final String[][] menuItems = {
        {"browse", "🔍  Browse Lands"},
        {"add", "➕  Add Land"},
        {"my-lands", "📋  My Listings"},
        {"purchases", "🛒  My Purchases"},
        {"inbox", "📬  Inbox"},
    };

    public UserDashboard() {
        initComponents();
        showPanel("browse");
        startBadgeTimer();
    }

    private void startBadgeTimer() {
        updateInboxBadge();
        badgeTimer = new javax.swing.Timer(5000, e -> updateInboxBadge());
        badgeTimer.start();
    }

    private void updateInboxBadge() {
        if (AuthService.getCurrentUser() != null && inboxBadge != null) {
            int count = new MessageDAO().countUnread(AuthService.getCurrentUser().getId());
            if (count > 0) {
                inboxBadge.setText(String.valueOf(count));
                inboxBadge.setVisible(true);
            } else {
                inboxBadge.setVisible(false);
            }
        }
    }

    private void initComponents() {
        User user = AuthService.getCurrentUser();
        setTitle("Land_Link — Welcome, " + (user != null ? user.getFullName() : "User"));
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

        // Logo in sidebar
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

        JLabel roleLabel = new JLabel("User Dashboard");
        roleLabel.setFont(ThemeColors.FONT_SMALL);
        roleLabel.setForeground(ThemeColors.TEXT_MUTED);
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
        userInfoPanel.setBorder(new EmptyBorder(15, 20, 25, 20)); // Align with menu text (20px left)
        userInfoPanel.setMaximumSize(new Dimension(ThemeColors.SIDEBAR_WIDTH, 100));

        JSeparator sep = new JSeparator();
        sep.setForeground(ThemeColors.BORDER);
        sep.setMaximumSize(new Dimension(200, 1));
        sep.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel userName = new JLabel(user != null ? "👤 " + user.getFullName() : "👤 User");
        userName.setFont(ThemeColors.FONT_SMALL);
        userName.setForeground(ThemeColors.TEXT_SECONDARY);
        userName.setAlignmentX(Component.LEFT_ALIGNMENT);
        // Padding removed so it aligns directly at 20px with the button

        StyledButton logoutBtn = StyledButton.danger("Logout");
        logoutBtn.setMaximumSize(new Dimension(220, 36));
        logoutBtn.setPreferredSize(new Dimension(220, 36));
        logoutBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        logoutBtn.setFocusable(false);
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
                    // Active - gradient highlight
                    g2.setColor(new Color(79, 172, 254, 20));
                    g2.fillRoundRect(8, 2, getWidth() - 16, getHeight() - 4, 10, 10);
                    // Left accent bar
                    g2.setColor(ThemeColors.PRIMARY);
                    g2.fillRoundRect(0, 6, 4, getHeight() - 12, 4, 4);
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
        item.add(itemLabel, BorderLayout.CENTER);

        // Add red notification badge for inbox
        if (key.equals("inbox")) {
            inboxBadge = new JLabel() {
                @Override
                protected void paintComponent(Graphics g) {
                    if (isVisible() && getText() != null && !getText().isEmpty()) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.setColor(new Color(220, 50, 50));
                        g2.fillOval(0, 0, getWidth(), getHeight());
                        g2.setColor(Color.WHITE);
                        g2.setFont(new Font("Segoe UI", Font.BOLD, 10));
                        FontMetrics fm = g2.getFontMetrics();
                        int tx = (getWidth() - fm.stringWidth(getText())) / 2;
                        int ty = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                        g2.drawString(getText(), tx, ty);
                        g2.dispose();
                    }
                }
            };
            inboxBadge.setPreferredSize(new Dimension(22, 22));
            inboxBadge.setVisible(false);
            item.add(inboxBadge, BorderLayout.EAST);
        }

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
        contentPanel.removeAll();

        JPanel panel;
        switch (key) {
            case "browse":
                panel = new BrowseLandsPanel();
                break;
            case "add":
                panel = new AddLandPanel(this);
                break;
            case "my-lands":
                panel = new MyListingsPanel();
                break;
            case "purchases":
                panel = new MyPurchasesPanel();
                break;
            case "inbox":
                panel = new UserInboxPanel();
                break;
            default:
                panel = new BrowseLandsPanel();
        }

        contentPanel.add(panel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();

        // Refresh sidebar highlights
        sidebarPanel.revalidate();
        sidebarPanel.repaint();
    }

    // Public method to switch panels (used by AddLandPanel after saving)
    public void switchToPanel(String key) {
        showPanel(key);
    }
}
