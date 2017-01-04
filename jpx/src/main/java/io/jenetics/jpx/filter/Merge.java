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
public class Merge {

	private Merge() {
	}

	public static GPX merge(final GPX gpx) {
		return null;
	}

	public static Route mergeRoutes(final List<Route> routes) {
		final List<WayPoint> points = routes.stream()
			.flatMap(Route::points)
			.collect(Collectors.toList());

		final Route.Builder builder = routes.isEmpty()
			? Route.builder()
			: routes.get(0).toBuilder();

		return builder
			.points(unmodifiableList(points))
			.build();
	}

	public static Track mergeTracks(final List<Track> tracks) {
		final List<TrackSegment> segments = tracks.stream()
			.flatMap(Track::segments)
			.collect(Collectors.toList());

		final Track.Builder builder = tracks.isEmpty()
			? Track.builder()
			: tracks.get(0).toBuilder();

		return builder
			.segments(unmodifiableList(segments))
			.build();
	}

	public static TrackSegment mergeTrackSegments(final List<TrackSegment> segments) {
		final List<WayPoint> points = segments.stream()
			.flatMap(TrackSegment::points)
			.collect(Collectors.toList());

		return TrackSegment.of(unmodifiableList(points));
	}

}
