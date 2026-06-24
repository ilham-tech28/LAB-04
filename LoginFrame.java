package com.landlink.ui;

import com.landlink.model.User;
import com.landlink.service.AuthService;
import com.landlink.ui.admin.AdminDashboard;
import com.landlink.ui.theme.*;
import com.landlink.ui.user.UserDashboard;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.geom.RoundRectangle2D;

/**
 * LoginFrame - Redesigned to look like a modern Web Interface (Split Screen).
 */
public class LoginFrame extends JFrame {

    private StyledTextField usernameField;
    private JPasswordField passwordField;
    private JLabel statusLabel;
    private AuthService authService;

    public LoginFrame() {
        authService = new AuthService();
        initComponents();
        AnimationUtils.animateFrameOpen(this);
    }

    private void initComponents() {
        setTitle("Land_Link — Sign In");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 650);
        setLocationRelativeTo(null);
        setResizable(false);

        // Main container (Split Layout)
        JPanel mainPanel = new JPanel(new GridLayout(1, 2));
        mainPanel.setBackground(ThemeColors.BG_MAIN);

        // === LEFT SIDE (Hero Image / Branding) ===
        GradientPanel leftPanel = new GradientPanel(ThemeColors.PRIMARY_DARK, ThemeColors.PRIMARY, ThemeColors.ACCENT);
        leftPanel.startAnimation();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBorder(new EmptyBorder(80, 50, 50, 50));

        JLabel heroTitle = new JLabel("Welcome to");
        heroTitle.setFont(new Font("Segoe UI", Font.BOLD, 36));
        heroTitle.setForeground(Color.WHITE);
        heroTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel logoLabel = new JLabel("Land_Link");
        logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 48));
        logoLabel.setForeground(Color.WHITE);
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        try {
            ImageIcon icon = new ImageIcon("logo.png");
            if (icon.getIconWidth() > 0) {
                Image img = icon.getImage().getScaledInstance(180, 180, Image.SCALE_SMOOTH);
                logoLabel.setIcon(new ImageIcon(img));
                logoLabel.setText(""); 
            }
        } catch (Exception ex) { }

        JLabel heroSub = new JLabel("<html><center>The premier digital platform for buying,<br>selling, and managing land properties.</center></html>");
        heroSub.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        heroSub.setForeground(new Color(255, 255, 255, 220));
        heroSub.setAlignmentX(Component.CENTER_ALIGNMENT);
        heroSub.setHorizontalAlignment(SwingConstants.CENTER);

        // Graphic placeholder
        JLabel graphicLabel = new JLabel("🌐", SwingConstants.CENTER);
        graphicLabel.setFont(new Font("Segoe UI", Font.PLAIN, 120));
        graphicLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        AnimationUtils.createPulse(graphicLabel, Color.WHITE, 50); // Pulse effect

        leftPanel.add(Box.createVerticalStrut(30));
        leftPanel.add(heroTitle);
        leftPanel.add(logoLabel);
        leftPanel.add(Box.createVerticalStrut(40));
        leftPanel.add(graphicLabel);
        leftPanel.add(Box.createVerticalStrut(40));
        leftPanel.add(heroSub);

        // === RIGHT SIDE (Login Form) ===
        JPanel rightPanel = new JPanel();
        rightPanel.setBackground(ThemeColors.BG_MAIN);
        rightPanel.setLayout(new GridBagLayout()); // To center the form card

        JPanel formCard = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(ThemeColors.BG_CARD);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 20, 20));
                g2.setColor(ThemeColors.BORDER);
                g2.setStroke(new BasicStroke(1f));
                g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, getWidth() - 1, getHeight() - 1, 20, 20));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        formCard.setOpaque(false);
        formCard.setLayout(new BoxLayout(formCard, BoxLayout.Y_AXIS));
        formCard.setBorder(new EmptyBorder(40, 40, 40, 40));
        formCard.setPreferredSize(new Dimension(380, 480));

        JLabel formTitle = new JLabel("Sign In");
        formTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        formTitle.setForeground(ThemeColors.TEXT_PRIMARY);
        formTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel formSub = new JLabel("Enter your credentials to access your account");
        formSub.setFont(ThemeColors.FONT_SMALL);
        formSub.setForeground(ThemeColors.TEXT_SECONDARY);
        formSub.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Fields
        JLabel userLabel = new JLabel("Username");
        userLabel.setFont(ThemeColors.FONT_SMALL);
        userLabel.setForeground(ThemeColors.TEXT_SECONDARY);
        userLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        usernameField = new StyledTextField("e.g. admin or john_doe");
        usernameField.setMaximumSize(new Dimension(300, 44));
        usernameField.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel passLabel = new JLabel("Password");
        passLabel.setFont(ThemeColors.FONT_SMALL);
        passLabel.setForeground(ThemeColors.TEXT_SECONDARY);
        passLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        passwordField = StyledTextField.createPasswordField("Enter your password");
        passwordField.setMaximumSize(new Dimension(300, 44));
        passwordField.setAlignmentX(Component.LEFT_ALIGNMENT);

        statusLabel = new JLabel(" ");
        statusLabel.setFont(ThemeColors.FONT_SMALL);
        statusLabel.setForeground(ThemeColors.DANGER);
        statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel loginButtonsPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        loginButtonsPanel.setOpaque(false);
        loginButtonsPanel.setMaximumSize(new Dimension(300, 44));
        loginButtonsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        StyledButton userLoginBtn = new StyledButton("User Login");
        userLoginBtn.setGradient(true);
        userLoginBtn.addActionListener(e -> handleLogin(false));

        StyledButton adminLoginBtn = StyledButton.secondary("Admin Login");
        adminLoginBtn.addActionListener(e -> handleLogin(true));

        loginButtonsPanel.add(userLoginBtn);
        loginButtonsPanel.add(adminLoginBtn);

        // Guest Login
        StyledButton guestBtn = StyledButton.secondary("Browse & Buy as Guest");
        guestBtn.setMaximumSize(new Dimension(300, 44));
        guestBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        guestBtn.addActionListener(e -> openGuestDashboard());

        // Register Link
        JPanel registerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        registerPanel.setOpaque(false);
        registerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel regText = new JLabel("New to Land_Link? ");
        regText.setFont(ThemeColors.FONT_SMALL);
        regText.setForeground(ThemeColors.TEXT_MUTED);

        JButton regLink = new JButton("Create an account");
        regLink.setFont(new Font("Segoe UI", Font.BOLD, 12));
        regLink.setForeground(ThemeColors.PRIMARY);
        regLink.setBorderPainted(false);
        regLink.setContentAreaFilled(false);
        regLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        regLink.setMargin(new Insets(0, 0, 0, 0));
        regLink.addActionListener(e -> openRegister());

        registerPanel.add(regText);
        registerPanel.add(regLink);

        // Build Form Card
        formCard.add(formTitle);
        formCard.add(Box.createVerticalStrut(5));
        formCard.add(formSub);
        formCard.add(Box.createVerticalStrut(30));
        formCard.add(userLabel);
        formCard.add(Box.createVerticalStrut(5));
        formCard.add(usernameField);
        formCard.add(Box.createVerticalStrut(20));
        formCard.add(passLabel);
        formCard.add(Box.createVerticalStrut(5));
        formCard.add(passwordField);
        formCard.add(Box.createVerticalStrut(10));
        formCard.add(statusLabel);
        formCard.add(Box.createVerticalStrut(20));
        formCard.add(loginButtonsPanel);
        formCard.add(Box.createVerticalStrut(15));
        formCard.add(guestBtn);
        formCard.add(Box.createVerticalStrut(25));
        formCard.add(registerPanel);

        rightPanel.add(formCard);

        mainPanel.add(leftPanel);
        mainPanel.add(rightPanel);

        setContentPane(mainPanel);
    }

    private void openGuestDashboard() {
        dispose();
        new com.landlink.ui.user.GuestDashboard().setVisible(true);
    }

    private void handleLogin(boolean asAdmin) {
        try {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());

            User user = authService.login(username, password);

            if (user == null) {
                statusLabel.setText("❌ Invalid username or password!");
                statusLabel.setForeground(ThemeColors.DANGER);
                return;
            }

            if (asAdmin && !user.isAdmin()) {
                statusLabel.setText("❌ You do not have admin privileges!");
                statusLabel.setForeground(ThemeColors.DANGER);
                return;
            }

            if (!asAdmin && user.isAdmin()) {
                statusLabel.setText("⚠️ Please use the Admin Login button.");
                statusLabel.setForeground(ThemeColors.WARNING);
                return;
            }

            statusLabel.setText("✅ Login successful! Redirecting...");
            statusLabel.setForeground(ThemeColors.SUCCESS);

            // Open appropriate dashboard
            Timer delay = new Timer(600, event -> {
                dispose();
                if (user.isAdmin()) {
                    new AdminDashboard().setVisible(true);
                } else {
                    new UserDashboard().setVisible(true);
                }
            });
            delay.setRepeats(false);
            delay.start();

        } catch (IllegalArgumentException ex) {
            statusLabel.setText("⚠️ " + ex.getMessage());
            statusLabel.setForeground(ThemeColors.WARNING);
        } catch (SecurityException ex) {
            statusLabel.setText("🔒 " + ex.getMessage());
            statusLabel.setForeground(ThemeColors.DANGER);
        } catch (Exception ex) {
            statusLabel.setText("❌ Error: " + ex.getMessage());
            statusLabel.setForeground(ThemeColors.DANGER);
        }
    }

    private void openRegister() {
        dispose();
        new RegisterFrame().setVisible(true);
    }
}
