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
import static io.jenetics.jpx.jdbc.Lists.map;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import io.jenetics.jpx.Link;
import io.jenetics.jpx.Route;
import io.jenetics.jpx.UInt;
import io.jenetics.jpx.WayPoint;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public class RouteDAO extends DAO {


	private static final class Row {
		final String name;
		final String comment;
		final String description;
		final String source;
		final UInt number;
		final String type;

		Row(
			final String name,
			final String comment,
			final String description,
			final String source,
			final UInt number,
			final String type
		) {
			this.name = name;
			this.comment = comment;
			this.description = description;
			this.source = source;
			this.number = number;
			this.type = type;
		}
	}

	public RouteDAO(final Connection conn) {
		super(conn);
	}

	/**
	 * The link row parser which creates a {@link Link} object from a given DB
	 * row.
	 */
	private static final RowParser<Stored<Row>> RowParser = rs -> Stored.of(
		rs.getLong("id"),
		new Row(
			rs.getString("name"),
			rs.getString("cmt"),
			rs.getString("desc"),
			rs.getString("src"),
			rs.getUInt("number"),
			rs.getString("type")
		)
	);


	/* *************************************************************************
	 * SELECT queries
	 **************************************************************************/

	public List<Stored<Route>> select() throws SQLException {
		final String query =
			"SELECT id, " +
				"name, " +
				"cmt, " +
				"desc, " +
				"src, " +
				"number, " +
				"type, " +
			"FROM route";

		final List<Stored<Row>> rows = SQL(query).as(RowParser.list());
		return toRoute(rows);
	}

	private List<Stored<Route>> toRoute(final List<Stored<Row>> rows)
		throws SQLException
	{
		final List<Long> ids = map(rows, Stored::id);

		final Map<Long, List<Link>> links = with(RouteLinkDAO::new)
			.selectLinksByRouteID(ids);

		final Map<Long, List<WayPoint>> points = with(RouteWayPointDAO::new)
			.selectWayPointsByRouteID(ids);

		return rows.stream()
			.map(row -> Stored.of(
				row.id(),
				Route.of(
					row.value().name,
					row.value().comment,
					row.value().description,
					row.value().source,
					links.get(row.id()),
					row.value().number,
					row.value().type,
					points.get(row.id())
				)
			))
			.collect(Collectors.toList());
	}

	public List<Stored<Route>> selectByID(final List<Long> ids)
		throws SQLException
	{
		final String query =
			"SELECT id, " +
				"name, " +
				"cmt, " +
				"desc, " +
				"src, " +
				"number, " +
				"type, " +
			"FROM route " +
			"WHERE id IN ({ids}) " +
			"ORDER BY id ASC";

		final List<Stored<Row>> rows = SQL(query)
			.on(Param.values("ids", ids))
			.as(RowParser.list());

		return toRoute(rows);
	}

	/* *************************************************************************
	 * INSERT queries
	 **************************************************************************/

	/**
	 * Insert the given person list into the DB.
	 *
	 * @param routes the persons to insert
	 * @return return the stored persons
	 * @throws SQLException if inserting fails
	 */
	public List<Stored<Route>> insert(final List<Route> routes)
		throws SQLException
	{
		final String query =
			"INSERT INTO route(" +
				"name, " +
				"cmt, " +
				"desc, " +
				"src, " +
				"number, " +
				"type, " +
			") " +
			"VALUES(" +
				"name, " +
				"cmt, " +
				"desc, " +
				"src, " +
				"number, " +
				"type, " +
			")";

		final List<Stored<Route>> inserted =
			Batch(query).insert(routes, route -> asList(
				Param.value("name", route.getName()),
				Param.value("cmt", route.getComment()),
				Param.value("desc", route.getDescription()),
				Param.value("src", route.getSource()),
				Param.value("number", route.getNumber()),
				Param.value("type", route.getType())
			));

		// Insert route links.
		final Map<Link, Long> links = DAO
			.set(routes, Route::getLinks, with(LinkDAO::new)::put);

		final List<Pair<Long, Long>> routeLinks = inserted.stream()
			.flatMap(md -> md.value().getLinks().stream()
				.map(link -> Pair.of(md.id(), links.get(link))))
			.collect(Collectors.toList());

		with(RouteLinkDAO::new).insert(routeLinks);

		// Insert route way-points.
		final Map<WayPoint, Long> points = DAO
			.set(routes, Route::getPoints, with(WayPointDAO::new)::insert);

		final List<Pair<Long, Long>> routePoints = inserted.stream()
			.flatMap(md -> md.value().getPoints().stream()
				.map(point -> Pair.of(md.id(), points.get(point))))
			.collect(Collectors.toList());

		with(RouteWayPointDAO::new).insert(routePoints);

		return inserted;
	}

	/* *************************************************************************
	 * DELETE queries
	 **************************************************************************/

	public int deleteByID(final List<Long> ids) throws SQLException {
		final Map<Long, List<Long>> wayPointIDs = with(RouteWayPointDAO::new)
			.selectWayPointIDsByRouteID(ids);

		final int count = SQL("DELETE FROM route WHERE id IN ({ids})")
			.on(Param.values("ids", ids))
			.execute();

		with(WayPointDAO::new)
			.deleteByID(wayPointIDs.values().stream()
				.flatMap(Collection::stream)
				.collect(Collectors.toList()));

		return count;
	}

}
