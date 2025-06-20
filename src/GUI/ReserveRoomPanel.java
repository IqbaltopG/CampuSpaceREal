package GUI;

import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.io.File;
import java.util.Date;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class ReserveRoomPanel extends BasePanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private JComboBox<String> buildingBox, roomBox, sessionBox;
    private JTextField purposeField;
    private JSpinner dateSpinner;
    private JButton addBtn, updateBtn, deleteBtn, clearBtn, approveBtn, rejectBtn, uploadPdfBtn;
    private int selectedBookingId = -1;
    private File selectedPdfFile = null;

    // Variabel userId dan role
    private int userId;
    private String role;
    private CampuSpaceDashboard dashboard;

    public ReserveRoomPanel(int userId, String role, DefaultListModel<String> notificationList, Runnable onSuccess,
            CampuSpaceDashboard dashboard) {
        this.userId = userId;
        this.role = role;
        this.dashboard = dashboard;
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
        uploadPdfBtn = new JButton("Upload Surat");

        addBtn.setBounds(100, 140, 90, 30);
        add(addBtn);
        updateBtn.setBounds(200, 140, 90, 30);
        add(updateBtn);
        deleteBtn.setBounds(300, 140, 90, 30);
        add(deleteBtn);
        clearBtn.setBounds(400, 140, 90, 30);
        add(clearBtn);
        uploadPdfBtn.setBounds(520, 100, 120, 25);
        add(uploadPdfBtn);

        // Tampilkan tombol approve/reject hanya untuk admin/superadmin
        if ("admin".equals(role) || "superadmin".equals(role)) {
            approveBtn.setBounds(500, 140, 90, 30);
            rejectBtn.setBounds(600, 140, 90, 30);
            add(approveBtn);
            add(rejectBtn);
            approveBtn.addActionListener(e -> updateStatusBooking("Approved"));
            rejectBtn.addActionListener(e -> updateStatusBooking("Rejected"));
        }

        // --- Table ---
        tableModel = new DefaultTableModel(
                new String[] { "ID", "Gedung", "Ruangan", "Tanggal", "Sesi", "Status", "Keperluan" }, 0);
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

        // --- Initial ---
        loadRooms();

        uploadPdfBtn.addActionListener(e -> uploadPdfFile());
    }

    // =========================
    // Data Loading Methods
    // =========================

    private void loadBuildings() {
        buildingBox.removeAllItems();
        try (Connection conn = DB.Connection.getConnection();
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
        try (Connection conn = DB.Connection.getConnection();
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
        try (Connection conn = DB.Connection.getConnection();
                Statement st = conn.createStatement()) {
            String sql;
            if ("admin".equals(role) || "superadmin".equals(role)) {
                sql = "SELECT b.booking_id, b.user_id, b.room_id, b.start_time, b.stop_time, b.status, b.purpose, b.pdf_path FROM bookings b " +
                        "JOIN rooms r ON b.room_id=r.room_id " +
                        "JOIN buildings g ON r.building_id=g.building_id";
            } else {
                sql = "SELECT b.booking_id, g.name as building, r.name as room, b.start_time, b.stop_time, b.status, b.purpose, b.pdf_path "
                        +
                        "FROM bookings b " +
                        "JOIN rooms r ON b.room_id=r.room_id " +
                        "JOIN buildings g ON r.building_id=g.building_id " +
                        "WHERE b.user_id=" + userId;
            }
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                int bookingId = rs.getInt("booking_id");
                String tanggalLog = getTanggalLogByBookingId(conn, bookingId);
                String sesi = getSesiFromStartTime(rs.getTimestamp("start_time"));
                tableModel.addRow(new Object[] {
                        bookingId,
                        rs.getString("building"),
                        rs.getString("room"),
                        tanggalLog,
                        sesi,
                        rs.getString("status"),
                        rs.getString("purpose")
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // =========================
    // Helper Methods
    // =========================

    private String getSesiFromStartTime(Timestamp startTime) {
        if (startTime == null)
            return "";
        String jam = new SimpleDateFormat("HH:mm:ss").format(startTime);
        switch (jam) {
            case "08:00:00":
                return "Sesi 1";
            case "10:00:00":
                return "Sesi 2";
            case "13:00:00":
                return "Sesi 3";
            case "15:00:00":
                return "Sesi 4";
            default:
                return jam;
        }
    }

    private String getTanggalLogByBookingId(Connection conn, int bookingId) {
        String tanggal = "";
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT DATE(timestamp) as tanggal FROM logs WHERE action LIKE ? ORDER BY timestamp ASC LIMIT 1")) {
            ps.setString(1, "CREATE booking_id=" + bookingId + "%");
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                tanggal = rs.getString("tanggal");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return tanggal;
    }

    private Timestamp getStartTime() {
        Date date = (Date) dateSpinner.getValue();
        String session = (String) sessionBox.getSelectedItem();
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

    private String findComboItem(JComboBox<String> box, String name) {
        for (int i = 0; i < box.getItemCount(); i++) {
            String item = box.getItemAt(i);
            if (item.contains(name))
                return item;
        }
        return null;
    }

    // =========================
    // CRUD Methods
    // =========================

    private void createBooking() {
        // Validasi form kosong
        if (buildingBox.getSelectedItem() == null ||
                roomBox.getSelectedItem() == null ||
                dateSpinner.getValue() == null ||
                sessionBox.getSelectedItem() == null ||
                purposeField.getText().trim().isEmpty() ||
                selectedPdfFile == null) {
            JOptionPane.showMessageDialog(this, "Semua field dan surat (PDF) wajib diisi!", "Validasi",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        try (Connection conn = DB.Connection.getConnection()) {
            String roomId = roomBox.getSelectedItem().toString().split(" - ")[0];
            Timestamp startTime = getStartTime();
            Timestamp stopTime = getStopTime();

            // Cek apakah sudah ada booking approved untuk ruangan, tanggal, dan sesi yang
            // sama
            PreparedStatement checkPs = conn.prepareStatement(
                    "SELECT COUNT(*) FROM bookings WHERE room_id=? AND start_time=? AND stop_time=? AND status='Approved'");
            checkPs.setInt(1, Integer.parseInt(roomId));
            checkPs.setTimestamp(2, startTime);
            checkPs.setTimestamp(3, stopTime);
            ResultSet checkRs = checkPs.executeQuery();
            if (checkRs.next() && checkRs.getInt(1) > 0) {
                JOptionPane.showMessageDialog(this, "Ruangan sudah di-booking dan di-approve pada waktu tersebut!");
                return;
            }

            String pdfPath = null;
            if (selectedPdfFile != null) {
                // Simpan ke folder pdf di dalam src
                File uploadsDir = new File("src/pdf");
                if (!uploadsDir.exists())
                    uploadsDir.mkdirs();
                File destFile = new File(uploadsDir, System.currentTimeMillis() + "_" + selectedPdfFile.getName());
                java.nio.file.Files.copy(selectedPdfFile.toPath(), destFile.toPath(),
                        java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                pdfPath = destFile.getAbsolutePath();
            }
            PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO bookings (user_id, room_id, status, start_time, stop_time, pdf_path, purpose) VALUES (?, ?, ?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, userId);
            ps.setInt(2, Integer.parseInt(roomId));
            ps.setString(3, "Pending");
            ps.setTimestamp(4, startTime);
            ps.setTimestamp(5, stopTime);
            ps.setString(6, pdfPath);
            ps.setString(7, purposeField.getText().trim());
            ps.executeUpdate();

            ResultSet keys = ps.getGeneratedKeys();
            int bookingId = -1;
            if (keys.next())
                bookingId = keys.getInt(1);

            logAction(conn, "CREATE", bookingId);

            JOptionPane.showMessageDialog(this, "Booking berhasil ditambahkan!");
            loadBookings();
            clearForm();
            selectedPdfFile = null;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void updateBooking() {
        if (selectedBookingId == -1)
            return;
        try (Connection conn = DB.Connection.getConnection()) {
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

    private void deleteBooking() {
        if (selectedBookingId == -1)
            return;
        try (Connection conn = DB.Connection.getConnection()) {
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
        if (buildingBox.getItemCount() > 0)
            buildingBox.setSelectedIndex(0);
        loadRooms();
        if (roomBox.getItemCount() > 0)
            dateSpinner.setValue(new java.util.Date());
        dateSpinner.setValue(new Date());
        if (sessionBox.getItemCount() > 0)
            sessionBox.setSelectedIndex(0);
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
        purposeField.setText((String) tableModel.getValueAt(row, 6));
        String status = (String) tableModel.getValueAt(row, 5);
        boolean pending = "Pending".equalsIgnoreCase(status);
        approveBtn.setEnabled(pending);
        rejectBtn.setEnabled(pending);
    }

    // =========================
    // Logging & Status Update
    // =========================

    private void logAction(Connection conn, String action, int bookingId) {
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO logs (user_id, action, timestamp) VALUES (?, ?, NOW())")) {
            ps.setInt(1, userId);
            ps.setString(2, action + " booking_id=" + bookingId);
            ps.executeUpdate();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void updateStatusBooking(String status) {
        if (selectedBookingId == -1) {
            JOptionPane.showMessageDialog(this, "Pilih booking yang ingin diubah statusnya!");
            return;
        }
        try (Connection conn = DB.Connection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                    "UPDATE bookings SET status=? WHERE booking_id=?");
            ps.setString(1, status);
            ps.setInt(2, selectedBookingId);
            ps.executeUpdate();

            logAction(conn, status.toUpperCase(), selectedBookingId);

            // Ambil user_id dari booking yang diubah statusnya
            int bookingUserId = -1;
            try (PreparedStatement ps2 = conn.prepareStatement(
                    "SELECT user_id FROM bookings WHERE booking_id=?")) {
                ps2.setInt(1, selectedBookingId);
                ResultSet rs = ps2.executeQuery();
                if (rs.next()) {
                    bookingUserId = rs.getInt("user_id");
                }
            }

            String notifMsg = "Reservasi #" + selectedBookingId + " "
                    + (status.equals("Approved") ? "di-approve" : "di-reject");

            // Simpan notifikasi ke database
            if (bookingUserId != -1) {
                DB.Notifications.addNotification(bookingUserId, notifMsg);
            }

            // Tampilkan juga di dashboard (jika user sedang login)
            dashboard.addNotification(notifMsg);

            JOptionPane.showMessageDialog(this, "Status booking berhasil diubah menjadi " + status + "!");
            loadBookings();
            clearForm();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void uploadPdfFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("PDF Documents", "pdf"));
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            // Validasi ekstensi PDF
            if (!file.getName().toLowerCase().endsWith(".pdf")) {
                JOptionPane.showMessageDialog(this, "File harus berformat PDF!", "Validasi",
                        JOptionPane.WARNING_MESSAGE);
                selectedPdfFile = null;
                return;
            }
            selectedPdfFile = file;
            JOptionPane.showMessageDialog(this, "File terpilih: " + selectedPdfFile.getName());
        }
    }

    // =========================
    // Inner Classes (Optional)
    // =========================

    public class Room {
        private int availableRooms;
        private int[] sesi = new int[4]; // 0 = belum dibooking, 1 = sudah dibooking

        public Room(int availableRooms) {
            this.availableRooms = availableRooms;
        }

        public void bookSesi(int sesiKe) {
            if (sesiKe < 0 || sesiKe >= 4)
                return;
            if (sesi[sesiKe] == 0) {
                sesi[sesiKe] = 1;
                if (isAllSesiBooked()) {
                    availableRooms--;
                    // Reset sesi jika ingin digunakan lagi
                    // Arrays.fill(sesi, 0);
                }
            }
        }

        private boolean isAllSesiBooked() {
            for (int s : sesi) {
                if (s == 0)
                    return false;
            }
            return true;
        }

        public int getAvailableRooms() {
            return availableRooms;
        }
    }

    public class RoomReservation {
        private Connection conn;

        public RoomReservation(Connection conn) {
            this.conn = conn;
        }

        public LocalDateTime getTanggalReserveRoom(int logId) throws SQLException {
            String sql = "SELECT timestamp FROM logs WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, logId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        Timestamp timestamp = rs.getTimestamp("timestamp");
                        if (timestamp != null) {
                            return timestamp.toLocalDateTime();
                        }
                    }
                }
            }
            return null;
        }
    }

    @Override
    public void refreshData() {
        loadBookings();
    }
}