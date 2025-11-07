package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class WelcomeFrame extends JFrame {
    private float titleOpacity = 0f;
    private float textOpacity = 0f;

    public WelcomeFrame() {
        setTitle(" Pharmacy Management System - Welcome");
        setSize(950, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);


        JPanel backgroundPanel = new JPanel() {
            private final Image bgImage = new ImageIcon("src/ui/assets/pharmacy.jpg").getImage();

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;

                // Draw background image (scaled)
                g2d.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);

                // Add transparent overlay (soft blue tint)
                // Adjust alpha value (0–255) → lower = more transparent
                g2d.setColor(new Color(0, 0, 40, 110));
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        backgroundPanel.setLayout(new GridBagLayout());
        setContentPane(backgroundPanel);

        //  Content container
        JPanel contentPanel = new JPanel();
        contentPanel.setOpaque(false);
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Logo
        JLabel logo = new JLabel();
        ImageIcon icon = new ImageIcon("src/ui/assets/icon.png");
        Image scaled = icon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
        logo.setIcon(new ImageIcon(scaled));
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);
        logo.setBorder(BorderFactory.createEmptyBorder(20, 0, 15, 0));
        contentPanel.add(logo);

        //  Title
        JLabel title = new JLabel("SMART PHARMACY MANAGEMENT SYSTEM");
        title.setFont(new Font("SansSerif", Font.BOLD, 26));
        title.setForeground(new Color(255, 255, 255, 0)); // Start transparent
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(title);

        // Subtitle / Description
        JTextArea message = new JTextArea(
                "An intelligent way to manage your pharmacy operations.\n"
                        + "Track medicine inventory, monitor expiries, handle suppliers, and automate billing — "
                        + "all from a single dashboard.\n\n"
                        + "Our goal: make every pharmacist's work easier, accurate, and error-free."
        );
        message.setFont(new Font("SansSerif", Font.PLAIN, 16));
        message.setForeground(new Color(255, 255, 255, 0)); // Start transparent
        message.setOpaque(false);
        message.setEditable(false);
        message.setFocusable(false);
        message.setLineWrap(true);
        message.setWrapStyleWord(true);
        message.setAlignmentX(Component.CENTER_ALIGNMENT);
        message.setMaximumSize(new Dimension(750, 200));
        message.setBorder(BorderFactory.createEmptyBorder(30, 0, 30, 0));
        contentPanel.add(message);

        // Button
        JButton btnStart = new JButton("Get Started →");
        btnStart.setFont(new Font("SansSerif", Font.BOLD, 16));
        btnStart.setForeground(Color.WHITE);
        btnStart.setBackground(new Color(65, 105, 225));
        btnStart.setFocusPainted(false);
        btnStart.setBorder(BorderFactory.createEmptyBorder(12, 25, 12, 25));
        btnStart.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnStart.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnStart.setBorderPainted(false);
        btnStart.setOpaque(true);

        // Button hover effect
        btnStart.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btnStart.setBackground(new Color(85, 125, 245));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btnStart.setBackground(new Color(65, 105, 225));
            }
        });

        // On click → open Login page
        btnStart.addActionListener(e -> {
            dispose();
            new LoginFrame().setVisible(true);
        });

        contentPanel.add(btnStart);
        backgroundPanel.add(contentPanel);

        // Fade-in animations
        Timer titleFade = new Timer(40, e -> {
            titleOpacity += 0.05f;
            if (titleOpacity > 1f) titleOpacity = 1f;
            title.setForeground(new Color(255, 255, 255, (int) (titleOpacity * 255)));
            if (titleOpacity >= 1f) ((Timer) e.getSource()).stop();
        });

        Timer textFade = new Timer(40, e -> {
            textOpacity += 0.04f;
            if (textOpacity > 1f) textOpacity = 1f;
            message.setForeground(new Color(255, 255, 255, (int) (textOpacity * 255)));
            if (textOpacity >= 1f) ((Timer) e.getSource()).stop();
        });

        // Start animations sequentially
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                titleFade.start();
                new Timer(1200, evt -> textFade.start()).start();
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new WelcomeFrame().setVisible(true));
    }
}
