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
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Some commonly usable way-point filter methods.
 *
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

	static List<Track> splitByDay(final Track track) {
		return splitWayPointsByDay(
			track.segments()
				.flatMap(TrackSegment::points))
			.stream()
			.map(TrackSegment::of)
			.map(segment -> Track.builder().addSegment(segment).build())
			.collect(toList());
	}

	private static List<List<WayPoint>> splitWayPointsByDay(
		final Stream<WayPoint> points
	) {
		final Map<LocalDate, List<WayPoint>> parts = points
			.collect(groupingBy(wp -> wp.getTime()
				.map(ZonedDateTime::toLocalDate)
				.orElse(LocalDate.MIN)));

		return parts.entrySet().stream()
			.sorted(Comparator.comparing(Entry::getKey))
			.map(Entry::getValue)
			.collect(Collectors.toList());
	}

	static List<TrackSegment> splitByDay(final TrackSegment segment) {
		return splitWayPointsByDay(segment.points()).stream()
			.map(TrackSegment::of)
			.collect(Collectors.toList());
	}

}
