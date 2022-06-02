package org.gepron1x.test;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.PreparedStatement;
import java.text.MessageFormat;

public final class ConnectionPoolCreation {

    private final String url;
    private final String username;
    private final String password;
    private final String database;
    private final boolean useSSL;

    public ConnectionPoolCreation(String url, String username, String password, String database, boolean useSSL) {

        this.url = url;
        this.username = username;
        this.password = password;
        this.database = database;
        this.useSSL = useSSL;
    }


    private void setupPooling(HikariConfig config) {
        config.setPoolName("YourPlugin-Pool");
        config.setMaximumPoolSize(6);
        config.setMinimumIdle(10);
        config.setMaxLifetime(1800000);
        config.setConnectionTimeout(5000);

    }

    private void setupConnection(HikariConfig config) {
        String jdbcUrl = MessageFormat.format("jdbc:mysql://{0}/{1}?useSSL={2}",
                this.url,
                this.database,
                this.useSSL
        );
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(this.username);
        config.setPassword(this.password);
    }


    public ConnectionPool create() {
        HikariConfig hikariConfig = new HikariConfig();
        setupPooling(hikariConfig);
        setupConnection(hikariConfig);
        return new ConnectionPool(new HikariDataSource(hikariConfig));

    }

    static void test() {

        ConnectionPool pool = new ConnectionPoolCreation(
                "127.0.0.1:3306",
                "gepron1x",
                "1234567",
                "test",
                false).create();
        pool.useConnection(connection -> {
            PreparedStatement ps = connection.prepareStatement("CREATE TABLE IF NOT EXISTS test (id INTEGER, name VARCHAR(65)");
            ps.executeUpdate();
        });
    }




}
