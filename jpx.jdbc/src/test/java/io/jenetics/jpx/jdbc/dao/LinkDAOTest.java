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
package io.jenetics.jpx.jdbc.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.testng.Assert;
import org.testng.annotations.Test;

import io.jenetics.jpx.Link;
import io.jenetics.jpx.LinkTest;
import io.jenetics.jpx.jdbc.DAOTestBase;
import io.jenetics.jpx.jdbc.internal.querily.Stored;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class LinkDAOTest extends DAOTestBase {

	private final Random random = new Random(1231321);
	private final List<Link> links = Stream.generate(() -> LinkTest.nextLink(random))
		.limit(10)
		.collect(Collectors.toList());

//	@Test
//	public void insert() throws SQLException {
//		db.transaction(conn -> {
//			final List<Long> ids = LinkDAO.insert(links, conn);
//
//			Assert.assertEquals(ids.size(), links.size());
//		});
//	}
//
//	@Test(dependsOnMethods = "insert")
//	public void selectAll() throws SQLException {
//		db.transaction(conn -> {
//			final List<Stored<Link>> links = LinkDAO.selectAll(conn);
//			Assert.assertEquals(
//				links.stream()
//					.map(Stored::value)
//					.collect(Collectors.toList()),
//				this.links
//			);
//		});
//	}
}
