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
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmail.com)
 */
package io.jenetics.jpx.jdbc.internal.dao;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Random;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import io.jenetics.jpx.GPX;
import io.jenetics.jpx.GPXTest;
import io.jenetics.jpx.jdbc.IO;
import io.jenetics.jpx.jdbc.MariaDB;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public class GPXAccessTest {

	private final Random random = new Random(1231321);
	private final GPX gpx = GPXTest.nextGPX(random);

	//@BeforeClass
	public void setup() throws IOException, SQLException {
		final String[] queries = IO.
			toSQLText(getClass().getResourceAsStream("/model-mysql.sql"))
			.split(";");

		MariaDB.INSTANCE.transaction(conn -> {
			for (String query : queries) {
				if (!query.trim().isEmpty()) {
					try (Statement stmt = conn.createStatement()) {
						stmt.execute(query);
					}
				}
			}
		});
	}

	@Test
	public void insert() throws SQLException {
		MariaDB.INSTANCE.transaction(conn -> {
			final Long id = GPXAccess.insert(gpx, conn);
			System.out.println(id);
		});
	}

}
