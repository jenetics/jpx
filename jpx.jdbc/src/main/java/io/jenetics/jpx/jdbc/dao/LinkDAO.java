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
package io.jenetics.jpx.jdbc.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import io.jenetics.jpx.Link;
import io.jenetics.jpx.jdbc.internal.querily.Dctor;
import io.jenetics.jpx.jdbc.internal.querily.Dctor.Field;
import io.jenetics.jpx.jdbc.internal.querily.Query;
import io.jenetics.jpx.jdbc.internal.querily.RowParser;
import io.jenetics.jpx.jdbc.internal.querily.Stored;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class LinkDAO {

	private LinkDAO() {
	}

	private static final RowParser<Stored<Link>> ROW_PARSER = row -> Stored.of(
		row.getLong("id"),
		Link.of(
			row.getString("href"),
			row.getString("text"),
			row.getString("type")
		)
	);

	private static final Dctor<Link> DCTOR = Dctor.of(
		Field.of("href", l -> l.getHref().toString()),
		Field.of("text", l -> l.getText().orElse(null)),
		Field.of("type", l -> l.getType().orElse(null))
	);

	public static List<Long> insert(final List<Link> links, final Connection conn)
		throws SQLException
	{
		final String sql =
			"INSERT INTO link(href, text, type) " +
				"VALUES({href}, {text}, {type});";

		return Query.of(sql).executeInsert(links, DCTOR, conn);
	}

	/**
	 * Select all available links.
	 *
	 * @return all stored links
	 * @throws SQLException if the operation fails
	 * @throws NullPointerException if one of the arguments is {@code null}
	 */
	public static List<Stored<Link>> selectAll(final Connection conn)
		throws SQLException
	{
		final String sql =
			"SELECT id, href, text, type FROM link ORDER BY id ASC";

		return Query.of(sql).as(ROW_PARSER.list(), conn);
	}



}
