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

import io.jenetics.jpx.Link;
import io.jenetics.jpx.LinkTest;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class LinkDAOTest extends DAOTestBase<Link> {

	@Override
	public Link nextObject(final Random random) {
		return LinkTest.nextLink(random);
	}

	private final List<Link> links = nextObjects(new Random(123), 20);


	@Test
	public void insert() throws SQLException {
		db.transaction(conn -> {
			new LinkDAO(conn).insert(links);
		});
	}

	@Test(dependsOnMethods = "insert")
	public void select() throws SQLException {
		final List<Stored<Link>> existing = db.transaction(conn -> {
			return new LinkDAO(conn).select();
		});

		Assert.assertEquals(map(existing, Stored::value), links);
	}

	@Test(dependsOnMethods = "insert")
	public void selectByHref() throws SQLException {
		final List<Stored<Link>> selected = db.transaction(conn -> {
			return new LinkDAO(conn)
				.selectBy("href", links.get(0).getHref());
		});

		Assert.assertEquals(selected.get(0).value(), links.get(0));
	}

	@Test(dependsOnMethods = "select")
	public void update() throws SQLException {
		final List<Stored<Link>> existing = db.transaction(conn -> {
			return new LinkDAO(conn).select();
		});

		db.transaction(conn -> {
			final Stored<Link> updated = existing.get(0)
				.map(l -> Link.of(l.getHref(), "Other text", null));

			Assert.assertEquals(
				new LinkDAO(conn).update(updated),
				updated
			);

			Assert.assertEquals(new LinkDAO(conn).select().get(0), updated);
		});
	}

	@Test(dependsOnMethods = "update")
	public void put() throws SQLException {
		db.transaction(conn -> {
			final LinkDAO dao = new LinkDAO(conn);

			dao.put(links);
			Assert.assertEquals(map(dao.select(), Stored::value), links);
		});
	}
}
