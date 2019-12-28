package io.jenetics.jpx.tool;

import static java.lang.String.format;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import io.jenetics.jpx.Track;
import io.jenetics.jpx.TrackSegment;
import io.jenetics.jpx.WayPoint;

public final class Tracks {
	private Tracks() {
	}

	/**
	 * Return a new collector, which collects a given way point list into an
	 * optional {@link Track}. All way points without a timestamp are filtered
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
	public static Collector<WayPoint, ?, Optional<Track>>
	toTrack(final Duration maxGap, final int minSegmentSize) {
		return Collectors.collectingAndThen(
			TrackSegments.toTrackSegments(maxGap, minSegmentSize),
			Tracks::toTrack
		);
	}

	private static Optional<Track> toTrack(final List<TrackSegment> segments) {
		if (segments.isEmpty()) {
			return Optional.empty();
		}

		final Track.Builder track = Track.builder()
			.number(1)
			.name("Track 1")
			.cmt(trackCmt(segments))
			.segments(segments);

		track.desc(format(
			"%d segments; %d track points",
			track.segments().size(),
			track.segments().stream()
				.flatMap(TrackSegment::points)
				.count()
		));

		return Optional.of(track.build());
	}

	private static String trackCmt(final List<TrackSegment> segments) {
		final List<WayPoint> points = segments.stream()
			.flatMap(TrackSegment::points)
			.collect(Collectors.toList());

		final OffsetDateTime start = points.get(0).getTime()
			.map(ZonedDateTime::toOffsetDateTime)
			.orElseThrow();

		final OffsetDateTime end = points.get(points.size() - 1).getTime()
			.map(ZonedDateTime::toOffsetDateTime)
			.orElseThrow();

		return format(
			"Track[start=%s, end=%s, duration=%s]",
			start, end, Duration.between(start, end)
		);
	}

}
