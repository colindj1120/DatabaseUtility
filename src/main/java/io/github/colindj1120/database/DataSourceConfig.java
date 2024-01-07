/*
 * Copyright (C) 2024 Colin Jokisch
 * This file is part of DatabaseUtility (https://github.com/colindj1120/DatabaseUtility).
 *
 * DatabaseUtility is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * DatabaseUtility is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with DatabaseUtility.  If not, see <http://www.gnu.org/licenses/>.
 */
package io.github.colindj1120.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;

/**
 * Utility class for creating and configuring a DataSource using HikariCP. This class provides a method to create a new DataSource with the specified JDBC driver class name and connection string. The
 * DataSource is configured using HikariCP, a popular connection pooling library for JDBC.
 *
 * <p><strong>Usage:</strong></p>
 * To create a DataSource instance, use the static method {@code createDataSource}. Provide the JDBC driver class name and connection string as parameters. The method ensures that the input parameters
 * are valid and throws an {@link IllegalArgumentException} if either the JDBC driver or the connection string is null or empty. If an error occurs during DataSource creation, a
 * {@link RuntimeException} is thrown with a meaningful error message.
 *
 * <p><strong>Example:</strong></p>
 * <pre>{@code
 * DataSource dataSource = DataSourceConfig.createDataSource("com.mysql.cj.jdbc.Driver",
 *                                                           "jdbc:mysql://localhost:3306/database");
 * }</pre>
 *
 * <p><strong>Exception Handling:</strong></p>
 * The method {@code createDataSource} throws an {@link IllegalArgumentException} if the input parameters are invalid, ensuring that both the JDBC driver and connection string are provided.
 * Additionally, it throws a {@link RuntimeException} with a detailed error message if an exception occurs during DataSource creation. This runtime exception encapsulates the original exception,
 * providing insights into the cause of the error.
 *
 * <p><strong>Configuration:</strong></p>
 * The HikariCP configuration includes setting the JDBC URL and driver class name. Additional configuration options, such as username, password, and various connection pool settings, can be added to
 * the {@link com.zaxxer.hikari.HikariConfig} instance as needed.
 *
 * <p><strong>Thread Safety:</strong></p>
 * This class is thread-safe as it does not maintain any shared state between method calls. Each call to {@code createDataSource} is independent and creates a new HikariCP DataSource instance.
 *
 * @author Colin Jokisch
 * @version 0.1.1
 */
public class DataSourceConfig {
    /**
     * Creates a new data source with the specified JDBC driver and connection string.
     *
     * @param jdbcDriver
     *         the JDBC driver class name
     * @param connStr
     *         the JDBC connection string
     *
     * @return a configured DataSource
     *
     * @throws IllegalArgumentException
     *         if input parameters are invalid
     * @throws RuntimeException
     *         if there is an error in DataSource creation
     */
    public static DataSource createDataSource(String jdbcDriver, String connStr) {
        if (jdbcDriver == null || jdbcDriver.isEmpty() || connStr == null || connStr.isEmpty()) {
            throw new IllegalArgumentException("JDBC driver and connection string must be provided.");
        }

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(connStr);
        config.setDriverClassName(jdbcDriver);

        try {
            return new HikariDataSource(config);
        }
        catch (Exception e) {
            throw new RuntimeException("Error creating DataSource: " + e.getMessage(), e);
        }
    }
}

