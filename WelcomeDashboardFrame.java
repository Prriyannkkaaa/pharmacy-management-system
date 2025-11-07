package ui;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

public class WelcomeDashboardFrame extends JFrame {
    private float opacity = 0f;
    private JPanel updateSubMenu;

    public WelcomeDashboardFrame() {
        setTitle("Pharmacy Dashboard - Welcome");
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        setLayout(new BorderLayout());

        // 🩵 Sidebar
        JPanel sidebar = new JPanel();
        sidebar.setBackground(new Color(65, 105, 225));
        sidebar.setPreferredSize(new Dimension(220, getHeight()));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));

        // 🖼️ Logo
        JLabel logo = new JLabel();
        ImageIcon icon = new ImageIcon("src/ui/assets/icon.png");
        Image scaled = icon.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);
        logo.setIcon(new ImageIcon(scaled));
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);
        logo.setBorder(BorderFactory.createEmptyBorder(30, 0, 30, 0));
        sidebar.add(logo);

        // ⚙️ Update Stock Section
        JButton btnUpdate = createSidebarButton("Update Stock ▼");

        updateSubMenu = new JPanel();
        updateSubMenu.setLayout(new BoxLayout(updateSubMenu, BoxLayout.Y_AXIS));
        updateSubMenu.setBackground(new Color(75, 120, 240));
        updateSubMenu.setBorder(new EmptyBorder(0, 25, 5, 0));

        JButton btnAdd = createSubMenuButton(" Add Stock");
        JButton btnEdit = createSubMenuButton("️ Edit Stock");
        JButton btnDelete = createSubMenuButton(" Delete Stock");

        // 🪄 Navigation Actions
        btnAdd.addActionListener(e -> {
            fadeOut();
            new AddStockFrame().setVisible(true);
            dispose();
        });
        btnEdit.addActionListener(e -> {
            fadeOut();
            new EditStockFrame().setVisible(true);
            dispose();
        });
        btnDelete.addActionListener(e -> {
            fadeOut();
            new DeleteStockFrame().setVisible(true);
            dispose();
        });

        updateSubMenu.add(btnAdd);
        updateSubMenu.add(btnEdit);
        updateSubMenu.add(btnDelete);

        sidebar.add(btnUpdate);
        sidebar.add(updateSubMenu);

        // 📦 Check Stock
        JButton btnCheck = createSidebarButton("Check Stock");
        btnCheck.addActionListener(e -> {
            fadeOut();
            new CheckStockFrame().setVisible(true);
            dispose();
        });
        sidebar.add(btnCheck);

        // 💵 Billing
        JButton btnBilling = createSidebarButton("Billing");
        btnBilling.addActionListener(e -> {
            fadeOut();
            new BillingFrame().setVisible(true);
            dispose();
        });
        sidebar.add(btnBilling);

        // 🔴 Logout Button
        sidebar.add(Box.createVerticalGlue());
        JButton btnLogout = new JButton("Logout");
        btnLogout.setMaximumSize(new Dimension(180, 40));
        btnLogout.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnLogout.setBackground(new Color(255, 80, 80));
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnLogout.setFocusPainted(false);
        btnLogout.setBorderPainted(false);
        btnLogout.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogout.setBorder(new EmptyBorder(10, 10, 10, 10));
        btnLogout.addActionListener(e -> {
            fadeOut();
            new LoginFrame().setVisible(true);
            dispose();
        });
        sidebar.add(btnLogout);
        sidebar.add(Box.createRigidArea(new Dimension(0, 20)));

        add(sidebar, BorderLayout.WEST);

        // 🌆 Main Panel with Background
        JPanel mainPanel = new JPanel(new BorderLayout()) {
            private final Image bgImage = new ImageIcon("src/ui/assets/bg1.jpeg").getImage();

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
                g2d.setColor(new Color(0, 0, 50, 150));
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
            }
        };
        mainPanel.setLayout(new GridBagLayout());
        add(mainPanel, BorderLayout.CENTER);

        // 🧾 Dashboard Content
        JPanel contentBox = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(new Color(255, 255, 255, 40));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);
            }
        };
        contentBox.setPreferredSize(new Dimension(720, 420));
        contentBox.setOpaque(false);
        contentBox.setLayout(new BoxLayout(contentBox, BoxLayout.Y_AXIS));
        contentBox.setBorder(new EmptyBorder(20, 30, 20, 30));

        JLabel title = new JLabel("Hello, Pharmacist ", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 28));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentBox.add(title);
        contentBox.add(Box.createRigidArea(new Dimension(0, 20)));

        JTextArea info = new JTextArea(
                "Pharmacy management systems are essential for smooth, accurate, and efficient medicine operations.\n\n"
                        + "They simplify day-to-day pharmacy tasks such as:\n"
                        + "• Adding and updating stock\n"
                        + "• Monitoring expiry dates\n"
                        + "• Managing bills and reports\n\n"
                        + "Use the sidebar to navigate through various management options and improve your workflow!"
        );
        info.setFont(new Font("SansSerif", Font.PLAIN, 16));
        info.setForeground(Color.WHITE);
        info.setOpaque(false);
        info.setEditable(false);
        info.setLineWrap(true);
        info.setWrapStyleWord(true);
        contentBox.add(info);

        mainPanel.add(contentBox, new GridBagConstraints());

        // ✨ Fade animation
        addWindowListener(new WindowAdapter() {
            @Override public void windowOpened(WindowEvent e) { fadeIn(); }
        });
    }

    // 🔹 Sidebar Button
    private JButton createSidebarButton(String text) {
        JButton btn = new JButton(text);
        btn.setMaximumSize(new Dimension(180, 40));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setBackground(new Color(90, 130, 245));
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("SansSerif", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(10, 10, 10, 10));
        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { btn.setBackground(new Color(110, 150, 255)); }
            @Override public void mouseExited(MouseEvent e) { btn.setBackground(new Color(90, 130, 245)); }
        });
        return btn;
    }

    // 🔹 Submenu Button
    private JButton createSubMenuButton(String text) {
        JButton btn = new JButton(text);
        btn.setMaximumSize(new Dimension(160, 35));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setBackground(new Color(95, 140, 250));
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("SansSerif", Font.PLAIN, 13));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(5, 15, 5, 10));
        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { btn.setBackground(new Color(115, 160, 255)); }
            @Override public void mouseExited(MouseEvent e) { btn.setBackground(new Color(95, 140, 250)); }
        });
        return btn;
    }

    // ✨ Fade Animations
    private void fadeIn() {
        Timer timer = new Timer(25, e -> {
            opacity += 0.05f;
            if (opacity >= 1f) {
                opacity = 1f;
                ((Timer)e.getSource()).stop();
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
                ((Timer)e.getSource()).stop();
            }
            repaint();
        });
        timer.start();
        try { Thread.sleep(300); } catch (InterruptedException ignored) {}
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new WelcomeDashboardFrame().setVisible(true));
    }
}
