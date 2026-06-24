package com.landlink.ui.user;

import com.landlink.model.Land;
import com.landlink.service.AuthService;
import com.landlink.service.PurchaseRequestService;
import com.landlink.ui.theme.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * PurchaseDialog - Submits a buy REQUEST (pending admin approval).
 */
public class PurchaseDialog extends JDialog {

    private Land land;
    private PurchaseRequestService requestService;
    private boolean purchased = false;

    public PurchaseDialog(JFrame parent, Land land) {
        super(parent, "Submit Purchase Request", true);
        this.land = land;
        this.requestService = new PurchaseRequestService();
        initComponents();
    }

    private void initComponents() {
        setSize(450, 520);
        setLocationRelativeTo(getParent());
        setResizable(false);

        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(ThemeColors.BG_MAIN);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(new EmptyBorder(30, 35, 30, 35));
        mainPanel.setOpaque(false);
        mainPanel.setBackground(ThemeColors.BG_MAIN);

        // Icon
        JLabel iconLabel = new JLabel("📋");
        iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 48));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Title
        JLabel titleLabel = new JLabel("Submit Purchase Request");
        titleLabel.setFont(ThemeColors.FONT_SUBTITLE);
        titleLabel.setForeground(ThemeColors.TEXT_PRIMARY);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Your request will be reviewed by an admin.");
        subtitleLabel.setFont(ThemeColors.FONT_SMALL);
        subtitleLabel.setForeground(ThemeColors.TEXT_MUTED);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Land info card
        JPanel infoCard = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(ThemeColors.BG_CARD);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
                g2.setColor(ThemeColors.BORDER);
                g2.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 12, 12));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        infoCard.setOpaque(false);
        infoCard.setLayout(new BoxLayout(infoCard, BoxLayout.Y_AXIS));
        infoCard.setBorder(new EmptyBorder(15, 20, 15, 20));
        infoCard.setAlignmentX(Component.CENTER_ALIGNMENT);
        infoCard.setMaximumSize(new Dimension(380, 170));

        infoCard.add(createInfoRow("Land", land.getTitle()));
        infoCard.add(createInfoRow("Location", land.getLocation()));
        infoCard.add(createInfoRow("Size", land.getFormattedSize()));
        infoCard.add(createInfoRow("Type", land.getLandType()));
        infoCard.add(Box.createVerticalStrut(8));

        JSeparator sep = new JSeparator();
        sep.setForeground(ThemeColors.BORDER);
        sep.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoCard.add(sep);
        infoCard.add(Box.createVerticalStrut(8));

        JPanel priceRow = new JPanel(new BorderLayout());
        priceRow.setOpaque(false);
        priceRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        priceRow.setMaximumSize(new Dimension(340, 30));

        JLabel priceTitle = new JLabel("Asking Price");
        priceTitle.setFont(ThemeColors.FONT_HEADING);
        priceTitle.setForeground(ThemeColors.TEXT_PRIMARY);

        JLabel priceValue = new JLabel(land.getFormattedPrice());
        priceValue.setFont(new Font("Segoe UI", Font.BOLD, 20));
        priceValue.setForeground(ThemeColors.ACCENT);

        priceRow.add(priceTitle, BorderLayout.WEST);
        priceRow.add(priceValue, BorderLayout.EAST);
        infoCard.add(priceRow);

        // Info note
        JLabel noteLabel = new JLabel("<html><center>ℹ️ After submitting, the admin will review your<br>request and contact you with an agreement date.</center></html>");
        noteLabel.setFont(ThemeColors.FONT_SMALL);
        noteLabel.setForeground(ThemeColors.TEXT_MUTED);
        noteLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        noteLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        StyledButton cancelBtn = StyledButton.secondary("Cancel");
        cancelBtn.setPreferredSize(new Dimension(120, 42));
        cancelBtn.addActionListener(e -> dispose());

        StyledButton submitBtn = StyledButton.success("📋 Submit Request");
        submitBtn.setPreferredSize(new Dimension(180, 42));
        submitBtn.addActionListener(e -> handleSubmit());

        buttonPanel.add(cancelBtn);
        buttonPanel.add(submitBtn);

        // Assemble
        mainPanel.add(iconLabel);
        mainPanel.add(Box.createVerticalStrut(8));
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createVerticalStrut(5));
        mainPanel.add(subtitleLabel);
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(infoCard);
        mainPanel.add(Box.createVerticalStrut(15));
        mainPanel.add(noteLabel);
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(buttonPanel);

        setContentPane(mainPanel);
    }

    private JPanel createInfoRow(String label, String value) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(340, 22));

        JLabel lblComp = new JLabel(label);
        lblComp.setFont(ThemeColors.FONT_SMALL);
        lblComp.setForeground(ThemeColors.TEXT_MUTED);

        JLabel valComp = new JLabel(value);
        valComp.setFont(ThemeColors.FONT_SMALL);
        valComp.setForeground(ThemeColors.TEXT_PRIMARY);

        row.add(lblComp, BorderLayout.WEST);
        row.add(valComp, BorderLayout.EAST);

        return row;
    }

    private void handleSubmit() {
        try {
            int buyerId = AuthService.getCurrentUser().getId();
            requestService.submitRequest(land.getId(), buyerId);

            purchased = true;
            
            if (com.landlink.model.Land.STATUS_RESERVED.equals(land.getStatus())) {
                JOptionPane.showMessageDialog(this,
                    "✅ Your request has been placed on the waiting list!\n\n" +
                    "This land is temporarily reserved for another buyer's final meeting.\n" +
                    "If their transaction falls through, the admin will review your request.",
                    "Waiting List", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                    "✅ Your purchase request has been submitted!\n\n" +
                    "The admin will review your request and you will\n" +
                    "receive a message in your Inbox with the outcome.",
                    "Request Submitted", JOptionPane.INFORMATION_MESSAGE);
            }
            dispose();

        } catch (IllegalArgumentException | IllegalStateException ex) {
            JOptionPane.showMessageDialog(this, "⚠️ " + ex.getMessage(),
                "Cannot Submit", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "❌ Error: " + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isPurchased() {
        return purchased;
    }
}
