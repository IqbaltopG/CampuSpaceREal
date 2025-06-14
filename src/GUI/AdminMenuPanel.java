package GUI;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import javax.swing.table.DefaultTableModel;

public class AdminMenuPanel extends JPanel {
    private JTable bookingTable;
    private JTable userTable;
    private DefaultTableModel bookingModel;
    private DefaultTableModel userModel;

    public AdminMenuPanel() {
        setLayout(new GridLayout(2, 1, 10, 10));

        // Booking Table
        bookingModel = new DefaultTableModel(new String[] { "Booking ID", "User ID", "Room", "Date" }, 0);
        bookingTable = new JTable(bookingModel);
        loadBookingData();
        add(new JScrollPane(bookingTable));

        // User Table
        userModel = new DefaultTableModel(new String[] { "ID", "Username", "Role" }, 0);
        userTable = new JTable(userModel);
        loadUserData();
        add(new JScrollPane(userTable));
    }

    private void loadBookingData() {
        bookingModel.setRowCount(0);
        try (Connection conn = DB.Connection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(
                     "SELECT b.booking_id, b.start_time, b.stop_time, b.user_id, b.room_id, l.timestamp AS booking_date "
                             +
                             "FROM bookings b " +
                             "JOIN logs l ON b.user_id = l.user_id")) {
            while (rs.next()) {
                bookingModel.addRow(new Object[] {
                        rs.getInt("booking_id"),
                        rs.getInt("user_id"),
                        rs.getString("room_id"),
                        rs.getString("booking_date") + " " + rs.getString("start_time") + " - "
                                + rs.getString("stop_time")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadUserData() {
        userModel.setRowCount(0);
        try (Connection conn = DB.Connection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT user_id, username, role FROM user")) {
            while (rs.next()) {
                int userId = rs.getInt("user_id");
                userModel.addRow(new Object[] {
                        userId,
                        rs.getString("username"),
                        rs.getString("role")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}