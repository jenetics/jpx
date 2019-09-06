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

import java.net.URI;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.jenetics.jpx.jdbc.internal.querily.Param.Value;

/**
 * A {@code Query} represents an executable piece of SQL text.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public class Query {

	private final String _sql;
	private final List<String> _names;

	Query(final String sql, final List<String> names) {
		_sql = requireNonNull(sql);
		_names = unmodifiableList(names);
	}

	/**
	 * Return the SQL string of {@code this} query class.
	 *
	 * @return the SQL string of {@code this} query class
	 */
	public String sql() {
		return _sql;
	}

	/**
	 * Return the parameter names of this query. The returned list may be empty.
	 *
	 * @return the parameter names of this query
	 */
	public List<String> names() {
		return _names;
	}

	/**
	 * Return a new query object with the given query parameter values.
	 *
	 * @param params the query parameters
	 * @return a new parameter query
	 */
	public Query on(final Param... params) {
		return params.length == 0
			? this
			: PreparedQuery.of(this, params);
	}

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
		try (PreparedStatement stmt = prepare(conn)) {
			return stmt.execute();
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
		try (PreparedStatement stmt = prepare(conn)) {
			return stmt.executeUpdate();
		}
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

	public <T> Long executeInsert(
		final T row,
		final SqlFunction3<? super T, String, Connection, Value> dctor,
		final Connection conn
	)
		throws SQLException
	{
		try (PreparedStatement stmt = prepare(conn)) {
			int index = 0;
			for (String name : names()) {
				final Value value = dctor.apply(row, name, conn);
				if (value != null) {
					stmt.setObject(++index, toSQLValue(value.value()));
				} else {
					throw new NoSuchElementException();
				}
			}

			stmt.executeUpdate();
			return readID(stmt).orElse(null);
		}
	}

	private static Object toSQLValue(final Object value) {
		Object result = value;

		while (result instanceof Optional) {
			result = ((Optional<?>)result).orElse(null);
		}

		if (result instanceof URI) {
			result = result.toString();
		} else if (result instanceof URL) {
			result = result.toString();
		}

		return result;
	}

	/**
	 * Inserts the given rows in one transaction and with the same prepared
	 * statement.
	 *
	 * @param rows the rows to insert
	 * @param dctor the deconstruction function, which splits a given row into
	 *        its components. This components can than be used setting the
	 *        parameter values of the query.
	 * @param conn the DB connection where {@code this} query is executed on
	 * @param <T> the row type
	 * @return the list of generated keys, might be empty
	 * @throws SQLException if a database access error occurs
	 * @throws java.sql.SQLTimeoutException when the driver has determined that
	 *         the timeout value has been exceeded
	 * @throws NullPointerException if one of the parameters is {@code null}
	 */
	public <T> List<Long> executeInserts(
		final Collection<T> rows,
		final SqlFunction3<? super T, String, Connection, Value> dctor,
		final Connection conn
	)
		throws SQLException
	{
		final List<Long> ids = new ArrayList<>();

		try (PreparedStatement stmt = prepare(conn)) {
			for (T row : rows) {
				int index = 0;
				for (String name : names()) {
					final Value value = dctor.apply(row, name, conn);
					if (value != null) {
						stmt.setObject(++index, toSQLValue(value.value()));
					} else {
						throw new NoSuchElementException();
					}
				}

				stmt.executeUpdate();
				readID(stmt).ifPresent(ids::add);
			}
		}

		return ids;
	}

	/**
	 * Executes {@code this} query and parses the result with the given
	 * result-set parser.
	 *
	 * @param parser the parser which converts the query result to the desired
	 *        type
	 * @param conn the DB connection where {@code this} query is executed on
	 * @param <T> the result type
	 * @return the query result, parsed to the desired type
	 * @throws SQLException if a database access error occurs
	 * @throws java.sql.SQLTimeoutException when the driver has determined that
	 *         the timeout value has been exceeded
	 * @throws NullPointerException if the given result parser or connection is
	 *         {@code null}
	 */
	public <T> T as(final ResultSetParser<T> parser, final Connection conn)
		throws SQLException
	{
		try (PreparedStatement ps = prepare(conn);
			 ResultSet rs = ps.executeQuery())
		{
			return parser.parse(rs);
		}
	}

	PreparedStatement prepare(final Connection conn) throws SQLException {
		return conn.prepareStatement(sql(), RETURN_GENERATED_KEYS);
	}

	static Optional<Long> readID(final Statement stmt)
		throws SQLException
	{
		try (ResultSet keys = stmt.getGeneratedKeys()) {
			if (keys.next()) {
				return Optional.of(keys.getLong(1));
			} else {
				return Optional.empty();
			}
		}
	}


	/* *************************************************************************
	 * Static factory methods.
	 * ************************************************************************/

	private static final Pattern PARAM_PATTERN = Pattern.compile("\\{(\\w+?)\\}");

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
