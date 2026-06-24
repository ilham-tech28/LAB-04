package com.landlink.ui.user;

import com.landlink.ui.LoginFrame;
import com.landlink.ui.theme.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * GuestDashboard - Simplified dashboard for unregistered users to browse and buy lands.
 */
public class GuestDashboard extends JFrame {

    private JPanel contentPanel;
    private JPanel sidebarPanel;

    public GuestDashboard() {
        initComponents();
        showBrowsePanel();
    }

    private void initComponents() {
        setTitle("Land_Link — Guest Browsing");
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

        JLabel roleLabel = new JLabel("Guest Access");
        roleLabel.setFont(ThemeColors.FONT_SMALL);
        roleLabel.setForeground(ThemeColors.TEXT_MUTED);
        roleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        logoPanel.add(logoLabel);
        logoPanel.add(Box.createVerticalStrut(3));
        logoPanel.add(roleLabel);

        sidebarPanel.add(logoPanel);
        sidebarPanel.add(Box.createVerticalStrut(10));

        // Browse Menu Item
        JPanel item = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(79, 172, 254, 20));
                g2.fillRoundRect(8, 2, getWidth() - 16, getHeight() - 4, 10, 10);
                g2.setColor(ThemeColors.PRIMARY);
                g2.fillRoundRect(0, 6, 4, getHeight() - 12, 4, 4);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        item.setOpaque(false);
        item.setLayout(new BorderLayout());
        item.setBorder(new EmptyBorder(12, 20, 12, 20));
        item.setMaximumSize(new Dimension(ThemeColors.SIDEBAR_WIDTH, 48));

        JLabel itemLabel = new JLabel("🔍  Browse Lands");
        itemLabel.setFont(ThemeColors.FONT_BODY);
        itemLabel.setForeground(ThemeColors.PRIMARY);
        item.add(itemLabel);

        sidebarPanel.add(item);
        sidebarPanel.add(Box.createVerticalGlue());

        // Back to Login at bottom
        JPanel userInfoPanel = new JPanel();
        userInfoPanel.setOpaque(false);
        userInfoPanel.setLayout(new BoxLayout(userInfoPanel, BoxLayout.Y_AXIS));
        userInfoPanel.setBorder(new EmptyBorder(15, 20, 25, 20));
        userInfoPanel.setMaximumSize(new Dimension(ThemeColors.SIDEBAR_WIDTH, 100));

        JSeparator sep = new JSeparator();
        sep.setForeground(ThemeColors.BORDER);
        sep.setMaximumSize(new Dimension(200, 1));
        sep.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel promptLabel = new JLabel("Want to list a property?");
        promptLabel.setFont(ThemeColors.FONT_SMALL);
        promptLabel.setForeground(ThemeColors.TEXT_SECONDARY);
        promptLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        // Padding removed so it aligns perfectly with the button

        StyledButton loginBtn = StyledButton.secondary("Login / Register");
        loginBtn.setMaximumSize(new Dimension(220, 36));
        loginBtn.setPreferredSize(new Dimension(220, 36));
        loginBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        loginBtn.addActionListener(e -> {
            dispose();
            new LoginFrame().setVisible(true);
        });

        userInfoPanel.add(sep);
        userInfoPanel.add(Box.createVerticalStrut(15));
        userInfoPanel.add(promptLabel);
        userInfoPanel.add(Box.createVerticalStrut(15));
        userInfoPanel.add(loginBtn);

        sidebarPanel.add(userInfoPanel);

        // === CONTENT AREA ===
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(ThemeColors.BG_MAIN);

        mainPanel.add(sidebarPanel, BorderLayout.WEST);
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        setContentPane(mainPanel);
    }

    private void showBrowsePanel() {
        contentPanel.removeAll();
        contentPanel.add(new BrowseLandsPanel(), BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }
}
