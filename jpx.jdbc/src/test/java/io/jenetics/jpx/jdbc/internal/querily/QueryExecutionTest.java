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

import lombok.ToString;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import io.jenetics.jpx.jdbc.DB;
import io.jenetics.jpx.jdbc.DB.Callable;
import io.jenetics.jpx.jdbc.H2DB;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class QueryExecutionTest {

	@ToString
	public static final class LinkRow {
		final String href;
		final String text;
		final String type;

		LinkRow(final String href, final String text, final String type) {
			this.href = href;
			this.text = text;
			this.type = type;
		}

		private static final RowParser<Stored<LinkRow>> ROW_PARSER =
			row -> Stored.of(
				row.get("id"),
				new LinkRow(
					row.get("href"),
					row.get("text"),
					row.get("type")
				)
			);
	}

	public final DB db = H2DB.newTestInstance();

	@AfterClass
	public void shutdown() throws SQLException {
		db.close();
	}

	@Test
	public void setup() throws SQLException {
		final Query query = Query.of(
			"CREATE TABLE link(\n" +
				"    id BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,\n" +
				"    href VARCHAR(255) NOT NULL,\n" +
				"    text VARCHAR(255),\n" +
				"    type VARCHAR(255),\n" +
				"    CONSTRAINT c_link_href UNIQUE (href)\n" +
				");"
		);

		db.transaction((Callable)query::execute);
	}

	@Test(dependsOnMethods = "setup")
	public void insert() throws SQLException {
		final Query query = Query.of(
			"INSERT INTO link(href, text, type) " +
				"VALUES('http://link.com', 'some text', 'some type');"
		);

		db.transaction(conn -> {
			final Optional<Long> id = query.executeInsert(conn);
			System.out.println(id);
		});
	}

	@Test(dependsOnMethods = "insert")
	public void select() throws SQLException {
		final Query query = Query.of("SELECT * FROM link;");

		db.transaction(conn -> {
			final List<Stored<LinkRow>> rows = query
				.as(LinkRow.ROW_PARSER.list(), conn);

			System.out.println(rows);
		});
	}
}
