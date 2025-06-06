package GUI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ReserveRoomPanel extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private JComboBox<String> buildingBox, roomBox, sessionBox;
    private JTextField purposeField;
    private JSpinner dateSpinner;
    private JButton addBtn, updateBtn, deleteBtn, clearBtn, approveBtn, rejectBtn;
    private int selectedBookingId = -1;

    // Tambahkan variabel userId dan waktu
    private int userId;

    public ReserveRoomPanel(int userId, String role, DefaultListModel<String> notificationList, Runnable onSuccess) {
        this.userId = userId;
        setLayout(null);
        setBackground(Color.WHITE);

        // --- Form ---
        buildingBox = new JComboBox<>();
        roomBox = new JComboBox<>();
        sessionBox = new JComboBox<>(new String[] { "Sesi 1", "Sesi 2", "Sesi 3", "Sesi 4" });
        purposeField = new JTextField();
        dateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor editor = new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd");
        dateSpinner.setEditor(editor);

        add(new JLabel("Gedung:")).setBounds(20, 20, 80, 25);
        buildingBox.setBounds(100, 20, 150, 25);
        add(buildingBox);
        add(new JLabel("Ruangan:")).setBounds(270, 20, 80, 25);
        roomBox.setBounds(350, 20, 150, 25);
        add(roomBox);
        add(new JLabel("Tanggal:")).setBounds(20, 60, 80, 25);
        dateSpinner.setBounds(100, 60, 150, 25);
        add(dateSpinner);
        add(new JLabel("Sesi:")).setBounds(270, 60, 80, 25);
        sessionBox.setBounds(350, 60, 150, 25);
        add(sessionBox);
        add(new JLabel("Keperluan:")).setBounds(20, 100, 80, 25);
        purposeField.setBounds(100, 100, 400, 25);
        add(purposeField);

        addBtn = new JButton("Tambah");
        updateBtn = new JButton("Ubah");
        deleteBtn = new JButton("Hapus");
        clearBtn = new JButton("Clear");
        approveBtn = new JButton("Approve");
        rejectBtn = new JButton("Reject");
        addBtn.setBounds(100, 140, 90, 30);
        add(addBtn);
        updateBtn.setBounds(200, 140, 90, 30);
        add(updateBtn);
        deleteBtn.setBounds(300, 140, 90, 30);
        add(deleteBtn);
        clearBtn.setBounds(400, 140, 90, 30);
        add(clearBtn);
        approveBtn.setBounds(500, 140, 90, 30);
        rejectBtn.setBounds(600, 140, 90, 30);
        add(approveBtn);
        add(rejectBtn);

        // --- Table ---
        tableModel = new DefaultTableModel(new String[] { "ID", "Gedung", "Ruangan", "Tanggal", "Sesi", "Keperluan" },
                0);
        table = new JTable(tableModel);
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBounds(20, 190, 700, 250);
        add(scroll);

        // --- Load Data ---
        loadBuildings();
        loadBookings();

        // --- Event ---
        buildingBox.addActionListener(e -> loadRooms());
        addBtn.addActionListener(e -> createBooking());
        updateBtn.addActionListener(e -> updateBooking());
        deleteBtn.addActionListener(e -> deleteBooking());
        clearBtn.addActionListener(e -> clearForm());
        table.getSelectionModel().addListSelectionListener(e -> fillFormFromTable());
        if ("admin".equals(role) || "superadmin".equals(role)) {
            add(approveBtn);
            add(rejectBtn);
            approveBtn.addActionListener(e -> updateStatusBooking("Approved"));
            rejectBtn.addActionListener(e -> updateStatusBooking("Rejected"));
        }

        // --- Initial ---
        loadRooms();
    }

    private void loadBuildings() {
        buildingBox.removeAllItems();
        try (java.sql.Connection conn = DB.Connection.getConnection();
                Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery("SELECT building_id, name FROM buildings")) {
            while (rs.next()) {
                buildingBox.addItem(rs.getInt("building_id") + " - " + rs.getString("name"));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void loadRooms() {
        roomBox.removeAllItems();
        if (buildingBox.getSelectedItem() == null)
            return;
        String buildingId = buildingBox.getSelectedItem().toString().split(" - ")[0];
        try (java.sql.Connection conn = DB.Connection.getConnection();
                PreparedStatement ps = conn.prepareStatement("SELECT room_id, name FROM rooms WHERE building_id=?")) {
            ps.setInt(1, Integer.parseInt(buildingId));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                roomBox.addItem(rs.getInt("room_id") + " - " + rs.getString("name"));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void loadBookings() {
        tableModel.setRowCount(0);
        try (java.sql.Connection conn = DB.Connection.getConnection();
                Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery(
                        "SELECT b.booking_id, g.name as building, r.name as room, b.start_time, b.stop_time, b.status "
                                +
                                "FROM bookings b " +
                                "JOIN rooms r ON b.room_id=r.room_id " +
                                "JOIN buildings g ON r.building_id=g.building_id")) {
            while (rs.next()) {
                tableModel.addRow(new Object[] {
                        rs.getInt("booking_id"),
                        rs.getString("building"),
                        rs.getString("room"),
                        rs.getString("start_time"),
                        rs.getString("stop_time"),
                        rs.getString("status")
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private Timestamp getStartTime() {
        // Ambil tanggal dari dateSpinner dan sesi dari sessionBox
        Date date = (Date) dateSpinner.getValue();
        String session = (String) sessionBox.getSelectedItem();
        // Contoh mapping sesi ke jam (silakan sesuaikan)
        String jamMulai;
        switch (session) {
            case "Sesi 1":
                jamMulai = "08:00:00";
                break;
            case "Sesi 2":
                jamMulai = "10:00:00";
                break;
            case "Sesi 3":
                jamMulai = "13:00:00";
                break;
            case "Sesi 4":
                jamMulai = "15:00:00";
                break;
            default:
                jamMulai = "08:00:00";
        }
        return Timestamp.valueOf(new SimpleDateFormat("yyyy-MM-dd").format(date) + " " + jamMulai);
    }

    private Timestamp getStopTime() {
        Date date = (Date) dateSpinner.getValue();
        String session = (String) sessionBox.getSelectedItem();
        String jamSelesai;
        switch (session) {
            case "Sesi 1":
                jamSelesai = "10:00:00";
                break;
            case "Sesi 2":
                jamSelesai = "12:00:00";
                break;
            case "Sesi 3":
                jamSelesai = "15:00:00";
                break;
            case "Sesi 4":
                jamSelesai = "17:00:00";
                break;
            default:
                jamSelesai = "10:00:00";
        }
        return Timestamp.valueOf(new SimpleDateFormat("yyyy-MM-dd").format(date) + " " + jamSelesai);
    }

    // CREATE
    private void createBooking() {
        try (java.sql.Connection conn = DB.Connection.getConnection()) {
            String roomId = roomBox.getSelectedItem().toString().split(" - ")[0];
            PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO bookings (user_id, room_id, status, start_time, stop_time) VALUES (?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, userId);
            ps.setInt(2, Integer.parseInt(roomId));
            ps.setString(3, "Pending");
            ps.setTimestamp(4, getStartTime());
            ps.setTimestamp(5, getStopTime());
            ps.executeUpdate();

            ResultSet keys = ps.getGeneratedKeys();
            int bookingId = -1;
            if (keys.next())
                bookingId = keys.getInt(1);

            logAction(conn, "CREATE", bookingId);

            JOptionPane.showMessageDialog(this, "Booking berhasil ditambahkan!");
            loadBookings();
            clearForm();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // UPDATE
    private void updateBooking() {
        if (selectedBookingId == -1)
            return;
        try (java.sql.Connection conn = DB.Connection.getConnection()) {
            String roomId = roomBox.getSelectedItem().toString().split(" - ")[0];
            PreparedStatement ps = conn.prepareStatement(
                    "UPDATE bookings SET room_id=?, start_time=?, stop_time=? WHERE booking_id=?");
            ps.setInt(1, Integer.parseInt(roomId));
            ps.setTimestamp(2, getStartTime());
            ps.setTimestamp(3, getStopTime());
            ps.setInt(4, selectedBookingId);
            ps.executeUpdate();

            logAction(conn, "UPDATE", selectedBookingId);

            JOptionPane.showMessageDialog(this, "Booking berhasil diubah!");
            loadBookings();
            clearForm();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // DELETE
    private void deleteBooking() {
        if (selectedBookingId == -1)
            return;
        try (java.sql.Connection conn = DB.Connection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("DELETE FROM bookings WHERE booking_id=?");
            ps.setInt(1, selectedBookingId);
            ps.executeUpdate();

            logAction(conn, "DELETE", selectedBookingId);

            JOptionPane.showMessageDialog(this, "Booking berhasil dihapus!");
            loadBookings();
            clearForm();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void clearForm() {
        selectedBookingId = -1;
        if (buildingBox.getItemCount() > 0) buildingBox.setSelectedIndex(0);
        loadRooms();
        if (roomBox.getItemCount() > 0) roomBox.setSelectedIndex(0);
        dateSpinner.setValue(new Date());
        if (sessionBox.getItemCount() > 0) sessionBox.setSelectedIndex(0);
        purposeField.setText("");
        table.clearSelection();
    }

    private void fillFormFromTable() {
        int row = table.getSelectedRow();
        if (row == -1)
            return;
        selectedBookingId = (int) tableModel.getValueAt(row, 0);
        buildingBox.setSelectedItem(findComboItem(buildingBox, (String) tableModel.getValueAt(row, 1)));
        loadRooms();
        roomBox.setSelectedItem(findComboItem(roomBox, (String) tableModel.getValueAt(row, 2)));
        try {
            dateSpinner.setValue(new SimpleDateFormat("yyyy-MM-dd").parse((String) tableModel.getValueAt(row, 3)));
        } catch (Exception ex) {
            dateSpinner.setValue(new Date());
        }
        sessionBox.setSelectedItem(tableModel.getValueAt(row, 4));
        purposeField.setText((String) tableModel.getValueAt(row, 5));
        String status = (String) tableModel.getValueAt(row, 5);
        boolean pending = "Pending".equalsIgnoreCase(status);
        approveBtn.setEnabled(pending);
        rejectBtn.setEnabled(pending);
    }

    private String findComboItem(JComboBox<String> box, String name) {
        for (int i = 0; i < box.getItemCount(); i++) {
            String item = box.getItemAt(i);
            if (item.contains(name))
                return item;
        }
        return null;
    }

    // LOG
    private void logAction(java.sql.Connection conn, String action, int bookingId) {
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO logs (user_id, action, timestamp) VALUES (?, ?, NOW())")) {
            ps.setInt(1, userId);
            ps.setString(2, action + " booking_id=" + bookingId);
            ps.executeUpdate();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // UPDATE STATUS
    private void updateStatusBooking(String status) {
        if (selectedBookingId == -1) {
            JOptionPane.showMessageDialog(this, "Pilih booking yang ingin diubah statusnya!");
            return;
        }
        try (java.sql.Connection conn = DB.Connection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                    "UPDATE bookings SET status=? WHERE booking_id=?");
            ps.setString(1, status);
            ps.setInt(2, selectedBookingId);
            ps.executeUpdate();

            logAction(conn, status.toUpperCase(), selectedBookingId);

            JOptionPane.showMessageDialog(this, "Status booking berhasil diubah menjadi " + status + "!");
            loadBookings();
            clearForm();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}