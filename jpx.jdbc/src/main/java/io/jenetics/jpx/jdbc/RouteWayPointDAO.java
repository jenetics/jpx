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
import static io.jenetics.jpx.jdbc.Lists.map;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import io.jenetics.jpx.WayPoint;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public class RouteWayPointDAO extends DAO {

	/**
	 * Represents a row in the "route_way_point" table.
	 */
	private static final class Row {
		final Long routeID;
		final Long wayPointID;

		Row(final Long routeID, final Long wayPointID) {
			this.routeID = routeID;
			this.wayPointID = wayPointID;
		}

		Long routeID() {
			return routeID;
		}

		Long wayPointID() {
			return wayPointID;
		}
	}

	public RouteWayPointDAO(final Connection conn) {
		super(conn);
	}

	private static final RowParser<Row> RowParser = rs -> new Row(
		rs.getLong("route_id"),
		rs.getLong("way_point_id")
	);

	/* *************************************************************************
	 * SELECT queries
	 **************************************************************************/

	public Map<Long, List<WayPoint>> selectWayPointsByRouteID(final List<Long> ids)
		throws SQLException
	{
		/*
		final String query =
			"SELECT route_id, way_point_id " +
			"FROM route_way_point " +
			"WHERE route_id IN ({ids})";

		final List<Row> rows = SQL(query)
			.on(Param.values("ids", ids))
			.as(RowParser.list());

		final Map<Long, WayPoint> points = with(WayPointDAO::new)
			.selectByID(map(rows, Row::wayPointID)).stream()
			.collect(toMap(Stored::id, Stored::value, (a, b) -> b));

		return rows.stream()
			.map(row -> Pair.of(row.routeID, points.get(row.wayPointID)))
			.collect(groupingBy(Pair::_1, mapping(Pair::_2, toList())));
	}

	public Map<Long, List<Long>> selectWayPointIDsByRouteID(final List<Long> ids)
		throws SQLException
	{
		final String query =
			"SELECT route_id, way_point_id " +
			"FROM route_way_point " +
			"WHERE route_id IN ({ids})";

		final List<Row> rows = SQL(query)
			.on(Param.values("ids", ids))
			.as(RowParser.list());

		return rows.stream()
			.map(row -> Pair.of(row.routeID, row.wayPointID))
			.collect(groupingBy(Pair::_1, mapping(Pair::_2, toList())));
			*/

		return null;
	}

	/* *************************************************************************
	 * INSERT queries
	 **************************************************************************/

	public List<Pair<Long, Long>> insert(final List<Pair<Long, Long>> routeWayPoints)
		throws SQLException
	{
		final String query =
			"INSERT INTO route_way_point(route_id, way_point_id) " +
			"VALUES({route_id}, {way_point_id});";

		Batch(query).execute(routeWayPoints, mdl -> asList(
			Param.value("route_id", mdl._1),
			Param.value("way_point_id", mdl._2)
		));

		return routeWayPoints;
	}

}
