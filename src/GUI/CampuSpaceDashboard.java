package GUI;

import java.awt.*;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.*;

public class CampuSpaceDashboard extends JFrame {
    private final JLabel timeLabel;
    private final JPanel contentPanel;
    private final DefaultListModel<String> notificationList = new DefaultListModel<>();
    private File selectedPDF = null;

    public CampuSpaceDashboard() {
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

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBounds(200, 0, 800, 40);
        topBar.setBackground(Color.WHITE);

        timeLabel = createLabel("", 18, true, Color.BLACK);
        timeLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 20));
        timeLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        updateTime();
        new Timer(1000, evt -> updateTime()).start();
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

        dashBtn.addActionListener(evt -> renderDashboard());
        reserveBtn.addActionListener(evt -> renderReserveRoom());

        renderDashboard();

        add(sidebar);
        add(topBar);
        add(contentPanel);
        setVisible(true);
    }

    private void renderDashboard() {
        contentPanel.removeAll();

        JLabel welcome = createLabel("Selamat Datang USER", 22, true, Color.BLACK);
        welcome.setBounds(20, 20, 400, 30);
        contentPanel.add(welcome);

        contentPanel.add(createCard("\uD83D\uDEAA Room Available Today", "5", 20, 80));
        contentPanel.add(createCard("\uD83D\uDC64 Active Reservation", "5", 220, 80));

        JButton notifBtn = new JButton("  \uD83D\uDD14 Lihat Notifikasi");
        notifBtn.setFont(new Font("SansSerif", Font.BOLD, 16));
        notifBtn.setBackground(new Color(0, 51, 102));
        notifBtn.setForeground(Color.WHITE);
        notifBtn.setBounds(20, 200, 380, 50);
        notifBtn.setFocusPainted(false);
        notifBtn.addActionListener(evt -> showNotificationPopup());

        contentPanel.add(notifBtn);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showNotificationPopup() {
        if (notificationList.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tidak ada notifikasi baru.");
        } else {
            JList<String> notifView = new JList<>(notificationList);
            JScrollPane scroll = new JScrollPane(notifView);
            scroll.setPreferredSize(new Dimension(300, 150));
            JOptionPane.showMessageDialog(this, scroll, "Notifikasi", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void renderReserveRoom() {
        contentPanel.removeAll();

        JLabel title = createLabel("Form Peminjaman Ruangan", 20, true, Color.BLACK);
        title.setBounds(20, 20, 400, 30);
        contentPanel.add(title);

        int y = 70, spacing = 40;

        JTextField nameField = new JTextField();
        JTextField nimField = new JTextField();

        JSpinner dateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor editor = new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd");
        dateSpinner.setEditor(editor);

        JComboBox<String> sessionBox = new JComboBox<>(new String[] { "Sesi 1", "Sesi 2", "Sesi 3", "Sesi 4" });
        JComboBox<String> buildingBox = new JComboBox<>(new String[] {
                "Gedung A", "Gedung B", "Gedung C", "Gedung D", "Gedung E", "Gedung F", "Gedung G"
        });
        JComboBox<String> floorBox = new JComboBox<>(new String[] { "1", "2", "3" });
        JComboBox<String> roomBox = new JComboBox<>();
        JTextField purposeField = new JTextField();

        Runnable updateRooms = () -> {
            roomBox.removeAllItems();
            String gedung = (String) buildingBox.getSelectedItem();
            String lantai = (String) floorBox.getSelectedItem();

            if (gedung != null && lantai != null) {
                char kodeGedung = gedung.charAt(gedung.length() - 1);
                for (int i = 1; i <= 5; i++) {
                    String room = kodeGedung + lantai + String.format("%02d", i);
                    roomBox.addItem(room);
                }
            }
        };
        buildingBox.addActionListener(evt -> updateRooms.run());
        floorBox.addActionListener(evt -> updateRooms.run());
        updateRooms.run();

        contentPanel.add(createLabel("Nama Lengkap:", 14, false, Color.BLACK)).setBounds(20, y, 150, 25);
        nameField.setBounds(180, y, 250, 25);
        contentPanel.add(nameField);
        y += spacing;

        contentPanel.add(createLabel("NIM:", 14, false, Color.BLACK)).setBounds(20, y, 150, 25);
        nimField.setBounds(180, y, 250, 25);
        contentPanel.add(nimField);
        y += spacing;

        contentPanel.add(createLabel("Tanggal:", 14, false, Color.BLACK)).setBounds(20, y, 150, 25);
        dateSpinner.setBounds(180, y, 250, 25);
        contentPanel.add(dateSpinner);
        y += spacing;

        contentPanel.add(createLabel("Sesi:", 14, false, Color.BLACK)).setBounds(20, y, 150, 25);
        sessionBox.setBounds(180, y, 250, 25);
        contentPanel.add(sessionBox);
        y += spacing;

        contentPanel.add(createLabel("Gedung:", 14, false, Color.BLACK)).setBounds(20, y, 150, 25);
        buildingBox.setBounds(180, y, 250, 25);
        contentPanel.add(buildingBox);
        y += spacing;

        contentPanel.add(createLabel("Lantai:", 14, false, Color.BLACK)).setBounds(20, y, 150, 25);
        floorBox.setBounds(180, y, 250, 25);
        contentPanel.add(floorBox);
        y += spacing;

        contentPanel.add(createLabel("Ruangan:", 14, false, Color.BLACK)).setBounds(20, y, 150, 25);
        roomBox.setBounds(180, y, 250, 25);
        contentPanel.add(roomBox);
        y += spacing;

        contentPanel.add(createLabel("Keperluan:", 14, false, Color.BLACK)).setBounds(20, y, 150, 25);
        purposeField.setBounds(180, y, 250, 25);
        contentPanel.add(purposeField);
        y += spacing;

        JButton uploadBtn = new JButton("Upload PDF Surat Izin");
        uploadBtn.setBounds(180, y, 250, 30);
        uploadBtn.setBackground(new Color(0, 102, 204));
        uploadBtn.setForeground(Color.WHITE);
        uploadBtn.setFocusPainted(false);
        uploadBtn.addActionListener(evt -> handlePDFUpload());
        contentPanel.add(uploadBtn);
        y += spacing + 10;

        JButton submitBtn = new JButton("Kirim");
        submitBtn.setBounds(180, y, 100, 30);
        submitBtn.setBackground(new Color(0, 102, 204));
        submitBtn.setForeground(Color.WHITE);
        submitBtn.setFocusPainted(false);
        submitBtn.addActionListener(evt -> {
            String formattedDate = new SimpleDateFormat("yyyy-MM-dd").format((Date) dateSpinner.getValue());
            if (selectedPDF != null && !nameField.getText().isEmpty()) {
                notificationList.addElement("Peminjaman " + nameField.getText() + " pada " + formattedDate);
                JOptionPane.showMessageDialog(this, "Data berhasil dikirim!");
                selectedPDF = null;
                renderDashboard();
            } else {
                JOptionPane.showMessageDialog(this, "Harap lengkapi semua data dan upload file PDF.");
            }
        });
        contentPanel.add(submitBtn);

        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void handlePDFUpload() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int result = chooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            if (file.getName().toLowerCase().endsWith(".pdf")) {
                selectedPDF = file;
                JOptionPane.showMessageDialog(this, "File berhasil diupload: " + file.getName());
            } else {
                JOptionPane.showMessageDialog(this, "Harap upload file PDF.");
            }
        }
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
}
