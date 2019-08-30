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

import static java.lang.String.format;
import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.util.Objects.requireNonNull;

import java.net.URI;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.Year;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import io.jenetics.jpx.DGPSStation;
import io.jenetics.jpx.Degrees;
import io.jenetics.jpx.Fix;
import io.jenetics.jpx.Latitude;
import io.jenetics.jpx.Length;
import io.jenetics.jpx.Longitude;
import io.jenetics.jpx.Speed;
import io.jenetics.jpx.UInt;
import io.jenetics.jpx.jdbc.internal.querily.Param;

/**
 * Represents a SQL query for usage with a {@link PreparedStatement}.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class PreparedSQL {
	private static final Pattern PARAM_PATTERN = Pattern.compile("\\{(\\w+?)\\}");

	private final String _query;
	private final List<String> _names;

	private PreparedSQL(
		final String query,
		final List<String> names
	) {
		_query = requireNonNull(query);
		_names = requireNonNull(names);
	}

	/**
	 * Create a new {@link PreparedStatement} from the given connection.
	 *
	 * @param conn the DB connection
	 * @return a new prepared statement
	 * @throws SQLException if the preparing fails
	 * @throws NullPointerException if the connection is {@code null}
	 */
	public PreparedStatement prepare(final Connection conn)
		throws SQLException
	{
		requireNonNull(conn);
		return conn.prepareStatement(_query, RETURN_GENERATED_KEYS);
	}

	/**
	 * Fills the given prepared statement with the parameter values.
	 *
	 * @param stmt the prepared statement
	 * @throws SQLException if the statement preparation fails
	 * @throws NullPointerException if one of the arguments is {@code null}
	 */
	public void fill(final PreparedStatement stmt, final List<Param> params)
		throws SQLException
	{
		requireNonNull(stmt);
		requireNonNull(params);

		final Map<String, List<Param>> paramsMap = params.stream()
			.collect(Collectors.groupingBy(Param::name));

		int index = 1;
		for (String name : _names) {
			if (!paramsMap.containsKey(name)) {
				throw new IllegalArgumentException(format(
					"Param '%s' not found.", name
				));
			}

			final List<Object> values = paramsMap.get(name).stream()
				.flatMap(p -> p.of().stream())
				.map(PreparedSQL::toSQLValue)
				.collect(Collectors.toList());

			for (Object value : values) {
				stmt.setObject(index++, value);
			}
		}
	}

	private static Object toSQLValue(final Object value) {
		Object result = value;
		while (result instanceof Optional<?>) {
			result = ((Optional<?>)result).orElse(null);
		}
		if (result instanceof URI) {
			result = result.toString();
		} else if (result instanceof URL) {
			result = result.toString();
		} else if (result instanceof Year) {
			result = ((Year)result).getValue();
		} else if (result instanceof ZonedDateTime) {
			result = Timestamp.from(((ZonedDateTime)result).toInstant());
		} else if (result instanceof Latitude) {
			result = ((Latitude)result).doubleValue();
		} else if (result instanceof Longitude) {
			result = ((Longitude)result).doubleValue();
		} else if (result instanceof Length) {
			result = ((Length)result).doubleValue();
		} else if (result instanceof Speed) {
			result = ((Speed)result).doubleValue();
		} else if (result instanceof Degrees) {
			result = ((Degrees)result).toDegrees();
		} else if (result instanceof Fix) {
			result = ((Fix)result).getValue();
		} else if (result instanceof UInt) {
			result = ((UInt)result).intValue();
		} else if (result instanceof Duration) {
			result = (int)((Duration) result).getSeconds();
		} else if (result instanceof DGPSStation) {
			result = ((DGPSStation)result).intValue();
		}

		return result;
	}

	/**
	 * Return the prepared statement query.
	 *
	 * @return the prepared statement query
	 */
	public String getQuery() {
		return _query;
	}

	@Override
	public String toString() {
		return _query;
	}

	/**
	 * Parses a query string into a query for prepared statements.
	 *
	 * @param sql the query string to parse
	 * @return a query string into a query for prepared statements
	 * @throws NullPointerException if one of the given arguments is {@code null}
	 */
	static PreparedSQL parse(final String sql, final List<Param> params) {
		final Map<String, List<Param>> paramsMap = params.stream()
			.collect(Collectors.groupingBy(Param::name));

		final List<String> names = new ArrayList<>();
		final StringBuffer parsedQuery = new StringBuffer();
		final Matcher matcher = PARAM_PATTERN.matcher(sql);
		while (matcher.find()) {
			final String name = matcher.group(1);
			if (!paramsMap.containsKey(name)) {
				throw new IllegalArgumentException(format(
					"Param '%s' not found.", name
				));
			}

			names.add(name);

			final String placeHolder = paramsMap.get(name).stream()
				.flatMap(p -> p.of().stream())
				.map(p -> "?")
				.collect(Collectors.joining(","));

			matcher.appendReplacement(parsedQuery, placeHolder);
		}
		matcher.appendTail(parsedQuery);

		return new PreparedSQL(parsedQuery.toString(), names);
	}

	/**
	 * Creates a new {@link PreparedStatement} from the given query and fills
	 * the parameters in one step.
	 *
	 * @param sql the SLQ query
	 * @param params the query parameters
	 * @param conn the DB connection
	 * @return the newly prepared statement
	 * @throws SQLException if the preparation fails
	 * @throws NullPointerException if one of the arguments is {@code null}
	 */
	public static PreparedStatement prepare(
		final String sql,
		final List<Param> params,
		final Connection conn
	)
		throws SQLException
	{
		final PreparedSQL preparedSQL = parse(sql, params);
		final PreparedStatement stmt = preparedSQL.prepare(conn);
		preparedSQL.fill(stmt, params);

		return stmt;
	}

}
