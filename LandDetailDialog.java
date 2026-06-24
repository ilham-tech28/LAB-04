package com.landlink.ui.user;

import com.landlink.model.Land;
import com.landlink.model.Message;
import com.landlink.dao.MessageDAO;
import com.landlink.service.AuthService;
import com.landlink.service.ImageService;
import com.landlink.ui.theme.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.List;

/**
 * LandDetailDialog - Full land details with image carousel and purchase option.
 */
public class LandDetailDialog extends JDialog {

    private Land land;
    private int currentImageIndex = 0;
    private JLabel imageLabel;
    private JLabel imageCountLabel;

    public LandDetailDialog(JFrame parent, Land land) {
        super(parent, "Land Details — " + land.getTitle(), true);
        this.land = land;
        initComponents();
    }

    private void initComponents() {
        setSize(850, 600);
        setLocationRelativeTo(getParent());
        setResizable(false);

        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(ThemeColors.BG_MAIN);

        // === LEFT: Image Carousel ===
        JPanel imagePanel = new JPanel(new BorderLayout());
        imagePanel.setBackground(ThemeColors.BG_DARK);
        imagePanel.setPreferredSize(new Dimension(350, 0));
        imagePanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Image display
        imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imageLabel.setVerticalAlignment(SwingConstants.CENTER);

        imageCountLabel = new JLabel("0 / 0");
        imageCountLabel.setFont(ThemeColors.FONT_SMALL);
        imageCountLabel.setForeground(ThemeColors.TEXT_MUTED);

        updateImage();

        // Navigation buttons
        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
        navPanel.setOpaque(false);

        StyledButton prevBtn = StyledButton.secondary("◀ Prev");
        prevBtn.setPreferredSize(new Dimension(90, 32));
        prevBtn.addActionListener(e -> {
            if (land.getImagePaths() != null && !land.getImagePaths().isEmpty()) {
                currentImageIndex = (currentImageIndex - 1 + land.getImagePaths().size()) % land.getImagePaths().size();
                updateImage();
            }
        });

        StyledButton nextBtn = StyledButton.secondary("Next ▶");
        nextBtn.setPreferredSize(new Dimension(90, 32));
        nextBtn.addActionListener(e -> {
            if (land.getImagePaths() != null && !land.getImagePaths().isEmpty()) {
                currentImageIndex = (currentImageIndex + 1) % land.getImagePaths().size();
                updateImage();
            }
        });



        navPanel.add(prevBtn);
        navPanel.add(imageCountLabel);
        navPanel.add(nextBtn);

        imagePanel.add(imageLabel, BorderLayout.CENTER);
        imagePanel.add(navPanel, BorderLayout.SOUTH);

        // === RIGHT: Details ===
        JPanel detailsPanel = new JPanel();
        detailsPanel.setBackground(ThemeColors.BG_MAIN);
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setBorder(new EmptyBorder(25, 25, 25, 25));

        // Status badge
        JLabel statusBadge = new JLabel("  " + land.getStatus() + "  ") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color statusColor = ThemeColors.getStatusColor(land.getStatus());
                g2.setColor(new Color(statusColor.getRed(), statusColor.getGreen(), statusColor.getBlue(), 30));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        statusBadge.setFont(new Font("Segoe UI", Font.BOLD, 11));
        statusBadge.setForeground(ThemeColors.getStatusColor(land.getStatus()));
        statusBadge.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Title
        JLabel titleLabel = new JLabel(land.getTitle());
        titleLabel.setFont(ThemeColors.FONT_SUBTITLE);
        titleLabel.setForeground(ThemeColors.TEXT_PRIMARY);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Type badge
        JLabel typeBadge = new JLabel(land.getLandType());
        typeBadge.setFont(ThemeColors.FONT_SMALL);
        typeBadge.setForeground(ThemeColors.PRIMARY_LIGHT);
        typeBadge.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Details rows - use GridLayout for proper alignment
        JPanel detailRows = new JPanel(new GridLayout(5, 2, 10, 8));
        detailRows.setOpaque(false);
        detailRows.setAlignmentX(Component.LEFT_ALIGNMENT);
        detailRows.setMaximumSize(new Dimension(400, 140));

        detailRows.add(createDetailLabel("📍 Location"));
        detailRows.add(createDetailValue(land.getLocation()));
        detailRows.add(createDetailLabel("📐 Size"));
        detailRows.add(createDetailValue(land.getFormattedSize()));
        detailRows.add(createDetailLabel("📞 Contact"));
        
        boolean canViewContact = false;
        if (AuthService.getCurrentUser() != null) {
            canViewContact = AuthService.getCurrentUser().isAdmin() || AuthService.getCurrentUser().getId() == land.getSellerId();
        }
        
        if (canViewContact) {
            detailRows.add(createDetailValue(land.getContactNumber()));
        } else {
            JLabel hiddenLabel = createDetailValue("Hidden");
            hiddenLabel.setForeground(ThemeColors.TEXT_MUTED);
            hiddenLabel.setFont(new Font("Segoe UI", Font.ITALIC, 13));
            detailRows.add(hiddenLabel);
        }
        detailRows.add(createDetailLabel("👤 Seller"));
        detailRows.add(createDetailValue(land.getSellerName() != null ? land.getSellerName() : "N/A"));
        detailRows.add(createDetailLabel("📅 Listed"));
        detailRows.add(createDetailValue(land.getCreatedAt() != null ?
            land.getCreatedAt().substring(0, Math.min(10, land.getCreatedAt().length())) : "N/A"));

        // Description
        JLabel descTitle = new JLabel("Description");
        descTitle.setFont(ThemeColors.FONT_HEADING);
        descTitle.setForeground(ThemeColors.TEXT_PRIMARY);
        descTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextArea descArea = new JTextArea(land.getDescription() != null ? land.getDescription() : "No description provided.");
        descArea.setFont(ThemeColors.FONT_SMALL);
        descArea.setForeground(ThemeColors.TEXT_SECONDARY);
        descArea.setBackground(ThemeColors.BG_MAIN);
        descArea.setEditable(false);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setBorder(null);
        descArea.setAlignmentX(Component.LEFT_ALIGNMENT);
        descArea.setMaximumSize(new Dimension(350, 60));

        // Price
        JLabel priceLabel = new JLabel(land.getFormattedPrice());
        priceLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        priceLabel.setForeground(ThemeColors.ACCENT);
        priceLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Buy button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        buttonPanel.setOpaque(false);
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        buttonPanel.setMaximumSize(new Dimension(450, 110));

        if (Land.STATUS_APPROVED.equals(land.getStatus()) || Land.STATUS_RESERVED.equals(land.getStatus())) {
            boolean isOwnLand = AuthService.getCurrentUser() != null &&
                                AuthService.getCurrentUser().getId() == land.getSellerId();

            if (!isOwnLand) {
                String btnText = Land.STATUS_RESERVED.equals(land.getStatus()) ? "🛒 Join Waiting List" : "🛒 Request for Purchase";
                StyledButton buyBtn = StyledButton.success(btnText);
                buyBtn.setPreferredSize(new Dimension(240, 44));
                buyBtn.addActionListener(e -> {
                    if (AuthService.getCurrentUser() == null) {
                        // Automatically redirect to registration
                        JOptionPane.showMessageDialog(this, 
                            "Please register an account to continue with your purchase.", 
                            "Registration Required", JOptionPane.INFORMATION_MESSAGE);
                        dispose();
                        Window ancestor = SwingUtilities.getWindowAncestor(this);
                        if (ancestor != null) {
                            ancestor.dispose();
                        }
                        new com.landlink.ui.RegisterFrame().setVisible(true);
                    } else {
                        PurchaseDialog purchaseDialog = new PurchaseDialog(
                            (JFrame) SwingUtilities.getWindowAncestor(this), land);
                        purchaseDialog.setVisible(true);
                        if (purchaseDialog.isPurchased()) {
                            dispose();
                        }
                    }
                });
                buttonPanel.add(buyBtn);


            } else {
                JLabel ownLabel = new JLabel("This is your listing");
                ownLabel.setFont(ThemeColors.FONT_SMALL);
                ownLabel.setForeground(ThemeColors.TEXT_MUTED);
                buttonPanel.add(ownLabel);
            }
        }

        StyledButton closeBtn = StyledButton.secondary("Close");
        closeBtn.setPreferredSize(new Dimension(80, 38));
        closeBtn.addActionListener(e -> dispose());
        buttonPanel.add(Box.createHorizontalStrut(10));
        buttonPanel.add(closeBtn);

        // Assemble
        detailsPanel.add(statusBadge);
        detailsPanel.add(Box.createVerticalStrut(8));
        detailsPanel.add(titleLabel);
        detailsPanel.add(Box.createVerticalStrut(3));
        detailsPanel.add(typeBadge);
        detailsPanel.add(Box.createVerticalStrut(15));
        detailsPanel.add(detailRows);
        detailsPanel.add(Box.createVerticalStrut(12));
        detailsPanel.add(descTitle);
        detailsPanel.add(Box.createVerticalStrut(5));
        detailsPanel.add(descArea);
        detailsPanel.add(Box.createVerticalGlue());
        detailsPanel.add(priceLabel);
        detailsPanel.add(Box.createVerticalStrut(12));
        detailsPanel.add(buttonPanel);

        mainPanel.add(imagePanel, BorderLayout.WEST);
        mainPanel.add(detailsPanel, BorderLayout.CENTER);

        setContentPane(mainPanel);
    }

    private void updateImage() {
        List<String> images = land.getImagePaths();
        if (images != null && !images.isEmpty()) {
            ImageIcon icon = ImageService.getResizedImage(images.get(currentImageIndex), 320, 350);
            if (icon != null) {
                imageLabel.setIcon(icon);
                imageLabel.setText("");
            } else {
                imageLabel.setIcon(ImageService.createPlaceholder(320, 350));
                imageLabel.setText("");
            }
            imageCountLabel.setText((currentImageIndex + 1) + " / " + images.size());
        } else {
            imageLabel.setIcon(ImageService.createPlaceholder(320, 350));
            imageLabel.setText("");
            imageCountLabel.setText("No images");
        }
    }

    private JLabel createDetailLabel(String label) {
        JLabel lbl = new JLabel(label);
        lbl.setFont(ThemeColors.FONT_SMALL);
        lbl.setForeground(ThemeColors.TEXT_MUTED);
        return lbl;
    }

    private JLabel createDetailValue(String value) {
        JLabel val = new JLabel(value != null && !value.isEmpty() ? value : "Not specified");
        val.setFont(ThemeColors.FONT_BODY);
        val.setForeground(ThemeColors.TEXT_PRIMARY);
        return val;
    }
}
