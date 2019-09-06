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

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import io.jenetics.jpx.TrackSegment;
import io.jenetics.jpx.WayPoint;
import io.jenetics.jpx.jdbc.internal.querily.Dctor;
import io.jenetics.jpx.jdbc.internal.querily.Dctor.Field;
import io.jenetics.jpx.jdbc.internal.querily.Query;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class TrackSegmentAccess {
	private TrackSegmentAccess() {}

	private static final Query INSERT_QUERY = Query.of(
		"INSERT INTO track_segment(number) VALUES({number})"
	);


	public static Long insert(
		final TrackSegment segment,
		final int number,
		final Connection conn
	)
		throws SQLException
	{
		if (segment == null || segment.isEmpty()) return null;

		final Long id = INSERT_QUERY.insert(
			segment,
			Dctor.of(Field.ofValue("number", number)),
			conn
		);
		insertWayPoints(id, segment.getPoints(), conn);

		return id;
	}

	private static final Query WAY_POINT_INSERT_QUERY = Query.of(
		"INSERT INTO track_segment_way_point(track_segment_id, way_point_id) " +
		"VALUES({track_segment_id}, {way_point_id});"
	);

	private static void insertWayPoints(
		final Long id,
		final List<WayPoint> points,
		final Connection conn
	)
		throws SQLException
	{
		WAY_POINT_INSERT_QUERY.inserts(
			points,
			Dctor.of(
				Field.ofValue("track_segment_id", id),
				Field.of("way_point_id", WayPointAccess::insert)
			),
			conn
		);
	}

}
