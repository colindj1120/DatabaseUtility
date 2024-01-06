package io.colindj1120.database.suppliers;

import java.sql.Connection;
import java.sql.SQLException;

@FunctionalInterface
public interface ConnectionSupplier<T> {
    T get(Connection connection) throws SQLException;
}
