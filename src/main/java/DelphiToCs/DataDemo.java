package DelphiToCs;

import java.sql.*;


public class DataDemo {
    private Connection connection;
    private Statement statement;

    public void launch() {
        try {
            Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
            connection = DriverManager.getConnection("jdbc:ucanaccess://DemoDB.mdb");
            statement = connection.createStatement();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ResultSet executeQuery(String query) throws SQLException {
        launch();
        boolean foundResult = statement.execute(query);
        if (foundResult) {
            ResultSet set = statement.getResultSet();

            if (set != null) return set;
        }
        closeConnection();
        return null;
    }

    public void executeUpdate(String query) throws SQLException {
        launch();
        statement.executeUpdate(query);
        closeConnection();
    }

    public void closeConnection() {
        try {
            connection.close();
        } catch (Exception e) {
        }
    }
}

