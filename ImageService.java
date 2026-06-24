package com.landlink.service;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

/**
 * ImageService - Handles image file operations.
 * Manages copying, resizing, and retrieving land images.
 */
public class ImageService {

    private static final String IMAGE_DIR = "land_images";
    private static final int THUMBNAIL_WIDTH = 300;
    private static final int THUMBNAIL_HEIGHT = 200;

    public ImageService() {
        // Create image directory if it doesn't exist
        try {
            File dir = new File(IMAGE_DIR);
            if (!dir.exists()) {
                dir.mkdirs();
                System.out.println("✅ Image directory created: " + dir.getAbsolutePath());
            }
        } catch (SecurityException e) {
            System.err.println("❌ Error creating image directory: " + e.getMessage());
        }
    }

    /**
     * Copy an image file to the land_images directory.
     * @param sourceFile The source image file
     * @param landId The land ID for organizing images
     * @return The relative path to the saved image
     */
    public String saveImage(File sourceFile, int landId) {
        try {
            // Create land-specific directory
            String landDir = IMAGE_DIR + File.separator + "land_" + landId;
            File dir = new File(landDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            // Generate unique filename
            String fileName = System.currentTimeMillis() + "_" + sourceFile.getName();
            Path targetPath = Paths.get(landDir, fileName);

            // Copy file
            Files.copy(sourceFile.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("✅ Image saved: " + targetPath);

            return targetPath.toString();
        } catch (IOException e) {
            System.err.println("❌ Error saving image: " + e.getMessage());
            return null;
        }
    }

    /**
     * Get a resized ImageIcon for display.
     * @param imagePath Path to the image file
     * @param width Desired width
     * @param height Desired height
     * @return Resized ImageIcon, or null if error
     */
    public static ImageIcon getResizedImage(String imagePath, int width, int height) {
        try {
            File file = new File(imagePath);
            if (!file.exists()) {
                return null;
            }
            BufferedImage originalImage = ImageIO.read(file);
            if (originalImage == null) {
                return null;
            }
            Image scaledImage = originalImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(scaledImage);
        } catch (IOException e) {
            System.err.println("❌ Error loading image: " + e.getMessage());
            return null;
        }
    }

    /**
     * Get a thumbnail ImageIcon.
     */
    public static ImageIcon getThumbnail(String imagePath) {
        return getResizedImage(imagePath, THUMBNAIL_WIDTH, THUMBNAIL_HEIGHT);
    }

    /**
     * Create a placeholder image when no image is available.
     */
    public static ImageIcon createPlaceholder(int width, int height) {
        BufferedImage placeholder = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = placeholder.createGraphics();
        g2d.setColor(new java.awt.Color(45, 52, 65));
        g2d.fillRect(0, 0, width, height);
        g2d.setColor(new java.awt.Color(100, 120, 140));
        g2d.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 14));
        String text = "No Image";
        int textWidth = g2d.getFontMetrics().stringWidth(text);
        g2d.drawString(text, (width - textWidth) / 2, height / 2);
        g2d.dispose();
        return new ImageIcon(placeholder);
    }
}
