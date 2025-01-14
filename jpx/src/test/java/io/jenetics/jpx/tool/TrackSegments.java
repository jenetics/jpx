package io.jenetics.jpx.tool;

import static java.lang.String.format;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.jenetics.jpx.GPX;
import io.jenetics.jpx.Track;
import io.jenetics.jpx.TrackSegment;
import io.jenetics.jpx.WayPoint;

public final class TrackSegments {
	private TrackSegments() {
	}

	/**
	 * Return a new collector, which collects a given way point list into a list
	 * of {@link TrackSegment}s. All way points without a timestamp are filtered
	 * out. A new segment is created if the timestamp of two consecutive points
	 * are greater then the give {@code maxGap} duration. Each segment will
	 * contain at least {@code minSegmentSize} points.
	 *
	 * @param maxGap the maximal allowed gap between two points within a
	 *        track segment. If two points exceed the given gap, a new segment
	 *        is created.
	 * @param minSegmentSize the minimal number of way points a segment must
	 *        consist
	 * @return a new track segment collector
	 * @throws NullPointerException if the given {@code maxGap} is {@code null}
	 * @throws IllegalArgumentException if the {@code maxGap} or
	 *         {@code minSegmentSize} is negative
	 */
	public static Collector<WayPoint, ?, List<TrackSegment>>
	toTrackSegments(final Duration maxGap, final int minSegmentSize) {
		if (maxGap.isNegative()) {
			throw new IllegalArgumentException(format(
				"The maximal allowed point gap must not be negative: %s",
				maxGap
			));
		}
		if (minSegmentSize < 1) {
			throw new IllegalArgumentException(format(
				"The minimal track segment size must be greater 0, but was %d.",
				minSegmentSize
			));
		}

		return Collectors.collectingAndThen(
			Collectors.toList(),
			points -> toTrackSegments(points, maxGap, minSegmentSize)
		);
	}

	private static List<TrackSegment> toTrackSegments(
		final List<WayPoint> points,
		final Duration gap,
		final int minSegmentSize
	) {
		final List<WayPoint> wps = points.stream()
			.filter(wp -> wp.getTime().isPresent())
			.toList();

		if (wps.size() < minSegmentSize) {
			return List.of();
		}

		final List<TrackSegment> segments = new ArrayList<>();
		Instant last = wps.get(0).getTime().orElseThrow();
		TrackSegment.Builder segment = TrackSegment.builder();

		for (final WayPoint point : wps) {
			final Instant zdt = point.getTime().orElseThrow();

			if (last.plusNanos(gap.toNanos()).isAfter(zdt)) {
				segment.addPoint(point);
			} else {
				if (segment.points().size() >= minSegmentSize) {
					segments.add(segment.build());
				}
				segment = TrackSegment.builder();
			}

			last = zdt;
		}

		if (segment.points().size() >= minSegmentSize) {
			segments.add(segment.build());
		}

		return segments;
	}

	public static void main(String[] args) throws IOException {
		final GPX gpx = GPX.Reader.DEFAULT.read("some_file.gpx");

		final Stream<WayPoint> points = gpx.tracks()
			.flatMap(Track::segments)
			.flatMap(TrackSegment::points);

		final List<TrackSegment> segments = points
			.collect(toTrackSegments(Duration.ofMinutes(1), 10));
	}

}
