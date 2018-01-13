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

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;

/**
 * The latitude of the point. Decimal degrees, WGS84 datum, which must be within
 * the range of {@code [-90..90]}.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 1.0
 * @since 1.0
 */
public final class Latitude extends Number implements Serializable {

	private static final long serialVersionUID = 1L;

	private final double _value;

	/**
	 * Create a new (decimal degrees) {@code Latitude} value.
	 *
	 * @param value the latitude value in decimal degrees
	 * @throws IllegalArgumentException if the given value is not within the
	 *         range of {@code [-90..90]}
	 */
	private Latitude(final double value) {
		if (value < -90 || value > 90) {
			throw new IllegalArgumentException(format(
				"%f is not in range [-90, 90].", value
			));
		}

		_value = value;
	}

	/**
	 * Return the latitude value in decimal degrees.
	 *
	 * @return the latitude value in decimal degrees
	 */
	@Override
	public double doubleValue() {
		return _value;
	}

	/**
	 * Return the latitude value in radians.
	 *
	 * @return the latitude value in radians
	 */
	public double toRadians() {
		return Math.toRadians(_value);
	}

	/**
	 * Return the latitude in decimal degree.
	 *
	 * @return the latitude in decimal degree
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
			obj instanceof Latitude &&
			Double.compare(((Latitude)obj)._value, _value) == 0;
	}

	@Override
	public String toString() {
		return Double.toString(_value);
	}


	/* *************************************************************************
	 *  Static object creation methods
	 * ************************************************************************/

	/**
	 * Create a new (decimal degrees) {@code Latitude} object.
	 *
	 * @param degrees the latitude value in decimal degrees
	 * @return a new (decimal degrees) {@code Latitude} object
	 * @throws IllegalArgumentException if the given value is not within the
	 *         range of {@code [-90..90]}
	 */
	public static Latitude ofDegrees(final double degrees) {
		return new Latitude(degrees);
	}

	/**
	 * Create a new {@code Latitude} value for the given {@code radians}.
	 *
	 * @param radians the latitude value in radians
	 * @return  a new {@code Latitude} value for the given {@code radians}
	 * @throws IllegalArgumentException if the given radians is not within the
	 *         range of {@code [-Pi..Pi]}
	 */
	public static Latitude ofRadians(final double radians) {
		return new Latitude(Math.toDegrees(radians));
	}

	static Latitude parse(final String string) {
		return string != null
			? Latitude.ofDegrees(Double.parseDouble(string))
			: null;
	}

	/* *************************************************************************
	 *  Java object serialization
	 * ************************************************************************/

	private Object writeReplace() {
		return new Serial(Serial.LATITUDE, this);
	}

	private void readObject(final ObjectInputStream stream)
		throws InvalidObjectException
	{
		throw new InvalidObjectException("Serialization proxy required.");
	}

	void write(final DataOutput out) throws IOException {
		out.writeDouble(_value);
	}

	static Latitude read(final DataInput in) throws IOException {
		return new Latitude(in.readDouble());
	}

}
