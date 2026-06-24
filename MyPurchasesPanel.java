package com.landlink.ui.user;

import com.landlink.model.Transaction;
import com.landlink.service.AuthService;
import com.landlink.service.TransactionService;
import com.landlink.ui.theme.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * MyPurchasesPanel - Shows the user's purchase history.
 */
public class MyPurchasesPanel extends JPanel {

    private TransactionService transactionService;
    private DefaultTableModel tableModel;

    public MyPurchasesPanel() {
        transactionService = new TransactionService();
        initComponents();
        loadPurchases();
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

        JLabel titleLabel = new JLabel("My Purchase History");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);

        JLabel subTitleLabel = new JLabel("View the lands you have successfully purchased.");
        subTitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subTitleLabel.setForeground(new Color(255, 255, 255, 200));

        textPanel.add(titleLabel);
        textPanel.add(Box.createVerticalStrut(5));
        textPanel.add(subTitleLabel);

        headerPanel.add(textPanel, BorderLayout.WEST);

        // Table
        String[] columns = {"ID", "Land", "Seller", "Amount (LKR)", "Status", "Date"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(tableModel);
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

        // Hide ID
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setPreferredWidth(0);

        // Status renderer
        table.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
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

        // Amount renderer (right-aligned)
        table.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                label.setHorizontalAlignment(SwingConstants.RIGHT);
                label.setForeground(ThemeColors.ACCENT);
                label.setFont(new Font("Segoe UI", Font.BOLD, 13));
                if (!isSelected) label.setBackground(ThemeColors.BG_CARD);
                return label;
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(ThemeColors.BORDER));
        scrollPane.getViewport().setBackground(ThemeColors.BG_CARD);

        // Refresh button
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottomPanel.setOpaque(false);
        StyledButton refreshBtn = StyledButton.secondary("🔄 Refresh");
        refreshBtn.setPreferredSize(new Dimension(120, 36));
        refreshBtn.addActionListener(e -> loadPurchases());
        bottomPanel.add(refreshBtn);

        add(headerPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void loadPurchases() {
        tableModel.setRowCount(0);
        try {
            int userId = AuthService.getCurrentUser().getId();
            List<Transaction> transactions = transactionService.getMyPurchases(userId);

            for (Transaction t : transactions) {
                tableModel.addRow(new Object[]{
                    t.getId(),
                    t.getLandTitle(),
                    t.getSellerName(),
                    String.format("%,.2f", t.getAmount()),
                    t.getStatus(),
                    t.getTransactionDate() != null ?
                        t.getTransactionDate().substring(0, Math.min(10, t.getTransactionDate().length())) : ""
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading purchases: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
