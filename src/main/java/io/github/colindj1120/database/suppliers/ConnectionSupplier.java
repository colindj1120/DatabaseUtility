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
package io.github.colindj1120.database.suppliers;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Functional interface representing a supplier of results that depend on a JDBC Connection. This interface is designed for scenarios where a result needs to be supplied based on a database
 * connection, and it may throw a SQLException.
 *
 * <p>This interface extends the standard {@link java.util.function.Supplier} interface
 * and allows implementations to handle SQLExceptions in the body of the {@code get} method.</p>
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * ConnectionSupplier<String> connectionSupplier = (connection) -> {
 *     // Retrieve data from the database and return a result
 *     return executeQueryAndReturnResult(connection, "SELECT column FROM table");
 * };
 * }</pre>
 *
 * <p>Note: Implementations are expected to handle SQLExceptions appropriately within the
 * {@code get} method. This interface is suitable for scenarios where a {@code Supplier<T>} is needed, and the result depends on a database connection.</p>
 *
 * <p><strong>Functional Interface:</strong> This interface is annotated with
 * {@link FunctionalInterface}, indicating that it is intended to be used as a functional interface for lambda expressions and method references.</p>
 *
 * @param <T>
 *         the type of result supplied by this supplier
 *
 * @author Colin Jokisch
 * @version 0.1.1
 */
@FunctionalInterface
public interface ConnectionSupplier<T> {
    /**
     * Supplies a result based on the provided JDBC Connection and may throw a SQLException.
     *
     * @param connection
     *         the JDBC Connection
     *
     * @return the result supplied by this supplier
     *
     * @throws SQLException
     *         if a database access error occurs
     */
    T get(Connection connection) throws SQLException;
}

