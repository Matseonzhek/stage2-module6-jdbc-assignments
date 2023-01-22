package jdbc;

import lombok.Getter;
import lombok.Setter;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.logging.Logger;

@Getter
@Setter
public class CustomDataSource implements DataSource {
    private static volatile CustomDataSource instance;
    private final String driver;
    private final String url;
    private final String name;
    private final String password;
    private Connection connection;

    private CustomDataSource(String driver, String url, String password, String name) {
        this.driver = driver;
        this.url = url;
        this.name = name;
        this.password = password;
    }

    public static CustomDataSource getInstance() throws IOException, SQLException {
        Properties properties = new Properties();
        try (InputStream inputStream = Files.newInputStream(Paths.get("C:\\Users\\matse\\Documents\\projects\\stage2-module6-jdbc-assignments\\src\\main\\resources\\app.properties"))) {
            properties.load(inputStream);
        }
        if (instance == null) {
            instance = new CustomDataSource(properties.getProperty("postgres.driver"), properties.getProperty("postgres.url"),
                    properties.getProperty("postgres.password"), properties.getProperty("postgres.name"));
        } else if (instance.getConnection().isClosed()) {
            instance = new CustomDataSource(properties.getProperty("postgres.driver"), properties.getProperty("postgres.url"),
                    properties.getProperty("postgres.password"), properties.getProperty("postgres.name"));
        }

        return instance;
    }

    @Override
    public Connection getConnection() throws SQLException {
        try {
            return new CustomConnector().getConnection(getUrl(), getName(), getPassword());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        try {
            return new CustomConnector().getConnection(getUrl(), getName(), getPassword());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return null;
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {

    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return 0;
    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {

    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return null;
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return null;
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }
}
