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
import java.util.Optional;

import io.jenetics.jpx.Longitude;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 2.2
 * @since 1.4
 */
enum LongitudeEW implements Format {

	INSTANCE;

	@Override
	public Optional<String> format(final Location value) {
		return value.longitude()
			.map(Longitude::toDegrees)
			.map(v -> Double.compare(v, 0.0) >= 0 ? "E" : "W");
	}

	@Override
	public void parse(
		final CharSequence in,
		final ParsePosition pos,
		final LocationBuilder builder
	) {
		final int i = pos.getIndex();
		switch (in.charAt(i)) {
			case 'E' -> {
				pos.setIndex(i + 1);
				builder.setLongitudeSign(+1);
			}
			case 'W' -> {
				pos.setIndex(i + 1);
				builder.setLongitudeSign(-1);
			}
			default -> {
				pos.setErrorIndex(i);
				throw new ParseException("Not found E/W", in, i);
			}
		}
	}

	@Override
	public String toPattern() {
		return "x";
	}

}
