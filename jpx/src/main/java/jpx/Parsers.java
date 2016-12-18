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
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 */
package jpx;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.time.Year;
import java.util.DoubleSummaryStatistics;
import java.util.function.Function;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
final class Parsers {

	private Parsers() {
	}

	static <T> T parse(final Object object, final Function<Object, T> parser) {
		return object != null
			? parser.apply(object)
			: null;
	}

	static String parseString(final Object object) {
		return object != null ? object.toString() : null;
	}

	static Double parseDouble(final Object object) {
		return object instanceof Number
			? Double.valueOf(((Number)object).doubleValue())
			: object != null
				? Double.valueOf(object.toString())
				: null;
	}

	static Duration parseSeconds(final Object object) {
		return object instanceof Duration
			? (Duration)object
			: object instanceof Number
				? Duration.ofSeconds(((Number) object).longValue())
				: object != null
					? Duration.ofSeconds(Long.parseLong(object.toString()))
					: null;
	}

	static Year parseYear(final Object object) {
		return object instanceof Year
			? (Year)object
			: object instanceof Number
				? Year.of(((Number) object).intValue())
				: object != null
					? Year.of(Integer.parseInt(object.toString()))
					: null;
	}

	static URI parseURI(final Object object) {
		try {
			return object instanceof URI
				? (URI)object
				: object != null
					? new URI(object.toString())
					: null;
		} catch (URISyntaxException e) {
			throw new IllegalArgumentException(e);
		}
	}

}
