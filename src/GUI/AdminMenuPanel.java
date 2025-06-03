package GUI;

import javax.swing.*;
import java.awt.*;

public class AdminMenuPanel extends JPanel {
    public AdminMenuPanel() {
        setLayout(null);
        setBackground(Color.WHITE);

        JLabel title = new JLabel("Admin Menu");
        title.setFont(new Font("SansSerif", Font.BOLD, 22));
        title.setBounds(30, 30, 300, 40);
        add(title);

        // Contoh tombol fitur admin
        JButton manageUsersBtn = new JButton("Kelola User");
        manageUsersBtn.setBounds(30, 100, 200, 40);
        add(manageUsersBtn);

        JButton manageBookingsBtn = new JButton("Kelola Booking");
        manageBookingsBtn.setBounds(30, 160, 200, 40);
        add(manageBookingsBtn);

        // Anda bisa menambahkan aksi pada tombol di sini
        // manageUsersBtn.addActionListener(e -> ...);
        // manageBookingsBtn.addActionListener(e -> ...);
    }
}