package GUI;

import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;

public class AdminMenuPanel extends BasePanel {
    private final JTable bookingTable;
    private final JTable userTable;
    private final DefaultTableModel bookingModel;
    private final DefaultTableModel userModel;

    private static final Color HEADER_BG = new Color(0, 51, 102);
    private static final Color HEADER_FG = Color.WHITE;
    private static final Color ROW_ALT = new Color(245, 245, 245);
    private static final Font HEADER_FONT = new Font("SansSerif", Font.BOLD, 13);
    private static final Font CELL_FONT = new Font("SansSerif", Font.PLAIN, 12);

    public AdminMenuPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));
        setBackground(Color.WHITE);

        bookingModel = new DefaultTableModel(
                new String[] { "Booking ID", "User ID", "Room", "Tanggal Booking & Waktu", "PDF" }, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        bookingTable = new JTable(bookingModel);
        styleTable(bookingTable);

        userModel = new DefaultTableModel(
                new String[] { "ID", "Username", "Role" }, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        userTable = new JTable(userModel);
        styleTable(userTable);

        JPanel bookingPanel = wrapWithTitledScrollPane("📋  HISTORY", bookingTable);
        JPanel userPanel = wrapWithTitledScrollPane("👥  User List", userTable);

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, bookingPanel, userPanel);
        split.setResizeWeight(0.6);
        split.setBorder(null);

        add(split, BorderLayout.CENTER);

        JButton viewPdfBtn = new JButton("Lihat PDF");
        viewPdfBtn.setFont(new Font("SansSerif", Font.PLAIN, 13));
        viewPdfBtn.addActionListener(e -> openSelectedPdf());

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        btnPanel.setOpaque(false);
        btnPanel.add(viewPdfBtn);

        add(btnPanel, BorderLayout.SOUTH);

        loadBookingData();
        loadUserData();
    }

    private void openSelectedPdf() {
        int selectedRow = bookingTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih baris booking terlebih dahulu.", "Info",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        String pdfPath = (String) bookingModel.getValueAt(selectedRow, 4);
        if (pdfPath == null || pdfPath.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "PDF tidak tersedia untuk booking ini.", "Info",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        try {
            Desktop.getDesktop().open(new java.io.File(pdfPath));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Gagal membuka PDF: " + ex.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void styleTable(JTable table) {
        table.setFont(CELL_FONT);
        table.setRowHeight(22);
        table.setFillsViewportHeight(true);
        table.setSelectionBackground(new Color(0, 102, 204));
        table.setSelectionForeground(Color.WHITE);
        table.setShowGrid(false);

        JTableHeader header = table.getTableHeader();
        header.setBackground(HEADER_BG);
        header.setForeground(HEADER_FG);
        header.setFont(HEADER_FONT);

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable tbl, Object value,
                    boolean isSel, boolean hasF,
                    int row, int col) {
                Component c = super.getTableCellRendererComponent(tbl, value, isSel, hasF, row, col);
                if (!isSel) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : ROW_ALT);
                }
                setHorizontalAlignment(col == 0 ? CENTER : LEFT);
                return c;
            }
        });

        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
    }

    private JPanel wrapWithTitledScrollPane(String title, JTable table) {
        JLabel lbl = new JLabel(title);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 14));
        lbl.setBorder(new EmptyBorder(0, 0, 5, 0));

        JScrollPane sc = new JScrollPane(table);
        sc.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));

        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.add(lbl, BorderLayout.NORTH);
        panel.add(sc, BorderLayout.CENTER);
        return panel;
    }

    private void loadBookingData() {
        bookingModel.setRowCount(0);
        try (Connection conn = DB.Connection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(
                "SELECT b.booking_id, b.user_id, b.room_id, b.start_time, b.stop_time, b.status, b.purpose, b.pdf_path " +
                "FROM bookings b " +
                "JOIN rooms r ON b.room_id = r.room_id " +
                "JOIN user u ON b.user_id = u.user_id")) {
            while (rs.next()) {
                int bookingId = rs.getInt("booking_id");
                String bookingDate = getBookingDateFromLogs(conn, bookingId);
                String startTime = rs.getString("start_time");
                String stopTime = rs.getString("stop_time");
                String dateTime = (bookingDate.isEmpty() ? "" : bookingDate + " ") + startTime + " - " + stopTime;

                bookingModel.addRow(new Object[] {
                    bookingId,
                    rs.getInt("user_id"),
                    rs.getString("room_id"),
                    dateTime,
                    rs.getString("pdf_path")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private String getBookingDateFromLogs(Connection conn, int bookingId) {
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT DATE(timestamp) as tanggal FROM logs WHERE action LIKE ? ORDER BY timestamp ASC LIMIT 1")) {
            ps.setString(1, "CREATE booking_id=" + bookingId + "%");
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("tanggal");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "";
    }

    private void loadUserData() {
        userModel.setRowCount(0);
        try (Connection conn = DB.Connection.getConnection();
                Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery("SELECT user_id, username, role FROM user")) {
            while (rs.next()) {
                userModel.addRow(new Object[] {
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        rs.getString("role")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void refreshData() {
        loadBookingData();
        loadUserData();
    }
}