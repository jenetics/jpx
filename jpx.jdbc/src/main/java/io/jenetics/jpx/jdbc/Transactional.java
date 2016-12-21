/*
 * Java Genetic Algorithm Library (@__identifier__@).
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
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 */
package io.jenetics.jpx.jdbc;

import static java.util.Objects.requireNonNull;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public interface Transactional {

	public interface Executable<T> {
		public T execute(final Connection conn) throws SQLException;
	}

	public interface Callable {
		public void call(final Connection conn) throws SQLException;
	}

	public DataSource dataSource();


	public default <T> T transaction(final Executable<T> executable) throws SQLException {
		try (Connection conn = dataSource().getConnection()) {
			return transaction(conn, executable);
		}
	}

	public default void transaction(final Callable callable) throws SQLException {
		try (Connection conn = dataSource().getConnection()) {
			transaction(conn, c -> {callable.call(c); return null;});
		}
	}

	static <T> T transaction(
		final Connection connection,
		final Executable<T> executable
	)
		throws SQLException
	{
		try {
			if (connection.getAutoCommit()) {
				connection.setAutoCommit(false);
			}

			final T result = executable.execute(connection);
			connection.commit();
			return result;
		} catch (Throwable e) {
			try {
				connection.rollback();
			} catch (Exception suppressed) {
				e.addSuppressed(suppressed);
			}
			throw e;
		}
	}

}
