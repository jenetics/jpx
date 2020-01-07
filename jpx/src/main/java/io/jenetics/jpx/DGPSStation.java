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
 * Represents a differential GPS station. This object only holds int values in
 * the range of {@code [0..1023]}.
 *
 * @see <a href="https://en.wikipedia.org/wiki/Value_object">Value object</a>
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 1.2
 * @since 1.0
 */
public final class DGPSStation
	extends Number
	implements
		Comparable<DGPSStation>,
		Serializable
{

	private static final long serialVersionUID = 2L;

	/**
	 * A constant holding the maximum value a {@code DGPSStation} value can have,
	 * 0 inclusively.
	 *
	 * @since 2.0
	 */
	public static final int MIN_VALUE = 0;

	/**
	 * A constant holding the maximum value a {@code DGPSStation} value can have,
	 * 1023 inclusively.
	 *
	 * @since 2.0
	 */
	public static final int MAX_VALUE = 1023;

	private final int _value;

	/**
	 * Create a new {@code DGPSStation} object.
	 *
	 * @param value the differential GPS station number
	 * @throws IllegalArgumentException if the given station number is not in the
	 *         range of {@code [0..1023]}
	 */
	private DGPSStation(final int value) {
		if (value < MIN_VALUE || value > MAX_VALUE) {
			throw new IllegalArgumentException(format(
				"%d is out of range [0, 1023].", value
			));
		}

		_value = value;
	}

	/**
	 * Return the differential GPS station number.
	 *
	 * @return the differential GPS station number
	 */
	public int intValue() {
		return _value;
	}

	@Override
	public double doubleValue() {
		return _value;
	}

	@Override
	public long longValue() {
		return _value;
	}

	@Override
	public float floatValue() {
		return (float)_value;
	}

	@Override
	public int compareTo(final DGPSStation other) {
		return Integer.compare(_value, other._value);
	}

	@Override
	public int hashCode() {
		return Integer.hashCode(_value);
	}

	@Override
	public boolean equals(final Object obj) {
		return obj == this ||
			obj instanceof DGPSStation &&
			((DGPSStation)obj)._value == _value;
	}

	@Override
	public String toString() {
		return Integer.toString(_value);
	}

	/* *************************************************************************
	 *  Static object creation methods
	 * ************************************************************************/

	/**
	 * Create a new {@code DGPSStation} object.
	 *
	 * @param value the differential GPS station number
	 * @return a new {@code DGPSStation} object
	 * @throws IllegalArgumentException if the given station number is not in the
	 *         range of {@code [0..1023]}
	 */
	public static DGPSStation of(final int value) {
		return new DGPSStation(value);
	}

	static DGPSStation parse(final String value) {
		final String stat = Strings.trim(value);

		return stat != null
			? DGPSStation.of(Integer.parseInt(stat))
			: null;
	}

	/* *************************************************************************
	 *  Java object serialization
	 * ************************************************************************/

	private Object writeReplace() {
		return new Serial(Serial.DGPS_STATION, this);
	}

	private void readObject(final ObjectInputStream stream)
		throws InvalidObjectException
	{
		throw new InvalidObjectException("Serialization proxy required.");
	}

	void write(final DataOutput out) throws IOException {
		IO.writeInt(_value, out);
	}

	static DGPSStation read(final DataInput in) throws IOException {
		return new DGPSStation(IO.readInt(in));
	}

}
