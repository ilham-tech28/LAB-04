package com.landlink.ui.theme;

import javax.swing.*;
import java.awt.*;

/**
 * GradientPanel - A JPanel with an animated gradient background.
 * Demonstrates Inheritance (extends JPanel).
 */
public class GradientPanel extends JPanel {

    private Color color1;
    private Color color2;
    private Color color3;
    private float angle = 0;
    private boolean animated = false;
    private Timer animationTimer;

    // Two-color gradient
    public GradientPanel(Color color1, Color color2) {
        this.color1 = color1;
        this.color2 = color2;
        this.color3 = null;
        setOpaque(false);
    }

    // Three-color gradient
    public GradientPanel(Color color1, Color color2, Color color3) {
        this.color1 = color1;
        this.color2 = color2;
        this.color3 = color3;
        setOpaque(false);
    }

    // Default gradient (blue to teal)
    public GradientPanel() {
        this(ThemeColors.GRADIENT_START, ThemeColors.GRADIENT_END, ThemeColors.GRADIENT_PURPLE);
    }

    // Start animation
    public void startAnimation() {
        if (animated) return;
        animated = true;
        animationTimer = new Timer(50, e -> {
            angle += 0.5f;
            if (angle >= 360) angle = 0;
            repaint();
        });
        animationTimer.start();
    }

    // Stop animation
    public void stopAnimation() {
        animated = false;
        if (animationTimer != null) {
            animationTimer.stop();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        int w = getWidth();
        int h = getHeight();

        if (animated && color3 != null) {
            // Animated 3-color gradient
            double rad = Math.toRadians(angle);
            float x1 = (float) (w / 2 + w / 2 * Math.cos(rad));
            float y1 = (float) (h / 2 + h / 2 * Math.sin(rad));
            float x2 = (float) (w / 2 - w / 2 * Math.cos(rad));
            float y2 = (float) (h / 2 - h / 2 * Math.sin(rad));

            // First gradient layer
            GradientPaint gp1 = new GradientPaint(x1, y1, color1, x2, y2, color2);
            g2.setPaint(gp1);
            g2.fillRect(0, 0, w, h);

            // Overlay with transparency
            float x3 = (float) (w / 2 + w / 2 * Math.cos(rad + Math.PI / 3));
            float y3 = (float) (h / 2 + h / 2 * Math.sin(rad + Math.PI / 3));
            Color transparentColor3 = new Color(color3.getRed(), color3.getGreen(), color3.getBlue(), 100);
            GradientPaint gp2 = new GradientPaint(x3, y3, transparentColor3, w / 2f, h / 2f, new Color(0, 0, 0, 0));
            g2.setPaint(gp2);
            g2.fillRect(0, 0, w, h);

        } else {
            // Static gradient
            GradientPaint gp = new GradientPaint(0, 0, color1, w, h, color2);
            g2.setPaint(gp);
            g2.fillRect(0, 0, w, h);

            if (color3 != null) {
                Color transparentColor3 = new Color(color3.getRed(), color3.getGreen(), color3.getBlue(), 80);
                GradientPaint gp2 = new GradientPaint(w, 0, transparentColor3, 0, h, new Color(0, 0, 0, 0));
                g2.setPaint(gp2);
                g2.fillRect(0, 0, w, h);
            }
        }

        g2.dispose();
        super.paintComponent(g);
    }

    // Create a dark card panel with rounded corners
    public static JPanel createCard() {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(ThemeColors.BG_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), ThemeColors.BORDER_RADIUS, ThemeColors.BORDER_RADIUS);
                g2.setColor(ThemeColors.BORDER);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, ThemeColors.BORDER_RADIUS, ThemeColors.BORDER_RADIUS);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        card.setOpaque(false);
        return card;
    }

    // Create a stat card for dashboard
    public static JPanel createStatCard(String title, String value, Color accentColor) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Card background
                g2.setColor(ThemeColors.BG_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);

                // Accent bar on top
                g2.setColor(accentColor);
                g2.fillRoundRect(0, 0, getWidth(), 4, 4, 4);

                // Border
                g2.setColor(ThemeColors.BORDER);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        card.setOpaque(false);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        card.setPreferredSize(new Dimension(200, 120));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(ThemeColors.FONT_SMALL);
        titleLabel.setForeground(ThemeColors.TEXT_SECONDARY);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(ThemeColors.FONT_TITLE);
        valueLabel.setForeground(accentColor);
        valueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(titleLabel);
        card.add(Box.createVerticalStrut(8));
        card.add(valueLabel);

        return card;
    }
}
