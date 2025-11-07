package ui;

import dao.ItemDAO;
import model.Item;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Date;
import java.util.List;
import java.util.stream.Collectors;

public class ItemFrame extends JFrame {
    private DefaultTableModel tableModel;
    private JTable table;
    private final ItemDAO dao = new ItemDAO();
    private JTextField searchField;

    public ItemFrame() {
        setTitle("💊 Pharmacy Management - Manage Items");
        setSize(950, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // 🌈 Global UI Styling
        UIManager.put("Label.font", new Font("SansSerif", Font.PLAIN, 14));
        UIManager.put("Button.font", new Font("SansSerif", Font.BOLD, 13));
        UIManager.put("Table.font", new Font("SansSerif", Font.PLAIN, 13));
        UIManager.put("TableHeader.font", new Font("SansSerif", Font.BOLD, 14));
        UIManager.put("Panel.background", new Color(245, 250, 255));

        // 🧰 Toolbar
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        toolBar.setBackground(new Color(230, 240, 250));
        toolBar.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JButton btnRefresh = new JButton("Refresh");
        JButton btnAdd = new JButton("Add Item");
        JButton btnEdit = new JButton("Edit Item");
        JButton btnDelete = new JButton("Delete");

        // 🔍 Search Field
        searchField = new JTextField(20);
        JButton btnSearch = new JButton("Search");

        // 🖌️ Button styling
        styleButton(btnRefresh, new Color(65, 105, 225)); // Blue
        styleButton(btnAdd, new Color(46, 139, 87));      // Green
        styleButton(btnEdit, new Color(255, 165, 0));     // Orange
        styleButton(btnDelete, new Color(220, 20, 60));   // Red
        styleButton(btnSearch, new Color(0, 123, 167));   // Teal Blue

        toolBar.add(btnRefresh);
        toolBar.add(btnAdd);
        toolBar.add(btnEdit);
        toolBar.add(btnDelete);
        toolBar.addSeparator();
        toolBar.add(new JLabel("  🔍 Search: "));
        toolBar.add(searchField);
        toolBar.add(btnSearch);

        add(toolBar, BorderLayout.NORTH);

        // 🧾 Table setup
        tableModel = new DefaultTableModel(new Object[]{
                "ID", "Name", "Brand", "Batch", "Qty", "Price", "Expiry"
        }, 0);
        table = new JTable(tableModel);
        table.setRowHeight(24);
        table.setSelectionBackground(new Color(220, 235, 255));
        table.setGridColor(new Color(230, 230, 230));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createTitledBorder("📦 Current Inventory"));
        add(scrollPane, BorderLayout.CENTER);

        // 🎛️ Event Listeners
        btnRefresh.addActionListener(e -> loadItems());
        btnAdd.addActionListener(e -> addItemDialog());
        btnEdit.addActionListener(e -> editSelectedItem());
        btnDelete.addActionListener(e -> deleteSelectedItem());
        btnSearch.addActionListener(e -> searchItems());

        // 🚀 Load initial data
        loadItems();
    }

    // 🔄 Load all items
    private void loadItems() {
        tableModel.setRowCount(0);
        try {
            List<Item> list = dao.getAll();
            for (Item item : list) {
                tableModel.addRow(new Object[]{
                        item.getItemId(),
                        item.getItemName(),
                        item.getBrandName(),
                        item.getBatchCode(),
                        item.getStockQty(),
                        item.getUnitPrice(),
                        item.getExpiry()
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading data: " + e.getMessage());
        }
    }

    // ➕ Add new item
    private void addItemDialog() {
        JTextField tfName = new JTextField();
        JTextField tfBrand = new JTextField();
        JTextField tfBatch = new JTextField();
        JTextField tfQty = new JTextField("0");
        JTextField tfPrice = new JTextField("0.0");
        JTextField tfExpiry = new JTextField("2026-12-31");

        JPanel panel = new JPanel(new GridLayout(0, 2, 6, 6));
        panel.add(new JLabel("Item Name:"));
        panel.add(tfName);
        panel.add(new JLabel("Brand Name:"));
        panel.add(tfBrand);
        panel.add(new JLabel("Batch Code:"));
        panel.add(tfBatch);
        panel.add(new JLabel("Stock Quantity:"));
        panel.add(tfQty);
        panel.add(new JLabel("Unit Price:"));
        panel.add(tfPrice);
        panel.add(new JLabel("Expiry (YYYY-MM-DD):"));
        panel.add(tfExpiry);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add New Item",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            try {
                Item newItem = new Item(
                        tfName.getText().trim(),
                        tfBrand.getText().trim(),
                        tfBatch.getText().trim(),
                        Integer.parseInt(tfQty.getText().trim()),
                        Double.parseDouble(tfPrice.getText().trim()),
                        Date.valueOf(tfExpiry.getText().trim())
                );
                dao.insert(newItem);
                loadItems();
                JOptionPane.showMessageDialog(this, "✅ Item added successfully!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "❌ Error adding item: " + ex.getMessage());
            }
        }
    }

    // ✏️ Edit selected item
    private void editSelectedItem() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select an item to edit.");
            return;
        }

        int id = (int) tableModel.getValueAt(row, 0);
        String name = (String) tableModel.getValueAt(row, 1);
        String brand = (String) tableModel.getValueAt(row, 2);
        String batch = (String) tableModel.getValueAt(row, 3);
        int qty = (int) tableModel.getValueAt(row, 4);
        double price = (double) tableModel.getValueAt(row, 5);
        Date expiry = (Date) tableModel.getValueAt(row, 6);

        JTextField tfName = new JTextField(name);
        JTextField tfBrand = new JTextField(brand);
        JTextField tfBatch = new JTextField(batch);
        JTextField tfQty = new JTextField(String.valueOf(qty));
        JTextField tfPrice = new JTextField(String.valueOf(price));
        JTextField tfExpiry = new JTextField(String.valueOf(expiry));

        JPanel panel = new JPanel(new GridLayout(0, 2, 6, 6));
        panel.add(new JLabel("Item Name:")); panel.add(tfName);
        panel.add(new JLabel("Brand Name:")); panel.add(tfBrand);
        panel.add(new JLabel("Batch Code:")); panel.add(tfBatch);
        panel.add(new JLabel("Stock Quantity:")); panel.add(tfQty);
        panel.add(new JLabel("Unit Price:")); panel.add(tfPrice);
        panel.add(new JLabel("Expiry (YYYY-MM-DD):")); panel.add(tfExpiry);

        int result = JOptionPane.showConfirmDialog(this, panel, "Edit Item",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            try {
                String sql = "UPDATE pharmacy_items SET item_name=?, brand_name=?, batch_code=?, stock_qty=?, unit_price=?, expiry=? WHERE item_id=?";
                var con = config.DB.getConnection();
                var ps = con.prepareStatement(sql);
                ps.setString(1, tfName.getText().trim());
                ps.setString(2, tfBrand.getText().trim());
                ps.setString(3, tfBatch.getText().trim());
                ps.setInt(4, Integer.parseInt(tfQty.getText().trim()));
                ps.setDouble(5, Double.parseDouble(tfPrice.getText().trim()));
                ps.setDate(6, Date.valueOf(tfExpiry.getText().trim()));
                ps.setInt(7, id);
                ps.executeUpdate();
                con.close();
                loadItems();
                JOptionPane.showMessageDialog(this, "✅ Item updated successfully!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "❌ Error updating item: " + ex.getMessage());
            }
        }
    }

    // 🗑️ Delete selected item
    private void deleteSelectedItem() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select an item to delete.");
            return;
        }

        int id = (int) tableModel.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete item ID " + id + "?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                if (dao.delete(id)) {
                    loadItems();
                    JOptionPane.showMessageDialog(this, "🗑️ Item deleted successfully!");
                } else {
                    JOptionPane.showMessageDialog(this, "❌ Failed to delete item.");
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "❌ Error deleting item: " + e.getMessage());
            }
        }
    }

    // 🔍 Search feature
    private void searchItems() {
        String query = searchField.getText().trim().toLowerCase();
        if (query.isEmpty()) {
            loadItems();
            return;
        }

        tableModel.setRowCount(0);
        try {
            List<Item> list = dao.getAll();
            List<Item> filtered = list.stream()
                    .filter(i -> i.getItemName().toLowerCase().contains(query) ||
                            i.getBrandName().toLowerCase().contains(query))
                    .collect(Collectors.toList());

            for (Item item : filtered) {
                tableModel.addRow(new Object[]{
                        item.getItemId(),
                        item.getItemName(),
                        item.getBrandName(),
                        item.getBatchCode(),
                        item.getStockQty(),
                        item.getUnitPrice(),
                        item.getExpiry()
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error searching: " + e.getMessage());
        }
    }

    // 🎨 Button styling helper
    private void styleButton(JButton btn, Color bg) {
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setFont(new Font("SansSerif", Font.BOLD, 13));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ItemFrame().setVisible(true));
    }
}
