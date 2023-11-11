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

import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.String.format;
import static java.util.Objects.hash;
import static java.util.Objects.requireNonNull;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collector;

/**
 * Two lat/lon pairs defining the extent of an element.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 1.6
 * @since 1.0
 */
public final class Bounds implements Serializable {

	@Serial
	private static final long serialVersionUID = 2L;

	private final Latitude _minLatitude;
	private final Longitude _minLongitude;
	private final Latitude _maxLatitude;
	private final Longitude _maxLongitude;

	/**
	 * Create a new {@code Bounds} object with the given extent.
	 *
	 * @param minLatitude the minimum latitude
	 * @param minLongitude the minimum longitude
	 * @param maxLatitude the maximum latitude
	 * @param maxLongitude the maximum longitude
	 * @throws NullPointerException if one of the arguments is {@code null}
	 */
	private Bounds(
		final Latitude minLatitude,
		final Longitude minLongitude,
		final Latitude maxLatitude,
		final Longitude maxLongitude
	) {
		_minLatitude = requireNonNull(minLatitude);
		_minLongitude = requireNonNull(minLongitude);
		_maxLatitude = requireNonNull(maxLatitude);
		_maxLongitude = requireNonNull(maxLongitude);
	}

	/**
	 * Return the minimum latitude.
	 *
	 * @return the minimum latitude
	 */
	public Latitude getMinLatitude() {
		return _minLatitude;
	}

	/**
	 * Return the minimum longitude.
	 *
	 * @return the minimum longitude
	 */
	public Longitude getMinLongitude() {
		return _minLongitude;
	}

	/**
	 * Return the maximum latitude.
	 *
	 * @return the maximum latitude
	 */
	public Latitude getMaxLatitude() {
		return _maxLatitude;
	}

	/**
	 * Return the maximum longitude
	 *
	 * @return the maximum longitude
	 */
	public Longitude getMaxLongitude() {
		return _maxLongitude;
	}

	@Override
	public int hashCode() {
		return hash(_minLatitude, _minLongitude, _maxLatitude, _maxLongitude);
	}

	@Override
	public boolean equals(final Object obj) {
		return obj == this ||
			obj instanceof  Bounds bounds &&
			Objects.equals(bounds._minLatitude, _minLatitude) &&
			Objects.equals(bounds._minLongitude, _minLongitude) &&
			Objects.equals(bounds._maxLatitude, _maxLatitude) &&
			Objects.equals(bounds._maxLongitude, _maxLongitude);
	}

	@Override
	public String toString() {
		return format(
			"[%s, %s][%s, %s]",
			_minLatitude,
			_minLongitude,
			_maxLatitude,
			_maxLongitude
		);
	}

	/**
	 * Return a collector which calculates the bounds of a given way-point
	 * stream. The following example shows how to calculate the bounds of all
	 * track-points of a given GPX object.
	 *
	 * {@snippet lang="java":
	 * final Bounds bounds = gpx.tracks()
	 *     .flatMap(Track::segments)
	 *     .flatMap(TrackSegment::points)
	 *     .collect(Bounds.toBounds());
	 * }
	 *
	 * If the collecting way-point stream is empty, the collected {@code Bounds}
	 * object is {@code null}.
	 *
	 * @since 1.6
	 *
	 * @param <P> The actual point type
	 * @return a new bounds collector
	 */
	public static <P extends Point> Collector<P, ?, Bounds> toBounds() {
		return Collector.of(
			() -> {
				final double[] a = new double[4];
				a[0] = Double.MAX_VALUE;
				a[1] = Double.MAX_VALUE;
				a[2] = -Double.MAX_VALUE;
				a[3] = -Double.MAX_VALUE;
				return a;
			},
			(a, b) -> {
				a[0] = min(b.getLatitude().doubleValue(), a[0]);
				a[1] = min(b.getLongitude().doubleValue(), a[1]);
				a[2] = max(b.getLatitude().doubleValue(), a[2]);
				a[3] = max(b.getLongitude().doubleValue(), a[3]);
			},
			(a, b) -> {
				a[0] = min(a[0], b[0]);
				a[1] = min(a[1], b[1]);
				a[2] = max(a[2], b[2]);
				a[3] = max(a[3], b[3]);
				return a;
			},
			a -> a[0] == Double.MAX_VALUE
				? null
				: Bounds.of(a[0], a[1], a[2], a[3])
		);
	}

	/* *************************************************************************
	 *  Static object creation methods
	 * ************************************************************************/

	/**
	 * Create a new {@code Bounds} object with the given extent.
	 *
	 * @param minLatitude the minimum latitude
	 * @param minLongitude the minimum longitude
	 * @param maxLatitude the maximum latitude
	 * @param maxLongitude the maximum longitude
	 * @return a new {@code Bounds} object with the given extent
	 * @throws NullPointerException if one of the arguments is {@code null}
	 */
	public static Bounds of(
		final Latitude minLatitude,
		final Longitude minLongitude,
		final Latitude maxLatitude,
		final Longitude maxLongitude
	) {
		return new Bounds(minLatitude, minLongitude, maxLatitude, maxLongitude);
	}

	/**
	 * Create a new {@code Bounds} object with the given extent.
	 *
	 * @param minLatitudeDegree the minimum latitude
	 * @param minLongitudeDegree the minimum longitude
	 * @param maxLatitudeDegree the maximum latitude
	 * @param maxLongitudeDegree the maximum longitude
	 * @return a new {@code Bounds} object with the given extent
	 * @throws IllegalArgumentException if the latitude values are not within
	 *         the range of {@code [-90..90]}
	 * @throws IllegalArgumentException if the longitudes value are not within
	 *         the range of {@code [-180..180]}
	 */
	public static Bounds of(
		final double minLatitudeDegree,
		final double minLongitudeDegree,
		final double maxLatitudeDegree,
		final double maxLongitudeDegree
	) {
		return new Bounds(
			Latitude.ofDegrees(minLatitudeDegree),
			Longitude.ofDegrees(minLongitudeDegree),
			Latitude.ofDegrees(maxLatitudeDegree),
			Longitude.ofDegrees(maxLongitudeDegree)
		);
	}

	/* *************************************************************************
	 *  Java object serialization
	 * ************************************************************************/

	@Serial
	private Object writeReplace() {
		return new SerialProxy(SerialProxy.BOUNDS, this);
	}

	@Serial
	private void readObject(final ObjectInputStream stream)
		throws InvalidObjectException
	{
		throw new InvalidObjectException("Serialization proxy required.");
	}

	void write(final DataOutput out) throws IOException {
		out.writeDouble(_minLatitude.toDegrees());
		out.writeDouble(_minLongitude.toDegrees());
		out.writeDouble(_maxLatitude.toDegrees());
		out.writeDouble(_maxLongitude.toDegrees());
	}

	static Bounds read(final DataInput in) throws IOException {
		return Bounds.of(
			in.readDouble(), in.readDouble(),
			in.readDouble(), in.readDouble()
		);
	}

	/* *************************************************************************
	 *  XML stream object serialization
	 * ************************************************************************/

	static XMLWriter<Bounds>
	writer(final Function<? super Number, String> formatter) {
		return XMLWriter.elem("bounds",
			XMLWriter.attr("minlat").map(b -> formatter.apply(b.getMinLatitude())),
			XMLWriter.attr("minlon").map(b -> formatter.apply(b.getMinLongitude())),
			XMLWriter.attr("maxlat").map(b -> formatter.apply(b.getMaxLatitude())),
			XMLWriter.attr("maxlon").map(b -> formatter.apply(b.getMaxLongitude()))
		);
	}

	static final XMLReader<Bounds> READER = XMLReader.elem(
		v -> Bounds.of(
			(Latitude)v[0], (Longitude)v[1],
			(Latitude)v[2], (Longitude)v[3]
		),
		"bounds",
		XMLReader.attr("minlat").map(Latitude::parse),
		XMLReader.attr("minlon").map(Longitude::parse),
		XMLReader.attr("maxlat").map(Latitude::parse),
		XMLReader.attr("maxlon").map(Longitude::parse)
	);

}
