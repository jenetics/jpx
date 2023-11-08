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
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 4.0
 * @since 1.4
 */
final class CompositeFormat implements Format {

	private final List<Format> _formats;

	private CompositeFormat(final List<Format> formats) {
		_formats = List.copyOf(formats);
	}

	@Override
	public Optional<String> format(final Location value) {
		final List<Optional<String>> strings = _formats.stream()
			.map(format -> format.format(value))
			.toList();

		final boolean complete = strings.stream().allMatch(Optional::isPresent);
		return complete
			? Optional.of(
				strings.stream()
					.map(s -> s.orElseThrow(AssertionError::new))
					.collect(Collectors.joining()))
			: Optional.empty();
	}

	@Override
	public void parse(
		final CharSequence in,
		final ParsePosition pos,
		final LocationBuilder builder
	) {
		for(var format : _formats) {
			format.parse(in, pos, builder);
		}
	}

	@Override
	public String toPattern() {
		return _formats.stream()
			.map(Format::toPattern)
			.collect(Collectors.joining());
	}

	static CompositeFormat of(final List<Format> formats) {
		return new CompositeFormat(formats);
	}

}
