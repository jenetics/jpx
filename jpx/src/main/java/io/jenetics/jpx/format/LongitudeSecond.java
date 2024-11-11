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

import java.text.ParsePosition;
import java.util.Optional;

import io.jenetics.jpx.Longitude;

/**
 * This field allows accessing the absolute value of the second part of the
 * longitude of a given location.
 *
 * @version 2.2
 * @since 2.2
 */
final class LongitudeSecond extends Field {

	LongitudeSecond(final String pattern) {
		super(pattern, 's');
	}

	@Override
	public void parse(
		final CharSequence in,
		final ParsePosition pos,
		final LocationBuilder builder
	) {
		double d = parse(in, pos);
		builder.addLongitudeSecond(d);
	}

	@Override
	public Optional<String> format(final Location loc) {
		return loc.longitude()
			.map(Longitude::toDegrees)
			.map(Field::toSeconds)
			.map(this::format);
	}

}
