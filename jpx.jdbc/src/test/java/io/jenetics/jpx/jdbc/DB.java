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
import java.sql.SQLException;

/**
 * Test DB abstraction.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public abstract class DB {

	@FunctionalInterface
	public interface Executable<T> {
		public T execute(final Connection conn) throws SQLException;
	}

	@FunctionalInterface
	public interface Callable {
		public void call(final Connection conn) throws SQLException;
	}

	public abstract Connection getConnection() throws SQLException;

	public <T> T transaction(
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

	public <T> T transaction(final Executable<T> executable) throws SQLException {
		try (Connection conn = getConnection()) {
			return transaction(conn, executable);
		}
	}

	public void transaction(final Callable callable) throws SQLException {
		try (Connection conn = getConnection()) {
			transaction(conn, c -> {callable.call(c); return null;});
		}
	}

	public void close() throws SQLException {
	}

}
