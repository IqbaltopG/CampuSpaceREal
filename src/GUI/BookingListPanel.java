package GUI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class BookingListPanel extends JPanel {
    public BookingListPanel() {
        setLayout(new BorderLayout());

        String[] columns = {"ID", "User", "Ruangan", "Tanggal Mulai", "Tanggal Selesai", "Status"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);
        JScrollPane scroll = new JScrollPane(table);
        add(scroll, BorderLayout.CENTER);

        // Ambil data booking dari database
        try (
            java.sql.Connection conn = DB.Connection.getConnection();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(
                "SELECT b.booking_id, u.username, r.name AS room, b.start_time, b.stop_time, b.status " +
                "FROM bookings b " +
                "JOIN user u ON b.user_id = u.user_id " +
                "JOIN rooms r ON b.room_id = r.room_id " +
                "ORDER BY b.booking_id DESC"
            )
        ) {
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("booking_id"),
                    rs.getString("username"),
                    rs.getString("room"),
                    rs.getString("start_time"),
                    rs.getString("stop_time"),
                    rs.getString("status")
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal memuat data booking!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}