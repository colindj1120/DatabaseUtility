package io.github.colindj1120.database.processors;

import java.sql.SQLException;

/**
 * Functional interface representing a processor for querying a database with a given input, potentially resulting in a specific output. This interface extends the standard
 * {@link java.util.function.Function} interface and allows implementations to handle SQLExceptions in the body of the {@code apply} method.
 *
 * <p>This interface is intended to be used in scenarios where a database query is performed
 * with a specific input, and the result needs to be processed, possibly transforming it to another type or performing additional operations. Implementations are expected to handle SQLExceptions
 * appropriately within the {@code apply} method.</p>
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * QueryResultProcessor<ResultSet, Integer> queryProcessor = (rs) -> {
 *     if(rs.next()) {
 *         int result = processResultSet(rs);
 *         return result;
 *     }
 *     return null;
 * };
 * }</pre>
 *
 * <p>Note: Implementations should handle SQLExceptions as needed within the {@code apply}
 * method. This interface is suitable for scenarios where a {@code Function<T, R>} is required, and SQLExceptions need to be handled explicitly.</p>
 *
 * <p><strong>Functional Interface:</strong> This interface is annotated with
 * {@link FunctionalInterface}, indicating that it is intended to be used as a functional interface for lambda expressions and method references.</p>
 *
 * @param <T>
 *         the type of input to the processor
 * @param <R>
 *         the type of result produced by the processor
 *
 * @author Colin Jokisch
 * @version 0.1.1
 */
@FunctionalInterface
public interface QueryResultProcessor<T, R> {
    /**
     * Applies this function to the given input, potentially resulting in a specific output.
     *
     * @param t
     *         the input to the processor
     *
     * @return the result of processing the input
     *
     * @throws SQLException
     *         if a database access error occurs or this method is called on a closed connection
     */
    R apply(T t) throws SQLException;
}

