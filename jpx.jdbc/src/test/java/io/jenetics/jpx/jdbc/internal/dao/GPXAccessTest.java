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

import java.sql.SQLException;
import java.util.Random;

import org.testng.annotations.Test;

import io.jenetics.jpx.GPX;
import io.jenetics.jpx.GPXTest;
import io.jenetics.jpx.jdbc.DAOTestBase;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public class GPXAccessTest extends DAOTestBase {

	private final Random random = new Random(1231321);
	private final GPX gpx = GPXTest.nextGPX(random);

	@Test
	public void insert() throws SQLException {
		db.transaction(conn -> {
			final Long id = GPXAccess.insert(gpx, conn);
			System.out.println(id);
		});
	}

}
