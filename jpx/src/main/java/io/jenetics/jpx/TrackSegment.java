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

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import static io.jenetics.jpx.Lists.copy;
import static io.jenetics.jpx.Lists.immutable;
import static io.jenetics.jpx.XMLWriter.elem;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A Track Segment holds a list of Track Points which are logically connected in
 * order. To represent a single GPS track where GPS reception was lost, or the
 * GPS receiver was turned off, start a new Track Segment for each continuous
 * span of track data.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since 1.0
 */
public final class TrackSegment implements Iterable<WayPoint>, Serializable {

	private static final long serialVersionUID = 1L;

	private final List<WayPoint> _points;

	/**
	 * Create a new track-segment with the given points.
	 *
	 * @param points the points of the track-segment
	 */
	private TrackSegment(final List<WayPoint> points) {
		_points = immutable(points);
	}

	/**
	 * Return the track-points of this segment.
	 *
	 * @return the track-points of this segment
	 */
	public List<WayPoint> getPoints() {
		return _points;
	}

	/**
	 * Return a stream of {@link WayPoint} objects this track-segments contains.
	 *
	 * @return a stream of {@link WayPoint} objects this track-segment contains
	 */
	public Stream<WayPoint> points() {
		return _points.stream();
	}

	@Override
	public Iterator<WayPoint> iterator() {
		return _points.iterator();
	}

	/**
	 * Convert the <em>immutable</em> track-segment object into a
	 * <em>mutable</em> builder initialized with the current track-segment
	 * values.
	 *
	 * @since 1.1
	 *
	 * @return a new track-segment builder initialized with the values of
	 *        {@code this} track-segment
	 */
	public Builder toBuilder() {
		return builder().points(_points);
	}

	/**
	 * Return {@code true} if {@code this} track-segment doesn't contain any
	 * track-point.
	 *
	 * @return {@code true} if {@code this} track-segment is empty, {@code false}
	 *         otherwise
	 */
	public boolean isEmpty() {
		return _points.isEmpty();
	}

	/**
	 * Return {@code true} if {@code this} track-segment contains at least one
	 * track-point.
	 *
	 * @since 1.1
	 *
	 * @return {@code true} if {@code this} track-segment is not empty,
	 *         {@code false} otherwise
	 */
	public boolean nonEmpty() {
		return !isEmpty();
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(_points);
	}

	@Override
	public boolean equals(final Object obj) {
		return obj == this ||
			obj instanceof TrackSegment &&
			Objects.equals(((TrackSegment)obj)._points, _points);
	}

	@Override
	public String toString() {
		return format("TrackSegment[points=%s]", _points.size());
	}

	/**
	 * Builder class for creating immutable {@code TrackSegment} objects.
	 * <p>
	 * Creating a {@code TrackSegment} object with  3 track-points:
	 * <pre>{@code
	 * final TrackSegment segment = TrackSegment.builder()
	 *     .addPoint(p -> p.lat(48.2081743).lon(16.3738189).ele(160))
	 *     .addPoint(p -> p.lat(48.2081743).lon(16.3738189).ele(161))
	 *     .addPoint(p -> p.lat(48.2081743).lon(16.3738189).ele(162))))
	 *     .build();
	 * }</pre>
	 */
	public static final class Builder implements Filter<WayPoint, TrackSegment> {
		private final List<WayPoint> _points = new ArrayList<>();

		private Builder() {
		}

		/**
		 * Set the way-points fo the track segment. The list of way-points may
		 * be {@code null}.
		 *
		 * @param points the track-segment points
		 * @return {@code this} {@code Builder} for method chaining
		 * @throws NullPointerException if one of the way-points in the list is
		 *         {@code null}
		 */
		public Builder points(final List<WayPoint> points) {
			copy(points, _points);
			return this;
		}

		/**
		 * Add a way-point to the track-segment.
		 *
		 * @param point the segment way-point
		 * @return {@code this} {@code Builder} for method chaining
		 * @throws NullPointerException if the given {@code href} is {@code null}
		 */
		public Builder addPoint(final WayPoint point) {
			_points.add(requireNonNull(point));

			return this;
		}

		/**
		 * Add a way-point to the track-segment, via the given way-point builder.
		 * <p>
		 * Creating a {@code TrackSegment} object with  3 track-points:
		 * <pre>{@code
		 * final TrackSegment segment = TrackSegment.builder()
		 *     .addPoint(p -> p.lat(48.2081743).lon(16.3738189).ele(160))
		 *     .addPoint(p -> p.lat(48.2081743).lon(16.3738189).ele(161))
		 *     .addPoint(p -> p.lat(48.2081743).lon(16.3738189).ele(162))))
		 *     .build();
		 * }</pre>
		 *
		 * @param point the segment way-point builder
		 * @return {@code this} {@code Builder} for method chaining
		 * @throws NullPointerException if the given {@code href} is {@code null}
		 */
		public Builder addPoint(final Consumer<WayPoint.Builder> point) {
			final WayPoint.Builder builder = WayPoint.builder();
			point.accept(builder);
			return addPoint(builder.build());
		}

		/**
		 * Return the current way-points. The returned list is mutable.
		 *
		 * @since 1.1
		 *
		 * @return the current, mutable way-point list
		 */
		public List<WayPoint> points() {
			return new NonNullList<>(_points);
		}

		@Override
		public Builder filter(final Predicate<? super WayPoint> predicate) {
			points(
				_points.stream()
					.filter(predicate)
					.collect(Collectors.toList())
			);

			return this;
		}

		@Override
		public Builder map(
			final Function<? super WayPoint, ? extends WayPoint> mapper
		) {
			points(
				_points.stream()
					.map(mapper)
					.collect(Collectors.toList())
			);

			return this;
		}

		@Override
		public Builder flatMap(
			final Function<
				? super WayPoint,
				? extends List<WayPoint>> mapper
		) {
			points(
				_points.stream()
					.flatMap(wp -> mapper.apply(wp).stream())
					.collect(Collectors.toList())
			);

			return this;
		}

		@Override
		public Builder listMap(
			final Function<
				? super List<WayPoint>,
				? extends List<WayPoint>> mapper
		) {
			points(mapper.apply(_points));

			return this;
		}

		/**
		 * Create a new track-segment from the current builder state.
		 *
		 * @return a new track-segment from the current builder state
		 */
		@Override
		public TrackSegment build() {
			return new TrackSegment(_points);
		}

	}

	/**
	 * Create a new track-segment builder.
	 *
	 * @return a new track-segment builder
	 */
	public static Builder builder() {
		return new Builder();
	}


	/* *************************************************************************
	 *  Static object creation methods
	 * ************************************************************************/

	/**
	 * Create a new track-segment with the given points.
	 *
	 * @param points the points of the track-segment
	 * @return a new track-segment with the given points
	 * @throws NullPointerException if the given {@code points} sequence is
	 *        {@code null}
	 */
	public static TrackSegment of(final List<WayPoint> points) {
		return new TrackSegment(points);
	}


	/* *************************************************************************
	 *  Java object serialization
	 * ************************************************************************/

	private Object writeReplace() {
		return new Serial(Serial.TRACK_SEGMENT, this);
	}

	private void readObject(final ObjectInputStream stream)
		throws InvalidObjectException
	{
		throw new InvalidObjectException("Serialization proxy required.");
	}

	void write(final DataOutput out) throws IOException {
		IO.writes(_points, WayPoint::write, out);
	}

	static TrackSegment read(final DataInput in) throws IOException {
		return new TrackSegment(IO.reads(WayPoint::read, in));
	}


	/* *************************************************************************
	 *  XML stream object serialization
	 * ************************************************************************/

	static final XMLWriter<TrackSegment> WRITER = elem("trkseg",
		XMLWriter.elems(WayPoint.writer("trkpt")).map(ts -> ts._points)
	);

	@SuppressWarnings("unchecked")
	static final XMLReader<TrackSegment> READER = XMLReader.elem(
		a -> TrackSegment.of((List<WayPoint>)a[0]),
		"trkseg",
		XMLReader.elems(WayPoint.reader("trkpt"))
	);

}
