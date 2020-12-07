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

import java.text.ParsePosition;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 1.4
 * @since 1.4
 */
final class CompositeFormat<T> implements Format<T> {

	private final List<Format<T>> _formats;

	private CompositeFormat(final List<Format<T>> formats) {
		_formats = List.copyOf(formats);
	}

	@Override public Optional<String> format(final T value) {
		final List<Optional<String>> strings = _formats.stream()
			.map(format -> format.format(value))
			.collect(Collectors.toList());

		final boolean complete = strings.stream().allMatch(Optional::isPresent);
		return complete
			? Optional.of(
				strings.stream()
					.map(s -> s.orElseThrow(AssertionError::new))
					.collect(Collectors.joining()))
			: Optional.empty();
	}

	@Override public void parse(CharSequence in, ParsePosition pos, LocationBuilder b) throws ParseException {
		for( Format<T> f : _formats ) f.parse(in, pos, b);
	}

	@Override public String toString() {
		return _formats.stream()
			.map( f -> f.toString() )
			.collect(Collectors.joining());
	}

	static <T> CompositeFormat<T> of(final List<Format<T>> formats) {
		return new CompositeFormat<>(formats);
	}

}
