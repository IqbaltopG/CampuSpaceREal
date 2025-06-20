package GUI;

import DB.Users;
import java.awt.*;
import java.util.Optional;
import javax.swing.*;

public class CampuspaceLogin extends JFrame {
    private final JTextField usernameField;
    private final JPasswordField passwordField;

    public CampuspaceLogin() {

        setTitle("Campuspace");
        setSize(400, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(240, 240, 240));

        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(240, 240, 240));
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(40, 0, 20, 0));

        JLabel titleLabel = new JLabel("Campuspace");
        titleLabel.setFont(new Font("Helvetica", Font.BOLD, 24));
        titleLabel.setForeground(new Color(51, 51, 51));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        headerPanel.add(titleLabel);

        add(headerPanel, BorderLayout.NORTH);

        JPanel loginPanel = new JPanel();
        loginPanel.setBackground(new Color(240, 240, 240));
        loginPanel.setLayout(new BoxLayout(loginPanel, BoxLayout.Y_AXIS));
        loginPanel.setBorder(BorderFactory.createEmptyBorder(0, 40, 0, 40));

        JLabel loginLabel = new JLabel("Login");
        loginLabel.setFont(new Font("Helvetica", Font.PLAIN, 16));
        loginLabel.setForeground(new Color(68, 68, 68));
        loginLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        loginPanel.add(loginLabel);

        JPanel usernamePanel = new JPanel();
        usernamePanel.setBackground(new Color(240, 240, 240));
        usernamePanel.setLayout(new BoxLayout(usernamePanel, BoxLayout.Y_AXIS));

        JLabel usernameLabel = new JLabel("User Name");
        usernameLabel.setFont(new Font("Helvetica", Font.PLAIN, 11));
        usernameLabel.setForeground(new Color(102, 102, 102));
        usernameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        usernamePanel.add(usernameLabel);

        usernameField = new JTextField();
        usernameField.setFont(new Font("Helvetica", Font.PLAIN, 11));
        usernameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        usernameField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        usernamePanel.add(usernameField);

        loginPanel.add(usernamePanel);
        loginPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        JPanel passwordPanel = new JPanel();
        passwordPanel.setBackground(new Color(240, 240, 240));
        passwordPanel.setLayout(new BoxLayout(passwordPanel, BoxLayout.Y_AXIS));

        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setFont(new Font("Helvetica", Font.PLAIN, 11));
        passwordLabel.setForeground(new Color(102, 102, 102));
        passwordLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        passwordPanel.add(passwordLabel);

        passwordField = new JPasswordField();
        passwordField.setFont(new Font("Helvetica", Font.PLAIN, 11));
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        passwordPanel.add(passwordField);

        loginPanel.add(passwordPanel);
        loginPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        JButton loginButton = new JButton("LOGIN");
        loginButton.setFont(new Font("Helvetica", Font.BOLD, 12));
        loginButton.setForeground(Color.WHITE);
        loginButton.setBackground(new Color(66, 133, 244));
        loginButton.setOpaque(true);
        loginButton.setBorderPainted(false);
        loginButton.setFocusPainted(false);
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.setMaximumSize(new Dimension(150, 40));
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        loginButton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                loginButton.setBackground(new Color(51, 103, 214));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                loginButton.setBackground(new Color(66, 133, 244));
            }
        });

        loginButton.addActionListener(e -> handleLogin());
        loginPanel.add(loginButton);

        JButton registerButton = new JButton("REGISTER");
        registerButton.setFont(new Font("Helvetica", Font.BOLD, 12));
        registerButton.setForeground(Color.WHITE);
        registerButton.setBackground(new Color(52, 168, 83));
        registerButton.setOpaque(true);
        registerButton.setBorderPainted(false);
        registerButton.setFocusPainted(false);
        registerButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        registerButton.setMaximumSize(new Dimension(150, 40));
        registerButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        registerButton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                registerButton.setBackground(new Color(42, 148, 73));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                registerButton.setBackground(new Color(52, 168, 83));
            }
        });

        registerButton.addActionListener(e -> new CampuspaceRegister());

        loginPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        loginPanel.add(registerButton);

        add(loginPanel, BorderLayout.CENTER);

        JPanel footerPanel = new JPanel();
        footerPanel.setBackground(new Color(240, 240, 240));
        footerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JLabel footerLabel = new JLabel("© 2025 Campuspace. All rights reserved.");
        footerLabel.setFont(new Font("Helvetica", Font.PLAIN, 9));
        footerLabel.setForeground(new Color(153, 153, 153));
        footerPanel.add(footerLabel);

        add(footerPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void handleLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        System.out.println("DEBUG: username=" + username + ", password=" + password);

        Optional<Users.User> userOpt = Users.authenticate(username, password);
        if (userOpt.isPresent()) {
            Users.User user = userOpt.get();
            JOptionPane.showMessageDialog(this,
                    "Login berhasil!\nSelamat datang, " + user.username + " (" + user.role + ")",
                    "Login Sukses", JOptionPane.INFORMATION_MESSAGE);
            new CampuSpaceDashboard(user.userId, user.role);
            this.dispose();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Username atau password salah.",
                    "Login Gagal", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(CampuspaceLogin::new);
    }
}
