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

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import io.jenetics.facilejdbc.Batch;
import io.jenetics.facilejdbc.Query;
import io.jenetics.facilejdbc.Stored;

import io.jenetics.jpx.Link;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class LinkAccessTest {

	private static final List<Link> LINKS = List.of(
		Link.of("http://jenetics.io"),
		Link.of("http://jenetics.io", "Jenetics", "web"),
		Link.of("https://duckduckgo.com", "DuckDuckGo", "search")
	);

	@BeforeClass
	public void setup() throws IOException, SQLException {
		final String[] queries = IO.
			toSQLText(getClass().getResourceAsStream("/model-psql.sql"))
			.split(";");

		H2DB.INSTANCE.transaction(conn -> {
			for (String query : queries) {
				if (!query.trim().isEmpty()) {
					try (Statement stmt = conn.createStatement()) {
						stmt.execute(query.trim());
					}
				}
			}
		});
	}

	@Test
	public void insert() throws SQLException {
		H2DB.INSTANCE.transaction(conn -> {
			final var query = LinkAccess.INSERT.prepareQuery(conn);
			final var batch = Batch.of(LINKS, LinkAccess.DCTOR);
			query.execute(batch);

			LinkAccess.INSERT.execute(batch, conn);
		});
	}

//	@Test(dependsOnMethods = "insert")
//	public void select() throws SQLException {
//		final var select = Query.of("SELECT * FROM link ORDER BY id");
//
//		H2DB.INSTANCE.transaction(conn -> {
//		final List<Stored<Long, Link>> links = select
//			.as(LinkAccess.PARSER.stored("id").list(), conn);
//
//			links.forEach(System.out::println);
//
//			assertThat(links.stream().map(Stored::value).toList())
//				.isEqualTo(LINKS);
//		});
//	}

}
