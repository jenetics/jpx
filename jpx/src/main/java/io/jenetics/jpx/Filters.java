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
package io.jenetics.jpx;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.Collections.unmodifiableList;
import static java.util.stream.Collectors.groupingBy;

import java.time.Duration;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class Filters {

	private Filters() {
	}

	/**
	 * Merges the given segments into one segment containing all way-points. The
	 * order of the way-points is preserved.
	 * <pre>{@code
	 * final GPX merged = gpx.toBuilder()
	 *     .trackFilter()
	 *         .map(track -> track.toBuilder()
	 *             .listMap(Filters::mergeSegments)
	 *             .filter(TrackSegment::nonEmpty)
	 *             .build())
	 *         .build()
	 *     .build();
	 * }</pre>
	 *
	 * @param segments the segment list to merge
	 * @return a list with one segment, containing all way-points of the original
	 *         segments
	 * @throws NullPointerException if the given segment list is {@code null}
	 */
	public static List<TrackSegment> mergeSegments(
		final List<TrackSegment> segments
	) {
		final List<WayPoint> points = segments.stream()
			.flatMap(TrackSegment::points)
			.collect(Collectors.toList());

		return Collections.singletonList(TrackSegment.of(points));
	}

	/**
	 * Merges the given tracks into one track containing all segments. The order
	 * of the segments is preserved.
	 * <pre>{@code
	 * final GPX merged = gpx.toBuilder()
	 *     .trackFilter()
	 *         .listMap(Filters::mergeTracks)
	 *         .filter(Track::nonEmpty)
	 *         .build())
	 *     .build();
	 * }</pre>
	 *
	 * @param tracks the track list to merge
	 * @return a list with one track, containing all segments
	 * @throws NullPointerException if the given track list is {@code null}
	 */
	public static List<Track> mergeTracks(final List<Track> tracks) {
		final List<TrackSegment> segments = tracks.stream()
			.flatMap(Track::segments)
			.collect(Collectors.toList());

		return tracks.isEmpty()
			? emptyList()
			: singletonList(
				tracks.get(0).toBuilder()
					.segments(segments)
					.build()
				);
	}

	/**
	 * Merges all way-points of all segments of the given track list into one
	 * track with one segment, containing all way-points. The order of the
	 * way-points is preserved.
	 * <pre>{@code
	 * final GPX merged = gpx.toBuilder()
	 *     .trackFilter()
	 *         .listMap(Filters::fullyMergeTracks)
	 *         .build())
	 *     .build();
	 * }</pre>
	 *
	 *
	 * @param tracks the track list to merge
	 * @return a list with one track, containing one segment with all way-points
	 * @throws NullPointerException if the given track list is {@code null}
	 */
	public static List<Track> fullyMergeTracks(final List<Track> tracks) {
		final List<WayPoint> points = tracks.stream()
			.flatMap(Track::segments)
			.flatMap(TrackSegment::points)
			.collect(Collectors.toList());

		return tracks.isEmpty()
			? emptyList()
			: singletonList(
					tracks.get(0).toBuilder()
						.segments(singletonList(TrackSegment.of(points)))
						.build()
				);
	}








	public static void main(final String[] args) {
		final GPX gpx = GPX.builder().build();

		final Track track = Track.builder().build();

		track.toBuilder()
			.listMap(Filters::mergeSegments)
			.build();

		final Track track1 = track.toBuilder()
			.map(segment -> segment.toBuilder()
				.map(wp -> wp.toBuilder()
					.time(wp.getTime().map(t -> t.plusDays(2)).orElse(null))
					.cmt("foo")
					.build())
				.build())
			.build();

		track.toBuilder()
//			.flatMap(merge())
			.build();


		gpx.toBuilder()
			.trackFilter()
				.listMap(Filters::merge)
				.listMap(Filters::splitByDay).build()
			.routeFilter()
				.map(route -> route).build()
			.build();
	}

	public static Function<WayPoint, WayPoint> addDuration(final Duration duration) {
		return null;
	}


	public static List<Track> merge(final List<Track> tracks) {
		/*
		tracks.stream()
			.map(track -> track.toBuilder()
				.map(segment -> segment.toBuilder()
					.map(wp -> wp.lat(wp.lat()))
					.build())
				.build())
			.collect(Collectors.toList());

		tracks.stream()
			.flatMap(Track::segments)
			.flatMap(TrackSegment::points);
		*/
		return null;
	}

	public static List<Track> splitByDay(final List<Track> tracks) {
		return null;
	}

	private static final class MergeTrackSegments
		implements Function<TrackSegment, Stream<TrackSegment>>
	{
		@Override
		public Stream<TrackSegment> apply(final TrackSegment wayPoints) {
			return null;
		}
	}

	public static Function<TrackSegment, Stream<TrackSegment>> merge() {
		return null;
	}

	public static Stream<TrackSegment> split(final TrackSegment segment) {
		final Map<LocalDate, List<WayPoint>> parts = segment.points()
			.collect(groupingBy(wp -> wp.getTime()
				.map(ZonedDateTime::toLocalDate)
				.orElse(LocalDate.MIN)));

		return parts.values().stream()
			.map(TrackSegment::of);
	}

	public static Track mergeTrackSegments(final Track track) {
		final List<WayPoint> points = track.segments()
			.flatMap(TrackSegment::points)
			.collect(Collectors.toList());

		return track.toBuilder()
			.segments(Collections.singletonList(TrackSegment.of(points)))
			.build();
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
