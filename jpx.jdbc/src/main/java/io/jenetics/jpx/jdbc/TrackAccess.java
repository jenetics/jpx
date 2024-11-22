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
import static io.jenetics.facilejdbc.Param.value;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import io.jenetics.jpx.Link;
import io.jenetics.jpx.Track;
import io.jenetics.jpx.TrackSegment;
import io.jenetics.jpx.UInt;

import io.jenetics.facilejdbc.Batch;
import io.jenetics.facilejdbc.Dctor;
import io.jenetics.facilejdbc.Query;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class TrackAccess {
	private TrackAccess() {}

	private static final Query INSERT_QUERY = Query.of(
		"INSERT INTO track(name, cmt, dscr, src, number, type) " +
		"VALUES(:name, :cmt, :dscr, :src, :number, :type)"
	);

	private static final Dctor<Track> DCTOR = Dctor.of(
		field("name", Track::getName),
		field("cmt", Track::getComment),
		field("dscr", Track::getDescription),
		field("src", Track::getSource),
		field("number", t -> t.getNumber().map(UInt::getValue)),
		field("type", Track::getType)
	);

	public static Long insert(final Track track, final Connection conn)
		throws SQLException
	{
		if (track == null || track.isEmpty()) return null;

		final Long id = INSERT_QUERY
			.on(track, DCTOR)
			.executeInsert(conn)
			.orElseThrow();

		insertLinks(id, track.getLinks(), conn);
		insertSegments(id, track.getSegments(), conn);
		return id;
	}

	private static final Query LINK_INSERT_QUERY = Query.of(
		"INSERT INTO track_link(track_id, link_id) " +
		"VALUES(:track_id, :link_id);"
	);

	private static void insertLinks(
		final Long id,
		final List<Link> links,
		final Connection conn
	)
		throws SQLException
	{
		final Batch batch = Batch.of(
			links,
			Dctor.of(
				field("track_id", r -> id),
				field("link_id", LinkAccess::insert)
			)
		);

		LINK_INSERT_QUERY.executeUpdate(batch, conn);
	}

	private static final Query SEGMENT_INSERT_QUERY = Query.of(
		"INSERT INTO track_track_segment(track_id, track_segment_id) " +
		"VALUES(:track_id, :track_segment_id);"
	);

	private static void insertSegments(
		final Long id,
		final List<TrackSegment> segments,
		final Connection conn
	)
		throws SQLException
	{
		for (int i = 0; i < segments.size(); ++i) {
			final TrackSegment segment = segments.get(i);
			final Long sid = TrackSegmentAccess.insert(segment, i, conn);

			if (sid != null) {
				SEGMENT_INSERT_QUERY
					.on(
						value("track_id", id),
						value("track_segment_id", sid))
					.executeUpdate(conn);
			}
		}
	}

}
