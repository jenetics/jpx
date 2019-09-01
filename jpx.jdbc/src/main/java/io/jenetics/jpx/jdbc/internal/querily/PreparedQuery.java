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
package io.jenetics.jpx.jdbc.internal.querily;

import static java.lang.String.format;
import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;
import static java.util.Objects.requireNonNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public class PreparedQuery {

	private final SimpleQuery _query;
	private final List<Param> _params;

	private PreparedQuery(final SimpleQuery query, final List<Param> params) {
		_query = requireNonNull(query);
		_params = unmodifiableList(params);
	}


	/* *************************************************************************
	 * Execution methods
	 * ************************************************************************/

	public boolean execute(final Connection conn) throws SQLException  {
		try (Statement stmt = prepare(conn)) {
			return stmt.execute(_query.sql());
		}
	}

	private PreparedStatement prepare(final Connection conn) throws SQLException {
		requireNonNull(conn);
		final PreparedStatement stmt =  conn.prepareStatement(
			_query.sql(),
			RETURN_GENERATED_KEYS
		);
		fill(stmt);
		return stmt;
	}

	private void fill(final PreparedStatement stmt) throws SQLException {
		requireNonNull(stmt);

		final Map<String, List<Param>> paramsMap = _params.stream()
			.collect(Collectors.groupingBy(Param::name));

		int index = 1;
		for (String name : _query.names()) {
			if (!paramsMap.containsKey(name)) {
				throw new IllegalArgumentException(format(
					"Param '%s' not found.", name
				));
			}

			final List<Object> values = paramsMap.get(name).stream()
				.flatMap(p -> p.of().stream())
				.collect(Collectors.toList());

			for (Object value : values) {
				stmt.setObject(index++, value);
			}
		}
	}

	public int executeUpdate(final Connection conn) throws SQLException {
		try (Statement stmt = prepare(conn)) {
			return stmt.executeUpdate(_query.sql());
		}
	}

	public Optional<Long> executeInsert(final Connection conn)
		throws SQLException
	{
		try (PreparedStatement stmt = prepare(conn)) {
			stmt.executeUpdate();
			return readID(stmt);
		}
	}

	private static Optional<Long> readID(final Statement stmt) throws SQLException {
		try (ResultSet keys = stmt.getGeneratedKeys()) {
			if (keys.next()) {
				return Optional.of(keys.getLong(1));
			} else {
				return Optional.empty();
			}
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
	public <T> T as(final RowParser<T> parser, final Connection conn) throws SQLException {
		requireNonNull(parser);

		try (PreparedStatement ps = prepare(conn);
			 ResultSet rs = ps.executeQuery())
		{
			return parser.parse(null/*Results.of(rs)*/);
		}
	}


	/* *************************************************************************
	 * Static factory methods.
	 * ************************************************************************/

	public static PreparedQuery of(final SimpleQuery query, final Param... params) {
		return new PreparedQuery(query, asList(params));
	}

}
