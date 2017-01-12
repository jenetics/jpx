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

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public class MySQL extends DB {

	public final static DB INSTANCE = new MySQL();

	private static final String TEST_DB;

	static {
		try {
			final Properties pwd = new Properties();
			try (final FileInputStream in = new FileInputStream("/home/fwilhelm/pwd.properties")) {
				pwd.load(in);
			}

			TEST_DB =
				"jdbc:mysql://playstation:3306/gpx_test?user=gpx_test&" +
					"password=" + pwd.getProperty("GPX_TEST_DB_PASSWORD");

			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public Connection getConnection() throws SQLException {
		return DriverManager.getConnection(TEST_DB);
	}

}
