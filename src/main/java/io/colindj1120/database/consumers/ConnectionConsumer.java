package io.colindj1120.database.consumers;

import java.sql.Connection;
import java.sql.SQLException;

@FunctionalInterface
public interface ConnectionConsumer {
    void accept(Connection connection) throws SQLException;
}

