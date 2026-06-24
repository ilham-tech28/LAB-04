package com.landlink.ui.theme;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

/**
 * StyledButton - Custom styled button with hover animation and gradient.
 * Demonstrates Inheritance (extends JButton) and custom painting.
 */
public class StyledButton extends JButton {

    private Color bgColor;
    private Color hoverColor;
    private Color pressColor;
    private Color currentBg;
    private boolean isHovered = false;
    private boolean isPressed = false;
    private int radius = 10;
    private boolean useGradient = false;
    private float animProgress = 0f;
    private Timer animTimer;

    // Standard button
    public StyledButton(String text) {
        this(text, ThemeColors.PRIMARY, ThemeColors.PRIMARY_LIGHT, ThemeColors.PRIMARY_DARK);
    }

    // Custom color button
    public StyledButton(String text, Color bgColor, Color hoverColor, Color pressColor) {
        super(text);
        this.bgColor = bgColor;
        this.hoverColor = hoverColor;
        this.pressColor = pressColor;
        this.currentBg = bgColor;

        // Configure button
        setFont(ThemeColors.FONT_BUTTON);
        setForeground(Color.WHITE);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setOpaque(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setPreferredSize(new Dimension(160, 42));

        // Hover animation
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                isHovered = true;
                animateHover(true);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                isHovered = false;
                isPressed = false;
                setCursor(new Cursor(Cursor.HAND_CURSOR));
                animateHover(false);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                isPressed = true;
                setCursor(new Cursor(Cursor.WAIT_CURSOR));
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                isPressed = false;
                setCursor(new Cursor(Cursor.HAND_CURSOR));
                repaint();
            }
        });
    }

    // Animate hover transition
    private void animateHover(boolean hovering) {
        if (animTimer != null && animTimer.isRunning()) {
            animTimer.stop();
        }
        animTimer = new Timer(16, e -> {
            if (hovering) {
                animProgress = Math.min(1f, animProgress + 0.1f);
            } else {
                animProgress = Math.max(0f, animProgress - 0.1f);
            }
            currentBg = interpolateColor(bgColor, hoverColor, animProgress);
            repaint();
            if ((hovering && animProgress >= 1f) || (!hovering && animProgress <= 0f)) {
                ((Timer) e.getSource()).stop();
            }
        });
        animTimer.start();
    }

    // Interpolate between two colors
    private Color interpolateColor(Color c1, Color c2, float t) {
        int r = (int) (c1.getRed() + (c2.getRed() - c1.getRed()) * t);
        int g = (int) (c1.getGreen() + (c2.getGreen() - c1.getGreen()) * t);
        int b = (int) (c1.getBlue() + (c2.getBlue() - c1.getBlue()) * t);
        return new Color(
            Math.max(0, Math.min(255, r)),
            Math.max(0, Math.min(255, g)),
            Math.max(0, Math.min(255, b))
        );
    }

    // Enable gradient style
    public void setGradient(boolean gradient) {
        this.useGradient = gradient;
        repaint();
    }

    // Set corner radius
    public void setRadius(int radius) {
        this.radius = radius;
        repaint();
    }

    // Create a danger button
    public static StyledButton danger(String text) {
        return new StyledButton(text, ThemeColors.DANGER,
            new Color(255, 100, 110), new Color(200, 50, 60));
    }

    // Create a success button
    public static StyledButton success(String text) {
        return new StyledButton(text, ThemeColors.SUCCESS,
            new Color(70, 230, 135), new Color(30, 180, 95));
    }

    // Create a secondary/outline button
    public static StyledButton secondary(String text) {
        StyledButton btn = new StyledButton(text, ThemeColors.BG_CARD,
            ThemeColors.BG_CARD_HOVER, ThemeColors.BG_CARD);
        btn.setForeground(ThemeColors.TEXT_SECONDARY);
        return btn;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();
        int yOffset = isPressed ? 2 : 0;

        // Draw background
        if (isPressed) {
            g2.setColor(pressColor);
        } else if (useGradient && !isPressed) {
            GradientPaint gp = new GradientPaint(0, 0, ThemeColors.GRADIENT_START, w, h, ThemeColors.GRADIENT_END);
            g2.setPaint(gp);
        } else {
            g2.setColor(currentBg);
        }

        g2.fill(new RoundRectangle2D.Float(0, yOffset, w, h - yOffset, radius, radius));

        // Draw glow effect on hover
        if (isHovered && !isPressed) {
            g2.setColor(new Color(255, 255, 255, 20));
            g2.fill(new RoundRectangle2D.Float(0, 0, w, h / 2, radius, radius));
        }

        g2.dispose();
        
        Graphics textG = g.create();
        if (isPressed) {
            textG.translate(0, 2);
        }
        super.paintComponent(textG);
        textG.dispose();
    }
}
