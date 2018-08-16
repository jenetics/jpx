/*
 * Java Genetic Algorithm Library (@__identifier__@).
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

import static java.lang.Math.abs;
import static java.util.Objects.requireNonNull;

import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.function.DoubleFunction;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public abstract class LocationFormatter {

	private static final class Fields {
		private final Latitude _latitude;
		private final Longitude _longitude;
		private final Length _elevation;

		private Fields(
			final Latitude latitude,
			final Longitude longitude,
			final Length elevation
		) {
			_latitude = latitude;
			_longitude = longitude;
			_elevation = elevation;
		}

		Latitude latitude() {
			return _latitude;
		}

		Longitude longitude() {
			return _longitude;
		}

		Length elevation() {
			return _elevation;
		}

		static Fields of(
			final Latitude latitude,
			final Longitude longitude,
			final Length elevation
		) {
			return new Fields(latitude, longitude, elevation);
		}
	}

	private static abstract class Field {
		private final Fields _fields;

		protected Field(final Fields fields) {
			_fields = requireNonNull(fields);
		}

		Fields fields() {
			return _fields;
		}

		abstract String format();

	}

	private static final class LatitudeDegree extends Field {

		private boolean _optional;

		private LatitudeDegree(final Fields fields) {
			super(fields);
		}

		@Override
		String format() {
			return null;
		}
	}


	private static final class Fmt {
		private final Supplier<OptionalDouble> _value;
		private final DoubleFunction<String> _formatter;
		private final boolean _optional;

		Fmt(
			final Supplier<OptionalDouble> value,
			final DoubleFunction<String> formatter,
			final boolean optional
		) {
			_value = requireNonNull(value);
			_formatter = requireNonNull(formatter);
			_optional = optional;
		}

		String format() {
			

			return null;
		}

	}


	public static final LocationFormatter ISO_HUMAN_LONG = new LocationFormatter() {

		DateTimeFormatter f = DateTimeFormatter.ofPattern("");

		/**
		 * -7.287954696138044 07°17'17"S 07°17'S -07.28795
		 * +16.449772215262600 16°26'59"N 16°27'N +16.44977
		 */
		@Override
		public String format(final Latitude lat) {
			final double degrees = lat.toDegrees();
			final StringBuilder out = new StringBuilder();

			final int[] parts = Angle.split(degrees);
			out.append(String.format("%02d", abs(parts[0])));
			out.append("\u00B0");

			out.append(String.format("%02d", abs(parts[1])));
			out.append("'");

			out.append(String.format("%02d", abs(parts[2])));
			out.append("\"");

			if (Double.compare(degrees, 0.0) >= 0) {
				out.append("N");
			} else {
				out.append("S");
			}

			return out.toString();
		}
	};

	public static final LocationFormatter ISO_HUMAN_MEDIUM = new LocationFormatter() {

		/**
		 * 37.335685	37°20'N
		 * -48.148918	48°09'S
		 */
		@Override
		public String format(final Latitude lat) {
			final double degrees = lat.toDegrees();
			final StringBuilder out = new StringBuilder();

			final int[] parts = Angle.split(degrees);
			out.append(String.format("%02d", abs(parts[0])));
			out.append("\u00B0");

			final int minutes = abs(parts[2]) < 30
				? abs(parts[1])
				: abs(parts[1]) + 1;

			out.append(String.format("%02d", minutes));
			out.append("'");

			if (Double.compare(degrees, 0.0) >= 0) {
				out.append("N");
			} else {
				out.append("S");
			}

			return out.toString();
		}
	};

	public static final LocationFormatter ISO_DECIMAL = new LocationFormatter() {
		@Override
		public String format(final Latitude lat) {
			final double degrees = lat.toDegrees();

			final NumberFormat fmt = NumberFormat.getInstance(Locale.ENGLISH);
			fmt.setMinimumIntegerDigits(2);
			fmt.setMinimumFractionDigits(5);

			return (Double.compare(degrees, 0.0) < 0 ? "" : "+") +
				fmt.format(degrees);
		}
	};

	public static final LocationFormatter ISO_LONG = new LocationFormatter() {
		@Override
		public String format(Latitude lat) {
			final double degrees = lat.toDegrees();
			final StringBuilder out = new StringBuilder();

			final int[] parts = Angle.split(degrees);
			out.append(Double.compare(degrees, 0.0) < 0 ? "-" : "+");
			out.append(String.format("%02d", abs(parts[0])));
			out.append(String.format("%02d", abs(parts[1])));
			out.append(String.format("%02d", abs(parts[2])));

			return out.toString();
		}
	};

	public static final LocationFormatter ISO_MEDIUM = new LocationFormatter() {
		@Override
		public String format(Latitude lat) {
			final double degrees = lat.toDegrees();
			final StringBuilder out = new StringBuilder();

			final int[] parts = Angle.split(degrees);
			out.append(Double.compare(degrees, 0.0) < 0 ? "-" : "+");
			out.append(String.format("%02d", abs(parts[0])));

			final int minutes = abs(parts[2]) < 30
				? abs(parts[1])
				: abs(parts[1]) + 1;

			out.append(String.format("%02d", minutes));

			return out.toString();
		}
	};

	public static final LocationFormatter ISO_SHORT = new LocationFormatter() {
		@Override
		public String format(final Latitude lat) {
			return (Double.compare(lat.toDegrees(), 0.0) < 0 ? "-" : "+") +
				String.format("%02d", abs(Angle.split(lat.toDegrees())[0]));
		}
	};

	public static final LocationFormatter ISO6709_HUMAN_SHORT = null;



	protected LocationFormatter() {
		DateTimeFormatter f;
	}

	public abstract String format(final Latitude lat);

	/*
	public abstract String format(final Longitude lon);

	public abstract String format(final Latitude lat, final Longitude lon);

	public abstract String format(final Point point);

	public abstract Object parseLatitude(final CharSequence text, final ParsePosition pos);

	public abstract Object parseLongitude(final CharSequence text, final ParsePosition pos);
	*/

	public static LocationFormatter ofPattern(final String pattern) {
		return null;
	}

	private static List<String> tokenize(final String pattern) {
		return null;
	}

}
