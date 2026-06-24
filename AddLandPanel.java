package com.landlink.ui.user;

import com.landlink.model.Land;
import com.landlink.service.AuthService;
import com.landlink.service.ImageService;
import com.landlink.service.LandService;
import com.landlink.ui.theme.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * AddLandPanel - Form for adding a new land listing with image upload.
 */
public class AddLandPanel extends JPanel {

    private LandService landService;
    private ImageService imageService;
    private UserDashboard parentDashboard;

    private StyledTextField titleField;
    private StyledTextField locationField;
    private StyledTextField sizeField;
    private StyledTextField priceField;
    private JTextArea descriptionArea;
    private JComboBox<String> typeCombo;
    private StyledTextField contactField;
    private JPanel imagesPanel;
    private List<File> selectedImages;
    private JLabel statusLabel;

    public AddLandPanel(UserDashboard parent) {
        this.parentDashboard = parent;
        this.landService = new LandService();
        this.imageService = new ImageService();
        this.selectedImages = new ArrayList<>();
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(ThemeColors.BG_MAIN);
        setBorder(new EmptyBorder(25, 25, 25, 25));

        // === HEADER ===
        GradientPanel headerPanel = new GradientPanel(new Color(25, 35, 60), ThemeColors.PRIMARY_DARK, ThemeColors.PRIMARY);
        headerPanel.startAnimation();
        headerPanel.setPreferredSize(new Dimension(0, 100));
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBorder(new EmptyBorder(20, 30, 20, 30));

        JLabel titleLabel = new JLabel("Add New Land Listing");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subTitleLabel = new JLabel("Fill in the details below to list your property.");
        subTitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subTitleLabel.setForeground(new Color(255, 255, 255, 200));
        subTitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        headerPanel.add(titleLabel);
        headerPanel.add(Box.createVerticalStrut(5));
        headerPanel.add(subTitleLabel);

        // Form panel in scrollable area
        JPanel formPanel = new JPanel();
        formPanel.setOpaque(false);
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));

        // Form card
        JPanel card = GradientPanel.createCard();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(25, 30, 25, 30));

        // Row 1: Title & Location
        JPanel row1 = createRow();
        titleField = createFormField("Land Title", 250);
        locationField = createFormField("Location (City/Area)", 250);
        row1.add(createFieldGroup("Title *", titleField));
        row1.add(Box.createHorizontalStrut(20));
        row1.add(createFieldGroup("Location *", locationField));

        // Row 2: Size, Price, Type
        JPanel row2 = createRow();
        sizeField = createFormField("Size in perches", 150);
        priceField = createFormField("Price in LKR", 150);
        String[] types = {"Residential", "Commercial", "Agricultural", "Industrial", "Mixed Use", "Bare Land", "Villa/House"};
        typeCombo = new JComboBox<>(types);
        typeCombo.setFont(ThemeColors.FONT_BODY);
        typeCombo.setPreferredSize(new Dimension(150, 44));
        typeCombo.setBackground(ThemeColors.BG_INPUT);
        typeCombo.setForeground(ThemeColors.TEXT_PRIMARY);

        row2.add(createFieldGroup("Land Size (perches) *", sizeField));
        row2.add(Box.createHorizontalStrut(20));
        row2.add(createFieldGroup("Price (LKR) *", priceField));
        row2.add(Box.createHorizontalStrut(20));
        row2.add(createFieldGroup("Land Type", typeCombo));

        // Row 3: Contact
        JPanel row3 = createRow();
        contactField = createFormField("Phone number", 250);
        row3.add(createFieldGroup("Contact Number *", contactField));

        // Description
        JPanel descGroup = new JPanel();
        descGroup.setOpaque(false);
        descGroup.setLayout(new BoxLayout(descGroup, BoxLayout.Y_AXIS));
        descGroup.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel descLabel = new JLabel("Description");
        descLabel.setFont(ThemeColors.FONT_SMALL);
        descLabel.setForeground(ThemeColors.TEXT_SECONDARY);
        descLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        descriptionArea = new JTextArea(4, 40);
        descriptionArea.setFont(ThemeColors.FONT_BODY);
        descriptionArea.setBackground(ThemeColors.BG_INPUT);
        descriptionArea.setForeground(ThemeColors.TEXT_PRIMARY);
        descriptionArea.setCaretColor(ThemeColors.PRIMARY);
        descriptionArea.setBorder(new EmptyBorder(10, 10, 10, 10));
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);

        JScrollPane descScroll = new JScrollPane(descriptionArea);
        descScroll.setBorder(BorderFactory.createLineBorder(ThemeColors.BORDER));
        descScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        descScroll.setMaximumSize(new Dimension(600, 100));

        descGroup.add(descLabel);
        descGroup.add(Box.createVerticalStrut(5));
        descGroup.add(descScroll);

        // Image upload section
        JPanel imageSection = new JPanel();
        imageSection.setOpaque(false);
        imageSection.setLayout(new BoxLayout(imageSection, BoxLayout.Y_AXIS));
        imageSection.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel imgLabel = new JLabel("Land Images (up to 5)");
        imgLabel.setFont(ThemeColors.FONT_SMALL);
        imgLabel.setForeground(ThemeColors.TEXT_SECONDARY);
        imgLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        imagesPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        imagesPanel.setOpaque(false);
        imagesPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        StyledButton addImageBtn = StyledButton.secondary("📷 Add Images");
        addImageBtn.setPreferredSize(new Dimension(140, 38));
        addImageBtn.addActionListener(e -> chooseImages());

        JPanel imgBtnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        imgBtnPanel.setOpaque(false);
        imgBtnPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        imgBtnPanel.add(addImageBtn);

        imageSection.add(imgLabel);
        imageSection.add(Box.createVerticalStrut(5));
        imageSection.add(imgBtnPanel);
        imageSection.add(imagesPanel);

        // Status label
        statusLabel = new JLabel(" ");
        statusLabel.setFont(ThemeColors.FONT_BODY);
        statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Submit button
        StyledButton submitBtn = new StyledButton("Submit Land Listing");
        submitBtn.setGradient(true);
        submitBtn.setMaximumSize(new Dimension(250, 44));
        submitBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        submitBtn.addActionListener(e -> handleSubmit());

        // Add all to card
        card.add(row1);
        card.add(Box.createVerticalStrut(15));
        card.add(row2);
        card.add(Box.createVerticalStrut(15));
        card.add(row3);
        card.add(Box.createVerticalStrut(15));
        card.add(descGroup);
        card.add(Box.createVerticalStrut(15));
        card.add(imageSection);
        card.add(Box.createVerticalStrut(10));
        card.add(statusLabel);
        card.add(Box.createVerticalStrut(10));
        card.add(submitBtn);

        formPanel.add(card);

        JScrollPane scroll = new JScrollPane(formPanel);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.setBackground(ThemeColors.BG_MAIN);
        scroll.getViewport().setBackground(ThemeColors.BG_MAIN);

        add(headerPanel, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
    }

    private JPanel createRow() {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(700, 75));
        return row;
    }

    private StyledTextField createFormField(String placeholder, int width) {
        StyledTextField field = new StyledTextField(placeholder);
        field.setPreferredSize(new Dimension(width, 44));
        return field;
    }

    private JPanel createFieldGroup(String label, JComponent field) {
        JPanel group = new JPanel();
        group.setOpaque(false);
        group.setLayout(new BoxLayout(group, BoxLayout.Y_AXIS));

        JLabel lbl = new JLabel(label);
        lbl.setFont(ThemeColors.FONT_SMALL);
        lbl.setForeground(ThemeColors.TEXT_SECONDARY);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        field.setAlignmentX(Component.LEFT_ALIGNMENT);

        group.add(lbl);
        group.add(Box.createVerticalStrut(5));
        group.add(field);

        return group;
    }

    private void chooseImages() {
        JFileChooser chooser = new JFileChooser();
        chooser.setMultiSelectionEnabled(true);
        chooser.setFileFilter(new FileNameExtensionFilter("Images", "jpg", "jpeg", "png", "gif", "bmp"));
        chooser.setDialogTitle("Select Land Images (max 5)");

        int result = chooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File[] files = chooser.getSelectedFiles();
            for (File file : files) {
                if (selectedImages.size() < 5) {
                    selectedImages.add(file);
                }
            }
            updateImagePreview();
        }
    }

    private void updateImagePreview() {
        imagesPanel.removeAll();
        for (int i = 0; i < selectedImages.size(); i++) {
            File file = selectedImages.get(i);
            final int index = i;

            JPanel imgCard = new JPanel(new BorderLayout());
            imgCard.setOpaque(false);
            imgCard.setPreferredSize(new Dimension(100, 80));

            ImageIcon icon = ImageService.getResizedImage(file.getAbsolutePath(), 100, 70);
            if (icon != null) {
                imgCard.add(new JLabel(icon), BorderLayout.CENTER);
            } else {
                imgCard.add(new JLabel(ImageService.createPlaceholder(100, 70)), BorderLayout.CENTER);
            }

            JButton removeBtn = new JButton("✕");
            removeBtn.setFont(new Font("Segoe UI", Font.BOLD, 10));
            removeBtn.setForeground(ThemeColors.DANGER);
            removeBtn.setBorderPainted(false);
            removeBtn.setContentAreaFilled(false);
            removeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            removeBtn.addActionListener(e -> {
                selectedImages.remove(index);
                updateImagePreview();
            });
            imgCard.add(removeBtn, BorderLayout.NORTH);

            imagesPanel.add(imgCard);
        }
        imagesPanel.revalidate();
        imagesPanel.repaint();
    }

    private void handleSubmit() {
        try {
            Land land = new Land(
                titleField.getText().trim(),
                locationField.getText().trim(),
                Double.parseDouble(sizeField.getText().trim()),
                Double.parseDouble(priceField.getText().trim()),
                descriptionArea.getText().trim(),
                (String) typeCombo.getSelectedItem(),
                AuthService.getCurrentUser().getId(),
                contactField.getText().trim()
            );

            // Save land first (without images)
            int landId = landService.addLand(land);

            if (landId > 0) {
                // Save images
                List<String> imagePaths = new ArrayList<>();
                for (File file : selectedImages) {
                    String path = imageService.saveImage(file, landId);
                    if (path != null) {
                        imagePaths.add(path);
                    }
                }

                // Update land with image paths (images are saved via DAO in saveImage)
                if (!imagePaths.isEmpty()) {
                    // Images are already saved in createLand via batch, but we need to save them separately
                    // since we got the landId after creating the land
                    land.setId(landId);
                    land.setImagePaths(imagePaths);
                    // Save image references to DB
                    new com.landlink.dao.LandDAO().deleteLand(landId); // Remove and re-add
                    land.setId(0);
                    landId = landService.addLand(land);
                    // Re-save images with proper paths
                    for (File file : selectedImages) {
                        imageService.saveImage(file, landId);
                    }
                }

                statusLabel.setText("✅ Land listing submitted for approval!");
                statusLabel.setForeground(ThemeColors.SUCCESS);

                // Clear form
                Timer delay = new Timer(2000, event -> {
                    if (parentDashboard != null) {
                        parentDashboard.switchToPanel("my-lands");
                    }
                });
                delay.setRepeats(false);
                delay.start();

            } else {
                statusLabel.setText("❌ Failed to add land listing.");
                statusLabel.setForeground(ThemeColors.DANGER);
            }

        } catch (NumberFormatException ex) {
            statusLabel.setText("⚠️ Please enter valid numbers for size and price!");
            statusLabel.setForeground(ThemeColors.WARNING);
        } catch (IllegalArgumentException ex) {
            statusLabel.setText("⚠️ " + ex.getMessage());
            statusLabel.setForeground(ThemeColors.WARNING);
        } catch (Exception ex) {
            statusLabel.setText("❌ Error: " + ex.getMessage());
            statusLabel.setForeground(ThemeColors.DANGER);
        }
    }
}
