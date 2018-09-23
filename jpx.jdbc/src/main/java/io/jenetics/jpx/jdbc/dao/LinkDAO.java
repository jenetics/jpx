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

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toMap;
import static io.jenetics.jpx.jdbc.internal.util.Lists.map;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import io.jenetics.jpx.Link;
import io.jenetics.jpx.jdbc.internal.db.Column;
import io.jenetics.jpx.jdbc.internal.db.DAO;
import io.jenetics.jpx.jdbc.internal.db.Param;
import io.jenetics.jpx.jdbc.internal.db.RowParser;
import io.jenetics.jpx.jdbc.internal.db.Selector;
import io.jenetics.jpx.jdbc.internal.db.Stored;
import io.jenetics.jpx.jdbc.model.LinkRow;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class LinkDAO
	extends DAO
	implements Selector<LinkRow>
{

	public LinkDAO(Connection conn) {
		super(conn);
	}

	private static final RowParser<LinkRow> RowParser = rs -> LinkRow.of(
		rs.getLong("id"),
		rs.getString("href"),
		rs.getString("text"),
		rs.getString("type")
	);


	/* *************************************************************************
	 * SELECT queries
	 **************************************************************************/

	/**
	 * Select all available links.
	 *
	 * @return all stored links
	 * @throws SQLException if the operation fails
	 * @throws NullPointerException if one of the arguments is {@code null}
	 */
	public List<LinkRow> select() throws SQLException {
		final String query =
			"SELECT id, href, text, type FROM link ORDER BY id";

		return SQL(query).as(RowParser.list());
	}

	@Override
	public <V, C> List<LinkRow> select(
		final Column<V, C> column,
		final Collection<V> values
	)
		throws SQLException
	{
		final String query =
			"SELECT id, href, text, type " +
			"FROM link WHERE "+column.name()+" IN ({values}) " +
			"ORDER BY id";

		return values.isEmpty()
			? Collections.emptyList()
			: SQL(query)
				.on(Param.values("values", values, column.mapper()))
				.as(RowParser.list());
	}

	public <T> void fill(
		final Collection<T> rows,
		final Function<T, LinkRow> mapper
	)
		throws SQLException
	{
		final Column<T, Long> col = Column.of("id", row -> mapper.apply(row).id);

		final Map<Long, LinkRow> links = select(col, rows).stream()
			.collect(toMap(l -> l.id, l -> l, (a, b) -> b));

		rows.stream()
			.map(mapper)
			.forEach(row -> row.fill(links.get(row.id)));
	}

	public void fill(final Collection<LinkRow> rows) throws SQLException {
		fill(rows, Function.identity());
	}

	/* *************************************************************************
	 * INSERT queries
	 **************************************************************************/

	public List<LinkRow> insert(final Collection<Link> links)
		throws SQLException
	{
		final String query =
			"INSERT INTO link(href, text, type) " +
			"VALUES({href}, {text}, {type});";

		final List<Stored<Link>> rows = Batch(query).insert(links, link -> asList(
			Param.value("href", link.getHref()),
			Param.value("text", link.getText()),
			Param.value("type", link.getType())
		));

		return map(rows, LinkRow::of);
	}

	/* *************************************************************************
	 * UPDATE queries
	 **************************************************************************/

	public List<LinkRow> update(final Collection<LinkRow> links)
		throws SQLException
	{
		final String query =
			"UPDATE link SET href = {href}, text = {text}, type = {type} " +
			"WHERE id = {id}";

		Batch(query).update(links, link -> asList(
			Param.value("id", link.id),
			Param.value("href", link.href),
			Param.value("text", link.text),
			Param.value("type", link.type)
		));

		return new ArrayList<>(links);
	}

}
