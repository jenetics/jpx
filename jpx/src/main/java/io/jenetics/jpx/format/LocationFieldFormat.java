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
package io.jenetics.jpx.format;

import static java.lang.Math.max;
import static java.util.Objects.requireNonNull;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Supplier;

import io.jenetics.jpx.format.Location.Field;

/**
 * This class formats a given location field (latitude, longitude or elevation)
 * with the given double value format. E.g. {@code DD}, {@code ss.sss} or
 * {@code HHHH.H}.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 1.4
 * @since 1.4
 */
final class LocationFieldFormat implements Format<Location> {

	private final Field _field;
	private final Supplier<NumberFormat> _format;

	private LocationFieldFormat(
		final Field field,
		final Supplier<NumberFormat> format
	) {
		_field = requireNonNull(field);
		_format = requireNonNull(format);
	}

	@Override
	public Optional<String> format(final Location location) {
		return _field.apply(location).map(v -> _format.get().format(v));
	}

	@Override
	public void parse(CharSequence in, ParsePosition pos, LocationBuilder builder) throws ParseException {
		// parse latitude, longitude, or elevation, as double
		int i = pos.getIndex();
		NumberFormat nf = _format.get();
		String s = in.toString();
		Number n = nf.parse(s, pos);//Does not throw an exception; if no object can be parsed, index is unchanged!
		if(i==pos.getIndex()) {
			pos.setErrorIndex(i);
			throw new ParseException("bad field", i);
		}
		double d = n.doubleValue();
		switch (_field){

			case LATITUDE: // L
			case DEGREE_OF_LATITUDE: // D
				builder.addLatitude(d); return;
			case MINUTE_OF_LATITUDE: builder.addLatitudeMinute(d); return; // M
			case SECOND_OF_LATITUDE: builder.addLatitudeSecond(d); return; // S

			case LONGITUDE: // l
			case DEGREE_OF_LONGITUDE: // m
				builder.addLongitude(d); return;
			case MINUTE_OF_LONGITUDE: builder.addLongitudeMinute(d); return; // m
			case SECOND_OF_LONGITUDE: builder.addLongitudeSecond(d); return; // s

			case ELEVATION:	// E
			case METER_OF_ELEVATION: // H
				builder.addElevation(d); return;

				default: throw new RuntimeException(_field.fieldName());
		}
	}

	static LocationFieldFormat of(final Field field, final String pattern) {
		// Fast fail. Will throw an IAE if the pattern is invalid.
		new DecimalFormat(pattern);
		return new LocationFieldFormat(field, () -> new DecimalFormat(
			pattern,
			DecimalFormatSymbols.getInstance(Locale.US)
		));
	}

	@Override
	public String toString() {
		final NumberFormat format = _format.get();
		final int minValDigit = format.getMinimumIntegerDigits();
		final int minFracDigit = format.getMinimumFractionDigits();
		final int maxFracDigit = format.getMaximumFractionDigits();

		final StringBuilder out = new StringBuilder();
		out.append(String.valueOf(_field.type()).repeat(max(0, minValDigit)));
		if (minFracDigit != 0 || maxFracDigit != 0) {
			out.append('.');
		}
		out.append(String.valueOf(_field.type()).repeat(max(0, minFracDigit)));

		return out.toString();
	}

	static LocationFieldFormat of(
		final Field field,
		final Supplier<NumberFormat> format
	) {
		return new LocationFieldFormat(field, format);
	}

}
