package ui;

import config.DB;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class AddStockFrame extends JFrame {
    private JTextField tfName, tfBrand, tfBatch, tfQty, tfPrice, tfExpiry;
    private float opacity = 0f;

    public AddStockFrame() {
        setTitle("Add New Medicine Stock");
        setSize(1000, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        setLayout(new BorderLayout());

        // Background Gradient
        JPanel background = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(
                        0, 0, new Color(220, 235, 255),
                        getWidth(), getHeight(), new Color(185, 205, 255)
                );
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        background.setLayout(new GridBagLayout());
        setContentPane(background);

        //  White Card
        JPanel card = new JPanel(new GridBagLayout()) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
                g2.dispose();
            }
        };
        card.setBackground(Color.WHITE);
        card.setPreferredSize(new Dimension(750, 520)); // 🔹 Bigger white card
        card.setBorder(new CompoundBorder(
                new LineBorder(new Color(170, 190, 240), 2, true),
                new EmptyBorder(40, 50, 40, 50)
        ));
        card.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 12, 12, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        JLabel title = new JLabel("Add New Medicine Stock", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 28));
        title.setForeground(new Color(45, 85, 225));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        card.add(title, gbc);

        //  Input Fields (larger now)
        tfName = createField();
        tfBrand = createField();
        tfBatch = createField();
        tfQty = createField();
        tfPrice = createField();
        tfExpiry = createField();

        gbc.gridwidth = 1;
        addFormRow(card, gbc, 1, "Medicine Name:", tfName);
        addFormRow(card, gbc, 2, "Brand Name:", tfBrand);
        addFormRow(card, gbc, 3, "Batch Code:", tfBatch);
        addFormRow(card, gbc, 4, "Stock Quantity:", tfQty);
        addFormRow(card, gbc, 5, "Unit Price:", tfPrice);
        addFormRow(card, gbc, 6, "Expiry (YYYY-MM-DD):", tfExpiry);

        // 💙 Add Button
        JButton btnAdd = new JButton("➕ Add Medicine");
        btnAdd.setFont(new Font("SansSerif", Font.BOLD, 18));
        btnAdd.setForeground(Color.WHITE);
        btnAdd.setBackground(new Color(65, 105, 225));
        btnAdd.setFocusPainted(false);
        btnAdd.setBorder(new EmptyBorder(14, 40, 14, 40));
        btnAdd.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAdd.setBorderPainted(false);
        btnAdd.setOpaque(true);

        // Hover effect
        btnAdd.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { btnAdd.setBackground(new Color(85, 125, 255)); }
            @Override public void mouseExited(MouseEvent e) { btnAdd.setBackground(new Color(65, 105, 225)); }
        });

        gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 2;
        card.add(btnAdd, gbc);

        // 🔙 Back Button
        JButton btnBack = new JButton("← Back");
        btnBack.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnBack.setForeground(new Color(65, 105, 225));
        btnBack.setFocusPainted(false);
        btnBack.setContentAreaFilled(false);
        btnBack.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnBack.addActionListener(e -> {
            fadeOut();
            new WelcomeDashboardFrame().setVisible(true);
            dispose();
        });
        gbc.gridy = 8;
        card.add(btnBack, gbc);

        background.add(card, new GridBagConstraints());

        // 🪄 Add Button Action
        btnAdd.addActionListener(e -> handleAdd());

        // ✨ Animation
        addWindowListener(new WindowAdapter() {
            @Override public void windowOpened(WindowEvent e) { fadeIn(); }
        });
    }

    /** Helper — Add Label + Field */
    private void addFormRow(JPanel panel, GridBagConstraints gbc, int y, String label, JTextField field) {
        gbc.gridx = 0; gbc.gridy = y;
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 16));
        panel.add(lbl, gbc);

        gbc.gridx = 1;
        panel.add(field, gbc);
    }

    /** Helper — Create longer text fields */
    private JTextField createField() {
        JTextField field = new JTextField();
        field.setPreferredSize(new Dimension(450, 45)); // 🔹 Bigger, comfortable input field
        field.setFont(new Font("SansSerif", Font.PLAIN, 15));
        field.setBorder(new CompoundBorder(
                new LineBorder(new Color(190, 200, 240), 1, true),
                new EmptyBorder(8, 12, 8, 12)
        ));
        return field;
    }

    /** Adding new medicine */
    private void handleAdd() {
        String name = tfName.getText().trim();
        String brand = tfBrand.getText().trim();
        String batch = tfBatch.getText().trim();
        String qty = tfQty.getText().trim();
        String price = tfPrice.getText().trim();
        String expiry = tfExpiry.getText().trim();

        if (name.isEmpty() || brand.isEmpty() || batch.isEmpty() ||
                qty.isEmpty() || price.isEmpty() || expiry.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try (Connection con = DB.getConnection()) {
            // Prevent duplicates
            String checkSql = "SELECT COUNT(*) FROM pharmacy_items WHERE item_name=?";
            PreparedStatement checkPs = con.prepareStatement(checkSql);
            checkPs.setString(1, name);
            ResultSet rs = checkPs.executeQuery();
            rs.next();
            if (rs.getInt(1) > 0) {
                JOptionPane.showMessageDialog(this,
                        "This medicine already exists. Please use Edit Stock instead.",
                        "Duplicate Entry", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String sql = "INSERT INTO pharmacy_items (item_name, brand_name, batch_code, stock_qty, unit_price, expiry) VALUES (?,?,?,?,?,?)";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, name);
            ps.setString(2, brand);
            ps.setString(3, batch);
            ps.setInt(4, Integer.parseInt(qty));
            ps.setDouble(5, Double.parseDouble(price));
            ps.setDate(6, Date.valueOf(expiry));
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "✅ Medicine added successfully!");
            clearFields();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "❌ Error: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Clear Fields */
    private void clearFields() {
        tfName.setText("");
        tfBrand.setText("");
        tfBatch.setText("");
        tfQty.setText("");
        tfPrice.setText("");
        tfExpiry.setText("2026-12-31");
    }

    /** Fade animations */
    private void fadeIn() {
        Timer t = new Timer(25, e -> {
            opacity += 0.05f;
            if (opacity >= 1f) { opacity = 1f; ((Timer)e.getSource()).stop(); }
            repaint();
        });
        t.start();
    }

    private void fadeOut() {
        Timer t = new Timer(25, e -> {
            opacity -= 0.05f;
            if (opacity <= 0f) { opacity = 0f; ((Timer)e.getSource()).stop(); }
            repaint();
        });
        t.start();
        try { Thread.sleep(300); } catch (InterruptedException ignored) {}
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AddStockFrame().setVisible(true));
    }
}
