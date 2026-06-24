package com.landlink.ui.admin;

import com.landlink.model.PurchaseRequest;
import com.landlink.service.PurchaseRequestService;
import com.landlink.ui.theme.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.List;

/**
 * AdminBuyRequestsPanel - Admin view of all purchase requests with approve/reject actions.
 */
public class AdminBuyRequestsPanel extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;
    private PurchaseRequestService requestService;
    private List<PurchaseRequest> requests;
    private JLabel statusFilterLabel;
    private String currentFilter = "PENDING";

    public AdminBuyRequestsPanel() {
        this.requestService = new PurchaseRequestService();
        initComponents();
        loadRequests();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(ThemeColors.BG_MAIN);
        setBorder(new EmptyBorder(25, 25, 25, 25));

        // Header
        GradientPanel headerPanel = new GradientPanel(new Color(30, 30, 50), new Color(80, 40, 120), new Color(120, 60, 180));
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setBorder(new EmptyBorder(20, 25, 20, 25));
        headerPanel.setPreferredSize(new Dimension(0, 80));

        JLabel titleLabel = new JLabel("🛒 Purchase Requests");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(Color.WHITE);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightPanel.setOpaque(false);

        // Filter buttons
        StyledButton allBtn = StyledButton.secondary("All");
        allBtn.setPreferredSize(new Dimension(80, 34));
        allBtn.addActionListener(e -> { currentFilter = "ALL"; loadRequests(); });

        StyledButton pendingBtn = StyledButton.secondary("Pending");
        pendingBtn.setPreferredSize(new Dimension(90, 34));
        pendingBtn.addActionListener(e -> { currentFilter = "PENDING"; loadRequests(); });

        StyledButton refreshBtn = StyledButton.secondary("🔄 Refresh");
        refreshBtn.setPreferredSize(new Dimension(110, 34));
        refreshBtn.addActionListener(e -> loadRequests());

        rightPanel.add(allBtn);
        rightPanel.add(pendingBtn);
        rightPanel.add(refreshBtn);

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(rightPanel, BorderLayout.EAST);

        // Table
        String[] columns = {"#", "Land", "Location", "Buyer", "Seller", "Amount", "Status", "Requested On"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(tableModel);
        table.setFont(ThemeColors.FONT_BODY);
        table.setForeground(ThemeColors.TEXT_PRIMARY);
        table.setBackground(ThemeColors.BG_CARD);
        table.setRowHeight(40);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 1));
        table.setSelectionBackground(new Color(79, 172, 254, 40));
        table.setSelectionForeground(ThemeColors.TEXT_PRIMARY);

        // Column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(30);
        table.getColumnModel().getColumn(1).setPreferredWidth(160);
        table.getColumnModel().getColumn(2).setPreferredWidth(110);
        table.getColumnModel().getColumn(3).setPreferredWidth(110);
        table.getColumnModel().getColumn(4).setPreferredWidth(110);
        table.getColumnModel().getColumn(5).setPreferredWidth(100);
        table.getColumnModel().getColumn(6).setPreferredWidth(85);
        table.getColumnModel().getColumn(7).setPreferredWidth(120);

        // Header styling
        table.getTableHeader().setFont(ThemeColors.FONT_HEADING);
        table.getTableHeader().setForeground(ThemeColors.TEXT_SECONDARY);
        table.getTableHeader().setBackground(ThemeColors.BG_SIDEBAR);
        table.getTableHeader().setBorder(BorderFactory.createEmptyBorder());

        // Status column renderer
        table.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val, boolean sel, boolean foc, int r, int c) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(t, val, sel, foc, r, c);
                String status = val != null ? val.toString() : "";
                switch (status) {
                    case "PENDING":  lbl.setForeground(ThemeColors.WARNING); break;
                    case "APPROVED": lbl.setForeground(ThemeColors.SUCCESS); break;
                    case "REJECTED": lbl.setForeground(ThemeColors.DANGER);  break;
                    default:         lbl.setForeground(ThemeColors.TEXT_MUTED);
                }
                lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
                setHorizontalAlignment(CENTER);
                return lbl;
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(ThemeColors.BG_CARD);

        // Action buttons panel
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        actionPanel.setBackground(ThemeColors.BG_MAIN);

        StyledButton approveBtn = StyledButton.success("✅ Approve with Date");
        approveBtn.setPreferredSize(new Dimension(180, 40));
        approveBtn.addActionListener(e -> handleApprove());

        StyledButton rejectBtn = StyledButton.danger("❌ Reject");
        rejectBtn.setPreferredSize(new Dimension(120, 40));
        rejectBtn.addActionListener(e -> handleReject());

        StyledButton cancelBtn = StyledButton.danger("⚠️ Cancel Approval");
        cancelBtn.setPreferredSize(new Dimension(180, 40));
        cancelBtn.addActionListener(e -> handleCancelApproval());

        actionPanel.add(approveBtn);
        actionPanel.add(rejectBtn);
        actionPanel.add(cancelBtn);

        add(headerPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(actionPanel, BorderLayout.SOUTH);
    }

    private void loadRequests() {
        tableModel.setRowCount(0);
        if ("PENDING".equals(currentFilter)) {
            requests = requestService.getPendingRequests();
        } else {
            requests = requestService.getAllRequests();
        }
        int i = 1;
        for (PurchaseRequest r : requests) {
            String date = r.getCreatedAt() != null ? r.getCreatedAt().substring(0, Math.min(10, r.getCreatedAt().length())) : "N/A";
            tableModel.addRow(new Object[]{
                i++, r.getLandTitle(), r.getLandLocation(),
                r.getBuyerName(), r.getSellerName(),
                r.getFormattedAmount(), r.getStatus(), date
            });
        }
    }

    private void handleApprove() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a request first.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        PurchaseRequest req = requests.get(row);
        if (!PurchaseRequest.STATUS_PENDING.equals(req.getStatus())) {
            JOptionPane.showMessageDialog(this, "Only PENDING requests can be approved.", "Invalid Action", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Date picker dialog
        JPanel datePanel = new JPanel(new BorderLayout(10, 10));
        datePanel.setBackground(ThemeColors.BG_CARD);
        JLabel msg = new JLabel("<html>Set the <b>Agreement Date</b> for:<br><i>" + req.getLandTitle() + "</i><br>Buyer: " + req.getBuyerName() + "</html>");
        msg.setFont(ThemeColors.FONT_BODY);
        msg.setForeground(ThemeColors.TEXT_PRIMARY);
        JTextField dateField = new JTextField(java.time.LocalDate.now().plusDays(7).toString());
        dateField.setFont(ThemeColors.FONT_BODY);
        dateField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ThemeColors.BORDER),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)));
        JLabel hint = new JLabel("Format: YYYY-MM-DD");
        hint.setFont(ThemeColors.FONT_SMALL);
        hint.setForeground(ThemeColors.TEXT_MUTED);
        datePanel.add(msg, BorderLayout.NORTH);
        datePanel.add(dateField, BorderLayout.CENTER);
        datePanel.add(hint, BorderLayout.SOUTH);
        datePanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        int result = JOptionPane.showConfirmDialog(this, datePanel, "Approve & Set Agreement Date",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) return;

        String agreementDate = dateField.getText().trim();
        if (agreementDate.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter an agreement date.", "Missing Date", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            java.time.LocalDate.parse(agreementDate);
        } catch (java.time.format.DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Invalid date format or value. Please use a valid YYYY-MM-DD date.", "Invalid Date", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            requestService.approveRequest(req.getId(), agreementDate);
            JOptionPane.showMessageDialog(this,
                "✅ Request APPROVED!\n\nMessages have been sent to the buyer and seller.\nAgreement date: " + agreementDate,
                "Approved", JOptionPane.INFORMATION_MESSAGE);
            loadRequests();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "❌ Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleReject() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a request first.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        PurchaseRequest req = requests.get(row);
        if (!PurchaseRequest.STATUS_PENDING.equals(req.getStatus())) {
            JOptionPane.showMessageDialog(this, "Only PENDING requests can be rejected.", "Invalid Action", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Reason dialog
        JPanel reasonPanel = new JPanel(new BorderLayout(10, 10));
        JLabel msg = new JLabel("<html>Rejection reason for:<br><i>" + req.getLandTitle() + "</i><br>Buyer: " + req.getBuyerName() + "</html>");
        msg.setFont(ThemeColors.FONT_BODY);
        msg.setForeground(ThemeColors.TEXT_PRIMARY);
        JTextArea reasonArea = new JTextArea(4, 30);
        reasonArea.setFont(ThemeColors.FONT_BODY);
        reasonArea.setLineWrap(true);
        reasonArea.setWrapStyleWord(true);
        reasonArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ThemeColors.BORDER),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)));
        reasonPanel.add(msg, BorderLayout.NORTH);
        reasonPanel.add(new JScrollPane(reasonArea), BorderLayout.CENTER);
        reasonPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        int result = JOptionPane.showConfirmDialog(this, reasonPanel, "Reject Request — Enter Reason",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) return;

        String reason = reasonArea.getText().trim();
        if (reason.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please provide a rejection reason.", "Missing Reason", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            requestService.rejectRequest(req.getId(), reason);
            JOptionPane.showMessageDialog(this, "Request REJECTED.\nMessage sent to buyer.", "Rejected", JOptionPane.INFORMATION_MESSAGE);
            loadRequests();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "❌ Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleCancelApproval() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a request first.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        PurchaseRequest req = requests.get(row);
        if (!PurchaseRequest.STATUS_APPROVED.equals(req.getStatus())) {
            JOptionPane.showMessageDialog(this, "Only APPROVED requests can be cancelled.", "Invalid Action", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int result = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to cancel the approval for this purchase?\n" +
            "The land will become available again and the buyer will be notified.",
            "Confirm Cancellation",
            JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            
        if (result == JOptionPane.YES_OPTION) {
            try {
                requestService.cancelApprovedRequest(req.getId());
                JOptionPane.showMessageDialog(this, "Approval cancelled successfully.\nThe land is now available again.", "Cancelled", JOptionPane.INFORMATION_MESSAGE);
                loadRequests();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "❌ Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
