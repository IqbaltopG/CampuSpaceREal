package GUI;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import javax.swing.table.DefaultTableModel;

public class SuperAdminPanel extends JFrame {
    private JTable adminTable;
    private AdminTableModel tableModel;

    public SuperAdminPanel() {
        setTitle("SuperAdmin Panel");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JLabel label = new JLabel("Panel SuperAdmin: Suspend Admin");
        add(label, BorderLayout.NORTH);

        // Tabel admin
        tableModel = new AdminTableModel();
        adminTable = new JTable((javax.swing.table.TableModel) tableModel);
        loadAdminData();

        JButton suspendBtn = new JButton("Suspend Admin");
        JButton unsuspendBtn = new JButton("Unsuspend Admin");

        suspendBtn.addActionListener(e -> {
            int selectedRow = adminTable.getSelectedRow();
            if (selectedRow != -1) {
                String adminUsername = adminTable.getValueAt(selectedRow, 0).toString();
                suspendAdmin(adminUsername);
                loadAdminData(); // refresh table
            }
        });

        unsuspendBtn.addActionListener(e -> {
            int selectedRow = adminTable.getSelectedRow();
            if (selectedRow != -1) {
                String adminUsername = adminTable.getValueAt(selectedRow, 0).toString();
                unsuspendAdmin(adminUsername);
                loadAdminData(); // refresh table
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(suspendBtn);
        buttonPanel.add(unsuspendBtn);

        add(new JScrollPane(adminTable), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void loadAdminData() {
        tableModel.clear();
        try (Connection conn = DB.Connection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                 "SELECT username, role FROM user WHERE role='admin'")) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                tableModel.addRow(new Object[] {
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
            JOptionPane.showMessageDialog(this, "Admin suspended (role diubah)!");
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
            JOptionPane.showMessageDialog(this, "Admin berhasil di-unsuspend!");
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    static class AdminTableModel extends DefaultTableModel {
        public AdminTableModel() {
            super(new String[] { "Username", "Role" }, 0);
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
