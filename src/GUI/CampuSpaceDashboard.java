package GUI;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.*;

public class CampuSpaceDashboard extends JFrame implements Notifiable {
    private final int userId;
    private final String role;
    private final String userName;
    private final JLabel timeLabel;
    private final JPanel contentPanel;
    private final DefaultListModel<String> notificationList = new DefaultListModel<>();

    public CampuSpaceDashboard(int userId, String role) {
        this.userId = userId;
        this.role = role;
        this.userName = getUserName(userId);
        notificationList.clear();
        for (String notif : DB.Notifications.getNotifications(userId)) {
            notificationList.addElement(notif);
        }

        setTitle("CampuSpace - Dashboard");
        setSize(1000, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        JPanel sidebar = new JPanel(null);
        sidebar.setBackground(new Color(0, 51, 102));
        sidebar.setBounds(0, 0, 200, 600);

        JLabel title = createLabel("CampuSpace", 20, true, Color.WHITE);
        title.setBounds(20, 20, 200, 30);
        sidebar.add(title);

        JButton dashBtn = createSidebarButton("\uD83C\uDFE0 Dashboard");
        dashBtn.setBounds(20, 80, 160, 30);
        sidebar.add(dashBtn);

        JButton reserveBtn = createSidebarButton("\uD83D\uDCC5 Reserve Room");
        reserveBtn.setBounds(20, 120, 160, 30);
        sidebar.add(reserveBtn);

        if ("admin".equals(role) || "superadmin".equals(role)) {
            JButton adminBtn = createSidebarButton("⚙️ Admin Panel");
            adminBtn.setBounds(20, 160, 160, 30);
            sidebar.add(adminBtn);
            adminBtn.addActionListener(e -> showAdminMenuPanel());
        }

        if ("superadmin".equals(role)) {
            JButton superAdminBtn = createSidebarButton("Super Admin Panel");
            superAdminBtn.setBounds(20, 200, 160, 30);
            sidebar.add(superAdminBtn);
            superAdminBtn.addActionListener(e -> new SuperAdminPanel());
        }

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBounds(200, 0, 800, 40);
        topBar.setBackground(Color.WHITE);

        timeLabel = createLabel("", 18, true, Color.BLACK);
        timeLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 20));
        timeLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        timeLabel.setPreferredSize(new Dimension(200, 40));
        updateTime();
        new Timer(1000, e -> updateTime()).start();
        topBar.add(timeLabel, BorderLayout.EAST);

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setFont(new Font("SansSerif", Font.PLAIN, 14));
        logoutBtn.setBackground(new Color(220, 53, 69));
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setFocusPainted(false);
        logoutBtn.addActionListener(e -> {
            dispose();
            SwingUtilities.invokeLater(CampuspaceLogin::new);
        });
        topBar.add(logoutBtn, BorderLayout.WEST);

        contentPanel = new JPanel(null);
        contentPanel.setBounds(200, 40, 800, 560);
        contentPanel.setBackground(Color.WHITE);

        dashBtn.addActionListener(e -> renderDashboard());
        reserveBtn.addActionListener(e -> showReserveRoomPanel());

        renderDashboard();

        add(sidebar);
        add(topBar);
        add(contentPanel);
        setVisible(true);
    }

    private void renderDashboard() {
        contentPanel.removeAll();

        JLabel welcome = createLabel(
                "Selamat Datang di Dashboard, " + role + " - " + userName,
                22, true, Color.BLACK);
        welcome.setBounds(20, 20, 600, 30);
        contentPanel.add(welcome);

        int availableRooms = getAvailableRoomCount();
        contentPanel.add(createCard("\uD83D\uDEAA Room Available Today", String.valueOf(availableRooms), 20, 80));

        int activeReservations = getActiveReservationCountForUser(userId);
        contentPanel.add(createCard("\uD83D\uDC64 Active Reservation", String.valueOf(activeReservations), 220, 80));

        JButton notifBtn = new JButton("  \uD83D\uDD14 Lihat Notifikasi");
        notifBtn.setFont(new Font("SansSerif", Font.BOLD, 16));
        notifBtn.setBackground(new Color(0, 51, 102));
        notifBtn.setForeground(Color.WHITE);
        notifBtn.setBounds(20, 200, 380, 50);
        notifBtn.setFocusPainted(false);
        notifBtn.addActionListener(e -> showNotificationPopup());

        contentPanel.add(notifBtn);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showNotificationPopup() {
        if (notificationList.isEmpty()) {
            JDialog dialog = new JDialog(this, "Notifikasi", true);
            dialog.setLayout(new BorderLayout());
            JLabel label = new JLabel("Tidak ada notifikasi baru.", SwingConstants.CENTER);
            label.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            dialog.add(label, BorderLayout.CENTER);
            dialog.setSize(300, 120);
            dialog.setLocationRelativeTo(this);
            new javax.swing.Timer(2000, e -> dialog.dispose()).start();
            dialog.setVisible(true);
        } else {
            JList<String> notifView = new JList<>(notificationList);
            JScrollPane scroll = new JScrollPane(notifView);
            scroll.setPreferredSize(new Dimension(300, 150));

            JDialog dialog = new JDialog(this, "Notifikasi", true);
            dialog.setLayout(new BorderLayout());
            dialog.add(scroll, BorderLayout.CENTER);
            dialog.setSize(350, 220);
            dialog.setLocationRelativeTo(this);

            new javax.swing.Timer(5000, e -> {
                dialog.dispose();
                notificationList.clear();
            }).start();
            dialog.setVisible(true);
        }
    }

    private void showReserveRoomPanel() {
        contentPanel.removeAll();
        ReserveRoomPanel reservePanel = new ReserveRoomPanel(userId, role, notificationList, this::renderDashboard, this);
        reservePanel.setBounds(0, 0, contentPanel.getWidth(), contentPanel.getHeight());
        reservePanel.setSize(contentPanel.getSize());
        reservePanel.setPreferredSize(contentPanel.getSize());
        contentPanel.add(reservePanel);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showAdminMenuPanel() {
        contentPanel.removeAll();
        AdminMenuPanel adminPanel = new AdminMenuPanel();
        adminPanel.setBounds(0, 0, contentPanel.getWidth(), contentPanel.getHeight());
        contentPanel.add(adminPanel);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    public void showBookingListPanel() {
        contentPanel.removeAll();
        BookingListPanel panel = new BookingListPanel();
        panel.setBounds(0, 0, contentPanel.getWidth(), contentPanel.getHeight());
        contentPanel.add(panel);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private JPanel createCard(String title, String value, int x, int y) {
        JPanel card = new JPanel(null);
        card.setBackground(new Color(0, 51, 102));
        card.setBounds(x, y, 180, 100);
        card.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));

        JLabel titleLabel = createLabel(title, 14, true, Color.WHITE);
        titleLabel.setBounds(15, 10, 150, 30);
        card.add(titleLabel);

        JLabel valueLabel = createLabel(value, 26, true, Color.WHITE);
        valueLabel.setBounds(15, 45, 150, 40);
        valueLabel.setHorizontalAlignment(SwingConstants.CENTER);
        card.add(valueLabel);

        return card;
    }

    private JButton createSidebarButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("SansSerif", Font.PLAIN, 14));
        btn.setForeground(Color.WHITE);
        btn.setBackground(new Color(0, 51, 102));
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        return btn;
    }

    private JLabel createLabel(String text, int size, boolean bold, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", bold ? Font.BOLD : Font.PLAIN, size));
        label.setForeground(color);
        return label;
    }

    private void updateTime() {
        timeLabel.setText(new SimpleDateFormat("HH : mm").format(new Date()));
    }

    private int getAvailableRoomCount() {
        int count = 0;
        try (java.sql.Connection conn = DB.Connection.getConnection();
             java.sql.PreparedStatement ps = conn.prepareStatement(
                     "SELECT COUNT(*) FROM rooms WHERE room_id NOT IN (" +
                             "SELECT room_id FROM bookings WHERE DATE(start_time) = CURDATE() AND status IN ('Pending', 'Approved')" +
                             ")")) {
            java.sql.ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return count;
    }

    private int getActiveReservationCountForUser(int userId) {
        int count = 0;
        try (java.sql.Connection conn = DB.Connection.getConnection();
             java.sql.PreparedStatement ps = conn.prepareStatement(
                     "SELECT COUNT(*) FROM bookings WHERE user_id=? AND status IN ('Pending', 'Approved')")) {
            ps.setInt(1, userId);
            java.sql.ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return count;
    }

    private String getUserName(int userId) {
        String name = "";
        try (java.sql.Connection conn = DB.Connection.getConnection();
             java.sql.PreparedStatement ps = conn.prepareStatement(
                     "SELECT username FROM user WHERE user_id=?")) {
            ps.setInt(1, userId);
            java.sql.ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                name = rs.getString("username");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return name;
    }

    @Override
    public void addNotification(String message) {
        notificationList.clear();
        notificationList.addElement(message);
    }
}
