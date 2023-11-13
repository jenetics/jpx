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

import static io.jenetics.facilejdbc.Dctor.field;
import static io.jenetics.facilejdbc.Param.value;
import static io.jenetics.facilejdbc.Row.map;

import java.net.URI;
import java.sql.Connection;
import java.sql.SQLException;

import io.jenetics.facilejdbc.Dctor;
import io.jenetics.facilejdbc.Query;
import io.jenetics.facilejdbc.RowParser;

import io.jenetics.jpx.Link;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class LinkAccess {
	private LinkAccess() {
	}

	static final RowParser<Link> PARSER = (row, conn) -> Link.of(
		map(row.getString("href"), URI::create),
		row.getString("text"),
		row.getString("type")
	);

	static final Dctor<Link> DCTOR = Dctor.of(
		field("href", Link::getHref),
		field("text", Link::getText),
		field("type", Link::getType)
	);

	static final Query SELECT_BY_ID = Query.of("""
		SELECT id, href, text, type
		FROM link
		WHERE id = :id
		"""
	);

	static final Query INSERT = Query.of("""
		INSERT INTO link(href, text, type)
		VALUES(:href, :text, :type)
		"""
	);

	public static Link selectById(final Long id, final Connection conn)
		throws SQLException
	{
		return id != null
			? SELECT_BY_ID
				.on(value("id", id))
				.as(PARSER.singleNull(), conn)
			: null;
	}

	public static Long insertIfMissing(final Link link, final Connection conn)
		throws SQLException
	{
		final var select = Query.of("""
			SELECT id FROM link
			WHERE href = :href AND text = :text AND type = :type
			"""
		);

		final var id = select
			.on(
				value("href", link.getHref()),
				value("text", link.getText()),
				value("type", link.getType()))
			.as(RowParser.int64(1).singleNull(), conn);

		return 1L;
	}

	public static Long insert(final Link link, final Connection conn)
		throws SQLException
	{
		return link != null
			? INSERT
				.on(link, DCTOR)
				.executeInsert(conn)
				.orElseThrow()
			: null;
	}

}
