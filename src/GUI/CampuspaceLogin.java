package GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.security.MessageDigest;

public class CampuspaceLogin extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;

    public CampuspaceLogin() {
        // Configure main window
        setTitle("Campuspace");
        setSize(400, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(240, 240, 240));

        // Create header panel
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(240, 240, 240));
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(40, 0, 20, 0));

        // Add title
        JLabel titleLabel = new JLabel("Campuspace");
        titleLabel.setFont(new Font("Helvetica", Font.BOLD, 24));
        titleLabel.setForeground(new Color(51, 51, 51));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        headerPanel.add(titleLabel);

        add(headerPanel, BorderLayout.NORTH);

        // Create login panel
        JPanel loginPanel = new JPanel();
        loginPanel.setBackground(new Color(240, 240, 240));
        loginPanel.setLayout(new BoxLayout(loginPanel, BoxLayout.Y_AXIS));
        loginPanel.setBorder(BorderFactory.createEmptyBorder(0, 40, 0, 40));

        // Add login heading
        JLabel loginLabel = new JLabel("Login");
        loginLabel.setFont(new Font("Helvetica", Font.PLAIN, 16));
        loginLabel.setForeground(new Color(68, 68, 68));
        loginLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        loginPanel.add(loginLabel);

        // Add username components
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

        // Add password components
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

        // Add login button
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

        // Add hover effects
        loginButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                loginButton.setBackground(new Color(51, 103, 214));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                loginButton.setBackground(new Color(66, 133, 244));
            }
        });

        // Add action listener for login button
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLogin();
            }
        });

        loginPanel.add(loginButton);

        // Add register button
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

        // Add hover effects
        registerButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                registerButton.setBackground(new Color(42, 148, 73));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                registerButton.setBackground(new Color(52, 168, 83));
            }
        });

        // Add action listener for register button
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new CampuspaceRegister(); // Open the registration form
            }
        });

        loginPanel.add(Box.createRigidArea(new Dimension(0, 15))); // Add spacing
        loginPanel.add(registerButton);

        add(loginPanel, BorderLayout.CENTER);

        // Add footer
        JPanel footerPanel = new JPanel();
        footerPanel.setBackground(new Color(240, 240, 240));
        footerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JLabel footerLabel = new JLabel("© 2025 Campuspace. All rights reserved.");
        footerLabel.setFont(new Font("Helvetica", Font.PLAIN, 9));
        footerLabel.setForeground(new Color(153, 153, 153));
        footerPanel.add(footerLabel);

        add(footerPanel, BorderLayout.SOUTH);

        // Make window visible
        setVisible(true);
    }

    private void handleLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        try (Connection connection = DB.Connection.getConnection()) {
            // Hash the password
            String hashedPassword = hashPassword(password);

            // Update the table name to 'user'
            String query = "SELECT * FROM user WHERE username = ? AND password = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, username);
            statement.setString(2, hashedPassword);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                JOptionPane.showMessageDialog(this, "Login successful!");
                // Open dashboard and close login window
                SwingUtilities.invokeLater(() -> {
                    new CampuSpaceDashboard();
                });
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid username or password.");
            }
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

    public static void main(String[] args) {
        // Use SwingUtilities to ensure thread safety
        SwingUtilities.invokeLater(() -> new CampuspaceLogin());
    }
}