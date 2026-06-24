package com.landlink.ui.admin;

import com.landlink.dao.UserDAO;
import com.landlink.model.User;
import com.landlink.ui.theme.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * ManageUsersPanel - Admin panel to view and manage users.
 */
public class ManageUsersPanel extends JPanel {

    private UserDAO userDAO;
    private JTable table;
    private DefaultTableModel tableModel;

    public ManageUsersPanel() {
        this.userDAO = new UserDAO();
        initComponents();
        loadUsers();
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

        JLabel titleLabel = new JLabel("Manage System Users");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);

        JLabel subTitleLabel = new JLabel("View, toggle status, and delete user accounts.");
        subTitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subTitleLabel.setForeground(new Color(255, 255, 255, 200));

        textPanel.add(titleLabel);
        textPanel.add(Box.createVerticalStrut(5));
        textPanel.add(subTitleLabel);

        headerPanel.add(textPanel, BorderLayout.WEST);

        StyledButton refreshBtn = StyledButton.secondary("🔄 Refresh");
        refreshBtn.setPreferredSize(new Dimension(120, 36));
        refreshBtn.addActionListener(e -> loadUsers());

        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setOpaque(false);
        rightPanel.add(refreshBtn);
        headerPanel.add(rightPanel, BorderLayout.EAST);

        // Table
        String[] columns = {"ID", "Username", "Full Name", "Email", "Phone", "Role", "Status", "Joined"};
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
        
        table.getColumnModel().getColumn(0).setMaxWidth(50); // ID

        // Status renderer
        table.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                label.setHorizontalAlignment(SwingConstants.CENTER);
                String status = value.toString();
                if (status.equals("Active")) {
                    label.setForeground(ThemeColors.SUCCESS);
                } else {
                    label.setForeground(ThemeColors.DANGER);
                }
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

        StyledButton toggleBtn = new StyledButton("Toggle Active/Inactive");
        toggleBtn.setPreferredSize(new Dimension(180, 38));
        toggleBtn.addActionListener(e -> toggleUserStatus());
        
        StyledButton deleteBtn = StyledButton.danger("Delete User");
        deleteBtn.setPreferredSize(new Dimension(140, 38));
        deleteBtn.addActionListener(e -> deleteUser());

        actionsPanel.add(toggleBtn);
        actionsPanel.add(deleteBtn);

        add(headerPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(actionsPanel, BorderLayout.SOUTH);
    }

    private void loadUsers() {
        tableModel.setRowCount(0);
        try {
            List<User> users = userDAO.getAllUsers();
            for (User u : users) {
                tableModel.addRow(new Object[]{
                    u.getId(),
                    u.getUsername(),
                    u.getFullName(),
                    u.getEmail(),
                    u.getPhone(),
                    u.getRole(),
                    u.isActive() ? "Active" : "Inactive",
                    u.getCreatedAt() != null ? u.getCreatedAt().substring(0, 10) : ""
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading users: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void toggleUserStatus() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a user.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int userId = (int) tableModel.getValueAt(row, 0);
        String role = (String) tableModel.getValueAt(row, 5);
        String currentStatus = (String) tableModel.getValueAt(row, 6);
        
        if (role.equals("ADMIN")) {
            JOptionPane.showMessageDialog(this, "Cannot change admin status.", "Restricted", JOptionPane.WARNING_MESSAGE);
            return;
        }

        boolean newActive = !currentStatus.equals("Active");
        if (userDAO.updateUserStatus(userId, newActive)) {
            loadUsers();
            JOptionPane.showMessageDialog(this, "User status updated.", "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void deleteUser() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a user.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int userId = (int) tableModel.getValueAt(row, 0);
        String username = (String) tableModel.getValueAt(row, 1);
        String role = (String) tableModel.getValueAt(row, 5);
        
        if (role.equals("ADMIN")) {
            JOptionPane.showMessageDialog(this, "Cannot delete an admin.", "Restricted", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, "Delete user '" + username + "'? This will cascade to their lands.", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (userDAO.deleteUser(userId)) {
                loadUsers();
                JOptionPane.showMessageDialog(this, "User deleted.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Could not delete user.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
