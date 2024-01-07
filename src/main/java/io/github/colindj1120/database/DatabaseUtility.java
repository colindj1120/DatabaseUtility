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

import io.github.colindj1120.database.consumers.ConnectionConsumer;
import io.github.colindj1120.database.models.NullParam;
import io.github.colindj1120.database.processors.QueryResultProcessor;
import io.github.colindj1120.database.suppliers.ConnectionSupplier;

import javax.sql.DataSource;
import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetProvider;
import java.math.BigDecimal;
import java.sql.*;
import java.util.List;
import java.util.Objects;

/**
 * The {@code DatabaseUtility} class provides utility methods for executing common database operations using JDBC and HikariCP for connection pooling. It supports query execution, batch updates,
 * transactional operations, and handling various parameter types.
 *
 * <p><strong>Usage:</strong></p>
 * To use this utility, create an instance by calling the {@code getInstance} method, passing the JDBC driver class name and connection string. You can then use the provided methods to perform
 * database operations.
 *
 * <p><strong>Thread Safety:</strong></p>
 * The class is designed to be thread-safe for creating a singleton instance. The {@code getInstance} method ensures that only one instance is created, and it uses double-checked locking for efficient
 * thread safety.
 *
 * <p><strong>Exception Handling:</strong></p>
 * Database operations may throw {@code SQLException}. The utility handles transactions and automatically manages commit and rollback operations. In case of an error, it throws a {@code SQLException}
 * with appropriate details.
 *
 * <p><strong>Supported Operations:</strong></p>
 * The utility supports executing queries, updates, and batch updates. It also supports transactions, allowing the execution of operations within a transaction, automatically handling commit and
 * rollback based on success or failure.
 *
 * <p><strong>Parameterized Execution:</strong></p>
 * The utility supports parameterized execution with the {@code executeQuery}, {@code executeUpdate}, and {@code executeUpdateReturnKeys} methods. It handles various parameter types, such as
 * {@code Double}, {@code Integer}, {@code String}, {@code Date}, {@code Boolean}, {@code byte[]}, {@code Timestamp}, {@code Array}, {@code BigDecimal}, {@code Long}, {@code java.sql.Time},
 * {@code Clob}, {@code Blob}, and custom {@code NullParam}.
 *
 * <p><strong>Batch Updates:</strong></p>
 * The utility allows batch updates using the {@code dbExecuteBatchUpdate} method, where a list of parameter arrays is provided for efficient bulk updates.
 *
 * <p><strong>Transaction Execution:</strong></p>
 * The utility provides methods for executing operations within a transaction using the {@code executeVoidTransaction} and {@code executeReturnTransaction} methods. It automatically manages
 * transaction boundaries and handles commit/rollback based on the success or failure of the executed operations.
 *
 * @author Colin Jokisch
 * @version 0.1.1
 */
public class DatabaseUtility {
    private final DataSource dataSource;

    /**
     * The {@code DatabaseUtilityHolder} class serves as a holder for the singleton instance of the {@code DatabaseUtility}.
     * It follows the lazy initialization and double-checked locking idiom to ensure thread-safe creation of the singleton
     * instance upon the first request, preventing unnecessary synchronization after initialization.
     *
     * <p><strong>Thread Safety:</strong></p>
     * The class utilizes the double-checked locking pattern to ensure that the singleton instance is created
     * atomically and efficiently in a multi-threaded environment. The inner {@code DatabaseUtilityHolder} class
     * maintains a single static instance of the {@code DatabaseUtility} to be shared across the application.
     *
     * <p><strong>Initialization:</strong></p>
     * The private {@code getInstance} method is responsible for creating the singleton instance of the {@code DatabaseUtility}
     * class. It checks if the instance is null, and if so, it synchronizes on the class, checks again for null, and then
     * creates the instance using the provided JDBC driver and connection string.
     *
     * <p><strong>Usage:</strong></p>
     * To obtain the singleton instance of the {@code DatabaseUtility} class, call the static {@code getInstance} method
     * with the required JDBC driver class name and connection string.
     */
    private static class DatabaseUtilityHolder {
        private static DatabaseUtility INSTANCE;

        /**
         * Gets the singleton instance of the {@code DatabaseUtility} class. If the instance is not yet created, it
         * synchronizes on the class and checks again before initializing. Once created, subsequent calls return the
         * existing instance.
         *
         * @param jdbcDriver the JDBC driver class name
         * @param connStr    the JDBC connection string
         * @return the singleton instance of {@code DatabaseUtility}
         */
        private static DatabaseUtility getInstance(String jdbcDriver, String connStr) {
            if (Objects.isNull(INSTANCE)) {
                synchronized (DatabaseUtilityHolder.class) {
                    if (Objects.isNull(INSTANCE)) {
                        INSTANCE = new DatabaseUtility(jdbcDriver, connStr);
                    }
                }
            }
            return INSTANCE;
        }
    }

    /**
     * Private constructor for creating an instance of the {@code DatabaseUtility} class. It initializes the
     * underlying DataSource using HikariCP with the provided JDBC driver and connection string. This constructor
     * is intended for use within the singleton pattern to ensure a single instance of the utility class.
     *
     * @param jdbcDriver the JDBC driver class name
     * @param connStr    the JDBC connection string
     */
    private DatabaseUtility(String jdbcDriver, String connStr) {
        this.dataSource = DataSourceConfig.createDataSource(jdbcDriver, connStr);
    }

    /**
     * Gets the singleton instance of the {@code DatabaseUtility} class. The method follows the singleton pattern,
     * ensuring that only one instance is created and shared across the application. It utilizes the
     * {@code DatabaseUtilityHolder} inner class with double-checked locking to safely create the instance upon the
     * first request, preventing unnecessary synchronization after initialization.
     *
     * <p><strong>Usage:</strong></p>
     * To obtain an instance of the {@code DatabaseUtility} class, call this static method with the required JDBC
     * driver class name and connection string.
     *
     * @param jdbcDriver the JDBC driver class name
     * @param connStr    the JDBC connection string
     * @return the singleton instance of {@code DatabaseUtility}
     */
    public static DatabaseUtility getInstance(String jdbcDriver, String connStr) {
        return DatabaseUtilityHolder.getInstance(jdbcDriver, connStr);
    }


    /**
     * Retrieves a connection from the underlying DataSource. The DataSource is configured using HikariCP for connection
     * pooling. This method is useful for obtaining a connection to execute various database operations.
     *
     * @return a {@link java.sql.Connection} object obtained from the DataSource
     * @throws SQLException if a database access error occurs or the connection cannot be obtained
     */
    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    /**
     * Executes a SQL query statement with the provided parameters and processes the result using a
     * {@link QueryResultProcessor}. The method automatically manages the opening and closing of the connection,
     * ensuring proper resource cleanup.
     *
     * @param queryStmt         the SQL query statement to be executed
     * @param resultProcessor   a processor to handle the result set and return a result of type {@code T}
     * @param params            optional parameters to be used in the query
     * @param <T>               the type of the result returned by the {@code resultProcessor}
     * @return the result of type {@code T} obtained by processing the query result set
     * @throws SQLException if a database access error occurs or the query execution fails
     */
    public <T> T executeQuery(String queryStmt, QueryResultProcessor<ResultSet, T> resultProcessor, Object... params) throws SQLException {
        try (Connection connection = getConnection()) {
            return executeQuery(connection, queryStmt, resultProcessor, params);
        }
    }

    /**
     * Executes a SQL query statement with the provided parameters and processes the result using a
     * {@link QueryResultProcessor}. This method allows the use of a pre-existing {@code Connection}, providing
     * flexibility for transactional scenarios or when a connection is managed externally. The method automatically
     * manages the opening and closing of the prepared statement and result set, ensuring proper resource cleanup.
     *
     * @param connection        the pre-existing {@code Connection} to be used for query execution
     * @param queryStmt         the SQL query statement to be executed
     * @param resultProcessor   a processor to handle the result set and return a result of type {@code T}
     * @param params            optional parameters to be used in the query
     * @param <T>               the type of the result returned by the {@code resultProcessor}
     * @return the result of type {@code T} obtained by processing the query result set
     * @throws SQLException if a database access error occurs or the query execution fails
     */
    public <T> T executeQuery(Connection connection, String queryStmt, QueryResultProcessor<ResultSet, T> resultProcessor, Object... params) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement(queryStmt)) {
            setPreparedStatementParameters(stmt, params);
            try (ResultSet rs = stmt.executeQuery()) {
                CachedRowSet crs = RowSetProvider.newFactory().createCachedRowSet();
                crs.populate(rs);
                return resultProcessor.apply(crs);
            }
        }
    }

    /**
     * Executes a SQL update statement (INSERT, UPDATE, DELETE) with the provided parameters. The method
     * automatically manages the opening and closing of a connection, ensuring proper resource cleanup.
     *
     * @param sqlStatement the SQL update statement to be executed
     * @param params       optional parameters to be used in the update statement
     * @return the number of rows affected by the update
     * @throws SQLException if a database access error occurs or the update execution fails
     */
    public int executeUpdate(String sqlStatement, Object... params) throws SQLException {
        try (Connection connection = getConnection()) {
            return executeUpdate(connection, sqlStatement, params);
        }
    }

    /**
     * Executes a SQL update statement (INSERT, UPDATE, DELETE) with the provided parameters using a pre-existing
     * {@code Connection}. This method allows for transactional scenarios or when a connection is managed externally.
     * The method automatically manages the opening and closing of the prepared statement, ensuring proper resource cleanup.
     *
     * @param connection   the pre-existing {@code Connection} to be used for update execution
     * @param sqlStatement the SQL update statement to be executed
     * @param params       optional parameters to be used in the update statement
     * @return the number of rows affected by the update
     * @throws SQLException if a database access error occurs or the update execution fails
     */
    public int executeUpdate(Connection connection, String sqlStatement, Object... params) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement(sqlStatement)) {
            setPreparedStatementParameters(stmt, params);
            return stmt.executeUpdate();
        }
    }

    /**
     * Executes a SQL update statement (INSERT, UPDATE, DELETE) and retrieves generated keys using the provided parameters.
     * The method automatically manages the opening and closing of a connection, ensuring proper resource cleanup.
     *
     * @param <T>              the type of result to be processed from the generated keys
     * @param sqlStatement     the SQL update statement to be executed
     * @param resultProcessor  a processor to handle the result set containing generated keys and return a result of type {@code T}
     * @param params           optional parameters to be used in the update statement
     * @return the result of type {@code T} obtained by processing the generated keys
     * @throws SQLException if a database access error occurs or the update execution fails
     */
    public <T> T executeUpdateReturnKeys(String sqlStatement, QueryResultProcessor<ResultSet, T> resultProcessor, Object... params) throws SQLException {
        try (Connection connection = getConnection()) {
            return executeUpdateReturnKeys(connection, sqlStatement, resultProcessor, params);
        }
    }

    /**
     * Executes a SQL update statement (INSERT, UPDATE, DELETE) and retrieves generated keys using a pre-existing
     * {@code Connection}. This method allows for transactional scenarios or when a connection is managed externally.
     * The method automatically manages the opening and closing of the prepared statement and result set,
     * ensuring proper resource cleanup.
     *
     * @param <T>              the type of result to be processed from the generated keys
     * @param connection       the pre-existing {@code Connection} to be used for update execution
     * @param sqlStatement     the SQL update statement to be executed
     * @param resultProcessor  a processor to handle the result set containing generated keys and return a result of type {@code T}
     * @param params           optional parameters to be used in the update statement
     * @return the result of type {@code T} obtained by processing the generated keys
     * @throws SQLException if a database access error occurs or the update execution fails
     */
    public <T> T executeUpdateReturnKeys(Connection connection, String sqlStatement, QueryResultProcessor<ResultSet, T> resultProcessor, Object... params) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement(sqlStatement, Statement.RETURN_GENERATED_KEYS)) {
            setPreparedStatementParameters(stmt, params);
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                CachedRowSet crs = RowSetProvider.newFactory().createCachedRowSet();
                crs.populate(rs);
                return resultProcessor.apply(crs);
            }
        }
    }

    /**
     * Executes a batch update with the provided parameters. The method uses a pre-existing {@code Connection}
     * for the batch update, allowing for transactional scenarios or when a connection is managed externally.
     * The method automatically manages the opening and closing of the prepared statement, ensuring proper resource cleanup.
     *
     * @param connection       the pre-existing {@code Connection} to be used for batch update
     * @param queryStmt        the SQL batch update statement to be executed
     * @param batchParameters  a list of parameter arrays representing the batch updates
     * @throws SQLException if a database access error occurs or the batch update execution fails
     */
    public void dbExecuteBatchUpdate(Connection connection, String queryStmt, List<Object[]> batchParameters) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement(queryStmt)) {
            for (Object[] params : batchParameters) {
                setPreparedStatementParameters(stmt, params);
                stmt.addBatch();
            }
            stmt.executeBatch();
        }
    }

    /**
     * Sets the parameters in a {@code PreparedStatement} based on the provided array of parameters. The method
     * supports various parameter types such as {@code Double}, {@code Integer}, {@code String}, {@code Date},
     * {@code Boolean}, {@code byte[]}, {@code Timestamp}, {@code Array}, {@code BigDecimal}, {@code Long},
     * {@code java.sql.Time}, {@code Clob}, {@code Blob}, and custom {@code NullParam}.
     *
     * @param stmt   the {@code PreparedStatement} to set parameters on
     * @param params an array of parameters to be set in the prepared statement
     * @throws SQLException if a database access error occurs or the parameter setting fails
     */
    private void setPreparedStatementParameters(PreparedStatement stmt, Object[] params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            Object param = params[i];
            switch (param) {
                case Double p -> stmt.setDouble(i + 1, p);
                case Integer p -> stmt.setInt(i + 1, p);
                case Float p -> stmt.setFloat(i + 1, p);
                case String p -> stmt.setString(i + 1, p);
                case Date p -> stmt.setDate(i + 1, p);
                case Boolean p -> stmt.setBoolean(i + 1, p);
                case byte[] p -> stmt.setBytes(i + 1, p);
                case Timestamp p -> stmt.setTimestamp(i + 1, p);
                case Array p -> stmt.setArray(i + 1, p);
                case BigDecimal p -> stmt.setBigDecimal(i + 1, p);
                case Long p -> stmt.setLong(i + 1, p);
                case java.sql.Time p -> stmt.setTime(i + 1, p);
                case Clob p -> stmt.setClob(i + 1, p);
                case Blob p -> stmt.setBlob(i + 1, p);
                case NullParam nullParam -> stmt.setNull(i + 1, nullParam.sqlType());
                case null, default -> stmt.setObject(i + 1, params[i]);
            }
        }
    }


    /**
     * Executes a void transaction by providing a {@code ConnectionConsumer}. The method automatically manages the
     * opening and closing of a connection, sets auto-commit to false, and handles the transaction logic. The provided
     * {@code ConnectionConsumer} is responsible for accepting the connection and performing transactional operations.
     * If an exception occurs during the transaction, it is rolled back, and the connection is reset to auto-commit mode.
     *
     * @param connectionConsumer the functional interface representing a transactional operation that accepts a {@code Connection}
     * @throws SQLException if a database access error occurs or the transaction execution fails
     */
    public void executeVoidTransaction(ConnectionConsumer connectionConsumer) throws SQLException {
        try (Connection conn = getConnection()) {
            try {
                conn.setAutoCommit(false);
                connectionConsumer.accept(conn);
                conn.commit();
            } catch (Exception e) {
                conn.rollback();
                if (e instanceof SQLException sqlException) {
                    throw sqlException;
                }
                throw new SQLException(e.getMessage(), e);
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    /**
     * Executes a transaction and returns a result by providing a {@code ConnectionSupplier}. The method automatically
     * manages the opening and closing of a connection, sets auto-commit to false, and handles the transaction logic.
     * The provided {@code ConnectionSupplier} is responsible for obtaining a result from the connection within the transaction.
     * If an exception occurs during the transaction, it is rolled back, and the connection is reset to auto-commit mode.
     *
     * @param <T>                the type of result to be obtained from the transaction
     * @param connectionSupplier the functional interface representing a transactional operation that supplies a result of type {@code T}
     * @return the result of type {@code T} obtained from the transaction
     * @throws SQLException if a database access error occurs or the transaction execution fails
     */
    public <T> T executeReturnTransaction(ConnectionSupplier<T> connectionSupplier) throws SQLException {
        try (Connection conn = getConnection()) {
            try {
                conn.setAutoCommit(false);
                T result = connectionSupplier.get(conn);
                conn.commit();
                return result;
            } catch (Exception e) {
                conn.rollback();
                if (e instanceof SQLException sqlException) {
                    throw sqlException;
                }
                throw new SQLException(e.getMessage(), e);
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

}
