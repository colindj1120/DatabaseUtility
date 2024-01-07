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
package io.github.colindj1120.database.models;

/**
 * Represents a record for a nullable parameter in a database query. Instances of this record encapsulate the SQL type of the parameter, indicating the type of data that would be passed as a null
 * value in a PreparedStatement.
 *
 * <p>This record is a concise way to define and model a nullable parameter for database
 * queries, leveraging the features introduced in Java records. It is designed to be used in conjunction with {@link java.sql.PreparedStatement} when setting parameters that can be null.</p>
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * // Create a NullParam record for a VARCHAR parameter
 * NullParam varcharNullParam = new NullParam(Types.VARCHAR);
 * }</pre>
 *
 * <p><strong>Immutable:</strong> This record is implicitly immutable, as records
 * automatically generate final fields and a public constructor, providing a concise and immutable data carrier.</p>
 *
 * @param sqlType
 *         the SQL type of the nullable parameter
 *
 * @author Colin Jokisch
 * @version 0.1.1
 */
public record NullParam(int sqlType) {}

