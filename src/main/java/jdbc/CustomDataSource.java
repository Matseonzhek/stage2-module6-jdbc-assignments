package jdbc;

import lombok.Getter;
import lombok.Setter;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
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

    public static CustomDataSource getInstance() {
        Properties properties = new Properties();
        try {
            properties.load(CustomDataSource.class.getClassLoader().getResourceAsStream("app.properties"));

        } catch (IOException e) {
            e.printStackTrace();
        }
        if (instance == null) {
            instance = new CustomDataSource(properties.getProperty("postgres.driver"), properties.getProperty("postgres.url"),
                    properties.getProperty("postgres.password"), properties.getProperty("postgres.name"));
        } else {
            try {
                if (instance.getConnection().isClosed()) {
                    instance = new CustomDataSource(properties.getProperty("postgres.driver"), properties.getProperty("postgres.url"),
                            properties.getProperty("postgres.password"), properties.getProperty("postgres.name"));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return instance;
    }

    @Override
    public Connection getConnection() {
        return new CustomConnector().getConnection(url, name, password);
    }

    @Override
    public Connection getConnection(String username, String password) {
        return new CustomConnector().getConnection(url, name, this.password);
    }

    @Override
    public PrintWriter getLogWriter() {
        return null;
    }

    @Override
    public void setLogWriter(PrintWriter out) {

    }

    @Override
    public int getLoginTimeout() {
        return 0;
    }

    @Override
    public void setLoginTimeout(int seconds) {

    }

    @Override
    public Logger getParentLogger() {
        return null;
    }

    @Override
    public <T> T unwrap(Class<T> iface) {
        return null;
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) {
        return false;
    }
}
