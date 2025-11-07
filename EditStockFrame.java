package ui;

import config.DB;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class EditStockFrame extends JFrame {
    private JTable table;
    private DefaultTableModel model;
    private float opacity = 0f;

    public EditStockFrame() {
        setTitle("Edit Medicine Stock");
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        setLayout(new BorderLayout(10, 10));

        // 🌈 Background Gradient
        JPanel background = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(
                        0, 0, new Color(225, 235, 255),
                        getWidth(), getHeight(), new Color(190, 210, 255)
                );
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        setContentPane(background);

        // 🩺 Title
        JLabel title = new JLabel("Edit Existing Medicine Stock", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 26));
        title.setForeground(new Color(45, 85, 225));
        title.setBorder(new EmptyBorder(20, 0, 10, 0));
        background.add(title, BorderLayout.NORTH);

        // 📋 Table Setup
        model = new DefaultTableModel(new String[]{
                "ID", "Medicine Name", "Brand Name", "Batch Code",
                "Quantity", "Unit Price", "Expiry Date"
        }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column != 0; // ID not editable
            }
        };

        table = new JTable(model);
        table.setRowHeight(32);
        table.setFont(new Font("SansSerif", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 15));
        table.setSelectionBackground(new Color(210, 225, 255));
        table.setGridColor(new Color(220, 230, 250));
        table.setFillsViewportHeight(true);
        table.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(new LineBorder(new Color(180, 200, 250), 1, true));
        background.add(scrollPane, BorderLayout.CENTER);

        // 🔘 Buttons Panel
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(new EmptyBorder(10, 20, 10, 20));
        bottomPanel.setBackground(new Color(245, 248, 255));

        JButton btnBack = new JButton("← Back");
        styleTextButton(btnBack);
        btnBack.addActionListener(e -> {
            fadeOut();
            new WelcomeDashboardFrame().setVisible(true);
            dispose();
        });

        JButton btnApply = new JButton("Apply Changes");
        stylePrimaryButton(btnApply);
        btnApply.addActionListener(e -> applyChanges());

        bottomPanel.add(btnBack, BorderLayout.WEST);
        bottomPanel.add(btnApply, BorderLayout.EAST);
        background.add(bottomPanel, BorderLayout.SOUTH);

        // ✨ Load Data Initially
        loadData();

        // ✨ Animation
        addWindowListener(new WindowAdapter() {
            @Override public void windowOpened(WindowEvent e) { fadeIn(); }
        });
    }

    /** Load Data from MySQL */
    private void loadData() {
        model.setRowCount(0);
        try (Connection con = DB.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM pharmacy_items ORDER BY item_id ASC")) {
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("item_id"),
                        rs.getString("item_name"),
                        rs.getString("brand_name"),
                        rs.getString("batch_code"),
                        rs.getInt("stock_qty"),
                        rs.getDouble("unit_price"),
                        rs.getDate("expiry")
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "❌ Error loading data: " + e.getMessage());
        }
    }

    /** Apply All Changes with Confirmation */
    private void applyChanges() {
        if (model.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "No data to update.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to apply all changes to the database?",
                "Confirm Changes",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirm != JOptionPane.YES_OPTION) return;

        try (Connection con = DB.getConnection()) {
            String sql = "UPDATE pharmacy_items SET item_name=?, brand_name=?, batch_code=?, stock_qty=?, unit_price=?, expiry=? WHERE item_id=?";
            PreparedStatement ps = con.prepareStatement(sql);

            for (int i = 0; i < model.getRowCount(); i++) {
                int id = Integer.parseInt(model.getValueAt(i, 0).toString());
                String name = model.getValueAt(i, 1).toString();
                String brand = model.getValueAt(i, 2).toString();
                String batch = model.getValueAt(i, 3).toString();
                int qty = Integer.parseInt(model.getValueAt(i, 4).toString());
                double price = Double.parseDouble(model.getValueAt(i, 5).toString());
                String expiry = model.getValueAt(i, 6).toString();

                ps.setString(1, name);
                ps.setString(2, brand);
                ps.setString(3, batch);
                ps.setInt(4, qty);
                ps.setDouble(5, price);
                ps.setDate(6, Date.valueOf(expiry));
                ps.setInt(7, id);
                ps.addBatch();
            }

            ps.executeBatch();
            JOptionPane.showMessageDialog(this, "✅ All changes applied successfully!");
            loadData();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "❌ Error applying changes: " + e.getMessage());
        }
    }

    /** Reusable Button Styles */
    private void stylePrimaryButton(JButton btn) {
        btn.setBackground(new Color(65, 105, 225));
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("SansSerif", Font.BOLD, 15));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(10, 25, 10, 25));
        btn.setOpaque(true);
        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { btn.setBackground(new Color(85, 125, 255)); }
            @Override public void mouseExited(MouseEvent e) { btn.setBackground(new Color(65, 105, 225)); }
        });
    }

    private void styleTextButton(JButton btn) {
        btn.setFont(new Font("SansSerif", Font.BOLD, 14));
        btn.setForeground(new Color(65, 105, 225));
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
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
        SwingUtilities.invokeLater(() -> new EditStockFrame().setVisible(true));
    }
}
