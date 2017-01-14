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
import static io.jenetics.jpx.jdbc.Lists.map;

import java.sql.SQLException;
import java.util.List;
import java.util.Random;

import org.testng.Assert;
import org.testng.annotations.Test;

import io.jenetics.jpx.Email;
import io.jenetics.jpx.Person;
import io.jenetics.jpx.jdbc.internal.db.Column;
import io.jenetics.jpx.jdbc.internal.db.Stored;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class PersonDAOTest extends DAOTestBase<Person> {

	@Override
	public Person nextObject(final Random random) {
		return nextPerson(random);
	}

	private final List<Person> objects = nextObjects(new Random(123), 20);

	@Test
	public void insert() throws SQLException {
		db.transaction(conn -> {
			new PersonDAO(conn).insert(objects);
		});
	}

	@Test(dependsOnMethods = "insert")
	public void select() throws SQLException {
		final List<Stored<Person>> existing = db.transaction(conn -> {
			return new PersonDAO(conn).select();
		});

		Assert.assertEquals(map(existing, Stored::value), objects);
	}

	@Test(dependsOnMethods = "insert")
	public void selectByName() throws SQLException {
		final List<Stored<Person>> selected = db.transaction(conn -> {
			return new PersonDAO(conn)
				.selectBy("name", objects.get(0).getName());
		});

		Assert.assertEquals(selected.get(0).value(), objects.get(0));
	}

	@Test(dependsOnMethods = "select")
	public void update() throws SQLException {
		final List<Stored<Person>> existing = db.transaction(conn -> {
			return new PersonDAO(conn).select();
		});

		db.transaction(conn -> {
			final Stored<Person> updated = existing.get(0)
				.map(l -> Person.of(
					l.getName().orElse(null),
					Email.of("other", "mail")));

			Assert.assertEquals(
				new PersonDAO(conn).update(updated),
				updated
			);

			Assert.assertEquals(new PersonDAO(conn).select().get(0), updated);
		});
	}

	@Test(dependsOnMethods = "update")
	public void put() throws SQLException {
		db.transaction(conn -> {
			final PersonDAO dao = new PersonDAO(conn);

			dao.put(objects);
			Assert.assertEquals(map(dao.select(), Stored::value), objects);
		});
	}

	@Test(dependsOnMethods = "put")
	public void delete() throws SQLException {
		db.transaction(conn -> {
			final PersonDAO dao = new PersonDAO(conn);

			final int count = dao
				.deleteBy(Column.of("name", Person::getName), objects.get(0));

			Assert.assertEquals(count, 1);

			Assert.assertEquals(
				map(dao.select(), Stored::value),
				objects.subList(1, objects.size())
			);
		});
	}

}
