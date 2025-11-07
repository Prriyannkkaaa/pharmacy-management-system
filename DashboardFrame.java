package ui;

import dao.ItemDAO;
import model.Item;
import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.List;

public class DashboardFrame extends JFrame {

    private JLabel lblTotalItems, lblStockValue, lblLowStock;
    private final ItemDAO dao = new ItemDAO();

    public DashboardFrame() {
        setTitle("💊 Pharmacy Management System - Dashboard");
        setSize(900, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(245, 250, 255));

        // 🌈 Header
        JLabel header = new JLabel("💊 Pharmacy Management System", SwingConstants.CENTER);
        header.setFont(new Font("SansSerif", Font.BOLD, 24));
        header.setOpaque(true);
        header.setBackground(new Color(65, 105, 225));
        header.setForeground(Color.WHITE);
        header.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));
        add(header, BorderLayout.NORTH);

        // 🧾 Main Dashboard Panel
        JPanel panel = new JPanel(new GridLayout(1, 3, 20, 20));
        panel.setBackground(new Color(245, 250, 255));
        panel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        // Dashboard cards
        lblTotalItems = createCard("📦 Total Items", "0", new Color(46, 139, 87));
        lblStockValue = createCard("💰 Total Stock Value", "₹0.00", new Color(65, 105, 225));
        lblLowStock = createCard("⚠️ Low Stock Items", "0", new Color(255, 165, 0));

        panel.add(lblTotalItems);
        panel.add(lblStockValue);
        panel.add(lblLowStock);
        add(panel, BorderLayout.CENTER);

        // 🔘 Button Panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(230, 240, 250));
        JButton btnManageItems = new JButton("📋 Manage Items");
        JButton btnRefresh = new JButton("🔄 Refresh Dashboard");
        JButton btnExit = new JButton("🚪 Exit");

        styleButton(btnManageItems, new Color(65, 105, 225));
        styleButton(btnRefresh, new Color(46, 139, 87));
        styleButton(btnExit, new Color(220, 20, 60));

        buttonPanel.add(btnManageItems);
        buttonPanel.add(btnRefresh);
        buttonPanel.add(btnExit);
        add(buttonPanel, BorderLayout.SOUTH);

        // Button actions
        btnManageItems.addActionListener(e -> {
            new ItemFrame().setVisible(true);
            dispose(); // close dashboard
        });
        btnRefresh.addActionListener(e -> refreshData());
        btnExit.addActionListener(e -> System.exit(0));

        // Load dashboard data
        refreshData();
    }

    // 🎨 Create card panels dynamically
    private JLabel createCard(String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(color);
        card.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        card.setPreferredSize(new Dimension(250, 150));

        JLabel lblTitle = new JLabel(title, SwingConstants.CENTER);
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 18));
        lblTitle.setForeground(Color.WHITE);

        JLabel lblValue = new JLabel(value, SwingConstants.CENTER);
        lblValue.setFont(new Font("SansSerif", Font.BOLD, 24));
        lblValue.setForeground(Color.WHITE);

        card.add(lblTitle, BorderLayout.NORTH);
        card.add(lblValue, BorderLayout.CENTER);

        add(card);
        return lblValue;
    }

    // 🔄 Load and refresh dashboard values
    private void refreshData() {
        try {
            List<Item> items = dao.getAll();
            lblTotalItems.setText(String.valueOf(items.size()));

            double totalValue = items.stream().mapToDouble(i -> i.getStockQty() * i.getUnitPrice()).sum();
            lblStockValue.setText("₹" + new DecimalFormat("#,##0.00").format(totalValue));

            long lowStockCount = items.stream().filter(i -> i.getStockQty() < 10).count();
            lblLowStock.setText(String.valueOf(lowStockCount));

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading dashboard: " + e.getMessage());
        }
    }

    // 🖌️ Button styling
    private void styleButton(JButton btn, Color bg) {
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setFont(new Font("SansSerif", Font.BOLD, 14));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(180, 40));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new DashboardFrame().setVisible(true));
    }
}
