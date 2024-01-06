package io.colindj1120.database.processors;

import java.sql.SQLException;

/**
 * Represents a function that accepts one argument and produces a result, and can throw a SQLException. This is a functional interface whose functional method
 * is {@link #apply(Object)}.
 *
 * @param <T> The type of the input to the function.
 * @param <R> The type of the result of the function.
 */
@FunctionalInterface
public interface QueryResultProcessor<T, R> {

    /**
     * Applies this function to the given argument.
     *
     * @param t The function argument.
     *
     * @return The function result.
     *
     * @throws SQLException if a SQL error occurs during the operation.
     */
    R apply(T t) throws SQLException;
}

