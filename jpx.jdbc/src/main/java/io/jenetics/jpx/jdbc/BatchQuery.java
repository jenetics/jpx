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

import static java.sql.Statement.RETURN_GENERATED_KEYS;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Represents batch insert query.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class BatchQuery extends AbstractQuery {

	public BatchQuery(final Connection conn, final String query) {
		super(conn, query);
	}

	public <T> List<Stored<T>> insert(
		final List<T> values,
		final Function<T, List<Param>> format
	)
		throws SQLException
	{
		for (T value : values) {
			for (Param param : format.apply(value)) {
				param.eval();
			}
		}

		final List<Stored<T>> results = new ArrayList<>();
		try (PreparedStatement stmt = _conn
			.prepareStatement(_query.getQuery(), RETURN_GENERATED_KEYS))
		{
			for (T value : values) {
				final List<Param> params = format.apply(value);
				_query.fill(stmt, params);

				stmt.executeUpdate();
				results.add(Stored.of(DAO.id(stmt), value));
			}
		}

		return results;
	}

	public <T> int update(
		final List<T> values,
		final Function<T, List<Param>> format
	)
		throws SQLException
	{
		for (T value : values) {
			for (Param param : format.apply(value)) {
				param.eval();
			}
		}

		int count = 0;
		try (PreparedStatement stmt = _conn.prepareStatement(_query.getQuery())) {
			for (T value : values) {
				final List<Param> params = format.apply(value);
				_query.fill(stmt, params);

				count += stmt.executeUpdate();
			}
		}

		return count;
	}

}
