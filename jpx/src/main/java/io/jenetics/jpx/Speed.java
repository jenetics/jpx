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
 * Represents the GPS speed value in m/s.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 1.0
 * @since 1.0
 */
public final class Speed extends Number implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Represents a given speed unit.
	 */
	public static enum Unit {

		/**
		 * Represents the speed unit <em>meters per second</em>: <b>m/s</b>.
		 */
		METERS_PER_SECOND(1.0),

		/**
		 * Represents the speed unit <em>kilometers per hour</em>: <b>km/h</b>.
		 */
		KILOMETERS_PER_HOUR(5.0/18.0),

		/**
		 * Represents the speed unit <em>miles per hour</em>: <b>mi/h</b>.
		 */
		MILES_PER_HOUR(1_397.0/3_125.0),

		/**
		 * Represents the speed unit <em>knots</em>: <b>kt</b>.
		 */
		KNOTS(463.0/900.0),

		/**
		 * Represents the speed unit <em>mach</em>: <b>Ma</b>.
		 */
		MACH(331.3);

		// The conversion factor to the base unit m/s.
		private final double _factor;

		private Unit(final double factor) {
			_factor = factor;
		}

		/**
		 * Convert the given speed value of the given {@code sourceUnit} into a
		 * speed value of {@code this} speed unit. The given example converts 3
		 * knots into kilometers per hour.
		 *
		 * <pre>{@code
		 * final double kilometersPerHour = KILOMETERS_PER_HOUR.convert(3, KNOTS);
		 * }</pre>
		 *
		 * @param speed the speed value
		 * @param sourceUnit the source speed unit
		 * @return the speed value of {@code this} speed unit
		 */
		public double convert(final double speed, final Unit sourceUnit) {
			requireNonNull(sourceUnit);
			final double metersPerSecond = speed*sourceUnit._factor;
			return metersPerSecond/_factor;
		}

	}


	private final double _value;

	/**
	 * Create a new GPS {@code Speed} object in m/s.
	 *
	 * @param value the GPS speed value in m/s.
	 */
	private Speed(final double value) {
		_value = value;
	}

	/**
	 * Return the GPS speed value in m/s.
	 *
	 * @return the GPS speed value in m/s
	 */
	@Override
	public double doubleValue() {
		return _value;
	}

	/**
	 * Return the GPS speed value in the desired unit.
	 *
	 * @param unit the speed unit
	 * @return the GPS speed value in the desired unit
	 */
	public double to(final Unit unit) {
		return unit.convert(_value, Unit.METERS_PER_SECOND);
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
		return obj instanceof Speed &&
			Double.compare(((Speed)obj)._value, _value) == 0;
	}

	@Override
	public String toString() {
		return format("%s m/s", _value);
	}


	/* *************************************************************************
	 *  Static object creation methods
	 * ************************************************************************/

	/**
	 * Create a new GPS {@code Speed} object.
	 *
	 * @param speed the GPS speed value
	 * @param unit the speed unit
	 * @return a new GPS {@code Speed} object
	 */
	public static Speed of(final double speed, final Unit unit) {
		return new Speed(Unit.METERS_PER_SECOND.convert(speed, unit));
	}

	/**
	 * Parses the given object.
	 *
	 * @param object the object to parse
	 * @return the parsed object
	 */
	static Speed parse(final Object object) {
		return object instanceof Speed
			? (Speed)object
			: object instanceof Number
				? of(((Number)object).doubleValue(), Unit.METERS_PER_SECOND)
				: object != null
					? of(Double.parseDouble(object.toString()),
							Unit.METERS_PER_SECOND)
					: null;
	}

}
