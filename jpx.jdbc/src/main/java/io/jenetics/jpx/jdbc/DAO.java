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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public abstract class DAO<T> {

	public static interface RowParser<T> {
		public Stored<T> toRow(final ResultSet rs) throws SQLException;
	}

	protected final Connection _conn;

	public DAO(final Connection conn) {
		_conn = conn;
	}

	public abstract RowParser<T> parser();

	public PreparedStatement prepareInsert(final String sql) throws SQLException {
		return _conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
	}

	public Optional<Stored<T>> firstOption(final ResultSet rs) throws SQLException {
		return rs.next()
			? Optional.of(parser().toRow(rs))
			: Optional.empty();
	}

	public List<Stored<T>> toList(final ResultSet rs) throws SQLException {
		final List<Stored<T>> result = new ArrayList<>();
		while (rs.next()) {
			result.add(parser().toRow(rs));
		}

		return result;
	}

	public Stream<Stored<T>> toStream(final ResultSet rs) throws SQLException {
		return null;
	}

	public static long id(final Statement stmt) throws SQLException {
		try (ResultSet keys = stmt.getGeneratedKeys()) {
			if (keys.next()) {
				return keys.getLong(1);
			} else {
				throw new SQLException("Can't fetch generation ID.");
			}
		}
	}

}
