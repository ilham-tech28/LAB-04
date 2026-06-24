package com.landlink.ui.theme;

import java.awt.Color;
import java.awt.Font;

/**
 * ThemeColors - Centralized dark theme color palette for the entire application.
 * Provides a modern, premium look with carefully chosen colors.
 */
public class ThemeColors {

    // ============== Primary Colors ==============
    public static final Color PRIMARY = new Color(43, 90, 43);       // Dark Forest Green
    public static final Color PRIMARY_DARK = new Color(28, 76, 39);  // Deep Logo Green
    public static final Color PRIMARY_LIGHT = new Color(110, 144, 64); // Light Field Green

    // ============== Accent Colors ==============
    public static final Color ACCENT = new Color(148, 103, 56);      // Earthy Brown
    public static final Color ACCENT_DARK = new Color(0, 180, 150);    // Dark teal
    public static final Color SUCCESS = new Color(46, 213, 115);       // Green
    public static final Color WARNING = new Color(255, 193, 7);        // Amber
    public static final Color DANGER = new Color(255, 71, 87);         // Red
    public static final Color INFO = new Color(116, 185, 255);         // Info blue

    // ============== Background Colors ==============
    public static final Color BG_DARK = new Color(245, 247, 250);         // Lightest background
    public static final Color BG_MAIN = new Color(240, 244, 248);         // Main background (light)
    public static final Color BG_CARD = new Color(255, 255, 255);         // Card background (white)
    public static final Color BG_CARD_HOVER = new Color(248, 250, 252);   // Card hover
    public static final Color BG_INPUT = new Color(255, 255, 255);        // Input field bg
    public static final Color BG_SIDEBAR = new Color(255, 255, 255);      // Sidebar bg (white)

    // ============== Text Colors ==============
    public static final Color TEXT_PRIMARY = new Color(30, 40, 50);       // Main text (dark gray)
    public static final Color TEXT_SECONDARY = new Color(90, 100, 110);   // Secondary text
    public static final Color TEXT_MUTED = new Color(140, 150, 160);      // Muted text
    public static final Color TEXT_DARK = new Color(20, 25, 30);          // Darkest text

    // ============== Border Colors ==============
    public static final Color BORDER = new Color(220, 225, 230);          // Light borders
    public static final Color BORDER_FOCUS = new Color(79, 172, 254);  // Focus border (primary)

    // ============== Gradient Colors ==============
    public static final Color GRADIENT_START = new Color(79, 172, 254);  // Blue
    public static final Color GRADIENT_END = new Color(0, 242, 195);    // Teal
    public static final Color GRADIENT_PURPLE = new Color(135, 100, 250); // Purple accent

    // ============== Status Colors ==============
    public static final Color STATUS_PENDING = new Color(255, 193, 7);   // Yellow
    public static final Color STATUS_APPROVED = new Color(46, 213, 115); // Green
    public static final Color STATUS_SOLD = new Color(79, 172, 254);     // Blue
    public static final Color STATUS_REJECTED = new Color(255, 71, 87);  // Red

    // ============== Fonts ==============
    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 28);
    public static final Font FONT_SUBTITLE = new Font("Segoe UI", Font.BOLD, 20);
    public static final Font FONT_HEADING = new Font("Segoe UI", Font.BOLD, 16);
    public static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font FONT_BUTTON = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_LOGO = new Font("Segoe UI", Font.BOLD, 36);

    // ============== Dimensions ==============
    public static final int BORDER_RADIUS = 12;
    public static final int CARD_PADDING = 20;
    public static final int SIDEBAR_WIDTH = 240;

    // Get status color based on status string
    public static Color getStatusColor(String status) {
        if (status == null) return TEXT_MUTED;
        switch (status.toUpperCase()) {
            case "PENDING":  return STATUS_PENDING;
            case "APPROVED": return STATUS_APPROVED;
            case "SOLD":     return STATUS_SOLD;
            case "RESERVED": return WARNING;
            case "REJECTED": return STATUS_REJECTED;
            case "COMPLETED": return STATUS_APPROVED;
            default:         return TEXT_MUTED;
        }
    }
}
