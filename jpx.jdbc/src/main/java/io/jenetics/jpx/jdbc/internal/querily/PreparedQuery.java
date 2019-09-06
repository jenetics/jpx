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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.jenetics.jpx.jdbc.internal.querily.Param.Value;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
final class PreparedQuery extends Query {

	private final Map<String, Param> _params;

	private PreparedQuery(
		final String sql,
		final List<String> names,
		final Map<String, Param> params
	) {
		super(sql, names);
		_params = params;
	}

	@Override
	public PreparedQuery on(final Param... params) {
		final Map<String, Param> map = new HashMap<>(_params);
		for (Param param : params) {
			map.put(param.name(), param);
		}

		return new PreparedQuery(sql(), names(), map);
	}

	@Override
	PreparedStatement prepare(final Connection conn) throws SQLException {
		final PreparedStatement stmt =  conn.prepareStatement(
			sql(),
			RETURN_GENERATED_KEYS
		);
		fill(stmt);
		return stmt;
	}

	private void fill(final PreparedStatement stmt) throws SQLException {
		int index = 1;
		for (String name : names()) {
			if (_params.containsKey(name)) {
				stmt.setObject(index, _params.get(name).value().value());
			}

			++index;
		}
	}

	@Override
	public <T> List<Long> inserts(
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
						stmt.setObject(++index, value.value());
					} else if (_params.containsKey(name)) {
						stmt.setObject(++index, _params.get(name).value().value());
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


	/* *************************************************************************
	 * Static factory methods.
	 * ************************************************************************/

	static PreparedQuery of(final Query query, final Param... params) {
		final Map<String, Param> map = Stream.of(params)
			.collect(Collectors.toMap(
				Param::name,
				Function.identity(),
				(a, b) -> b));

		return new PreparedQuery(query.sql(), query.names(), map);
	}

}
