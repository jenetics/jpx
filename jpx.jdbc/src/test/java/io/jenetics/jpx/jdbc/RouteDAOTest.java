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

import static io.jenetics.jpx.jdbc.Lists.map;

import java.sql.SQLException;
import java.util.List;
import java.util.Random;

import org.testng.Assert;
import org.testng.annotations.Test;

import io.jenetics.jpx.Route;
import io.jenetics.jpx.RouteTest;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public class RouteDAOTest extends DAOTestBase<Route> {

	@Override
	public Route nextObject(final Random random) {
		return RouteTest.nextRoute(random);
	}

	private final List<Route> objects = nextObjects(new Random(), 20);

	@Test
	public void insert() throws SQLException {
		db.transaction(conn -> {
			new RouteDAO(conn).insert(objects);
		});
	}

	@Test(dependsOnMethods = "insert")
	public void select() throws SQLException {
		final List<Stored<Route>> existing = db.transaction(conn -> {
			return new RouteDAO(conn).select();
		});

		Assert.assertEquals(map(existing, Stored::value), objects);
	}

	@Test(dependsOnMethods = "insert")
	public void selectByName() throws SQLException {
		final List<Stored<Route>> selected = db.transaction(conn -> {
			return new RouteDAO(conn)
				.selectBy("name", objects.get(0).getName());
		});

		Assert.assertEquals(
			selected.get(0).value(),
			objects.get(0));
	}

	@Test(dependsOnMethods = "select")
	public void update() throws SQLException {
		final List<Stored<Route>> existing = db.transaction(conn -> {
			return new RouteDAO(conn).select();
		});

		db.transaction(conn -> {
			final Stored<Route> updated = existing.get(0)
				.map(l -> nextObject(new Random()));

			Assert.assertEquals(
				new RouteDAO(conn).update(updated),
				updated
			);

			Assert.assertEquals(
				new RouteDAO(conn).select().get(0).value(),
				updated.value()
			);
		});
	}

	@Test(dependsOnMethods = "update")
	public void delete() throws SQLException {
		db.transaction(conn -> {
			final RouteDAO dao = new RouteDAO(conn);

			final List<Stored<Route>> existing = dao.select();

			final int count = dao
				.deleteBy(Column.of("name", md -> md.value().getName()), existing.get(0));

			Assert.assertEquals(count, 1);

			Assert.assertEquals(
				dao.select(),
				existing.subList(1, existing.size())
			);
		});
	}

}
