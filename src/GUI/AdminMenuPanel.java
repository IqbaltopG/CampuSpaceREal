package GUI;

import javax.swing.*;
import java.awt.*;

public class AdminMenuPanel extends JPanel {
    public AdminMenuPanel(CampuSpaceDashboard dashboard) {
        setLayout(null);
        setBackground(Color.WHITE);
        setBounds(0, 0, 800, 560);

        JLabel title = new JLabel("Admin Menu");
        title.setFont(new Font("SansSerif", Font.BOLD, 22));
        title.setBounds(30, 30, 300, 40);
        add(title);

        JButton listBookingBtn = new JButton("List Booking");
        listBookingBtn.setBounds(30, 100, 200, 40);
        add(listBookingBtn);

        listBookingBtn.addActionListener(e -> dashboard.showBookingListPanel());
    }
}