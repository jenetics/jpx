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

import java.text.ParsePosition;
import java.util.List;
import java.util.Optional;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 1.4
 * @since 1.4
 */
final class OptionalFormat<T> implements Format<T> {

	private final Format<T> _format;

	private OptionalFormat(final Format<T> format) {
		_format = requireNonNull(format);
	}

	@Override
	public Optional<String> format(final T value) {
		return Optional.of(_format.format(value).orElse(""));
	}

	@Override
	public void parse(CharSequence in, ParsePosition pos, LocationBuilder builder) throws ParseException {
		int index = pos.getIndex();
		int errorIndex = pos.getErrorIndex();
		try {
			_format.parse(in, pos, builder);
		} catch (ParseException e){
			// Assume that in and builder have not changed.
			// Set pos back to what it was.
			pos.setIndex(index);
			pos.setErrorIndex(errorIndex);
		}
	}

	@Override public String toString() {
		return String.format("[%s]", _format);
	}

	static <T> OptionalFormat<T> of(final Format<T> format) {
		return new OptionalFormat<>(format);
	}

	static <T> OptionalFormat<T> of(final List<Format<T>> formats) {
		return new OptionalFormat<>(CompositeFormat.of(formats));
	}

}
