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

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.testng.Assert;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import io.jenetics.jpx.Copyright;
import io.jenetics.jpx.CopyrightTest;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class CopyrightDAOTest {

	private final DB db = H2DB.newTestInstance();

	private final List<Copyright> copyrights = nextCopyrights(new Random(123), 20);

	private static List<Copyright> nextCopyrights(final Random random, final int count) {
		return Stream.generate(() -> CopyrightTest.nextCopyright(random))
			.limit(count)
			.collect(Collectors.toList());
	}

	@BeforeSuite
	public void setup() throws IOException, SQLException {
		final String[] queries = IO.
			toSQLText(getClass().getResourceAsStream("/model-mysql.sql"))
			.split(";");

		db.transaction(conn -> {
			for (String query : queries) {
				try (Statement stmt = conn.createStatement()) {
					stmt.execute(query);
				}
			}
		});
	}

	@Test
	public void insert() throws SQLException {
		db.transaction(conn -> {
			CopyrightDAO.of(conn).insert(copyrights);
		});
	}

	@Test(dependsOnMethods = "insert")
	public void select() throws SQLException {
		final List<Stored<Copyright>> existing = db.transaction(conn -> {
			return CopyrightDAO.of(conn).select();
		});

		Assert.assertEquals(
			existing.stream()
				.map(Stored::value)
				.collect(Collectors.toSet()),
			copyrights.stream()
				.collect(Collectors.toSet())
		);
	}

	@Test(dependsOnMethods = "select")
	public void update() throws SQLException {
		final List<Stored<Copyright>> existing = db.transaction(conn -> {
			return CopyrightDAO.of(conn).select();
		});

		db.transaction(conn -> {
			final Stored<Copyright> updated = existing.get(0)
				.map(l -> Copyright.of(l.getAuthor(), 2000, (String)null));

			Assert.assertEquals(
				CopyrightDAO.of(conn).update(updated),
				updated
			);
		});
	}

	@Test(dependsOnMethods = "update")
	public void put() throws SQLException {
		db.transaction(conn -> {
			final CopyrightDAO dao = CopyrightDAO.of(conn);

			dao.put(copyrights);

			Assert.assertEquals(
				DAO.map(dao.select(), Stored::value),
				copyrights.stream()
					.collect(Collectors.toSet())
			);
		});
	}

}
