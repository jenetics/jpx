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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

/**
 * Represents batch insert query.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
final class BatchQuery extends AbstractQuery {

	/**
	 * Create a new batch query object with the given connection and SQL string.
	 *
	 * @param conn the DB connection used by this query object
	 * @param sql the SQL query string
	 * @throws NullPointerException if one of the arguments is {@code null}
	 */
	BatchQuery(final Connection conn, final String sql) {
		super(conn, sql);
	}

	/**
	 * Inserts the given {@code values} into the DB.
	 *
	 * @param values the values which will be inserted into the DB
	 * @param format creates the needed parameters for inserting one object
	 *        value
	 * @param <T> the value type
	 * @return the inserted objects
	 * @throws SQLException if the insertion fails
	 */
	<T> List<Stored<T>> insert(
		final Collection<T> values,
		final Function<T, List<Param>> format
	)
		throws SQLException
	{
		final List<Stored<T>> results = new ArrayList<>();

		if (!values.isEmpty()) {
			final PreparedSQL preparedSQL = PreparedSQL
				.parse(_sql, format.apply(head(values)));

			try (PreparedStatement ps = preparedSQL.prepare(_conn)) {
				for (T value : values) {
					final List<Param> params = format.apply(value);
					preparedSQL.fill(ps, params);

					ps.executeUpdate();
					results.add(Stored.of(DAO.readID(ps), value));
				}
			}
		}

		return results;
	}

	private static <T> T head(final Collection<T> values) {
		return values.iterator().next();
	}

	/**
	 * Executes the query with the given (batch) values.
	 *
	 * @param values the object value
	 * @param format creates the needed parameters for executing the query with
	 *        one value
	 * @param <T> the value type
	 * @throws SQLException if the execution fails
	 */
	<T> void execute(
		final Collection<T> values,
		final Function<T, List<Param>> format
	)
		throws SQLException
	{
		if (!values.isEmpty()) {
			final PreparedSQL preparedSQL = PreparedSQL
				.parse(_sql, format.apply(head(values)));

			try (PreparedStatement stmt = preparedSQL.prepare(_conn)) {
				for (T value : values) {
					final List<Param> params = format.apply(value);
					preparedSQL.fill(stmt, params);

					stmt.executeUpdate();
				}
			}
		}
	}

	/**
	 * Updates the given {@code values}.
	 *
	 * @param values the values which will be inserted into the DB
	 * @param format creates the needed parameters for inserting one object
	 *        value
	 * @param <T> the value type
	 * @return the number of affected rows
	 * @throws SQLException if the update fails
	 */
	<T> int update(
		final Collection<T> values,
		final Function<T, List<Param>> format
	)
		throws SQLException
	{
		int count = 0;
		if (!values.isEmpty()) {
			final PreparedSQL preparedSQL = PreparedSQL
				.parse(_sql, format.apply(head(values)));

			try (PreparedStatement stmt = preparedSQL.prepare(_conn)) {
				for (T value : values) {
					final List<Param> params = format.apply(value);
					preparedSQL.fill(stmt, params);

					count += stmt.executeUpdate();
				}
			}
		}

		return count;
	}

}
