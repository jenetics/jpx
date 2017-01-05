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

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.jenetics.jpx.jdbc.DAO.Param;

/**
 * Represents a SQL query for usage with a {@link PreparedStatement}.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
final class PreparedQuery {
	private static final Pattern PARAM_PATTERN = Pattern.compile("\\{(\\w+?)\\}");

	private final String _query;
	private final Map<String, Integer> _params;

	private PreparedQuery(
		final String query,
		final Map<String, Integer> params
	) {
		_query = requireNonNull(query);
		_params = requireNonNull(params);
	}

	void fill(final PreparedStatement stmt, final List<Param> params)
		throws SQLException
	{
		for (Param param : params) {
			final Integer index = _params.get(param.getName());
			if (index != null) {
				stmt.setObject(index, param.getValue());
			}
		}
	}

	/**
	 * Return the prepared statement query.
	 *
	 * @return the prepared statement query
	 */
	String getQuery() {
		return _query;
	}

	@Override
	public String toString() {
		return _query;
	}

	/**
	 * Parses a query string into a query for prepared statements.
	 *
	 * @param query the query string to parse
	 * @return a query string into a query for prepared statements
	 */
	public static PreparedQuery parse(final String query) {
		final Matcher matcher = PARAM_PATTERN.matcher(query);

		int index = 1;
		final Map<String, Integer> params = new HashMap<>();
		final StringBuffer parsedQuery = new StringBuffer();
		while (matcher.find()) {
			params.put(matcher.group(1), index++);
			matcher.appendReplacement(parsedQuery, "?");
		}
		matcher.appendTail(parsedQuery);

		return new PreparedQuery(parsedQuery.toString(), params);
	}

}
