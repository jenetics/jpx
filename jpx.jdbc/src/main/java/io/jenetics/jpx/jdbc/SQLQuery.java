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

import static java.util.Objects.requireNonNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a select SQL query.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
final class SQLQuery extends AbstractQuery {
	private final List<Param> _params = new ArrayList<>();

	/**
	 * Create a new query object with the given connection and SQL string.
	 *
	 * @param conn the DB connection used by this query object
	 * @param sql the SQL query string
	 * @throws NullPointerException if one of the arguments is {@code null}
	 */
	SQLQuery(final Connection conn, final String sql) {
		super(conn, sql);
	}

	/**
	 * Set the given query parameter.
	 *
	 * @param param the query parameter to set
	 * @return {@code this} object, for method chaining
	 */
	SQLQuery on(final Param param) {
		_params.add(param);
		return this;
	}

	/**
	 * Set the query parameter with the given {@code name} and {@code value}.
	 *
	 * @param name the parameter name
	 * @param value the parameter value
	 * @return {@code this} object, for method chaining
	 * @throws NullPointerException if the parameter {@code name} is {@code null}
	 */
	SQLQuery on(final String name, final Object value) {
		return on(Param.value(name, value));
	}

	/**
	 * Execute the update query.
	 *
	 * @return the number of affected rows
	 * @throws SQLException if the query execution fails
	 */
	int execute() throws SQLException {
		try (PreparedStatement ps = PreparedSQL.prepare(_sql, _params, _conn)) {
			return ps.executeUpdate();
		}
	}

	/**
	 * Executes the select query.
	 *
	 * @param parser the row parser used for creating the result objects
	 * @param <T> the result type
	 * @return the query result
	 * @throws SQLException if the query execution fails
	 * @throws NullPointerException if the given row {@code parser} is
	 *         {@code null}
	 */
	<T> T as(final RowParser<T> parser) throws SQLException {
		requireNonNull(parser);

		try (PreparedStatement ps = PreparedSQL.prepare(_sql, _params, _conn);
			ResultSet rs = ps.executeQuery())
		{
			return parser.parse(Results.of(rs));
		}
	}

}
