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

import io.jenetics.jpx.EmailTest;
import io.jenetics.jpx.LinkTest;
import io.jenetics.jpx.Person;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 */
public class PersonDAOTest {

	private final DB db = H2DB.newTestInstance();

	private final List<Person> persons = nextPersons(new Random(123), 2);

	private static List<Person> nextPersons(final Random random, final int count) {
		return Stream.generate(() -> nextPerson(random))
			.limit(count)
			.collect(Collectors.toList());
	}

	private static Person nextPerson(final Random random) {
		return Person.of(
			format("name_%s", Math.abs(random.nextLong())),
			random.nextBoolean() ? EmailTest.nextEmail(random) : null,
			random.nextBoolean() ? LinkTest.nextLink(random) : null
		);
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
			PersonDAO.of(conn).insert(persons);
		});
	}

	@Test(dependsOnMethods = "insert")
	public void select() throws SQLException {
		final List<Stored<Person>> existing = db.transaction(conn -> {
			return PersonDAO.of(conn).select();
		});

		Assert.assertEquals(
			existing.stream()
				.map(Stored::value)
				.collect(Collectors.toSet()),
			persons.stream()
				.collect(Collectors.toSet())
		);
	}

	@Test(dependsOnMethods = "select")
	public void update() throws SQLException {
		final List<Stored<Person>> existing = db.transaction(conn -> {
			return PersonDAO.of(conn).select();
		});

		db.transaction(conn -> {
			final Stored<Person> updated = existing.get(0)
				.map(p -> Person.of(p.getName().get(), null, null));

			Assert.assertEquals(
				PersonDAO.of(conn).update(updated),
				updated
			);
		});
	}

	@Test(dependsOnMethods = "update")
	public void put() throws SQLException {
		db.transaction(conn -> {
			final PersonDAO dao = PersonDAO.of(conn);

			dao.put(persons);

			Assert.assertEquals(
				dao.select().stream()
					.map(Stored::value)
					.collect(Collectors.toSet()),
				persons.stream()
					.collect(Collectors.toSet())
			);
		});
	}

}
