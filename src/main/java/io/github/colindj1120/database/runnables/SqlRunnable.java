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
package io.github.colindj1120.database.runnables;

import java.sql.SQLException;

/**
 * Functional interface representing an operation that can be executed and may throw a SQLException. This interface is designed for scenarios where a piece of code needs to be executed and may
 * encounter a database access error, requiring the handling of SQLExceptions.
 *
 * <p>This interface extends the standard {@link java.lang.Runnable} interface and allows
 * implementations to handle SQLExceptions in the body of the {@code run} method.</p>
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * SqlRunnable sqlOperation = () -> {
 *     // Perform database-related operations
 *     executeUpdateStatement("DELETE FROM table_name");
 * };
 * }</pre>
 *
 * <p>Note: Implementations are expected to handle SQLExceptions appropriately within the
 * {@code run} method. This interface is suitable for scenarios where a {@code Runnable} is needed, and SQLExceptions need to be handled explicitly.</p>
 *
 * <p><strong>Functional Interface:</strong> This interface is annotated with
 * {@link FunctionalInterface}, indicating that it is intended to be used as a functional interface for lambda expressions and method references.</p>
 *
 * @author Colin Jokisch
 * @version 0.1.1
 */
@FunctionalInterface
public interface SqlRunnable {
    /**
     * Executes this operation and may throw a SQLException.
     *
     * @throws SQLException
     *         if a database access error occurs
     */
    void run() throws SQLException;
}


