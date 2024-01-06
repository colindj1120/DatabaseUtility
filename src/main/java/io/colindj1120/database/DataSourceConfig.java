package io.colindj1120.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;

public class DataSourceConfig {
    /**
     * Creates a new data source.
     *
     * @return a configured DataSource
     */
    public static DataSource createDataSource(String jdbc_driver, String connStr) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(connStr);
        config.setDriverClassName(jdbc_driver);
        return new HikariDataSource(config);
    }
}
