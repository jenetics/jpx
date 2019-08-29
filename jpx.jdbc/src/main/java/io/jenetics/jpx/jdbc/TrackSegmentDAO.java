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

import static java.util.stream.Collectors.toMap;
/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
//public class TrackSegmentDAO extends DAO {
//
//
//
//	private static final io.jenetics.jpx.jdbc.internal.db.RowParser<Stored<Integer>> RowParser = rs -> Stored.of(
//		rs.getLong("id"),
//		rs.getInt("number")
//	);
//
//	public TrackSegmentDAO(final Connection conn) {
//		super(conn);
//	}
//
//
//	/* *************************************************************************
//	 * SELECT queries
//	 **************************************************************************/
//
//	public List<Stored<TrackSegment>> select() throws SQLException {
//		final String query = "SELECT id FROM track_segment";
//
//		final List<Stored<Integer>> rows = SQL(query).as(RowParser.list());
//
//		return selectByID(map(rows, Stored::id));
//	}
//
//	public List<Stored<TrackSegment>> selectByID(final Collection<Long> ids)
//		throws SQLException
//	{
//		final String query = "SELECT id FROM track_segment WHERE id IN ({ids})";
//
//		final List<Stored<Integer>> rows = SQL(query)
//			.on(Param.values("ids", ids))
//			.as(RowParser.list());
//
//		final Map<Long, Integer> numbers = rows.stream()
//			.collect(toMap(Stored::id, Stored::value, (a, b) -> b));
//
//		final Map<Long, List<WayPoint>> segments = with(TrackSegmentWayPointDAO::new)
//			.selectWayPointsByTrackSegmentID(ids);
//
//		return segments.keySet().stream()
//			.map(id -> Stored.of(id, TrackSegment.of(segments.get(id))))
//			.sorted(Comparator.comparingInt(a -> numbers.get(a.id())))
//			.collect(Collectors.toList());
//	}
//
//	/* *************************************************************************
//	 * INSERT queries
//	 **************************************************************************/
//
//	public List<Stored<TrackSegment>> insert(final List<TrackSegment> segments)
//		throws SQLException
//	{
//		final String query = "INSERT INTO track_segment(number) VALUES({number})";
//
//		final AtomicInteger number = new AtomicInteger(0);
//		final List<Stored<TrackSegment>> rows =
//			Batch(query).insert(segments, segment -> singletonList(
//				Param.value("number", number.getAndIncrement())
//			));
//
//		final List<Pair<Long, Long>> segmentPoints = new ArrayList<>();
//		for (Stored<TrackSegment> segment : rows) {
//			final List<Stored<WayPoint>> points = with(WayPointDAO::new)
//				.insert(segment.value().getPoints());
//
//			for (Stored<WayPoint> point : points) {
//				segmentPoints.add(Pair.of(segment.id(), point.id()));
//			}
//		}
//
//		with(TrackSegmentWayPointDAO::new).insert(segmentPoints);
//
//		return rows;
//	}
//
//}
