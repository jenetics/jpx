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
package io.jenetics.jpx.format;

import static java.util.Objects.requireNonNull;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * A format object which returns a constant value.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
final class ConstFormat<T> implements Format<T> {

	private static final Set<Character> PROTECTED = new HashSet<>(Arrays.asList(
		'D', 'M', 'S', 'd', 'm', 's', 'E', 'X', 'x', '+', '[', ']'
	));

	private final String _value;

	/**
	 * Create a new <em>constant</em> location format object.
	 *
	 * @param value the constant value, returned by the
	 *        {@link Format#format(Object)} method
	 */
	ConstFormat(final String value) {
		_value = requireNonNull(value);
	}

	@Override
	public Optional<String> format(final T value) {
		return Optional.of(_value);
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
			if (PROTECTED.contains(c)) {
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

}
