public class App {
    public static void main(String[] args) {
        // Connect to the database
        DB.Connection.getConnection();

        // Show login window
        javax.swing.SwingUtilities.invokeLater(() -> {
            try {
                new GUI.CampuspaceLogin();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
