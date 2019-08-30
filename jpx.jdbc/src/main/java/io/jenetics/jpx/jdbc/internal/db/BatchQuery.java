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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import io.jenetics.jpx.jdbc.internal.querily.Param;

/**
 * Represents batch insert query.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class BatchQuery extends AbstractQuery {

	public final class Select<T> {
		private final Collection<T> _values;
		private final  Function<T, List<Param>> _format;

		private Select(
			final Collection<T> values,
			final Function<T, List<Param>> format
		) {
			_values = requireNonNull(values);
			_format = requireNonNull(format);
		}

		/**
		 * Executes the select query.
		 *
		 * @param parser the row parser used for creating the result objects
		 * @return the query result
		 * @throws SQLException if the query execution fails
		 * @throws NullPointerException if the given row {@code parser} is
		 *         {@code null}
		 */
		public <B> List<B> as(final RowParser<List<B>> parser) throws SQLException {
			requireNonNull(parser);

			final Set<B> results = new HashSet<B>();

			if (!_values.isEmpty()) {
				final PreparedSQL preparedSQL = PreparedSQL
					.parse(_sql, _format.apply(head(_values)));

				try (PreparedStatement ps = preparedSQL.prepare(_conn)) {
					for (T value : _values) {
						final List<Param> params = _format.apply(value);
						preparedSQL.fill(ps, params);

						try (ResultSet rs = ps.executeQuery()) {
							final List<B> rows = parser.parse(Results.of(rs));
							results.addAll(rows);
						}
					}
				}
			}

			return new ArrayList<B>(results);
		}

	}

	/**
	 * Create a new batch query object with the given connection and SQL string.
	 *
	 * @param conn the DB connection used by this query object
	 * @param sql the SQL query string
	 * @throws NullPointerException if one of the arguments is {@code null}
	 */
	public BatchQuery(final Connection conn, final String sql) {
		super(conn, sql);
	}

	public <T> Select<T> select(
		final Collection<T> values,
		final Function<T, List<Param>> format
	)  {
		return new Select<T>(values, format);
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
	public <T> List<Stored<T>> insert(
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
	public <T> void execute(
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
	public <T> int update(
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
