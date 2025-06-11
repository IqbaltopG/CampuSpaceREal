public class App {
    public static void main(String[] args) {
        // connect to the database
        DB.Connection.getConnection();

        // login window
        javax.swing.SwingUtilities.invokeLater(() -> {
            try {
                new GUI.CampuspaceLogin();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
