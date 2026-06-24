package com.landlink.ui.admin;

import com.landlink.dao.UserDAO;
import com.landlink.model.Land;
import com.landlink.service.LandService;
import com.landlink.ui.theme.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * AdminOverviewPanel — The "Overview" dashboard screen for the Admin.
 *
 * ✏️ HOW TO EDIT:
 *   - Change stat card labels/colors  → see the "STAT CARDS" section below
 *   - Change welcome banner text      → see the "WELCOME BANNER" section below
 *   - Change the title text           → see the "TITLE" section below
 *   - Resize the banner               → change welcomeBanner.setPreferredSize(...)
 */
public class AdminOverviewPanel extends JPanel {

    // ──────────────────────────────────────────────────────────────────
    // SERVICES (provide live counts from the database)
    // ──────────────────────────────────────────────────────────────────
    private final UserDAO userDAO = new UserDAO();
    private final LandService landService = new LandService();

    public AdminOverviewPanel() {
        setLayout(new BorderLayout());
        setBackground(ThemeColors.BG_MAIN);
        setBorder(new EmptyBorder(25, 25, 25, 25));
        buildUI();
    }

    private void buildUI() {
        // ──────────────────────────────────────────────────────────────
        // TITLE
        // Change the overview page heading text here
        // ──────────────────────────────────────────────────────────────
        JLabel titleLabel = new JLabel("📊 Dashboard Overview");
        titleLabel.setFont(ThemeColors.FONT_SUBTITLE);
        titleLabel.setForeground(ThemeColors.TEXT_PRIMARY);
        titleLabel.setBorder(new EmptyBorder(0, 0, 20, 0));

        // ──────────────────────────────────────────────────────────────
        // LIVE DATA (fetched fresh every time this panel is shown)
        // ──────────────────────────────────────────────────────────────
        int totalUsers   = userDAO.countUsers();
        int totalLands   = landService.countAll();
        int pendingLands = landService.countByStatus(Land.STATUS_PENDING);
        int reservedLands = landService.countByStatus(Land.STATUS_RESERVED);

        // ──────────────────────────────────────────────────────────────
        // STAT CARDS
        // To change a card: (Label text, Value, Color)
        //   ThemeColors.PRIMARY  = Dark Green
        //   ThemeColors.INFO     = Blue
        //   ThemeColors.WARNING  = Amber/Yellow
        //   ThemeColors.SUCCESS  = Green
        //   ThemeColors.DANGER   = Red
        // ──────────────────────────────────────────────────────────────
        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 20));
        statsPanel.setOpaque(false);
        statsPanel.add(GradientPanel.createStatCard("Total Users",        String.valueOf(totalUsers),    ThemeColors.PRIMARY));
        statsPanel.add(GradientPanel.createStatCard("Total Listings",     String.valueOf(totalLands),    ThemeColors.INFO));
        statsPanel.add(GradientPanel.createStatCard("Pending Approvals",  String.valueOf(pendingLands),  ThemeColors.WARNING));
        statsPanel.add(GradientPanel.createStatCard("Reserved Lands",     String.valueOf(reservedLands), ThemeColors.SUCCESS));

        // ──────────────────────────────────────────────────────────────
        // WELCOME BANNER
        // Change the greeting text and sub-text below
        // To resize the banner, change setPreferredSize(new Dimension(width, height))
        // ──────────────────────────────────────────────────────────────
        GradientPanel welcomeBanner = new GradientPanel(new Color(25, 35, 60), ThemeColors.PRIMARY);
        welcomeBanner.startAnimation();
        welcomeBanner.setLayout(new BorderLayout());
        welcomeBanner.setBorder(new EmptyBorder(30, 30, 30, 30));
        welcomeBanner.setPreferredSize(new Dimension(800, 150));

        JLabel welcomeLabel = new JLabel("Welcome back, Administrator");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        welcomeLabel.setForeground(Color.WHITE);

        JLabel welcomeSubLabel = new JLabel(
            "System is running smoothly. You have " + pendingLands + " pending listings to review.");
        welcomeSubLabel.setFont(ThemeColors.FONT_BODY);
        welcomeSubLabel.setForeground(new Color(255, 255, 255, 200));

        JPanel welcomeTextPanel = new JPanel();
        welcomeTextPanel.setOpaque(false);
        welcomeTextPanel.setLayout(new BoxLayout(welcomeTextPanel, BoxLayout.Y_AXIS));
        welcomeTextPanel.add(welcomeLabel);
        welcomeTextPanel.add(Box.createVerticalStrut(5));
        welcomeTextPanel.add(welcomeSubLabel);
        welcomeBanner.add(welcomeTextPanel, BorderLayout.CENTER);

        // ──────────────────────────────────────────────────────────────
        // ASSEMBLE
        // ──────────────────────────────────────────────────────────────
        JPanel contentContainer = new JPanel(new BorderLayout(0, 30));
        contentContainer.setOpaque(false);
        contentContainer.add(statsPanel, BorderLayout.NORTH);
        contentContainer.add(welcomeBanner, BorderLayout.CENTER);

        add(titleLabel, BorderLayout.NORTH);
        add(contentContainer, BorderLayout.CENTER);
    }
}
