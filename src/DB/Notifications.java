package DB;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class Notifications {
    public static void addNotification(int userId, String message) {
        try (java.sql.Connection conn = DB.Connection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "INSERT INTO notifications (user_id, message) VALUES (?, ?)")) {
            ps.setInt(1, userId);
            ps.setString(2, message);
            ps.executeUpdate();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static List<String> getNotifications(int userId) {
        List<String> list = new ArrayList<>();
        try (java.sql.Connection conn = DB.Connection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT message FROM notifications WHERE user_id=? ORDER BY created_at DESC")) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(rs.getString("message"));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return list;
    }
}