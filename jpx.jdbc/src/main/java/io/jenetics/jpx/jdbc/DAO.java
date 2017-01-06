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
package io.jenetics.jpx.jdbc;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

/**
 * Abstract DAO class
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public abstract class DAO {

	protected final Connection _conn;

	/**
	 * Create a new DAO object with uses the given connection.
	 *
	 * @param conn the DB connection used for the DAO operations
	 */
	protected DAO(final Connection conn) {
		_conn = conn;
	}

	public <T> T dao(final Function<Connection, T> create) {
		return create.apply(_conn);
	}

	/**
	 * Create a new select query object.
	 *
	 * @param query the SQL query
	 * @return a new select query object
	 */
	public SQLQuery SQL(final String query) {
		return new SQLQuery(_conn, query);
	}

	/**
	 * Create a new batch insert query object
	 *
	 * @param query the insert SQL query
	 * @return a new batch insert query object
	 */
	public BatchQuery Batch(final String query) {
		return new BatchQuery(_conn, query);
	}

	static <T, K> List<Stored<T>> put(
		final List<T> values,
		final Function<T, K> key,
		final SQL.Function<List<T>, List<Stored<T>>> select,
		final SQL.Function<List<T>, List<Stored<T>>> insert,
		final SQL.Function<List<Stored<T>>, List<Stored<T>>> update
	)
		throws SQLException
	{
		final Map<K, Stored<T>> existing = select.apply(values).stream()
			.collect(toMap(
				value -> key.apply(value.value()),
				value -> value,
				(a, b) -> b));

		final Map<K, T> actual = values.stream()
			.collect(toMap(key, value -> value, (a, b) -> b));

		final Diff<K, Stored<T>, T> diff = Diff.of(existing, actual);

		final List<T> missing = diff.missing();

		final List<Stored<T>> updated = diff
			.updated((e, a) -> Objects.equals(e.value(), a))
			.entrySet().stream()
				.map(entry -> entry.getKey().map(m -> entry.getValue()))
				.collect(toList());

		final List<Stored<T>> unchanged = diff
			.unchanged((e, a) -> Objects.equals(e.value(), a));

		final List<Stored<T>> result = new ArrayList<>();
		result.addAll(insert.apply(missing));
		result.addAll(update.apply(updated));
		result.addAll(unchanged);

		return result;
	}

	/**
	 * Reads the auto increment id from the previously inserted record.
	 *
	 * @param stmt the statement used for inserting the record
	 * @return the DB id of the inserted record
	 * @throws SQLException if fetching the ID fails
	 */
	public static long id(final Statement stmt) throws SQLException {
		try (ResultSet keys = stmt.getGeneratedKeys()) {
			if (keys.next()) {
				return keys.getLong(1);
			} else {
				throw new SQLException("Can't fetch generation ID.");
			}
		}
	}

}
