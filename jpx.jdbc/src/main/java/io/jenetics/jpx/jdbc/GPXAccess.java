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

import static io.jenetics.facilejdbc.Dctor.field;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import io.jenetics.jpx.GPX;
import io.jenetics.jpx.Route;
import io.jenetics.jpx.Track;
import io.jenetics.jpx.WayPoint;

import io.jenetics.facilejdbc.Batch;
import io.jenetics.facilejdbc.Dctor;
import io.jenetics.facilejdbc.Query;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class GPXAccess {
	private GPXAccess() {}

	private static final Query INSERT_QUERY = Query.of(
		"INSERT INTO gpx(version, creator, metadata_id) " +
		"VALUES(:version, :creator, :metadata_id);"
	);

	private static final Dctor<GPX> DCTOR = Dctor.of(
		field("version", GPX::getVersion),
		field("creator", GPX::getCreator),
		field(
			"metadata_id",
			(g, c) -> MetadataAccess.insert(g.getMetadata().orElse(null), c)
		)
	);


	public static Long insert(final GPX gpx, final Connection conn)
		throws SQLException
	{
		if (gpx == null) return null;

		final Long id = INSERT_QUERY
			.on(gpx, DCTOR)
			.executeInsert(conn)
			.orElseThrow();

		insertWayPoints(id, gpx.getWayPoints(), conn);
		insertRoutes(id, gpx.getRoutes(), conn);
		insertTracks(id, gpx.getTracks(), conn);
		return id;
	}

	private static final Query WAY_POINT_INSERT_QUERY = Query.of(
		"INSERT INTO gpx_way_point(gpx_id, way_point_id) " +
		"VALUES(:gpx_id, :way_point_id);"
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
				field("gpx_id", r -> id),
				field("way_point_id", WayPointAccess::insert)
			)
		);

		WAY_POINT_INSERT_QUERY.executeUpdate(batch, conn);
	}

	private static final Query ROUTE_INSERT_QUERY = Query.of(
		"INSERT INTO gpx_route(gpx_id, route_id) " +
		"VALUES(:gpx_id, :route_id);"
	);

	private static void insertRoutes(
		final Long id,
		final List<Route> routes,
		final Connection conn
	)
		throws SQLException
	{
		final Batch batch = Batch.of(
			routes,
			Dctor.of(
				field("gpx_id", r -> id),
				field("route_id", RouteAccess::insert)
			)
		);

		ROUTE_INSERT_QUERY.executeUpdate(batch, conn);
	}

	private static final Query TRACK_INSERT_QUERY = Query.of(
		"INSERT INTO gpx_track(gpx_id, track_id) " +
		"VALUES(:gpx_id, :track_id);"
	);

	private static void insertTracks(
		final Long id,
		final List<Track> tracks,
		final Connection conn
	)
		throws SQLException
	{
		final Batch batch = Batch.of(
			tracks,
			Dctor.of(
				field("gpx_id", r ->id),
				field("track_id", TrackAccess::insert)
			)
		);

		TRACK_INSERT_QUERY.executeUpdate(batch, conn);
	}

}
