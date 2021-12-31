/*
 * Java GPX Library (@__identifier__@).
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
 */
package io.jenetics.jpx.format;

import static java.lang.Math.abs;
import static java.lang.Math.floor;
import static java.math.RoundingMode.DOWN;
import static java.math.RoundingMode.HALF_EVEN;
import static java.util.Objects.requireNonNull;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Represents one of the existing location fields: latitude, longitude and
 * elevation.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 3.0
 * @since 1.4
 */
abstract class Field implements Format {

	private static final DecimalFormatSymbols SYMBOLS =
		DecimalFormatSymbols.getInstance(Locale.US);

	private final String _pattern;
	private final char _type;

	private boolean _prefixSign = false;
	private boolean _absolute = false;

	private final AtomicReference<NumberFormat> _format = new AtomicReference<>();

	Field(final String pattern, final char type) {
		_pattern = requireNonNull(pattern);
		_type = type;
		_format.set(new DecimalFormat(toDecimalPattern(pattern, type), SYMBOLS));
	}

	private static String toDecimalPattern(final String pattern, final char type) {
		return pattern.replace(type, '0');
	}

	final void setPrefixSign(final boolean b) {
		_prefixSign = b;

		final String decimalPattern = toDecimalPattern(_pattern, _type);
		final String pattern = _prefixSign
			? "+%1$s;-%1$s".formatted(decimalPattern)
			:  decimalPattern;

		setFormat(new DecimalFormat(pattern, SYMBOLS));
	}

	final boolean isPrefixSign() {
		return _prefixSign;
	}

	final void setAbsolute(final boolean b) {
		_absolute = b;
	}

	final boolean isAbsolute() {
		return _absolute;
	}

	final void setTruncate(final boolean b) {
		_format.get().setRoundingMode(b ? DOWN : HALF_EVEN);
	}

	final void setFormat(final NumberFormat format) {
		_format.set(requireNonNull(format));
	}

	final int getMinimumFractionDigits() {
		return _format.get().getMinimumFractionDigits();
	}

	@Override
	public String toPattern() {
		return _pattern;
	}

	/**
	 * Formatting the given double value with the field formatter.
	 *
	 * @param value the double value to format
	 * @return the formatted double value
	 */
	final String format(final double value) {
		synchronized (_format) {
			return _format.get().format(value);
		}
	}

	/**
	 * Parsers the given input string.
	 *
	 * @param in the input string to parse
	 * @param pos the parse position
	 * @return the parsed double value
	 */
	final double parse(final CharSequence in, final ParsePosition pos) {
		int i = pos.getIndex();
		String s = in.toString();
		boolean strictWidth = 1 < _format.get().getMinimumIntegerDigits();
		if (strictWidth) {
			int end = i + toPattern().length();
			s = in.subSequence(0, end).toString();
		}

		final Number n = parse0(s, pos);

		if (i == pos.getIndex()) {
			pos.setErrorIndex(i);
			throw new ParseException("Not found " + toPattern(), in, i);
		}

		return n.doubleValue();
	}

	private Number parse0(final String value, final ParsePosition pos) {
		synchronized (_format) {
			return _format.get().parse(value, pos);
		}
	}


	static double toMinutes(final double degrees) {
		final double dd = abs(degrees);
		return (dd - floor(dd)) * 60.0;
	}

	static double toSeconds(final double degrees) {
		final double dd = abs(degrees);
		final double d = floor(dd);
		final double m = floor((dd - d) * 60.0);
		return (dd - d - m / 60.0) * 3600.0;
	}

	static Optional<Field> ofPattern(final String pattern) {
		// TODO better?
		for (int i = 0; i < pattern.length(); ++i) {
			switch (pattern.charAt(i)) {
				case 'L': {
					final String p = pattern.replace('L', 'D');
					return Optional.of(new LatitudeDegree(p));
				}
				case 'D': return Optional.of(new LatitudeDegree(pattern));
				case 'M': return Optional.of(new LatitudeMinute(pattern));
				case 'S': return Optional.of(new LatitudeSecond(pattern));
				case 'l': {
					final String p = pattern.replace('l','d');
					return Optional.of(new LongitudeDegree(p));
				}
				case 'd': return Optional.of(new LongitudeDegree(pattern));
				case 'm': return Optional.of(new LongitudeMinute(pattern));
				case 's': return Optional.of(new LongitudeSecond(pattern));
				case 'E': return Optional.of(new Elevation(pattern));
				case 'H': {
					final String p = pattern.replace('H', 'E');
					return Optional.of(new Elevation(p));
				}
			}
		}

		return Optional.empty();
	}

}
