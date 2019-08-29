/*
 * Java GPX Library (@__identifier__@).
 * Copyright (c) @__year__@ Franz Wilhelmstötter
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Author:
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmail.com)
 */
package io.jenetics.jpx.jdbc.internal.db;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import io.jenetics.jpx.jdbc.internal.anorm.SqlFunction;

/**
 * Abstract DAO class which implements the methods for doing easy SQL.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public abstract class DAO {

	private final Connection _conn;

	/**
	 * Create a new DAO object with uses the given connection.
	 *
	 * @param conn the DB connection used for the DAO operations
	 * @throws NullPointerException if the given connection is {@code null}
	 */
	protected DAO(final Connection conn) {
		_conn = requireNonNull(conn);
	}

	/**
	 * Create a new DAO object which the current connection.
	 *
	 * @param create the DAO creation function
	 * @param <T> the DAO type
	 * @return a new DAO object of type {@code T}
	 */
	protected <T extends DAO> T with(final Function<Connection, T> create) {
		return create.apply(_conn);
	}

	/**
	 * Create a new select query object.
	 *
	 * @param query the SQL query
	 * @return a new select query object
	 * @throws NullPointerException if the given {@code query} string is
	 *         {@code null}
	 */
	protected SQLQuery SQL(final String query) {
		return new SQLQuery(_conn, query);
	}

	/**
	 * Create a new batch insert query object
	 *
	 * @param query the insert SQL query
	 * @return a new batch insert query object
	 */
	protected BatchQuery Batch(final String query) {
		return new BatchQuery(_conn, query);
	}

	/**
	 * Helper method for insert or update the given values.
	 *
	 * @param values the values to insert or to update
	 * @param key key function used for determining object equality
	 * @param select select function for selecting existing objects
	 * @param inserter insert function for inserting missing objects
	 * @param updater update function for updating changed values
	 * @param <V> the value type
	 * @param <K> the key type
	 * @return the missing + updated + unchanged rows
	 * @throws SQLException if the DB operation fails
	 * @throws NullPointerException if one of the arguments is {@code null}
	 */
	protected static <V, K> List<Stored<V>> put(
		final Collection<V> values,
		final Function<V, K> key,
		final SqlFunction<Collection<V>, List<Stored<V>>> select,
		final Inserter<V> inserter,
		final Updater<V> updater
	)
		throws SQLException
	{
		requireNonNull(values);
		requireNonNull(key);
		requireNonNull(select);
		requireNonNull(inserter);
		requireNonNull(updater);

		final List<Stored<V>> result;

		if (!values.isEmpty()) {
			final Map<K, Stored<V>> existing = select.apply(values).stream()
				.collect(toMap(
					value -> key.apply(value.value()),
					value -> value,
					(a, b) -> b));

			final Map<K, V> actual = values.stream()
				.collect(toMap(key, value -> value, (a, b) -> b));

			final Diff<K, Stored<V>, V> diff = Diff.of(existing, actual);

			final List<V> missing = diff.missing();

			final List<Stored<V>> updated = diff
				.updated((e, a) -> Objects.equals(e.value(), a))
				.entrySet().stream()
				.map(entry -> entry.getKey().map(m -> entry.getValue()))
				.collect(toList());

			final List<Stored<V>> unchanged = diff
				.unchanged((e, a) -> Objects.equals(e.value(), a));

			result = new ArrayList<>();
			result.addAll(inserter.insert(missing));
			result.addAll(updater.update(updated));
			result.addAll(unchanged);
		} else {
			result = Collections.emptyList();
		}

		return result;
	}

	/**
	 * Writes the given values into the DB. Before the values are written, they
	 * are mapped to type {@code T} with the given {@code mapper}.
	 *
	 * @param values the list of values to write
	 * @param mapper the function used for converting the values into the actual
	 *        insertion values
	 * @param writer the method used for writing the converted values into the DB
	 * @param <A> the raw value type
	 * @param <B> the converted value type
	 * @return a map of the inserted values mapped to its DB id
	 * @throws SQLException if the DB write fails
	 * @throws NullPointerException if one of the arguments is {@code null}
	 */
	protected static <A, B> Map<B, Long> write(
		final Collection<A> values,
		final ListMapper<A, B> mapper,
		final SqlFunction<List<B>, List<Stored<B>>> writer
	)
		throws SQLException
	{
		final List<B> mapped = values.stream()
			.flatMap(v -> mapper.apply(v).stream())
			.collect(Collectors.toList());

		return writer.apply(mapped).stream()
			.collect(toMap(Stored::value, Stored::id, (a, b) -> b));
	}

	/**
	 * Writes the given values into the DB. Before the values are written, they
	 * are mapped to type {@code T} with the given {@code mapper}.
	 *
	 * @param values the list of values to write
	 * @param mapper the function used for converting the values into the actual
	 *        insertion values
	 * @param writer the method used for writing the converted values into the DB
	 * @param <A> the raw value type
	 * @param <B> the converted value type
	 * @return a map of the inserted values mapped to its DB id
	 * @throws SQLException if the DB write fails
	 * @throws NullPointerException if one of the arguments is {@code null}
	 */
	protected static <A, B> Map<B, Long> write(
		final Collection<A> values,
		final OptionMapper<A, B> mapper,
		final SqlFunction<List<B>, List<Stored<B>>> writer
	)
		throws SQLException
	{
		return write(values, mapper.toListMapper(), writer);
	}

	/**
	 * Reads the auto increment id from the previously inserted record.
	 *
	 * @param stmt the statement used for inserting the record
	 * @return the DB id of the inserted record
	 * @throws SQLException if fetching the ID fails
	 */
	protected static long readID(final Statement stmt) throws SQLException {
		try (ResultSet keys = stmt.getGeneratedKeys()) {
			if (keys.next()) {
				return keys.getLong(1);
			} else {
				throw new SQLException("Can't fetch generation ID.");
			}
		}
	}

}
