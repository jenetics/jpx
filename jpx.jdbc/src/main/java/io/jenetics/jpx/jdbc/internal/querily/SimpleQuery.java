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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public class SimpleQuery extends Query {

	private static final Pattern PARAM_PATTERN = Pattern.compile("\\{(\\w+?)\\}");

	private SimpleQuery(final String sql, final List<String> names) {
		super(sql, names);
	}

	/**
	 * Return a new query object with the given query parameter values.
	 *
	 * @param params the query parameters
	 * @return a new parameter query
	 */
	public PreparedQuery on(final Param... params) {
		return PreparedQuery.of(this, params);
	}

	@Override
	PreparedStatement prepare(final Connection conn) throws SQLException {
		return conn.prepareStatement(sql(), RETURN_GENERATED_KEYS);
	}

	public <T> List<Long> executeInsert(
		final Collection<T> values,
		final Dctor<T> dctor,
		final Connection conn
	)
		throws SQLException
	{
		return null;
	}

	public <T> List<Long> executeInsert(
		final Collection<T> values,
		final Function<String, Function<String, ?>> dctor,
		final Connection conn
	)
		throws SQLException
	{
		return null;
	}

	public <T> List<Long> executeInsert(
		final Collection<T> values,
		final Map<String, Function<String, ?>> dctor,
		final Connection conn
	)
		throws SQLException
	{
		return null;
	}

	/* *************************************************************************
	 * Static factory methods.
	 * ************************************************************************/

	public static SimpleQuery of(final String sql) {
		final List<String> names = new ArrayList<>();
		final StringBuffer parsedQuery = new StringBuffer();

		final Matcher matcher = PARAM_PATTERN.matcher(sql);
		while (matcher.find()) {
			final String name = matcher.group(1);
			names.add(name);

			matcher.appendReplacement(parsedQuery, "?");
		}
		matcher.appendTail(parsedQuery);

		return new SimpleQuery(parsedQuery.toString(), names);
	}

}
