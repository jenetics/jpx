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
import static java.util.stream.Collectors.toMap;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import io.jenetics.jpx.WayPoint;
import io.jenetics.jpx.jdbc.internal.db.DAO;
import io.jenetics.jpx.jdbc.internal.querily.Param;
import io.jenetics.jpx.jdbc.internal.util.Pair;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
//public class TrackSegmentWayPointDAO extends DAO {
//
//	/**
//	 * Represents a row in the "route_way_point" table.
//	 */
//	private static final class Row {
//		final Long trackSegmentID;
//		final Long wayPointID;
//
//		Row(final Long trackSegmentID, final Long wayPointID) {
//			this.trackSegmentID = trackSegmentID;
//			this.wayPointID = wayPointID;
//		}
//
//		Long trackSegmentID() {
//			return trackSegmentID;
//		}
//
//		Long wayPointID() {
//			return wayPointID;
//		}
//	}
//
//	public TrackSegmentWayPointDAO(final Connection conn) {
//		super(conn);
//	}
//
//	private static final io.jenetics.jpx.jdbc.internal.querily.RowParser<Row> RowParser = rs -> new Row(
//		rs.getLong("track_segment_id"),
//		rs.getLong("way_point_id")
//	);
//
//	/* *************************************************************************
//	 * SELECT queries
//	 **************************************************************************/
//
//	public Map<Long, List<WayPoint>> selectWayPointsByTrackSegmentID(final Collection<Long> ids)
//		throws SQLException
//	{
//		/*
//		final String query =
//			"SELECT track_segment_id, way_point_id " +
//			"FROM track_segment_way_point " +
//			"WHERE track_segment_id IN ({ids})";
//
//		final List<Row> rows = SQL(query)
//			.on(Param.values("ids", ids))
//			.as(RowParser.list());
//
//		final Map<Long, WayPoint> points = with(WayPointDAO::new)
//			.selectByID(map(rows, Row::wayPointID)).stream()
//			.collect(toMap(Stored::id, Stored::value, (a, b) -> b));
//
//		return rows.stream()
//			.map(row -> Pair.of(row.trackSegmentID, points.get(row.wayPointID)))
//			.collect(groupingBy(Pair::_1, mapping(Pair::_2, toList())));
//			*/
//		return null;
//	}
//
//	/* *************************************************************************
//	 * INSERT queries
//	 **************************************************************************/
//
//	public List<Pair<Long, Long>> insert(final List<Pair<Long, Long>> trackSegmentWayPoints)
//		throws SQLException
//	{
//		final String query =
//			"INSERT INTO track_segment_way_point(track_segment_id, way_point_id) " +
//			"VALUES({track_segment_id}, {way_point_id});";
//
//		Batch(query).execute(trackSegmentWayPoints, point -> asList(
//			Param.of("track_segment_id", point._1),
//			Param.of("way_point_id", point._2)
//		));
//
//		return trackSegmentWayPoints;
//	}
//
//}
