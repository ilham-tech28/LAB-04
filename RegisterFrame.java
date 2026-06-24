package com.landlink.ui;

import com.landlink.model.User;
import com.landlink.service.AuthService;
import com.landlink.ui.theme.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.geom.RoundRectangle2D;

/**
 * RegisterFrame - Redesigned to match the modern Web Interface split-screen layout.
 */
public class RegisterFrame extends JFrame {

    private StyledTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private StyledTextField fullNameField;
    private StyledTextField emailField;
    private StyledTextField phoneField;
    private JLabel statusLabel;
    private AuthService authService;

    public RegisterFrame() {
        authService = new AuthService();
        initComponents();
        AnimationUtils.animateFrameOpen(this);
    }

    private void initComponents() {
        setTitle("Land_Link — Create Account");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setResizable(false);

        // Main container (Split Layout)
        JPanel mainPanel = new JPanel(new GridLayout(1, 2));
        mainPanel.setBackground(ThemeColors.BG_MAIN);

        // === LEFT SIDE (Hero Image / Branding) ===
        GradientPanel leftPanel = new GradientPanel(new Color(0, 180, 150), ThemeColors.PRIMARY, new Color(135, 100, 250));
        leftPanel.startAnimation();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBorder(new EmptyBorder(100, 50, 50, 50));

        JLabel heroTitle = new JLabel("Join");
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

        JLabel heroSub = new JLabel("<html><center>Create an account to start listing<br>and purchasing prime real estate.</center></html>");
        heroSub.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        heroSub.setForeground(new Color(255, 255, 255, 220));
        heroSub.setAlignmentX(Component.CENTER_ALIGNMENT);
        heroSub.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel graphicLabel = new JLabel("🚀", SwingConstants.CENTER);
        graphicLabel.setFont(new Font("Segoe UI", Font.PLAIN, 100));
        graphicLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        AnimationUtils.createPulse(graphicLabel, Color.WHITE, 60);

        leftPanel.add(heroTitle);
        leftPanel.add(logoLabel);
        leftPanel.add(Box.createVerticalStrut(40));
        leftPanel.add(graphicLabel);
        leftPanel.add(Box.createVerticalStrut(40));
        leftPanel.add(heroSub);

        // === RIGHT SIDE (Register Form) ===
        JPanel rightPanel = new JPanel();
        rightPanel.setBackground(ThemeColors.BG_MAIN);
        rightPanel.setLayout(new GridBagLayout()); 

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
        formCard.setBorder(new EmptyBorder(30, 40, 30, 40));
        formCard.setPreferredSize(new Dimension(420, 620));

        JLabel formTitle = new JLabel("Create Account");
        formTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        formTitle.setForeground(ThemeColors.TEXT_PRIMARY);
        formTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Fields
        fullNameField = createField("Full Name");
        usernameField = createField("Username");
        emailField = createField("Email Address");
        phoneField = createField("Phone Number");
        
        passwordField = StyledTextField.createPasswordField("Password (min 4 characters)");
        passwordField.setMaximumSize(new Dimension(340, 44));
        passwordField.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        confirmPasswordField = StyledTextField.createPasswordField("Confirm Password");
        confirmPasswordField.setMaximumSize(new Dimension(340, 44));
        confirmPasswordField.setAlignmentX(Component.LEFT_ALIGNMENT);

        statusLabel = new JLabel(" ");
        statusLabel.setFont(ThemeColors.FONT_SMALL);
        statusLabel.setForeground(ThemeColors.DANGER);
        statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        StyledButton registerBtn = new StyledButton("Sign Up");
        registerBtn.setGradient(true);
        registerBtn.setMaximumSize(new Dimension(340, 44));
        registerBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        registerBtn.addActionListener(this::handleRegister);

        // Back to login
        JPanel loginPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        loginPanel.setOpaque(false);
        loginPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel logText = new JLabel("Already have an account? ");
        logText.setFont(ThemeColors.FONT_SMALL);
        logText.setForeground(ThemeColors.TEXT_MUTED);

        JButton logLink = new JButton("Sign In");
        logLink.setFont(new Font("Segoe UI", Font.BOLD, 12));
        logLink.setForeground(ThemeColors.PRIMARY);
        logLink.setBorderPainted(false);
        logLink.setContentAreaFilled(false);
        logLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logLink.setMargin(new Insets(0, 0, 0, 0));
        logLink.addActionListener(e -> {
            dispose();
            new LoginFrame().setVisible(true);
        });

        loginPanel.add(logText);
        loginPanel.add(logLink);

        // Build Form Card
        formCard.add(formTitle);
        formCard.add(Box.createVerticalStrut(20));
        formCard.add(createLabel("Full Name"));
        formCard.add(Box.createVerticalStrut(5));
        formCard.add(fullNameField);
        formCard.add(Box.createVerticalStrut(10));
        formCard.add(createLabel("Username"));
        formCard.add(Box.createVerticalStrut(5));
        formCard.add(usernameField);
        formCard.add(Box.createVerticalStrut(10));
        formCard.add(createLabel("Email"));
        formCard.add(Box.createVerticalStrut(5));
        formCard.add(emailField);
        formCard.add(Box.createVerticalStrut(10));
        formCard.add(createLabel("Phone"));
        formCard.add(Box.createVerticalStrut(5));
        formCard.add(phoneField);
        formCard.add(Box.createVerticalStrut(10));
        formCard.add(createLabel("Password"));
        formCard.add(Box.createVerticalStrut(5));
        formCard.add(passwordField);
        formCard.add(Box.createVerticalStrut(10));
        formCard.add(createLabel("Confirm Password"));
        formCard.add(Box.createVerticalStrut(5));
        formCard.add(confirmPasswordField);
        formCard.add(Box.createVerticalStrut(5));
        formCard.add(statusLabel);
        formCard.add(Box.createVerticalStrut(10));
        formCard.add(registerBtn);
        formCard.add(Box.createVerticalStrut(15));
        formCard.add(loginPanel);

        rightPanel.add(formCard);

        mainPanel.add(leftPanel);
        mainPanel.add(rightPanel);

        setContentPane(mainPanel);
        getRootPane().setDefaultButton(registerBtn);
    }

    private StyledTextField createField(String placeholder) {
        StyledTextField field = new StyledTextField(placeholder);
        field.setMaximumSize(new Dimension(340, 44));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        return field;
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(ThemeColors.FONT_SMALL);
        label.setForeground(ThemeColors.TEXT_SECONDARY);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private void handleRegister(ActionEvent e) {
        try {
            String password = new String(passwordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());

            if (!password.equals(confirmPassword)) {
                throw new IllegalArgumentException("Passwords do not match!");
            }

            User user = new User(
                usernameField.getText().trim(),
                password,
                fullNameField.getText().trim(),
                emailField.getText().trim(),
                phoneField.getText().trim()
            );

            boolean success = authService.register(user);

            if (success) {
                statusLabel.setText("✅ Account created successfully!");
                statusLabel.setForeground(ThemeColors.SUCCESS);

                Timer delay = new Timer(1500, event -> {
                    dispose();
                    new LoginFrame().setVisible(true);
                });
                delay.setRepeats(false);
                delay.start();
            } else {
                statusLabel.setText("❌ Failed to create account. Try again.");
                statusLabel.setForeground(ThemeColors.DANGER);
            }

        } catch (IllegalArgumentException ex) {
            statusLabel.setText("⚠️ " + ex.getMessage());
            statusLabel.setForeground(ThemeColors.WARNING);
        } catch (Exception ex) {
            statusLabel.setText("❌ Error: " + ex.getMessage());
            statusLabel.setForeground(ThemeColors.DANGER);
        }
    }
}
