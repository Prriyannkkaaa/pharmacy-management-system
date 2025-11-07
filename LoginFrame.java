package ui;

import config.DB;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginFrame extends JFrame {
    private JTextField tfEmail;
    private JPasswordField pfPassword;
    private float opacity = 0f;

    public LoginFrame() {
        setTitle("💊 Pharmacy Portal - Login");
        setSize(900, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);

        // 🌈 Background gradient
        JPanel background = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(0, 0,
                        new Color(230, 240, 255),
                        getWidth(), getHeight(),
                        new Color(190, 215, 255));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        background.setLayout(new BorderLayout());
        setContentPane(background);

        // 🩵 Centered Login Card
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);
        background.add(centerPanel, BorderLayout.CENTER);

        JPanel card = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
                super.paintComponent(g2);
                g2.dispose();
            }
        };

        card.setPreferredSize(new Dimension(420, 460));
        card.setBackground(Color.WHITE);
        card.setBorder(new CompoundBorder(
                new LineBorder(new Color(190, 210, 250), 1, true),
                new EmptyBorder(30, 45, 30, 45)
        ));
        card.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 0, 10, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;

        // 🖼️ Logo
        JLabel logo = new JLabel();
        ImageIcon icon = new ImageIcon("src/ui/assets/icon.png");
        Image scaled = icon.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);
        logo.setIcon(new ImageIcon(scaled));
        logo.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        card.add(logo, gbc);

        // 💊 Title
        JLabel title = new JLabel("Welcome Back!", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        title.setForeground(new Color(65, 105, 225));
        gbc.gridy = 1;
        card.add(title, gbc);

        JLabel subtitle = new JLabel("Login to your pharmacy dashboard", SwingConstants.CENTER);
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 14));
        subtitle.setForeground(new Color(90, 90, 90));
        gbc.gridy = 2;
        card.add(subtitle, gbc);

        // 🧾 Fields
        tfEmail = new JTextField();
        pfPassword = new JPasswordField();
        stylizeField(tfEmail);
        stylizeField(pfPassword);

        gbc.gridy = 3;
        card.add(labeled("Email:", tfEmail), gbc);
        gbc.gridy = 4;
        card.add(labeled("Password:", pfPassword), gbc);

        // 🔘 Login button
        JButton btnLogin = createStyledButton("Login");
        gbc.gridy = 5;
        card.add(btnLogin, gbc);

        // 🧭 Sign up link
        JLabel signupLabel = new JLabel("<html><font color='#555555'>Not a user? </font><u><font color='#4169E1'>Sign up</font></u></html>");
        signupLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        signupLabel.setHorizontalAlignment(SwingConstants.CENTER);
        signupLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        signupLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                fadeOut();
                new SignupFrame().setVisible(true);
                dispose();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                signupLabel.setText("<html><font color='#555555'>Not a user? </font><u><font color='#2F4DE8'>Sign up</font></u></html>");
            }

            @Override
            public void mouseExited(MouseEvent e) {
                signupLabel.setText("<html><font color='#555555'>Not a user? </font><u><font color='#4169E1'>Sign up</font></u></html>");
            }
        });
        gbc.gridy = 6;
        card.add(signupLabel, gbc);

        centerPanel.add(card);

        // 🔙 Back button (bottom-left)
        JButton btnBack = new JButton("← Back");
        btnBack.setFont(new Font("SansSerif", Font.BOLD, 13));
        btnBack.setFocusPainted(false);
        btnBack.setBorderPainted(false);
        btnBack.setForeground(new Color(65, 105, 225));
        btnBack.setBackground(new Color(230, 235, 255));
        btnBack.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnBack.setOpaque(true);
        btnBack.setBorder(new EmptyBorder(8, 15, 8, 15));
        btnBack.addActionListener(e -> {
            fadeOut();
            new WelcomeFrame().setVisible(true);
            dispose();
        });

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);
        bottomPanel.add(btnBack, BorderLayout.WEST);
        bottomPanel.setBorder(new EmptyBorder(0, 15, 15, 0));
        background.add(bottomPanel, BorderLayout.SOUTH);

        //  Fade-in animation
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                fadeIn();
            }
        });

        // Action
        btnLogin.addActionListener(e -> handleLogin());
    }

    private void fadeIn() {
        Timer timer = new Timer(25, e -> {
            opacity += 0.05f;
            if (opacity >= 1f) {
                opacity = 1f;
                ((Timer) e.getSource()).stop();
            }
            repaint();
        });
        timer.start();
    }

    private void fadeOut() {
        Timer timer = new Timer(25, e -> {
            opacity -= 0.05f;
            if (opacity <= 0f) {
                opacity = 0f;
                ((Timer) e.getSource()).stop();
            }
            repaint();
        });
        timer.start();
        try {
            Thread.sleep(300);
        } catch (InterruptedException ignored) {}
    }

    private JPanel labeled(String text, JComponent field) {
        JPanel p = new JPanel(new BorderLayout(5, 5));
        p.setBackground(Color.WHITE);
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 14));
        lbl.setForeground(new Color(70, 70, 70));
        p.add(lbl, BorderLayout.NORTH);
        p.add(field, BorderLayout.CENTER);
        return p;
    }

    private void stylizeField(JTextField field) {
        field.setPreferredSize(new Dimension(300, 38));
        field.setFont(new Font("SansSerif", Font.PLAIN, 14));
        field.setBackground(Color.WHITE);
        field.setBorder(new CompoundBorder(
                new LineBorder(new Color(180, 200, 240), 1, true),
                new EmptyBorder(8, 10, 8, 10)
        ));
    }

    private JButton createStyledButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(new Color(65, 105, 225));
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("SansSerif", Font.BOLD, 15));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setBorder(new EmptyBorder(10, 10, 10, 10));
        return btn;
    }

    private void handleLogin() {
        String email = tfEmail.getText().trim();
        String password = new String(pfPassword.getPassword()).trim();

        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.");
            return;
        }

        try (Connection con = DB.getConnection()) {
            String sql = "SELECT * FROM users WHERE email=? AND password=?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, email);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                fadeOut();
                //  Fixed: no argument passed to constructor
                new WelcomeDashboardFrame().setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "❌ Invalid email or password.");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "❌ Error: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}
