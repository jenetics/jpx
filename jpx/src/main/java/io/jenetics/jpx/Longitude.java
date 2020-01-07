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

import static java.lang.Double.doubleToLongBits;
import static java.lang.Double.longBitsToDouble;
import static java.lang.String.format;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;

/**
 * The longitude of the point. Decimal degrees, WGS84 datum, which must be within
 * the range of {@code [-180..180]}.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 2.0
 * @since 1.0
 */
public final class Longitude extends Number implements Serializable {

	private static final long serialVersionUID = 2L;

	/**
	 * A constant holding the maximum value a {@code Latitude} value can have,
	 * -180 inclusively.
	 *
	 * @since 2.0
	 */
	public static final double MIN_DEGREES = -180;

	/**
	 * A constant holding the maximum value a {@code Latitude} value can have,
	 * -180 inclusively.
	 *
	 * @since 2.0
	 */
	public static final Longitude MIN_VALUE = ofDegrees(MIN_DEGREES);

	/**
	 * A constant holding the maximum value a {@code Latitude} value can have,
	 * 179.99999999999997. This is the greatest {@code double} value smaller
	 * than 180.0.
	 *
	 * @since 2.0
	 */
	public static final double MAX_DEGREES =
		longBitsToDouble(doubleToLongBits(180) - 1);

	/**
	 * A constant holding the maximum value a {@code Latitude} value can have,
	 * 179.99999999999997. This is the greatest {@code double} value smaller
	 * than 180.0.
	 *
	 * @since 2.0
	 */
	public static final Longitude MAX_VALUE = ofDegrees(MAX_DEGREES);

	private final double _value;

	/**
	 * Create a new (decimal degrees) {@code Longitude} value.
	 *
	 * @param value the longitude value in decimal degrees
	 * @throws IllegalArgumentException if the given value is not within the
	 *         range of {@code [-180..180)}
	 */
	private Longitude(final double value) {
		if (value < MIN_DEGREES || value > MAX_DEGREES) {
			throw new IllegalArgumentException(format(
				"%f is not in range [-180, 180).", value
			));
		}

		_value = value;
	}

	/**
	 * Return the longitude value in decimal degrees.
	 *
	 * @return the longitude value in decimal degrees
	 */
	@Override
	public double doubleValue() {
		return _value;
	}

	/**
	 * Return the longitude value in radians.
	 *
	 * @return the longitude value in radians
	 */
	public double toRadians() {
		return Math.toRadians(_value);
	}

	/**
	 * Return the longitude in decimal degree.
	 *
	 * @return the longitude in decimal degree
	 */
	public double toDegrees() {
		return _value;
	}

	@Override
	public int intValue() {
		return (int)doubleValue();
	}

	@Override
	public long longValue() {
		return (long)doubleValue();
	}

	@Override
	public float floatValue() {
		return (float)doubleValue();
	}

	@Override
	public int hashCode() {
		return Double.hashCode(_value);
	}

	@Override
	public boolean equals(final Object obj) {
		return obj == this ||
			obj instanceof Longitude &&
			Double.compare(((Longitude)obj)._value, _value) == 0;
	}

	@Override
	public String toString() {
		return Double.toString(_value);
	}


	/* *************************************************************************
	 *  Static object creation methods
	 * ************************************************************************/

	/**
	 * Create a new (decimal degrees) {@code Longitude} object.
	 *
	 * @param degrees the longitude value in decimal degrees
	 * @return a new (decimal degrees) {@code Longitude} object
	 * @throws IllegalArgumentException if the given value is not within the
	 *         range of {@code [-180..180)}
	 */
	public static Longitude ofDegrees(final double degrees) {
		return new Longitude(degrees);
	}

	/**
	 * Create a new {@code Longitude} value for the given {@code radians}.
	 *
	 * @param radians the longitude value in radians
	 * @return a new {@code Longitude} value for the given {@code radians}
	 * @throws IllegalArgumentException if the given radians is not within the
	 *         range of {@code [-2*Pi..2*Pi]}
	 */
	public static Longitude ofRadians(final double radians) {
		return new Longitude(Math.toDegrees(radians));
	}

	static Longitude parse(final String value) {
		final String lon = Strings.trim(value);
		return lon != null
			? Longitude.ofDegrees(Double.parseDouble(lon))
			: null;
	}

	/* *************************************************************************
	 *  Java object serialization
	 * ************************************************************************/

	private Object writeReplace() {
		return new Serial(Serial.LONGITUDE, this);
	}

	private void readObject(final ObjectInputStream stream)
		throws InvalidObjectException
	{
		throw new InvalidObjectException("Serialization proxy required.");
	}

	void write(final DataOutput out) throws IOException {
		out.writeDouble(_value);
	}

	static Longitude read(final DataInput in) throws IOException {
		return new Longitude(in.readDouble());
	}

}
