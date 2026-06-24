package com.landlink.ui.theme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.geom.RoundRectangle2D;

/**
 * StyledTextField - Custom styled text field with placeholder and focus animation.
 * Demonstrates Inheritance (extends JTextField).
 */
public class StyledTextField extends JTextField {

    private String placeholder;
    private Color borderColor;
    private Color focusBorderColor;
    private boolean focused = false;
    private int radius = 10;
    private float borderAlpha = 0f;
    private Timer focusTimer;

    public StyledTextField(String placeholder) {
        this.placeholder = placeholder;
        this.borderColor = ThemeColors.BORDER;
        this.focusBorderColor = ThemeColors.BORDER_FOCUS;

        // Style
        setFont(ThemeColors.FONT_BODY);
        setForeground(ThemeColors.TEXT_PRIMARY);
        setCaretColor(ThemeColors.PRIMARY);
        setOpaque(false);
        setBorder(new EmptyBorder(10, 15, 10, 15));
        setPreferredSize(new Dimension(280, 44));

        // Focus animation
        addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                focused = true;
                animateFocus(true);
            }

            @Override
            public void focusLost(FocusEvent e) {
                focused = false;
                animateFocus(false);
            }
        });
    }

    // Animate focus border
    private void animateFocus(boolean gaining) {
        if (focusTimer != null && focusTimer.isRunning()) {
            focusTimer.stop();
        }
        focusTimer = new Timer(16, e -> {
            if (gaining) {
                borderAlpha = Math.min(1f, borderAlpha + 0.15f);
            } else {
                borderAlpha = Math.max(0f, borderAlpha - 0.15f);
            }
            repaint();
            if ((gaining && borderAlpha >= 1f) || (!gaining && borderAlpha <= 0f)) {
                ((Timer) e.getSource()).stop();
            }
        });
        focusTimer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        // Background
        g2.setColor(ThemeColors.BG_INPUT);
        g2.fill(new RoundRectangle2D.Float(0, 0, w - 1, h - 1, radius, radius));

        // Border
        Color currentBorder = interpolateColor(borderColor, focusBorderColor, borderAlpha);
        g2.setColor(currentBorder);
        g2.setStroke(new BasicStroke(focused ? 2f : 1f));
        g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, w - 2, h - 2, radius, radius));

        g2.dispose();
        super.paintComponent(g);

        // Draw placeholder
        if (getText().isEmpty() && !focused) {
            Graphics2D g2p = (Graphics2D) g.create();
            g2p.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2p.setColor(ThemeColors.TEXT_MUTED);
            g2p.setFont(getFont());
            Insets insets = getInsets();
            g2p.drawString(placeholder, insets.left, h / 2 + g2p.getFontMetrics().getAscent() / 2 - 2);
            g2p.dispose();
        }
    }

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

    public String getPlaceholder() {
        return placeholder;
    }

    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
        repaint();
    }

    /**
     * Create a styled password field with same aesthetics.
     */
    public static JPasswordField createPasswordField(String placeholder) {
        JPasswordField field = new JPasswordField() {
            private float borderAlpha = 0f;
            private boolean focused = false;
            private Timer focusTimer;

            {
                setFont(ThemeColors.FONT_BODY);
                setForeground(ThemeColors.TEXT_PRIMARY);
                setCaretColor(ThemeColors.PRIMARY);
                setOpaque(false);
                setBorder(new EmptyBorder(10, 15, 10, 15));
                setPreferredSize(new Dimension(280, 44));

                addFocusListener(new FocusAdapter() {
                    @Override
                    public void focusGained(FocusEvent e) {
                        focused = true;
                        animateFocus(true);
                    }
                    @Override
                    public void focusLost(FocusEvent e) {
                        focused = false;
                        animateFocus(false);
                    }
                });
            }

            private void animateFocus(boolean gaining) {
                if (focusTimer != null && focusTimer.isRunning()) focusTimer.stop();
                focusTimer = new Timer(16, e -> {
                    borderAlpha = gaining ? Math.min(1f, borderAlpha + 0.15f) : Math.max(0f, borderAlpha - 0.15f);
                    repaint();
                    if ((gaining && borderAlpha >= 1f) || (!gaining && borderAlpha <= 0f))
                        ((Timer)e.getSource()).stop();
                });
                focusTimer.start();
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth(), h = getHeight();
                g2.setColor(ThemeColors.BG_INPUT);
                g2.fill(new java.awt.geom.RoundRectangle2D.Float(0, 0, w-1, h-1, 10, 10));
                Color border = new Color(
                    (int)(ThemeColors.BORDER.getRed() + (ThemeColors.BORDER_FOCUS.getRed() - ThemeColors.BORDER.getRed()) * borderAlpha),
                    (int)(ThemeColors.BORDER.getGreen() + (ThemeColors.BORDER_FOCUS.getGreen() - ThemeColors.BORDER.getGreen()) * borderAlpha),
                    (int)(ThemeColors.BORDER.getBlue() + (ThemeColors.BORDER_FOCUS.getBlue() - ThemeColors.BORDER.getBlue()) * borderAlpha)
                );
                g2.setColor(border);
                g2.setStroke(new BasicStroke(focused ? 2f : 1f));
                g2.draw(new java.awt.geom.RoundRectangle2D.Float(0.5f, 0.5f, w-2, h-2, 10, 10));
                g2.dispose();
                super.paintComponent(g);
                if (getPassword().length == 0 && !focused) {
                    Graphics2D g2p = (Graphics2D) g.create();
                    g2p.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                    g2p.setColor(ThemeColors.TEXT_MUTED);
                    g2p.setFont(getFont());
                    Insets insets = getInsets();
                    g2p.drawString(placeholder, insets.left, h/2 + g2p.getFontMetrics().getAscent()/2 - 2);
                    g2p.dispose();
                }
            }
        };
        return field;
    }
}
