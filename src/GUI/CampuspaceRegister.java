package GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.security.MessageDigest;

public class CampuspaceRegister extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;

    public CampuspaceRegister() {
        // Configure main window
        setTitle("Campuspace - Register");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(240, 240, 240));

        // Create header panel
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(240, 240, 240));
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        JLabel titleLabel = new JLabel("Register");
        titleLabel.setFont(new Font("Helvetica", Font.BOLD, 24));
        titleLabel.setForeground(new Color(51, 51, 51));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        headerPanel.add(titleLabel);

        add(headerPanel, BorderLayout.NORTH);

        // Create form panel
        JPanel formPanel = new JPanel();
        formPanel.setBackground(new Color(240, 240, 240));
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(BorderFactory.createEmptyBorder(0, 40, 0, 40));

        // Add username components
        JLabel usernameLabel = new JLabel("User Name");
        usernameLabel.setFont(new Font("Helvetica", Font.PLAIN, 11));
        usernameLabel.setForeground(new Color(102, 102, 102));
        usernameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(usernameLabel);

        usernameField = new JTextField();
        usernameField.setFont(new Font("Helvetica", Font.PLAIN, 11));
        usernameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        usernameField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        formPanel.add(usernameField);
        formPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Add password components
        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setFont(new Font("Helvetica", Font.PLAIN, 11));
        passwordLabel.setForeground(new Color(102, 102, 102));
        passwordLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(passwordLabel);

        passwordField = new JPasswordField();
        passwordField.setFont(new Font("Helvetica", Font.PLAIN, 11));
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        formPanel.add(passwordField);
        formPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        // Add register button
        JButton registerButton = new JButton("REGISTER");
        registerButton.setFont(new Font("Helvetica", Font.BOLD, 12));
        registerButton.setForeground(Color.WHITE);
        registerButton.setBackground(new Color(66, 133, 244));
        registerButton.setOpaque(true);
        registerButton.setBorderPainted(false);
        registerButton.setFocusPainted(false);
        registerButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        registerButton.setMaximumSize(new Dimension(150, 40));
        registerButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Add action listener for register button
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleRegister();
            }
        });

        formPanel.add(registerButton);
        add(formPanel, BorderLayout.CENTER);

        // Make window visible
        setVisible(true);
    }

    private void handleRegister() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.");
            return;
        }

        try (Connection connection = DB.Connection.getConnection()) {
            // Hash the password
            String hashedPassword = hashPassword(password);

            String query = "INSERT INTO user (username, password, role) VALUES (?, ?, ?)";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, username);
            ps.setString(2, hashedPassword);
            ps.setString(3, "user"); // atau "admin", "superadmin" jika ingin
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Registration successful!");
            dispose(); // Close the registration form
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "An error occurred while connecting to the database.");
        }
    }

    private String hashPassword(String password) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hash = md.digest(password.getBytes("UTF-8"));
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1)
                hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }
}