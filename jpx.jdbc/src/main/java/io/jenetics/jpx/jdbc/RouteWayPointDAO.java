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

import io.jenetics.jpx.WayPoint;
import io.jenetics.jpx.jdbc.internal.db.Column;
import io.jenetics.jpx.jdbc.internal.db.DAO;
import io.jenetics.jpx.jdbc.internal.db.Delete;
import io.jenetics.jpx.jdbc.internal.db.Inserter;
import io.jenetics.jpx.jdbc.internal.db.Param;
import io.jenetics.jpx.jdbc.internal.db.SelectBy;
import io.jenetics.jpx.jdbc.internal.db.Stored;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
//public class RouteWayPointDAO
//	extends DAO
//	implements
//	SelectBy<RouteWayPoint>,
//	Inserter<RouteWayPoint>,
//	Delete
//{
//
//	public RouteWayPointDAO(final Connection conn) {
//		super(conn);
//	}
//
//	private static final io.jenetics.jpx.jdbc.internal.db.RowParser<Stored<RouteWayPoint>> RowParser = rs -> Stored.of(
//		rs.getLong("route_id"),
//		RouteWayPoint.of(
//			rs.getLong("route_id"),
//			rs.getLong("way_point_id")
//		)
//	);
//
//	/* *************************************************************************
//	 * SELECT queries
//	 **************************************************************************/
//
//	@Override
//	public <V, C> List<Stored<RouteWayPoint>> selectByVals(
//		final Column<V, C> column,
//		final Collection<V> values
//	)
//		throws SQLException
//	{
//		final String query =
//			"SELECT route_id, way_point_id " +
//			"FROM route_way_point " +
//			"WHERE "+column.name()+" IN ({values}) " +
//			"ORDER BY way_point_id";
//
//		return SQL(query)
//			.on(Param.values("values", values, column.mapper()))
//			.as(RowParser.list());
//	}
//
//	public <T> Map<Long, List<WayPoint>> selectWayPoints(
//		final Collection<T> values,
//		final Function<T, Long> mapper
//	)
//		throws SQLException
//	{
//		final List<Stored<RouteWayPoint>> rows =
//			selectByVals(Column.of("route_id", mapper), values);
//
//		final Map<Long, WayPoint> links = with(WayPointDAO::new)
//			.selectByVals(Column.of("id", row -> row.value().getWayPointUD()), rows)
//			.stream()
//			.collect(toMap(Stored::id, Stored::value, (a, b) -> b));
//
//		return rows.stream()
//			.collect(groupingBy(
//				Stored::id,
//				mapping(row -> links.get(row.value().getWayPointUD()), toList())));
//	}
//
//	/* *************************************************************************
//	 * INSERT queries
//	 **************************************************************************/
//
//	@Override
//	public List<Stored<RouteWayPoint>> insert(final Collection<RouteWayPoint> rows)
//		throws SQLException
//	{
//		final String query =
//			"INSERT INTO route_way_point(route_id, way_point_id) " +
//			"VALUES({route_id}, {way_point_id});";
//
//		Batch(query).execute(rows, row -> asList(
//			Param.value("route_id", row.getRouteID()),
//			Param.value("way_point_id", row.getWayPointUD())
//		));
//
//		return map(rows, row -> Stored.of(row.getRouteID(), row));
//	}
//
//	/* *************************************************************************
//	 * DELETE queries
//	 **************************************************************************/
//
//	@Override
//	public <V, C> int deleteByVals(
//		final Column<V, C> column,
//		final Collection<V> values
//	)
//		throws SQLException
//	{
//		final int count;
//		if (!values.isEmpty()) {
//			final String query =
//				"DELETE FROM route_way_point WHERE "+column.name()+" IN ({values})";
//
//			count = SQL(query)
//				.on(Param.values("values", values, column.mapper()))
//				.execute();
//
//		} else {
//			count = 0;
//		}
//
//		return count;
//	}
//
//}
