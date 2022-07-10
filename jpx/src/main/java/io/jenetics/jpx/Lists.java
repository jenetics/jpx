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
package io.jenetics.jpx;

import static java.util.Objects.requireNonNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Helper methods for handling lists. All method handles null values correctly.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 2.0
 * @since 1.0
 */
final class Lists {

	private Lists() {
	}

	static <T> List<T> copyOf(final List<? extends T> list) {
		return list == null ? List.of() : List.copyOf(list);
	}

	static <T> void copyTo(
		final List<? extends T> source,
		final List<? super T> target
	) {
		requireNonNull(target);
		if (source != null) {
			source.forEach(Objects::requireNonNull);
		}

		target.clear();
		if (source != null) {
			target.addAll(source);
		}
	}

	static int hashCode(final List<?> list) {
		return list != null
			? 17*list.stream().mapToInt(Objects::hashCode).sum() + 31
			: 0;
	}

	static boolean equals(final List<?> b, final List<?> a) {
		boolean result = false;
		if (a != null) {
			if (b != null) {
				result = a.size() == b.size();
				if (result) {
					result = a.isEmpty()|| containsAll(a, b);
				}
			}
		} else {
			result = b == null;
		}

		return result;
	}

	private static boolean containsAll(final Collection<?> a, final Collection<?> b) {
		final Iterator<?> ita = a.iterator();
		final Set<Object> visited = new HashSet<>();

		for (final Object next : b) {
			if (visited.contains(next)) {
				continue;
			}

			boolean foundCurrentElement = false;
			while (ita.hasNext()) {
				final Object p = ita.next();
				visited.add(p);

				if (Objects.equals(next, p)) {
					foundCurrentElement = true;
					break;
				}
			}

			if (!foundCurrentElement) {
				return false;
			}
		}

		return true;
	}

}
