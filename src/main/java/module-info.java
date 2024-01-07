module DatabaseUtility {
    requires com.zaxxer.hikari;
    requires java.sql;
    requires java.sql.rowset;

    exports io.github.colindj1120.database;
    opens io.github.colindj1120.database;

    exports io.github.colindj1120.database.consumers;
    opens io.github.colindj1120.database.consumers;

    exports io.github.colindj1120.database.models;
    opens io.github.colindj1120.database.models;

    exports io.github.colindj1120.database.processors;
    opens io.github.colindj1120.database.processors;

    exports io.github.colindj1120.database.runnables;
    opens io.github.colindj1120.database.runnables;

    exports io.github.colindj1120.database.suppliers;
    opens io.github.colindj1120.database.suppliers;
}