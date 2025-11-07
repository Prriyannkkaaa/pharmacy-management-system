package ui;

import config.DB;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class DeleteStockFrame extends JFrame {
    private JTable table;
    private DefaultTableModel model;
    private float opacity = 0f;

    public DeleteStockFrame() {
        setTitle("Delete Medicines - Pharmacy Management");
        setSize(950, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        setLayout(new BorderLayout(10, 10));

        // 🌈 Background Gradient
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

        // 🧾 Title
        JLabel title = new JLabel("Delete Medicine Records", SwingConstants.CENTER);
        title.setFont(new java.awt.Font("SansSerif", java.awt.Font.BOLD, 26));
        title.setForeground(new Color(45, 85, 225));
        title.setBorder(new EmptyBorder(20, 0, 20, 0));
        background.add(title, BorderLayout.NORTH);

        // 📋 Table Setup
        String[] cols = {"Batch Code", "Medicine Name", "Brand", "Price per Unit (₹)", "Delete"};
        model = new DefaultTableModel(cols, 0);
        table = new JTable(model) {
            @Override
            public Class<?> getColumnClass(int column) {
                if (column == 4) return Icon.class;
                return Object.class;
            }
        };

        table.setRowHeight(40);
        table.setFont(new java.awt.Font("SansSerif", java.awt.Font.PLAIN, 14));
        table.getTableHeader().setFont(new java.awt.Font("SansSerif", java.awt.Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(65, 105, 225));
        table.getTableHeader().setForeground(Color.WHITE);
        table.setGridColor(new Color(200, 210, 240));
        table.setSelectionBackground(new Color(210, 230, 255));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(new LineBorder(new Color(190, 210, 250), 1, true));
        background.add(scrollPane, BorderLayout.CENTER);

        // 🔙 Back Button
        JButton btnBack = new JButton("← Back");
        btnBack.setFont(new java.awt.Font("SansSerif", java.awt.Font.BOLD, 13));
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

        // 🗑️ Load Table Data
        loadMedicines();

        // ✨ Handle Delete Button Click
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());

                if (col == 4) { // Delete icon column
                    String batchCode = model.getValueAt(row, 0).toString();
                    int confirm = JOptionPane.showConfirmDialog(
                            null,
                            "Are you sure you want to delete this medicine?",
                            "Confirm Deletion",
                            JOptionPane.YES_NO_OPTION
                    );

                    if (confirm == JOptionPane.YES_OPTION) {
                        deleteMedicine(batchCode);
                        model.removeRow(row);
                    }
                }
            }
        });

        // ✨ Fade Animation
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                fadeIn();
            }
        });
    }

    // 🔹 Load All Medicines from DB
    private void loadMedicines() {
        try (Connection con = DB.getConnection()) {
            String sql = "SELECT batch_code, item_name, brand_name, unit_price FROM pharmacy_items";
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            ImageIcon binIcon = new ImageIcon(
                    new ImageIcon("src/ui/assets/bin.png").getImage().getScaledInstance(25, 25, Image.SCALE_SMOOTH)
            );

            while (rs.next()) {
                Object[] row = {
                        rs.getString("batch_code"),
                        rs.getString("item_name"),
                        rs.getString("brand_name"),
                        rs.getDouble("unit_price"),
                        binIcon
                };
                model.addRow(row);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading data: " + e.getMessage());
        }
    }

    // ❌ Delete Medicine from Database
    private void deleteMedicine(String batchCode) {
        try (Connection con = DB.getConnection()) {
            String sql = "DELETE FROM pharmacy_items WHERE batch_code=?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, batchCode);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "🗑️ Medicine deleted successfully!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error deleting record: " + e.getMessage());
        }
    }

    // 🌟 Fade Animations
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
        try { Thread.sleep(300); } catch (InterruptedException ignored) {}
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new DeleteStockFrame().setVisible(true));
    }
}
