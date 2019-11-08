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

import io.jenetics.facilejdbc.Batch;
import io.jenetics.facilejdbc.Dctor;
import io.jenetics.facilejdbc.Query;
import io.jenetics.jpx.TrackSegment;
import io.jenetics.jpx.WayPoint;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import static io.jenetics.facilejdbc.Dctor.field;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class TrackSegmentAccess {
	private TrackSegmentAccess() {}

	private static final Query INSERT_QUERY = Query.of(
		"INSERT INTO track_segment(number) VALUES(:number)"
	);


	public static Long insert(
		final TrackSegment segment,
		final int number,
		final Connection conn
	)
		throws SQLException
	{
		if (segment == null || segment.isEmpty()) return null;

		final Long id = INSERT_QUERY
			.on(segment, Dctor.of(field("number", r -> number)))
			.executeInsert(conn)
			.orElseThrow();

		insertWayPoints(id, segment.getPoints(), conn);
		return id;
	}

	private static final Query WAY_POINT_INSERT_QUERY = Query.of(
		"INSERT INTO track_segment_way_point(track_segment_id, way_point_id) " +
		"VALUES(:track_segment_id, :way_point_id);"
	);

	private static void insertWayPoints(
		final Long id,
		final List<WayPoint> points,
		final Connection conn
	)
		throws SQLException
	{
		final Batch batch = Batch.of(
			points,
			Dctor.of(
				field("track_segment_id", r -> id),
				field("way_point_id", WayPointAccess::insert)
			)
		);

		WAY_POINT_INSERT_QUERY.executeUpdate(batch, conn);
	}

}
