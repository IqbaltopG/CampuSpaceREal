package DB;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
// import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Optional;

public class Users {
    // Mendapatkan user berdasarkan username dan password (untuk login)
    public static Optional<User> authenticate(String username, String password) {
        try (java.sql.Connection conn = DB.Connection.getConnection();
                PreparedStatement ps = conn.prepareStatement(
                        "SELECT user_id, username, role FROM user WHERE username=? AND password=?")) {
            ps.setString(1, username);
            ps.setString(2, hashPassword(password)); // hash password sebelum query
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

    // Mendapatkan user berdasarkan user_id
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

    // Menambahkan user baru
    public static boolean addUser(String username, String password, String role) {
        try (java.sql.Connection conn = DB.Connection.getConnection();
                PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO user (username, password, role) VALUES (?, ?, ?)")) {
            ps.setString(1, username);
            ps.setString(2, password); // hash jika perlu
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

    // Class User (inner class)
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