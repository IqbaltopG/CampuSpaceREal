package DB;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Optional;

public class Users {
    // Authenticate user by username and password
    public static Optional<User> authenticate(String username, String password) {
        try (java.sql.Connection conn = DB.Connection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT user_id, username, role FROM user WHERE username=? AND password=?")) {
            ps.setString(1, username);
            ps.setString(2, hashPassword(password));
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                User user = new User(
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        rs.getString("role"));
                return Optional.of(user);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return Optional.empty();
    }

    // Get user by user_id
    public static Optional<User> getUserById(int userId) {
        try (java.sql.Connection conn = DB.Connection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT user_id, username, role FROM user WHERE user_id=?")) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                User user = new User(
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        rs.getString("role"));
                return Optional.of(user);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return Optional.empty();
    }

    // Add new user
    public static boolean addUser(String username, String password, String role) {
        try (java.sql.Connection conn = DB.Connection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "INSERT INTO user (username, password, role) VALUES (?, ?, ?)")) {
            ps.setString(1, username);
            ps.setString(2, hashPassword(password));
            ps.setString(3, role);
            return ps.executeUpdate() > 0;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    private static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    // User inner class
    public static class User {
        public final int userId;
        public final String username;
        public final String role;

        public User(int userId, String username, String role) {
            this.userId = userId;
            this.username = username;
            this.role = role;
        }
    }
}