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
 * @version 2.2
 * @since 1.4
 */
class OptionalFormat implements Format {

	private final Format _format;

	private OptionalFormat(final Format format) {
		_format = requireNonNull(format);
	}

	@Override
	public Optional<String> format(final Location value) {
		return Optional.of(_format.format(value).orElse(""));
	}

	@Override
	public void parse(
		final CharSequence in,
		final ParsePosition pos,
		final LocationBuilder builder
	) {
		int index = pos.getIndex();
		int errorIndex = pos.getErrorIndex();
		LocationBuilder before = builder.copy();

		try {
			_format.parse(in, pos, builder);
		} catch (ParseException e){
			builder.copy(before);
			pos.setIndex(index); // Set pos back to what it was.
			pos.setErrorIndex(errorIndex);
		}
	}

	@Override
	public String toPattern() {
		return String.format("[%s]", _format.toPattern());
	}

	static OptionalFormat of(final List<Format> formats) {
		return new OptionalFormat(CompositeFormat.of(formats));
	}

}
