package GUI;

import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;

public class AdminMenuPanel extends JPanel {
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
                new String[] { "Booking ID", "User ID", "Room", "Date & Time", "PDF" }, 0) {
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
                        "SELECT b.booking_id, b.user_id, b.room_id, b.start_time, b.stop_time, b.pdf_path, l.timestamp AS booking_date "
                                +
                                "FROM bookings b " +
                                "LEFT JOIN logs l ON b.booking_id = l.booking_id AND l.action = 'CREATE'")) {

            while (rs.next()) {
                bookingModel.addRow(new Object[] {
                        rs.getInt("booking_id"),
                        rs.getInt("user_id"),
                        rs.getString("room_id"),
                        (rs.getString("booking_date") != null ? rs.getString("booking_date") + " " : "")
                                + rs.getString("start_time") + " - " + rs.getString("stop_time"),
                        rs.getString("pdf_path")
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

    private void openSelectedPdf() {
        int row = bookingTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Pilih booking terlebih dahulu!");
            return;
        }
        String pdfPath = (String) bookingModel.getValueAt(row, 4);
        if (pdfPath == null || pdfPath.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tidak ada file PDF untuk booking ini.");
            return;
        }
        try {
            Desktop.getDesktop().open(new java.io.File(pdfPath));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Gagal membuka file PDF.");
        }
    }
}
