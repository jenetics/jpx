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
package io.jenetics.jpx.jdbc;

import static io.jenetics.facilejdbc.Dctor.field;
import static io.jenetics.facilejdbc.Param.value;

import java.net.URI;
import java.sql.Connection;
import java.sql.SQLException;

import io.jenetics.jpx.Link;

import io.jenetics.facilejdbc.Dctor;
import io.jenetics.facilejdbc.Query;
import io.jenetics.facilejdbc.RowParser;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class LinkAccess {
	private LinkAccess() {}

	private static final Query SELECT = Query.of(
		"SELECT id, href, text, type " +
		"FROM link " +
		"WHERE id = :id;"
	);

	private static final Query INSERT = Query.of(
		"INSERT INTO link(href, text, type) " +
		"VALUES(:href, :text, :type);"
	);

	private static final RowParser<Link> PARSER = (row, conn) -> Link.of(
		URI.create(row.getString("href")),
		row.getString("text"),
		row.getString("type")
	);

	private static final Dctor<Link> DCTOR = Dctor.of(
		field("href", Link::getHref),
		field("text", Link::getText),
		field("type", Link::getType)
	);

	public static Link selectById(final Long id, final Connection conn)
		throws SQLException
	{
		return id != null
			? SELECT
				.on(value("id", id))
				.as(PARSER.singleNull(), conn)
			: null;
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
