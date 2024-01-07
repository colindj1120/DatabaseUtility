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
package io.github.colindj1120.database.consumers;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Functional interface representing a consumer that accepts a JDBC Connection and performs operations that may throw a SQLException.
 *
 * <p>This interface is designed to be used with methods that involve working with database
 * connections and may throw SQLException. It extends the standard {@link java.util.function.Consumer} interface and allows implementations to handle SQLExceptions in the body of the {@code accept}
 * method.</p>
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * ConnectionConsumer connectionConsumer = (connection) -> {
 *     // Perform operations with the database connection
 *     // that may throw SQLException
 *     connection.createStatement().executeUpdate("INSERT INTO table_name VALUES (1, 'example')");
 * };
 * }</pre>
 *
 * <p>Note: Implementations are expected to handle SQLExceptions appropriately within the
 * {@code accept} method. It is recommended to use this interface in scenarios where a {@code Consumer<Connection>} is needed, and SQLExceptions need to be handled explicitly.</p>
 *
 * <p><strong>Functional Interface:</strong> This interface is annotated with
 * {@link FunctionalInterface}, indicating that it is intended to be used as a functional interface for lambda expressions and method references.</p>
 *
 * @author Colin Jokisch
 * @version 0.1.1
 */
@FunctionalInterface
public interface ConnectionConsumer {
    /**
     * Performs this operation on the given JDBC Connection.
     *
     * @param connection
     *         the JDBC Connection to be operated on
     *
     * @throws SQLException
     *         if a database access error occurs or this method is called on a closed connection
     */
    void accept(Connection connection) throws SQLException;
}


