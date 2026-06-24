package com.landlink.ui.user;

import com.landlink.model.Land;
import com.landlink.service.AuthService;
import com.landlink.service.ImageService;
import com.landlink.service.LandService;
import com.landlink.ui.theme.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.util.List;

/**
 * BrowseLandsPanel - Redesigned ultra-modern web style grid view.
 */
public class BrowseLandsPanel extends JPanel {

    private LandService landService;
    private JPanel landsGrid;
    private StyledTextField searchField;
    private JComboBox<String> typeFilter;
    private StyledTextField minPriceField;
    private StyledTextField maxPriceField;

    public BrowseLandsPanel() {
        landService = new LandService();
        initComponents();
        loadLands(null, null, null, null);
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(ThemeColors.BG_MAIN);

        // === HERO SECTION (Top Banner) ===
        GradientPanel heroPanel = new GradientPanel(new Color(25, 35, 60), ThemeColors.PRIMARY_DARK, ThemeColors.PRIMARY);
        heroPanel.startAnimation();
        heroPanel.setPreferredSize(new Dimension(0, 280));
        heroPanel.setLayout(new BoxLayout(heroPanel, BoxLayout.Y_AXIS));
        heroPanel.setBorder(new EmptyBorder(30, 40, 30, 40));

        JLabel titleLabel = new JLabel("Discover Premium Real Estate");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subTitleLabel = new JLabel("Find the perfect land for your next big project or dream home.");
        subTitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subTitleLabel.setForeground(new Color(255, 255, 255, 200));
        subTitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Search Bar Container
        JPanel searchContainer = new JPanel();
        searchContainer.setLayout(new BoxLayout(searchContainer, BoxLayout.Y_AXIS));
        searchContainer.setOpaque(false);
        searchContainer.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel row1 = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        row1.setOpaque(false);

        searchField = new StyledTextField("Enter location or keyword...");
        searchField.setPreferredSize(new Dimension(300, 46));
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 16));

        typeFilter = new JComboBox<>(new String[]{"All Types", "Residential", "Commercial", "Agricultural", "Industrial", "Mixed Use", "Bare Land", "Villa/House"});
        typeFilter.setFont(ThemeColors.FONT_BODY);
        typeFilter.setPreferredSize(new Dimension(150, 46));
        typeFilter.setBackground(Color.WHITE);
        typeFilter.setForeground(ThemeColors.TEXT_PRIMARY);

        row1.add(searchField);
        row1.add(typeFilter);

        JPanel row2 = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        row2.setOpaque(false);

        minPriceField = new StyledTextField("Min Price");
        minPriceField.setPreferredSize(new Dimension(120, 46));

        maxPriceField = new StyledTextField("Max Price");
        maxPriceField.setPreferredSize(new Dimension(120, 46));

        StyledButton searchBtn = new StyledButton("Search");
        searchBtn.setGradient(true);
        searchBtn.setPreferredSize(new Dimension(120, 46));
        searchBtn.addActionListener(e -> performSearch());

        StyledButton resetBtn = new StyledButton("Reset");
        resetBtn.setPreferredSize(new Dimension(90, 46));
        resetBtn.setBackground(new Color(255, 255, 255, 40));
        resetBtn.setForeground(Color.WHITE);
        resetBtn.addActionListener(e -> {
            searchField.setText("");
            typeFilter.setSelectedIndex(0);
            minPriceField.setText("");
            maxPriceField.setText("");
            loadLands(null, null, null, null);
        });

        row2.add(minPriceField);
        row2.add(maxPriceField);
        row2.add(searchBtn);
        row2.add(resetBtn);

        searchContainer.add(row1);
        searchContainer.add(Box.createVerticalStrut(15));
        searchContainer.add(row2);

        heroPanel.add(titleLabel);
        heroPanel.add(Box.createVerticalStrut(10));
        heroPanel.add(subTitleLabel);
        heroPanel.add(Box.createVerticalStrut(35));
        heroPanel.add(searchContainer);

        // === LANDS GRID ===
        landsGrid = new JPanel();
        landsGrid.setOpaque(false);
        landsGrid.setLayout(new FlowLayout(FlowLayout.LEFT, 25, 25)); // Use FlowLayout for wrapping cards

        JPanel gridWrapper = new JPanel(new BorderLayout());
        gridWrapper.setOpaque(false);
        gridWrapper.setBorder(new EmptyBorder(10, 20, 30, 20));
        gridWrapper.add(landsGrid, BorderLayout.CENTER);

        JScrollPane scrollPane = new JScrollPane(gridWrapper);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(20);

        add(heroPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void performSearch() {
        try {
            String keyword = searchField.getText().trim();
            String type = (String) typeFilter.getSelectedItem();
            if (type.equals("All Types")) type = null;
            
            Double minPrice = null;
            Double maxPrice = null;

            if (!minPriceField.getText().trim().isEmpty()) {
                minPrice = Double.parseDouble(minPriceField.getText().trim());
            }
            if (!maxPriceField.getText().trim().isEmpty()) {
                maxPrice = Double.parseDouble(maxPriceField.getText().trim());
            }

            loadLands(keyword.isEmpty() ? null : keyword, type, minPrice, maxPrice);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter valid numbers for price filters!",
                "Invalid Input", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void loadLands(String keyword, String type, Double minPrice, Double maxPrice) {
        landsGrid.removeAll();

        List<Land> lands;
        if (keyword != null || type != null || minPrice != null || maxPrice != null) {
            lands = landService.searchLands(keyword, type, minPrice, maxPrice);
        } else {
            lands = landService.getApprovedLands();
        }

        if (lands.isEmpty()) {
            JLabel emptyLabel = new JLabel("No properties found matching your criteria.");
            emptyLabel.setFont(new Font("Segoe UI", Font.PLAIN, 20));
            emptyLabel.setForeground(ThemeColors.TEXT_MUTED);
            emptyLabel.setBorder(new EmptyBorder(50, 50, 50, 50));
            landsGrid.add(emptyLabel);
        } else {
            for (Land land : lands) {
                landsGrid.add(createModernCard(land));
            }
        }

        landsGrid.revalidate();
        landsGrid.repaint();
    }

    private JPanel createModernCard(Land land) {
        // Fetch Image early
        Image img = null;
        if (land.getImagePaths() != null && !land.getImagePaths().isEmpty()) {
            ImageIcon icon = ImageService.getResizedImage(land.getImagePaths().get(0), 320, 200);
            if (icon != null) img = icon.getImage();
        }
        if (img == null) {
            img = ImageService.createPlaceholder(320, 200).getImage();
        }
        final Image cardImg = img;

        JPanel card = new JPanel() {
            private boolean hovered = false;

            {
                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        try {
                            if (AuthService.getCurrentUser() == null) {
                                JOptionPane.showMessageDialog(BrowseLandsPanel.this, 
                                    "Please register an account to view property details and make purchases.", 
                                    "Registration Required", JOptionPane.INFORMATION_MESSAGE);
                                Window ancestor = SwingUtilities.getWindowAncestor(BrowseLandsPanel.this);
                                if (ancestor != null) {
                                    ancestor.dispose();
                                }
                                new com.landlink.ui.RegisterFrame().setVisible(true);
                            } else {
                                Window ancestor = SwingUtilities.getWindowAncestor(BrowseLandsPanel.this);
                                JFrame parentFrame = (ancestor instanceof JFrame) ? (JFrame) ancestor : null;
                                LandDetailDialog dlg = new LandDetailDialog(parentFrame, land);
                                dlg.setVisible(true);
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            JOptionPane.showMessageDialog(BrowseLandsPanel.this,
                                "Error opening land details: " + ex.getMessage(),
                                "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }

                    @Override
                    public void mouseEntered(MouseEvent e) {
                        hovered = true;
                        setCursor(new Cursor(Cursor.HAND_CURSOR));
                        repaint();
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        hovered = false;
                        repaint();
                    }
                });
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth();
                int h = getHeight();
                int arc = 24;

                // Draw shadow
                if (hovered) {
                    g2.setColor(new Color(0, 0, 0, 40));
                    g2.fillRoundRect(2, 6, w - 4, h - 8, arc, arc);
                } else {
                    g2.setColor(new Color(0, 0, 0, 20));
                    g2.fillRoundRect(2, 4, w - 4, h - 6, arc, arc);
                }

                // Draw card background
                g2.setColor(ThemeColors.BG_CARD);
                g2.fillRoundRect(0, 0, w, h - 4, arc, arc);

                // Draw clipped image at the top
                Shape oldClip = g2.getClip();
                g2.setClip(new RoundRectangle2D.Float(0, 0, w, 200 + arc, arc, arc)); // Clip for top corners
                g2.drawImage(cardImg, 0, 0, w, 200, null);
                g2.setClip(oldClip);

                // Draw Type Badge over image
                g2.setColor(new Color(0, 0, 0, 150));
                g2.fillRoundRect(15, 15, 90, 28, 14, 14);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
                FontMetrics fm = g2.getFontMetrics();
                int textX = 15 + (90 - fm.stringWidth(land.getLandType())) / 2;
                g2.drawString(land.getLandType(), textX, 15 + fm.getAscent() + (28 - fm.getHeight()) / 2);

                // Draw RESERVED Badge if applicable
                if (com.landlink.model.Land.STATUS_RESERVED.equals(land.getStatus())) {
                    g2.setColor(new Color(255, 193, 7, 220)); // Warning Yellow/Orange
                    g2.fillRoundRect(w - 140, 15, 125, 28, 14, 14);
                    g2.setColor(Color.WHITE);
                    int resX = w - 140 + (125 - fm.stringWidth("Temporarily Reserved")) / 2;
                    g2.drawString("Temporarily Reserved", resX, 15 + fm.getAscent() + (28 - fm.getHeight()) / 2);
                }

                // Draw Border
                if (hovered) {
                    g2.setColor(ThemeColors.PRIMARY);
                    g2.setStroke(new BasicStroke(2f));
                    g2.drawRoundRect(1, 1, w - 2, h - 5, arc, arc);
                } else {
                    g2.setColor(ThemeColors.BORDER);
                    g2.setStroke(new BasicStroke(1f));
                    g2.drawRoundRect(0, 0, w - 1, h - 5, arc, arc);
                }

                g2.dispose();
            }
        };

        card.setPreferredSize(new Dimension(320, 360));
        card.setOpaque(false);
        card.setLayout(null); // Absolute positioning for text below image

        // Mouse forwarder - ensures clicks on labels are forwarded to the card
        MouseAdapter forwarder = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                card.dispatchEvent(SwingUtilities.convertMouseEvent((Component) e.getSource(), e, card));
            }
            @Override
            public void mouseEntered(MouseEvent e) {
                card.dispatchEvent(SwingUtilities.convertMouseEvent((Component) e.getSource(), e, card));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                card.dispatchEvent(SwingUtilities.convertMouseEvent((Component) e.getSource(), e, card));
            }
        };

        // Text elements below image (Y > 200)
        JLabel titleLabel = new JLabel(land.getTitle());
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(ThemeColors.TEXT_PRIMARY);
        titleLabel.setBounds(20, 215, 280, 25);
        titleLabel.addMouseListener(forwarder);

        JLabel locationLabel = new JLabel("📍 " + land.getLocation());
        locationLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        locationLabel.setForeground(ThemeColors.TEXT_SECONDARY);
        locationLabel.setBounds(18, 245, 280, 20);
        locationLabel.addMouseListener(forwarder);

        JLabel sizeLabel = new JLabel("📐 " + land.getFormattedSize());
        sizeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        sizeLabel.setForeground(ThemeColors.TEXT_MUTED);
        sizeLabel.setBounds(18, 265, 280, 20);
        sizeLabel.addMouseListener(forwarder);

        JLabel priceLabel = new JLabel(land.getFormattedPrice());
        priceLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        priceLabel.setForeground(ThemeColors.ACCENT);
        priceLabel.setBounds(20, 305, 280, 30);
        priceLabel.addMouseListener(forwarder);

        card.add(titleLabel);
        card.add(locationLabel);
        card.add(sizeLabel);
        card.add(priceLabel);

        return card;
    }

    private void showLandDetail(Land land) {
        Window window = SwingUtilities.getWindowAncestor(this);
        JFrame parentFrame = (window instanceof JFrame) ? (JFrame) window : null;
        LandDetailDialog dialog = new LandDetailDialog(parentFrame, land);
        dialog.setVisible(true);

        // Refresh after dialog closes
        loadLands(null, null, null, null);
    }
}
