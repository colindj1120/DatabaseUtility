package io.colindj1120.database.runnables;

import java.sql.SQLException;

@FunctionalInterface
public interface SqlRunnable {
    /**
     * Performs an action that can throw a SQLException.
     *
     * @throws SQLException if a database access error occurs.
     */
    void run() throws SQLException;
}

