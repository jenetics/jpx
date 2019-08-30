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
package io.jenetics.jpx.jdbc.internal.querily;

import java.sql.SQLException;

import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import io.jenetics.jpx.jdbc.DB;
import io.jenetics.jpx.jdbc.H2DB;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class QuerilyTest {

	public final DB db = H2DB.newTestInstance();

	@AfterClass
	public void shutdown() throws SQLException {
		db.close();
	}

	@Test
	public void setup() throws SQLException {
		db.transaction(conn -> {
			final Query query = Query.of(
				"CREATE TABLE link(\n" +
				"    id BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,\n" +
				"    href VARCHAR(255) NOT NULL,\n" +
				"    text VARCHAR(255),\n" +
				"    type VARCHAR(255),\n" +
				"    CONSTRAINT c_link_href UNIQUE (href)\n" +
				");"
			);

			query.execute(conn);
		});
	}

	@Test
	public void insert() throws SQLException {

	}

}
