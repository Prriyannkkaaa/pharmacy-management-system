package ui;

import config.DB;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.Vector;

public class CartFrame extends JFrame {
    public static Vector<Vector<Object>> cartData = new Vector<>();
    private JTable cartTable;
    private DefaultTableModel model;
    private float opacity = 0f;

    public CartFrame() {
        setTitle("Pharmacy Cart");
        setSize(950, 600);
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
        JLabel title = new JLabel("Pharmacy Cart", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 26));
        title.setForeground(new Color(45, 85, 225));
        title.setBorder(new EmptyBorder(20, 0, 20, 0));
        background.add(title, BorderLayout.NORTH);

        // 📋 Table setup
        String[] cols = {"Batch Code", "Medicine Name", "Price per Unit (₹)", "Units", "Total (₹)"};
        model = new DefaultTableModel(cols, 0);
        cartTable = new JTable(model);
        cartTable.setRowHeight(28);
        cartTable.setFont(new Font("SansSerif", Font.PLAIN, 14));
        cartTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 14));
        cartTable.getTableHeader().setBackground(new Color(65, 105, 225));
        cartTable.getTableHeader().setForeground(Color.WHITE);
        cartTable.setGridColor(new Color(200, 210, 240));
        cartTable.setSelectionBackground(new Color(210, 230, 255));

        JScrollPane scrollPane = new JScrollPane(cartTable);
        scrollPane.setBorder(new LineBorder(new Color(190, 210, 250), 1, true));
        background.add(scrollPane, BorderLayout.CENTER);

        // 🔘 Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 15));
        buttonPanel.setOpaque(false);

        JButton btnRemove = createBlueButton("Remove Selected");
        JButton btnDownload = createBlueButton("Download Bill");
        JButton btnBack = createBlueButton("← Back to Billing");

        buttonPanel.add(btnRemove);
        buttonPanel.add(btnDownload);
        buttonPanel.add(btnBack);

        background.add(buttonPanel, BorderLayout.SOUTH);

        // Load Data
        loadCartData();

        // Button actions
        btnBack.addActionListener(e -> {
            fadeOut();
            new BillingFrame().setVisible(true);
            dispose();
        });

        btnRemove.addActionListener(e -> removeSelectedRow());
        btnDownload.addActionListener(e -> {
            if (cartData.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Your cart is empty!");
                return;
            }
            generateBillFile();    // Generate bill
            updateStockAfterPurchase(); //  Update stock after purchase
        });

        //  Fade Animation
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                fadeIn();
            }
        });
    }

    // Create Styled Button
    private JButton createBlueButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(new Color(45, 85, 225));
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("SansSerif", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setOpaque(true);
        btn.setBorder(new EmptyBorder(10, 20, 10, 20));
        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { btn.setBackground(new Color(70, 115, 255)); }
            @Override public void mouseExited(MouseEvent e) { btn.setBackground(new Color(45, 85, 225)); }
        });
        return btn;
    }

    // 🧺 Load Data into Table
    private void loadCartData() {
        model.setRowCount(0);
        for (Vector<Object> row : cartData) {
            model.addRow(row);
        }
    }

    // ❌ Remove Selected Row
    private void removeSelectedRow() {
        int selectedRow = cartTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a row to remove.");
            return;
        }
        cartData.remove(selectedRow);
        model.removeRow(selectedRow);
        JOptionPane.showMessageDialog(this, "Item removed successfully!");
    }

    // 💾 Generate Bill File
    private void generateBillFile() {
        try {
            java.io.File downloads = new java.io.File(System.getProperty("user.home") + "/Downloads");
            if (!downloads.exists()) downloads.mkdirs();

            java.io.File file = new java.io.File(downloads, "Pharmacy_Bill_" + System.currentTimeMillis() + ".txt");
            java.io.FileWriter writer = new java.io.FileWriter(file);

            writer.write("========= PHARMACY BILL =========\n\n");
            writer.write(String.format("%-15s %-25s %-15s %-10s %-10s\n",
                    "Batch Code", "Medicine Name", "Price", "Units", "Total"));
            writer.write("-------------------------------------------------------------\n");

            double grandTotal = 0;
            for (Vector<Object> row : cartData) {
                writer.write(String.format("%-15s %-25s %-15s %-10s %-10s\n",
                        row.get(0), row.get(1), row.get(2), row.get(3), row.get(4)));
                grandTotal += Double.parseDouble(row.get(4).toString());
            }

            writer.write("\n-------------------------------------------------------------\n");
            writer.write(String.format("Grand Total: ₹%.2f\n", grandTotal));
            writer.write("\nThank you for shopping with us!\n");
            writer.close();

            JOptionPane.showMessageDialog(this, "🧾 Bill saved in Downloads folder!");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error while saving bill: " + e.getMessage());
        }
    }

    // ✅ Update Stock After Purchase
    private void updateStockAfterPurchase() {
        try (Connection con = DB.getConnection()) {
            String checkSql = "SELECT stock_qty FROM pharmacy_items WHERE batch_code = ?";
            String updateSql = "UPDATE pharmacy_items SET stock_qty = stock_qty - ? WHERE batch_code = ?";
            PreparedStatement checkPs = con.prepareStatement(checkSql);
            PreparedStatement updatePs = con.prepareStatement(updateSql);

            for (Vector<Object> row : cartData) {
                String batchCode = row.get(0).toString();
                int unitsPurchased = Integer.parseInt(row.get(3).toString());

                // Check stock
                checkPs.setString(1, batchCode);
                ResultSet rs = checkPs.executeQuery();

                if (rs.next()) {
                    int currentStock = rs.getInt("stock_qty");

                    if (unitsPurchased > currentStock) {
                        JOptionPane.showMessageDialog(this,
                                " Not enough stock for " + row.get(1) + "! Available: " + currentStock);
                        continue;
                    }

                    // Update stock
                    updatePs.setInt(1, unitsPurchased);
                    updatePs.setString(2, batchCode);
                    updatePs.executeUpdate();
                }
            }

            JOptionPane.showMessageDialog(this, "Stock updated successfully after purchase!");
            cartData.clear(); // empty cart
            model.setRowCount(0);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, " Error updating stock: " + e.getMessage());
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

    // 🏁 Main
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CartFrame().setVisible(true));
    }
}
