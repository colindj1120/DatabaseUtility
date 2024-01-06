package io.colindj1120.database.models;

public class NullParam {
    private final int sqlType;

    public NullParam(int sqlType) {
        this.sqlType = sqlType;
    }

    public int getSqlType() {
        return sqlType;
    }
}
