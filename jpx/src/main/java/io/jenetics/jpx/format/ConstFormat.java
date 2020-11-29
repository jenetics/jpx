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

import static java.util.Objects.requireNonNull;
import static io.jenetics.jpx.format.LocationFormatter.PROTECTED_CHARS;

import java.text.ParsePosition;
import java.util.Optional;

/**
 * A format object which returns a constant value.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 1.4
 * @since 1.4
 */
final class ConstFormat<T> implements Format<T> {

	private final String _value;

	/**
	 * Create a new <em>constant</em> location format object.
	 *
	 * @param value the constant value, returned by the
	 *        {@link Format#format(Object)} method
	 */
	private ConstFormat(final String value) {
		_value = requireNonNull(value);
	}

	@Override
	public Optional<String> format(final T value) {
		return Optional.of(_value);
	}

	@Override
	public void parse(CharSequence in, ParsePosition pos, LocationBuilder builder) throws ParseException {
		// parse _value
		int start = pos.getIndex();
		int end = start + _value.length();
		if( end <= in.length()){
			String s = in.subSequence(start, end).toString();
			if(s.equals(_value)){
				pos.setIndex(end);
				// but no call to builder
				return;
			}
		}
		pos.setErrorIndex(start);
		throw new ParseException("bad constant", start);
	}

	@Override
	public String toString() {
		return escape(_value);
	}

	private static String escape(final String value) {
		final StringBuilder out = new StringBuilder();
		boolean quote = false;
		for (int i = 0; i < value.length(); ++i) {
			final char c = value.charAt(i);
			if (PROTECTED_CHARS.contains(c)) {
				quote = true;
			}
			if (c == '\'') {
				out.append(c);
			}
			out.append(c);
		}

		return quote
			? "'" + out.toString() + "'"
			: out.toString();
	}

	static <T> ConstFormat<T> of(final String value) {
		return new ConstFormat<>(value);
	}

}
