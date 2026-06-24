package com.landlink.ui.admin;

import com.landlink.model.Land;
import com.landlink.service.LandService;
import com.landlink.ui.theme.*;
import com.landlink.ui.user.LandDetailDialog;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * ManageLandsPanel - Admin panel to approve or reject land listings.
 */
public class ManageLandsPanel extends JPanel {

    private LandService landService;
    private JTable table;
    private DefaultTableModel tableModel;

    public ManageLandsPanel() {
        this.landService = new LandService();
        initComponents();
        loadLands();
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

        JLabel titleLabel = new JLabel("Manage Land Listings");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);

        JLabel subTitleLabel = new JLabel("Approve or reject properties submitted by users.");
        subTitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subTitleLabel.setForeground(new Color(255, 255, 255, 200));

        textPanel.add(titleLabel);
        textPanel.add(Box.createVerticalStrut(5));
        textPanel.add(subTitleLabel);

        StyledButton refreshBtn = StyledButton.secondary("🔄 Refresh");
        refreshBtn.setPreferredSize(new Dimension(120, 36));
        refreshBtn.addActionListener(e -> loadLands());

        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setOpaque(false);
        rightPanel.add(refreshBtn);
        headerPanel.add(textPanel, BorderLayout.WEST);
        headerPanel.add(rightPanel, BorderLayout.EAST);

        // Table
        String[] columns = {"ID", "Title", "Seller", "Type", "Size", "Price", "Status", "Date"};
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

        table.getColumnModel().getColumn(0).setMaxWidth(50);

        // Status renderer
        table.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                label.setHorizontalAlignment(SwingConstants.CENTER);
                label.setForeground(ThemeColors.getStatusColor(value != null ? value.toString() : ""));
                label.setFont(new Font("Segoe UI", Font.BOLD, 12));
                if (!isSelected) label.setBackground(ThemeColors.BG_CARD);
                return label;
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(ThemeColors.BORDER));
        scrollPane.getViewport().setBackground(ThemeColors.BG_CARD);

        // Actions
        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        actionsPanel.setOpaque(false);

        StyledButton viewBtn = new StyledButton("👁️ View Details");
        viewBtn.setPreferredSize(new Dimension(140, 38));
        viewBtn.addActionListener(e -> viewSelected());
        
        StyledButton approveBtn = StyledButton.success("✅ Approve");
        approveBtn.setPreferredSize(new Dimension(120, 38));
        approveBtn.addActionListener(e -> changeStatus(Land.STATUS_APPROVED));
        
        StyledButton rejectBtn = StyledButton.danger("❌ Reject");
        rejectBtn.setPreferredSize(new Dimension(120, 38));
        rejectBtn.addActionListener(e -> changeStatus(Land.STATUS_REJECTED));

        actionsPanel.add(viewBtn);
        actionsPanel.add(approveBtn);
        actionsPanel.add(rejectBtn);

        add(headerPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(actionsPanel, BorderLayout.SOUTH);
    }

    private void loadLands() {
        tableModel.setRowCount(0);
        try {
            List<Land> lands = landService.getAllLands();
            for (Land l : lands) {
                tableModel.addRow(new Object[]{
                    l.getId(),
                    l.getTitle(),
                    l.getSellerName(),
                    l.getLandType(),
                    String.format("%.2f", l.getLandSize()),
                    String.format("%,.2f", l.getPrice()),
                    l.getStatus(),
                    l.getCreatedAt() != null ? l.getCreatedAt().substring(0, 10) : ""
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading lands: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void viewSelected() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a land listing.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int landId = (int) tableModel.getValueAt(row, 0);
        Land land = landService.getLandById(landId);
        if (land != null) {
            Window window = SwingUtilities.getWindowAncestor(this);
            JFrame parentFrame = (window instanceof JFrame) ? (JFrame) window : null;
            new LandDetailDialog(parentFrame, land).setVisible(true);
        }
    }
    
    private void changeStatus(String status) {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a land listing.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int landId = (int) tableModel.getValueAt(row, 0);
        
        boolean success = false;
        if (Land.STATUS_APPROVED.equals(status)) {
            success = landService.approveLand(landId);
        } else if (Land.STATUS_REJECTED.equals(status)) {
            success = landService.rejectLand(landId);
        }
        
        if (success) {
            loadLands();
            JOptionPane.showMessageDialog(this, "Land status updated to " + status + ".", "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
