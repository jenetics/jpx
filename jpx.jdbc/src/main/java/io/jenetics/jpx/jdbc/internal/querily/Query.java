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

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.util.Collections.unmodifiableList;
import static java.util.Objects.requireNonNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public class Query {

	private static final Pattern PARAM_PATTERN = Pattern.compile("\\{(\\w+?)\\}");

	private final String _sql;
	private final List<String> _names;

	private Query(final String sql, final List<String> names) {
		_sql = requireNonNull(sql);
		_names = unmodifiableList(names);
	}

	public String sql() {
		return _sql;
	}

	public List<String> names() {
		return _names;
	}

	public PreparedQuery on(final Param... params) {
		return null;
	}


	/* *************************************************************************
	 * Execution methods
	 * ************************************************************************/

	/**
	 * Executes the SQL statement defined by {@code this} query object, which
	 * may be any kind of SQL statement.
	 *
	 * @see PreparedStatement#execute()
	 *
	 * @param conn the DB connection where {@code this} query is executed on
	 * @return {@code true} if the first result is a {@link java.sql.ResultSet}
	 *         object; {@code false} if the first result is an update count or
	 *         there is no result
	 * @throws SQLException if a database access error occurs
	 * @throws java.sql.SQLTimeoutException when the driver has determined that
	 *         the timeout value has been exceeded
	 * @throws NullPointerException if the given connection is {@code null}
	 */
	public boolean execute(final Connection conn) throws SQLException  {
		try (Statement stmt = conn.createStatement()) {
			return stmt.execute(_sql);
		}
	}

	/**
	 * Executes the SQL statement defined by {@code this} query object, which
	 * must be an SQL Data Manipulation Language (DML) statement, such as
	 * {@code INSERT}, {@code UPDATE} or {@code DELETE}; or an SQL statement
	 * that returns nothing, such as a DDL statement.
	 *
	 * @see PreparedStatement#executeUpdate()
	 *
	 * @param conn the DB connection where {@code this} query is executed on
	 * @return either (1) the row count for SQL Data Manipulation Language (DML)
	 *         statements or (2) 0 for SQL statements that return nothing
	 * @throws SQLException if a database access error occurs
	 * @throws java.sql.SQLTimeoutException when the driver has determined that
	 *         the timeout value has been exceeded
	 * @throws NullPointerException if the given connection is {@code null}
	 */
	public int executeUpdate(final Connection conn) throws SQLException {
		throw new UnsupportedOperationException();
	}

	/**
	 * Executes the SQL statement defined by {@code this} query object, which
	 * must be an {@code INSERT} statement.
	 *
	 * @param conn the DB connection where {@code this} query is executed on
	 * @return the key generated during the insertion
	 * @throws SQLException if a database access error occurs
	 * @throws java.sql.SQLTimeoutException when the driver has determined that
	 *         the timeout value has been exceeded
	 * @throws NullPointerException if the given connection is {@code null}
	 */
	public Optional<Long> executeInsert(final Connection conn)
		throws SQLException
	{
		try (PreparedStatement stmt = prepare(conn)) {
			stmt.executeUpdate();
			return readID(stmt);
		}
	}

	private PreparedStatement prepare(final Connection conn) throws SQLException {
		return conn.prepareStatement(_sql, RETURN_GENERATED_KEYS);
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

	public <T> T as(final ResultSetParser<T> parser, final Connection conn)
		throws SQLException
	{
		try (PreparedStatement ps = prepare(conn);
			 ResultSet rs = ps.executeQuery())
		{
			return parser.parse(rs);
		}
	}

	/* *************************************************************************
	 * Static factory methods.
	 * ************************************************************************/

	public static Query of(final String sql) {
		final List<String> names = new ArrayList<>();
		final StringBuffer parsedQuery = new StringBuffer();

		final Matcher matcher = PARAM_PATTERN.matcher(sql);
		while (matcher.find()) {
			final String name = matcher.group(1);
			names.add(name);

			matcher.appendReplacement(parsedQuery, "?");
		}
		matcher.appendTail(parsedQuery);

		return new Query(parsedQuery.toString(), names);
	}

}
