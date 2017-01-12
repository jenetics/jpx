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

import static io.jenetics.jpx.PersonTest.nextPerson;

import java.sql.SQLException;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.testng.Assert;
import org.testng.annotations.Test;

import io.jenetics.jpx.Person;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class PersonDAOTest extends DAOTestBase<Person> {

	@Override
	public Person nextObject(final Random random) {
		return nextPerson(random);
	}

	private final List<Person> persons = nextObjects(new Random(123), 20);


	@Test
	public void insert() throws SQLException {
		db.transaction(conn -> {
			new PersonDAO(conn).insert(persons);
		});
	}

	@Test(dependsOnMethods = "insert")
	public void select() throws SQLException {
		final List<Stored<Person>> existing = db.transaction(conn -> {
			return new PersonDAO(conn).select();
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
			return new PersonDAO(conn).select();
		});

		db.transaction(conn -> {
			final Stored<Person> updated = existing.get(0)
				.map(p -> Person.of(p.getName().get(), null, null));

			Assert.assertEquals(
				new PersonDAO(conn).update(updated),
				updated
			);
		});
	}

	@Test(dependsOnMethods = "update")
	public void put() throws SQLException {
		db.transaction(conn -> {
			final PersonDAO dao = new PersonDAO(conn);

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
