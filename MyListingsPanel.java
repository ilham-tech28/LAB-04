package com.landlink.ui.user;

import com.landlink.model.Land;
import com.landlink.service.AuthService;
import com.landlink.service.ImageService;
import com.landlink.service.LandService;
import com.landlink.ui.theme.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * MyListingsPanel - Shows the user's own land listings with status and delete option.
 */
public class MyListingsPanel extends JPanel {

    private LandService landService;
    private JTable table;
    private DefaultTableModel tableModel;

    public MyListingsPanel() {
        landService = new LandService();
        initComponents();
        loadMyLands();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(ThemeColors.BG_MAIN);
        setBorder(new EmptyBorder(25, 25, 25, 25));

        // === HEADER ===
        GradientPanel headerPanel = new GradientPanel(new Color(25, 35, 60), ThemeColors.PRIMARY_DARK, ThemeColors.PRIMARY);
        headerPanel.startAnimation();
        headerPanel.setPreferredSize(new Dimension(0, 100));
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setBorder(new EmptyBorder(20, 30, 20, 30));

        JPanel textPanel = new JPanel();
        textPanel.setOpaque(false);
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel("My Property Listings");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);

        JLabel subTitleLabel = new JLabel("Manage your submitted properties and check their approval status.");
        subTitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subTitleLabel.setForeground(new Color(255, 255, 255, 200));

        textPanel.add(titleLabel);
        textPanel.add(Box.createVerticalStrut(5));
        textPanel.add(subTitleLabel);

        StyledButton refreshBtn = StyledButton.secondary("🔄 Refresh");
        refreshBtn.setPreferredSize(new Dimension(120, 36));
        refreshBtn.addActionListener(e -> loadMyLands());

        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setOpaque(false);
        rightPanel.add(refreshBtn);
        headerPanel.add(textPanel, BorderLayout.WEST);
        headerPanel.add(rightPanel, BorderLayout.EAST);

        // Table
        String[] columns = {"ID", "Title", "Location", "Type", "Size (perches)", "Price (LKR)", "Status", "Created"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setFont(ThemeColors.FONT_BODY);
        table.setForeground(ThemeColors.TEXT_PRIMARY);
        table.setBackground(ThemeColors.BG_CARD);
        table.setGridColor(ThemeColors.BORDER);
        table.setRowHeight(40);
        table.setSelectionBackground(new Color(79, 172, 254, 30));
        table.setSelectionForeground(ThemeColors.TEXT_PRIMARY);
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(false);
        table.getTableHeader().setFont(ThemeColors.FONT_HEADING);
        table.getTableHeader().setBackground(ThemeColors.BG_SIDEBAR);
        table.getTableHeader().setForeground(ThemeColors.TEXT_SECONDARY);
        table.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, ThemeColors.BORDER));

        // Hide ID column
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setPreferredWidth(0);

        // Status column renderer
        table.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                label.setHorizontalAlignment(SwingConstants.CENTER);
                label.setForeground(ThemeColors.getStatusColor(value != null ? value.toString() : ""));
                label.setFont(new Font("Segoe UI", Font.BOLD, 12));
                if (!isSelected) {
                    label.setBackground(ThemeColors.BG_CARD);
                }
                return label;
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(ThemeColors.BORDER));
        scrollPane.getViewport().setBackground(ThemeColors.BG_CARD);

        // Bottom actions
        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        actionsPanel.setOpaque(false);

        StyledButton deleteBtn = StyledButton.danger("🗑️ Delete Selected");
        deleteBtn.setPreferredSize(new Dimension(160, 38));
        deleteBtn.addActionListener(e -> deleteSelected());

        StyledButton viewBtn = new StyledButton("👁️ View Details");
        viewBtn.setPreferredSize(new Dimension(140, 38));
        viewBtn.addActionListener(e -> viewSelected());

        actionsPanel.add(viewBtn);
        actionsPanel.add(deleteBtn);

        add(headerPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(actionsPanel, BorderLayout.SOUTH);
    }

    private void loadMyLands() {
        tableModel.setRowCount(0);
        try {
            int userId = AuthService.getCurrentUser().getId();
            List<Land> lands = landService.getMyLands(userId);

            for (Land land : lands) {
                tableModel.addRow(new Object[]{
                    land.getId(),
                    land.getTitle(),
                    land.getLocation(),
                    land.getLandType(),
                    String.format("%.2f", land.getLandSize()),
                    String.format("%,.2f", land.getPrice()),
                    land.getStatus(),
                    land.getCreatedAt() != null ? land.getCreatedAt().substring(0, Math.min(10, land.getCreatedAt().length())) : ""
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading your lands: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a land listing to delete.",
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int landId = (int) tableModel.getValueAt(row, 0);
        String title = (String) tableModel.getValueAt(row, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete '" + title + "'?",
            "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                if (landService.deleteLand(landId)) {
                    loadMyLands();
                    JOptionPane.showMessageDialog(this, "Land listing deleted successfully!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error deleting: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void viewSelected() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a land listing to view.",
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int landId = (int) tableModel.getValueAt(row, 0);
        Land land = landService.getLandById(landId);
        if (land != null) {
            Window window = SwingUtilities.getWindowAncestor(this);
            JFrame parentFrame = (window instanceof JFrame) ? (JFrame) window : null;
            LandDetailDialog dialog = new LandDetailDialog(parentFrame, land);
            dialog.setVisible(true);
        }
    }
}
