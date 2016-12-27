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

import java.io.Serializable;

/**
 * Extent of something along its greatest dimension or the extent of space
 * between two objects or places. The metric system unit for this quantity is
 * "m" (metre).
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 1.0
 * @since 1.0
 */
public final class Length extends Number implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Represents a given length unit.
	 */
	public static enum Unit {

		/**
		 * Represents a meter.
		 */
		METER(1.0),

		/**
		 * Represents a kilometer.
		 */
		KILOMETER(1_000.0),

		/**
		 * Represents an inch.
		 */
		INCH(127.0/5_000.0),

		/**
		 * Represents a yard.
		 */
		YARD(1_143.0/1_250.0),

		/**
		 * Represents a mile.
		 */
		MILE(201_168.0/125.0);

		private final double _factor;

		private Unit(final double factor) {
			_factor = factor;
		}

		/**
		 * Convert the given length value of the given {@code sourceUnit} into a
		 * length value of {@code this} length unit. The given example converts 3
		 * inches into yards.
		 *
		 * <pre>{@code
		 * final double yards = YARD.convert(3, INCH);
		 * }</pre>
		 *
		 * @param length the length value
		 * @param sourceUnit the source length unit
		 * @return the speed value of {@code this} length unit
		 */
		public double convert(final double length, final Unit sourceUnit) {
			requireNonNull(sourceUnit);
			final double meters = length*sourceUnit._factor;
			return meters/_factor;
		}
	}

	private final double _value;

	/**
	 * Create a new {@code Length} object with the given value in meters.
	 *
	 * @param value the value (in meters) of the new {@code Length} object
	 */
	private Length(final double value) {
		_value = value;
	}

	/**
	 * Return the length in meter.
	 *
	 * @return the length in meter
	 */
	@Override
	public double doubleValue() {
		return _value;
	}

	/**
	 * Return the length in the desired unit.
	 *
	 * @param unit the desired length unit
	 * @return the length in the desired unit
	 * @throws NullPointerException if the given length {@code unit} is
	 *         {@code null}
	 */
	public double to(final Unit unit) {
		return unit.convert(_value, Unit.METER);
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
		return obj instanceof Length &&
			Double.compare(((Length)obj)._value, _value) == 0;
	}

	@Override
	public String toString() {
		return format("%s m", _value);
	}


	/* *************************************************************************
	 *  Static object creation methods
	 * ************************************************************************/

	/**
	 * Create a new {@code Length} object with the given length.
	 *
	 * @param length the length
	 * @param unit the length unit
	 * @return a new {@code Length} object with the given length.
	 * @throws NullPointerException if the given length {@code unit} is
	 *         {@code null}
	 */
	public static Length of(final double length, final Unit unit) {
		requireNonNull(unit);
		return new Length(Unit.METER.convert(length, unit));
	}

	/**
	 * Parses the given object.
	 *
	 * @param object the object to parse
	 * @return the parsed object
	 */
	static Length parse(final Object object) {
		return object instanceof Length
			? (Length)object
			: object instanceof Number
				? of(((Number)object).doubleValue(), Unit.METER)
				: object != null
					? of(Double.parseDouble(object.toString()), Unit.METER)
					: null;
	}

}
