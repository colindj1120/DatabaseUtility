package io.colindj1120.database;

import io.colindj1120.database.consumers.ConnectionConsumer;
import io.colindj1120.database.models.NullParam;
import io.colindj1120.database.processors.QueryResultProcessor;
import io.colindj1120.database.suppliers.ConnectionSupplier;

import javax.sql.DataSource;
import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetProvider;
import java.math.BigDecimal;
import java.sql.*;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("SqlSourceToSinkFlow")
public class DatabaseUtility {
    private final DataSource dataSource;

    private static class DatabaseUtilityHolder {
        private static DatabaseUtility INSTANCE;

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

    private DatabaseUtility(String jdbcDriver, String connStr) {
        try {
            this.dataSource = DataSourceConfig.createDataSource(jdbcDriver, connStr);
        }
        catch (Exception e) {
            throw new RuntimeException("Error creating DataSource", e);
        }
    }

    public static DatabaseUtility getInstance(String jdbcDriver, String connStr) {
        return DatabaseUtilityHolder.getInstance(jdbcDriver, connStr);
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public <T> T executeQuery(String queryStmt, QueryResultProcessor<ResultSet, T> resultProcessor, Object... params) throws SQLException {
        try (Connection connection = getConnection()) {
            return executeQuery(connection, queryStmt, resultProcessor, params);
        }
    }

    public <T> T executeQuery(Connection connection, String queryStmt, QueryResultProcessor<ResultSet, T> resultProcessor, Object... params) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement(queryStmt)) {
            setPreparedStatementParameters(stmt, params);
            try (ResultSet rs = stmt.executeQuery()) {
                CachedRowSet crs = RowSetProvider.newFactory()
                                                 .createCachedRowSet();
                crs.populate(rs);
                return resultProcessor.apply(crs);
            }
        }
    }

    public int executeUpdate(String sqlStatement, Object... params) throws SQLException {
        try (Connection connection = getConnection()) {
            return executeUpdate(connection, sqlStatement, params);
        }
    }

    public int executeUpdate(Connection connection, String sqlStatement, Object... params) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement(sqlStatement)) {
            setPreparedStatementParameters(stmt, params);
            return stmt.executeUpdate();
        }
    }

    public <T> T executeUpdateReturnKeys(String sqlStatement, QueryResultProcessor<ResultSet, T> resultProcessor, Object... params) throws SQLException {
        try (Connection connection = getConnection()) {
            return executeUpdateReturnKeys(connection, sqlStatement, resultProcessor, params);
        }
    }

    public <T> T executeUpdateReturnKeys(Connection connection, String sqlStatement, QueryResultProcessor<ResultSet, T> resultProcessor, Object... params) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement(sqlStatement, Statement.RETURN_GENERATED_KEYS)) {
            setPreparedStatementParameters(stmt, params);
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                CachedRowSet crs = RowSetProvider.newFactory()
                                                 .createCachedRowSet();
                crs.populate(rs);
                return resultProcessor.apply(crs);
            }
        }
    }

    public void dbExecuteBatchUpdate(Connection connection, String queryStmt, List<Object[]> batchParameters) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement(queryStmt)) {
            for (Object[] params : batchParameters) {
                setPreparedStatementParameters(stmt, params);
                stmt.addBatch();
            }
            stmt.executeBatch();
        }
    }

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
                case NullParam nullParam -> stmt.setNull(i + 1, nullParam.getSqlType());
                case null, default -> stmt.setObject(i + 1, params[i]);
            }
        }
    }

    public void executeVoidTransaction(ConnectionConsumer connectionConsumer) throws SQLException {
        try (Connection conn = getConnection()) {
            try {
                conn.setAutoCommit(false);
                connectionConsumer.accept(conn);
                conn.commit();
            }
            catch (Exception e) {
                conn.rollback();
                if(e instanceof SQLException sqlException) {
                    throw sqlException;
                }
                throw new SQLException(e.getMessage(), e);
            }
            finally {
                conn.setAutoCommit(true);
            }
        }
    }

    public <T> T executeReturnTransaction(ConnectionSupplier<T> connectionSupplier) throws SQLException {
        try (Connection conn = getConnection()) {
            try {
                conn.setAutoCommit(false);
                T result = connectionSupplier.get(conn);
                conn.commit();
                return result;
            }
            catch (Exception e) {
                conn.rollback();
                if (e instanceof SQLException sqlException) {
                    throw sqlException;
                }
                throw new SQLException(e.getMessage(), e);
            }
            finally {
                conn.setAutoCommit(true);
            }
        }
    }
}
