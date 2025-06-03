package DB;

import java.sql.DriverManager;
import java.sql.SQLException;

public class Connection {
    public static java.sql.Connection getConnection() {
        java.sql.Connection connection = null;
        try {
            String url = "jdbc:mysql://localhost:3306/testdb";
            String user = "root";
            String password = "";
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return connection;
    }
}

// java.sql.Connection conn = DB.Connection.getConnection();
