package ui;

import config.DB;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class SignupFrame extends JFrame {
    private JTextField tfName, tfEmail;
    private JPasswordField pfPassword, pfConfirm;
    private float opacity = 0f;

    public SignupFrame() {
        setTitle("💊 Pharmacy Portal - Sign Up");
        setSize(1050, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);

        // 🌈 Background gradient
        JPanel background = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(0, 0, new Color(230,240,255),
                        getWidth(), getHeight(), new Color(190,215,255));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        background.setLayout(new BorderLayout());
        setContentPane(background);

        // Center container
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);
        background.add(centerPanel, BorderLayout.CENTER);

        // Card panel
        JPanel card = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        card.setPreferredSize(new Dimension(500, 600));
        card.setBackground(Color.WHITE);
        card.setBorder(new CompoundBorder(
                new LineBorder(new Color(190,210,250), 1, true),
                new EmptyBorder(40, 60, 40, 60)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 0, 12, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;

        // Logo
        JLabel logo = new JLabel(new ImageIcon(
                new ImageIcon("src/ui/assets/icon.png").getImage()
                        .getScaledInstance(90, 90, Image.SCALE_SMOOTH)));
        logo.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 0; card.add(logo, gbc);

        // Title
        JLabel title = new JLabel("Create Your Account", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 26));
        title.setForeground(new Color(65,105,225));
        gbc.gridy = 1; card.add(title, gbc);

        // Subtitle
        JLabel subtitle = new JLabel("Register to manage your pharmacy efficiently", SwingConstants.CENTER);
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 15));
        subtitle.setForeground(new Color(90, 90, 90));
        gbc.gridy = 2; card.add(subtitle, gbc);

        // Input fields
        tfName = new JTextField();
        tfEmail = new JTextField();
        pfPassword = new JPasswordField();
        pfConfirm = new JPasswordField();

        stylizeField(tfName);
        stylizeField(tfEmail);
        stylizeField(pfPassword);
        stylizeField(pfConfirm);

        gbc.gridy = 3; card.add(labeled("Full Name:", tfName), gbc);
        gbc.gridy = 4; card.add(labeled("Email:", tfEmail), gbc);
        gbc.gridy = 5; card.add(labeled("Password:", pfPassword), gbc);
        gbc.gridy = 6; card.add(labeled("Confirm Password:", pfConfirm), gbc);

        // ✅ Sign Up button
        JButton btnSignup = createStyledButton("Sign Up");
        gbc.gridy = 7;
        gbc.insets = new Insets(20, 0, 10, 0);
        gbc.anchor = GridBagConstraints.CENTER;
        card.add(btnSignup, gbc);

        // Login link
        JLabel loginLabel = new JLabel(
                "<html><font color='#555555'>Already a user? </font><u><font color='#4169E1'>Login here</font></u></html>",
                SwingConstants.CENTER);
        loginLabel.setFont(new Font("SansSerif", Font.PLAIN, 15));
        loginLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                fadeOut();
                new LoginFrame().setVisible(true);
                dispose();
            }
        });
        gbc.gridy = 8;
        gbc.insets = new Insets(10, 0, 0, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        card.add(loginLabel, gbc);

        centerPanel.add(card);

        // Back button
        JButton btnBack = new JButton("← Back");
        btnBack.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnBack.setFocusPainted(false);
        btnBack.setBorderPainted(false);
        btnBack.setForeground(new Color(65,105,225));
        btnBack.setBackground(new Color(230,235,255));
        btnBack.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnBack.setOpaque(true);
        btnBack.setBorder(new EmptyBorder(10,20,10,20));
        btnBack.addActionListener(e -> {
            fadeOut();
            new WelcomeFrame().setVisible(true);
            dispose();
        });

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);
        bottomPanel.add(btnBack, BorderLayout.WEST);
        bottomPanel.setBorder(new EmptyBorder(0, 20, 20, 0));
        background.add(bottomPanel, BorderLayout.SOUTH);

        // Animation
        addWindowListener(new WindowAdapter() {
            @Override public void windowOpened(WindowEvent e) { fadeIn(); }
        });

        // Button click → save data
        btnSignup.addActionListener(e -> handleSignup());
    }

    // Animations
    private void fadeIn() {
        Timer t = new Timer(25, e -> {
            opacity += 0.05f;
            if (opacity >= 1f) {
                opacity = 1f;
                ((Timer) e.getSource()).stop();
            }
            repaint();
        });
        t.start();
    }

    private void fadeOut() {
        Timer t = new Timer(25, e -> {
            opacity -= 0.05f;
            if (opacity <= 0f) {
                opacity = 0f;
                ((Timer) e.getSource()).stop();
            }
            repaint();
        });
        t.start();
        try { Thread.sleep(300); } catch (InterruptedException ignored) {}
    }

    private JPanel labeled(String text, JComponent field) {
        JPanel p = new JPanel(new BorderLayout(5,5));
        p.setBackground(Color.WHITE);
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 15));
        lbl.setForeground(new Color(70,70,70));
        p.add(lbl, BorderLayout.NORTH);
        p.add(field, BorderLayout.CENTER);
        return p;
    }

    private void stylizeField(JTextField field) {
        field.setPreferredSize(new Dimension(350, 40));
        field.setFont(new Font("SansSerif", Font.PLAIN, 15));
        field.setBackground(Color.WHITE);
        field.setBorder(new CompoundBorder(
                new LineBorder(new Color(180,200,240), 1, true),
                new EmptyBorder(8,10,8,10)));
    }

    private JButton createStyledButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(new Color(65,105,225));
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("SansSerif", Font.BOLD, 17));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setBorder(new EmptyBorder(12, 35, 12, 35));
        return btn;
    }

    //  Save signup details into database (with regex)
    private void handleSignup() {
        String name = tfName.getText().trim();
        String email = tfEmail.getText().trim();
        String pass = new String(pfPassword.getPassword()).trim();
        String confirm = new String(pfConfirm.getPassword()).trim();

        // Check for empty fields
        if (name.isEmpty() || email.isEmpty() || pass.isEmpty() || confirm.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.");
            return;
        }

        // Email validation using regex
        if (!email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
            JOptionPane.showMessageDialog(this,
                    "Invalid email address! Please include '@' and a valid domain (e.g., user@gmail.com).");
            return;
        }

        // Password match check
        if (!pass.equals(confirm)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match!");
            return;
        }

        // Insert data into database
        try (Connection con = DB.getConnection()) {
            String sql = "INSERT INTO users (name, email, password) VALUES (?, ?, ?)";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, pass);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, " Account created successfully!");
            fadeOut();
            new WelcomeDashboardFrame().setVisible(true);
            dispose();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, " Error: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SignupFrame().setVisible(true));
    }
}
