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

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static io.jenetics.jpx.jdbc.internal.util.Lists.map;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import io.jenetics.jpx.Link;
import io.jenetics.jpx.jdbc.internal.db.Column;
import io.jenetics.jpx.jdbc.internal.db.DAO;
import io.jenetics.jpx.jdbc.internal.db.Delete;
import io.jenetics.jpx.jdbc.internal.db.Inserter;
import io.jenetics.jpx.jdbc.internal.db.Param;
import io.jenetics.jpx.jdbc.internal.db.Stored;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public class RouteLinkDAO
	extends DAO
	implements
	Inserter<RouteLink>,
	Delete
{

	public RouteLinkDAO(final Connection conn) {
		super(conn);
	}

	private static final io.jenetics.jpx.jdbc.internal.db.RowParser<Stored<RouteLink>> RowParser = rs -> Stored.of(
		rs.getLong("route_id"),
		RouteLink.of(
			rs.getLong("route_id"),
			rs.getLong("link_id")
		)
	);

	/* *************************************************************************
	 * SELECT queries
	 **************************************************************************/

	public <T> Map<Long, List<Link>> selectLinks(
		final Collection<T> values,
		final Function<T, Long> mapper
	)
		throws SQLException
	{
		final String query =
			"SELECT route_id, link_id " +
			"FROM route_link " +
			"WHERE route_id IN ({ids}) " +
			"ORDER BY link_id";

		final List<Stored<RouteLink>> rows = SQL(query)
			.on(Param.values("ids", values, mapper))
			.as(RowParser.list());

		final Map<Long, Link> links = with(LinkDAO::new)
			.selectByVals(Column.of("id", row -> row.value().getLinkID()), rows)
			.stream()
			.collect(toMap(Stored::id, Stored::value, (a, b) -> b));

		return rows.stream()
			.collect(groupingBy(
				Stored::id,
				mapping(row -> links.get(row.value().getLinkID()), toList())));
	}

	/* *************************************************************************
	 * INSERT queries
	 **************************************************************************/

	@Override
	public List<Stored<RouteLink>> insert(final Collection<RouteLink> rows)
		throws SQLException
	{
		final String query =
			"INSERT INTO route_link(route_id, link_id) " +
			"VALUES({route_id}, {link_id});";

		Batch(query).execute(rows, row -> asList(
			Param.value("route_id", row.getRouteID()),
			Param.value("link_id", row.getLinkID())
		));

		return map(rows, row -> Stored.of(row.getRouteID(), row));
	}

	/* *************************************************************************
	 * DELETE queries
	 **************************************************************************/

	@Override
	public <V, C> int deleteByVals(
		final Column<V, C> column,
		final Collection<V> values
	)
		throws SQLException
	{
		final int count;
		if (!values.isEmpty()) {
			final String query =
				"DELETE FROM route_link WHERE "+column.name()+" IN ({values})";

			count = SQL(query)
				.on(Param.values("values", values, column.mapper()))
				.execute();

		} else {
			count = 0;
		}

		return count;
	}

}
