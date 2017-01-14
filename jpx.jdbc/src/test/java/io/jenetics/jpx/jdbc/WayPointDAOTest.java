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

import io.jenetics.jpx.WayPoint;
import io.jenetics.jpx.WayPointTest;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public class WayPointDAOTest extends DAOTestBase<WayPoint> {

	@Override
	public WayPoint nextObject(final Random random) {
		return WayPointTest.nextWayPoint(random);
	}

	private final List<WayPoint> objects = nextObjects(new Random(), 20);

	@Test
	public void insert() throws SQLException {
		db.transaction(conn -> {
			new WayPointDAO(conn).insert(objects);
		});
	}

	@Test(dependsOnMethods = "insert")
	public void select() throws SQLException {
		final List<Stored<WayPoint>> existing = db.transaction(conn -> {
			return new WayPointDAO(conn).select();
		});

		Assert.assertEquals(map(existing, Stored::value), objects);
	}

	@Test(dependsOnMethods = "insert")
	public void selectByLat() throws SQLException {
		final List<Stored<WayPoint>> selected = db.transaction(conn -> {
			return new WayPointDAO(conn)
				.selectBy("lat", objects.get(0).getLatitude());
		});

		Assert.assertEquals(selected.get(0).value(), objects.get(0));
	}

	@Test(dependsOnMethods = "select")
	public void update() throws SQLException {
		final List<Stored<WayPoint>> existing = db.transaction(conn -> {
			return new WayPointDAO(conn).select();
		});

		db.transaction(conn -> {
			final Stored<WayPoint> updated = existing.get(0)
				.map(l -> nextObject(new Random()));

			Assert.assertEquals(
				new WayPointDAO(conn).update(updated),
				updated
			);

			Assert.assertEquals(new WayPointDAO(conn).select().get(0), updated);
		});
	}

	@Test(dependsOnMethods = "update")
	public void delete() throws SQLException {
		db.transaction(conn -> {
			final WayPointDAO dao = new WayPointDAO(conn);

			final List<Stored<WayPoint>> existing = dao.select();

			final int count = dao
				.deleteBy(Column.of("lat", md -> md.value().getLatitude()), existing.get(0));

			Assert.assertEquals(count, 1);

			Assert.assertEquals(
				dao.select(),
				existing.subList(1, existing.size())
			);
		});
	}

}
