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

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import io.jenetics.jpx.Link;
import io.jenetics.jpx.Route;
import io.jenetics.jpx.UInt;
import io.jenetics.jpx.WayPoint;
import io.jenetics.jpx.jdbc.internal.db.Column;
import io.jenetics.jpx.jdbc.internal.db.DAO;
import io.jenetics.jpx.jdbc.internal.db.Delete;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public class RouteDAO
	extends DAO
	implements
		SelectBy<Route>,
		Insert<Route>,
		Update<Route>,
	Delete
{

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
				"type " +
			"FROM route";

		final List<Stored<Row>> rows = SQL(query).as(RowParser.list());
		return toRoute(rows);
	}

	private List<Stored<Route>> toRoute(final List<Stored<Row>> rows)
		throws SQLException
	{
		final Map<Long, List<Link>> links = with(RouteLinkDAO::new)
			.selectLinks(rows, Stored::id);

		final Map<Long, List<WayPoint>> points = with(RouteWayPointDAO::new)
			.selectWayPoints(rows, Stored::id);

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

	@Override
	public <V, C> List<Stored<Route>> selectByVals(
		final Column<V, C> column,
		final Collection<V> values
	)
		throws SQLException
	{
		return toRoute(selectRowsByVal(column, values));
	}

	public <V, C> List<Stored<Row>> selectRowsByVal(
		final Column<V, C> column,
		final Collection<V> values
	)
		throws SQLException
	{
		final String query =
			"SELECT id, " +
				"name, " +
				"cmt, " +
				"desc, " +
				"src, " +
				"number, " +
				"type " +
			"FROM route " +
			"WHERE "+column.name()+" IN ({values}) " +
			"ORDER BY id";

		return values.isEmpty()
			? Collections.emptyList()
			: SQL(query)
				.on(Param.values("values", values, column.mapper()))
				.as(RowParser.list());
	}

	/* *************************************************************************
	 * INSERT queries
	 **************************************************************************/

	@Override
	public List<Stored<Route>> insert(final Collection<Route> routes)
		throws SQLException
	{
		final String query =
			"INSERT INTO route(" +
				"name, " +
				"cmt, " +
				"desc, " +
				"src, " +
				"number, " +
				"type" +
			") " +
			"VALUES(" +
				"{name}, " +
				"{cmt}, " +
				"{desc}, " +
				"{src}, " +
				"{number}, " +
				"{type}" +
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
			.write(routes, Route::getLinks, with(LinkDAO::new)::put);

		final List<RouteLink> routeLinks = inserted.stream()
			.flatMap(md -> md.value().getLinks().stream()
				.map(l -> RouteLink.of(md.id(), links.get(l))))
			.collect(Collectors.toList());

		with(RouteLinkDAO::new).insert(routeLinks);


		// Insert route way-points.
		final Map<WayPoint, Long> points = DAO
			.write(routes, Route::getPoints, with(WayPointDAO::new)::insert);

		final List<RouteWayPoint> routePoints = inserted.stream()
			.flatMap(md -> md.value().getPoints().stream()
				.map(point -> RouteWayPoint.of(md.id(), points.get(point))))
			.collect(Collectors.toList());

		with(RouteWayPointDAO::new).insert(routePoints);

		return inserted;
	}


	/* *************************************************************************
	 * UPDATE queries
	 **************************************************************************/

	@Override
	public List<Stored<Route>> update(
		final Collection<Stored<Route>> routes
	)
		throws SQLException
	{
		final String query =
			"UPDATE route " +
				"SET name = {name}, " +
				"cmt = {cmt}, " +
				"desc = {desc}, " +
				"src = {src}, " +
				"number = {number}, " +
				"type = {type} " +
			"WHERE id = {id}";

		// Update way-points.
		Batch(query).update(routes, route -> asList(
			Param.value("id", route.id()),
			Param.value("name", route.value().getName()),
			Param.value("cmt", route.value().getComment()),
			Param.value("desc", route.value().getDescription()),
			Param.value("src", route.value().getSource()),
			Param.value("number", route.value().getNumber()),
			Param.value("type", route.value().getType())
		));

		// Update route links.
		with(RouteLinkDAO::new)
			.deleteByVals(Column.of("route_id", Stored::id), routes);

		final Map<Link, Long> links = DAO.write(
			routes,
			(ListMapper<Stored<Route>, Link>)md -> md.value().getLinks(),
			with(LinkDAO::new)::put
		);

		final List<Pair<Long, Long>> wayPointLinks = routes.stream()
			.flatMap(md -> md.value().getLinks().stream()
				.map(l -> Pair.of(md.id(), links.get(l))))
			.collect(Collectors.toList());

		with(WayPointLinkDAO::new)
			.insert(wayPointLinks, WayPointLink::of);

		// Update route way-points.
		final List<Stored<RouteWayPoint>> wayPoints = with(RouteWayPointDAO::new)
			.selectByVals(Column.of("route_id", Stored::id), routes);

		with(RouteWayPointDAO::new)
			.deleteByVals(Column.of("route_id", Stored::id), routes);

		with(WayPointDAO::new).deleteByVals(
			Column.of("id", p -> p.value().getWayPointUD()),
			wayPoints
		);

		final Map<WayPoint, Long> points = DAO.write(
			routes,
			(ListMapper<Stored<Route>, WayPoint>)r -> r.value().getPoints(),
			with(WayPointDAO::new)::insert
		);

		final List<RouteWayPoint> routePoints = routes.stream()
			.flatMap(md -> md.value().getPoints().stream()
				.map(point -> RouteWayPoint.of(md.id(), points.get(point))))
			.collect(Collectors.toList());

		with(RouteWayPointDAO::new).insert(routePoints);

		return new ArrayList<>(routes);
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
		final List<Stored<Route>> routes = selectByVals(column, values);

		final List<Stored<RouteWayPoint>> wayPoints = with(RouteWayPointDAO::new)
			.selectByVals(Column.of("route_id", Stored::id), routes);

		final int count;
		if (!routes.isEmpty()) {
			final String query =
				"DELETE FROM route WHERE id IN ({ids})";

			count = SQL(query)
				.on(Param.values("ids", routes, Stored::id))
				.execute();
		} else {
			count = 0;
		}

		with(RouteWayPointDAO::new)
			.deleteByVals(Column.of("route_id", Stored::id), routes);

		return count;
	}

}
