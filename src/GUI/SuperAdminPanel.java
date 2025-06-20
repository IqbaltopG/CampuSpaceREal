package GUI;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import javax.swing.table.DefaultTableModel;

public class SuperAdminPanel extends JFrame {
    private JTable adminTable;
    private AdminTableModel tableModel;

    public SuperAdminPanel() {
        setTitle("SuperAdmin Panel - Manage Admins");
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(15, 15));

        // Header
        JLabel label = new JLabel("🛡️ Super Admin Control Panel", JLabel.CENTER);
        label.setFont(new Font("Segoe UI", Font.BOLD, 20));
        label.setBorder(BorderFactory.createEmptyBorder(20, 10, 10, 10));
        add(label, BorderLayout.NORTH);

        // Table
        tableModel = new AdminTableModel();
        adminTable = new JTable(tableModel);
        adminTable.setFillsViewportHeight(true);
        adminTable.setRowHeight(30);
        adminTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        adminTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        loadAdminData();

        JScrollPane scrollPane = new JScrollPane(adminTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Daftar Admin dan Status"));
        add(scrollPane, BorderLayout.CENTER);

        // Buttons
        JButton suspendBtn = new JButton("🔒 Suspend Admin");
        JButton unsuspendBtn = new JButton("🔓 Unsuspend Admin");

        suspendBtn.setFocusPainted(false);
        unsuspendBtn.setFocusPainted(false);

        suspendBtn.setBackground(new Color(220, 53, 69));
        suspendBtn.setForeground(Color.WHITE);

        unsuspendBtn.setBackground(new Color(40, 167, 69));
        unsuspendBtn.setForeground(Color.WHITE);

        suspendBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        unsuspendBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));

        suspendBtn.setPreferredSize(new Dimension(150, 40));
        unsuspendBtn.setPreferredSize(new Dimension(150, 40));

        suspendBtn.addActionListener(e -> {
            int selectedRow = adminTable.getSelectedRow();
            if (selectedRow != -1) {
                String adminUsername = adminTable.getValueAt(selectedRow, 0).toString();
                suspendAdmin(adminUsername);
                loadAdminData();
            }
        });

        unsuspendBtn.addActionListener(e -> {
            int selectedRow = adminTable.getSelectedRow();
            if (selectedRow != -1) {
                String adminUsername = adminTable.getValueAt(selectedRow, 0).toString();
                unsuspendAdmin(adminUsername);
                loadAdminData();
            }
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        buttonPanel.add(suspendBtn);
        buttonPanel.add(unsuspendBtn);
        add(buttonPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void loadAdminData() {
        tableModel.clear();
        try (Connection conn = DB.Connection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT username, role FROM user WHERE role='admin' OR role='suspended_admin'")) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getString("username"),
                        rs.getString("role")
                });
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void suspendAdmin(String username) {
        try (Connection conn = DB.Connection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "UPDATE user SET role='suspended_admin' WHERE username=? AND role='admin'")) {
            ps.setString(1, username);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "✅ Admin berhasil disuspend!");
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void unsuspendAdmin(String username) {
        try (Connection conn = DB.Connection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "UPDATE user SET role='admin' WHERE username=? AND role='suspended_admin'")) {
            ps.setString(1, username);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "✅ Admin berhasil di-unsuspend!");
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    static class AdminTableModel extends DefaultTableModel {
        public AdminTableModel() {
            super(new String[]{"Username", "Role"}, 0);
        }

        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }

        public void clear() {
            setRowCount(0);
        }
    }
}
