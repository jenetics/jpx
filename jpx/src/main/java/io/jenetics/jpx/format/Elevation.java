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

import static io.jenetics.jpx.Length.Unit.METER;

import java.text.ParsePosition;
import java.util.Optional;

/**
 * This field allows accessing the elevation (in meter) of a given location.
 *
 * @version 4.0
 * @since 2.2
 */
final class Elevation extends Field {

	Elevation(final String pattern) {
		super(pattern, 'E');
	}

	@Override
	public void parse(
		final CharSequence in,
		final ParsePosition pos,
		final LocationBuilder builder
	) {
		builder.setElevation(parse(in, pos));
	}

	@Override
	public Optional<String> format(final Location loc) {
		return loc.elevation()
			.map(l -> l.to(METER))
			.map(this::format);
	}

	@Override
	public String toPattern() {
		return isPrefixSign() ? "+" + super.toPattern() : super.toPattern();
	}

}
