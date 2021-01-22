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
import static java.math.RoundingMode.DOWN;
import static java.math.RoundingMode.HALF_EVEN;

import java.text.DecimalFormat;
import java.text.ParsePosition;
import java.util.Optional;

import io.jenetics.jpx.Longitude;

/**
 * This field allows to access the longitude degrees of a given location. If the
 * pattern has a fractional part, the longitude is rounded to match the pattern.
 * If the pattern has no fractional part, the longitude is truncated rather than
 * rounded, on the assumption that the fractional part will be represented by
 * minutes and seconds.
 *
 * @version 2.2
 * @since 2.2
 */
class LongitudeDegree extends Field {

	private boolean prefixSign = false;
	private boolean absolute = false;

	LongitudeDegree(final String pattern) {
		super(pattern);
	}

	void setPrefixSign(final boolean b) {
		prefixSign = b;
		String decimalPattern = toDecimalPattern(_pattern);
		String p = b ? ("+" + decimalPattern + ";" + "-" + decimalPattern) :  decimalPattern;
		_numberFormat = new DecimalFormat(p, SYMBOLS);
	}

	boolean isPrefixSign() {
		return prefixSign;
	}

	void setTruncate(final boolean b) {
		_numberFormat.setRoundingMode(b ? DOWN : HALF_EVEN);
	}

	void setAbsolute(final boolean b) {
		absolute = b;
	}

	@Override
	char type() {
		return 'd';
	}

	@Override
	public void parse(
		final CharSequence in,
		final ParsePosition pos,
		final LocationBuilder builder
	) {
		double d = parseDouble(in, pos);
		builder.addLongitude(d);
	}

	@Override
	public Optional<String> format(final Location loc) {
		return loc.longitude()
			.map(Longitude::toDegrees)
			.map(d -> absolute ? abs(d) : d)
			.map(d -> _numberFormat.format(d));
	}

	@Override
	public String toPattern() {
		return prefixSign ? "+" + _pattern : _pattern;
	}

}
