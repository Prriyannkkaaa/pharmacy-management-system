package ui;

import config.DB;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class CheckStockFrame extends JFrame {
    private JTextField tfSearch;
    private JTable table;
    private DefaultTableModel model;
    private float opacity = 0f;

    public CheckStockFrame() {
        setTitle("Check Stock");
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        setLayout(new BorderLayout(10, 10));

        // 🌈 Background Gradient
        JPanel background = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(
                        0, 0, new Color(225, 235, 255),
                        getWidth(), getHeight(), new Color(190, 210, 255)
                );
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        setContentPane(background);

        // 🩺 Title
        JLabel title = new JLabel("Check Medicine Stock", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 26));
        title.setForeground(new Color(45, 85, 225));
        title.setBorder(new EmptyBorder(20, 0, 10, 0));
        background.add(title, BorderLayout.NORTH);

        // 🔍 Search Panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        searchPanel.setOpaque(false);

        tfSearch = new JTextField();
        tfSearch.setPreferredSize(new Dimension(300, 38));
        tfSearch.setFont(new Font("SansSerif", Font.PLAIN, 15));
        tfSearch.setBorder(new CompoundBorder(
                new LineBorder(new Color(180, 200, 240), 1, true),
                new EmptyBorder(5, 10, 5, 10)
        ));

        JButton btnSearch = new JButton("Search");
        btnSearch.setFont(new Font("SansSerif", Font.BOLD, 15));
        btnSearch.setForeground(Color.WHITE);
        btnSearch.setBackground(new Color(65, 105, 225));
        btnSearch.setFocusPainted(false);
        btnSearch.setBorder(new EmptyBorder(8, 20, 8, 20));
        btnSearch.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSearch.setBorderPainted(false);
        btnSearch.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { btnSearch.setBackground(new Color(85, 125, 255)); }
            @Override public void mouseExited(MouseEvent e) { btnSearch.setBackground(new Color(65, 105, 225)); }
        });

        // 🔹 Action: Perform Search
        btnSearch.addActionListener(e -> performSearch(tfSearch.getText().trim()));

        searchPanel.add(tfSearch);
        searchPanel.add(btnSearch);

        background.add(searchPanel, BorderLayout.NORTH);

        // 📋 Table Setup
        model = new DefaultTableModel(new String[]{
                "ID", "Medicine Name", "Brand Name", "Batch Code",
                "Quantity", "Unit Price", "Expiry Date"
        }, 0);
        table = new JTable(model);
        table.setRowHeight(30);
        table.setFont(new Font("SansSerif", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 15));
        table.setGridColor(new Color(220, 230, 250));
        table.setSelectionBackground(new Color(210, 225, 255));
        table.setFillsViewportHeight(true);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(new LineBorder(new Color(180, 200, 250), 1, true));
        background.add(scrollPane, BorderLayout.CENTER);

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

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(new EmptyBorder(10, 15, 10, 15));
        bottomPanel.add(btnBack, BorderLayout.WEST);
        background.add(bottomPanel, BorderLayout.SOUTH);

        // ✨ Animation
        addWindowListener(new WindowAdapter() {
            @Override public void windowOpened(WindowEvent e) { fadeIn(); }
        });
    }

    /** Search function: fetch results from database */
    private void performSearch(String keyword) {
        model.setRowCount(0);

        if (keyword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a medicine name to search.");
            return;
        }

        try (Connection con = DB.getConnection()) {
            String sql = "SELECT * FROM pharmacy_items WHERE item_name LIKE ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, "%" + keyword + "%");
            ResultSet rs = ps.executeQuery();

            boolean hasResults = false;
            while (rs.next()) {
                hasResults = true;
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

            if (!hasResults) {
                JOptionPane.showMessageDialog(this, "No results found for \"" + keyword + "\".");
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "❌ Error searching data: " + e.getMessage());
        }
    }

    /** Fade Animations */
    private void fadeIn() {
        Timer t = new Timer(25, e -> {
            opacity += 0.05f;
            if (opacity >= 1f) {
                opacity = 1f;
                ((Timer)e.getSource()).stop();
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
                ((Timer)e.getSource()).stop();
            }
            repaint();
        });
        t.start();
        try { Thread.sleep(300); } catch (InterruptedException ignored) {}
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CheckStockFrame().setVisible(true));
    }
}
