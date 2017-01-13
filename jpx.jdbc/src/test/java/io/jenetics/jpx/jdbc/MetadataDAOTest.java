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

import io.jenetics.jpx.Metadata;
import io.jenetics.jpx.MetadataTest;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class MetadataDAOTest extends DAOTestBase<Metadata> {

	@Override
	public Metadata nextObject(final Random random) {
		return MetadataTest.nextMetadata(random);
	}

	private final List<Metadata> objects = nextObjects(new Random(123), 3);


	@Test
	public void insert() throws SQLException {
		db.transaction(conn -> {
			new MetadataDAO(conn).insert(objects);
		});
	}

	@Test(dependsOnMethods = "insert")
	public void select() throws SQLException {
		final List<Stored<Metadata>> existing = db.transaction(conn -> {
			return new MetadataDAO(conn).select();
		});

		Assert.assertEquals(map(existing, Stored::value), objects);
	}

}
