package jdbc;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class CustomConnector {
    public Connection getConnection(String url) throws SQLException, IOException {
        return DriverManager.getConnection(url);
    }

    public Connection getConnection(String url, String user, String password) throws SQLException, IOException {
        return DriverManager.getConnection(url, user, password);
    }
}
