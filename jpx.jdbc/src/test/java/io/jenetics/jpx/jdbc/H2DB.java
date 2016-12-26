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

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Random;

import javax.sql.DataSource;

import org.h2.jdbcx.JdbcDataSource;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public class H2DB extends DB {

	public static final DB INSTANCE = new H2DB("jdbc:h2:mem:testdb-gpx;MODE=MySQL");

	private final DataSource _dataSource;

	public H2DB(final DataSource dataSource) {
		_dataSource = requireNonNull(dataSource);
	}

	public H2DB(final String url) {
		this(ds(url));
	}

	private static DataSource ds(final String url) {
		final JdbcDataSource ds = new JdbcDataSource();
		ds.setURL(url);
		return ds;
	}

	@Override
	public Connection getConnection() throws SQLException {
		return _dataSource.getConnection();
	}

	@Override
	public <T> T transaction(final Executable<T> executable) throws SQLException {
		return transaction(getConnection(), executable);
	}

	@Override
	public void transaction(final Callable callable) throws SQLException {
		transaction(getConnection(), c -> {callable.call(c); return null;});
	}

	public static DB newTestInstance() {
		final String name = format("testdb_%s", Math.abs(new Random().nextLong()));
		final String url = format("jdbc:h2:mem:%s;MODE=MySQL", name);
		return new H2DB(url);
	}

}
