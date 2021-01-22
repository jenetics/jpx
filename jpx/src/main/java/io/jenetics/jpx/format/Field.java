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

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.Locale;
import java.util.Optional;

/**
 * Represents one of the existing location fields: latitude, longitude and
 * elevation.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 2.2
 * @since 1.4
 */
abstract class Field implements Format {

	final static DecimalFormatSymbols SYMBOLS =
		DecimalFormatSymbols.getInstance(Locale.US);

	protected NumberFormat _numberFormat;
	protected final String _pattern;

	Field(final String pattern){
		final var decimalPattern = toDecimalPattern(pattern);
		_numberFormat = new DecimalFormat(decimalPattern, SYMBOLS);
		_pattern = pattern;
	}

	/**
	 * Return the type character of this field.
	 *
	 * @return the type character of this field
	 */
	abstract char type();

	void setPrefixSign(final boolean b) {
	}

	static double toMinutes(final double degrees) {
		double dd = abs(degrees);
		return (dd - floor(dd)) * 60.0;
	}

	static double toSeconds(final double degrees) {
		double dd = abs(degrees);
		double d = floor(dd);
		double m = floor((dd - d) * 60.0);
		return (dd - d - m / 60.0) * 3600.0;
	}

	static Optional<Field> ofPattern(final String pattern) {
		// TODO better?
		for (int i = 0; i < pattern.length(); ++i) {
			char c = pattern.charAt(i);
			switch (c){
				case 'L': {
					String p = pattern.replace('L', 'D');
					return Optional.of(new LatitudeDegree(p));
				}
				case 'D': return Optional.of(new LatitudeDegree(pattern));
				case 'M': return Optional.of(new LatitudeMinute(pattern));
				case 'S': return Optional.of(new LatitudeSecond(pattern));
				case 'l': {
					String p = pattern.replace('l','d');
					return Optional.of(new LongitudeDegree(p));
				}
				case 'd': return Optional.of(new LongitudeDegree(pattern));
				case 'm': return Optional.of(new LongitudeMinute(pattern));
				case 's': return Optional.of(new LongitudeSecond(pattern));
				case 'E': return Optional.of(new Elevation(pattern));
				case 'H': {
					String p = pattern.replace('H', 'E');
					return Optional.of(new Elevation(p));
				}
			}
		}
		return Optional.empty();
	}

	String toDecimalPattern(final String pattern) {
		return pattern.replace(type(), '0');
	}

	@Override
	public String toPattern() {
		return _pattern;
	}

	double parseDouble(final CharSequence in, final ParsePosition pos) {
		int i = pos.getIndex();
		String s = in.toString();
		boolean strictWidth = 1 < _numberFormat.getMinimumIntegerDigits(); //better?
		if(strictWidth) {
			int end = i + toPattern().length(); // toPattern() rather than pattern because LatitudeDegree.toPattern()
			s = in.subSequence(0, end).toString(); // don't eat more digits
		}
		Number n = _numberFormat.parse(s, pos);//Does not throw an exception; if no object can be parsed, index is unchanged.
		if(i==pos.getIndex()) {
			pos.setErrorIndex(i);
			throw new ParseException("Not found " + _pattern, in, i);
		}

		return n.doubleValue();
	}
}
