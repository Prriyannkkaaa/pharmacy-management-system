package ui;
import java.util.Vector;
import config.DB;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class BillingFrame extends JFrame {
    private JTextField tfBatchCode, tfMedicineName, tfPrice, tfUnits;
    private JButton btnSearch, btnAddToCart, btnGoToCart;
    private float opacity = 0f;

    public BillingFrame() {
        setTitle("Pharmacy Billing");
        setSize(900, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        setLayout(new BorderLayout(10, 10));

        //  Background Gradient
        JPanel background = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(
                        0, 0, new Color(230, 240, 255),
                        getWidth(), getHeight(), new Color(190, 210, 255)
                );
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        setContentPane(background);


        JLabel title = new JLabel("Pharmacy Billing", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 28));
        title.setForeground(new Color(45, 75, 210));
        title.setBorder(new EmptyBorder(25, 0, 15, 0));
        background.add(title, BorderLayout.NORTH);

        // 🧾 Form Container
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        formPanel.setBorder(new CompoundBorder(
                new LineBorder(new Color(190, 210, 250), 1, true),
                new EmptyBorder(30, 60, 30, 60)
        ));
        background.add(formPanel, BorderLayout.CENTER);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 10, 15, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 🔍 Row 1: Search Button (Left) + Batch Code Field (Right)
        btnSearch = createBlueButton("Search");
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        formPanel.add(btnSearch, gbc);

        tfBatchCode = new JTextField();
        tfBatchCode.setPreferredSize(new Dimension(350, 38));
        tfBatchCode.setFont(new Font("SansSerif", Font.PLAIN, 15));
        tfBatchCode.setBorder(new CompoundBorder(
                new LineBorder(new Color(180, 200, 240), 1, true),
                new EmptyBorder(5, 10, 5, 10)
        ));
        tfBatchCode.setToolTipText("Enter Batch Code");
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        formPanel.add(tfBatchCode, gbc);
        gbc.gridwidth = 1;

        // 🧾 Row 2: Medicine Name
        JLabel lblName = new JLabel("Medicine Name:");
        lblName.setFont(new Font("SansSerif", Font.PLAIN, 16));
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(lblName, gbc);

        tfMedicineName = new JTextField();
        tfMedicineName.setEditable(false);
        stylizeField(tfMedicineName);
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        formPanel.add(tfMedicineName, gbc);
        gbc.gridwidth = 1;

        // 💸 Row 3: Price per Unit
        JLabel lblPrice = new JLabel("Price per Unit (₹):");
        lblPrice.setFont(new Font("SansSerif", Font.PLAIN, 16));
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(lblPrice, gbc);

        tfPrice = new JTextField();
        tfPrice.setEditable(false);
        stylizeField(tfPrice);
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        formPanel.add(tfPrice, gbc);
        gbc.gridwidth = 1;

        // 📦 Row 4: Units to Buy
        JLabel lblUnits = new JLabel("Units to Buy:");
        lblUnits.setFont(new Font("SansSerif", Font.PLAIN, 16));
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(lblUnits, gbc);

        tfUnits = new JTextField();
        stylizeField(tfUnits);
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        formPanel.add(tfUnits, gbc);
        gbc.gridwidth = 1;

        // 🛒 Row 5: Add to Cart + Go to Cart buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 10));
        buttonPanel.setOpaque(false);

        btnAddToCart = createBlueButton("+ Add to Cart");
        btnGoToCart = createBlueButton("→ Go to Cart");

        buttonPanel.add(btnAddToCart);
        buttonPanel.add(btnGoToCart);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 3;
        formPanel.add(buttonPanel, gbc);

        // 🔙 Back button
        JButton btnBack = new JButton("← Back");
        btnBack.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnBack.setForeground(new Color(45, 85, 225));
        btnBack.setFocusPainted(false);
        btnBack.setContentAreaFilled(false);
        btnBack.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnBack.addActionListener(e -> {
            fadeOut();
            new WelcomeDashboardFrame().setVisible(true);
            dispose();
        });

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(new EmptyBorder(10, 15, 10, 15));
        bottomPanel.add(btnBack, BorderLayout.WEST);
        background.add(bottomPanel, BorderLayout.SOUTH);

        // 🎯 Actions
        btnSearch.addActionListener(e -> fetchMedicine());
        btnAddToCart.addActionListener(e -> addToCart());
        btnGoToCart.addActionListener(e -> goToCart());

        // ✨ Animation
        addWindowListener(new WindowAdapter() {
            @Override public void windowOpened(WindowEvent e) { fadeIn(); }
        });
    }

    // 🧩 UI Helpers
    private void stylizeField(JTextField field) {
        field.setPreferredSize(new Dimension(350, 38));
        field.setFont(new Font("SansSerif", Font.PLAIN, 15));
        field.setBorder(new CompoundBorder(
                new LineBorder(new Color(180, 200, 240), 1, true),
                new EmptyBorder(5, 10, 5, 10)
        ));
    }

    private JButton createBlueButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(new Color(70, 100, 240));
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("SansSerif", Font.BOLD, 15));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(10, 20, 10, 20));
        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { btn.setBackground(new Color(50, 80, 220)); }
            @Override public void mouseExited(MouseEvent e) { btn.setBackground(new Color(70, 100, 240)); }
        });
        return btn;
    }

    // 🧠 Functional Methods
    private void fetchMedicine() {
        String batchCode = tfBatchCode.getText().trim();
        if (batchCode.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a Batch Code!");
            return;
        }

        try (Connection con = DB.getConnection()) {
            String sql = "SELECT item_name, unit_price FROM pharmacy_items WHERE batch_code = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, batchCode);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                tfMedicineName.setText(rs.getString("item_name"));
                tfPrice.setText(String.valueOf(rs.getDouble("unit_price")));
            } else {
                JOptionPane.showMessageDialog(this, "❌ No medicine found for this Batch Code.");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Database Error: " + e.getMessage());
        }
    }

    private void addToCart() {
        try {
            Vector<Object> row = new Vector<>();
            row.add(tfBatchCode.getText());
            row.add(tfMedicineName.getText());
            row.add(tfPrice.getText());
            row.add(tfUnits.getText());
            double total = Double.parseDouble(tfPrice.getText()) * Integer.parseInt(tfUnits.getText());
            row.add(String.format("%.2f", total));

            CartFrame.cartData.add(row);
            JOptionPane.showMessageDialog(this, "✅ Added to cart!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "❌ Error adding to cart: " + e.getMessage());
        }
    }

    private void goToCart() {
        fadeOut();
        new CartFrame().setVisible(true);
        dispose();
    }


    // ✨ Fade animation
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
        SwingUtilities.invokeLater(() -> new BillingFrame().setVisible(true));
    }
}
