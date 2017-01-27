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
package io.jenetics.jpx.filter;

import static java.util.Collections.unmodifiableList;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import io.jenetics.jpx.GPX;
import io.jenetics.jpx.Route;
import io.jenetics.jpx.Track;
import io.jenetics.jpx.TrackSegment;
import io.jenetics.jpx.WayPoint;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class Filters {

	private Filters() {
	}

	public static GPX filter(final GPX gpx, final Predicate<? super WayPoint> filter) {
		final List<WayPoint> wayPoints = gpx.wayPoints()
			.filter(filter)
			.collect(Collectors.toList());

		final List<Route> routes = gpx.routes()
			.map(route -> filter(route, filter))
			.collect(Collectors.toList());

		final List<Track> tracks = gpx.tracks()
			.map(track -> filter(track, filter))
			.collect(Collectors.toList());

		return gpx.toBuilder()
			.wayPoints(unmodifiableList(wayPoints))
			.routes(unmodifiableList(routes))
			.tracks(unmodifiableList(tracks))
			.build();
	}

	private static Route filter(final Route route, final Predicate<? super WayPoint> filter) {
		final List<WayPoint> points = route.points()
			.filter(filter)
			.collect(Collectors.toList());

		return route.toBuilder()
			.points(unmodifiableList(points))
			.build();
	}

	private static Track filter(final Track track, final Predicate<? super WayPoint> filter) {
		final List<TrackSegment> segments = track.segments()
			.map(segment -> filter(segment, filter))
			.collect(Collectors.toList());

		return track.toBuilder()
			.segments(unmodifiableList(segments))
			.build();
	}

	private static TrackSegment filter(final TrackSegment segment, final Predicate<? super WayPoint> filter) {
		return TrackSegment.of(unmodifiableList(
			segment.points()
				.filter(filter)
				.collect(Collectors.toList())
		));
	}

}
