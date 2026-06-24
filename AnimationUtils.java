package com.landlink.ui.theme;

import javax.swing.*;
import java.awt.*;

/**
 * AnimationUtils - Utility class for UI animations.
 * Provides fade-in, slide-in, and pulse effects.
 */
public class AnimationUtils {

    /**
     * Fade in a component from transparent to opaque.
     */
    public static void fadeIn(JComponent component, int durationMs) {
        component.setVisible(true);
        Timer timer = new Timer(16, null);
        final float[] opacity = {0f};
        final long[] startTime = {System.currentTimeMillis()};

        timer.addActionListener(e -> {
            long elapsed = System.currentTimeMillis() - startTime[0];
            float progress = Math.min(1f, (float) elapsed / durationMs);
            opacity[0] = progress;

            // Set opacity using AlphaComposite trick
            component.putClientProperty("fadeOpacity", opacity[0]);
            component.repaint();

            if (progress >= 1f) {
                timer.stop();
            }
        });
        timer.start();
    }

    /**
     * Slide in a component from bottom.
     */
    public static void slideInFromBottom(JComponent component, int durationMs) {
        int targetY = component.getY();
        int startY = targetY + 50;

        Timer timer = new Timer(16, null);
        final long[] startTime = {System.currentTimeMillis()};

        timer.addActionListener(e -> {
            long elapsed = System.currentTimeMillis() - startTime[0];
            float progress = Math.min(1f, (float) elapsed / durationMs);

            // Ease out cubic
            float eased = 1 - (1 - progress) * (1 - progress) * (1 - progress);

            int currentY = (int) (startY + (targetY - startY) * eased);
            component.setLocation(component.getX(), currentY);
            component.repaint();

            if (progress >= 1f) {
                component.setLocation(component.getX(), targetY);
                timer.stop();
            }
        });
        timer.start();
    }

    /**
     * Animate a JFrame appearing with a scale effect.
     */
    public static void animateFrameOpen(JFrame frame) {
        try {
            frame.setOpacity(0f);
        } catch (Exception ex) {
            // Opacity might not be supported for decorated frames on this platform
        }
        frame.setVisible(true);

        Timer timer = new Timer(16, null);
        final long[] startTime = {System.currentTimeMillis()};
        final int duration = 300;

        timer.addActionListener(e -> {
            long elapsed = System.currentTimeMillis() - startTime[0];
            float progress = Math.min(1f, (float) elapsed / duration);
            float eased = 1 - (1 - progress) * (1 - progress);

            try {
                frame.setOpacity(eased);
            } catch (Exception ex) {
                // Opacity might not be supported
            }

            if (progress >= 1f) {
                try {
                    frame.setOpacity(1f);
                } catch (Exception ex) {
                    // ignore
                }
                timer.stop();
            }
        });
        timer.start();
    }

    /**
     * Create a pulsating glow effect on a component.
     */
    public static Timer createPulse(JComponent component, Color glowColor, int intervalMs) {
        Timer timer = new Timer(intervalMs, null);
        final float[] pulse = {0f};
        final boolean[] growing = {true};

        timer.addActionListener(e -> {
            if (growing[0]) {
                pulse[0] += 0.05f;
                if (pulse[0] >= 1f) growing[0] = false;
            } else {
                pulse[0] -= 0.05f;
                if (pulse[0] <= 0f) growing[0] = true;
            }

            int alpha = (int) (40 * pulse[0]);
            component.putClientProperty("pulseAlpha", alpha);
            component.putClientProperty("pulseColor", glowColor);
            component.repaint();
        });
        timer.start();
        return timer;
    }

    /**
     * Smoothly scroll a JScrollPane to the top.
     */
    public static void scrollToTop(JScrollPane scrollPane) {
        SwingUtilities.invokeLater(() -> {
            JScrollBar vertical = scrollPane.getVerticalScrollBar();
            Timer timer = new Timer(16, null);
            timer.addActionListener(e -> {
                int current = vertical.getValue();
                int target = 0;
                int step = Math.max(1, (current - target) / 5);
                int newValue = current - step;
                if (newValue <= target) {
                    vertical.setValue(target);
                    timer.stop();
                } else {
                    vertical.setValue(newValue);
                }
            });
            timer.start();
        });
    }

    /**
     * Create a loading spinner label.
     */
    public static JLabel createLoadingLabel(String text) {
        JLabel label = new JLabel(text + " ⟳");
        label.setFont(ThemeColors.FONT_BODY);
        label.setForeground(ThemeColors.TEXT_SECONDARY);
        label.setHorizontalAlignment(SwingConstants.CENTER);

        Timer timer = new Timer(100, null);
        final String[] spinChars = {"⠋", "⠙", "⠹", "⠸", "⠼", "⠴", "⠦", "⠧", "⠇", "⠏"};
        final int[] index = {0};
        timer.addActionListener(e -> {
            label.setText(text + " " + spinChars[index[0]]);
            index[0] = (index[0] + 1) % spinChars.length;
        });
        timer.start();
        label.putClientProperty("spinnerTimer", timer);

        return label;
    }
}
