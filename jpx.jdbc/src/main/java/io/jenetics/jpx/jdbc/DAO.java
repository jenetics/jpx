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
import static java.util.Objects.requireNonNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * Abstract DAO class
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public abstract class DAO {
	/**
	 * Represents a query parameter with <em>name</em> and <em>value</em>.
	 */
	public static final class Param {

		private final String _name;
		private final SQL.Lazy<?> _value;

		private Param(final String name, final SQL.Lazy<?> value) {
			_name = requireNonNull(name);
			_value = requireNonNull(value);
		}

		/**
		 * Return the parameter name.
		 *
		 * @return the parameter name
		 */
		public String getName() {
			return _name;
		}

		void eval() throws SQLException {
			_value.get();
		}

		/**
		 * Return the parameter value.
		 *
		 * @return the parameter value.
		 */
		@SuppressWarnings({"raw", "unchecked"})
		public Object getValue() throws SQLException {
			Object value = _value.get();
			if (value instanceof Optional<?>) {
				value = ((Optional)value).orElse(null);
			}
			if (value instanceof Optional<?>) {
				value = ((Optional)value).orElse(null);
			}

			return value;
		}

		/**
		 * Create a new query parameter object from the given {@code name} and
		 * {@code value}.
		 *
		 * @param name the parameter name
		 * @param value the parameter value
		 * @return a new query parameter object
		 * @throws NullPointerException if the given parameter {@code name} is
		 *         {@code null}
		 */
		public static Param value(final String name, final Object value) {
			return new Param(name, SQL.Lazy.ofValue(value));
		}

		public static <T> Param insert(
			final String name,
			final SQL.Supplier<T> value
		) {
			return new Param(name, SQL.Lazy.of(value));
		}
	}

	/**
	 * Represents a select SQL query.
	 */
	public static final class SQLQuery extends AbstractQuery {
		private final List<Param> _params = new ArrayList<>();

		public SQLQuery(final Connection conn, final PreparedQuery query) {
			super(conn, query);
		}

		public SQLQuery(final Connection conn, final String query) {
			super(conn, query);
		}

		public SQLQuery on(final String name, final Object value) {
			_params.add(Param.value(name, value));
			return this;
		}

		public <T> T as(final RowParser<T> parser) throws SQLException {
			for (Param param : _params) {
				param.eval();
			}

			try (PreparedStatement stmt = _conn.prepareStatement(_query.getQuery())) {
				_query.fill(stmt, _params);
				try (final ResultSet rs = stmt.executeQuery()) {
					return parser.parse(rs);
				}
			}
		}

	}

	/**
	 * Represents batch insert query.
	 */
	public static final class Batch {
		private final Connection _conn;
		private final PreparedQuery _query;

		public Batch(final Connection conn, final String query) {
			_conn = conn;
			_query = PreparedQuery.parse(query);
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
					results.add(Stored.of(id(stmt), value));
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

	protected final Connection _conn;

	/**
	 * Create a new DAO object with uses the given connection.
	 *
	 * @param conn the DB connection used for the DAO operations
	 */
	protected DAO(final Connection conn) {
		_conn = conn;
	}

	public <T> T dao(final Function<Connection, T> create) {
		return create.apply(_conn);
	}

	/**
	 * Create a new select query object.
	 *
	 * @param query the SQL query
	 * @return a new select query object
	 */
	public SQLQuery sql(final String query) {
		return new SQLQuery(_conn, query);
	}

	/**
	 * Create a new batch insert query object
	 *
	 * @param query the insert SQL query
	 * @return a new batch insert query object
	 */
	public Batch batch(final String query) {
		return new Batch(_conn, query);
	}

	public static <T> Stored<T> put(
		final T value,
		final SQL.Function<T, SQL.Option<Stored<T>>> select,
		final SQL.Function<T, Stored<T>> insert,
		final SQL.Consumer<Stored<T>> update
	)
		throws SQLException
	{
		return select.apply(value)
			.map(stored -> {
				update.accept(stored);
				return stored.copy(value); })
			.orElseGet(() -> insert.apply(value));
	}

	/**
	 * Reads the auto increment id from the previously inserted record.
	 *
	 * @param stmt the statement used for inserting the record
	 * @return the DB id of the inserted record
	 * @throws SQLException if fetching the ID fails
	 */
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
